#include "btree.h"
#include "thtree.h"
#include <iostream>
#include <chrono>

using namespace std;
using namespace chrono;
using chrono::microseconds;

const int COUNT = 10000;

int main()
{
	string buf = "abcdefhijklmnopqrstuvwxyz";
	BTree<char> &btree = readFromBuf<char>(buf);
	THTree<char> &thtree = btree.buildTHTree();

	printTree<char>(btree);
	cout << "\n***************************************************************\n";
	
	BVisitFunc<char> printBNode = [](BNode<char> *node) {cout << node->getVal(); };
	BVisitFunc<char> bNop = [](BNode<char> *node) {};
	THVisitFunc<char> printTHNode = [](THNode<char> *node) {cout << node->getVal(); };
	THVisitFunc<char> thNop = [](THNode<char> *node) {};

	//print Btree traversal order
	cout << "BTree preOrder:\n";
	btree.preOrder(printBNode);
	cout << endl;
	auto start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		btree.preOrder(bNop);
	auto end = system_clock::now();
	auto dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	cout << "BTree inOrder:\n";
	btree.inOrder(printBNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		btree.inOrder(bNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	cout << "BTree postOrder:\n";
	btree.postOrder(printBNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		btree.postOrder(bNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	cout << "BTree levelOrder:\n";
	btree.levelOrder(printBNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		btree.levelOrder(bNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	//print THTree traversal order
	cout << "\n--------------\n";
	cout << "THTree preOrder:\n";
	thtree.preOrder(printTHNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		thtree.preOrder(thNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	cout << "THTree inOrder:\n";
	thtree.inOrder(printTHNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		thtree.inOrder(thNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	cout << "THTree postOrder:\n";
	thtree.postOrder(printTHNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		thtree.postOrder(thNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";

	cout << "THTree levelOrder:\n";
	thtree.levelOrder(printTHNode);
	cout << endl;
	start = system_clock::now();
	for (int i = 0; i != COUNT; ++i)
		thtree.levelOrder(thNop);
	end = system_clock::now();
	dura = duration_cast<microseconds> (end - start);
	cout << "cost " << double(dura.count()) * microseconds::period::num / microseconds::period::den << "s\n";
	
	return 0;
}
