#ifndef _BTREE_H_
#define _BTREE_H_

#include <cmath>	//for log2
#include <utility>	//for std::pair
#include <iostream>	//for std::string
#include <functional> //for std::function
#include <string>	//for std::istringstream
#include <sstream>	//for istringstream
#include <queue>	//for queue
#include <stack>

//if Recursion is defined the following function will be recursive
//recursive functions : inOrder, preOrder, postOrder, ~BTree
//#define RECURRSIVE

template <typename T>
class BNode;
template <typename T>
class BTree;

//declare friend function
template<typename T>
void printTree(BTree<T> &btree);

template<typename T>
BTree<T> &readFromBuf(std::string buf);

//name alias
template<typename T>
using BVisitFunc = std::function<void(BNode<T> *)>;

template<typename T>
using BBuildFunc = std::function<int(BNode<T> **)>;

template <typename T>
class BNode
{

public:
	BNode<T> *lChild;
	BNode<T> *rChild;
	
private:
	T value;

public:
	//default constructor
	BNode():lChild(nullptr),rChild(nullptr){}

	//init constructor
	BNode(T &value):value(value){}

	//get value
	T getVal() const {return value;}
	//set value
	void setVal(T &value) {this->value = value;}
};

//BTree
template <typename T>
class BTree
{

private:
	BNode<T> *root;
	int node_num;

	int _levelTravelBuild(BNode<T> **root, BBuildFunc<T> build);

public:
	friend class THTree<T>;

	//default constructor
	BTree():root(nullptr),node_num(0){}
	
	//destructor
	~BTree() { deleteNodes(); }

	//copy constructor
	BTree(const BTree &bTree) { root = copyNodes(bTree.root); }

	//move constructor
	BTree(BTree &&bTree) { root = bTree.root; bTree.root = nullptr; }

	//assign operator
	BTree<T> &operator=(const BTree &bTree) {deleteNodes(); copyNodes(bTree.root);}

	//move assign operator
	BTree<T> &operator=(BTree &&bTree) { root = bTree.root; bTree.root = nullptr;}

	//count the nodes of tree start with root
	static int countNodes(BNode<T> *root);

	//traversal in inOrder
	static void inOrder(BNode<T> *root, BVisitFunc<T> visit);
	void inOrder(BVisitFunc<T> visit) { inOrder(root, visit); }

	//traversal in preOrder
	static void preOrder(BNode<T> *root, BVisitFunc<T> visit);
	void preOrder(BVisitFunc<T> visit) { preOrder(root, visit); }

	//traversal in postOredr
	static void postOrder(BNode<T> *root, BVisitFunc<T> visit);
	void postOrder(BVisitFunc<T> visit) { postOrder(root, visit); }

	//traversal in levelOrder
	static void levelOrder(BNode<T> *root, BVisitFunc<T> visit);
	void levelOrder(BVisitFunc<T> visit) { levelOrder(root, visit); }

	//copy Nodes chain from another node chain, return the root 
	static BNode<T> * copyNodes(BNode<T> *node);

	//delete all nodes from root
	static void deleteNodes(BNode<T> *root);

	//if root is empty the tree is empty
	bool empty() { return root == nullptr; }

	//use build to build tree in preOrder
	int levelTravelBuild(BNode<T> **root, BBuildFunc<T> build);

	//delete all nodes
	void deleteNodes() {deleteNodes(root);}

	//get depth of the tree, 0 stand for empty tree depth
	int depth();

	THTree<T> &buildTHTree()
	{
		THTree<T> *thtree = new THTree<T>;
		thtree->buildFromBTree(*this);
		return *thtree;
	}

	//get root value
	const T& getVal() const {return root->getVal();}
	//set root value
	void setVal(const T &value) {root->setVal(value);}
	BNode<T> *&plNode() {return (root->lChild);}
	BNode<T> *&prNode() {return (root->rChild);}

	//print tree
	friend void printTree<T> (BTree<T> &btree);

	//read BTree from buf in preOrder
	friend BTree<T> &readFromBuf<T> (std::string buf);
};

/*Member Function definition*/

/*BTree*/

//count the nodes of tree start with root
template <typename T>
int BTree<T>::countNodes(BNode<T> *root)
{
	if (root)
		return 1 + BTree<T>::countNodes(root->lChild) + BTree<T>::countNodes(root->rChild);
	else
		return 0;
}

//traversal in inOrder
template<typename T>
void BTree<T>::inOrder(BNode<T> *root, BVisitFunc<T> visit)
{
#ifdef RECURRSIVE

	if(nullptr == root)
		return;
	inOrder(root->lChild, visit);
	visit(root);
	inOrder(root->rChild, visit);

#else

	Stack<BNode<T> *> visitStack;	//store root nodes
	BNode<T> *pBNode = root;

	//if the stack is empty, the traversal is over or the root is nullptr
	//if the root is nullptr, the pBNode is nullptr either
	while(!visitStack.empty() || pBNode != nullptr)
	{
		// loop until no lChild 
		while(nullptr != pBNode)
		{
			visitStack.push(pBNode);
			pBNode = pBNode->lChild;
		}

		if(!visitStack.empty())
		{
			//visit root
			pBNode = visitStack.pop();
			visit(pBNode);
			//visit rChild
			pBNode = pBNode->rChild;
		}
	}

#endif
}

