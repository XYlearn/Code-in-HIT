import sys

import sqlite3

from PyQt5.QtWidgets import QMainWindow, QApplication, QLabel, \
    QCheckBox, QLineEdit, QTextEdit, QTreeView, QWidget, QPushButton,\
    QHBoxLayout, QVBoxLayout, QMessageBox
from PyQt5.QtGui import QStandardItemModel, QStandardItem

class App(QMainWindow):
    def __init__(self):
        super().__init__()
        self.initUI()
        self.connDB()

    def initUI(self):
        # create global widget
        self.gwid = QWidget(self)
        self.setCentralWidget(self.gwid)

        panelWidget = self.initQueryPanel()
        sqlEdit = self.initSqlPanel()
        treeView = self.initTreeView()
        
        leftPanel = QWidget()
        leftLayout = QVBoxLayout()
        leftLayout.addWidget(panelWidget)
        leftLayout.addWidget(sqlEdit)
        leftPanel.setLayout(leftLayout)

        gLayout = QHBoxLayout()
        gLayout.addWidget(leftPanel)
        gLayout.addWidget(treeView)
        self.gwid.setLayout(gLayout)

        self.queryButton.clicked.connect(self.queryButtonClicked)

        self.show()

    attrs = ['sno', 'name', 'ageFrom', 'ageTo', 'sex', 'class', 'dept', 'addr']

    def initQueryPanel(self):
        # labels
        self.snoLabel = QLabel()
        self.snoLabel.setText("学号")
        self.nameLabel = QLabel()
        self.nameLabel.setText("姓名")
        self.ageFromLabel = QLabel()
        self.ageFromLabel.setText("年龄自")
        self.ageToLabel = QLabel()
        self.ageToLabel.setText("年龄到")
        self.sexLabel = QLabel()
        self.sexLabel.setText("性别")
        self.classLabel = QLabel()
        self.classLabel.setText("班级")
        self.deptLabel = QLabel()
        self.deptLabel.setText("系别")
        self.addrLabel = QLabel()
        self.addrLabel.setText("地址")

        # line edit
        self.snoEdit = QLineEdit()
        self.nameEdit = QLineEdit()
        self.ageFromEdit = QLineEdit()
        self.ageToEdit = QLineEdit()
        self.sexEdit = QLineEdit()
        self.classEdit = QLineEdit()
        self.deptEdit = QLineEdit()
        self.addrEdit = QLineEdit()

        # checkboxes
        self.snoCheckBox = QCheckBox()
        self.nameCheckBox = QCheckBox()
        self.ageFromCheckBox = QCheckBox()
        self.ageToCheckBox = QCheckBox()
        self.sexCheckBox = QCheckBox()
        self.classCheckBox = QCheckBox()
        self.deptCheckBox = QCheckBox()
        self.addrCheckBox = QCheckBox()

        # query button
        self.queryButton = QPushButton()
        self.queryButton.setText("查询")

        snoPanel = QWidget()
        namePanel = QWidget()
        ageFromPanel = QWidget()
        ageToPanel = QWidget()
        sexPanel = QWidget()
        classPanel = QWidget()
        deptPanel = QWidget()
        addrPanel = QWidget()

        snoLayout = QHBoxLayout()
        nameLayout = QHBoxLayout()
        ageFromLayout = QHBoxLayout()
        ageToLayout = QHBoxLayout()
        sexLayout = QHBoxLayout()
        classLayout = QHBoxLayout()
        deptLayout = QHBoxLayout()
        addrLayout = QHBoxLayout()

        snoLayout.addWidget(self.snoCheckBox)
        snoLayout.addWidget(self.snoLabel)
        snoLayout.addWidget(self.snoEdit)
        nameLayout.addWidget(self.nameCheckBox)
        nameLayout.addWidget(self.nameLabel)
        nameLayout.addWidget(self.nameEdit)
        ageFromLayout.addWidget(self.ageFromCheckBox)
        ageFromLayout.addWidget(self.ageFromLabel)
        ageFromLayout.addWidget(self.ageFromEdit)
        ageToLayout.addWidget(self.ageToCheckBox)
        ageToLayout.addWidget(self.ageToLabel)
        ageToLayout.addWidget(self.ageToEdit)
        sexLayout.addWidget(self.sexCheckBox)
        sexLayout.addWidget(self.sexLabel)
        sexLayout.addWidget(self.sexEdit)
        classLayout.addWidget(self.classCheckBox)
        classLayout.addWidget(self.classLabel)
        classLayout.addWidget(self.classEdit)
        deptLayout.addWidget(self.deptCheckBox)
        deptLayout.addWidget(self.deptLabel)
        deptLayout.addWidget(self.deptEdit)
        addrLayout.addWidget(self.addrCheckBox)
        addrLayout.addWidget(self.addrLabel)
        addrLayout.addWidget(self.addrEdit)


        snoPanel.setLayout(snoLayout)
        namePanel.setLayout(nameLayout)
        ageFromPanel.setLayout(ageFromLayout)
        ageToPanel.setLayout(ageToLayout)
        sexPanel.setLayout(sexLayout)
        classPanel.setLayout(classLayout)
        deptPanel.setLayout(deptLayout)
        addrPanel.setLayout(addrLayout)

        # layout
        panleWidget = QWidget()
        panelLayout = QHBoxLayout()
        colWidget1 = QWidget()
        colLayout1 = QVBoxLayout()
        colWidget2 = QWidget()
        colLayout2 = QVBoxLayout()

        colLayout1.addWidget(snoPanel)
        colLayout1.addWidget(namePanel)
        colLayout1.addWidget(ageFromPanel)
        colLayout1.addWidget(ageToPanel)
        colLayout2.addWidget(sexPanel)
        colLayout2.addWidget(classPanel)
        colLayout2.addWidget(deptPanel)
        colLayout2.addWidget(addrPanel)

        colWidget1.setLayout(colLayout1)
        colWidget2.setLayout(colLayout2)
        panelLayout.addWidget(colWidget1)
        panelLayout.addWidget(colWidget2)
        panelLayout.addWidget(self.queryButton)
        panleWidget.setLayout(panelLayout)

        return panleWidget

    def initSqlPanel(self):
        self.sqlEdit = QTextEdit()
        self.sqlEdit.setReadOnly(True)
        return self.sqlEdit

    def initTreeView(self):
        self.treeView = QTreeView()
        self.model = QStandardItemModel()
        self.model.setHorizontalHeaderLabels(["sno", "sname", "sage", "ssex", "sclass", "sdept", "saddr"])
        self.treeView.setModel(self.model)
        return self.treeView

    def connDB(self):
        self.conn = sqlite3.connect("stu.db")
        if self.conn is None:
            QMessageBox.critical(self, "Error", 'Fail to Connect to stu.db')
            exit(0)

    def queryButtonClicked(self):
        sql = "SELECT * FROM Student "

        filter_stat = []
        if self.snoCheckBox.isChecked():
            filter_stat.append("sno LIKE \"{}\"".format(self.snoEdit.text()))
        if self.nameCheckBox.isChecked():
            filter_stat.append("sname LIKE \"{}\"".format(self.nameEdit.text()))
        if self.ageFromCheckBox.isChecked():
            filter_stat.append("sage >= {}".format(self.ageFromEdit.text()))
        if self.ageToCheckBox.isChecked():
            filter_stat.append("sage <= {}".format(self.ageToEdit.text()))
        if self.sexCheckBox.isChecked():
            filter_stat.append("ssex = \"{}\"".format(self.sexEdit.text()))
        if self.classCheckBox.isChecked():
            filter_stat.append("sclass LIKE \"{}\"".format(self.classEdit.text()))
        if self.deptCheckBox.isChecked():
            filter_stat.append("sdept LIKE \"{}\"".format(self.deptEdit.text()))
        if self.addrCheckBox.isChecked():
            filter_stat.append("saddr LIKE \"{}\"".format(self.addrEdit.text()))
        
        if len(filter_stat) != 0:
            sql = sql + "WHERE " + ' AND '.join(filter_stat)
        sql += ";"
        self.sqlEdit.setPlainText(sql)

        res = self.conn.execute(sql).fetchall()
        self.model.clear()
        self.model.setHorizontalHeaderLabels(["sno", "sname", "sage", "ssex", "sclass", "sdept", "saddr"])
        for item in res:
            print(item)
            row = list(map(QStandardItem, map(str, item)))
            self.model.appendRow(row)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    ex = App()
    sys.exit(app.exec_())
