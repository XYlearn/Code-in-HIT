#ifndef _STACK_H_
#define _STACK_H_

#include "list.h"

template<typename T>
class Stack : public List<T>
{
public:
    using List<T>::List;
    Stack(const Stack<T> &stack);
    Stack<T> & operator=(const Stack<T> &stack);
    void push(const T & data);
    T pop();
    T& top()const;
};

template<typename T>
Stack<T>::Stack(const Stack<T> &stack)
{
    this->head = new Node<T>();
    for(Node<T> *n = stack.head->next, *temp = this->head; n!=nullptr; n = n->next)
    {
        temp->next = new Node<T>(n->data);
        temp = temp->next;
    }
    this->size = stack.size;
}

template<typename T>
Stack<T> &Stack<T>::operator=(const Stack<T> &stack)
{
    this->makeEmpty();
    Node<T> *temp = this->head;
    for(Node<T> *n = stack.first(); n != nullptr; n = n->next)
    {
        temp->next = new Node<T>(n->data);
        temp = temp->next;
    }
    this->size = stack.size;
    return *this;
}

template<typename T>
void Stack<T>::push(const T & data)
{
    Node<T> *head = this->getHead();
    Node<T> *p = new Node<T>(data);
    p -> next = head -> next;
    head -> next = p;
    ++this->size;
}

template<typename T>
T Stack<T>::pop()
{
    Node<T> *head = this->getHead();
    Node<T> *p = head->next;
    if (p == nullptr)
        Stack<T>::error("Stack is empty!\n");
    T data = p->data;
    head->next = p->next;
    delete p;
    --this->size;
    return data;
}

template<typename T>
T& Stack<T>::top()const
{
    Node<T> *head = this->getHead();
    Node<T> *p = head->next;
    if(p == nullptr)
        Stack<T>::error("Stack is empty!\n");
    return head->next->data;
}

#endif
