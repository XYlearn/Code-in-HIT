from semantic import ExprTree
from tokdef import Token, TokenType

expr0 = ExprTree(Token(TokenType.CINT, 0))
expr1 = ExprTree(Token(TokenType.CINT, 1))
expr2 = ExprTree(Token(TokenType.CINT, 2))
expr3 = ExprTree(Token(TokenType.CINT, 3))
expr4 = ExprTree(TokenType.ADD, [expr2, expr3])
expr5 = ExprTree(TokenType.LT, [expr0, expr4])

