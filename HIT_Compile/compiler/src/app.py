import sys
from PyQt5.QtWidgets import QApplication, QWidget, QMainWindow, QTextEdit,\
    QPushButton, QHBoxLayout, QVBoxLayout, QStatusBar, QMessageBox, QFileDialog, QCheckBox
from PyQt5.QtGui import QFont

from json import JSONDecodeError
from lexer import Lexer, LexException
from syntax import SyntaxAnalyser
from semantic import SemanticContext


class App(QMainWindow):
    def __init__(self):
        super().__init__()
        self.title = "My Compiler"
        self.initUI()
        self.initLexer()
        self.initSyntax()

    def initUI(self):
        self.setWindowTitle(self.title)
        self.setGeometry(10, 10, 800, 600)

        self.font_ = QFont('Mono')

        # global widget
        self.widget = QWidget(self)
        self.setCentralWidget(self.widget)

        # textEdit
        self.textEdit = QTextEdit()
        self.textEdit.setFont(self.font_)

        # buttons
        self.loadButton = QPushButton()
        self.loadButton.setText("Load")
        self.lexButton = QPushButton()
        self.lexButton.setText("Lex")
        self.syntaxButton = QPushButton()
        self.syntaxButton.setText("Syntax")
        self.semanticButton = QPushButton()
        self.semanticButton.setText("Semantic")
        self.semanticMode = QCheckBox()
        self.semanticMode.setText("raw")
        self.semanticPanel = QWidget()
        self.symtabButton = QPushButton()
        self.symtabButton.setText("SymTab")

        self.buttonPanel = QWidget()
        self.buttonLayout = QVBoxLayout()
        self.buttonLayout.addStretch(1)
        self.buttonLayout.addWidget(self.loadButton)
        self.buttonLayout.addStretch(1)
        self.buttonLayout.addWidget(self.lexButton)
        self.buttonLayout.addStretch(1)
        self.buttonLayout.addWidget(self.syntaxButton)
        self.buttonLayout.addStretch(1)
        self.buttonLayout.addWidget(self.semanticButton)
        self.buttonLayout.addWidget(self.semanticMode)
        self.buttonLayout.addStretch(1)
        self.buttonLayout.addWidget(self.symtabButton)
        self.buttonLayout.addStretch(1)
        self.buttonPanel.setLayout(self.buttonLayout)

        # output
        self.ouputBoard = QTextEdit()
        self.ouputBoard.setReadOnly(True)
        self.ouputBoard.setFont(self.font_)

        # lex layout
        self.lexLayout = QHBoxLayout()
        self.lexLayout.addWidget(self.textEdit)
        self.lexLayout.addWidget(self.buttonPanel)
        self.lexLayout.addWidget(self.ouputBoard)
        self.widget.setLayout(self.lexLayout)

        # setup signal slots
        self.lexButton.clicked.connect(self.lexButtonClicked)
        self.syntaxButton.clicked.connect(self.syntaxButtonClicked)
        self.semanticButton.clicked.connect(self.semanticButtonClicked)
        self.loadButton.clicked.connect(self.loadButtonCliecked)
        self.symtabButton.clicked.connect(self.symTabClicked)

        self.show()

    def initLexer(self):
        try:
            self.lexer = Lexer.load_lexer("defs/lexer.json")
        except (IOError, JSONDecodeError):
            QMessageBox.critical(self, "Error", "Fail to load lexer.json")
            exit(0)

    def initSyntax(self):
        try:
            self.syntax = SyntaxAnalyser("defs/semantic_syntax.json")
        except (IOError, JSONDecodeError):
            QMessageBox.critical(self, "Error", "Fail to load syntax.json")
            exit(0)

    def lexButtonClicked(self):
        text = self.textEdit.toPlainText()
        try:
            tokens, err_strs = self.lexer.lex(text)
            output = ''
            if len(err_strs) != 0:
                output += '[-] Some errors occur:\n' + \
                    '\n'.join(err_strs) + '\n'
            output += "[+] Tokens:\n"
            output += self.getLexResultText(tokens)
        except LexException as e:
            output = e.args[0]
        self.ouputBoard.setPlainText(output)

    def syntaxButtonClicked(self):
        text = self.textEdit.toPlainText()
        # lex
        tokens, err_strs = self.lexer.lex(text)
        output = ''
        if len(err_strs) != 0:
            output += '[-] Some errors occur:\n' + '\n'.join(err_strs) + '\n'

        tree, syntax_err_strs = self.syntax.analyze(tokens, text)
        err_strs.extend(syntax_err_strs)
        if len(err_strs) != 0:
            output += '[-] Some errors occur:\n' + '\n'.join(err_strs) + '\n'
        output += "[+] Syntax Analyze Results:\n"
        output += tree.get_printable()

        self.ouputBoard.setPlainText(output)

    def semanticButtonClicked(self):
        text = self.textEdit.toPlainText()
        # lex
        tokens, err_strs = self.lexer.lex(text)
        output = ''
        if len(err_strs) != 0:
            output += '[-] Some errors occur:\n' + '\n'.join(err_strs) + '\n'
        context = SemanticContext()
        tree, syntax_err_strs = self.syntax.analyze(
            tokens, text, semantic=True, context=context)
        err_strs.extend(syntax_err_strs)
        if len(err_strs) != 0:
            output += '[-] Some errors occur:\n' + '\n'.join(err_strs) + '\n'
        output += "[+] Syntax Analyze Results:\n"
        funcs = {}
        for func, symbol in context.func_symtab.items():
            funcs[symbol.offset] = func
        for addr, ins in enumerate(context.code):
            if addr in funcs:
                output += "\nPROC {}:\n".format(funcs[addr])
            if self.semanticMode.isChecked():
                insstr = str(ins)
            else:
                insstr = ins.get_pt()
            output += "{:03}: {:s}\n".format(addr, insstr)
        self.ouputBoard.setText(output)

    def symTabClicked(self):
        text = self.textEdit.toPlainText()
        # lex
        tokens, err_strs = self.lexer.lex(text)
        output = ''
        if len(err_strs) != 0:
            output += '[-] Some errors occur:\n' + '\n'.join(err_strs) + '\n'
        context = SemanticContext()
        tree, syntax_err_strs = self.syntax.analyze(
            tokens, text, semantic=True, context=context)
        err_strs.extend(syntax_err_strs)
        if len(err_strs) != 0:
            output += '[-] Some errors occur:\n' + '\n'.join(err_strs) + '\n'
        output += "[+] Function Symbol Table:\n\n"
        for func, symbol in context.func_symtab.items():
            output += "*{} {} at {}\n".format(func, str(symbol.type), hex(symbol.offset))
        output += '\n'
        output += "[+] Variable Symbol Table:\n\n"
        for name, syms in context.namespaces.items():
            output += "*{}\n".format(name)
            for sym in syms.values():
                output += "->{} {} at {}\n".format(sym.name, str(sym.type), sym.offset)
            output += '\n'
        self.ouputBoard.setText(output)

    def loadButtonCliecked(self):
        option = QFileDialog.Options()
        fileName, _ = QFileDialog.getOpenFileName(
            self, "QFileDialog.getOpenFileName()", "./tests/", "All Files (*)")
        try:
            with open(fileName, "r") as f:
                cont = f.read()
            self.textEdit.setText(cont)
        except IOError:
            pass

    def getLexResultText(self, tokens):
        output = ''
        text = self.textEdit.toPlainText()
        for token in tokens:
            token_str = text[token.start: token.end]
            output += "<{},{}>\n    {}\n".format(
                token.type.name, repr(token.val), token_str)
        return output


if __name__ == "__main__":
    app = QApplication(sys.argv)
    ex = App()
    sys.exit(app.exec_())
