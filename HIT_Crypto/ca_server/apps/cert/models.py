from django.contrib import admin
from django.contrib.auth.models import User
from django.db import models


class Subject(models.Model):
    C = models.CharField("Country Name", max_length=64)
    ST = models.CharField("State or province name", max_length=64)
    L = models.CharField("Locality name", max_length=64)
    O = models.CharField("Organization name", max_length=64)
    OU = models.CharField("Organizational unit name", max_length=64)
    CN = models.CharField("Common name", max_length=64)
    emailAddress = models.EmailField("Email Address")
    
    der = models.CharField("der", max_length=1024)
    user = models.OneToOneField(User)
    valid = models.BooleanField(default=False)

    def __unicode__(self):
        return 'Subject: ' + '/'.join([self.C, self.ST, self.L, self.O, self.OU, self.CN])

class CertKey(models.Model):
    serial = models.AutoField("Serial", primary_key=True)
    cert = models.CharField('Cert PEM', max_length=1024)
    privkey = models.CharField("Privkey PEM", max_length=4096)
    user = models.OneToOneField(User)
    
    def __unicode__(self):
        return 'CertKey: ' + str(self.serial)

admin.site.register(Subject)
admin.site.register(CertKey)
