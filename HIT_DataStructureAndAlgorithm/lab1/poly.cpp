#include "list.h"
#include <utility>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <limits>
#include <cmath>

using namespace std;

class Poly;

struct DivResult
{
    Poly *qPoly;
    Poly *rPoly;
};

struct Entry
{
    double coef;
    int index;

    Entry() = default;
    Entry(int index, double coef):index(index),coef(coef){}

};

/*polynomial class*/
class Poly : public List<Entry>
{
private:
public:
    using List<Entry>::List;

    Poly(const Poly &p):List<Entry>::List(p){}

    Poly &operator-()
    {
        Poly *poly = new Poly(*this);
        for(auto curr = poly->first(); curr != nullptr; curr = curr->next)
            curr->data.coef = -curr->data.coef;
        return *poly;
    }

    /*reload operator*/
    friend Poly& operator+(const Poly &p1, const Poly &p2);
    friend Poly& operator-(const Poly &p1, const Poly &p2);
    friend Poly& operator*(const Poly &p1, const Poly &p2);
    friend DivResult operator/(const Poly &p1, const Poly &p2);
    friend std::ostream& operator<<(const std::ostream &os, const Poly &p);

    
    /*add an entry to poly*/
    void addEntry(int index, double coef);

    /*multiply an entry with poly*/
    void mulEntry(int index, double coef);

    /*calculate poly*/
    double calculate(double x);

    /*friend print poly*/
    friend void printPoly(const Poly &poly);
};

/*get input until get a double*/
double getDouble()
{
    double num;
    cin >> num;
    while (cin.fail())
    {
        cin.clear();
        cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        cin >> num;
    }
    cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    return num;
}


/*get input until get a double*/
int getInt()
{
    int num;
    cin >> num;
    while (cin.fail())
    {
        cin.clear();
        cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        cin >> num;
    }
    cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    return num;
}

/*
 * read poly from file an calculate, receive a filename and return a Poly
 * each line of file contains index and coef number split by a space
 */
Poly &readFromFile(string filename)
{
    ifstream file(filename);
    stringstream line_stream;
    string line;

    Poly *poly = new Poly();
    int index, coef;
    if(file.is_open())
    {
        while(getline(file, line))
        {
            line_stream << line;
            line_stream >> index >> coef;
            poly->addEntry(index, coef);
            line_stream.clear();
        }
        file.close();
        return *poly;
    }
    else
    {
        std::cerr << "Can't open the file" << filename << endl;
        exit(0);
    }
}

//parse the polynomial from a string
Poly &inputPoly(int mark)
{
    Poly *poly = new Poly();
    int input_number;
    cout << "The number of entry you want to input:";
    input_number = getInt(); 

    /*read coef and index and add entries*/
    double coef, index;
    for(int count = 0; count != input_number; ++count)
    {
        cout << "coefficient" << count << ":";
        coef = getDouble();

        cout << "index" << count << ":";
        index = getDouble();
        poly->addEntry(index, coef);
    }
    return *poly;
}

void printPoly(const Poly &poly)
{
    bool isFirstEntry = true;
    if(poly.empty())
    {
        cout << "0" << endl;
        return;
    }
    for(auto pNode = poly.first(); pNode != nullptr; pNode = pNode->next)
    {
        /*for consideration of the + and -*/
        if(!isFirstEntry && pNode->data.coef >= 0)
            cout << "+";
        else if (isFirstEntry) isFirstEntry = false;

        /*print coef if it's not 1 or -1 or the index is 0*/
        if ((pNode->data.coef != 1 && pNode->data.coef != -1) || !pNode->data.index)
            cout << pNode->data.coef;
        /*if the index is not 0 and coef is -1 then print -*/
        else if(pNode->data.coef == -1)
            cout << "-";

        if(pNode->data.index)
            cout << "X^" << pNode->data.index;
    }
    cout << endl;
}

