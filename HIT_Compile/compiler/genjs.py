import string
import itertools

def present_s(ch):
	res = []
	if ch == "\"":
		res.append("\\\"")
	elif ch == "\\":
		res.append("\\\\")
	elif ch == "\f":
		res.append("\\f")
	elif ch in '\t\n\r':
		res.append(repr(ch).replace("'", ""))
	else:
		res.append(ch)
	return "\"{}\"".format(''.join(res))

def print_js(char_set, dst, cols=7):
	cnter = itertools.count()
	print(",\n\t\t\t".join(
		map(
			lambda arr: ', '.join(arr[1]),
			itertools.groupby(
				map(lambda c:"{}:{}".format(present_s(c), present_s(dst)), char_set),
				lambda c: next(cnter) // cols
			)
		)
	))