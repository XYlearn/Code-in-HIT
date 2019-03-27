
/*
 * ����ͼ���ڽӱ��ʾ��
 *     Ϊ�˽�ʡ�߱�洢��������ȡ�������Խ��ߵ���������ʽ���棬
 * ����֮���Ƿ��б�����ȡ����edges[max(ind1,ind1)][min(ind1,
 * ind2)]�Ƿ�Ϊ1��
 *	   ɾ������ʱΪ��ʡʱ�䣬��ֱ�ӽ�����ӱ���ɾȥ�����Ǳ���
 * �õ���Ϣ������߱��е��������Ϊ0���������ű����ڿ��б��
 * �б��С�
 *      ����ӽڵ�ʱ�ȴӿ��б�ű��в����Ƿ��п��кţ�������
 * �ڵ�����ڸñ�Ŷ�Ӧ���������߱��У���û�������б����
 * �ӽڵ���������½ڵ㡣
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

//���嶥������
template <typename T>
using VertexTable = vector<T>;

//����߱����
using EdgeTable = vector<vector<bool>>;

//ͼ���ڽӾ����ʾ
template <typename T>
class MTGraph : public Graph<T>
{
private:
	VertexTable<T> vertexes;	//���涨����Ϣ���±��ʾ���
	EdgeTable edges;		//�������Ϣ
	size_t n;	//���涥����
	size_t e;	//�������

	list<size_t> empty_indexes;	//��������±���
	vector<bool> empty_table;	//���������Ƿ����

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

	//��ӽڵ�
	void addVertex(const T & data);	

	//ɾ����
	void delVertex(size_t index);

	//��ӱ�
	void addEdge(size_t node_ind1, size_t node_ind2);

	//���ݱ����ӵ�����������ɾ����
	void delEdge(size_t node_ind1, size_t node_ind2);

	//���ͼ
	void clear();

	//�����������
	void dfs(GVisitFunc<T> visit);

	//�����������
	void bfs(GVisitFunc<T> visit);

	//�ݹ�����
	void rec_dfs(GVisitFunc<T> visit);

	//�ݹ����
	void rec_bfs(GVisitFunc<T> visist);

	//�жϱ��Ƿ�����ͼ
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

//��ӽڵ�
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

//ɾ���ڵ�
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

//��ӱ�
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

//ɾ����
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