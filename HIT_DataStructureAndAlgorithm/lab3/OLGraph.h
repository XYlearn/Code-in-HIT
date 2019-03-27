#ifndef _OL_GRAPH_H_
#define _OL_GRAPH_H_

#include <vector>	// for std::vector
#include <list>		// for std::list
#include <stack>	// for std::stack
#include <queue>	// for std::queue
#include <algorithm>	//for max,min
#include "Graph.h"
#include "AdjGraph.h"
#include "MTGraph.h"

template <typename T> class MTGraph;
template <typename T, typename W> class AdjGraph;

using std::vector;
using std::list;
using std::stack;
using std::queue;
using std::max;
using std::min;

template<typename W>
struct EBox;

//边类
template<typename W>
struct EBox
{
	bool visit_mark;
	size_t ivex, jvex;
	EBox<W> * ilink, * jlink;
	W info;

	EBox(size_t ivex, size_t jvex) :ivex(ivex), jvex(jvex), ilink(nullptr), jlink(nullptr) {}
};

//顶点类
template<typename T, typename W>
struct VBox
{
	T vertex;
	EBox<W> *first;

	VBox(const T & vertex) : vertex(vertex), first(nullptr) {}
};

//邻接多重表类
template<typename T, typename W>
class OLGraph : public Graph<T>
{
private:
	vector<VBox<T, W>> vertexes;	
	size_t n;	//vertex num
	size_t e;	//edge num

	vector<bool> empty_table;	//空闲编号标志表
	list<size_t> empty_indexes;	//空闲编号表

	// find minimun index that is not empty return -1 if not found
	size_t _find_not_empty()
	{
		// no vertex
		if (n == 0)
			return -1;
		size_t beg = 0;
		// find a vertex that is not empty
		for (; beg != vertexes.size() && empty_table[beg]; ++beg);

		return (beg == vertexes.size()) ? -1 : beg;
	}

	bool _out_of_range(size_t index) { return index < vertexes.size(); }

	bool _is_empty_index(size_t index) { return empty_table[index]; }

	// check if there is edge between 2 param(doesn't check if they are valid)
	bool _is_edge(size_t node_ind1, size_t node_ind2)
	{
		for (auto p = vertexes[node_ind1].first; p != nullptr; p = p->ilink)
			if (p->jvex == node_ind2)
				return true;
		for (auto p = vertexes[node_ind1].first; p != nullptr; p = p->jlink)
			if (p->ivex == node_ind2)
				return true;
		return false;
	}

public:
	virtual ~OLGraph() { clear(); }

	AdjGraph<T, W> &convert2Adj();

	MTGraph<T> &convert2MT();

	//添加节点
	void addVertex(const T & data);

	//删除边
	void delVertex(size_t index);

	//添加带权边
	void addEdge(size_t node_ind1, size_t node_ind2, const W & weight);

	//添加边,权值默认为1
	void addEdge(size_t node_ind1, size_t node_ind2) { addEdge(node_ind1, node_ind2, (W)1); }

	//根据边连接的两个顶点编号删除边
	void delEdge(size_t node_ind1, size_t node_ind2);

	//清空图
	void clear();

	//深度优先搜索
	void dfs(GVisitFunc<T> visit);

	//广度优先搜索
	void bfs(GVisitFunc<T> visit);

	//递归深搜
	void rec_dfs(GVisitFunc<T> visit);

	//递归广搜
	void rec_bfs(GVisitFunc<T> visist);

	//判断编号是否有效
	bool isValid(size_t ind)
	{
		return _out_of_range(ind) && !_is_empty_index(ind);
	}

	bool isEdge(size_t node_ind1, size_t node_ind2)
	{
		if (!isValid(node_ind1) || !isValid(node_ind2))
			return false;
		return _is_edge(node_ind1, node_ind2);
	}

	void addEmpty()
	{
		T temp;
		size_t index = vertexes.size();
		VBox<T, W> v(temp);
		vertexes.push_back(v);
		empty_indexes.push_back(index);
		empty_table.push_back(true);
	}
};

