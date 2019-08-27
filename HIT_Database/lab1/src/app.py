import sys

import sqlite3
import math

from PyQt5.QtWidgets import QMainWindow, QApplication, QLabel, \
    QCheckBox, QLineEdit, QTextEdit, QTreeView, QWidget, QPushButton,\
    QHBoxLayout, QVBoxLayout, QMessageBox, QTabWidget, QMenu, QAction, \
    QActionGroup, QDialog, QInputDialog, QTableView, QGridLayout
from PyQt5.QtGui import QStandardItemModel, QStandardItem
from PyQt5.QtSql import QSqlQueryModel, QSqlTableModel, QSqlDatabase, QSqlQuery, QSqlRecord
from PyQt5.QtCore import Qt

class App(QMainWindow):
    """Application"""
    def __init__(self):
        super().__init__()
        self._init_db()
        self._init_ui()

    def _init_db(self):
        db = QSqlDatabase.addDatabase('QSQLITE')
        db.setDatabaseName('db.sqlite3')
        db.open()
        self.db = db

    def _init_ui(self):
        self.setWindowTitle("Lilac社团管理")
        self._init_position()
        self._init_components()
        self.show()

    def _init_position(self, width=1024, height=720):
        rec = QApplication.desktop().screenGeometry()
        self.setGeometry(
            (rec.width() - width) / 2, 
            (rec.height() - height) / 2,
            width, height)

    def _init_components(self):
        tab_widget = QTabWidget()
        self.setCentralWidget(tab_widget)
        member_widget = self._init_member_view()
        competition_widget = self._init_competition_view()
        activity_widget = self._init_activity_view()
        finance_manage_widget = self._init_finance_manage_view()
        summary_view = self._init_summary_view()

        tab_widget.addTab(member_widget, "成员")
        tab_widget.addTab(competition_widget, "竞赛")
        tab_widget.addTab(activity_widget, "活动")
        tab_widget.addTab(finance_manage_widget, "财务管理")
        tab_widget.addTab(summary_view, "概览")

    def _init_member_view(self):
        member_widget = QWidget()
        member_widget_layout = QVBoxLayout()
        member_widget.setLayout(member_widget_layout)
        self.member_table = MemberTableView(self.db)
        member_widget_layout.addWidget(self.member_table)

        return member_widget

    def _init_competition_view(self):
        competition_widget = QWidget()
        competition_widget_layout = QVBoxLayout()
        competition_widget.setLayout(competition_widget_layout)
        self.competition_table = CompetitionTableView(self.db)
        competition_widget_layout.addWidget(self.competition_table)

        return competition_widget

    def _init_activity_view(self):
        activity_widget = QWidget()
        activity_widget_layout = QVBoxLayout()
        activity_widget.setLayout(activity_widget_layout)
        self.activity_table = ActivityTableView(self.db)
        activity_widget_layout.addWidget(self.activity_table)

        return activity_widget

    def _init_finance_manage_view(self):
        finance_widget = QWidget()
        finance_widget_layout = QVBoxLayout()
        finance_widget.setLayout(finance_widget_layout)
        competition_invoice_label = QLabel()
        competition_invoice_label.setText("竞赛报销")
        competition_invoice_view = CompetitionInvoiceTableView(self.db)
        personal_invoice_label = QLabel()
        personal_invoice_label.setText("个人报销")
        personal_invoice_view = PersonalInvoiceTableView(self.db)
        finance_widget_layout.addWidget(competition_invoice_label)
        finance_widget_layout.addWidget(competition_invoice_view)
        finance_widget_layout.addWidget(personal_invoice_label)
        finance_widget_layout.addWidget(personal_invoice_view)
        return finance_widget
    
    def _init_summary_view(self):
        summary_view = QWidget()
        layout = QVBoxLayout()
        summary_view.setLayout(layout)
        label = QLabel()
        label.setText("成员解题数")
        query = '''SELECT Member.id as member, COUNT() as solver_num FROM Member, ChallengeSolver
        WHERE Member.id = ChallengeSolver.member_id GROUP BY Member.id;'''
        query_view = DatabaseQueryView(self.db, query)
        layout.addWidget(label)
        layout.addWidget(query_view)

        label = QLabel()
        label.setText("未报销竞赛")
        query = '''SELECT * FROM Competition WHERE Competition.id in (
            SELECT competition_id FROM CompetitionInvoice 
            WHERE competition_id = Competition.id AND CompetitionInvoice.complete = 0
        );'''
        query_view = DatabaseQueryView(self.db, query)
        layout.addWidget(label)
        layout.addWidget(query_view)

        return summary_view


