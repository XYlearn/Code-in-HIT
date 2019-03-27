#include "list.h"
#include "stack.h"
#include <iostream>
#include <cstring>
#include <cstdlib>
#include <ctime>
#include <unistd.h>
#include <algorithm>
#include <sstream>
#include <fstream>

/*if RECURSION is defined the PathFinder will use recursion algorithm to find path*/
//#define RECURSION

using namespace std;

const int MAX_STEPS = 1 << ((sizeof(int)-1) * 8);

typedef bool** Maze;
enum Direct {None = -1, N = 0, NE, E, SE, S, SW, W, NW};
const int DIRECTION_NUMBER = 8;
struct NodeTrack
{
    //constructors
    NodeTrack(){}
    NodeTrack(int x, int y):x(x),y(y){}

    int x,y;
    /*all flase after init*/
    bool coverd[DIRECTION_NUMBER] = {0};
    Direct nextDirect()
    {
        for(int i=0; i != DIRECTION_NUMBER; ++i)
            if(!coverd[i])
            {
                coverd[i] = true;
                return Direct(i);
            }
        return None;
    }

    void cover(Direct dir) {coverd[dir] = true;}
};

const int Nextx[] = {0, 1, 1, 1, 0, -1, -1, -1};

const int Nexty[] = {-1, -1, 0, 1, 1, 1, 0, -1};

/*given a direction to next NodeTrack,return the from direction*/
const Direct From[] = {S, SW, W, NW, N, NE, E, SE};

Maze allocMaze(int rows, int cols)
{
    /*allocate memory for maze, (n+2)*(n+2) for maze of n*n*/
    Maze maze = new bool*[rows+2]; 
    
    for(int i = 0; i != rows+2; ++i)
        maze[i] = new bool[cols+2];
    //initialize boarder
    for(int i = 0; i != cols+2; ++i)
    {
        maze[0][i] = true;
        maze[rows+1][i] = true;
    }
    for(int i = 0; i != rows+2; ++i)
    {
        maze[i][0] = true;
        maze[i][cols+1] = true;
    }

    return maze;
}

void freeMaze(Maze maze, int rows, int cols)
{
    for(int i = 0; i != rows+2; ++i)
        delete[] maze[i];
    delete[] maze;
}

/*build a random maze of specific size*/
Maze randMaze(int rows, int cols)
{
    Maze maze = allocMaze(rows, cols);

    //set boarders to 1
    for(int i = 0; i != cols+2; ++i)
    {
        maze[0][i] = 1;
        maze[rows+1][i] = 1;
    }
    
    for(int i = 0; i != rows+2; ++i)
    {
        maze[i][0] = 1;
        maze[i][cols+1] = 1;
    }

    for(int i = 1; i != rows+1; ++i)
        for(int j = 1; j != cols+1; ++j)
            maze[i][j] = rand() % 2;
    
    /*the entry and exit should be 0*/
    maze[rows][cols] = maze[1][1] = 0;
    return maze;
}

//read maze from stringBuffer
//format:
//rows cols 
//chars
bool readMaze(string buf, Maze &maze, int *prows, int *pcols, int *pstartx, int *pstarty, int *pdestx, int *pdesty)
{
    int rows; int cols;
    istringstream in(buf);

    in >> rows >> cols;
    if(in.fail())
    {
        return false;
    }

    *prows = rows; *pcols = cols;
    *pstartx = *pstarty = 0;
    *pdestx = rows; *pdesty = cols;
    //alloc memory for maze
    maze = allocMaze(rows, cols);
    for(auto i = 1; i != rows+1; ++i)
    {
        string row;
        in >> row;
        if(row.size() != cols)
            continue;
        for(auto j = 1; j != cols+1; ++j)
        {
            switch(row[j-1])
            {
                case '|':
                    maze[i][j] = true;
                    break;
                case 'O':
                    maze[i][j] = false;
                    break;
                case '@':
                    maze[i][j] = false;
                    *pstartx = i; *pstarty = j;
                    break;
                case '#':
                    maze[i][j] = false;
                    *pdestx = i, *pdesty = j;
                    break;
                default:
                    freeMaze(maze, rows, cols);
                    maze = nullptr;
                    return false;
            }
        }
    }
    return true;
}

