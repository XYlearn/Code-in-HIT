from django import forms
from django.core.exceptions import ValidationError

from apps.cert.models import Subject


class SubjectForm(forms.ModelForm):
    class Meta:
        model = Subject
        fields = ['C', 'ST', 'L', 'O', 'OU', 'CN', 'emailAddress']
