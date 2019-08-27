"""
Aho-Corasick with regex
"""
import ahocorasick
import functools
import abc


class ReRule(metaclass=abc.ABCMeta):
    """regex rule"""
    def __init__(self):
        self.childs = []

    @property
    def sat(self):
        '''check statisfy condition'''
        return True

    def has_child(self):
        '''check whether has child'''
        return bool(self.childs)

    def simplify_childs(self):
        '''get list of simplified childs'''
        if not self.has_child():
            return []
        return list(map(lambda child: child.simplify(), self.childs))

    def simplify(self):
        '''get a new simplified rule'''
        return self


class Contain(ReRule):
    """Contain rule. it's the basic rule other rules base on"""
    def __init__(self, word, cond=None):
        if not word:
            raise Exception("Zero-length word is invalid")
        self.word = word
        self.count = 0
        self.cond = cond
        self.childs = []

    @property
    def sat(self):
        if self.cond:
            return self.cond(self.count)
        else:
            return bool(self.count)

    def initialize(self):
        self.count = 0

    def process(self):
        self.count += 1


class And(ReRule):
    def __init__(self, *rules):
        self.childs = list(rules)
        self.value = 0

    @property
    def sat(self):
        return functools.reduce(lambda y, x: x.sat and y, self.childs, True)

    def simplify(self):
        childs = self.simplify_childs()
        new_rules = []
        for child in childs:
            if isinstance(child, type(self)):
                new_rules += child.childs
            else:
                new_rules.append(child)
        return And(*new_rules)


class Or(ReRule):
    def __init__(self, *rules):
        self.childs = list(rules)

    @property
    def sat(self):
        for child in self.childs:
            if child.sat:
                return True
        return False

    def simplify(self):
        childs = self.simplify_childs()
        new_childs = []
        for child in childs:
            if isinstance(child, type(self)):
                new_childs.extend(child.childs)
            else:
                new_childs.append(child)
        return Or(*new_childs)


class ReAutomaton(object):
    """Automaton """

    def __init__(self):
        self.automaton = ahocorasick.Automaton()
        self.rule = None
        self.word_rule = {}

    def set_rule(self, rule):
        self.rule = rule
        # simplify rule
        self.rule = self.rule.simplify()
        self.compile()

    def compile(self):
        if not self.rule:
            return
        self.word_rule = {}
        if not isinstance(self.rule, Contain):
            # trim duplicated Contain rule, and set word_rule
            stack = [self.rule]
            while stack:
                rule = stack.pop()
                childs = rule.childs
                for idx, child_rule in enumerate(childs):
                    if isinstance(child_rule, Contain):
                        existed_rule = self.word_rule.get(child_rule.word)
                        if existed_rule:
                            childs[idx] = existed_rule
                        else:
                            self.word_rule[child_rule.word] = child_rule
                    else:
                        stack.append(child_rule)
        else:
            self.word_rule[self.rule.word] = self.rule
        self.automaton.clear()
        for rule in self.word_rule.values():
            word = rule.word
            self.automaton.add_word(word, word)
        self.automaton.make_automaton()

    def match(self, haystack):
        if not self.rule:
            return False
        for rule in self.word_rule.values():
            rule.initialize()
        for _, word in self.automaton.iter(haystack):
            contain_rule = self.word_rule.get(word)
            assert contain_rule
            contain_rule.process()
        return self.rule.sat

def test():
    a = Contain("a")
    b = Contain("b")
    c = Contain("c")
    d = Contain("d")
    ab = And(a, b)
    cd = And(c, d)
    abcd = Or(ab, cd)
    rea = ReAutomaton()
    rea.set_rule(abcd)
    print(rea.match("acxcvmzxcvd"))


if __name__ == "__main__":
    test()
