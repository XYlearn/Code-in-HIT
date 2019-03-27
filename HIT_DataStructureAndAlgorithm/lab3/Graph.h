#ifndef _GRAPH_H_
#define _GRAPH_H_

#include <functional>	//for std::functional
#include <vector>		//for std::vector

using std::function;

template<typename T>
using GVisitFunc = std::function<void(T &)>;

//����ͼ����
template <typename T>
class Graph
{
public:

	//��ӽڵ�
	virtual void addVertex(const T &) = 0;

	//���ݵ���ɾ����
	virtual void delVertex(size_t index) = 0;

	//��ӱ�
	virtual void addEdge(size_t, size_t) = 0;

	//���ݱ����ӵ�����������ɾ����
	virtual void delEdge(size_t, size_t) = 0;

	//���ͼ
	virtual void clear() = 0;

	//�����������
	virtual void dfs(GVisitFunc<T> visit) = 0;
	
	//�����������
	virtual void bfs(GVisitFunc<T> visit) = 0;
	
	//�ݹ�����
	virtual void rec_dfs(GVisitFunc<T> visit) = 0;
	
	//�ݹ����
	virtual void rec_bfs(GVisitFunc<T> visist) = 0;

	//�ж��Ƿ�Ϊ��
	virtual bool isEdge(size_t n1, size_t n2) = 0;
};

#endif 