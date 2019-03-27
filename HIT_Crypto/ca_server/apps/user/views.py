import hashlib

from django.contrib.auth.decorators import login_required
from django.http import HttpResponse, HttpResponseBadRequest, HttpResponseForbidden
from django.shortcuts import render_to_response, redirect, get_object_or_404, render
from django.contrib import messages

from caapi import caapi

from apps.cert import certgen
from apps.cert import load_ca_cert, load_ca_privkey, ca_enc_data, ca_dec_data
from apps.cert.forms import SubjectForm
from apps.cert.models import Subject, CertKey
from apps.cert import load_ca_cert_pem, load_ca_privkey_pem, load_ca_subject_json

from .decorators import confirm_password

from OpenSSL import crypto


@login_required
def bind_subject(request):
    if not request.user.is_authenticated():
        return HttpResponseForbidden("Please Login First")

    if request.method == 'POST':
        form = SubjectForm(request.POST)
        if Subject.objects.filter(user=request.user).exists():
            messages.add_message(
                request, message.ERROR, "You have sent a binding request. Please wait for approval.")
        if not form.is_valid():
            messages.add_message(request, messages.ERROR,
                                 "Invalid Subject Form")
            return render(request, "user/bindsubj.html", context={'form': form})
        name = {
            'C': request.POST.get('C'),
            'ST': request.POST.get('ST'),
            'L': request.POST.get('L'),
            'O': request.POST.get('O'),
            'OU': request.POST.get('OU'),
            'CN': request.POST.get('CN'),
            'emailAddress': request.POST.get('emailAddress')
        }
        try:
            subject = certgen.create_subject(**name)
            der = subject.der().encode('hex')
        except:
            messages.add_message(request, messages.ERROR,
                                 "Invalid field detected")
            return render(request, "user/bindsubj.html")
        if Subject.objects.filter(der=der).exists():
            messages.add_message(request, messages.ERROR,
                                 "Subject already exists. Please contact the admin for help")
            return render(request, "user/bindsubj.html",
                          context={'form': SubjectForm()})
        subject = form.save(commit=False)
        subject.user = request.user
        subject.der = der
        subject.valid = False
        subject.save()
        messages.add_message(request, messages.SUCCESS,
                             "Bind Request has sent. Please wait for approval.")
        return redirect('/')
    else:
        if Subject.objects.filter(user=request.user).exists():
            subject = Subject.objects.get(user=request.user)
            return render(request, "user/viewsubj.html",
                          context={'subj': subject, 'form': SubjectForm()})
        else:
            return render(request, "user/bindsubj.html",
                          context={'form': SubjectForm()})


@login_required
def cancel_bind(request):
    if not request.user.is_authenticated():
        return HttpResponseForbidden("Please Login First")

    if request.method == 'GET':
        return render(request, "user/")
    else:
        if Subject.objects.filter(user=request.user).exists():
            subject = Subject.objects.get(user=request.user)
            subject.delete()
            messages.add_message(request, messages.SUCCESS, "Cancel Success")
            return render(request, "user/bindsubj.html",
                          context={'form': SubjectForm()})
        else:
            messages.add_message(request, messages.ERROR,
                                 "No subject binding request")


@login_required
def user_home(request):
    return render(request, "user/home.html")


@login_required
def create_cert(request):
    if CertKey.objects.filter(user=request.user).exists():
        messages.add_message(request, messages.ERROR,
                             "You already have the cert")
        return redirect('/user/view')
    else:
        try:
            subject = Subject.objects.get(user=request.user, valid=True)
        except:
            messages.add_message(request, messages.ERROR, "Bind Subject First")
            return redirect('/')
        name = {
            'C': subject.C,
            'ST': subject.ST,
            'L': subject.L,
            'O': subject.O,
            'OU': subject.OU,
            'CN': subject.CN,
            'emailAddress': subject.emailAddress
        }
        subject = certgen.create_subject(**name)
        pkey = certgen.generate_pkey()
        privkey = crypto.dump_privatekey(crypto.FILETYPE_PEM, pkey)
        req = certgen.create_cert_req(pkey, **name)
        issuer_cert = load_ca_cert()
        issuer_key = load_ca_privkey()
        validity_period = (0, 1 * 365 * 24 * 60 * 60)
        # privkey should be encrypted
        stored_privkey = ca_enc_data(privkey)
        cert_model = CertKey.objects.create(
            user=request.user, cert='', privkey=stored_privkey)
        cert_model.save()
        serial = cert_model.serial
        cert = certgen.create_cert(
            req, issuer_cert, issuer_key, serial, validity_period)
        cert_model.cert = crypto.dump_certificate(crypto.FILETYPE_PEM, cert)
        cert_model.save()
        messages.add_message(request, messages.SUCCESS, "Create Cert Succeed")
        return redirect('/')


@login_required
def view_cert(request):
    try:
        certkey = CertKey.objects.get(user=request.user)
    except:
        messages.add_message(request, messages.ERROR,
                             "Please Create Certificate First")
        return redirect('/')
    try:
        cert = crypto.load_certificate(crypto.FILETYPE_PEM, cert)
        if cert.has_expired():
            messages.add_message(request, messages.ERROR,
                                 "Your Certificate has expired, Please update it")
    except:
        pass
    if request.method == 'GET':
        cert = crypto.load_certificate(crypto.FILETYPE_PEM, certkey.cert)
        return render(request, "user/viewcert.html",
                      context={
                          'cert_info': crypto.dump_certificate(crypto.FILETYPE_TEXT, cert),
                          'cert_pem': crypto.dump_certificate(crypto.FILETYPE_PEM, cert)
                      })
    else:
        cert = certkey.cert
        filename = hashlib.md5(cert).hexdigest() + ".cert"
        content = cert
        response = HttpResponse(content, content_type='text/plain')
        response['Content-Disposition'] = 'attachment; filename={0}'.format(
            filename)
        return response


@login_required
@confirm_password
def cancel_cert(request):
    try:
        certkey = CertKey.objects.get(user=request.user)
    except:
        messages.add_message(request, messages.ERROR,
                             "Please Create Certificate First")
        return redirect('/')
    certkey.delete()
    messages.add_message(request, messages.SUCCESS,
                         "Cert is removed from database. But it's still valid before expired")
    return render(request, "user/home.html")


@login_required
@confirm_password
def update_cert(request):
    try:
        certkey = CertKey.objects.get(user=request.user)
    except:
        messages.add_message(request, messages.ERROR,
                             "Please Create Certificate First")
        return redirect('/')
    certkey.delete()
    create_cert(request)
    # clean messages
    storage = messages.get_messages(request)
    storage.used = True
    messages.add_message(request, messages.SUCCESS,
                         "Cert Updated. The original cert is removed from database. But it's still valid before expired")
    return render(request, "user/home.html")


@login_required
@confirm_password
def recover_key(request):
    try:
        certkey = CertKey.objects.get(user=request.user)
    except:
        messages.add_message(request, messages.ERROR,
                             "Please Create Certificate First")
        return redirect('/')
    stored_key = certkey.privkey
    # decrypted the key
    key = ca_dec_data(stored_key)
    filename = hashlib.md5(key).hexdigest() + ".priv"
    content = key
    response = HttpResponse(content, content_type='text/plain')
    response['Content-Disposition'] = 'attachment; filename={0}'.format(
        filename)
    return response
