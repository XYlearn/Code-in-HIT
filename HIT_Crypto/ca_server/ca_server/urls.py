"""ca_server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
"""
# Django imports
from django.conf.urls import include, url
from django.contrib import admin
from apps.cert import views as cert_views
from apps.authentication import views as auth_views
from apps.user import views as user_views

urlpatterns = [
    # Examples:
    # url(r'^blog/', include('blog.urls', namespace='blog')),

    # provide the most basic login/logout functionality
    # url(r'^login/$', auth_views.login,
    #     {'template_name': 'core/login.html'}, name='core_login'),
    # url(r'^logout/$', auth_views.logout, name='core_logout'),

    # authenticate interface
    url(r'^$', auth_views.signin, name='signin'),
    url(r'^auth/signup/', auth_views.signup, name='signup'),
    url(r'^auth/signin/', auth_views.signin, name='signin'),
    url(r'^auth/signout/', auth_views.signout, name='signout'),
    url(r'^auth/reset/', auth_views.reset, name="reset"),
    url(r'^auth/reset_confirm/(?P<uidb64>[0-9A-Za-z_\-]+)/(?P<token>[0-9A-Za-z]{1,13}-[0-9A-Za-z]{1,20})/$', auth_views.reset_confirm, name='reset_confirm'),
    url(r'^auth/success/$', auth_views.success, name='success'),

    # certification query
    url(r'^cert/', cert_views.query_cert),

    # enable the admin interface
    url(r'^admin/', admin.site.urls),
    url(r'^signout/', auth_views.signout),
    
    # user home
    url(r'^user/$', user_views.user_home, name='home'),
    url(r'^user/bind/', user_views.bind_subject, name='bind'),
    url(r'^user/cancelbind/', user_views.cancel_bind, name='cancelbind'),
    url(r'^user/view/', user_views.view_cert, name='view'),
    url(r'^user/create/', user_views.create_cert, name='create'),
    url(r'^user/recover/', user_views.recover_key, name='recover'),
    url(r'^user/cancel/', user_views.cancel_cert, name='cancel'),
    url(r'^user/update/', user_views.update_cert, name='update'),
]