template <typename T, typename W>
void OLGraph<T, W>::addVertex(const T& vertex)
{
	//increase n
	++n;

	if (empty_indexes.empty())
	{
		// add to vertexes
		VBox<T, W> vbox(vertex);
		vertexes.push_back(vbox);

		// add a none empty index
		empty_table.push_back(false);
	}
	else
	{
		// find the empty index
		size_t index = empty_indexes.front();
		empty_table[index] = false;
		
		// put the new vertex to the index
		vertexes[index].vertex = vertex;
		vertexes[index].first = nullptr;
	}
}

template <typename T, typename W>
void OLGraph<T, W>::delVertex(size_t index)
{
	if (!isValid(index))
		return;
	//decrease n
	--n;

	// delete relative edges
	EBox<W> *edge = nullptr;
	while (edge = vertexes[index].first)
		delEdge(edge->ivex, edge->jvex);

	// set index to empty
	empty_indexes.push_back(index);
	empty_table[index] = true;

}

template <typename T, typename W>
void OLGraph<T, W>::addEdge(size_t node_ind1, size_t node_ind2, const W & weight)
{
	if (!isValid(node_ind1) || !isValid(node_ind2))
		return;
	if (_is_edge(node_ind1, node_ind2))
		return;
	// increase e
	++e;
	// add EBox. (min one is ivex)
	size_t n1 = min(node_ind1, node_ind2);
	size_t n2 = max(node_ind2, node_ind1);
	auto p = new EBox<W>(n1, n2);
	p->ilink = vertexes[n1].first;
	p->jlink = vertexes[n2].first;
	vertexes[n1].first = vertexes[n2].first = p;
}

template <typename T, typename W>
void OLGraph<T, W> ::delEdge(size_t node_ind1, size_t node_ind2)
{
	// TODO
	if (!isValid(node_ind1) || !isValid(node_ind2))
		return;
	if (!_is_edge(node_ind1, node_ind2))
		return;
	//decrease e
	--e;

	size_t n1 = min(node_ind1, node_ind2);
	size_t n2 = min(node_ind1, node_ind2);

	EBox<W> *pre = nullptr;
	auto curr1 = vertexes[n1].first;
	for (; curr1 != nullptr; curr1 = curr1->ilink)
	{
		if (curr1->jvex == n2)
		{
			if (nullptr == pre)
				vertexes[n1].first = curr1->ilink;
			else
				pre->ilink = curr1->ilink;
			break;
		}
		pre = curr1;
	}

	auto curr2 = vertexes[n2].first;
	for (; curr2 != nullptr && curr2->jlink != curr1; curr2 = curr2->jlink);
	if (nullptr == curr2)
		vertexes[n2].first = nullptr;
	else
		curr2->jlink = curr1->jlink;

	//delete edge
	delete curr1;
}

template <typename T, typename W>
void OLGraph<T, W> ::clear()
{
	// set n and e to 0
	n = e = 0;
	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (_is_empty_index(index))
			continue;
		EBox<W> *edge = nullptr;
		while (edge = vertexes[index].first)
			delEdge(edge->ivex, edge->jvex);
	}

	vertexes.clear();
	empty_indexes.clear();
	empty_table.clear();
}

template <typename T, typename W>
void OLGraph<T, W> ::dfs(GVisitFunc<T> visit)
{
	if (0 == n)
		return;

	size_t index = _find_not_empty();
	vector<bool> visited(vertexes.size());

	stack<size_t> visit_stack;

	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		visit_stack.push(index);
		visited[index] = true;

		while (!visit_stack.empty())
		{
			size_t n1 = visit_stack.top();
			visit_stack.pop();
			visit(vertexes[n1].vertex);

			for (auto p = vertexes[n1].first; p != nullptr; p = p->ilink)
			{
				size_t n2 = p->jvex;
				if (visited[n2])
					continue;
				visit_stack.push(n2);
				visited[n2] = true;
			}
			for (auto p = vertexes[n1].first; p != nullptr; p = p->jlink)
			{
				size_t n2 = p->ivex;
				if (visited[n2])
					continue;
				visit_stack.push(n2);
				visited[n2] = true;
			}
		}
	}
}

