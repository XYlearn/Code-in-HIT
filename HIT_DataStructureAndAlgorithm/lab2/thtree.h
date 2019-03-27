/*
 * An inOrder THTree
 */

#ifndef _THTREE_H_
#define _THTREE_H_

#include "btree.h"
#include <queue>	//for std::queue
#include <functional>
#include <cmath>
#include <stack>	// for stack

//using std
using std::stack;

//previous declaration
template<typename T>
class THNode;
template <typename T>
class BNode;
template<typename T>
class THTree;
template <typename T>
class BTree;

//function declaration
template<typename T>
void printTree(THTree<T> &thtree);

//name alias
template <typename T>
using THVisitFunc = std::function<void(THNode<T> *)>;

template<typename T>
class THNode
{
private:
	T value;
public:
	THNode<T> *lChild;
	THNode<T> *rChild;

	bool ltag;
	bool rtag;

	//default constructor
	THNode():lChild(nullptr), rChild(nullptr), ltag(false), rtag(false){}

	T getVal() const { return value; }
	void setVal(const T& val) { value = val; }

	THNode(const THNode<T> &) = default;
};

template<typename T>
class THTree
{
private:
	THNode<T> * head;
	int node_num;

	//build THTree from BTree
	void buildFromBTree(const BTree<T> &btree);

	//copy nodes from btree
	THNode<T> *copyNodesFromBNodes(const BNode<T> *node);

public:
	friend class BTree<T>;

	//default constructor
	THTree()
	{
		head = new THNode<T>;
		head->lChild = head;
		head->ltag = false;
		head->rChild = head;
		head->rtag = true;
	}

	~THTree()
	{
		deleteNodes();
		delete head;
	}

	//copy constructor
	THTree(const THTree<T> &thTree) = delete;

	//move constructor
	THTree(THTree<T> &&thTree) = delete;

	//assign operator
	THTree<T> &operator=(const THTree<T> &thTree) = delete;

	THTree<T> &operator=(BTree<T> &btree) 
	{ 
		deleteNodes();
		buildFromBTree(btree);
		return *this;
	}

	//move assign operator
	THTree<T> &operator=(THTree<T> &&thTree) = delete;
	
	//traversal in inOrder
	void inOrder(THVisitFunc<T> visit);

	//traversal in preOrder
	void preOrder(THVisitFunc<T> visit);

	//traversal in postOredr
	void postOrder(THVisitFunc<T> visit);

	void levelOrder(THVisitFunc<T> visit);

	//copy Nodes chain from another node chain, return the root 
	static THNode<T> * copyNodes(THNode<T> *node) = delete;

	//delete all nodes from root
	static void deleteNodes(THNode<T> *root);

	void deleteNodes() { if(!empty()) deleteNodes(head->lChild); }

	//if root is empty the tree is empty
	bool empty() { return head->ltag == false; }

	//get depth of the tree, 0 stand for empty tree depth
	int depth() = delete;

	// inOrder next node
	THNode<T> *inNext(THNode<T> *);

	// preOrder next node
	THNode<T> *preNext(THNode<T> *);

	// postOrder previous node
	THNode<T> *postPrev(THNode<T> *node);

	// postOrder next node
	THNode<T> *postNext(THNode<T> *);

	//get node_num
	int count_node() { return node_num; }

	// print Tree
	friend void printTree(THTree<T> &thtree);
};

//copy nodes from btree
template<typename T>
THNode<T> *THTree<T>::copyNodesFromBNodes(const BNode<T> *node)
{
	if (node == nullptr)
		return nullptr;
	THNode<T> *root = new THNode<T>;
	root->setVal(node->getVal());
	root->lChild = copyNodesFromBNodes(node->lChild);
	root->rChild = copyNodesFromBNodes(node->rChild);
	return root;
}

//build THTree from BTree
template<typename T>
void THTree<T>::buildFromBTree(const BTree<T> &btree)
{

	THNode<T> *pre = nullptr;
	head->lChild = copyNodesFromBNodes(btree.root);
	THNode<T> *root = head->lChild;
	if (root)
		head->ltag = true;
	else
		return;
	//inOrder recursive threading
	std::function<void(THNode<T> *)> thread = [&](THNode<T> *node)
	{
		if (node)
		{
			thread(node->lChild);
			node->ltag = (node->lChild) ? true : false;
			node->rtag = (node->rChild) ? true : false;
			if (pre)
			{
				if (pre->rtag == false)
					pre->rChild = node;
				if (node->ltag == false)
					node->lChild = pre;
			}
			pre = node;
			thread(node->rChild);
		}
	};
	thread(root);

	// bind to head
	THNode<T> *temp = root;
	while (temp->ltag) temp = temp->lChild;
	temp->lChild = head;
	temp = root;
	while (temp->rtag) temp = temp->rChild;
	temp->rChild = head;

	node_num = btree.node_num;
}

