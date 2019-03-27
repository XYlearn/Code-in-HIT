from django.contrib import admin
from django.contrib.auth.models import User
from django.db import models

class Payment(models.Model):
    SA = models.CharField("Shop Account", max_length=0x100)
    OSN = models.IntegerField("Order Serial Number")
    BA = models.CharField("Bank Account", max_length=0x100)
    DA = models.CharField("Dest Account", max_length=0x100)
    AM = models.IntegerField("Total Amount")
