"""
schedule spiders
"""
import time
import logging
import traceback

from configparser import ConfigParser
from PyQt5.QtCore import QThread, pyqtSlot
from urllib3.exceptions import HTTPError
from selenium.common.exceptions import InvalidSessionIdException

from .weibospider import WeiboSpider
from .topicspider import TopicSpider
from .utils import get_browser


class Scheduler(QThread):
    """Scheduler to schedule spiders"""

    def __init__(self, dbconn):
        super().__init__()
        self.dbconn = dbconn
        self.browser = get_browser()
        self.weibospider = WeiboSpider(self.browser)
        self.topicspider = TopicSpider(self.browser)
        self.logger = logging.getLogger("weibospider.schedule")
        self.logger.addHandler(logging.FileHandler("spider.log"))
        self.weibo_handler = self._handle_weibo
        self.topic_handler = self._handle_topic
        self.config = ConfigParser()
        self.config.read('config.ini')

    def set_weibo_handler(self, weibo_handler):
        self.weibo_handler = weibo_handler

    def set_topic_handler(self, topic_handler):
        self.topic_handler = topic_handler

    @pyqtSlot()
    def run(self):
        '''Start the spider'''
        self.browser.get("https://m.weibo.cn/")
        while True:
            cfg = self.load_config()
            try:
                for category in cfg['categories']:
                    self.weibospider.refresh_category(category)
                    self.weibospider.get_weibos(
                        cfg['item_each_turn'], cfg['item_interval'], self.weibo_handler)
                for topic in cfg['topic_tracklist']:
                    self.topicspider.get_topic_info(topic, self.topic_handler)
                for weibo_id in cfg['weibo_id_tracklist']:
                    self.weibospider.get_weibo_by_id(
                        weibo_id, self.weibo_handler)
                self.topicspider.get_top_topics(
                    self.topic_handler, num=cfg['topic_num'])
                time.sleep(cfg['turn_interval'])
            except HTTPError:
                pass
            except InvalidSessionIdException:
                pass
            except Exception as ex:
                traceback.print_exc()

    def load_config(self):
        self.config = ConfigParser()
        self.config.read('config.ini')
        cfg = {}
        cfg['turn_interval'] = float(
            self.config['WeiboSpider']['turn_interval'])
        cfg['item_each_turn'] = int(
            self.config['WeiboSpider']['item_each_turn'])
        cfg['item_interval'] = float(
            self.config['WeiboSpider']['item_interval'])
        cfg['categories'] = list(
            map(lambda s: int(s.strip()),
                self.config['WeiboSpider']['categories'].split(','))
        )
        cfg['weibo_id_tracklist'] = list(filter(
            len,
            map(lambda s: s.strip(),
                self.config['WeiboSpider']['weibo_id_tracklist'].split(','))
        ))
        cfg['topic_tracklist'] = list(filter(
            len,
            map(lambda s: s.strip(),
                self.config['TopicSpider']['topic_tracklist'].split(','))
        ))
        cfg['topic_num'] = int(self.config['TopicSpider']['topic_num'])
        return cfg

    # def restart(self):
    #     del self.weibospider
    #     del self.topicspider
    #     self.weibospider = WeiboSpider(self.browser)
    #     self.topicspider = TopicSpider(self.browser)
    #     self.logger.info("Restart schedule")

    def _handle_weibo(self, weibo):
        self.dbconn.process_weibo(weibo)

    def _handle_topic(self, topic):
        self.dbconn.process_topic(topic)
