"""Classes for sematic analysis"""

from tokdef import TokenType, Token
from syntaxtree import SyntaxTree
from functools import reduce, wraps


class STypeRecord:
    """Structure type"""

    def __init__(self, eles):
        self.eles = dict(eles)
        self.width = reduce(lambda total, x: total +
                            x['width'], self.eles.values(), 0)

    def get_ele_type(self, name):
        '''type of attribute `name`'''
        ele = self.eles.get(name)
        if ele:
            return ele['type']
        return None

    def get_ele_offset(self, name):
        '''offset of attribute `name`'''
        ele = self.eles.get(name)
        if ele is None:
            return None
        return ele['offset']

    def get_width(self):
        '''width of record'''
        return self.width

    def __str__(self):
        tmp_list = []
        for name, ele in self.eles.items():
            tmp_list.append("({}.{})".format(name, ele['type']))
        return "record({})".format("x".join(tmp_list))


class STypeArray:
    """Array Type"""

    def __init__(self, prim_type, dims):
        self.prim_type = prim_type
        self.dims = list(dims)

    def get_ele_type(self):
        '''get the element type'''
        if len(self.dims) == 1:
            return self.prim_type
        else:
            return STypeArray(self.prim_type, self.dims[1:])

    def get_width(self):
        '''get width of array'''
        prim_width = get_type_width(self.prim_type)
        return reduce(lambda g, x: g * x, self.dims, prim_width)

    def __str__(self):
        if len(self.dims) == 1:
            return 'array({},{})'.format(self.dims[0], self.prim_type)
        else:
            return 'array({},{})'.format(self.dims[0], self.get_ele_type())

    def __eq__(self, value):
        if not isinstance(value, STypeArray):
            return False
        return value.prim_type == self.prim_type and \
            value.dims == self.dims


class STypePtr:
    """Type of Pointer"""

    def __init__(self, ref_type):
        self.ref_type = ref_type

    def get_ref_type(self):
        '''get refered type'''
        return self.ref_type

    def __eq__(self, value):
        return isinstance(value, STypePtr) and value.ref_type == self.ref_type


class STypeFunc:
    """Type of function"""

    def __init__(self, ret_type, param_types):
        self.ret_type = ret_type
        self.param_types = param_types

    def __eq__(self, value):
        return isinstance(value, STypePtr) and value.ret_type == self.ret_type\
            and value.param_types == self.param_types

    def __str__(self):
        return "({}) -> {}".format(', '.join(map(str, self.param_types)), str(self.ret_type))


def get_type_width(stype):
    '''get width of specific type'''
    if isinstance(stype, TokenType):
        if stype == TokenType.CHAR or stype == TokenType.BOOL:
            return 1
        elif stype == TokenType.SHORT:
            return 2
        elif stype == TokenType.INT or stype == TokenType.FLOAT:
            return 4
        elif stype == TokenType.DOUBLE:
            return 8
        elif stype == TokenType.VOID:
            return 0
    if isinstance(stype, STypeArray):
        return stype.get_width()
    if isinstance(stype, STypeRecord):
        return stype.get_width()
    if isinstance(stype, STypeFunc):
        return get_type_width(stype.get_ret_type())
    assert False, "Unknown Type {}".format(str(stype))


class ILIns:
    """Intermidiate Language Instruction"""

    def __init__(self, op, y, z, x):
        self.op = op
        self.y = y
        self.z = z
        self.x = x

    def get_raw(self):
        return str(self)

    def get_pt(self):
        ystr = self.__str_operand(self.y)
        zstr = self.__str_operand(self.z)
        xstr = self.__str_operand(self.x)
        if self.op in ['+', '-', '*', '/', '%']:
            return "{} = {} {} {}".format(xstr, ystr, self.op, zstr)
        elif self.op == '=':
            return "{} = {}".format(self.x, self.y)
        elif self.op in ['<', '<=', '>', '>=', '==', '!=']:
            return "if {} {} {} goto {}".format(ystr, self.op, zstr, xstr)
        elif self.op in ['goto', 'param', 'return']:
            return '{} {}'.format(self.op, xstr)
        elif self.op == '=[]':
            return '{} = {}[{}]'.format(xstr, ystr, zstr)
        elif self.op == '[]=':
            return '{}[{}] = {}'.format(ystr, zstr, xstr)
        elif self.op == '&':
            return '{} = &{}'.format(xstr, ystr)
        elif self.op == '=*':
            return '{} = *{}'.format(xstr, ystr)
        elif self.op == '*=':
            return '*{} = {}'.format(xstr, ystr)
        elif self.op == 'call':
            return '{} = call {}, {}'.format(xstr, ystr, zstr)
        else:
            return 'Unknown op'

    def __str__(self):
        ystr = self.__str_operand(self.y)
        zstr = self.__str_operand(self.z)
        xstr = self.__str_operand(self.x)
        return "({}, {}, {}, {})".format(self.op, ystr, zstr, xstr)

    def __str_operand(self, operand):
        if operand is None:
            return "_"
        return str(operand)


