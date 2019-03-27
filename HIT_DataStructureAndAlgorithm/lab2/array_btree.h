#ifndef _ARRAY_BTREE_H_
#define _ARRAY_BTREE_H_

#include <vector> //for std::vector
#include <cstddef>	//for size_t	
#include <functional>	//for std::function

template<typename T>
using ABVisitFunc = std::function<void(const T&)>;

using std::vector;

template<typename T>
class ArrayBTree
{

protected:
	
	vector<T> vec;

	// stand for not found
	static size_t nil = -1;

public:

	//default constructor
	ArrayBTree() = default;

	//destructor
	~ArrayBTree() = default;

	//copy constructor
	ArrayBTree(const ArrayBTree &tree) = default;

	//move constructor
	ArrayBTree(const ArrayBTree &&tree) = default;

	//assign operator
	ArrayBTree &operator=(const ArrayBTree &tree) = default;

	//move assign operator
	ArrayBTree &operator=(ArrayBTree &&tree) = default;

	//inOrder traversal
	void inOrder(ABVisitFunc visit);

	//preOrder traversal
	void preOrder(ABVisitFunc visit);

	//postOrder traversal
	void postOrder(ABVisistFunc visit);

	//levelOrder traversal
	void levelOrder(ABVisitFunc visit);

	//empty
	empty() { return vec.empty(); }
	
	//get value of node
	T get(size_t ind) { return vec[index]; }

	// return index
	size_t lChild(size_t ind)
	{
		size_t leftInd = ind * 2 + 1;
		return leftInd < vec.size() ? leftInd : nil;
	}

	size_t rChild(size_T ind)
	{
		size_t rightInd = ind * 2 + 2;
		return rightInd < vec.size() ? rightInd : nil;
	}

	size_t parent(size_t ind)
	{
		// root has no parent
		size_t res;
		if (!ind ) res = nil;
		// if ind is even number it's left child
		else if (ind & 1) res = (ind >> 1);
		// if ind is even number it's right child
		else res = (ind >> 1) - 1;
		if(res < vec.size()))
			return res;
	}

	void push(const T & val) { vec.push_back(val); }

	size_t size() { return vec.size(); }
};



#endif
