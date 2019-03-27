import os
import json
import base64
from OpenSSL import crypto
from apps.cert.utils import certgen
from django.conf import settings

from caapi import caapi

ca_cert_path = os.path.join(settings.PROJECT_ROOT, "ca_data/cert")
ca_privkey_path = os.path.join(settings.PROJECT_ROOT, "ca_data/key")
ca_name_path = os.path.join(settings.PROJECT_ROOT, "ca_data/name")

if not os.path.exists(ca_cert_path) \
        or not os.path.exists(ca_privkey_path) \
        or not os.path.exists(ca_name_path):
    pkey = certgen.generate_pkey()
    name = {
        'C': "CN",
        'ST': "Heilongjiang",
        'L': "Harbin",
        'O': "HIT",
        'OU': "CS",
        'CN': "Certification Authority",
        'emailAddress': 'xylearn@qq.com'
    }
    req = certgen.create_cert_req(pkey, **name)
    validity_period = (0, 5 * 365 * 24 * 60 * 60)
    cert = certgen.create_cert(req, req, pkey, 0, validity_period)
    certgen.dump_cert(ca_cert_path, cert)
    certgen.dump_privkey(ca_privkey_path, pkey)
    with open(ca_name_path, "w+") as f:
        f.write(json.dumps(name))


def load_ca_cert_pem():
    with open(ca_cert_path, "r") as f:
        return f.read()


def load_ca_privkey_pem():
    with open(ca_privkey_path, 'r') as f:
        return f.read()


def load_ca_subject_json():
    with open(ca_name_path, 'r') as f:
        return json.load(f)


def load_ca_cert():
    pem = load_ca_cert_pem()
    return crypto.load_certificate(crypto.FILETYPE_PEM, pem)


def load_ca_privkey():
    pem = load_ca_privkey_pem()
    return crypto.load_privatekey(crypto.FILETYPE_PEM, pem)


def load_ca_subject():
    subject = crypto.X509Req().get_subject()
    attrs = load_ca_subject_json()
    for attr, value in attrs.items():
        setattr(subject, attr, value)
    return subject


def ca_enc_data(data):
    key = caapi.Fernet.generate_key()
    fernet = caapi.Fernet(key)
    cert_pem = load_ca_cert_pem()
    pubkey = caapi.load_pubkey_from_cert(cert_pem)
    enc_key = caapi.encrypt(pubkey, key)
    part1 = base64.b64encode(enc_key)
    part2 = fernet.encrypt(data)
    result = "{}|{}|{}{}".format(len(part1), len(part2), part1, part2)
    return result


def ca_dec_data(data):
    try:
        idx = data.find('|')
        size1 = int(data[:idx])
        data = data[idx+1:]
        idx = data.find('|')
        size2 = int(data[:idx])
        data = data[idx+1:]
        if len(data) != size1 + size2:
            return ''
        part1 = data[:size1]
        part2 = data[size1:]
        enc_key = base64.b64decode(part1)

        privkey_pem = load_ca_privkey_pem()
        privkey = caapi.load_privkey(privkey_pem)
        key = caapi.decrypt(privkey, enc_key)
        fernet = caapi.Fernet(key)
        # return part2
        return fernet.decrypt(part2.encode('utf-8'), None)
    except:
        return ""
