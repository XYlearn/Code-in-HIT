"""
Weibo Spider
"""
import time
import logging

from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import (
    StaleElementReferenceException,
    NoSuchElementException,
    TimeoutException
)

from .utils import process_num, process_time

logger = logging.getLogger("spider")


class WeiboData:
    """Weibo data"""

    def __init__(self):
        self.weibo_id = -1
        self.user_id = -1
        self.release_time = ""
        self.text = ""
        self.forward_num = -1
        self.like_num = -1
        self.comment_num = -1
        self.comments = []

    def __str__(self):
        return str(self.__dict__)


class WeiboSpider:
    """Weibo Spider for weibo content"""

    def __init__(self, browser):
        self.browser = browser
        if self.browser.current_url != 'https://m.weibo.cn/':
            self.browser.get("https://m.weibo.cn/")
        self.current_category = 1

    def refresh_category(self, category):
        """refresh category to get new content"""
        # close the alert
        try:
            cancel_alert_xpath = \
                '/html/body/div/div[2]/div[1]/div[2]/footer/div[1]/a'
            WebDriverWait(self.browser, 1).until(EC.alert_is_present())
            alert = self.browser.switch_to.alert
            alert.accept()
            # self.browser.execute_script("arguments[0].click();", element)
        # except NoSuchElementException:
        #     pass
        except TimeoutException:
            pass
        if self.browser.current_url != "https://m.weibo.cn/":
            self.browser.get("https://m.weibo.cn/")
        category_xpath = \
            '//*[@id="app"]/div[1]/div[1]/div[2]/div/div[1]/div/div/ul/li[{category}]'.format(
                category=category)
        try:
            WebDriverWait(self.browser, 10).until(
                EC.presence_of_element_located((By.XPATH, category_xpath)))
            element = self.browser.find_element_by_xpath(category_xpath)
            self.browser.execute_script("arguments[0].click();", element)
            WebDriverWait(self.browser, 10).until(
                lambda x: "display: none;" in self.browser.find_element_by_xpath(
                    '/html/body/div/div[1]/div[2]/div[3]').get_attribute('style'))
        except StaleElementReferenceException:
            return False
        except TimeoutException:
            return False
        self.current_category = category

    def get_weibos(self, size, interval=0, handler=None, with_comment=False):
        weibos = []
        idx = 1
        while idx <= size:
            weibo = self._get_weibo_at(idx, handler, with_comment)
            if not weibo:
                self.refresh_category(self.current_category)
                size -= idx - 1
                idx = 1
                continue
            weibos.append(weibo)
            idx += 1
            time.sleep(interval)
        return weibos

    def get_weibo_by_id(self, weibo_id, handler=None, with_comment=False):
        self.browser.get('https://m.weibo.cn/detail/{}'.format(weibo_id))
        weibo = self._get_weibo_data(with_comment=with_comment)
        self.browser.back()
        if handler:
            handler(weibo)
        return weibo

    def _get_weibo_at(self, idx, handler=None, with_comment=False):
        if not self._enter_weibo_detail(idx):
            return None
        weibo = self._get_weibo_data(with_comment=with_comment)
        self.browser.back()
        if handler:
            handler(weibo)
        return weibo

    def _get_weibo_data(self, with_comment=False):
        weibo = WeiboData()
        weibo.user_id = self._get_weibo_user_id()
        weibo.text = self._get_weibo_text()
        weibo.release_time = self._get_weibo_release_time()
        weibo.like_num = self._get_weibo_like_num()
        weibo.comment_num = self._get_weibo_comment_num()
        weibo.forward_num = self._get_weibo_forward_num()
        weibo.weibo_id = self._get_weibo_id()
        if with_comment:
            weibo.comments = self._get_weibo_comments(-1)
        # print(self._get_weibo_comments(20))
        return weibo

    def _enter_weibo_detail(self, idx):
        weibo_xpath = \
            '/html/body/div/div[1]/div[2]/div[2]/div[{}]/div/div/article/div/div/div[1]'.format(
                idx)
        try:
            element = self.browser.find_element_by_xpath(weibo_xpath)
        except NoSuchElementException:
            return False
        self.browser.execute_script("arguments[0].click();", element)
        return True

    def _get_weibo_text(self):
        text_xpath = '/html/body/div/div[1]/div/div[2]/div/article/div/div/div[1]'
        return self._get_element_text_by_xpath(text_xpath)

    def _get_weibo_release_time(self):
        time_xpath = '/html/body/div/div[1]/div/div[2]/div/div/header/div[2]/div/h4/span[1]'
        time_str = self._get_element_text_by_xpath(time_xpath)
        return process_time(time_str)

    def _get_weibo_id(self):
        url = self.browser.current_url
        needle = 'detail/'
        start = url.find(needle) + len(needle)
        end = url.rfind("#")
        if end > 0:
            return int(url[start:end], 10)
        else:
            return int(url[start:], 10)

    def _get_weibo_user_id(self):
        profile_xpath = '/html/body/div/div[1]/div/div[2]/div/div/header/div[2]/div/a'
        try:
            WebDriverWait(self.browser, 10).until(
                EC.presence_of_element_located((By.XPATH, profile_xpath)))
        except TimeoutException:
            pass
        profile = self.browser.find_element_by_xpath(
            profile_xpath).get_attribute('href')
        needle = 'profile/'
        start = profile.find(needle) + len(needle)
        return int(profile[start:], 10)

    def _get_weibo_like_num(self):
        like_xpath = '/html/body/div/div[1]/div/div[3]/div[1]/div[3]/i[2]'
        like_num = process_num(
            self._get_element_text_by_xpath(like_xpath))
        return like_num

    def _get_weibo_comment_num(self):
        comment_xpath = '/html/body/div/div[1]/div/div[3]/div[1]/div[2]/i[2]'
        comment_num = process_num(
            self._get_element_text_by_xpath(comment_xpath))
        return comment_num

    def _get_weibo_forward_num(self):
        forward_xpath = '/html/body/div/div[1]/div/div[3]/div[1]/div[1]/i[2]'
        forward_num = process_num(
            self._get_element_text_by_xpath(forward_xpath))
        return forward_num

    def _get_element_text_by_xpath(self, xpath):
        WebDriverWait(self.browser, 10).until(
            EC.presence_of_element_located((By.XPATH, xpath))
        )
        element = self.browser.find_element_by_xpath(xpath)
        return element.text

    def _get_weibo_comments(self, max_num):
        if max_num <= 0:
            max_num = self._get_weibo_comment_num()
        else:
            max_num = min(max_num, self._get_weibo_comment_num())
        ele_template = '//*[@id="app"]/div[1]/div/div[3]/div[2]/div[{}]/div/div/div/div/div[2]/div[1]/div/div/h3'
        while not self._get_element(ele_template.format(max_num)):
            try:
                self.browser.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            except Exception:
                time.sleep(0.2)
        comments = []
        for i in range(max_num):
            comment_ele = self._get_element(ele_template.format(i + 1))
            if comment_ele:
                comments.append(comment_ele.text)
        return comments
    
    def _get_element(self, xpath):
        locator = EC.presence_of_element_located((By.XPATH, xpath))
        try:
            return locator(self.browser)
        except NoSuchElementException:
            return None
