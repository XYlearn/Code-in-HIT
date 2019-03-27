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
	const static size_t nil = -1;

public:

	//default constructor
	ArrayBTree() = default;

	//destructor
	~ArrayBTree() = default;

	//copy constructor
	ArrayBTree(const ArrayBTree &tree) = default;

	//assign operator
	ArrayBTree &operator=(const ArrayBTree &tree) = default;

	//inOrder traversal
	void inOrder(ABVisitFunc<T> visit);

	//preOrder traversal
	void preOrder(ABVisitFunc<T> visit);

	//postOrder traversal
	void postOrder(ABVisitFunc<T> visit);

	//levelOrder traversal
	void levelOrder(ABVisitFunc<T> visit);

	//empty
	bool empty() { return vec.empty(); }

	size_t size() { return vec.size(); }
	
	//get value of node
	T get(size_t ind) { return vec[ind]; }

	// return index
	size_t lChild(size_t ind)
	{
		size_t leftInd = ind * 2 + 1;
		return leftInd < vec.size() ? leftInd : nil;
	}

	size_t rChild(size_t ind)
	{
		size_t rightInd = ind * 2 + 2;
		return rightInd < vec.size() ? rightInd : nil;
	}

	size_t parent(size_t ind)
	{
		// root has no parent
		if (!ind || ind > vec.size()) return nil;
		else if (ind & 1) return (ind >> 1);
		// if ind is even number it's right child
		return (ind >> 1) - 1;
	}

	void push(const T & val) { vec.push_back(val); }
};



#endif