class DatabaseQueryView(QTableView):
    def __init__(self, db, query, parent=None):
        super().__init__(parent)
        self.db = db
        self.query = query
        self._init_context_menu()
        self._init_model()

    def _init_context_menu(self):
        self.setContextMenuPolicy(Qt.CustomContextMenu)
        self.customContextMenuRequested.connect(self._on_context_menu)

    def _init_model(self):
        self.model = QSqlQueryModel()
        self.model.setQuery(QSqlQuery(self.query, self.db))
        self.setModel(self.model)

    def set_query(self, query):
        self.query = query
        self._init_model()

    def set_header_labels(self, labels):
        for idx, label in enumerate(labels):
            self.model.setHeaderData(idx + 1, Qt.Horizontal, label)

    def _on_context_menu(self, pos):
        _context_menu = QMenu(self)
        _refresh_action = QAction("刷新")
        _context_menu.addAction(_refresh_action)

        action = _context_menu.exec_(self.viewport().mapToGlobal(pos))
        if action == _refresh_action:
            self.set_query(self.query)

class DatabaseTableView(QTableView):
    """customized table view"""
    TABLE = ''
    def __init__(self, db, parent=None):
        super().__init__(parent)
        self.db = db
        self._init_model()
        self._init_context_menu()

    def _init_model(self):
        self.model = QSqlTableModel(db=self.db)
        self.model.setTable(self.TABLE)
        self.model.setEditStrategy(QSqlTableModel.OnManualSubmit)
        self.setModel(self.model)
        self.model.select()

    def _init_context_menu(self):
        self.setContextMenuPolicy(Qt.CustomContextMenu)
        self._context_menu = QMenu(self)
        self.customContextMenuRequested.connect(self._on_context_menu)
        self._add_row_action = QAction("添加行", self)
        self._del_row_action = QAction("删除行", self)
        self._submit_action = QAction("提交修改", self)
        self._modify_filter_action = QAction("筛选", self)
        self._update_action = QAction("刷新", self)
        self._show_detail_action = QAction("详情", self)

        self._context_menu.addAction(self._add_row_action)
        self._context_menu.addAction(self._del_row_action)
        self._context_menu.addAction(self._submit_action)
        self._context_menu.addAction(self._modify_filter_action)
        self._context_menu.addAction(self._update_action)
        self._context_menu.addAction(self._show_detail_action)

    def _on_context_menu(self, pos):
        action = self._context_menu.exec_(
            self.viewport().mapToGlobal(pos))
        if action == self._add_row_action:
            self.model.insertRows(self.model.rowCount(), 1)
        elif action == self._del_row_action:
            self.model.removeRow(self.currentIndex().row())
        elif action == self._submit_action:
            res = self.model.submitAll()
            if not res:
                QMessageBox.warning(self, "Sql Error", self.model.lastError().text())
        elif action == self._modify_filter_action:
            self._modify_filter()
        elif action == self._update_action:
            self._update_model()
        elif action == self._show_detail_action:
            self._show_detail()

    def _modify_filter(self):
        filter_cmd = self.model.filter()
        new_filter, succ = QInputDialog.getText(self, "过滤", "过滤表达式", text=filter_cmd)
        if succ:
            self.model.setFilter(new_filter)
            self.model.select()

    def _show_detail(self):
        row = self.currentIndex().row()
        idn = self.model.index(row, 0).data()
        if idn is None:
            return
        self._get_detail(idn).show()
        
    def _get_detail(self, idn):
        raise NotImplementedError()

    def _update_model(self):
        self.model.select()


