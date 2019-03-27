#include "MTGraph.h"
#include "AdjGraph.h"
#include "OLGraph.h"
#include <iostream>
#include <list>
#include <vector>
#include <utility>
#include <fstream>

using namespace std;

void readGraph(Graph<char, int> *graph)
{
	fstream f("C:\\Users\\XHWhy\\Documents\\WORK\\数据结构\\homework3\\graph.txt");
	if (!f.is_open())
	{
		exit(0);
	}
	size_t n, e;
	f >> n >> e;
	for (auto i = 0; i != n; ++i)
	{
		char ch;
		f >> ch;
		graph->addVertex(ch);
	}

	for (auto j = 0; j != e; ++j)
	{
		size_t n1, n2;
		int cost;
		f >> n1 >> n2 >> cost;
		graph->addEdge(n1, n2, cost);
	}
	f.close();
}

int main()
{
	MTGraph<char, int> graph1;
	AdjGraph<char, int> graph2;
	OLGraph<char, int> graph3;
	readGraph(&graph1);
	readGraph(&graph2);
	readGraph(&graph3);
	auto res1 = graph1.prim();
	auto res2 = graph2.prim();
	auto res3 = graph3.kruskal();
	cout << "Prim Algorithm\n";
	for (auto ele : res1)
	{
		cout << "( " << ele.first << " , " << ele.second << " ) ";
	}
	cout << endl;

	cout << "Prim Algorithm(with heap)\n";
	for (auto ele : res2)
	{
		cout << "( " << ele.first << " , " << ele.second << " ) ";
	}
	cout << endl;

	cout << "Kruskal Algorithm(with MFSet)\n";
	for (auto ele : res2)
	{
		cout << "( " << ele.first << " , " << ele.second << " ) ";
	}
	cout << endl;
	cin.get();
	return 0;
}