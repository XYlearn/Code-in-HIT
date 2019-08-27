# 语法分析



## 声明语句

```
# 变量声明
VarDecl -> IDN | VarDecl OB CINT CB
VarDeclStat -> Type VarDecl SEMI


# 函数声明
FuncDecl -> IDN OP VarDeclList CP
FuncDeclStat -> Type FuncDecl SEMI
ParamDecl -> Type VarDecl
VarDeclList -> ParamDecl COMMA VarDeclList | ParamDecl | e

# 函数定义
FuncDefStat -> IDN OP VarDeclList CP Block

# 类型
Type -> SHORT | INT | LONG | FLOAT | DOUBLE | BOOL
```



## 表达式及赋值语句

```
# 表达式语句
Expr -> 
	# 普通运算表达式
	Expr ADD Expr |
	Expr SUB Expr |
	Expr MUL Expr |
	Expr DIV Expr |
	Expr MOD Expr |
	# 位运算表达式
	Expr BOR Expr |
	Expr BAND Expr |
	Expr BLSHIFT Expr |
	Expr BRSHIFT Expr |
	BNEG Expr |
	# 逻辑运算表达式
	Expr AND Expr |
	Expr OR Expr |
	NOT Expr |
	# 括号表达式
	OP Expr CP |
	# 变量相关
	VarRef |
	VarRef AssignOp Expr | # 赋值
	INC VarRef |
	DEC VarRef |
	VarRef INC |
	VarRef DEC |
	# 常量
	CINT |
	CFLOAT
VarRef -> IDN | VarRef OB CINT CB
ExprStat -> Expr SEMI
```

```
# 表达式语句（优先级）
Expr -> 
	# 普通运算表达式
	Expr ADD Expr |
	Expr SUB Expr |
	Expr MUL Expr |
	Expr DIV Expr |
	Expr MOD Expr |
	# 位运算表达式
	Expr BOR Expr |
	Expr BAND Expr |
	Expr BLSHIFT Expr |
	Expr BRSHIFT Expr |
	BNEG Expr |
	# 逻辑运算表达式
	Expr AND Expr |
	Expr OR Expr |
	NOT Expr |
	# 括号表达式
	OP Expr CP |
	# 变量相关
	VarRef |
	# 赋值表达式
	VarRef AssignOp Expr | 
	LeftIncDec VarRef |
	VarRef RightIncDec
	# 常量
	CINT |
	CFLOAT
LeftIncDec -> INC | DEC
RightIncDec -> INC | DEC
AssignOp -> ASSIGN | ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | 
    BOR_ASSIGN | BAND_ASSIGN | BXOR_ASSIGN | BLSHIFT_ASSIGN | BRSHIFT_ASSIGN
VarRef -> IDN | VarRef OB CINT CB
ExprStat -> Expr SEMI
```



## 分支语句

```
# 语句块
Block -> OC Block CC | OC Statements CC
```



```
# 分支语句
Branch -> IF OP Expr CP Block ElseBranch
ElseBranch -> e | 
		ELIF OP Expr CP Block | 
		ELSE Block
```



## 循环语句

```
Loop -> WHILE OP Expr CP Block |
			FOR OP Expr SEMI Expr SEMI Expr CP Block
```



## 过程调用语句

```
# 过程调用
Call -> IDN OP Args CP
Args -> Expr COMMA Args | Expr | e
```



## 语句

```
Statement -> 
	VarDeclStat | 
  ExprStat |
  Branch |
  Loop
Statements -> e | Statement Statements
```



## 宏观定义

```
Language -> FuncDeclStat | FuncDefStat
```





## Appendix

```python
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
    SHORT = enum.auto()
    INT = enum.auto()
    LONG = enum.auto()
    FLOAT = enum.auto()
    DOUBLE = enum.auto()
    BOOL = enum.auto()
    # other keywords
    IF = enum.auto()
    ELIF = enum.auto()
    SWITCH = enum.auto()
    CASE = enum.auto()
    DO = enum.auto()
    WHILE = enum.auto()
    CONTINUE = enum.auto()
    BREAK = enum.auto()
    FOR = enum.auto()
    RETURN = enum.auto()
    # calculate operators
    ADD = enum.auto() # +
    SUB = enum.auto() # -
    MUL = enum.auto() # *
    DIV = enum.auto() # /
    MOD = enum.auto() # %
    BOR = enum.auto() # |
    BAND = enum.auto() # &
    BXOR = enum.auto() # ^
    BNEG = enum.auto() # ~
    BLSHIFT = enum.auto() # <<
    BRSHIFT = enum.auto() # >>
    INC = enum.auto() # ++
    DEC = enum.auto() # --
    # relation operators
    GT = enum.auto() # >
    LT = enum.auto() # <
    GE = enum.auto() # >=
    LE = enum.auto() # <=
    EQ = enum.auto() # ==
    NE = enum.auto() # !=
    # logical operators
    AND = enum.auto() # &&
    OR = enum.auto() # ||
    NOT = enum.auto() # !
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
```

