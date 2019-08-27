- identifier:
```
[_A-Za-z][_0-9A-Za-z]*
```

- key words:

  - types:

  ```c
  // 类型
  void | short | int | long | float | double | bool
  // 分支
  if | else | switch | case
  // 循环
  do | while | continue | break | for
  // 过程调用
  return
  ```

  

  - operator:

  ```c
  // 算数运算符
  {"+", "-", "*", "/", "%", "|", "&", "^", "~", "<<", ">>", "++", "--"}
  // 关系运算符
  {">", "<", ">=", "<=", "==", "!="}
  // 逻辑运算符
  {"&&", "||", "!"}
  ```

  

  - seperator:

  ```c
  //assign seperator
  {"=", "+=", "-=", "*=", "/=", "%=", "|=", "&=", "^=", "<<=", ">>="}
  //statement seperator
  {";"}
  //array seperator
  {"[", "]"}
  //float seperator
  {"."}
  ```

  

- const number

  - integer

    ```
    // decimal
    [1-9][0-9]*
    // hexical
    0x[0-9a-fA-F]+
    // octal
    0[0-7]*
    // character
    '\\.|[^']'
    ```

  - float number

    ```
    [0-9]+.[0-9]+(e[0-9]+)?
    ```

  - literal string

    ```
    \"(\\.|[^"\\])*\"
    ```

    

- comment

  ```
  /\\*(.|[\\n\\r])*\\*/
  ```

  

