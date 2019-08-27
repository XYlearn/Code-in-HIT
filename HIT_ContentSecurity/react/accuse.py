import smtplib
from email.mime.text import MIMEText


class Accuser:
    def __init__(self):
        self.mail_host = 'smtp.163.com'
        self.mail_user = 'xylear'
        self.mail_pass = 'coding520'
        self.sender = 'xylear@163.com'
        self.receivers = ['xylear@163.com']
        self.stmp = smtplib.SMTP()

    def connect(self):
        try:
            self.stmp.connect(self.mail_host, 25)
            self.stmp.login(self.mail_user, self.mail_pass)
        except smtplib.SMTPException:
            return False
        return True

    def accuse_weibo(self, weibo, reason):
        content = "建议审查用户(id为{})于 {} 所发微博(id为{})\n".format(
            weibo.user_id, weibo.release_time, weibo.weibo_id)
        content += "微博内容为:\n"
        content += "-" * 80 + '\n'
        content += weibo.text + '\n'
        content += "-" * 80 + '\n'
        content += "建议原因为：{}\n".format(reason)
        message = MIMEText(content, 'plain', 'utf-8')
        message['Subject'] = "举报微博{}".format(weibo.weibo_id)
        message['From'] = self.sender
        message['To'] = self.receivers[0]

        failed = False
        try:
            self.stmp.sendmail(
                self.sender, self.receivers, message.as_string())
        except smtplib.SMTPException:
            failed = True
            self.connect()
        if failed:
            try:
                self.stmp.sendmail(
                    self.sender, self.receivers, message.as_string())
            except smtplib.SMTPException:
                return False
        return True
