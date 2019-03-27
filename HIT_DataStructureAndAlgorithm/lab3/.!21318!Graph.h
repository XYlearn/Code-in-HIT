#ifndef _GRAPH_H_
#define _GRAPH_H_

#include <functional>	//for std::functional
#include <vector>		//for std::vector

using std::function;

template<typename T>
using GVisitFunc = std::function<void(T &)>;

