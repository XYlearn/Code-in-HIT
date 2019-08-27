"""definition of SyntaxTree"""

from tokdef import TokenType, Token

class SyntaxTree:
    """Syntax Tree"""
    def __init__(self, sym):
        self.parent = None
        self.childs = []
        self.sym = sym
        # for semantic analysis
        self.attrs = {}

    def get_attr(self, attr):
        return self.attrs.get(attr)

    def set_attr(self, attr, value):
        self.attrs[attr] = value

    def get_sym_name(self):
        if isinstance(self.sym, Token):
            return self.sym.type.name
        elif isinstance(self.sym, TokenType):
            return self.sym.name
        else:
            return self.sym

    def add_child(self, child):
        if isinstance(child, SyntaxTree):
            child.parent = self
        self.childs.append(child)

    def set_parent(self, parent):
        parent.add_child(self)

    def is_leaf(self):
        return isinstance(self.sym, TokenType) or isinstance(self.sym, Token)

    def get_printable(self, level=0):
        try:
            from asciitree import LeftAligned
            from collections import OrderedDict as OD
            from asciitree.drawing import BoxStyle, BOX_DOUBLE, BOX_ASCII
            def get_ascii_tree(node):
                if node.is_leaf():
                    return str(node.sym), {}
                return str(node.sym), OD(map(get_ascii_tree, node.childs))
            tree = {str(self.sym): get_ascii_tree(self)[1]}
            tr = LeftAligned(draw=BoxStyle(gfx=BOX_ASCII, indent=0))
            return str(tr(tree))
        except Exception as e:
            print(e.args)
        if self.is_leaf():
            return " " * level + str(self.sym) + '\n'
        pp = " " * level + str(self.sym) + '\n'
        for child in self.childs:
            pp += child.get_printable(level+1)
        return pp