/*print a maze in a more visual way*/
void printMaze(Maze maze, int rows, int cols)
{
    for (int i = 1; i != rows + 1; ++i)
    {
        for (int j =1; j != cols+1; ++j)
            cout << maze[i][j] ? "|" : "O";
        cout << endl;
    }
}

//the class to find path
class PathFinder
{
private:
    Maze maze;
    int rows, cols;
    int startx,starty,destx,desty;
    Stack<NodeTrack> path; /*stack to save current path*/
    List<Stack<NodeTrack>> paths;
    bool **table;
    bool impossible;

public:
    //init maze
    //use start and end to mark the entrance and exit
    //must set the boarder to bool 1
    PathFinder(Maze maze, int rows, int cols, int startx, int starty, int destx, int desty):maze(maze),rows(rows),cols(cols),startx(startx),starty(starty),destx(destx),desty(desty)
    {
        table = new bool*[rows+2];
        for(int i = 0; i != rows+2; ++i)
            table[i] = new bool[cols+2];
        /*the first node must be (1,1)*/
        table[rows][cols] = true;
        NodeTrack nt(rows,cols);
        path.push(nt);
    }

    void init()
    {
        path.makeEmpty();
        paths.makeEmpty();
        for(int i = 0; i!=rows+2; ++i)
            for(int j =0; j != cols+2; ++j)
                table[i][j] = false;
    }

    //start is (1,1) and end is (rows, cols) by default
    PathFinder(Maze maze, int rows, int cols):PathFinder(maze, rows, cols, 1, 1, rows, cols){}
    
    //free the maze
    ~PathFinder()
    {
        freeMaze(maze, rows, cols);
        for(int i = 0; i != rows+2; ++i)
        {
            delete[] table[i];
        }
        delete []table;
    }

    /*find one path to the exit*/
    Stack<NodeTrack> &findPath(bool all=false);

    /*find the shortest path*/
    Stack<NodeTrack> &findShortest() 
    {
        if(impossible)
            return path;
        if(paths.empty())
        {
            path.makeEmpty();
            findPath(true);
        }
        int min_step = MAX_STEPS;
        Stack<NodeTrack> *pPath = nullptr;
        for(auto i = paths.first(); i != nullptr; i = i->next)
        {
            if(i->data.getSize() < min_step)
            {
                pPath = &(i->data);
                min_step = i->data.getSize();
            }
        }
        path = *pPath;
        return path;
    }

    bool hasPath() {return !paths.empty();}

    /*print path*/
    void printPath(bool all);
};

int main(int argc, char *argv[])
{
    srand(time(0));
    int rows = 5, cols = 4;
    int startx,starty,destx,desty;
    startx = starty = 1;
    destx = rows; desty = cols;
    Maze maze;
    if(argc == 3)
    {
        rows = atoi(argv[1]);
        cols = atoi(argv[2]);
        maze = randMaze(rows, cols);
        destx = rows;
        desty = cols;
    }
    else if(argc == 2)
    {
        ifstream fin(argv[1]);
        ostringstream tmp;
        tmp << fin.rdbuf();
        string buf = tmp.str();
        bool success = readMaze(buf, maze, &rows, &cols, &startx, &starty, &destx, &desty);
        if(!success)
        {
            cout << "parse error\n";
            exit(0);
        }
    }
    else
    {
        maze = randMaze(rows, cols);
    }
    printMaze(maze, rows, cols);
    PathFinder pf(maze, rows, cols, startx,starty,destx,desty);
    pf.findPath(true);
    pf.printPath(true);
    pf.findShortest();
    cout << "Shortest Path:\n";
    pf.printPath(false);
    return 0;
}

#ifndef RECURSION
/*if all == false(default):find the path from (1,1) to (rows,cols);if there is no path, return an empty stack
 * 
 *if all == true; find all paths and return an empty path
 * */
