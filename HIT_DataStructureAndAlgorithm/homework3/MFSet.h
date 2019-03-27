#ifndef _MFSET_H_
#define _MFSET_H_

#include <utility>
#include <vector>

using std::pair;
using std::make_pair;
using std::vector;

class MFSet
{
private:
	vector<size_t> parent;
	vector<size_t> count;

	static const size_t NIL = -1;
public:
	MFSet(size_t sz) :parent(sz, NIL), count(sz, 1) {}

	void merge(size_t ind1, size_t ind2)
	{
		if (count[ind1] < count[ind2])
		{
			parent[ind1] = ind2;
			count[ind2] += count[ind1];
		}
		else
		{
			parent[ind2] = ind1;
			count[ind1] += count[ind2];
		}
	}

	size_t find(size_t index)
	{
		size_t tmp = index;
		while (parent[tmp] != NIL)
			tmp = parent[tmp];
		return tmp;
	}
};

#endif
