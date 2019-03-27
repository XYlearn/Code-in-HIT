#include <iostream>
#include <fstream>
#include <string>
#include "avl_tree.h"

using namespace std;

void readFromFile(AVLTree<int, char> &alv)
{
	string path("C:\\Users\\XHWhy\\Documents\\WORK\\数据结构\\lab4\\db.txt");
	fstream f(path);
	char key;
	int val;
	char control;
	while (!f.eof())
	{
		f >> control;
		if (f.fail())
			break;
		if (control == 'A')
		{
			f >> key;
			f >> val;
			if(alv.add(val, key))
				cout << "[Add] key : " << key << "\tval : " << val << endl;
		}
		else if (control == 'D')
		{
			f >> key;
			if(alv.del(key))
				cout << "[Del] key : " << key << endl;
		}
		else
		{
			cout << "[-]parse error\n";
			break;
		}
		alv.print();
		cin.get();
	}
	f.close();
}

int main()
{
	AVLTree<int, char> db;
	readFromFile(db);
	return 0;
}