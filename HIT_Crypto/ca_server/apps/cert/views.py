import json
import datetime

from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from .utils import certgen
from .models import Subject, CertKey
from . import load_ca_cert_pem, load_ca_privkey_pem, load_ca_subject_json


@csrf_exempt
def query_cert(request):
    fields = ['C', 'ST', 'L', 'O', 'OU', 'CN', 'emailAddress']
    name = {}
    res = {
        'status': 0,
        'msg': 'Success',
        'count': 0,
        'result': []
    }
    if request.method == 'GET':
        qdict = request.GET.dict()
    else:
        qdict = request.POST.dict()

    if len(qdict) == 0:
        res['count'] = 1
        item = {
            'subject': load_ca_subject_json(),
            'cert': load_ca_cert_pem(),
        }
        res['result'].append(item)
        return HttpResponse(json.dumps(res))

    for key in qdict:
        if key not in fields:
            res['status'] = -1
            res['msg'] = "Invalid Subject Field {}".format(key)
            return HttpResponse(json.dumps(res))

    for field in fields:
        field_value = qdict.get(field)
        if field_value:
            name[field] = field_value

    subject_records = Subject.objects.filter(**name)[:10]
    for record in subject_records:
        subject = {}
        for field in fields:
            subject[field] = getattr(record, field)
        certkeys = CertKey.objects.filter(user=record.user)[:1]
        if len(certkeys) == 0:
            continue
        cert = certkeys[0].cert
        if certgen.crypto.load_certificate(certgen.crypto.FILETYPE_PEM, cert).has_expired():
            continue
        item = {
            'subject': subject,
            'cert': cert
        }
        res['result'].append(item)
        res['count'] += 1
    return HttpResponse(json.dumps(res))