template <typename T, typename W>
void OLGraph<T, W> ::bfs(GVisitFunc<T> visit)
{
	if (0 == n)
		return;

	size_t index = _find_not_empty();
	vector<bool> visited(vertexes.size());

	queue<size_t> visit_queue;

	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		visit_queue.push(index);
		visited[index] = true;

		while (!visit_queue.empty())
		{
			size_t n1 = visit_queue.front();
			visit_queue.pop();
			visit(vertexes[n1].vertex);

			for (auto p = vertexes[n1].first; p != nullptr; p = p->ilink)
			{
				size_t n2 = p->jvex;
				if (visited[n2])
					continue;
				visit_queue.push(n2);
				visited[n2] = true;
			}
			for (auto p = vertexes[n1].first; p != nullptr; p = p->jlink)
			{
				size_t n2 = p->ivex;
				if (visited[n2])
					continue;
				visit_queue.push(n2);
				visited[n2] = true;
			}
		}
	}
}

template <typename T, typename W>
void OLGraph<T, W> ::rec_dfs(GVisitFunc<T> visit)
{
	if (0 == n)
		return;

	vector<bool> visited(vertexes.size());

	function<void(size_t)> rec = [&](size_t index)
	{
		visit(vertexes[index].vertex);

		for (auto p = vertexes[index].first; p != nullptr; p = p->ilink)
		{
			size_t n2 = p->jvex;
			if (visited[n2])
				continue;
			visited[n2] = true;
			rec(n2);
		}

		for (auto p = vertexes[index].first; p != nullptr; p = p->jlink)
		{
			size_t n2 = p->ivex;
			if (visited[n2])
				continue;
			visited[n2] = true;
			rec(n2);
		}
	};

	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		rec(index);
	}
}

template <typename T, typename W>
void OLGraph<T, W> ::rec_bfs(GVisitFunc<T> visit)
{
	if (0 == n)
		return;

	vector<bool> visited(vertexes.size());

	queue<size_t> visit_queue;

	auto rec = [&]()
	{
		size_t n1 = visit_queue.front();
		visit_queue.pop();
		visit(vertexes[n1].vertex);

		for (auto p = vertexes[n1].first; p != nullptr; p = p->ilink)
		{
			size_t n2 = p->jvex;
			if (visited[n2])
				continue;
			visit_queue.push(n2);
			visited[n2] = true;
		}
		for (auto p = vertexes[n1].first; p != nullptr; p = p->jlink)
		{
			size_t n2 = p->ivex;
			if (visited[n2])
				continue;
			visit_queue.push(n2);
			visited[n2] = true;
		}
	};

	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		visit_queue.push(index);
		visited[index] = true;
		rec();
	}
}

template <typename T, typename W>
MTGraph<T> &OLGraph<T, W>::convert2MT()
{
	MTGraph<T> *graph = new MTGraph<T>();
	// add vertexes
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
		{
			graph->addEmpty();
			continue;
		}
		graph->addVertex(vertexes[i].vertex);
	}
	// add edges
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
			continue;
		for (auto p = vertexes[i].first; p != nullptr; p = p->ilink)
			graph->addEdge(p->ivex, p->jvex);
	}

	return *graph;
}

template<typename T, typename W>
AdjGraph<T, W> &OLGraph<T, W>::convert2Adj()
{
	AdjGraph<T, W> *graph = new AdjGraph<T, W>();
	// add vertexes
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
		{
			graph->addEmpty();
			continue;
		}
		graph->addVertex(vertexes[i].vertex);
	}
	// add edges
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
			continue;
		for (auto p = vertexes[i].first; p != nullptr; p = p->ilink)
			graph->addEdge(p->ivex, p->jvex, p->info);
	}

	return *graph;
}

#endif