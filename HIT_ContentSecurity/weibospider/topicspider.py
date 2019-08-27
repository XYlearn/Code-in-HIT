"""
A Spider get the search number of topic
"""
from selenium.common.exceptions import NoSuchElementException, WebDriverException, TimeoutException
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC

from .utils import process_num


class TopicInfo:
    """Information of topic"""

    def __init__(self):
        self.name = ""
        self.read_num = -1
        self.discuss_num = -1

    def __str__(self):
        return str(self.__dict__)


class TopicSpider:
    """Topic Spider"""

    def __init__(self, browser):
        self.browser = browser

    def get_topic_info(self, topic_name, handler=None):
        '''get information of topic'''
        url = 'https://m.weibo.cn/search?containerid=100103type%3D1%26q%3D%23{}%23'.format(
            topic_name)
        self.browser.get(url)
        info_xpath = '//*[@id="app"]/div[1]/div[1]/div[2]/div/div/div[2]/h4[2]/span'
        try:
            WebDriverWait(self.browser, 10).until(
                EC.presence_of_element_located((By.XPATH, info_xpath))
            )
            element = self.browser.find_element_by_xpath(info_xpath)
        except NoSuchElementException:
            self.browser.back()
            return None
        except WebDriverException:
            self.browser.back()
            return None
        info_text = element.text.strip()
        mid = info_text.find('讨论')
        reading_num_str = info_text[2: mid].strip()
        discuss_num_str = info_text[mid + 2:].strip()
        topic = TopicInfo()
        topic.name = topic_name
        topic.read_num = process_num(reading_num_str)
        topic.discuss_num = process_num(discuss_num_str)
        self.browser.back()
        if handler:
            handler(topic)
        return topic

    def get_top_topics(self, handler=None, num=50):
        '''get top topics in hot search board'''
        url = 'https://m.weibo.cn/p/index?containerid=106003' + \
            'type%3D25%26t%3D3%26disable_hot%3D1%26filter_type%3D' + \
            'realtimehot&title=%E5%BE%AE%E5%8D%9A%E7%83%AD%E6%90%9C' + \
            '&extparam=filter_type%3Drealtimehot%26mi_cid%3D100103%26' + \
            'pos%3D0_0%26c_type%3D30%26display_time%3D1558335354&luicode=10000011&lfid=231583'
        self.browser.get(url)
        topic_names = []
        for idx in range(num):
            idx = idx + 1
            topic_xpath = ('//*[@id="app"]/div[1]/div[1]/div[3]/div/div/div[{}]' + \
                           '/div/div/div/div/span[2]/span[1]').format(idx)
            try:
                WebDriverWait(self.browser, 10).until(
                    EC.presence_of_element_located((By.XPATH, topic_xpath))
                )
            except TimeoutException:
                pass
            element = self.browser.find_element_by_xpath(topic_xpath)
            topic_name = element.text.strip()
            topic_names.append(topic_name)
        topics = []
        for topic_name in topic_names:
            topic = self.get_topic_info(topic_name, handler)
            if topic:
                topics.append(topic)
        return topics