class MemberTableView(DatabaseTableView):
    TABLE = 'Member'

    def _get_detail(self, idn):
        return MemberDetail(idn, self.db, self)


class ActivityTableView(DatabaseTableView):
    TABLE = 'Activity'


class CompetitionTableView(DatabaseTableView):
    TABLE = 'Competition'

    def _get_detail(self, idn):
        return CompetitionDetail(idn, self.db, self)


class ChallengeTableView(DatabaseTableView):
    TABLE = 'Challenge'

    def _get_detail(self, idn):
        return ChallengeDetail(idn, self.db, self)


class CompetitionInvoiceTableView(DatabaseTableView):
    TABLE = 'CompetitionInvoice'


class PersonalInvoiceTableView(DatabaseTableView):
    TABLE = 'PersonalInvoice'


class ChoiceDialog(QDialog):
    def __init__(self, db, tables, parent=None):
        super().__init__(parent=parent)
        self.db = db
        self.table_views = []
        self.res = []
        self.tables = list(tables)
        self._init_ui()
        self.show()

    def _init_ui(self):
        width = 640
        height = 420
        rec = QApplication.desktop().screenGeometry()
        self.setGeometry(
            (rec.width() - width) / 2,
            (rec.height() - height) / 2,
            width, height)
        layout = QHBoxLayout()
        self.setLayout(layout)
        choose_button = QPushButton("选择")
        left_widget = QWidget()
        left_layout = QGridLayout()
        left_widget.setLayout(left_layout)
        layout.addWidget(left_widget)
        layout.addWidget(choose_button)
        choose_button.clicked.connect(self._on_choose_click)

        wid_nums = len(self.tables)
        rows = round(math.sqrt((2 * wid_nums /3)))
        cols = math.ceil(wid_nums / rows)
        for i in range(wid_nums):
            table = self.tables[i]
            model = QSqlTableModel(db=self.db)
            model.setTable(table)
            table_view = QTableView()
            table_view.setModel(model)
            table_view.setSelectionBehavior(QTableView.SelectRows)
            model.select()
            col = i % cols
            row = i // cols
            left_layout.addWidget(table_view, row, col, 1, 1)
            self.table_views.append(table_view)

    def _on_choose_click(self):
        res = []
        for view in self.table_views:
            indexes = view.selectionModel().selectedRows()
            if not indexes:
                res.append(None)
            else:
                index = indexes[0]
                res.append(index.data())
        self.res = res
        self.close()

    @classmethod
    def choose_among(cls, db, tables, parent=None):
        dialog = ChoiceDialog(db, tables, parent)
        dialog.exec_()
        return dialog.res


class DetailDialog(QDialog):
    TABLE = ""
    def __init__(self, idn, db: QSqlDatabase, parent=None):
        super().__init__(parent)
        self.idn = idn
        self.db = db
        self._init_ui()

    def _init_ui(self):
        width = 640
        height = 420
        rec = QApplication.desktop().screenGeometry()
        self.setGeometry(
            (rec.width() - width) / 2,
            (rec.height() - height) / 2,
            width, height)

        self.tab_widget = QTabWidget(self)
        layout = QVBoxLayout()
        self.setLayout(layout)
        layout.addWidget(self.tab_widget)
        basic_view = self._init_basic_view()
        self.tab_widget.addTab(basic_view, "基本")

    def _init_basic_view(self):
        basic_view = QWidget()
        basic_view_layout = QGridLayout()
        basic_view.setLayout(basic_view_layout)
        query = QSqlQuery(
            "SELECT * FROM {} WHERE id = {};".format(self.TABLE, self.idn), self.db)
        assert query.next()
        record = query.record()
        for idx in range(record.count()):
            # record_panel = QWidget()
            # record_layout = QHBoxLayout()
            # record_panel.setLayout(record_layout)
            label = QLabel()
            label.setText(record.fieldName(idx))
            line = QLineEdit()
            line.setText(str(record.field(idx).value()))
            line.setReadOnly(True)
            # record_layout.addWidget(label)
            # record_layout.addWidget(line)
            # basic_view_layout.addWidget(record_panel)
            basic_view_layout.addWidget(label, idx, 0, 1, 2)
            basic_view_layout.addWidget(line, idx, 1, 1, 8)
        return basic_view


