
/*
 * 无向图的邻接表表示。
 *     为了节省边表存储开销，采取不包含对角线的下三角形式保存，
 * 两点之间是否有边相邻取决于edges[max(ind1,ind1)][min(ind1,
 * ind2)]是否为1。
 *	   删除顶点时为节省时间，不直接将顶点从表中删去，而是保留
 * 该点信息，将其边表中的相关项置为0，并将其编号保存在空闲编号
 * 列表中。
 *      在添加节点时先从空闲编号表中查找是否有空闲号，若有则将
 * 节点放置在该编号对应顶点表并分配边表行，若没有则在列表后添
 * 加节点编号来存放新节点。
*/
#ifndef _MT_GRAPH_H_
#define _MT_GRAPH_H_

#include <vector>	// for std::vector
#include <list>		// for std::list
#include <stack>	// for std::stack
#include <queue>	// for std::queue
#include <algorithm>	//for max,min
#include "Graph.h"
#include "AdjGraph.h"
#include "OLGraph.h"

template <typename T, typename W> class AdjGraph;
template <typename T, typename W> class OLGraph;

using std::vector;
using std::list;
using std::stack;
using std::queue;
using std::max;
using std::min;

//定义顶点表别名
template <typename T>
using VertexTable = vector<T>;

//定义边表别名
using EdgeTable = vector<vector<bool>>;

//图的邻接矩阵表示
template <typename T>
class MTGraph : public Graph<T>
{
private:
	VertexTable<T> vertexes;	//保存定点信息，下表表示编号
	EdgeTable edges;		//保存边信息
	size_t n;	//保存顶点数
	size_t e;	//保存边数

	list<size_t> empty_indexes;	//保存空闲下标编号
	vector<bool> empty_table;	//保存各编号是否空闲

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

	bool _is_empty_index(size_t index) { return empty_table[index]; }

public:
	virtual ~MTGraph() { clear(); }

	AdjGraph<T, double> &convert2Adj();

	OLGraph<T, double> &convert2OL();

	//添加节点
	void addVertex(const T & data);	

	//删除边
	void delVertex(size_t index);

	//添加边
	void addEdge(size_t node_ind1, size_t node_ind2);

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

	//判断边是否树于图
	bool isEdge(size_t ind1, size_t ind2)
	{
		if (ind1 == ind2) return true;
		return edges[std::max(ind1, ind2)][std::min(ind1, ind2)] == 1;
	}

	bool isEmpty(size_t index) { return empty_table[index]; }

	bool isValid(size_t index) { return index < vertexes.size() && !isEmpty(index); }

	void addEmpty() 
	{
		T temp;
		size_t ind = vertexes.size();
		vertexes.push_back(temp);
		vector<bool> te;
		edges.push_back(te);
		empty_table.push_back(true);
		empty_indexes.push_back(ind);
	}

};

//添加节点
template <typename T>
void MTGraph<T>::addVertex(const T & node_data)
{
	if (empty_indexes.empty())
	{
		// increase node_num
		++n;

		// push zero edge line
		vector<bool> temp(n-1, false);
		edges.push_back(temp);

		// push back to VertexTable
		vertexes.push_back(node_data);

		// set empty_table to false
		empty_table.push_back(false);
	}
	// if empty index exist
	else
	{
		// incrase node num
		++n;
	
		// get index
		auto index = empty_indexes.front();
		empty_indexes.pop_front();

		// set empty table to false
		empty_table[index] = 0;

		// realloc edge
		vector<bool> temp((index == 0) ? 0 : (index-1));
		edges[index] = std::move(temp);

	}

}

//删除节点
template <typename T>
void MTGraph<T>::delVertex(size_t index)
{
	int del_num = 0;	//count num of eadges to delete
	//set relative edge to 0
	for (auto i = 0; i != index; ++i)
		if (edges[index][i])
			del_num += 1;
	for (auto i = index+1; i < edges.size(); ++i)
	{
		if (edges[i][index])
		{
			del_num += 1;
			edges[i][index] = 0;
		}
	}

	// reset the edges num
	e -= del_num;

	// decrease vertex number
	--n;

	// clear the edge to save space
	edges[index].clear();

	//put the deleted index to empty_indexes
	empty_indexes.push_back(index);
	// set empty_table to true
	empty_table[index] = true;
}

