
/*
 *    图的邻接表表示方法
 * 使用空闲标号表来保存被删除的节点编号
*/

#ifndef _ADJ_GRAPH_H_
#define _ADJ_GRAPH_H_

#include "Graph.h"
#include <vector>
#include <stack>
#include <utility>
#include <algorithm>
#include <queue>
#include <list>

using std::vector;
using std::stack;
using std::queue;
using std::list;
using std::priority_queue;
using std::max;
using std::min;
using std::pair;
using std::make_pair;

// 边表节点
template <typename W>	// T 表示权值类型
struct EdgeNode
{
	size_t adj;	// 下标
	W cost;		// 边上权值
	EdgeNode<W> *next;

	EdgeNode(size_t adj, const W & cost) :adj(adj), cost(cost), next(nullptr) {}
};

// 顶点表节点
template <typename T, typename W>
struct VertexNode
{
	T vertex;	//节点数据
	EdgeNode<W> *first;

	bool delEdge(size_t index);
};

template<typename T, typename W>
bool VertexNode<T, W>::delEdge(size_t index)
{
	EdgeNode<W> *pre = nullptr;
	
	// find edge(adj == index)
	for (auto p = first; p != nullptr; p = p->next)
	{
		if (p->adj == index)
		{
			if (pre == nullptr)
				first = p->next;
			else
				pre->next = p->next;
			delete p;
			return true;
		}
		pre = p;
	}
	return false;
}

template <typename T, typename W>
class AdjGraph : public Graph<T, W>
{
private:
	vector<VertexNode<T, W>> vertexes;	//顶点表
	size_t n;	//节点数
	size_t e;	//边数

	vector<bool> empty_table;	//空闲编号表
	list<size_t> empty_indexes;	//空闲编号

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

public:
	virtual ~AdjGraph() { clear(); }

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

	//是否为边
	bool isEdge(size_t n1, size_t n2)
	{
		if (!isValid(n1) || !isValid(n2))
			return false;
		// check if n2 has edge with n1
		for (auto it = vertexes[n1].first; it != nullptr; it = it->next)
			if (it->adj == n2)
				return true;

		return false;
	}

	//判断编号是否有效
	bool isValid(size_t ind)
	{
		return _out_of_range(ind) && !_is_empty_index(ind);
	}

	void addEmpty()
	{
		T temp;
		size_t index = vertexes.size();

		VertexNode<T, W> v;
		vertexes.push_back(v);
		empty_table[index] = true;
		empty_indexes.push_back(index);
	}

	//Prim算法堆结构优化
	list<pair<size_t, size_t>> prim();
};

template<typename T, typename W>
void AdjGraph<T, W>::addVertex(const T& vertex)
{
	// increase node_num
	++n;
	if (empty_indexes.empty())
	{
		VertexNode<T, W> node;
		node.vertex = vertex;
		node.first = nullptr;
		vertexes.push_back(node);

		// add to empty_table
		empty_table.push_back(false);
	}
	else
	{
		size_t ind = _find_not_empty();
		vertexes[ind].first = nullptr;
		vertexes[ind].vertex = vertex;

		empty_table[ind] = false;
	}
}


template<typename T, typename W>
void AdjGraph<T, W> ::delVertex(size_t index)
{
	if (!isValid(index))
		return;
	// set the first of index to null
	vertexes[index].first = nullptr;

	// add to empty_indexes and set empty_table
	empty_indexes.push_back(index);
	empty_table[index] = true;

	// decrease n
	--n;

	// clear edges
	for(size_t i = 0; i != vertexes.size(); ++i)
	{
		if (!_is_empty_index(i))
		{
			//del edge and decrease e
			e -= vertexes[i].delEdge(index);
		}
	}
}

template<typename T, typename W>
void AdjGraph<T, W> ::addEdge(size_t node_ind1, size_t node_ind2, const W & weight)
{
	
	//increase e
	++e;

	// add edge to node_ind1
	auto first = vertexes[node_ind1].first;
	if (nullptr == first)
		vertexes[node_ind1].first = new EdgeNode<W>(node_ind2, weight);
	else
	{
		auto tmp = first->next;
		first->next = new EdgeNode<W>(node_ind2, weight);
		first->next->next = tmp;
	}

	// add edge to node_ind2
	first = vertexes[node_ind2].first;
	if (nullptr == first)
		vertexes[node_ind2].first = new EdgeNode<W>(node_ind1, weight);
	else
	{
		auto tmp = first->next;
		first->next = new EdgeNode<W>(node_ind1, weight);
		first->next->next = tmp;
	}
}


