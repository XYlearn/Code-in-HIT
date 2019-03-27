#ifndef _AVL_TREE_H_
#define _AVL_TREE_H_

#include <stack>	// for std::stack
#include <utility>	// for std::pair, std::make_pair
#include <algorithm>	// for std::max
#include <iostream>	
#include <functional>
#include <vector>

using std::stack;
using std::vector;
using std::pair;
using std::make_pair;
using std::max;
using std::cout;
using std::endl;

template <typename T, typename K>
struct AVLNode
{
public:

	AVLNode(const T &data, const K &key) :
		data(data), key(key), lChild(nullptr), 
		rChild(nullptr), height (1) {}

	AVLNode<T, K> *lChild;	// left child
	AVLNode<T, K> *rChild;	// right child

	const T & getData() { return data; }
	void setData(const T& data) { this->data = data; }
	const T & getKey() { return key; }
	void setKey(const K& key) { this->key = key; }
	int getHeight() { return height; }
	void setHeight(int height) { this->height = height; }

private:
	T data;	// data
	K key;	// key
	int height;
};

template <typename T, typename K>
using AVLNodePtr = AVLNode<T, K> *;

template <typename T, typename K>
using SearchResult = pair<AVLNodePtr<T, K>, stack<AVLNodePtr<T, K>>>;

// calculate height of AVLNode
template <typename T, typename K>
int height(AVLNodePtr<T, K> np)
{
	if (nullptr == np)
		return 0;
	return np->getHeight();
}

template<typename T, typename K>
class AVLTree
{

protected:
	AVLNode<T, K> *root;	// root of tree

	// when unbalenced node in lChild of lChild;rotate clockwise
	void _rotate_ll(AVLNodePtr<T, K> & node);

	// when unbalenced node is rChild of rChild;rotate counter clockwise
	void _rotate_rr(AVLNodePtr<T, K> & node);

	// when unbalenced node in lChild of rChild;rotate clockwise and then counter clockwise
	void _rotate_rl(AVLNodePtr<T, K> & node);

	// when unbalenced node in rChild of lChild;rotate counter clockwise and then clockwise
	void _rotate_lr(AVLNodePtr<T, K> & node);

	// search for key value and return AVLNodePtr and search_stack
	SearchResult<T, K> _search(const K &key, bool ret_path = false);

	// insert node
	bool _add(AVLNodePtr<T, K> &np, const T &data, const K& key);

	bool _del(AVLNodePtr<T, K> &np, const K&key);

	AVLNodePtr<T, K> _find_max(AVLNodePtr<T, K> np);

	pair<AVLNodePtr<T, K>, AVLNodePtr<T, K>> _find_max_prev(AVLNodePtr<T, K> np);

	AVLNodePtr<T, K> _find_min(AVLNodePtr<T, K> np);

	pair<AVLNodePtr<T, K>, AVLNodePtr<T, K>> _find_min_prev(AVLNodePtr<T, K> np);

public:
	virtual ~AVLTree()
	{
		while (root)
			_del(root, root->getKey());
	}

	AVLTree() = default;

	// add a node with data if success return true, if key exist return false
	bool add(const T &data, const K &key) { return _add(root, data, key); }

	// update data according key and return true if key exist else return false
	bool update(const T &data, const K &key) 
	{
		auto res = _search(key, false);
		if (res.first == nullptr)
			return false;
		else
		{
			res.first->setData(data);
			return true;
		}
	}

	// delete a node according to key and return true else return false
	bool del(const K &key) { return _del(root, key); }

	// search for data accroding to key
	T search(const K &key){	return _search(key, false).first->getData(); }

	vector<pair<T, K>> sort();

	// print current tree
	void print();
};


/*
 * Definations
 */

template <typename T, typename K>
void AVLTree<T, K>::_rotate_ll(AVLNodePtr<T, K> & node)
{
	AVLNodePtr<T, K> temp = node;
	node = node->lChild;
	temp->lChild = node->rChild;
	node->rChild = temp;

	// update height
	temp = node->rChild;
	temp->setHeight(max(height(temp->lChild), height(temp->rChild)) + 1);
	node->setHeight(max(height(node->lChild), height(node->rChild)) + 1);
}

template <typename T, typename K>
void AVLTree<T, K>::_rotate_rr(AVLNodePtr<T, K> & node)
{
	AVLNodePtr<T, K> temp = node;
	node = node->rChild;
	temp->rChild = node->lChild;
	node->lChild = temp;

	// update height
	temp = node->lChild;
	temp->setHeight(max(height(temp->lChild), height(temp->rChild)) + 1);
	node->setHeight(max(height(node->lChild), height(node->rChild)) + 1);
}

template <typename T, typename K>
void AVLTree<T, K>::_rotate_lr(AVLNodePtr<T, K> & node)
{
	_rotate_rr(node->lChild);
	_rotate_ll(node);
}

template <typename T, typename K>
void AVLTree<T, K>::_rotate_rl(AVLNodePtr<T, K> & node)
{
	_rotate_ll(node->rChild);
	_rotate_rr(node);
}

template<typename T, typename K>
SearchResult<T, K> AVLTree<T, K>::_search(const K &key, bool ret_path)
{
	AVLNodePtr<T, K> curr = root;
	stack<AVLNodePtr<T, K>> search_stack;
	while (curr->lChild || curr->rChild)
	{
		// found
		if (key == curr->getKey())
			return make_pair(curr, search_stack);
		// key is less
		else if (key < curr->getKey())
		{
			// update stack
			if(ret_path)
				search_stack.push(curr);
			curr = curr->lChild;
		}
		// key is larger
		else
		{
			// update stack
			if(ret_path)
				search_stack.push(curr);
			curr = curr->rChild;
		}
	}
	// if not found
	curr = nullptr;
	return make_pair(curr, search_stack);
}

