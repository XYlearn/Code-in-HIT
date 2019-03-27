import caapi
import json
import hashlib
import os

from django.contrib.auth.decorators import login_required
from django.http import (
    HttpResponse, HttpResponseBadRequest, HttpResponseForbidden, HttpResponseRedirect)
from django.shortcuts import render_to_response, redirect, get_object_or_404, render
from django.contrib import messages
from django.conf import settings

from .forms import PayForm


def pay(request):
    if request.method == "POST":
        form = PayForm(request.POST)
        if not form.is_valid():
            messages.add_message(request, messages.ERROR, "Fail to pay")
            return redirect('/')
        oi = {
            "SA": str(request.POST['SA']),
            "OSN": str(request.POST['OSN']),
            "AM": str(request.POST['AM']),
            "DA": str(request.POST['DA']),
        }
        pi = {
            "BA": str(request.POST['BA']),
            "DA": str(request.POST['DA']),
            "AM": str(request.POST['AM']),
        }
        pimd = hashlib.sha256(json.dumps(pi)).hexdigest()
        oimd = hashlib.sha256(json.dumps(oi)).hexdigest()
        pomd = hashlib.sha256(pimd+oimd).hexdigest()
        privkey_path = os.path.join(settings.PROJECT_ROOT, "data/privkey")
        privkey_pem = caapi.read_file(privkey_path)
        privkey = caapi.load_privkey(privkey_pem)
        pomd_sig = caapi.sign(privkey, pomd).encode('hex')
        url = 'http://149.28.137.247:80/fastpay/' + oi['OSN'] + '/' + pi["DA"] + '/' + pi['AM'] + '/' + oimd + '/' + pomd_sig + '/'
        return redirect(url)


    else:
        form = PayForm()
        return render(request, "client.html",
                      context={'form': form})
        
