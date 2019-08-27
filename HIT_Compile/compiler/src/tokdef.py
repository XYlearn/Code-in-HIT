"""
class and functions about Token
"""

import enum
from enum import Enum


class Token:
    """Token Type"""
    def __init__(self, t, v, start=-1, end=-1):
        self.type = t
        self.val = v
        self.start = start
        self.end = end

    def __str__(self):
        if self.val is None:
            return "{}".format(self.type.name)
        return "{}({})".format(self.type.name, self.val)


class TokenType(Enum):
    """Token Type"""
    IDN = enum.auto()
    COMMENT = enum.auto()
    # const value
    CINT = enum.auto()
    CFLOAT = enum.auto()
    CSTR = enum.auto()
    CCHAR = enum.auto()
    CCOMMENT = enum.auto()
    # type keywords
    CHAR = enum.auto()
    SHORT = enum.auto()
    INT = enum.auto()
    LONG = enum.auto()
    FLOAT = enum.auto()
    DOUBLE = enum.auto()
    BOOL = enum.auto()
    VOID = enum.auto()
    SIGNED = enum.auto()
    UNSIGNED = enum.auto()
    # other keywords
    PROC = enum.auto()
    CALL = enum.auto()
    IF = enum.auto()
    ELSE = enum.auto()
    ELIF = enum.auto()
    SWITCH = enum.auto()
    CASE = enum.auto()
    DO = enum.auto()
    WHILE = enum.auto()
    CONTINUE = enum.auto()
    BREAK = enum.auto()
    FOR = enum.auto()
    RETURN = enum.auto()
    STRUCT = enum.auto()
    ENUM = enum.auto()
    UNION = enum.auto()
    # calculate operators
    ADD = enum.auto()
    SUB = enum.auto()
    MUL = enum.auto()
    DIV = enum.auto()
    MOD = enum.auto()
    BOR = enum.auto()
    BAND = enum.auto()
    BXOR = enum.auto()
    BNEG = enum.auto()
    BLSHIFT = enum.auto()
    BRSHIFT = enum.auto()
    INC = enum.auto()
    DEC = enum.auto()
    CONST = enum.auto()
    # relation operators
    GT = enum.auto()
    LT = enum.auto()
    GE = enum.auto()
    LE = enum.auto()
    EQ = enum.auto()
    NE = enum.auto()
    # logical operators
    AND = enum.auto()
    OR = enum.auto()
    NOT = enum.auto()
    # seperators
    ASSIGN = enum.auto()
    ADD_ASSIGN = enum.auto()
    SUB_ASSIGN = enum.auto()
    MUL_ASSIGN = enum.auto()
    DIV_ASSIGN = enum.auto()
    MOD_ASSIGN = enum.auto()
    BOR_ASSIGN = enum.auto()
    BAND_ASSIGN = enum.auto()
    BXOR_ASSIGN = enum.auto()
    BLSHIFT_ASSIGN = enum.auto()
    BRSHIFT_ASSIGN = enum.auto()
    SEMI = enum.auto() # ;
    COMMA = enum.auto() # ,

    OC = enum.auto() # open curly {
    CC = enum.auto() # close curly }
    OB = enum.auto() # open bracket [
    CB = enum.auto() # close bracket ]
    OP = enum.auto() # open paren (
    CP = enum.auto() # close paren )

    DOT = enum.auto() # .
    PTR = enum.auto() # ->
    DEREF = enum.auto() # *
    REF = enum.auto() # &
    BRACKETS = enum.auto() # []

    TRUE = enum.auto()
    FALSE = enum.auto()

    # Tokens for Grammar
    e = enum.auto()
    END = enum.auto()

    def __str__(self):
        return self.name

tokdict = dict(zip(map(lambda tok: tok.name, TokenType), list(TokenType)))


keywords = {
    "char": TokenType.CHAR,
    "short": TokenType.SHORT,
    "int": TokenType.INT,
    "long": TokenType.LONG,
    "float": TokenType.FLOAT,
    "double": TokenType.DOUBLE,
    "bool": TokenType.BOOL,
    "void": TokenType.VOID,
    'proc': TokenType.PROC,
    'call': TokenType.CALL,
    'signed': TokenType.SIGNED,
    'unsigned': TokenType.UNSIGNED,
    "if": TokenType.IF,
    "elif": TokenType.ELIF,
    'else': TokenType.ELSE,
    "switch": TokenType.SWITCH,
    "case": TokenType.CASE,
    "do": TokenType.DO,
    "while": TokenType.WHILE,
    "continue": TokenType.CONTINUE,
    "break": TokenType.BREAK,
    "for": TokenType.FOR,
    "return": TokenType.RETURN,
    'struct': TokenType.STRUCT,
    'enum': TokenType.ENUM,
    'union': TokenType.UNION,
    'const': TokenType.CONST,
    'true': TokenType.TRUE,
    'flase': TokenType.FALSE
}


def get_keyword_type(word):
    '''get keyword type if word is keyword'''
    return keywords.get(word.strip())


class Action:
    pass