/*print help*/
void printHelp()
{
    /*print instructions formats*/
    cout << string(34,'*') << "Instructions" << string(34,'*') << endl
        << "\ti : read a poly from stdin\n"
        << "\tf [filename]: read a poly from file\n"
        << "\t+-*/ : do operations to previous poly and next poly\n"
        << "\tc : clear\n"
        << "\th : print this help\n"
        << "\tq : quit this process\n"
        << string(80, '*') << endl;
}

//main function
int main(int argc, char *argv[])
{
    string cmd;
    Poly p1;
    Poly p2;

    bool has_poly = false;

    char op = 0; //set to 0 if there is no op

    DivResult divResult;
    bool is_prev_div = false;

    double x = 0;

    auto readPoly = [&](auto readfunc, auto arg)
    {
        if(!op)
        {
            p1 = readfunc(arg);
            has_poly = true;
        }
        else
        {
            p2 = readfunc(arg);
            switch(op)
            {
            case '+':
                p1 = p1 + p2;
                break;
            case '-':
                p1 = p1 - p2;
                break;
            case '*':
                p1 = p1 * p2;
                break;
            case '/':
                divResult = p1 / p2;
                is_prev_div = true;
                break;
            default:
                cerr << "Unknown operation error!\n";
                exit(0);
            }
            op = 0;
        }
    };

    /*interact*/
    while(true)
    {
        cout << ">>";
        getline(cin, cmd);
        // can only parse cmd not start with spaces
        switch(cmd[0])
        {
        case 'i': //read from stdin
            readPoly(inputPoly, 0);
            break;
        case 'f': //read from file
            {
            istringstream cmdstream(cmd);
            string filename;
            cmdstream >> filename >> filename;
            readPoly(readFromFile, filename);
            }
            break;
        case 'h':   //help list
            printHelp();
            break;
        case 'q':   //quit
            exit(0);
        case 'c':   //clear poly and op
            has_poly = false;
            op = 0;
            break;
        case 'x':
            if(!has_poly)
            {
                cout << "No inputed Poly!" << endl;
                break;
            }
            x = getDouble();
            cout << "Result:" << p1.calculate(x) << endl;
            break;
        default:    //+-*/ or other situations
            //set op
            //check whether the command is valid
            if(cmd[0] != '+' && cmd[0] != '-' && cmd[0] != '*' && cmd[0] != '/')
            {
                cout << "Unkown command!\n";
                continue;
            }
            //check the format
            else if(op || !has_poly)
            {
                cout << "Format Error!\n";
                continue;
            }
            else
                op = cmd[0];
        }
        /*print current poly*/
        /*if the / operation done print qPoly and rPoly*/
        if(is_prev_div)
        {
            is_prev_div = false;
            printPoly(*(divResult.qPoly));
            cout << "\t...\t\n";
            printPoly(*(divResult.rPoly));
        }
        else if(has_poly)
            printPoly(p1);
        if(op)
            cout << op << endl;
    }
    return 0;
}


/* add 2 sorted polynomial and return the result
 * O(p1.size+p2.size)*/
Poly& operator+(const Poly &p1, const Poly &p2)
{
    Poly *poly = new Poly();
    auto curr = poly->getHead();
    auto curr1 = p1.first(), curr2 = p2.first();
    Entry entry;
    for(;curr1 != nullptr && curr2 != nullptr;)
    {
        /*find the larger entry to insert*/
        if(curr1->data.index > curr2->data.index)
        {
            entry.index = curr1->data.index;
            entry.coef = curr1->data.coef;
            poly->insert(entry, curr);
            curr1 = curr1->next;
        }
        else if(curr1->data.index == curr2->data.index)
        {
            entry.coef = curr1->data.coef + curr2->data.coef;
            /*if the primer result is 0,don't need to insert it*/
            if(!entry.coef)
            {
                curr1 = curr1->next;
                curr2 = curr2->next;
                continue;
            }
            entry.index = curr1->data.index;
            poly->insert(entry, curr);
            curr1 = curr1->next;
            curr2 = curr2->next;
        }
        else
        {
            entry.index = curr2->data.index;
            entry.coef = curr2->data.coef; 
            poly->insert(entry, curr);
            curr2 = curr2->next;
        }
        curr = curr->next;
    }
    /*insert the rest of p1 and p2 into new poly*/
    while(curr1)
    {
        poly->insert(curr1->data, curr);
        curr1 = curr1->next;
        curr = curr->next;
    }
    while(curr2)
    {
        poly->insert(curr2->data, curr);
        curr2 = curr2->next;
        curr = curr->next;
    }
    return *poly;
}

