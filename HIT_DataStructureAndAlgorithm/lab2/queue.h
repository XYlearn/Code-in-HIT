#ifndef _QUEUE_H_
#define _QUEUE_H_

#include "list.h"

template<typename T>
class Queue : public List<T>
{
private:
	Node<T> *rear;

public:
	Queue():List<T>(){}
	Queue(const Queue<T> &queue):List<T>(queue){rear = queue.rear;}
    Queue(Queue<T> && queue):List<T>(queue){rear = queue.rear;}

	Queue<T> & operator=(const Queue<T> &queue) 
	{
		Queue<T> &temp = List<T>::operator=(queue);
		temp.rear = queue.rear;
		return temp;
	}

	Queue<T> & operator=(Queue<T> &&queue)
	{
		Queue<T> &temp = List<T>::operator=(queue);
		temp.rear = queue.rear;
		return temp;
	}

	void enQueue(const T &data) 
	{
		this->insert(data, rear);
	}

	T deQueue() 
	{
		T data = head->data;
		remove(head);
		return data;
	}
}

#endif
