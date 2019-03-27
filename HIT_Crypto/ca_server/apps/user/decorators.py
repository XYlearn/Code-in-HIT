import datetime
from functools import wraps

from django.views.generic.edit import UpdateView
from django.utils import timezone
from .forms import ConfirmPasswordForm


class ConfirmPasswordView(UpdateView):
    form_class = ConfirmPasswordForm
    template_name = 'user/confirm_password.html'

    def get_object(self):
        return self.request.user

    def get_success_url(self):
        return self.request.get_full_path()

def confirm_password(view_func):
    @wraps(view_func)
    def _wrapped_view(request, *args, **kwargs):
        last_login = request.user.last_login
        timespan = last_login + datetime.timedelta(seconds=10)
        if timezone.now() > timespan:
            return ConfirmPasswordView.as_view()(request, *args, **kwargs)
        return view_func(request, *args, **kwargs)
    return _wrapped_view
