"""
Analyze
"""
import re

from pymongo import MongoClient
from matplotlib import pyplot as plt
from datetime import datetime
from snownlp import SnowNLP

class Analyzer:
    def __init__(self, dbconn):
        self.weibos = dbconn.weibos
        self.topics = dbconn.topics

    def get_topic(self, topic_name):
        topic = self.topics.find_one({"name": topic_name})
        if not topic:
            return None
        return topic

    def get_weibo(self, weibo_id):
        weibo = self.weibos.find_one({"weibo_id": weibo_id})
        if not weibo:
            return None
        return weibo

    def get_weibo_topics(self, weibo_id):
        weibo = self.weibos.find_one({"weibo_id": weibo_id})
        topics = []
        if not weibo:
            return []
        text = weibo["text"]
        return list(map(lambda s: s.replace('#', ''), re.findall(r"#.+?#", text)))

    def get_sentiment(self, text):
        return SnowNLP(text).sentiments

    def get_topic_heat(self, topic):
        def calculate_heat(info):
            return int(info['read_num'] + info['discuss_num'] * 2000)
        time_heat = list(
            map(lambda x: (
                x[0], calculate_heat(x[1])),
                topic['info'].items()
                )
        )
        time_heat.sort(key=lambda x: int(x[0]))
        time_heat = list(
            map(lambda x: (datetime.fromtimestamp(int(x[0])), x[1]), time_heat)
        )
        return time_heat

    def get_weibo_heat(self, weibo):
        def calculate_heat(info):
            return int(info['like_num'] + info['comment_num'] * 5 + info['forward_num'] * 6)
        time_heat = list(
            map(lambda x: (
                x[0], calculate_heat(x[1])),
                weibo['heat'].items()
                )
        )
        time_heat.sort(key=lambda x: int(x[0]))
        time_heat = list(
            map(lambda x: (datetime.fromtimestamp(int(x[0])), x[1]), time_heat)
        )
        return time_heat

