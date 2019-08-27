"""Load and parse grammar"""

import re
import queue
import enum
import json

from tokdef import TokenType, Token, tokdict
from semantic import SemanticAction


class ActionType(enum.Enum):
    ACCEPT = enum.auto()
    MOVE = enum.auto()
    REDUCE = enum.auto()
    GOTO = enum.auto()


class LR1Item:
    """LR(1) Item
    lhs(str): left side of production
    rhs(list): right side of production and inserted dot
    exp_syms(list): expected symbols for reduction
    """

    def __init__(self, lhs, rhs, exp_syms):
        assert "." in rhs
        self.lhs = lhs
        self.rhs = list(rhs)
        self.exp_syms = set(exp_syms)
        if TokenType.e in self.exp_syms:
            self.exp_syms.remove(TokenType.e)

    def reduced(self):
        '''whether the item is reduced'''
        return self.rhs[-1] == '.'

    def next_sym(self):
        idx = self.rhs.index('.') + 1
        if idx >= len(self.rhs):
            return None
        else:
            return self.rhs[idx]

    def move(self):
        idx = self.rhs.index('.')
        assert not self.reduced(), "Can't move reduced item"
        lhs = self.lhs
        rhs = self.rhs[:]
        rhs[idx], rhs[idx + 1] = rhs[idx + 1], rhs[idx]
        exp_syms = set(self.exp_syms)
        return LR1Item(lhs, rhs, exp_syms)

    def get_production(self):
        rhs = list(self.rhs)
        rhs.remove('.')
        return Production(self.lhs, rhs)

    def __hash__(self):
        return hash((self.lhs, tuple(self.rhs), tuple(self.exp_syms)))

    def __eq__(self, other):
        if not isinstance(other, type(self)):
            return NotImplemented
        return self.lhs == other.lhs and self.rhs == other.rhs and self.exp_syms == other.exp_syms

    def __str__(self):
        return "{} -> {}, ({})".format(
            str(self.lhs),
            ' '.join(map(lambda s: str(s).replace("TokenType.", ''), self.rhs)),
            ' , '.join(map(lambda t: str(t).replace("TokenType.", ''), self.exp_syms)))


class LALRItem(LR1Item):
    @classmethod
    def from_lr1(cls, item):
        return LALRItem(item.lhs, item.rhs, item.exp_syms)

    def __hash__(self):
        return hash((self.lhs, tuple(self.rhs)))

    def __eq__(self, other):
        if not isinstance(other, type(self)):
            return NotImplemented
        return self.lhs == other.lhs and self.rhs == other.rhs


