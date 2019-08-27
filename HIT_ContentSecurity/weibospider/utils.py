"""Utils"""
from datetime import datetime, timedelta
from selenium import webdriver


def get_browser():
    '''Get configured browser driver'''
    profile = webdriver.FirefoxProfile()
    profile.set_preference('browser.migration.version', 9001)
    profile.set_preference('permissions.default.image', 2)
    browser = webdriver.Firefox(profile)
    return browser


def process_time(time_str):
    if "刚刚" in time_str:
        release_time = datetime.now()
    elif time_str.startswith("昨天"):
        hour = int(time_str[3:5])
        minute = int(time_str[6:])
        release_time = datetime.now() - timedelta(days=1)
        release_time = release_time.replace(
            hour=hour, minute=minute, second=0, microsecond=0
        )
    elif time_str.endswith("秒前"):
        seconds = time_str[:time_str.find("秒前")]
        delta = timedelta(seconds=int(seconds))
        release_time = (datetime.now() - delta)
    elif time_str.endswith("分钟前"):
        minutes = time_str[:time_str.find("分钟前")]
        delta = timedelta(minutes=int(minutes))
        release_time = (datetime.now() - delta)
    elif time_str.endswith("小时前"):
        hours = time_str[:time_str.find("小时前")]
        delta = timedelta(hours=int(hours))
        release_time = (datetime.now() - delta)
    elif '-' in time_str and ':' in time_str:
        time_str = time_str.strip()
        date_str, time_str = time_str.split(' ')
        split_idx = date_str.find('-')
        month = int(date_str[:split_idx].strip())
        day = int(date_str[split_idx + 1:].strip())
        split_idx = time_str.find(':')
        hour = int(time_str[:split_idx])
        minute = int(time_str[split_idx+1:])
        release_time = datetime.now().replace(
            month=month, day=day, hour=hour, minute=minute, second=0, microsecond=0
        )
    else:
        split_idx = time_str.find('-')
        month = int(time_str[:split_idx].strip())
        day = int(time_str[split_idx + 1:].strip())
        release_time = datetime.now().replace(
            month=month, day=day, hour=0, minute=0, second=0, microsecond=0
        )
    return release_time


def process_num(num_str):
    try:
        return int(num_str)
    except ValueError:
        if num_str.endswith("万"):
            return int(float(num_str[:num_str.rfind("万")]) * 10000)
        elif num_str.endswith("亿"):
            return int(float(num_str[:num_str.rfind("万")]) * 100000000)
        else:
            assert False
