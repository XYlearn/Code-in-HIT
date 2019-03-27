from OpenSSL import crypto
import os


def create_subject(**name):
    req = crypto.X509Req()
    subj = req.get_subject()

    for key, value in name.items():
        setattr(subj, key, value)
    return subj


def create_cert_req(pkey, digest='sha256', dump=False, **name):
    """
    Create a certificate request.
    Arguments: pkey     - The key to associate with the request
               digest   - Digestion method to use for signing, default is sha256
               convert  - whether to convert to pem string
               **name   - The name of the subject of the request, possible
                        arguments are:
                          C     - Country name
                          ST    - State or province name
                          L     - Locality name
                          O     - Organization name
                          OU    - Organizational unit name
                          CN    - Common name
                          emailAddress - E-mail address
    Returns:   The certificate request in an X509Req object

    Example:
        pkey = crypto.PKey()
        pkey.generate_key(crypto.TYPE_RSA, 2048)
        name = {
            'C': 'CN',
            'ST': 'Fujian',
            'L': 'ZhouNing',
            'O': 'Stay Home',
            'OU': 'CTF department',
            'CN': 'l1nk',
            'emailAddress': 'xylearn@qq.com'
        }
        create_cert_req(pkey, **name)
    """
    req = crypto.X509Req()
    subj = req.get_subject()

    for key, value in name.items():
        setattr(subj, key, value)

    req.set_pubkey(pkey)
    req.sign(pkey, digest)
    if not dump:
        return req
    else:
        return crypto.dump_certificate_request(crypto.FILETYPE_PEM, req)


def create_cert(req, issuer_cert, issuer_key, serial, validity_period,
                digest="sha256", dump=False):
    """
    Generate a certificate given a certificate request.
    Arguments: req        - Certificate request to use
               issuer_cert - The certificate of the issuer
               issuer_key  - The private key of the issuer
               serial     - Serial number for the certificate
               notBefore  - Timestamp (relative to now) when the certificate
                            starts being valid
               notAfter   - Timestamp (relative to now) when the certificate
                            stops being valid
               digest     - Digest method to use for signing, default is sha256
    Returns:   The signed certificate in an X509 object
    """
    notBefore, notAfter = validity_period
    cert = crypto.X509()
    cert.set_serial_number(serial)
    cert.gmtime_adj_notBefore(notBefore)
    cert.gmtime_adj_notAfter(notAfter)
    cert.set_issuer(issuer_cert.get_subject())
    cert.set_subject(req.get_subject())
    cert.set_pubkey(req.get_pubkey())
    cert.sign(issuer_key, digest)
    if not dump:
        return cert
    else:
        return crypto.dump_certificate(crypto.FILETYPE_PEM, cert)


def generate_pkey(crypt_type=crypto.TYPE_RSA, bits=2048):
    pkey = crypto.PKey()
    pkey.generate_key(crypt_type, bits)
    return pkey


def generate_keypair(crypt_type=crypto.TYPE_RSA, bits=2048):
    pkey = crypto.PKey()
    pkey.generate_key(crypt_type, bits)
    pubkey = crypto.dump_publickey(crypto.FILETYPE_PEM, pkey)
    privkey = crypto.dump_privatekey(crypto.FILETYPE_PEM, pkey)
    return pubkey, privkey


def save_keypair(keypair, path, filepref='id_rsa'):
    pubkey_path = os.path.join(path, filepref + '.pub')
    privkey_path = os.path.join(path, filepref)
    if pubkey_path:
        with open(pubkey_path, "w+") as f:
            f.write(keypair[0])
    with privkey_path:
        with open(privkey_path, "w+") as f:
            f.write(keypair[1])
    return True


def load_cert(path):
    with open(path, 'r') as f:
        pem = f.read()
    return crypto.load_certificate(crypto.FILETYPE_PEM, pem)


def load_privkey(path):
    with open(path, 'r') as f:
        pem = f.read()
    return crypto.load_privatekey(crypto.FILETYPE_PEM, pem)


def dump_cert(path, cert):
    with open(path, "w+") as f:
        pem = crypto.dump_certificate(crypto.FILETYPE_PEM, cert)
        f.write(pem)


def dump_privkey(path, pkey):
    with open(path, "w+") as f:
        pem = crypto.dump_privatekey(crypto.FILETYPE_PEM, pkey)
        f.write(pem)