class LR1Grammar:
    """LR(1) Grammar
    """

    def __init__(self, grammar, start="S"):
        self.grammar = grammar
        self.production_idxs = {}
        idx = 0
        for productions in grammar.values():
            for production in productions:
                self.production_idxs[production] = idx
                idx += 1
        self.idx_productions = dict(
            zip(self.production_idxs.values(), self.production_idxs.keys()))
        self.terms, self.non_terms = self.get_syms()
        self.start = start
        self.first = {}
        self.first = self.get_first()
        # get action table
        self.action_table, self.status_items = self.get_action_table()

    def get_action_table(self):
        '''get automata of grammar'''
        assert len(self.grammar[self.start]) == 1, "Must have unique start symbol"
        # results
        item_set_idxs = {}
        status_items = {}
        # combination of action_table and goto table
        action_table = {}

        # initilize item_sets
        init_set = frozenset(
            self.get_closure(frozenset([self.grammar[self.start][0].get_lr1_item([TokenType.END])]))
        )
        item_sets = set([init_set])
        ana_stack = []
        ana_stack.append(init_set)
        item_set_idxs[init_set] = 0
        status_items[0] = init_set
        next_idx = 1

        while ana_stack:
            item_set = ana_stack.pop()
            item_set_idx = item_set_idxs[item_set]

            # handle reduction item
            for item in filter(lambda item: item.reduced(), item_set):
                if item.lhs == self.start:
                    self.set_action(action_table, item_set_idx,
                                    TokenType.END, ActionType.ACCEPT, item_set_idx,
                                    status_items=status_items, production_idxs=self.production_idxs)
                else:
                    production = item.get_production()
                    production_idx = self.production_idxs[production]
                    for exp_sym in item.exp_syms:
                        self.set_action(action_table, item_set_idx,
                                        exp_sym, ActionType.REDUCE, production_idx,
                                        status_items=status_items, production_idxs=self.production_idxs)
            next_syms = set(
                map(
                    lambda item: item.next_sym(),
                    filter(lambda item: not item.reduced(), item_set)
                )
            )
            for sym in next_syms:
                next_item_set = frozenset(self.get_goto(item_set, sym))
                next_item_set_idx = item_set_idxs.get(next_item_set)
                if next_item_set_idx is None:
                    next_item_set_idx = next_idx
                    item_set_idxs[next_item_set] = next_item_set_idx
                    status_items[next_item_set_idx] = next_item_set
                    next_idx += 1
                    item_sets.add(next_item_set)
                    ana_stack.append(next_item_set)

                # TODO add support to Action Symbol in grammar
                if isinstance(sym, TokenType):
                    self.set_action(action_table, item_set_idx,
                                    sym, ActionType.MOVE, next_item_set_idx,
                                    status_items=status_items, idx_productions=self.idx_productions)
                else:
                    self.set_action(action_table, item_set_idx,
                                    sym, ActionType.GOTO, next_item_set_idx,
                                    status_items=status_items, production_idxs=self.production_idxs)
        # status_items = dict(zip(item_set_idxs.values(), item_set_idxs.keys()))
        return action_table, status_items

    @classmethod
    def set_action(cls, action_table, idx, sym, act_type: ActionType, next_idx, **kwargs):
        action_line = action_table.get(idx)
        if not action_line:
            action_line = {}
            action_table[idx] = action_line
        assert action_line.get(sym) is None or action_line.get(sym) == (act_type, next_idx), \
            cls.__gen_conflict_msg(
                action_table, idx, sym, act_type, next_idx, **kwargs)
        action_line[sym] = (act_type, next_idx)

    @classmethod
    def __gen_conflict_msg(cls, action_table, idx, sym, act_type: ActionType, next_idx, **kwargs):
        action_line = action_table.get(idx)
        msg = "The grammar has confliction: action[{}][{}] = {} {} | {} {}\n".format(
            idx, str(sym), action_line.get(sym)[
                0], action_line.get(sym)[1], act_type, next_idx
        )

        def gen_detail(act_type, next_idx):
            nonlocal msg, kwargs
            if act_type == ActionType.REDUCE:
                msg += "Reduce {} to {}\n".format(next_idx,
                                                  str(kwargs['idx_productions'].get(next_idx)))
            else:
                msg += "Move {} to:\n{}\n".format(next_idx, str(
                    '\n'.join(map(str, kwargs['status_items'].get(next_idx)))))
        if 'status_items' in kwargs and 'idx_productions' in kwargs:
            gen_detail(act_type, next_idx)
            act_type, next_idx = action_line.get(sym)
            gen_detail(act_type, next_idx)
        return msg

    def get_closure(self, item_set):
        '''get_closure is to calculate the closure of an item set'''
        closure = set(item_set)
        while True:
            new_items = set()
            for item in closure:
                next_sym = item.next_sym()
                if next_sym is None:
                    continue
                productions = self.grammar.get(next_sym)
                if not productions:
                    continue
                tmp_idx = item.rhs.index('.') + 2
                if len(item.rhs) == tmp_idx:
                    exp_syms = set(item.exp_syms)
                else:
                    serial = item.rhs[tmp_idx:]
                    exp_syms = set(self.get_first(serial))
                    if TokenType.e in exp_syms:
                        exp_syms = exp_syms.union(item.exp_syms)
                # first(serial a)
                for production in productions:
                    new_items.add(production.get_lr1_item(exp_syms))
            new_closure = closure.union(new_items)
            if len(new_closure) == len(closure):
                break
            closure = new_closure
        # merge closure
        item_exp_syms = {}
        for item in map(LALRItem.from_lr1, closure):
            exp_syms = item_exp_syms.get(item)
            if exp_syms is None:
                exp_syms = set()
            exp_syms = exp_syms.union(item.exp_syms)
            item_exp_syms[item] = exp_syms
        closure_list = []
        for item, exp_syms in item_exp_syms.items():
            closure_list.append(LR1Item(item.lhs, item.rhs, exp_syms))
        return set(closure_list)

    def get_goto(self, item_set, sym):
        '''get_goto is to calculate the goto table of an item set'''
        return self.get_closure(
            map(
                lambda item: item.move(),
                filter(lambda item: item.next_sym() ==
                       sym and not item.reduced(), item_set)
            )
        )

    def get_first(self, target=None):
        '''get first set of every None terminated symbol
        if target is not None, only get first set of the target
        '''
        # get target's first
        if target:
            if isinstance(target, list):
                if len(target) != 1:
                    return self._get_serial_first(target)
                target = target[0]
            first_set = self.first.get(target)
            if first_set:
                return first_set
            return set()

        # or get first set of all symbols
        if self.first:
            return self.first

        self.first = {}
        first = self.first
        # set terminates' first set
        for term in self.terms:
            first[term] = set([term])
        # initialize non-terminates' first set
        for sym in self.non_terms:
            first[sym] = set()
        while True:
            changed = False
            for sym in self.non_terms:
                sym_first = first[sym]
                old_length = len(sym_first)
                productions = self.grammar[sym]
                for production in productions:
                    if not production.rhs:
                        sym_first.add(TokenType.e)
                        continue
                    has_empty = True
                    for rsym in production.rhs:
                        rsym_first = first[rsym]
                        # temporarily remove empty Token
                        rsym_first_sub = rsym_first.difference(
                            set([TokenType.e]))
                        # do union
                        sym_first = sym_first.union(rsym_first_sub)
                        if TokenType.e not in self.first[rsym]:
                            # the production can‘t deduce empty token
                            has_empty = False
                            break
                    if has_empty:
                        sym_first.add(TokenType.e)
                if old_length != len(sym_first):
                    first[sym] = sym_first
                    changed = True
            # loop until no change take place
            if not changed:
                break
        return self.first

    def _get_serial_first(self, target):
        serial_first = set()
        for sym in target:
            if sym == TokenType.e:
                continue
            sym_first = self.first[sym]
            serial_first = serial_first.union(sym_first)
            if TokenType.e not in sym_first:
                break
        return serial_first

    def get_syms(self):
        '''get grammar symbols
        Returns terminates and non-terminates'''
        terms = set()
        non_terms = set()
        for key in self.grammar:
            non_terms.add(key)
        for productions in self.grammar.values():
            for production in productions:
                for sym in production.rhs:
                    if isinstance(sym, TokenType):
                        terms.add(sym)
                    else:
                        non_terms.add(sym)
        return terms, non_terms

    def get_productions(self, sym):
        productions = self.grammar.get(sym)
        if not productions:
            productions = []
        return productions

    def save(self, path):
        result = {}
        # save action_table
        serial_action_table = {}

        def serialize_sym(sym):
            if isinstance(sym, TokenType):
                return sym.name
            else:
                return sym
        for key, value in self.action_table.items():
            tmpset = {}
            for sym, action in value.items():
                tmpset[serialize_sym(sym)] = action[0].name, action[1]
            serial_action_table[key] = tmpset
        result['action_table'] = serial_action_table

        serial_production_table = {}
        for idx, production in self.idx_productions.items():
            serial_production_table[idx] = str(production)
        result['productions'] = serial_production_table
        with open(path, "w+") as fp:
            json.dump(result, fp)

    @classmethod
    def load(cls, path):
        with open(path, "r") as fp:
            serial_table = json.load(fp)
        serial_action_table = serial_table["action_table"]
        # load action_table

        def unserialize_sym(sym):
            toktype = TokenType.__members__.get(sym)
            if toktype is None:
                return sym
            else:
                return toktype
        action_table = {}
        for key, value in serial_action_table.items():
            tmpset = {}
            for sym, action in value.items():
                tmpset[unserialize_sym(sym)] = ActionType[action[0]], action[1]
            action_table[int(key)] = tmpset

        productions = {}
        serial_productions = serial_table['productions']
        for key, value in serial_productions.items():
            prod = CFGGrammarLoader.parse_production(value, obj=True)
            productions[int(key)] = prod
        return action_table, productions


