/*
 *	Heap Class
 *	derived from class BTree
 */
#ifndef _HEAP_H_
#define _HEAP_H_

#include "array_btree.h" //for ArrayBTree
#include <vector>	//for std::vector
#include <functional> //for std::function

using std::vector;

template<typename T>
class Heap : public ArrayBTree<T>
{
protected:
	bool maxHeap;
public:
	Heap(bool max) :ArrayBTree<T>(), maxHeap(max) {}

	// build a heap from std::vector
	void build(const vector<T>& vec);

	// build a heap from BTree
	void build(const ArrayBTree<T>& abtree);

	// add a BNode of data T to Heap in proper position
	void push(const T &val);

	// get value of top BNode and delete it
	T pop();

	// get value of top BNode and delete it
	T top() { return vec[0]; }
};

//build a heap from vector
template<typename T>
void Heap<T>::build(const vector<T> &vec)
{
	for (auto ind = 0; ind != vec.size(); ++ind)
		push(vec[ind]);
}

//build a heap from btree
template <typename T>
void Heap<T>::build(const ArrayBTree<T> &abtree)
{
	for (auto ind = 0; ind != abtree.size(); ++ind)
		this->push(abtree.vec[ind]);
}

template <typename T>
void Heap<T>::push(const T & val)
{
	size_t ind = vec.size();

	vec.push_back(val);	

	size_t par = parent(ind);

	if (par == nil) return;

	// different compare according to maxHeap and minHeap
	while (par != nil && (maxHeap ? (val > vec[par]) : (val < vec[par])) )
	{
		vec[ind] = vec[par];
		ind = par;
		par = parent(ind);
	}
	
	vec[ind] = val;
}

// pop top element; throw exception 
template <typename T>
T Heap<T>::pop()
{
	if (empty())
		throw std::range_error("range error");
	size_t par = 0;
	size_t child = 1;

	// pop last element
	T temp = vec.back();
	vec.pop_back();

	if (vec.empty())
		return temp;

	// for return 
	T ele = vec[0];

	while (rChild(par) < vec.size())
	{
		// find proper child 
		if (maxHeap)
		{
			//right child come first if equal
			if (get(lChild(par)) > get(rChild(par)))
				child = lChild(par);
			else child = rChild(par);
			if (temp >= get(child)) break;
		}
		else
		{
			//left child come first if equal
			if (get(lChild(par)) > get(rChild(par)))
				child = rChild(par);
			else child = lChild(par);
			if (temp <= get(child)) break;
		}
		vec[par] = vec[child];
		par = child;
	}
	vec[par] = temp;

	return ele;
}


#endif