/*operator-*/
/*p1-p2 is equal to p1 + (-p2)*/
Poly &operator-(const Poly &p1, const Poly &p2)
{
    Poly temp(p2);
    return p1 + (-temp); 
}

/*operator* */
Poly &operator*(const Poly &p1, const Poly &p2)
{
    Poly *poly = new Poly();
    Poly temp(p1);

    /*do n times mulEntry and plus the result with operator+*/
    for(auto curr = p2.first(); curr != nullptr; curr = curr->next)
    {
        temp.mulEntry(curr->data.index, curr->data.coef);
        *poly = *poly + temp;
        temp = p1;
    }
    return *poly;
}

/*operator/ */
/*return DivResult*/
DivResult operator/(const Poly &p1, const Poly &p2)
{
    if(p2.empty())
    {
        cerr << "Divide 0 Error\n";
        exit(0);
    }
    Poly *qPoly = new Poly();
    Poly *rPoly = new Poly(p1);
    int p2_index = p2.first()->data.index;
    double p2_coef = p2.first()->data.coef;
    int p1_index;
    double p1_coef;
    for(auto pNode = rPoly->first(); pNode != nullptr; pNode = rPoly->first())
    {
        if(rPoly->empty())
            break;
        p1_index = pNode->data.index;
        p1_coef = pNode->data.coef;
        p1_index -= p2_index;
        p1_coef /= p2_coef;
        if(p1_index < 0)
            break;
        Poly tempPoly(p2);
        tempPoly.mulEntry(p1_index, p1_coef);
        *rPoly = *rPoly - tempPoly;
        qPoly->addEntry(p1_index, p1_coef);
    }
    DivResult res;
    res.qPoly = qPoly;
    res.rPoly = rPoly;
    return res;
}

/*can use this function to quickly insert entry*/
void Poly::addEntry(int index, double coef)
{
    /*don't need to add entry if coef is 0*/
    if(coef == 0)
        return ;

    Node<Entry> *previous = this->getHead();
    Node<Entry> *curr = this->first();
    Entry entry(index, coef);

    /*if the Poly is empty add a entry directly*/
    if(curr == nullptr)
    {
        this->insert(entry, previous);
        return;
    }

    /*adding a zero entry makes no sense*/
    if(coef == 0)
        return;
    for(;curr != nullptr; curr = curr->next, previous = previous->next)
    {
        if(curr->data.index > index)
        {
            /*if the entry is smaller than any other entry, insert it back*/
            if(curr->next == nullptr)
            {
                this->insert(entry, curr);
                return;
            }
            else
                continue;
        }
        else if(curr->data.index == index)
        {
            curr->data.coef += coef;
            if(curr->data.coef == 0)
                this->remove(previous);
            return;
        }
        else
        {
            this->insert(entry, previous);
            return;
        }
    }
}

/*multiply with an entry and change the polynomial*/
void Poly::mulEntry(int index, double coef)
{
    /*any entry would turn to 0 when mul with 0*/
    if(coef == 0)
    {
        this->makeEmpty();
        return;
    }
    /*do multiply to each entry*/
    for(auto curr = this->first(); curr != nullptr; curr = curr->next)
    {
        curr->data.index += index;
        curr->data.coef *= coef;
    }
}

/*calculate poly*/
double Poly::calculate(double x)
{
    double result = 0;
    for(auto pNode= this->first();pNode != nullptr; pNode = pNode->next)
        result += pNode->data.coef * pow(x,pNode->data.index);
    return result;
}