template<typename T, typename W>
void AdjGraph<T, W> ::delEdge(size_t node_ind1, size_t node_ind2)
{
	if (!isValid(node_ind1) || !isValid(node_ind2))
		return;
	--e;
	vertexes[node_ind1].delEdge(node_ind2);
	vertexes[node_ind2].delEdge(node_ind1);
}

template<typename T, typename W>
void AdjGraph<T, W> ::clear()
{
	vertexes.clear();
	n = e = 0;

	empty_indexes.clear();
	empty_table.clear();
}

template<typename T, typename W>
void AdjGraph<T, W> ::dfs(GVisitFunc<T> visit)
{
	if (n == 0)
		return;

	vector<bool> visited(vertexes.size());	// visited table
	stack<size_t> visit_stack;	

	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (visited[index] || _is_empty_index(index))
			continue;
		// push the first node to visit 
		visit_stack.push(index);
		visited[index] = true;

		while (!visit_stack.empty())
		{
			size_t n1 = visit_stack.top();
			visit_stack.pop();
			visit(vertexes[n1].vertex);

			for (auto p = vertexes[n1].first; p != nullptr; p = p->next)
			{
				size_t n2 = p->adj;
				// if n2 hasn't been visited
				if (visited[n2])
					continue;
				visit_stack.push(n2);
				visited[n2] = true;
			}
		}
	}
}

template<typename T, typename W>
void AdjGraph<T, W> ::bfs(GVisitFunc<T> visit)
{
	if (n == 0)
		return;
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
			visited[n1] = true;

			for (auto p = vertexes[n1].first; p != nullptr; p = p->next)
			{
				size_t n2 = p->adj;
				if (visited[n2])
					continue;
				visit_queue.push(n2);
				visited[n2] = true;
			}
		}
	}
}

template<typename T, typename W>
void AdjGraph<T, W> ::rec_dfs(GVisitFunc<T> visit)
{
	if (n == 0)
		return;

	vector<bool> visited(vertexes.size());

	function<void(size_t)> rec = [&](size_t index)
	{
		if (n == 0)
			return;

		// first visit the node
		visit(vertexes[index].vertex);
		visited[index] = true;

		// dfs to visit every node has edge with index
		for (auto p = vertexes[index].first; p != nullptr; p = p->next)
		{
			size_t n2 = p->adj;
			if (visited[n2])
				continue;
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

template<typename T, typename W>
void AdjGraph<T, W> ::rec_bfs(GVisitFunc<T> visit)
{
	if (n == 0)
		return;

	vector<bool> visited(vertexes.size());

	queue<size_t> visit_queue;
	
	function<void()> rec = [&]()
	{
		size_t index = visit_queue.front();
		visit_queue.pop();
		visit(vertexes[index].vertex);

		for (auto p = vertexes[index].first; p != nullptr; p = p->next)
		{
			size_t n2 = p->adj;
			if (visited[n2])
				continue;
			visit_queue.push(n2);
			visited[n2] = true;
		}
		rec();
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

template<typename T, typename W>
list<pair<size_t, size_t>> AdjGraph<T, W>::prim()
{
	list<pair<size_t, size_t>> res;	//result

	vector<bool> visited(vertexes.size(), false);	// if vertex is in MST it's visited

	vector<W> low_cost(vertexes.size(), std::numeric_limits<W>::max());

	priority_queue<pair<W, size_t>, vector<pair<W, size_t>>, std::greater<pair<W, size_t>>> heap;

	const size_t NIL = -1;
	// use parent array to save MST
	vector<size_t> parent(vertexes.size(), NIL);

	size_t src = _find_not_empty();


	heap.push(make_pair((W)0, src));
	
	while (!heap.empty())
	{
		size_t n1 = heap.top().second;
		heap.pop();
		visited[n1] = true;

		for (auto p = vertexes[n1].first; p != nullptr; p = p->next)
		{
			size_t n2 = p->adj;
			W weight = p->cost;

			if (!visited[n2] && weight < low_cost[n2])
			{
				low_cost[n2] = weight;
				heap.push(make_pair(weight, n2));
				//put into MST
				parent[n2] = n1;
			}
		}
	}
	//add edge to MST
	for (size_t index = 0; index != vertexes.size(); ++index)
	{
		if (this->_is_empty_index(index) || parent[index] == NIL)
			continue;
		res.push_back(make_pair(index, parent[index]));
	}
	return res;
}

#endif