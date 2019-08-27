"""Syntax Analyzation"""

import sys

from grammar import LALRGrammar, ActionType
from tokdef import TokenType, Token
from lexer import Lexer, count_position
from syntaxtree import SyntaxTree
from semantic import SemanticContext

class SyntaxAnalyser:
    """Syntax analyzer"""

    def __init__(self, path):
        self.action_table, self.productions = LALRGrammar.load(path)

    def analyze(self, toks, text, semantic=False, context=None):
        if not toks:
            return SyntaxTree(TokenType.END), []
        err_strs = []
        status_stack = [0]
        sym_stack = [SyntaxTree(TokenType.END)]
        tok_idx = 0
        accept = False
        if context is None:
            context = SemanticContext()

        if toks[-1] != TokenType.END:
            toks = toks + [Token(TokenType.END, None, 0, -1)]

        while tok_idx < len(toks):
            tok = toks[tok_idx]
            if tok.type == TokenType.CCOMMENT:
                tok_idx += 1
                continue
            toktype = tok.type
            status = status_stack[-1]
            if self._has_error(status, toktype):
                err_strs.append(self._gen_err_str(tok, text))
                tok_idx += 1
                continue

            act, number = self.action_table[status][toktype]
            if act == ActionType.MOVE:
                tok_idx += 1
                status_stack.append(number)
                sym_stack.append(SyntaxTree(tok))
            elif act == ActionType.REDUCE:
                production = self.productions[number]
                parent = SyntaxTree(production.lhs)
                for exp_sym in reversed(production.rhs):
                    status_stack.pop()
                    sym = sym_stack.pop()
                    assert sym.get_sym_name() == str(exp_sym), "Expect {} but get {}".format(exp_sym, sym.get_sym_name())
                    parent.add_child(sym)
                parent.childs = parent.childs[::-1]
                sym_stack.append(parent)
                act, idx = self.action_table[status_stack[-1]][parent.sym]
                assert act == ActionType.GOTO
                status_stack.append(idx)
                # do action
                if semantic and production.action:
                    print(production.action.name)
                    try:
                        ret = production.action.do_action(parent, context)
                        if ret:
                            err_code, message = ret
                        else:
                            err_code = 0
                    except Exception as ex:
                        print(''.join(ex.args))
                    if err_code:
                        node = parent
                        while node.childs:
                            node = node.childs[0]
                        row, col = count_position(text, node.sym.start)
                        err_strs.append("{}:{} {}".format(row, col, message))
            elif act == ActionType.ACCEPT:
                accept=True
                break
            else:
                assert False
        if not accept:
            err_strs.append("Expect More Token")
            sym = SyntaxTree("Incomplete")
            while sym_stack:
                sym.add_child(sym_stack.pop())
            sym_stack.append(sym)
        return sym_stack.pop(), err_strs

    def _has_error(self, status, toktype):
        if status not in self.action_table:
            return True
        if toktype not in self.action_table[status]:
            return True
        return False

    def _panic(self, toks, tok_idx, status_stack, sym_stack, next_tok):
        while status_stack:
            status = status_stack.pop()
            sym_tree = sym_stack.pop()
            # conserve END Token
            if sym_tree.sym == TokenType.END:
                sym_tree.append(TokenType.END)
                status_stack.append(0)
                break
            if isinstance(sym_tree.sym, str):
                status_stack.append(status)
                sym_stack.append(sym_tree)
                tok_idx = len(toks)
                for i in range(tok_idx, len(toks)):
                    if self._has_error(status, toks[i].type if isinstance(toks[i], Token) else toks[i]):
                        tok_idx = i
                        break
        return tok_idx

    def _gen_err_str(self, token, text):
        row, col = count_position(text, token.start)
        return "{}:{} Unexpected token {}".format(row, col, str(token))

text = \
'''
proc main; () {
    int i;
    i = 0;
    i = i + 1;
    while i < 10 do {
        call printf();
    }
}
'''

if __name__ == "__main__":
    analyzer = SyntaxAnalyser("defs/syntax.json")
    lexer = Lexer.load_lexer("defs/lexer.json")
    toks, err_strs = lexer.lex(text)
    print(' '.join(map(lambda tok: tok.type.name, toks)))
    tree, err_strs = analyzer.analyze(toks, text)
    print(tree.get_printable())
