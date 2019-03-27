"""ca_server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
"""
# Django imports
from django.conf.urls import include, url
from apps.client.views import pay

urlpatterns = [
    url(r'^$', pay, name='pay')
]