class MemberDetail(DetailDialog):
    TABLE = "Member"
    def __init__(self, idn, db, parent=None):
        super().__init__(idn, db, parent=parent)
        competition_view = self._init_competition_view()
        self.tab_widget.addTab(competition_view, "竞赛情况")

    def _init_competition_view(self):
        competition_view = QWidget()
        view_layout = QVBoxLayout()
        competition_view.setLayout(view_layout)

        self.query = '''
        SELECT name as competition, challenge, Challenge.score, weight FROM Competition, Challenge, ChallengeSolver
        WHERE ChallengeSolver.member_id = {} AND Competition.id = Challenge.competition_id
        AND Challenge.id = ChallengeSolver.challenge_id;'''.format(self.idn)
        self.competition_table = DatabaseQueryView(self.db, self.query)
        self.competition_table.setContextMenuPolicy(Qt.CustomContextMenu)
        self.competition_table.customContextMenuRequested.connect(self._on_competition_context_menu)

        view_layout.addWidget(self.competition_table)
        return competition_view

    def _on_competition_context_menu(self, pos):
        _context_menu = QMenu(self)
        _add_solve_action = QAction("修改解题情况")
        _context_menu.addAction(_add_solve_action)

        action = _context_menu.exec_(self.competition_table.viewport().mapToGlobal(pos))
        if action == _add_solve_action:
            self._add_challenge_solver()
            self.competition_table.model.setQuery(self.query)

    def _add_challenge_solver(self):
        challenge_id = ChoiceDialog.choose_among(self.db, ['Challenge'], self)[0]
        if challenge_id is None:
            return
        query = QSqlQuery(
            '''DELETE FROM ChallengeSolver WHERE challenge_id = {} 
            AND member_id = {};'''.format(challenge_id, self.idn), self.db)
        query = QSqlQuery("SELECT SUM(weight) FROM ChallengeSolver WHERE challenge_id = {};".format(challenge_id), self.db)
        if not query.next():
            assigned_weight = 0.0
        else:
            assigned_weight = query.value(0)
            if not assigned_weight:
                assigned_weight = 0.0
        print(assigned_weight)
        weight, res = QInputDialog.getDouble(self, "Weight", "weight", 0, 0, 1.0 - assigned_weight)
        if weight > 0 and res:
            query = QSqlQuery('''INSERT INTO ChallengeSolver (weight, challenge_id, member_id)
            VALUES({}, {}, {});'''.format(weight, challenge_id, self.idn), self.db)

    def _init_activity_view(self):
        pass

    def _init_invoice_view(self):
        pass


class CompetitionDetail(DetailDialog):
    TABLE = 'Competition'
    def __init__(self, idn, db, parent=None):
        super().__init__(idn, db, parent=parent)
        challenge_view = self._init_challenge_view()
        self.tab_widget.addTab(challenge_view, "题目")

    def _init_challenge_view(self):
        challenge_view = ChallengeTableView(self.db, self)
        challenge_view.model.setFilter("competition_id = {}".format(self.idn))
        
        challenge_view.model.primeInsert.connect(self._prev_challenge_insert)
        return challenge_view

    def _prev_challenge_insert(self, row, record: QSqlRecord):
        record.setValue(record.indexOf('competition_id'), self.idn)


class ChallengeDetail(DetailDialog):
    TABLE = 'Challenge'

class ActivityDetail(DetailDialog):
    TABLE = 'Activity'


def exec_query(query, parent=None):
    if not query.exec_():
        QMessageBox.warning(parent, "Sql Error", query.lastError().text())
        return False
    return True


if __name__ == "__main__":
    app = QApplication(sys.argv)
    ex = App()
    sys.exit(app.exec_())
