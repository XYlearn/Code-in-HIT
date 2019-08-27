# 编译原理实验



更新至实验三

## 程序依赖

```shell
pip install PyQt5
```



## 程序使用：

```sh
# 使用python3
python src/app.py
```



## 目录结构

```sh
.
├── README.md
├── defs
│   ├── csyntax.def
│   ├── lexer.json
│   ├── semantic_syntax.def
│   ├── semantic_syntax.json
│   ├── subsyntax.def
│   ├── syntax.def
│   └── syntax.json
├── docs
│   ├── dfa.pdf
│   ├── dfa.tex
│   ├── regex.md
│   ├── syntax.md
│   ├── 编译原理实验报告-词法分析.doc # 实验一报告
│   ├── 编译原理实验报告-语法分析.docx # 实验二报告
│   └── 编译原理实验报告-语义分析.doc # 本次实验报告
├── genjs.py
├── src # 实验代码
│   ├── __init__.py
│   ├── app.py # 主程序
│   ├── grammar.py # 文法分析及导出文法分析
│   ├── lexer.py # 词法分析
│   ├── semantic.py # 各种语义分析相关函数、数据结构和语义动作处理函数
│   ├── syntax.py # 语法分析和文法分析的处理函数
│   ├── syntaxtree.py #语法树结构
│   └── tokdef.py # Token定义
└── tests
    ├── lex_test_01.c
    ├── semantic_test_01.s # 语义测试1
    ├── semantic_test_02.s # 语义测试2
    ├── semantic_test_03.s # 语义测试3
    ├── syntax_output.txt # 语法分析测试样例输出
    ├── syntax_test_01.s # 语法分析测试代码
    └── tests.py
```

