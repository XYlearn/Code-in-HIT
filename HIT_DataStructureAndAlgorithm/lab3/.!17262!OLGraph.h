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

