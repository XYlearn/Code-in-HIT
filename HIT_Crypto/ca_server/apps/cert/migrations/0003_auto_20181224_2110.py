# -*- coding: utf-8 -*-
# Generated by Django 1.11.17 on 2018-12-24 21:10
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('cert', '0002_subject_valid'),
    ]

    operations = [
        migrations.AlterField(
            model_name='certkey',
            name='privkey',
            field=models.CharField(max_length=4096, verbose_name=b'Privkey PEM'),
        ),
    ]