class Operand:
    """value and type pair"""

    def __init__(self, val, valtype):
        self.type = valtype
        self.val = val

    def concrete(self) -> bool:
        return isinstance(self.val, (int, float))

    def get_concrete(self):
        assert self.concrete(), "Operand is not concrete with val {}".format(self.val)
        return self.val

    def __str__(self):
        return str(self.val)


class Symbol:
    """Symbol of symtab"""

    def __init__(self, name, type_, offset=0):
        self.name = name
        self.type = type_
        self.offset = offset

    def __str__(self):
        return str(self.name)


class SemanticContext:
    """store context of semantic analysis"""

    def __init__(self):
        self.tmpidx = 0  # offset of temp variable
        self.offset = 0  # symbol offset
        self.addr = 0  # addr of instruction
        self.symtab = {}
        self.func_symtab = {}
        self.code = []
        self.namespaces = {}

    def lookup_sym(self, name):
        '''find symbol in symtab and func_symtab'''
        sym = self.symtab.get(name)
        if sym is None:
            return self.func_symtab.get(name)
        return sym

    def add_sym(self, name, sym_type, offset=None):
        '''add symbol to symtab or func_symtab'''
        if name in self.func_symtab or name in self.symtab:
            return False
        if isinstance(sym_type, STypeFunc):
            self.func_symtab[name] = Symbol(name, sym_type, offset)
        else:
            self.symtab[name] = Symbol(name, sym_type, self.offset)
            self.offset += get_type_width(sym_type)
        return True

    def gen_tmp(self):
        '''get a temporary variable'''
        tmp = "t{}".format(self.tmpidx)
        self.tmpidx += 1
        return tmp

    def gen_ins(self, op, y, z, x):
        '''generate an instruction'''
        ins = ILIns(op, y, z, x)
        self.code.append(ins)
        self.addr += 1
        return ins

    def back_patch(self, lst, addr):
        '''patch x of instructions in lst with addr'''
        for ins in lst:
            ins.x = addr

    def migrate(self, name):
        '''migrate namespace of function with `name`
        this will save current symtab to namespace and reset offset and tmpidx
        '''
        self.namespaces[name] = self.symtab
        self.symtab = {}
        self.offset = 0
        self.tmpidx = 0


def deduce_type(*types):
    type_order = [
        TokenType.DOUBLE, TokenType.FLOAT, TokenType.LONG, TokenType.INT,
        TokenType.SHORT, TokenType.CHAR, TokenType.BOOL
    ]
    assume_type = types[0]
    for stype in types:
        if stype not in type_order:
            return None
    if reduce(lambda res, stype: res and stype == assume_type, types, True):
        return assume_type
    for assume_type in type_order:
        for stype in types:
            if stype == assume_type:
                return assume_type
    return None


def arithmatic_expr(func):
    '''decorator to arithmatic action handler'''
    @wraps(func)
    def wrapper(sym: SyntaxTree, context: SemanticContext):
        start = context.addr
        sym.set_attr('start', start)
        op = func(sym, context)
        ysym = sym.childs[0]
        fsym = sym.childs[2]
        yres = ysym.get_attr('res')
        fres = fsym.get_attr('res')
        res = context.gen_tmp()
        res_type = deduce_type(yres.type, fres.type)
        context.gen_ins(op, yres.val, fres.val, res)
        sym.set_attr('res', Operand(res, res_type))
        if res_type is None:
            return -1, "Can't do {} on {} and {}".format(op, str(yres.type), str(fres.type))
    return wrapper