template <typename T, typename K>
bool AVLTree<T, K>::_add(AVLNodePtr<T, K> &np, const T &data, const K& key)
{
	bool success;
	if (nullptr == np)
	{
		np = new AVLNode<T, K>(data, key);
		return true;
	}
	else if (key < np->getKey())
	{
		success = _add(np->lChild, data, key);
		// left
		if (height(np->lChild) - height(np->rChild) == 2)
		{
			// left left
			if (height(np->lChild->lChild) > height(np->lChild->rChild))
				_rotate_ll(np);
			// left right
			else
				_rotate_lr(np);
		}
	}
	else if (key > np->getKey())
	{
		success = _add(np->rChild, data, key);
		// right
		if (height(np->lChild) - height(np->rChild) == -2)
		{
			// right left
			if (height(np->rChild->lChild) > height(np->rChild->rChild))
				_rotate_rl(np);
			else
				_rotate_rr(np);
		}
	}
	// repeated
	else
	{
		return false;
	}
	// recalculate height
	np->setHeight(max(height(np->lChild), height(np->rChild)) + 1);
	return success;
}

template <typename T, typename K>
bool AVLTree<T, K>::_del(AVLNodePtr<T, K> &np, const K &key)
{
	bool success;
	if (np == nullptr)
		return false;
	// find node to delete
	else if (key == np->getKey())
	{
		if (np->lChild || np->rChild)
		{
			// replace np
			if (height(np->lChild) > height(np->rChild))
			{
				AVLNodePtr<T, K> lmaxp = _find_max(np->lChild);
				np->setData(lmaxp->getData());
				np->setKey(lmaxp->getKey());
				_del(np->lChild, lmaxp->getKey());
			}
			else
			{
				AVLNodePtr<T, K> rminp = _find_min(np->rChild);
				np->setData(rminp->getData());
				np->setKey(rminp->getKey());
				_del(np->rChild, rminp->getKey());
			}
		}
		else
		{
			delete np;
			np = nullptr;
		}
		return true;
	}
	else if (key < np->getKey())	// equal to insert right
	{
		success = _del(np->lChild, key);
		if (height(np->lChild) - height(np->rChild) == -2)
		{
			// same as insert right left
			if (height(np->rChild->lChild) > height(np->rChild->rChild))
				_rotate_rl(np);
			// same as insert right right
			else
				_rotate_rr(np);
		}
	}
	else if (key > np->getKey())	// equal to insert left
	{
		success = _del(np->rChild, key);
		if (height(np->lChild) - height(np->rChild) == 2)
		{
			// same as insert right right
			if (height(np->lChild->lChild) >= height(np->lChild->rChild))
				_rotate_ll(np);
			// same as insert left right
			else
				_rotate_lr(np);
		}
	}
	np->setHeight(max(height(np->lChild), height(np->rChild)) + 1);
	return success;
}

template <typename T, typename K>
AVLNodePtr<T, K> AVLTree<T, K>::_find_max(AVLNodePtr<T, K> np)
{
	if (nullptr == np)
		return nullptr;
	AVLNodePtr<T, K> res = np;
	while (res->rChild)
		res = res->rChild;
	return res;
}

template <typename T, typename K>
pair<AVLNodePtr<T, K>, AVLNodePtr<T, K>> AVLTree<T, K>::_find_max_prev(AVLNodePtr<T, K> np)
{
	if (nullptr == np)
		return make_pair(nullptr, nullptr);
	AVLNodePtr<T, K> p = np;
	if (nullptr == p->rChild)
		return make_pair(p, nullptr);
	AVLNodePtr<T, K> prev = p;
	p = p->rChild;
	while (p->rChild)
	{
		prev = p;
		p = p->rChild;
	}
	return make_pair(p, prev);
}

template <typename T, typename K>
AVLNodePtr<T, K> AVLTree<T, K>::_find_min(AVLNodePtr<T, K> np)
{
	if (nullptr == np)
		return nullptr;
	AVLNodePtr<T, K> res = np;
	while (res->lChild)
		res = res->lChild;
	return res;
}

template <typename T, typename K>
pair<AVLNodePtr<T, K>, AVLNodePtr<T, K>> AVLTree<T, K>::_find_min_prev(AVLNodePtr<T, K> np)
{
	if (nullptr == np)
		return make_pair(nullptr, nullptr);
	AVLNodePtr<T, K> p = np;
	if (nullptr == p->lChild)
		return make_pair(p, nullptr);
	AVLNodePtr<T, K> prev = p;
	p = p->lChild;
	while (p->lChild)
	{
		prev = p;
		p = p->lChild;
	}
	return make_pair(p, prev);
}

template <typename T, typename K>
vector<pair<T, K>> AVLTree<T, K>::sort()
{
	vector<pair<T, K>> res;
	std::function<void(AVLNodePtr<T, K>)> inOrder
		= [&](AVLNodePtr<T, K> p)
	{
		if (nullptr == p)
			return;
		inOrder(p->lChild);
		res.push_back(make_pair(p->getData(), p->getKey()));
		inOrder(p->rChild);
	};
	inOrder(root);
	return res;
}

template <typename T, typename K>
void AVLTree<T, K>::print()
{
	std::function<void(AVLNodePtr<T, K>)> inOrder
		= [&](AVLNodePtr<T, K> p)
	{
		if (nullptr == p)
			return;
		cout << "[";
		inOrder(p->lChild);
		cout << (K)p->getKey() << "," << (T)p->getData();
		inOrder(p->rChild);
		cout << "]";
	};
	inOrder(root);
}

#endif
