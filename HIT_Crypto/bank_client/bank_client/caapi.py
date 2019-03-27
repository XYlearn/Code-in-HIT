# -*-coding: utf-8 -*-
"""
Example:
- 获取ca公钥, 保存到ca_cert文件中:
    ca_cert = query_ca_cert()
    write_file("./ca_cert", ca_cert)
- 查询对方公钥，保存到cert文件中:
    # name 需要替换成对方的subject内容
    name = {
            'C': "CN",
            'ST': "Heilongjiang",
            'L': "Harbin",
            'O': "HIT",
            'OU': "IS",
            'CN': "XHY",
            'emailAddress': 'xylearn@qq.com'
    }
    cert = query_one_cert(**name)
    if cert:
        write_file("./cert", cert)
    else:
        print "No such subject" # error handling
- 获取对方公钥后应该验证有效性
    if verify_certificate_chain(cert, [ca_cert]):
        # 有效处理
    else:
        # 无效处理
- 从证书中获取公钥(这个是一个类的对象):
    pubkey = load_pubkey_from_cert(cert)
- 将公钥转化成pem文本格式:
    pubkey_pem = dump_pubkey(pubkey)
- 读取自己的私钥(类对象):
    privkey = load_privkey(read_file("./privkey"))
- 将私钥转换成pem文本格式:
    privkey_pem = dum_privkey(privkey)
- 用自己的私钥签名:
    sig = sign(privkey, "therethere")
- 用公钥验证:
    if not verify(pubkey, "therethere", sig):
        # error handling
- 用公钥加密:
    ct = encrypt(pubkey, "therethere")
- 用私钥解密:
    pt = decrypt(privkey, ct)
- 对称加密:
    key = Fernet.generate_key()
    # key exchange then
    # ...
    fernet = Fernet(key)
    ct = fernet.encrypt("hello world")
    pt = fernet.decrypt(ct)
"""

import requests
import json

from urlparse import urljoin

from OpenSSL import crypto

from cryptography.fernet import Fernet
from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.asymmetric import padding


query_url = 'http://127.0.0.1:8000/cert/'


class CaAPIException(Exception):
    pass


def read_file(path):
    with open(path, 'rb') as f:
        return f.read()


def write_file(path, cont):
    with open(path, "wb+") as f:
        f.write(cont)


def query(**name):
    """query cert from ca
    Example:   
        name = {
            'C': "CN",
            'ST': "Heilongjiang",
            'L': "Harbin",
            'O': "HIT",
            'OU': "CS",
            'CN': "Certification Authority",
            'emailAddress': 'xylearn@qq.com'
        }
        res = query_cert(**name)
    """
    url = query_url
    resp = requests.post(url, name)
    return json.loads(resp.content)


def query_one_cert(**name):
    res = query(**name)
    results = res['result']
    if len(results) == 0:
        return None
    elif len(results) != 1:
        raise CaAPIException("More than one cert")
    else:
        result = results[0]
        cert = bytes(result['cert'])
        return cert


def query_ca_cert():
    return query_one_cert()


def verify_certificate_chain(cert, trusted_certs):
    # Download the certificate from the url and load the certificate
    assert(len(trusted_certs) != 0)
    certificate = x509.load_pem_x509_certificate(cert, default_backend())

    # Create a certificate store and add your trusted certs
    try:
        store = crypto.X509Store()

        # Assuming the certificates are in PEM format in a trusted_certs list
        for _cert in trusted_certs:
            cert_data = _cert
            client_certificate = crypto.load_certificate(
                crypto.FILETYPE_PEM, cert_data)
            store.add_cert(client_certificate)
        # Create a certificate context using the store and the downloaded certificate
        store_ctx = crypto.X509StoreContext(store, certificate)

        # Verify the certificate, returns None if it can validate the certificate
        store_ctx.verify_certificate()

        return True
    except Exception as e:
        print(e)
        return False


def verify_certificate_subject(cert, **name):
    cert = crypto.load_certificate(crypto.FILETYPE_PEM, cert)
    subject = cert.get_subject()
    for key, val in name.items():
        if getattr(subject, key) != val:
            return False
    return True


def load_privkey(pem):
    return serialization.load_pem_private_key(pem, None, default_backend())


def load_pubkey(pem):
    return serialization.load_pem_public_key(pem, default_backend())


def dump_pubkey(pubkey):
    return pubkey.public_bytes(
        serialization.Encoding.PEM, 
        serialization.PublicFormat.SubjectPublicKeyInfo
    )


def dump_privkey(privkey):
    return privkey.private_bytes(
        serialization.Encoding.PEM, 
        serialization.PrivateFormat.TraditionalOpenSSL,
        serialization.NoEncryption()
    )


def load_certificate(pem, backend='cryptography'):
    if backend == 'cryptography':
        return x509.load_pem_x509_certificate(pem, default_backend())
    else:
        return crypto.load_certificate(crypto.FILETYPE_PEM, pem)


def load_pubkey_from_cert(pem):
    cert = load_certificate(pem, backend='cryptography')
    return cert.public_key()


def encrypt(pubkey, pt):
    ct = pubkey.encrypt(
        pt,
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )
    return ct


def decrypt(privkey, ct):
    pt = privkey.decrypt(
        ct,
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )
    return pt


def sign(privkey, msg):
    signature = privkey.sign(
        msg,
        padding.PSS(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            salt_length=padding.PSS.MAX_LENGTH
        ),
        algorithm=hashes.SHA256()
    ),
    return signature


def verify(pubkey, msg, signature):
    try:
        pubkey.verify(
            signature,
            msg,
            padding.PSS(
                mgf=padding.MGF1(algorithm=hashes.SHA256()),
                salt_length=padding.PSS.MAX_LENGTH
            ),
            algorithm=hashes.SHA256()
        )
    except:
        return False
    return True