RELOPS = {
    TokenType.LT: "<",
    TokenType.LE: "<=",
    TokenType.EQ: "==",
    TokenType.NE: "!=",
    TokenType.GT: ">",
    TokenType.GE: ">=",
}


class SemanticHandler:
    """Namespace for semantic action handlers"""
    @staticmethod
    def empty_s(sym: SyntaxTree, context: SemanticContext):
        sym.set_attr('nl', [])

    @staticmethod
    def stmt_s(sym: SyntaxTree, context: SemanticContext):
        saddr = sym.childs[1].get_attr('quad')
        s1nl = sym.childs[0].get_attr('nl')
        s2nl = sym.childs[2].get_attr('nl')
        if s1nl:
            context.back_patch(s1nl, saddr)
        sym.set_attr('nl', s2nl)

    @staticmethod
    def stmt_return(sym: SyntaxTree, context: SemanticContext):
        expr = sym.childs[1]
        res = expr.get_attr('res')
        context.gen_ins('return', None, None, res)

    @staticmethod
    def stmt_var_assign(sym: SyntaxTree, context: SemanticContext):
        var = sym.childs[0].sym.val
        symbol = context.lookup_sym(var)
        if not symbol:
            return -1, "{} is not defined".format(var)
        res = sym.childs[2].get_attr('res')
        if deduce_type(symbol.type, res.type) is None:
            return -1, "Invalid assignment from {} to {}".format(str(res.type), str(symbol.type))
        context.gen_ins("=", res, None, var)

    @staticmethod
    def stmt_arr_assign(sym: SyntaxTree, context: SemanticContext):
        lhs = sym.childs[0].get_attr('res')
        assert isinstance(lhs.type, STypePtr)
        expr = sym.childs[2].get_attr('res')
        if deduce_type(lhs.type.get_ref_type(), expr.type) is None:
            return -1, "Invalid assignment from {} to {}".format(str(expr.type), str(lhs.type))
        context.gen_ins('*=', expr.val, None, lhs.val)

    @staticmethod
    def stmt_call(sym: SyntaxTree, context: SemanticContext):
        fname = sym.childs[1].sym.val
        symbol = context.lookup_sym(fname)
        if not symbol:
            return -1, "Function {} is not defined".format(fname)
        args = sym.childs[3].get_attr('args')
        res = Operand(context.gen_tmp(), symbol.type.ret_type)
        context.gen_ins('call', symbol, len(args), res)
        sym.set_attr('res', res)

        args_len = len(args)
        params_len = len(symbol.type.param_types)
        if args_len < params_len:
            return -1, "Too few arguments was given"
        if args_len > params_len:
            return -1, "Too many arguments was given"
        for i in range(args_len):
            if args[i].type != symbol.type.param_types[i] and\
                deduce_type(args[i].type, symbol.type.param_types[i]) is None:
                args_str = ', '.join(map(lambda arg: str(arg.type), args))
                params_str = ', '.join(map(str, symbol.type.param_types))
                return -1, "Incompatible parameter types {}({}). expected {}({})".format(
                    fname, args_str, fname, params_str)

    @staticmethod
    def stmt_if(sym: SyntaxTree, context: SemanticContext):
        sym_b = sym.childs[1]
        sym_s = sym.childs[4]
        sstart = sym.childs[3].get_attr('quad')
        btl = sym_b.get_attr('tl')
        bfl = sym_b.get_attr('fl')
        context.back_patch(btl, sstart)
        snl = bfl + sym_s.get_attr('nl')
        sym.set_attr('nl', snl)

    @staticmethod
    def else_gate(sym: SyntaxTree, context: SemanticContext):
        ins = context.gen_ins('goto', None, None, None)
        sym.set_attr('nl', [ins])

    @staticmethod
    def stmt_if_else(sym: SyntaxTree, context: SemanticContext):
        sym_b = sym.childs[1]
        sym_s1 = sym.childs[4]
        sym_s2 = sym.childs[10]
        else_gate = sym.childs[6]
        s1start = sym.childs[3].get_attr('quad')
        s2start = sym.childs[9].get_attr('quad')
        s1nl = sym_s1.get_attr('nl')
        s2nl = sym_s2.get_attr('nl')
        btl = sym_b.get_attr('tl')
        bfl = sym_b.get_attr('fl')
        context.back_patch(btl, s1start)
        context.back_patch(bfl, s2start)
        snl = s1nl + s2nl + else_gate.get_attr('nl')
        sym.set_attr('nl', snl)

    @staticmethod
    def stmt_while(sym: SyntaxTree, context: SemanticContext):
        sym_b = sym.childs[2]
        sym_s = sym.childs[6]
        bstart = sym.childs[1].get_attr('quad')
        sstart = sym.childs[5].get_attr('quad')
        snl = sym_s.get_attr('nl')
        btl = sym_b.get_attr('tl')
        bfl = sym_b.get_attr('fl')
        context.back_patch(snl, bstart)
        context.back_patch(btl, sstart)
        context.gen_ins('goto', None, None, bstart)
        sym.set_attr('nl', bfl)

    @staticmethod
    def stmt_ref_assign(sym: SyntaxTree, context: SemanticContext):
        lhs = sym.childs[0].get_attr('res')
        assert isinstance(lhs.type, STypePtr)
        expr = sym.childs[2].get_attr('res')
        if deduce_type(lhs.type.get_ref_type(), expr.type) is None:
            return -1, "Invalid assignment from {} to {}".format(str(expr.type), str(lhs.type.get_ref_type()))
        context.gen_ins('*=', expr.val, None, lhs.val)

    @staticmethod
    def func_decl(sym: SyntaxTree, context: SemanticContext):
        name = sym.childs[1].sym.val
        params = sym.childs[3].get_attr('params')
        ret_type = sym.childs[6].get_attr('type')
        start = sym.childs[8].get_attr('quad')
        param_types = list(map(lambda param: param['type'], params))
        sym_type = STypeFunc(ret_type, param_types)
        if name in context.namespaces:
            return -1, "Duplicated declaration of function {}".format(name)
        context.migrate(name)
        context.add_sym(name, sym_type, start)

    @staticmethod
    def func_decl_void(sym: SyntaxTree, context: SemanticContext):
        name = sym.childs[1].sym.val
        params = sym.childs[3].get_attr('params')
        start = sym.childs[6].get_attr('quad')
        ret_type = TokenType.VOID
        param_types = list(map(lambda param: param['type'], params))
        sym_type = STypeFunc(ret_type, param_types)
        if name in context.namespaces:
            return -1, "Duplicated declaration of function {}".format(name)
        context.migrate(name)
        context.add_sym(name, sym_type, start)
        context.gen_ins('return', None, None, 'void')

    @staticmethod
    def stmt_var_decl(sym: SyntaxTree, context: SemanticContext):
        vard = sym.childs[0]
        child_type = vard.get_attr('type')
        child_name = vard.get_attr('name')
        if child_name in context.symtab:
            return -1, "Duplicate variable {}".format(child_name)
        context.add_sym(child_name, child_type)

    @staticmethod
    def prim_type(sym: SyntaxTree, context: SemanticContext):
        sym_type = sym.childs[0].sym.type
        sym.set_attr("type", sym_type)
        if sym_type == TokenType.CHAR:
            width = 1
        elif sym_type == TokenType.SHORT:
            width = 2
        elif sym_type == TokenType.INT or sym_type == TokenType.FLOAT:
            width = 4
        elif sym_type == TokenType.DOUBLE:
            width = 8
        else:
            assert False, "Unknown Type {}".format(str(sym_type))
        sym.set_attr("width", width)

    @staticmethod
    def arr_brace(sym: SyntaxTree, context: SemanticContext):
        size = sym.childs[1].sym.val
        dims = [size]
        ll_dims = sym.childs[3].get_attr('dims')
        if ll_dims:
            dims.extend(ll_dims)
        sym.set_attr('dims', dims)

    @staticmethod
    def array_type(sym: SyntaxTree, context: SemanticContext):
        dims = sym.childs[1].get_attr('dims')
        prim_type = sym.childs[0].get_attr('type')
        prim_width = sym.childs[0].get_attr('width')
        if dims:
            sym_type = STypeArray(prim_type, dims)
            ele_num = reduce(lambda total, x: total * x, dims)
        else:
            sym_type = prim_type
            ele_num = 1
        sym.set_attr('type', sym_type)
        sym.set_attr('width', prim_width * ele_num)

    @staticmethod
    def var_decl(sym: SyntaxTree, context: SemanticContext):
        child_type = sym.childs[0].get_attr('type')
        child_width = sym.childs[0].get_attr('width')
        child_name = sym.childs[1].sym.val
        sym.set_attr('type', child_type)
        sym.set_attr('width', child_width)
        sym.set_attr('name', child_name)

    @staticmethod
    def var_decl_list_begin(sym: SyntaxTree, context: SemanticContext):
        child_type = sym.childs[0].get_attr('type')
        child_width = sym.childs[0].get_attr('width')
        child_name = sym.childs[0].get_attr('name')
        eles = {child_name: {"type": child_type,
                             "width": child_width, "offset": 0}}
        sym.set_attr('eles', eles)
        sym.set_attr('offset', child_width)

    @staticmethod
    def var_decl_list(sym: SyntaxTree, context: SemanticContext):
        has_err = False
        vard = sym.childs[1]
        child_type = vard.get_attr('type')
        child_width = vard.get_attr('width')
        child_name = vard.get_attr('name')
        vardl = sym.childs[0]
        eles = dict(vardl.get_attr('eles'))
        offset = vardl.get_attr('offset')
        if child_name in eles:
            has_err = True
        else:
            eles[child_name] = {"type": child_type,
                                "width": child_width, "offset": offset}
            offset += child_width
        sym.set_attr('eles', eles)
        sym.set_attr('offset', offset)
        if has_err:
            return -1, "Duplicate record attribute {}".format(child_name)

    @staticmethod
    def struct_type(sym: SyntaxTree, context: SemanticContext):
        vardl = sym.childs[2]
        eles = vardl.get_attr('eles')
        sym_width = vardl.get_attr('width')
        sym_type = STypeRecord(eles)
        sym.set_attr('type', sym_type)
        sym.set_attr('width', sym_width)

    @staticmethod
    def param_list_one(sym: SyntaxTree, context: SemanticContext):
        child_type = sym.childs[0].get_attr('type')
        child_width = sym.childs[0].get_attr('width')
        child_name = sym.childs[1].sym.val
        context.add_sym(child_name, child_type)
        params = [{'name': child_name, 'type': child_type, 'width': child_width}]
        sym.set_attr('params', params)

    @staticmethod
    def param_list(sym: SyntaxTree, context: SemanticContext):
        has_err = False
        err_strs = []
        child_type = sym.childs[2].get_attr('type')
        child_width = sym.childs[2].get_attr('width')
        child_name = sym.childs[3].sym.val
        sym_param = sym.childs[0]
        params = list(sym_param.get_attr('params'))
        for param in params:
            if child_name == param['name']:
                has_err = True
                err_strs.append("Duplicate parameter {}".format(child_name))
        context.add_sym(child_name, child_type)
        params.append(
            {'name': child_name, 'type': child_type, 'width': child_width})
        sym.set_attr('params', params)
        if has_err:
            return -1, '\n'.join(err_strs)

    @staticmethod
    def param_list_empty(sym: SyntaxTree, context: SemanticContext):
        sym.set_attr('params', [])

    @staticmethod
    def expr_derive(sym: SyntaxTree, context: SemanticContext):
        attrs = sym.childs[0].attrs
        sym.attrs = attrs

    @staticmethod
    def paren_expr(sym: SyntaxTree, context: SemanticContext):
        attrs = sym.childs[1].attrs
        sym.attrs = attrs

    @staticmethod
    def quad(sym: SyntaxTree, context: SemanticContext):
        sym.set_attr('quad', context.addr)

    @staticmethod
    def bool_expr1(sym: SyntaxTree, context: SemanticContext):
        sym_b = sym.childs[0]
        sym_g = sym.childs[3]
        quad = sym.childs[2].get_attr('quad')
        btl = sym_b.get_attr('tl')
        gtl = sym_g.get_attr('tl')
        bfl = sym_b.get_attr('fl')
        gfl = sym_g.get_attr('fl')
        context.back_patch(bfl, quad)
        sym.set_attr('tl', btl + gtl)
        sym.set_attr('fl', gfl)

    @staticmethod
    def bool_expr2(sym: SyntaxTree, context: SemanticContext):
        sym_g = sym.childs[0]
        sym_h = sym.childs[3]
        quad = sym.childs[2].get_attr('quad')
        gtl = sym_g.get_attr('tl')
        htl = sym_h.get_attr('tl')
        gfl = sym_g.get_attr('fl')
        hfl = sym_h.get_attr('fl')
        context.back_patch(gtl, quad)
        sym.set_attr('tl', htl)
        sym.set_attr('fl', gfl + hfl)

    @staticmethod
    def bool_expr3(sym: SyntaxTree, context: SemanticContext):
        sym_h = sym.childs[1]
        htl = sym_h.get_attr('tl')
        hfl = sym_h.get_attr('fl')
        sym.set_attr('tl', hfl)
        sym.set_attr('fl', htl)

    @staticmethod
    def bool_expr4(sym: SyntaxTree, context: SemanticContext):
        start = context.addr
        sym.set_attr('start', start)
        res1 = sym.childs[0].get_attr('res')
        res2 = sym.childs[2].get_attr('res')
        relop = RELOPS[sym.childs[1].childs[0].sym.type]
        ins1 = context.gen_ins(relop, res1, res2, None)
        sym.set_attr('tl', [ins1])
        ins2 = context.gen_ins('goto', None, None, None)
        sym.set_attr('fl', [ins2])

    @staticmethod
    def bool_expr5(sym: SyntaxTree, context: SemanticContext):
        toktype = sym.childs[0].childs[0].sym.type
        start = context.addr
        sym.set_attr('start', start)
        if toktype == TokenType.TRUE:
            ins = context.gen_ins('goto', None, None, None)
            stl = [ins]
            sfl = []
        else:
            ins = context.gen_ins('goto', None, None, None)
            stl = []
            sfl = [ins]
        sym.set_attr('tl', stl)
        sym.set_attr('fl', sfl)

    @staticmethod
    @arithmatic_expr
    def arithmatic_expr1(sym: SyntaxTree, context: SemanticContext):
        return '+'

    @staticmethod
    @arithmatic_expr
    def arithmatic_expr2(sym: SyntaxTree, context: SemanticContext):
        return '-'

    @staticmethod
    @arithmatic_expr
    def arithmatic_expr3(sym: SyntaxTree, context: SemanticContext):
        return '*'

    @staticmethod
    @arithmatic_expr
    def arithmatic_expr4(sym: SyntaxTree, context: SemanticContext):
        return '/'

    @staticmethod
    @arithmatic_expr
    def arithmatic_expr5(sym: SyntaxTree, context: SemanticContext):
        return '%'

    @staticmethod
    def sub_val(sym: SyntaxTree, context: SemanticContext):
        tok = sym.childs[1].sym
        if tok.type == TokenType.CINT:
            res_type = TokenType.INT
        elif tok.type == TokenType.CFLOAT:
            res_type = TokenType.FLOAT
        else:
            assert False, "Invalid Type {}".format(tok.type)
        sym.set_attr("res", Operand(-tok.val, res_type))

    @staticmethod
    def expr_idn(sym: SyntaxTree, context: SemanticContext):
        var = sym.childs[0].sym.val
        symbol = context.lookup_sym(var)
        if not symbol:
            return -1, "{} is not defined".format(var)
        operand = Operand(var, symbol.type)
        sym.set_attr("res", operand)

    @staticmethod
    def expr_tok(sym: SyntaxTree, context: SemanticContext):
        tok = sym.childs[0].sym
        if tok.type == TokenType.CINT:
            res_type = TokenType.INT
        elif tok.type == TokenType.CFLOAT:
            res_type = TokenType.FLOAT
        else:
            assert False, "Invalid Type {}".format(tok.type)
        operand = Operand(tok.val, res_type)
        sym.set_attr("res", operand)

    @staticmethod
    def arr_ref_one(sym: SyntaxTree, context: SemanticContext):
        start = context.addr
        sym.set_attr('start', start)
        var = sym.childs[0].sym.val
        symbol = context.lookup_sym(var)
        if not symbol:
            return -1, "{} is not defined".format(var)
        elif not isinstance(symbol.type, STypeArray):
            return -1, "{} is not a valid array".format(var)
        index_res = sym.childs[2].get_attr('res')
        # get offset
        ele_type = symbol.type.get_ele_type()
        if index_res != 0:
            res1 = context.gen_tmp()
            ele_type_width = get_type_width(ele_type)
            context.gen_ins("*", index_res, ele_type_width, res1)
            res2 = context.gen_tmp()
            context.gen_ins('&', var, None, res2)
            res = context.gen_tmp()
            context.gen_ins('+', res2, res1, res)
        else:
            res = context.gen_tmp()
            context.gen_ins('&', var, None, res)
        res_type = STypePtr(ele_type)
        sym.set_attr('res', Operand(res, res_type))

    @staticmethod
    def arr_ref(sym: SyntaxTree, context: SemanticContext):
        start = context.addr
        sym.set_attr('start', start)
        operand = sym.childs[0].get_attr('res')
        last_res = operand.val
        last_type = operand.type
        arr_type = last_type.get_ref_type()
        if not isinstance(arr_type, STypeArray):
            return -1, "Too many array brackets"
        ele_type = arr_type.get_ele_type()

        index_operand = sym.childs[2].get_attr('res')
        index_res = index_operand.val
        index_type = index_operand.type
        if index_type != TokenType.INT:
            return -1, "Array index must be int"
        if index_res != 0:
            res1 = context.gen_tmp()
            ele_type_width = get_type_width(ele_type)
            context.gen_ins("*", index_res, ele_type_width, res1)
            res = context.gen_tmp()
            context.gen_ins('+', last_res, res1, res)
        else:
            res = last_res
        res_type = STypePtr(ele_type)
        sym.set_attr('res', Operand(res, res_type))

    @staticmethod
    def record_ref(sym: SyntaxTree, context: SemanticContext):
        start = context.addr
        sym.set_attr('start', start)
        var = sym.childs[0].sym.val
        attr = sym.childs[2].sym.val
        symbol = context.lookup_sym(var)
        if not symbol:
            return -1, "{} is not defined".format(var)
        elif not isinstance(symbol.type, STypeRecord):
            return -1, "{} is not a valid structure".format(var)
        offset = symbol.type.get_ele_offset(attr)
        if offset is None:
            return -1, "{} has no attribute {}".format(var, attr)
        if offset != 0:
            res1 = context.gen_tmp()
            context.gen_ins("&", var, None, res1)
            res = context.gen_tmp()
            context.gen_ins('+', res1, offset, res)
        else:
            res = context.gen_tmp()
            context.gen_ins("&", var, None, res)
        res_type = STypePtr(symbol.type.get_ele_type(attr))
        sym.set_attr('res', Operand(res, res_type))

    @staticmethod
    def arg_list_one(sym: SyntaxTree, context: SemanticContext):
        res = sym.childs[0].get_attr('res')
        sym.set_attr('args', [res])

    @staticmethod
    def arg_list_empty(sym: SyntaxTree, context: SemanticContext):
        sym.set_attr('args', [])

    @staticmethod
    def arg_list(sym: SyntaxTree, context: SemanticContext):
        left = sym.childs[0]
        arg = sym.childs[2]
        args = left.get_attr('args')
        args += [arg.get_attr('res')]
        sym.set_attr('args', args)


class SemanticAction:
    """Class to store action on semantic analysis"""
    action_map = dict(map(
        lambda item: (item[0], item[1].__func__),
        filter(
            lambda item: not item[0].startswith('__'),
            SemanticHandler.__dict__.items())))

    def __init__(self, name):
        self.name = name
        assert self.name in self.action_map,\
            "Action {} is not defined".format(name)
        self.handle = self.action_map.get(name)

    def do_action(self, sym, context: SemanticContext):
        return self.handle(sym, context)