class LALRGrammar(LR1Grammar):
    def __init__(self, grammar, start="S"):
        super().__init__(grammar, start)
        self.action_table, self.status_items = self.merge(self)

    @classmethod
    def merge(cls, lr1):
        action_table = lr1.action_table
        status_items = lr1.status_items
        new_action_table = {}
        new_status_items = {}
        item_set_idxs = {}
        idx_conv = {}
        next_idx = 0
        # merge the item_sets
        for status, item_set in status_items.items():
            new_item_set = frozenset(map(LALRItem.from_lr1, item_set))
            idx = item_set_idxs.get(new_item_set)
            if idx is None:
                idx = next_idx
                item_set_idxs[new_item_set] = next_idx
                next_idx += 1
                new_status_items[idx] = new_item_set
            # should be merged
            else:
                old_item_set = new_status_items[idx]
                new_item_set = cls._merge_itemset(old_item_set, new_item_set)
                new_status_items[idx] = new_item_set
            # map the indexes
            idx_conv[status] = idx
        # modify action table
        for status, conv in action_table.items():
            for sym, action in conv.items():
                cls.set_action(
                    new_action_table,
                    idx_conv[status],
                    sym, action[0],
                    idx_conv[action[1]])
        return new_action_table, new_status_items

    @classmethod
    def _merge_itemset(cls, itemset1, itemset2):
        assert itemset1 == itemset2
        setdict = dict(zip(itemset2, itemset2))
        lalritems = []
        for item1 in itemset1:
            item2 = setdict[item1]
            lalritems.append(LALRItem(item1.lhs, item1.rhs,
                                      item1.exp_syms.union(item2.exp_syms)))
        return frozenset(lalritems)


