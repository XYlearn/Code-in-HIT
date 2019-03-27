#include "MTGraph.h"
#include "AdjGraph.h"
#include "OLGraph.h"
#include <iostream>
#include <fstream>
#include <sstream>

using namespace std;

void readGraph(Graph<char> &graph)
{
	fstream f("graph.txt");
	size_t n, e;
	f >> n >> e;
	for (auto i = 0; i != n; ++i)
	{
		char ch;
		f >> ch;
		graph.addVertex(ch);
	}

	for (auto j = 0; j != e; ++j)
	{
		size_t n1, n2;
		f >> n1 >> n2;
		graph.addEdge(n1, n2);
	}
	f.close();
}

void printMenu()
{
	cout << "Menu:" << endl;
	cout << "0.exit" << endl;
	cout << "1.read MTGraph from file" << endl;
	cout << "2.convert to MTGraph" << endl;
	cout << "3.convert to AjdGraph" << endl;
	cout << "4.convert to OLGraph" << endl;
	cout << "5.dfs current graph" << endl;
	cout << "6.bfs current graph" << endl;
	cout << "7.rec_dfs current graph" << endl;
	cout << "8.rec_bfs current graph" << endl;
}

enum GraphType
{
	MT_GRAPH,
	ADJ_GRAPH,
	OL_GRAPH
};

void interactive()
{
	GVisitFunc<char> visit = [](char ch)
	{
		cout << ch << " ";
	};
	Graph<char> *graph = new MTGraph<char>();
	Graph<char> *temp = nullptr;
	GraphType type = MT_GRAPH;
	while (true)
	{
		printMenu();
		cout << ">>";
		int choice;
		cin >> choice;
		switch (choice)
		{
		case 0:
			delete graph;
			exit(0);
		case 1:
			readGraph(*graph);
			break;
		case 2:
			switch (type)
			{
			case MT_GRAPH:
				break;
			case ADJ_GRAPH:
				temp = graph;
				graph = &dynamic_cast<AdjGraph<char, double> *>(graph)->convert2MT();
				delete temp;
				break;
			case OL_GRAPH:
				temp = graph;
				graph = &dynamic_cast<OLGraph<char, double> *>(graph)->convert2MT();
				delete temp;
				break;
			default:
				break;
			}
			type = MT_GRAPH;
			break;
		case 3:
			switch (type)
			{
			case MT_GRAPH:
				temp = graph;
				graph = &dynamic_cast<MTGraph<char> *>(graph)->convert2Adj();
				delete temp;
				break;
			case ADJ_GRAPH:
				break;
			case OL_GRAPH:
				temp = graph;
				graph = &dynamic_cast<OLGraph<char, double> *>(graph)->convert2Adj();
				delete temp;
				break;
			default:
				break;
			}
			type = ADJ_GRAPH;
			break;
		case 4:
			switch (type)
			{
			case MT_GRAPH:
				temp = graph;
				graph = &dynamic_cast<MTGraph<char> *>(graph)->convert2OL();
				delete temp;
				break;
			case ADJ_GRAPH:
				temp = graph;
				graph = &dynamic_cast<AdjGraph<char, double> *>(graph)->convert2OL();
				delete temp;
				break;
			case OL_GRAPH:
				break;
			default:
				break;
			}
			type = OL_GRAPH;
			break;
		case 5:
			graph->dfs(visit);
			cout << endl;
			break;
		case 6:
			graph->bfs(visit);
			cout << endl;
			break;
		case 7:
			graph->rec_dfs(visit);
			cout << endl;
			break;
		case 8:
			graph->rec_bfs(visit);
			cout << endl;
			break;
		default:
			break;
		}
	}
}

int main()
{
	interactive();

	return 0;
}
