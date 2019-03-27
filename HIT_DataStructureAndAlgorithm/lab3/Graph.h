#ifndef _GRAPH_H_
#define _GRAPH_H_

#include <functional>	//for std::functional
#include <vector>		//for std::vector

using std::function;

template<typename T>
using GVisitFunc = std::function<void(T &)>;

//无向图基类
template <typename T>
class Graph
{
public:

	//添加节点
	virtual void addVertex(const T &) = 0;

	//根据点编号删除点
	virtual void delVertex(size_t index) = 0;

	//添加边
	virtual void addEdge(size_t, size_t) = 0;

	//根据边连接的两个顶点编号删除边
	virtual void delEdge(size_t, size_t) = 0;

	//清空图
	virtual void clear() = 0;

	//深度优先搜索
	virtual void dfs(GVisitFunc<T> visit) = 0;
	
	//广度优先搜索
	virtual void bfs(GVisitFunc<T> visit) = 0;
	
	//递归深搜
	virtual void rec_dfs(GVisitFunc<T> visit) = 0;
	
	//递归广搜
	virtual void rec_bfs(GVisitFunc<T> visist) = 0;

	//判断是否为边
	virtual bool isEdge(size_t n1, size_t n2) = 0;
};

#endif 