Stack<NodeTrack> & PathFinder::findPath(bool all)
{
    /*find path from dest to start
     * so that the stack returned will be a path from start to dest
     */
    init();
    int startx = this->destx;
    int starty = this->desty;
    int destx = this->startx;
    int desty = this->starty;
    NodeTrack curr(startx,starty);
    Direct dir;
    path.push(curr);

    /*start searching path*/
    while(!path.empty())
    {
        curr = path.top();
        if(curr.x == destx && curr.y == desty)
        {
            if(!all)
                return path;
            /*to find all, just store the path and clear the table*/
            for(int i = 0; i != rows + 2; ++i)
                for(int j =0; j != cols + 2; ++j)
                    table[i][j] = 0;
            for(auto n = path.getHead()->next; n != nullptr; n = n->next)
                table[n->data.x][n->data.y] = true;
            paths.insert(path, paths.getHead());
            path.pop();
            continue;
        }
        /*get net direction and mark it*/
        dir = curr.nextDirect();
        path.pop();
        path.push(curr);
        if(dir == None)
        {
            path.pop();
            if(path.empty())
                break;
            if(!all)
                continue;
            /*to find all paths, reset the table*/
            for(int i = 0; i != rows + 2; ++i)
                for(int j =0; j != cols + 2; ++j)
                    table[i][j] = 0;
            for(auto n = path.getHead()->next; n != nullptr; n = n->next)
                table[n->data.x][n->data.y] = true;
            continue;
        }
        curr.x += Nextx[dir];
        curr.y += Nexty[dir];

        if(maze[curr.x][curr.y] || table[curr.x][curr.y])
            continue;
        /*if(!all && table[curr.x][curr.y])
            continue;*/

        /*reset the coverd array*/
        for(int i = 0; i != DIRECTION_NUMBER; ++i)
            curr.coverd[i] = false;

        /*mark the from direction coverd*/
        curr.coverd[From[dir]] = true;
        path.push(curr);
        table[curr.x][curr.y] = true;
    }
    /*make the path empty and return*/
    path.makeEmpty();
    impossible = paths.getSize() == 0;
    return path;
}

#else

/*use recursion to find path*/
Stack<NodeTrack> & PathFinder::findPath()
{
    if(path.empty())
        return path;
    NodeTrack curr = path.top();
    int x = curr.x;
    int y = curr.y;
    if(x == this->startx && y == this->starty)
        return path;
    for(int i = 0; i != DIRECTION_NUMBER; ++i)
    {
        Stack<NodeTrack> temp = path;
        if(!table[x+Nextx[i]][y+Nexty[i]] && !maze[x+Nextx[i]][y+Nexty[i]])
        {
            table[x+Nextx[i]][y+Nexty[i]] = true;
            curr.x += Nextx[i];
            curr.y += Nexty[i];
            path.push(curr);
            findPath();
            if(!path.empty())
            {
                impossible = path.getSize() == 0;
                return path;
            }
            path = temp;
        }
    }
    path.makeEmpty();
    return path;
}

#endif

void PathFinder::printPath(bool all)
{
    if(path.empty() && paths.empty())
    {
        cout << "Can't find way out!\n";
        return;
    }

    /*print a path*/
    else if(paths.empty() || !all)
    {
        Stack<NodeTrack> tempPath = path;
        while(!tempPath.empty())
        {
            NodeTrack nodeTrack = tempPath.pop();
            cout << "(" << nodeTrack.y << "," << nodeTrack.x << ")" << "->";
        }
        cout << "win!\n";
    }

    /*print all possible paths*/
    else
    {
        List<Stack<NodeTrack>> tempPaths = paths;
        int count = 1;
        for(auto n = tempPaths.getHead()->next; n != nullptr; n = n->next)
        {
            cout << "Path" << count << ":\n";
            while(!n->data.empty())
            {
                NodeTrack nodeTrack = n->data.pop();
                cout << "(" << nodeTrack.y << "," << nodeTrack.x << ")" << "->";
            }
            cout << "win!\n";
            ++count;
        }
    }
}


