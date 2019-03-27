from django import forms
from django.core.exceptions import ValidationError

from .models import Payment


class PayForm(forms.ModelForm):
    class Meta:
        model = Payment
        fields = ["SA", "OSN", "BA", "DA", "AM"]

