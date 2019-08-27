"""
python implementation of Booyer-Moore Algorithm
"""

def get_BMBC(pattern):
    BMBC = dict()
    for i in range(len(pattern) - 1):
        char = pattern[i]
        BMBC[char] = i + 1
    return BMBC


def get_BMGS(pattern):
    BMGS = dict()
    BMGS[''] = 0
    for i in range(len(pattern)):
        GS = pattern[len(pattern) - i - 1:]
        for j in range(len(pattern) - i - 1):
            NGS = pattern[j:j + i + 1]
            if GS == NGS:
                BMGS[GS] = len(pattern) - j - i - 1
    return BMGS


def booyer_moore(string, pattern, BMBC, BMGS):
    i = 0
    j = len(pattern)
    while i < len(string):
        while j > 0:
            a = string[i + j - 1:i + len(pattern)]
            b = pattern[j - 1:]
            if a == b:
                j = j - 1
            else:
                i = i + max(BMGS.setdefault(b[1:], len(pattern)),
                            j - BMBC.setdefault(string[i + j - 1], 0))
                j = len(pattern)
            if j == 0:
                yield i
