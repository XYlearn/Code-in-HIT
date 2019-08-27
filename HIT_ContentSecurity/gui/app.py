import matplotlib.pyplot as plt

import anal

from datetime import datetime
from PyQt5.QtWidgets import (
    QPushButton, QTextEdit, QListView, QLabel, QMainWindow, QWidget,
    QHBoxLayout, QVBoxLayout, QTextBrowser, QSizePolicy, QTabWidget,
    QTableView, QMessageBox, QCheckBox
)
from PyQt5.QtCore import QThreadPool, pyqtSlot
from PyQt5.Qt import QStandardItemModel, QStandardItem, QHeaderView

from matplotlib.backends.backend_qt5agg import (
    FigureCanvasQTAgg as FigureCanvas
)
from matplotlib.figure import Figure
from matplotlib.font_manager import FontProperties

from weibospider import Scheduler, DBConn
from strutils import ReAutomaton, And, Or, Contain, ReRule
from react import Accuser


class App(QMainWindow):
    def __init__(self):
        super().__init__()
        # self.gfont = FontProperties(fname='assets/PingFang.ttc')
        self.accuser = Accuser()
        self.dbconn = DBConn()
        self.scheduler = Scheduler(self.dbconn)
        self.scheduler_status = False
        self.automaton = ReAutomaton()
        self.analyzer = anal.Analyzer(self.dbconn)
        self.init_ui()

    def closeEvent(self, a0):
        self.scheduler.browser.quit()
        return super().closeEvent(a0)

    def init_ui(self):
        self.setWindowTitle("Weibo Monitor")
        self.setGeometry(30, 30, 1024, 720)
        self.init_components()
        self.show()

    def init_components(self):
        self.gwidget = QTabWidget()
        self.setCentralWidget(self.gwidget)
        self.glayout = QHBoxLayout()
        self.crawl_widget = QWidget()
        self.ana_widget = QWidget()
        self.init_crawl_widget()
        self.init_ana_widget()
        self.gwidget.addTab(self.crawl_widget, "Crawl")
        self.gwidget.addTab(self.ana_widget, "Analyze")
        self.init_triggers()
        self.running = False

    def init_crawl_widget(self):
        self.llayout = QVBoxLayout()
        self.crawl_widget.setLayout(self.llayout)
        self.log_label = QLabel("Log")
        self.log_list = QTableView()
        self.log_model = QStandardItemModel()
        self.log_model.setColumnCount(3)
        self.log_model.setHorizontalHeaderLabels(["Status", "Time", "Message"])
        self.log_list.setModel(self.log_model)
        self.log_list.horizontalHeader().setSectionResizeMode(QHeaderView.Stretch)
        self.log_list.horizontalHeader().setSectionResizeMode(
            0, QHeaderView.ResizeToContents)
        self.log_list.horizontalHeader().setSectionResizeMode(
            1, QHeaderView.ResizeToContents)
        self.log_list.horizontalHeader().setSectionResizeMode(
            2, QHeaderView.ResizeToContents)
        log_widget = QWidget()
        log_layout = QVBoxLayout()
        log_layout.addWidget(self.log_label)
        log_layout.addWidget(self.log_list)
        log_widget.setLayout(log_layout)
        self.rule_label = QLabel("Rule")
        self.rule_edit = QTextEdit()
        rule_widget = QWidget()
        rule_layout = QVBoxLayout()
        rule_layout.addWidget(self.rule_label)
        rule_layout.addWidget(self.rule_edit)
        rule_widget.setLayout(rule_layout)
        self.crawl_button = QPushButton('Crawl')
        self.llayout.addWidget(log_widget)
        self.llayout.addWidget(rule_widget)
        self.llayout.addWidget(self.crawl_button)
        self.llayout.setStretch(0, 15)
        self.llayout.setStretch(1, 6)
        self.llayout.setStretch(2, 1)

    def init_ana_widget(self):
        self.rlayout = QHBoxLayout()
        self.ana_widget.setLayout(self.rlayout)
        self.fig = Figure()
        self.canvas = FigureCanvas(self.fig)
        self.canvas_label = QLabel("Result")
        canvas_widget = QWidget()
        canvas_layout = QVBoxLayout()
        canvas_widget.setLayout(canvas_layout)
        canvas_layout.addWidget(self.canvas_label)
        canvas_layout.addWidget(self.canvas)
        canvas_layout.setStretch(0, 1)
        canvas_layout.setStretch(1, 20)

        self.tracklist_panel = QWidget()
        self.tracklist_layout = QVBoxLayout()
        self.tracklist_panel.setLayout(self.tracklist_layout)
        self.init_topic_tracklist()
        self.init_weibo_tracklist()
        self.tracklist_layout.addWidget(self.topic_tracklist_widget)
        self.tracklist_layout.addWidget(self.weibo_tracklist_widget)
        self.rlayout.addWidget(self.tracklist_panel)
        self.rlayout.addWidget(canvas_widget)
        cfg = self.scheduler.load_config()
        for topic_name in cfg['topic_tracklist']:
            self.topic_tracklist_model.appendRow(QStandardItem(topic_name))
        for weibo_id in cfg["weibo_id_tracklist"]:
            self.weibo_tracklist_model.appendRow(QStandardItem(str(weibo_id)))

    def init_topic_tracklist(self):
        self.topic_tracklist = QListView()
        self.topic_tracklist_label = QLabel("Topic Track List")
        self.topic_tracklist_model = QStandardItemModel()
        self.topic_tracklist.setModel(self.topic_tracklist_model)
        topic_tracklist_buttons = QWidget()
        topic_tracklist_buttons_layout = QHBoxLayout()
        topic_tracklist_buttons_layout.setSpacing(0)
        topic_tracklist_buttons_layout.setContentsMargins(0, 0, 0, 0)
        topic_tracklist_buttons.setLayout(topic_tracklist_buttons_layout)
        self.topic_tracklist_add_button = QPushButton("+")
        self.topic_tracklist_del_button = QPushButton("-")
        self.topic_tracklist_conf_button = QPushButton("confirm")
        self.topic_analyze_button = QPushButton("analyze")
        topic_tracklist_buttons_layout.addWidget(
            self.topic_tracklist_add_button)
        topic_tracklist_buttons_layout.addWidget(
            self.topic_tracklist_del_button)
        topic_tracklist_buttons_layout.addStretch(20)
        topic_tracklist_buttons_layout.addWidget(
            self.topic_tracklist_conf_button)
        topic_tracklist_buttons_layout.addWidget(self.topic_analyze_button)
        topic_tracklist_buttons_layout.setStretch(0, 6)
        topic_tracklist_buttons_layout.setStretch(1, 6)
        topic_tracklist_buttons_layout.setStretch(2, 6)
        topic_tracklist_buttons_layout.setStretch(3, 6)
        self.topic_tracklist_widget = QWidget()
        topic_tracklist_layout = QVBoxLayout()
        self.topic_tracklist_widget.setLayout(topic_tracklist_layout)
        topic_tracklist_layout.addWidget(self.topic_tracklist_label)
        topic_tracklist_layout.addWidget(self.topic_tracklist)
        topic_tracklist_layout.addWidget(topic_tracklist_buttons)

    def init_weibo_tracklist(self):
        self.weibo_tracklist = QListView()
        self.weibo_tracklist_label = QLabel("Weibo Track List")
        self.weibo_tracklist_model = QStandardItemModel()
        self.weibo_tracklist.setModel(self.weibo_tracklist_model)
        weibo_tracklist_buttons = QWidget()
        weibo_tracklist_buttons_layout = QHBoxLayout()
        weibo_tracklist_buttons_layout.setSpacing(0)
        weibo_tracklist_buttons_layout.setContentsMargins(0, 0, 0, 0)
        weibo_tracklist_buttons.setLayout(weibo_tracklist_buttons_layout)
        self.weibo_tracklist_add_button = QPushButton("+")
        self.weibo_tracklist_del_button = QPushButton("-")
        self.weibo_tracklist_conf_button = QPushButton("confirm")
        self.weibo_analyze_button = QPushButton("analyze")
        weibo_tracklist_buttons_layout.addWidget(
            self.weibo_tracklist_add_button)
        weibo_tracklist_buttons_layout.addWidget(
            self.weibo_tracklist_del_button)
        weibo_tracklist_buttons_layout.addStretch(20)
        weibo_tracklist_buttons_layout.addWidget(
            self.weibo_tracklist_conf_button)
        weibo_tracklist_buttons_layout.addWidget(
            self.weibo_analyze_button)
        weibo_tracklist_buttons_layout.setStretch(0, 6)
        weibo_tracklist_buttons_layout.setStretch(1, 6)
        weibo_tracklist_buttons_layout.setStretch(2, 6)
        weibo_tracklist_buttons_layout.setStretch(3, 6)

        self.weibo_sentiment_checkbox = QCheckBox("Comment Sentimnet")
        weibo_analyze_options = QWidget()
        weibo_analyze_options_layout = QHBoxLayout()
        weibo_analyze_options.setLayout(weibo_analyze_options_layout)
        weibo_analyze_options_layout.addWidget(self.weibo_sentiment_checkbox)

        self.weibo_tracklist_widget = QWidget()
        weibo_tracklist_layout = QVBoxLayout()
        self.weibo_tracklist_widget.setLayout(weibo_tracklist_layout)
        weibo_tracklist_layout.addWidget(self.weibo_tracklist_label)
        weibo_tracklist_layout.addWidget(self.weibo_tracklist)
        weibo_tracklist_layout.addWidget(weibo_tracklist_buttons)
        weibo_tracklist_layout.addWidget(weibo_analyze_options)

    def init_triggers(self):
        self.crawl_button.clicked.connect(self.crawl_button_clicked)
        self.topic_tracklist_add_button.clicked.connect(
            self.topic_add_button_clicked)
        self.topic_tracklist_del_button.clicked.connect(
            self.topic_del_button_clicked)
        self.topic_tracklist_conf_button.clicked.connect(
            self.topic_conf_button_clicked)
        self.topic_analyze_button.clicked.connect(
            self.topic_analyze_button_clicked)
        self.weibo_tracklist_add_button.clicked.connect(
            self.weibo_add_button_clicked)
        self.weibo_tracklist_del_button.clicked.connect(
            self.weibo_del_button_clicked)
        self.weibo_tracklist_conf_button.clicked.connect(
            self.weibo_conf_button_clicked)
        self.weibo_analyze_button.clicked.connect(
            self.weibo_analyze_button_clicked)

    @pyqtSlot()
    def crawl_button_clicked(self):
        rule_text = self.rule_edit.toPlainText().strip()
        if rule_text:
            try:
                rule = eval(rule_text)
                assert isinstance(rule, ReRule)
            except Exception as ex:
                QMessageBox.information(
                    self, "Invalid Rule Syntax", '\n'.join(ex.args))
                return
            self.automaton.set_rule(rule)
        if self.scheduler_status:
            self.log("Restart Crawlers", "INFO")
        else:
            self.scheduler_status = True
            self.crawl_button.setText("Restart Crawler")
            self.log("Start Crawlers", "INFO")
        self.scheduler.terminate()
        self.scheduler.wait()
        self.scheduler.set_weibo_handler(self.handle_weibo)
        self.scheduler.set_topic_handler(self.handle_topic)
        self.scheduler.start()

    @pyqtSlot()
    def topic_analyze_button_clicked(self):
        indexes = self.topic_tracklist.selectedIndexes()
        if not indexes:
            return
        index = indexes[0]
        topic_name = self.topic_tracklist_model.item(index.row()).text()

        if not topic_name:
            return
        topic = self.analyzer.get_topic(topic_name)
        if not topic:
            return
        topic_heat = self.analyzer.get_topic_heat(topic)
        self.fig.clear()
        ax = self.fig.add_subplot(111)
        times = list(map(lambda x: x[0], topic_heat))
        heats = list(map(lambda x: x[1], topic_heat))
        ax.plot(times, heats)
        # ax.set_title(topic_name, fontproperties=self.gfont)
        self.fig.autofmt_xdate()
        self.canvas.draw()

    @pyqtSlot()
    def weibo_analyze_button_clicked(self):
        indexes = self.weibo_tracklist.selectedIndexes()
        if not indexes:
            return
        index = indexes[0]
        weibo_id = self.weibo_tracklist_model.item(index.row()).text()
        if not weibo_id:
            return
        weibo_id = int(weibo_id)

        weibo = self.analyzer.get_weibo(weibo_id)
        if not weibo:
            return
        weibo_heat = self.analyzer.get_weibo_heat(weibo)
        self.fig.clear()
        ax = self.fig.add_subplot(111)
        times = list(map(lambda x: x[0], weibo_heat))
        heats = list(map(lambda x: x[1], weibo_heat))
        ax.plot(times, heats)
        topics = self.analyzer.get_weibo_topics(weibo_id)
        sentiment = self.analyzer.get_sentiment(weibo['text'])
        # ax.set_title('#'.join(topics) + "--Sentiment: {:.3f}".format(sentiment), fontproperties=self.gfont)
        self.fig.autofmt_xdate()
        self.canvas.draw()

    @pyqtSlot()
    def topic_add_button_clicked(self):
        item = QStandardItem("")
        self.topic_tracklist_model.appendRow(item)
        self.topic_tracklist.setCurrentIndex(item.index())

    @pyqtSlot()
    def topic_del_button_clicked(self):
        index = self.topic_tracklist.selectedIndexes()[0]
        self.topic_tracklist_model.removeRow(index.row())
        if self.topic_tracklist_model.rowCount():
            self.topic_tracklist.setCurrentIndex(
                self.topic_tracklist_model.index(0, 0))

    @pyqtSlot()
    def topic_conf_button_clicked(self):
        topics = [self.topic_tracklist_model.item(idx).text() for idx in range(
            self.topic_tracklist_model.rowCount())]
        topic_tracklist = ','.join(topics)
        self.scheduler.config.set(
            "TopicSpider", "topic_tracklist", topic_tracklist)
        with open("config.ini", "w") as fp:
            self.scheduler.config.write(fp)

    @pyqtSlot()
    def weibo_add_button_clicked(self):
        item = QStandardItem("")
        self.weibo_tracklist_model.appendRow(item)
        self.weibo_tracklist.setCurrentIndex(item.index())

    @pyqtSlot()
    def weibo_del_button_clicked(self):
        index = self.weibo_tracklist.selectedIndexes()[0]
        self.weibo_tracklist_model.removeRow(index.row())
        if self.weibo_tracklist_model.rowCount():
            self.weibo_tracklist.setCurrentIndex(
                self.weibo_tracklist_model.index(0, 0))

    @pyqtSlot()
    def weibo_conf_button_clicked(self):
        weibo_ids = [self.weibo_tracklist_model.item(idx).text() for idx in range(
            self.weibo_tracklist_model.rowCount())]
        weibo_tracklist = ','.join(weibo_ids)
        self.scheduler.config.set(
            "WeiboSpider", "weibo_id_tracklist", weibo_tracklist)
        with open("config.ini", "w") as fp:
            self.scheduler.config.write(fp)

    def handle_weibo(self, weibo):
        self.dbconn.process_weibo(weibo)
        if self.automaton.match(weibo.text):
            msg = "Matched Weibo {}: {}".format(
                weibo.weibo_id, repr(weibo.text))
            self.log(msg, "WARNING")
            self.handle_matched_weibo(weibo)
        else:
            msg = "Process Weibo {}".format(weibo.weibo_id)
            self.log(msg, "NORMAL")

    def handle_matched_weibo(self, weibo):
        reason = '匹配敏感关键字规则\n' + self.rule_edit.toPlainText()
        if self.accuser.accuse_weibo(weibo, reason):
            msg = "Accuse weibo {}".format(weibo.weibo_id)
            self.log(msg, "INFO")

    def handle_topic(self, topic):
        self.dbconn.process_topic(topic)
        msg = "Process Topic {}".format(repr(topic.name))
        self.log(msg, "NORMAL")

    def log(self, msg, status_str="INFO"):
        time_str = datetime.now().strftime("%Y-%m-%d-%H:%M:%S")
        self.log_model.appendRow([
            QStandardItem(status_str),
            QStandardItem(time_str),
            QStandardItem(msg)
        ])
