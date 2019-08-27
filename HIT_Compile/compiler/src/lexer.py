#!env python3
"""
This module is to load lexer DFA and do lex
"""

import json

from tokdef import Token, TokenType, get_keyword_type


class LexException(Exception):
    pass


class Lexer:
    """Lexer"""
    def __init__(self, lex_table):
        assert isinstance(lex_table, dict), "lex_table must be dictionary"
        self.status_tp = lex_table['status_tp']
        self.start = lex_table['start']
        # self.curr = self.start
        self.accepts = set(lex_table['accepts'])
        self.conv = lex_table['table']
        assert isinstance(self.conv, dict), "table must be dictionary"

    @staticmethod
    def load_lexer(path):
        '''load lexer from file'''
        with open(path, 'r') as lexer_file:
            lex_table = json.load(lexer_file)
        return Lexer(lex_table)

    def lex(self, text):
        '''lex text
        Args:
            text(str): text to lex
        Return(list(str), list(str)): token list and error list
        '''
        tokens = []

        stat = self.start
        status_conv = self._get_status_conv(stat)
        last_idx = 0
        curr_idx = 0
        err_strs = []
        while curr_idx < len(text):
            ch = text[curr_idx]

            # receive good character
            is_good_ch = ch in status_conv
            # receive bad character at acceptable status
            is_accept_stat = self._is_accept(stat) and stat != self.start

            if is_good_ch or is_accept_stat:
                if is_good_ch:
                    next_stat = status_conv[ch]
                else:
                    curr_idx -= 1
                    next_stat = self.start
                # move to start
                if next_stat == self.start:
                    token = self._parse_token(
                        text, last_idx, curr_idx + 1, stat)
                    if token is not None:
                        tokens.append(token)
                    else:
                        # space
                        if last_idx == curr_idx:
                            last_idx += 1
                            curr_idx += 1
                            continue
                        # fail to get token
                        raw_token = text[last_idx: curr_idx + 1]
                        err_str = self._gen_err_str(
                            text, last_idx, "Unknown token {}", repr(raw_token))
                        err_strs.append(err_str)
                        stat = self.start
                        status_conv = self._get_status_conv(stat)
                        last_idx = curr_idx + 1
                        continue
                        # raise LexException(err_str)
                    last_idx = curr_idx + 1
                stat = next_stat
                status_conv = self._get_status_conv(stat)
            else:
                # Bad Character
                err_str = self._gen_err_str(
                    text, last_idx, "Unexpected serial {}", repr(text[last_idx:curr_idx + 1]))
                # raise LexException(err_str)
                err_strs.append(err_str)
                last_idx = curr_idx + 1
                stat = self.start
                status_conv = self._get_status_conv(stat)
            curr_idx += 1
        # check the last token
        if last_idx != curr_idx and self._is_accept(stat):
            token = self._parse_token(text, last_idx, curr_idx + 1, stat)
            if token is not None:
                tokens.append(token)
            else:
                raw_token = text[last_idx:]
                err_str = self._gen_err_str(
                    text, last_idx, "Unknown token {}", repr(raw_token))
                # raise LexException(err_str)
                err_strs.append(err_str)
        elif not self._is_accept(stat):
            err_str = self._gen_err_str(
                text, last_idx, "Unexpected serial {}", repr(text[last_idx:]))
            # raise LexException(err_str)
            err_strs.append(err_str)

        return tokens, err_strs

    def _gen_err_str(self, text, index, fmt, *args):
        # get line no
        row, col = count_position(text, index)
        return "{}:{} ".format(row, col) + fmt.format(*args)

    def _get_status_conv(self, status):
        if status in self.conv:
            return self.conv[status]
        else:
            return {}

    def _is_accept(self, status):
        return status in self.accepts

    def _parse_token(self, text, start, end, stat):
        '''parse token at stat from raw'''
        raw = text[start: end]
        if stat in self.status_tp:
            tp_str = self.status_tp[stat]
            # identifier or keywords
            if tp_str == "IDN":
                keyword_type = get_keyword_type(raw)
                if keyword_type:
                    return Token(keyword_type, None, start, end)
                return Token(TokenType.IDN, raw.strip(), start, end)
            token_type = TokenType[tp_str]
            if token_type == TokenType.CINT:
                val = self._parse_int(raw)
            elif token_type == TokenType.CFLOAT:
                val = self._parse_float(raw)
            elif token_type == TokenType.CCHAR:
                # char is integer
                token_type = TokenType.CINT
                val = self._parse_char(raw)
            elif token_type == TokenType.CSTR:
                val = self._parse_str(raw)
            else:
                val = None
            return Token(token_type, val, start, end)
        # no token type is given
        else:
            return None

    def _parse_int(self, raw):
        return eval(raw)

    def _parse_float(self, raw):
        return eval(raw)

    def _parse_char(self, raw):
        assert raw[0] == "'" and raw[-1] == "'", "wrong char format"
        return ord(eval(raw))

    def _parse_str(self, raw):
        return eval(raw)


def count_position(text, index):
    '''count the line no and column no of index in text'''
    lineno = 0
    line_start = 0
    while line_start < index:
        line_end = text.find('\n', line_start)
        if line_end < 0:
            break
        if line_end < index:
            line_start = line_end + 1
            lineno += 1
        else:
            break
    lineno += 1
    colno = index - line_start
    return lineno, colno