class Production:
    def __init__(self, lhs: str, rhs: list, action=None):
        self.lhs = lhs
        self.rhs = rhs
        self.action = action

    def __eq__(self, other):
        return self.lhs == other.lhs and self.rhs == other.rhs

    def __hash__(self):
        return hash(tuple([self.lhs] + self.rhs))

    def __str__(self):
        action_str = "{{{}}}".format(self.action.name) if self.action else ""
        if not self.rhs:
            return self.lhs + " -> e " + action_str
        return self.lhs + " -> " + ' '.join(map(str, self.rhs)) + " " + action_str
            

    def get_lr1_item(self, exp_toks):
        return LR1Item(self.lhs, ["."] + self.rhs, exp_toks)


class CFGParseError(Exception):
    """Exception raised when parsing CFG Grammar"""
    pass


class CFGGrammarLoader:
    """Class to load cfg
    CFGGrammar.cfg -> {GrammarSymbol : DefList, ...}
    DefList -> [Def, ...]
    Def -> [Token | GrammarSymbol]
    """

    @classmethod
    def load_grammar(cls, path):
        with open(path, "r") as f:
            cont = f.read()
        grammar = cls.parse_grammar(cont)
        cls._check_grammar(grammar)
        return grammar

    @classmethod
    def parse_production(cls, prod_str, obj=True):
        '''parse one production, prod_str must be in format
        Left -> SYM1 SYM2 {Action}..
        '''
        defl_parts = prod_str.split("->")
        if len(defl_parts) != 2:
            raise CFGParseError("Invalid CFG Definition: {}", repr(prod_str))
        lhs, rhs = defl_parts
        lhs = lhs.strip()
        rhs = list(filter(lambda s: len(s) > 0, re.split(r"\s", rhs)))
        rhs = cls._parse_symlist(rhs)
        if rhs and isinstance(rhs[-1], SemanticAction):
            rhs, action = rhs[:-1], rhs[-1]
        else:
            action = None
        if not obj:
            return lhs, rhs, action
        else:
            return Production(lhs, rhs, action)

    @classmethod
    def parse_grammar(cls, cont):
        cont = cls._remove_comment(cont)
        tmpl = list(filter(lambda s: len(s) > 0, map(
            lambda s: s.strip(), cont.split("\n"))))

        grammar = {}
        # get each definition
        curr_s = ""
        defl = []
        for line in tmpl:
            if "->" in line:
                if len(curr_s) > 0:
                    defl.append(curr_s)
                curr_s = line
            else:
                curr_s += line
        if len(curr_s) > 0:
            defl.append(curr_s)

        # get definition left and right
        for line in defl:
            defl_parts = line.split("->")
            if len(defl_parts) != 2:
                raise CFGParseError("Invalid CFG Definition: {}", repr(line))
            lhs, rhss = defl_parts
            lhs = lhs.strip()
            # rhss = map(
            #     lambda s: filter(lambda s: len(s) > 0, re.split(r"\s", s)),
            #     rhss.split("|")
            # )
            grammar[lhs] = {}
            prods = []
            for rhs in rhss.split('|'):
                prod_str = "{} -> {}".format(lhs, rhs)
                prod = cls.parse_production(prod_str, obj=True)
                prods.append(prod)
            # deflist = cls._parse_deflist(rhss)
            grammar[lhs] = prods
        return grammar

    @classmethod
    def _check_grammar(cls, grammar):
        for prods in grammar.values():
            for prod in prods:
                for sym in prod.rhs:
                    assert not isinstance(sym, SemanticAction)
                    if isinstance(sym, TokenType):
                        continue
                    if sym not in grammar:
                        raise CFGParseError(
                            "No definition of {} was found".format(repr(sym)))

    @classmethod
    def _parse_deflist(cls, rhs):
        deflist = []
        for stmt in rhs:
            statdef = cls._parse_symlist(stmt)
            deflist.append(statdef)
        return deflist

    @classmethod
    def _parse_symlist(cls, stmt):
        symlist = []
        for sym in stmt:
            if sym.startswith('{') and sym.endswith('}'):
                sym = SemanticAction(sym[1:-1])
                symlist.append(sym)
            elif sym in tokdict:
                sym = tokdict[sym]
                if sym != TokenType.e:
                    symlist.append(sym)
            else:
                symlist.append(sym)
        return symlist

    @classmethod
    def _remove_comment(cls, cont):
        return re.sub("#.*", " ", cont)


def print_item_set(item_set):
    for item in item_set:
        print(str(item))


def main():
    global lr1, lalr, grammar, action_table, productions
    grammar = CFGGrammarLoader.load_grammar("defs/semantic_syntax.def")
    lr1 = LR1Grammar(grammar, start='Start')
    lr1.save("defs/semantic_syntax.json")


if __name__ == "__main__":
    main()
    # pass
