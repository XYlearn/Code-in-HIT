#ifndef _HTREE_H_
#define _HTREE_H_

#include <utility>	//for std::pair
#include <vector>	//for std::vector
#include <string>	//for std::string
#include <map>		//for std::map
#include <algorithm>	//for std::reverse
#include "heap.h"	//for Heap

using std::vector;
using std::pair;
using std::make_pair;
using std::map;
using std::string;
using std::reverse;

// template<datatype, weighttype>
template<typename T, typename W>
class HNode
{
public:
	int parent;
	int lChild;
	int rChild;
	W weight;
	T data;

	// default constructor
	HNode() :parent(-1), lChild(-1), rChild(-1) {}

	//build with weight and data
	HNode(T data, W weight) : parent(-1), lChild(-1), rChild(-1), weight(weight), data(data) {}

	pair<int, W> toHeapNode(int ind) { return pair<int, W>(ind, weight); }
};

template<typename W>
struct HeapEle
{
	W weight;
	int index;

	HeapEle(int index, W weight) :index(index), weight(weight) {}

	//compare operator define
	bool operator<(const HeapEle<W> &ele) const { return weight < ele.weight; }
	bool operator<=(const HeapEle<W> &ele) const { return weight <= ele.weight; }
	bool operator>(const HeapEle<W> &ele) const { return weight > ele.weight; }
	bool operator>=(const HeapEle<W> &ele) const { return weight >= ele.weight; }
	bool operator==(const HeapEle<W> &ele) const { return weight == ele.weight; }
	bool operator!=(const HeapEle<W> &ele) const { return weight != ele.weight; }
};

template<typename T, typename W>
class HTree
{
protected:
	vector<HNode<T, W>> vec;
	Heap<HeapEle<W>> heap;	// to find min weight node
	size_t leaves_num;

public:
	//default constructor
	HTree() :heap(Heap<HeapEle<W>>(false)) {}

	//constructor initialized with a vector
	HTree(const map<T, W>);

	// add an element into Haffman Tree
	void add(const T &data, const W & weight);

	// get code in a bool vector of a element
	// which is coding by traversal direction 
	// of each nodes in the path from root to the node
	// 0 stands for left; 1 stands for right
	vector<bool> getVec(int ind);

	//get code; return binary string
	string getCode(int ind);

	void build();
};

template<typename T, typename W>
HTree<T, W>::HTree(const map<T, W> m) : heap(Heap<HeapEle<W>>(false))
{
	for (auto ele : m)
		add(ele.first, ele.second);
	this->vec.reserve(vec.size() * 2 - 1);
}

template<typename T, typename W>
void HTree<T, W>::add(const T &data, const W & weight)
{
	HNode<T, W> node(data, weight);
	HeapEle<W> ele(vec.size(), weight);

	heap.push(ele);
	vec.push_back(node);
	
	++leaves_num;
}

// build the initialized tree to Haffman Tree
template<typename T, typename W>
void HTree<T, W>::build()
{
	HNode<T, W> node;

	// add n-1 nodes
	size_t cur_ind = leaves_num;
	size_t end_ind = 2 * cur_ind - 1;
	if (end_ind == -1)
		return ;

	size_t parent;
	for (; cur_ind != end_ind; ++cur_ind)
	{
		// find two min element 
		HeapEle<W> ele1 = heap.pop();
		HeapEle<W> ele2 = heap.pop();

		// attach to a node
		node.lChild = ele1.index;
		node.rChild = ele2.index;
		vec[ele1.index].parent = cur_ind;
		vec[ele2.index].parent = cur_ind;

		//parent weight is sum of child
		node.weight = ele1.weight + ele2.weight;
		ele1.weight = node.weight;
		ele1.index = cur_ind;

		//insert the parent node
		heap.push(ele1);
		vec.push_back(node);
	}
}

// return bitvec
template <typename T, typename W>
vector<bool> HTree<T, W>::getVec(int ind)
{
	vector<bool> res;

	for (auto i = ind; ; i = vec[i].parent)
	{
		if(i == -1)
			break;
		int parent = vec[i].parent;
		if (parent == -1)
			break;
		if (i == vec[parent].lChild)
			res.push_back(false);
		else res.push_back(true);
	}
	reverse(res.begin(), res.end());
	return res;
}

// return bit string
template <typename T, typename W>
string HTree<T, W>::getCode(int ind)
{
	vector<bool> codeVec = getVec(ind);
	string code;
	for (auto bit : codeVec)
		code.push_back(bit ? '1' : '0');
	return code;
}

#endif