// inOrder next node
template<typename T>
THNode<T> *THTree<T>::inNext(THNode<T> *node)
{
	if (node->rtag == false)
		return node->rChild;
	else
	{
		THNode<T> *cur = node->rChild;
		while (cur->ltag) cur = cur->lChild;
		return cur;
	}
}

// preOrder next node
template<typename T>
THNode<T> *THTree<T>::preNext(THNode<T> *node)
{
	if (node->ltag)
		return node->lChild;	
	else
		return node->rChild->rChild;
}

// postOrder next node
template<typename T>
THNode<T> *THTree<T>::postNext(THNode<T> *node)
{
	if (par->rChild == node || !par->rtag)
		return par;
	// else curr is lChild and has rChild
	curr = par->rChild;
	while (curr->ltag || curr->rtag)
	{
		if (curr->ltag)
			curr = curr->lChild;
		else if (curr->rtag)
			curr = curr->rChild;
	}
	return curr;
}

//postOrder previous node
template<typename T>
THNode<T> *THTree<T>::postPrev(THNode<T> *node)
{
	if (node->rtag)
		return node->rChild;
	else
		return node->lChild;
}

//traversal in inOrder
template<typename T>
void THTree<T>::inOrder(THVisitFunc<T> visit)
{
	THNode<T> *curr = head->lChild;
	while (curr->ltag)
		curr = curr->lChild;
	while (curr != head)
	{
		visit(curr);
		curr = inNext(curr);
	}
}

//traversal in preOrder
template<typename T>
void THTree<T>::preOrder(THVisitFunc<T> visit)
{
	THNode<T> *curr = head->lChild;
	while (curr != head)
	{
		visit(curr);
		curr = preNext(curr);
	}
}

//traversal in postOredr
template<typename T>
void THTree<T>::postOrder(THVisitFunc<T> visit)
{
	stack<THNode<T> *> nodesStack;

	//rPrevOrder
	THNode<T> * curr = head->lChild;
	while (curr != head)
	{
		nodesStack.push(curr);
		// find rPrevNext node
		if (curr->rtag)
			curr = curr->rChild;
		else curr = curr->lChild->lChild;
		if (curr == head->lChild)
			break;
	}

	while (!nodesStack.empty())
	{
		visit(nodesStack.top());
		nodesStack.pop();
	}
}

template<typename T>
void THTree<T>::levelOrder(THVisitFunc<T> visit)
{
	THNode<T> *root = head->lChild;
	if (root == nullptr)
		return;
	std::queue<THNode<T> *> visitQueue;
	THNode<T> * pTHNode = root;
	visitQueue.push(pTHNode);
	while (!visitQueue.empty())
	{
		pTHNode = visitQueue.front();
		visitQueue.pop();
		visit(pTHNode);
		if (pTHNode->ltag)
			visitQueue.push(pTHNode->lChild);
		if (pTHNode->rtag)
			visitQueue.push(pTHNode->rChild);
	}
}

//delete all nodes from root
template<typename T>
void THTree<T>::deleteNodes(THNode<T> *root)
{
	if (root)
	{
		if(root->ltag)
			deleteNodes(root->lChild);
		if(root->rtag)
			deleteNodes(root->rChild);
		delete root;
	}
}

// print Tree
template<>
void printTree<char>(THTree<char> &thtree)
{
	int ord = 0;	//record the visit order
	int dep = 0;
	int prev_ord = 0;
	int max_dep = log2(thtree.count_node())+1;
	void(*printSpace)(int) =
		[](int n) {
		for (int i = 0; i != n; ++i)
			std::cout << " ";
	};

	THVisitFunc<char> printNode =
		[&](THNode<char> *node) {
		int space;
		if (++ord >> 1 == prev_ord) {
			++dep;
			prev_ord = ord;
			std::cout << std::endl;
			space = (1 << (max_dep - dep)) - 1;
			printSpace(space);
		}
		//calc space
		space = (1 << (max_dep - dep + 1)) - 1;
		if (node)
			std::cout << node->getVal();
		else
			std::cout << ' ';
		printSpace(space);
	};
	thtree.levelOrder(printNode);
}

#endif