//traversal in levelOrder
template<typename T>
void BTree<T>::levelOrder(BNode<T> *root, BVisitFunc<T> visit)
{
	if (root == nullptr)
		return;
	std::queue<BNode<T> *> visitQueue;
	BNode<T> * pBNode = root;
	visitQueue.push(pBNode);
	while (!visitQueue.empty())
	{
		pBNode = visitQueue.front();
		visitQueue.pop();
		visit(pBNode);
		if (pBNode->lChild)
			visitQueue.push(pBNode->lChild);
		if (pBNode->rChild)
			visitQueue.push(pBNode->rChild);
	}
}

//use build to build tree in preOrder
template<typename T>
int BTree<T>::levelTravelBuild(BNode<T> **root, BBuildFunc<T> build)
{
	int num = _levelTravelBuild(root, build);
	node_num += num;
	return num;
}

template<typename T> 
int BTree<T>::_levelTravelBuild(BNode<T> **root, BBuildFunc<T> build)
{
	int num = 0; //count increased node_num
	//delete nodes before build
	if (root != nullptr) 
	{
		num -= countNodes(*root);
		deleteNodes(*root);
	}

	std::queue<BNode<T> **> buildQueue;
	buildQueue.push(root);
	while (!buildQueue.empty())
	{
		BNode<T> ** pBNode = buildQueue.front();
		buildQueue.pop();
		num += build(pBNode);
		if (*pBNode)
		{
			buildQueue.push(&((*pBNode)->lChild));
			buildQueue.push(&((*pBNode)->rChild));
		}
	}
	return num;
}

//traversal in preOrder
template<typename T>
void BTree<T>::preOrder(BNode<T> *root, BVisitFunc<T> visit)
{
#ifdef RECURRSIVE

	if (root == nullptr)
		return;
	visit(root);
	preOrder(root->lChild, visit);
	preOrder(root->rChild, visit);

#else

	Stack<BNode<T> *> visitStack;
	BNode<T> * pBNode = root;
	while(nullptr != root)
	{
		//visit root
		visit(pBNode);

		//save rChild
		if(pBNode->rChild)
			visitStack.push(pBNode->rChild);
		
		//visit lChild
		if(pBNode->lChild)
			pBNode = pBNode->lChild;
		// visit rChild
		else
		{
			if(visitStack.empty())
				break;
			pBNode = visitStack.pop();
		}
	}

#endif
}

//traversal in postOredr
template<typename T>
void BTree<T>::postOrder(BNode<T> *root, BVisitFunc<T> visit)
{
#ifdef RECURRSIVE

	if (root == nullptr)
		return;
	postOrder(root->lChild, visit);
	postOrder(root->rChild, visit);
	visit(root);

#else
	if (nullptr == root) return;

	//Stack<std::pair<BNode<T> *, char>> visitStack;
	Stack<BNode<T> *> visitStack;

	std::pair<BNode<T> *, char> ele;

	BNode<T> *pBNode = root;

	visitStack.push(root);
	visitStack.push(root);
	while (!visitStack.empty())
	{
		pBNode = visitStack.pop();
		if (!visitStack.empty() && pBNode == visitStack.top())
		{
			if (pBNode->rChild)
			{
				visitStack.push(pBNode->rChild);
				visitStack.push(pBNode->rChild);
			}
			if (pBNode->lChild)
			{
				visitStack.push(pBNode->lChild);
				visitStack.push(pBNode->lChild);
			}
		}
		else
			visit(pBNode);
	}

#endif
}

//copy Nodes chain from another node chain
template<typename T>
BNode<T> * BTree<T>::copyNodes(BNode<T> *node)
{
#ifdef RECURRSIVE
	//recursive copy
	BNode<T> *root;
	if(nullptr == node)
		return nullptr;
	root = new BNode<T>(node->getVal());
	++node_num;	//increase node num
	root->lChild = copyNodes(node->lChild);
	root->rChild = copyNodes(node->rChild);
	return root;

#else

	// TODO

#endif
}

//delete all nodes
template<typename T>
void BTree<T>::deleteNodes(BNode<T> *root)
{
	BVisitFunc<T>  deleteNode = [](BNode<T> *node) {delete node;};
	postOrder(root, deleteNode);
	root = nullptr;
}

//get depth of the tree, 0 stand for empty tree depth
template<typename T>
int BTree<T>::depth()
{
	int num = node_num;
	int dep = 0;
	while(num)
	{
		++depth;
		num >>= 1;
	}
	return depth;
}

//print tree
template<> 
void printTree<char>(BTree<char> &btree)
{
	int ord = 0;	//record the visit order
	int dep = 0;
	int prev_ord = 0;
	int max_dep = log2(btree.node_num) + 1;
	void (*printSpace)(int) = 
		[](int n) {
			for(int i = 0; i != n; ++i)
				std::cout << " ";
		};

	BVisitFunc<char> printNode = 
		[&](BNode<char> *node) {
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
		std::cout << (node ? node->getVal() : ' ');
		printSpace(space);
	};
	btree.levelOrder(btree.root, printNode);
}


template <>
BTree<char> &readFromBuf<char>(std::string buf)
{
	size_t length = buf.size();
	size_t curr = 0;

	BTree<char> *btree = new BTree<char>();
	BBuildFunc<char> buildNode = 
		[&](BNode<char> **node) -> int
		{
			if (curr == length)
				return 0;
			char ch = buf[curr++];
			
			if (ch != '#')
			{
				*node = new BNode<char>(ch);
				return 1;
			}
			else
			{
				*node = nullptr;
				return 0;
			}
		};
	btree->levelTravelBuild(&(btree->root), buildNode);
	return *btree;
}

#endif