//添加边
template <typename T>
void MTGraph<T>::addEdge(size_t node_ind1, size_t node_ind2)
{
	if (!isValid(node_ind1) || !isValid(node_ind2) || node_ind1 == node_ind2)
		return;
	// increase edge num
	++e;
	// add edge to edges
	edges[max(node_ind1, node_ind2)][min(node_ind1, node_ind2)] = true;
	
}

//删除边
template <typename T>
void MTGraph<T>::delEdge(size_t ind1, size_t ind2)
{
	if (isEmpty(ind1) || isEmpty(ind2))
		return;

	if (!isEdge(ind1, ind2) || ind1 == ind2)
		return;

	--e;
	edges[max(ind1, ind2)][min(ind1, ind2)] = false;
}

template <typename T>
void MTGraph<T>::clear()
{
	vertexes.clear();
	edges.clear();
	empty_indexes.clear();
	empty_table.clear();
	n = e = 0;
}

template <typename T>
void MTGraph<T>::dfs(GVisitFunc<T> visit)
{
	// no nodes to visit
	if (n == 0)
		return;

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
			// visit
			size_t n1 = visit_stack.top();
			visit_stack.pop();
			visit(vertexes[n1]);

			for (size_t n2 = 0; n2 != vertexes.size(); ++n2)
			{
				if (isEmpty(n2) || visited[n2])
					continue;
				if (isEdge(n1, n2))
				{
					visit_stack.push(n2);
					visited[n2] = true;
				}
			}
		}
	}

}

template <typename T>
void MTGraph<T>::bfs(GVisitFunc<T> visit)
{
	// no nodes to visit
	if (n == 0)
		return;

	vector<bool> visited(vertexes.size());

	queue<size_t> visit_queue;
	
	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		// push first node
		visit_queue.push(index);
		visited[index] = true;

		while (!visit_queue.empty())
		{
			size_t n1 = visit_queue.front();
			visit_queue.pop();
			visit(vertexes[n1]);

			// push node linked with node ind
			for (size_t n2 = 0; n2 != vertexes.size(); ++n2)
			{
				if (visited[n2] || !isEdge(n1, n2))
					continue;
				visit_queue.push(n2);
				visited[n2] = true;
			}
		}
	}
}

template <typename T>
void MTGraph<T>::rec_dfs(GVisitFunc<T> visit)
{
	// no nodes to visit
	if (n == 0)
		return;

	vector<bool> visited(vertexes.size());

	function<void(size_t)> rec = [&](size_t n1)
	{
		visit(vertexes[n1]);
		visited[n1] = true;
		for (size_t n2 = 0; n2 != visited.size(); ++n2)
		{
			if (isEmpty(n2) || visited[n2])
				continue;
			if (isEdge(n1, n2))
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

template <typename T>
void MTGraph<T>::rec_bfs(GVisitFunc<T> visit)
{
	// no nodes to visit
	if (n == 0)
		return;

	vector<bool> visited(vertexes.size());

	size_t n1 = _find_not_empty();
	
	// queue for bfs
	queue<size_t> visit_queue;
	
	function<void()> rec = [&]()
	{
		n1 = visit_queue.front();
		visit_queue.pop();
		visit(vertexes[n1]);

		for (size_t n2 = 0; n2 != visited.size(); ++n2)
		{
			if (isEmpty(n2) || visited[n2])
				continue;
			if (isEdge(n1, n2))
			{
				visit_queue.push(n2);
				visited[n2] = true;
			}
		}

		while (!visit_queue.empty())
			rec();
	};
	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		visit_queue.push(index);
		visited[n1] = true;
		rec();
	}
}

template<typename T>
AdjGraph<T, double> &MTGraph<T>::convert2Adj()
{
	AdjGraph<T, double> *graph = new AdjGraph<T, double>();
	// add vertexes
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
		{
			graph->addEmpty();
			continue;
		}
		graph->addVertex(vertexes[i]);
	}

	// add edges
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
			continue;
		for (size_t j = 0; j != i; ++j)
			if (isEdge(i, j))
				graph->addEdge(i, j);
	}

	return *graph;
}

template<typename T>
OLGraph<T, double> &MTGraph<T>::convert2OL()
{
	OLGraph<T, double> *graph = new OLGraph<T, double>();
	// add vertexes
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
		{
			graph->addEmpty();
			continue;
		}
		graph->addVertex(vertexes[i]);
	}

	// add edges
	for (size_t i = 0; i != vertexes.size(); ++i)
	{
		if (_is_empty_index(i))
			continue;
		for (size_t j = 0; j != i; ++j)
			if (isEdge(i, j))
				graph->addEdge(i, j);
	}

	return *graph;
}

#endif