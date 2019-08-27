'''mongodb connection'''
from datetime import datetime
from pymongo import MongoClient


class DBConn(object):
    """MongoDB Connection to process WeiboData"""

    def __init__(self):
        client = MongoClient("localhost", 27017)
        self.databse = client["Weibo"]
        self.weibos = self.databse["Weibo"]
        self.topics = self.databse["Topic"]

    def process_weibo(self, item):
        '''Insert item to databse'''
        timestamp = str(datetime.now().timestamp())
        timestamp = timestamp[:timestamp.find('.')]
        record = self.weibos.find_one({"weibo_id": item.weibo_id})
        if record:
            self.weibos.update_one(
                {"weibo_id": item.weibo_id},
                {"$set": {
                    "text": item.text,
                    "heat.{}".format(timestamp): {
                        "forward_num": item.forward_num,
                        "like_num": item.like_num,
                        "comment_num": item.comment_num
                    }
                }}
            )
        else:
            new_record = {
                "weibo_id": item.weibo_id,
                "user_id": item.user_id,
                "release_time": item.release_time,
                "text": item.text,
                "heat": {
                    timestamp: {
                        "forward_num": item.forward_num,
                        "like_num": item.like_num,
                        "comment_num": item.comment_num
                    }
                },
                "comments": item.comments
            }
            self.weibos.insert(new_record)

    def process_topic(self, item):
        """Insert topic to databse"""
        timestamp = str(datetime.now().timestamp())
        timestamp = timestamp[:timestamp.find('.')]
        record = self.topics.find_one({"name": item.name})
        if record:
            self.topics.update_one(
                {"name": item.name},
                {"$set": {
                    "info.{}".format(timestamp): {
                        "read_num": item.read_num,
                        "discuss_num": item.discuss_num
                    }}}
            )
        else:
            new_record = {
                "name": item.name,
                "info": {
                    timestamp: {
                        "read_num": item.read_num,
                        "discuss_num": item.discuss_num
                    }
                }
            }
            self.topics.insert(new_record)
