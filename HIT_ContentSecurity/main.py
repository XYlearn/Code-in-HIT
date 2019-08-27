"""main"""

import sys

from weibospider import DBConn, Scheduler
from gui import App

from PyQt5.QtWidgets import QApplication

if __name__ == '__main__':
    # dbconn = DBConn()
    # spider = WeiboSpider()
    # spider.run()
    # scheduler = Scheduler(dbconn)
    # scheduler.run()
    app = QApplication(sys.argv)
    ex = App()
    sys.exit(app.exec_())