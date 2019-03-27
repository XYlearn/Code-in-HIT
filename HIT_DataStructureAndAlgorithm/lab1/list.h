/*
 *This is a DIY list
 * */
#ifndef _LIST_H_
#define _LIST_H_

#include <cstddef>
#include <iostream>
#include <cstdlib>
#include <string>
#include <cstring>

template<typename T>
struct Node;

template<typename T>
struct Node
{
private:
    static void *bin;
public:
    T data;
    Node<T> *next;

    /*init list*/
    Node():next(nullptr){}
    Node(const T &t):data(t), next(nullptr){}

    /*reuse bin operations*/
    static void freeBin()
    {
        while(((Node<T> *)bin)->next)
        {
            Node<T> *temp = ((Node<T> *)bin)->next;
            ((Node<T> *)bin)->next = temp->next;
            ::operator delete(temp);
        }
        ::operator delete(bin);
    }

    /*reload operator new*/
    void *operator new(size_t size, const T &t)
    {
        Node<T> *retNode = (Node<T> *)operator new(size);
        retNode->data = t;
        return retNode;
    }

    void *operator new(size_t size)
    {
        Node<T> *retNode;
        if(bin == nullptr)
        {
            retNode = (Node<T> *) ::operator new (size);
            retNode->next = nullptr;
        }
        else
        {
            retNode = (Node<T> *)bin;
            bin = retNode->next;
            memset(&(retNode->data), 0, sizeof(T));
            retNode->next = nullptr;
        }
        return retNode;
    }
    
    /*reload operator delete*/
    void operator delete(void *ptr)
    {
        ((Node<T> *)ptr)->next = (Node<T> *)bin;
        bin = ptr;
    }
};
template<typename T>
void *Node<T>::bin = nullptr;

template<typename T>
class List
{
protected:
    Node<T> *head;
    size_t size;

public:
    List():head(new Node<T>()),size(0){}
    List(const List<T> &list);
    List(List<T> &&list);

    List<T>& operator=(const List<T> &list);
    List<T>& operator=(const List<T> &&list);

    void makeEmpty();
    bool empty()const {return size == 0;}
    int getSize()const {return size;}
    Node<T> *getHead() const {return head;}
    virtual ~List();

    void insert(const T &data, Node<T> *pos);
    T retrieve(Node<T> *pos) {return pos ? pos->next->data : nullptr;}
    void remove(Node<T> *pos);
    Node<T> *locate(const T &t)const;
    Node<T> *previous(Node<T> *pos)const;
    Node<T> *next(Node<T> *pos)const;

    Node<T> *first()const {return head->next;}
    Node<T> *end()const;

    /*show error message and exit*/
    static void error(std::string s) {std::cerr << s; exit(0);}

};

/*copy constructor*/
template<typename T>
List<T>::List(const List<T> &list)
{
    head = new Node<T>();
    for(Node<T> * n = list.head->next, *temp = head; n != nullptr; n = n->next)
    {
        temp->next = new Node<T>(n->data);
        temp = temp->next;
    }
    size = list.size;
}

/*move constructor*/
template<typename T>
List<T>::List(List<T> &&list)
{
    this->head = list.head;
    list.head = nullptr;
    this->size = list.size;
    list.size = 0;
}

/*destructor*/
template<typename T>
List<T>::~List()
{
    Node<T> *temp = nullptr;
    this->makeEmpty();
    delete head;
}

/*assign opertor*/
template<typename T>
List<T> & List<T>::operator=(const List<T> &list)
{
    this->makeEmpty();
    Node<T> *temp = head;
    for(Node<T> *n = list.head->next; n != nullptr; n = n->next)
    {
        temp->next = new Node<T>(n->data);
        temp = temp->next;
    }
    size = list.size;
    return *this;
}

/*move assign operator*/
template<typename T>
List<T> & List<T>::operator=(const List<T> &&list)
{
    this->makeEmpty();
    delete head;
    this->head = list.head;
    list.head = nullptr;
    this->size = list.size;
    list.size = 0;
}

/*insert node of data t after pos*/
template<typename T>
void List<T>::insert(const T& t, Node<T> *pos)
{
    if(pos == nullptr)
        List<T>::error(std::string("[-]Null Pointer Error\n"));
    Node<T> *temp = new Node<T>(t);
    temp->next = pos->next;
    pos->next = temp;
    ++size;
}

/*remove the next node of pos from the list*/
template<typename T>
void List<T>::remove(Node<T> *pos)
{
    Node<T> *temp = nullptr;
    if(pos == nullptr)
        error("[-]Null Pointer Error!\n");
    if(pos->next != nullptr)
    {
        temp = pos->next;
        pos->next = temp->next;
        delete temp;
        --size;
    }
}

/*find the previous node of pos
 * return nullptr if pos is nullptr or pos out of range*/
template<typename T>
Node<T> * List<T>::previous(Node<T> *pos)const
{
    Node<T> *p = head;
    int count = 0;
    if(pos == nullptr)
        return nullptr;
    while(count++ < size && p->next != pos)
        p = p->next;
    if(count == size)
        p = nullptr;
    return p;
}

template<typename T>
Node<T> *List<T>::next(Node<T> *pos) const
{
    if(pos == nullptr)
        error("[-]Null Pointer Error!\n");
    return pos->next;
}

/*clear the list except the head node*/
template<typename T>
void List<T>::makeEmpty()
{
    Node<T> *p = head->next;
    while(p)
    {
        head->next = p->next;
        delete p;
        p = head->next;
    }
    size = 0;
}

template<typename T>
Node<T> *List<T>::end() const
{
    Node<T> *p = head;
    while(p->next);
    return p==head ? nullptr : p;
}
#endif
