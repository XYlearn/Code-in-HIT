#include <iostream>
#include <iomanip>
#include <string>
#include "BMI.h"

using namespace std;

StudentBMI *inputInfo();
void printCmdTable();
void printInfo(const StudentBMIs &data);

int main()
{
	StudentBMIs studentBMIs;
	StudentBMI *pStudentBMI;
	int cmd;
	
	printCmdTable();
	while (true && cin.good())
	{
		cout << ">Cmd: ";
		cin >> cmd;
		switch (cmd)
		{
		case 0:
			printCmdTable();
			break;
		case 1:
			pStudentBMI = inputInfo();
			if (pStudentBMI)
				studentBMIs.add(*pStudentBMI);
			else cout << "Invalid input!" << endl;
			break;
		case 2:
			printInfo(studentBMIs);
			break;
		case 3:
			studentBMIs.sortById();
			break;
		case 4:
			studentBMIs.sortByName();
			break;
		case 5:
			studentBMIs.sortByHeight();
			break;
		case 6:
			studentBMIs.sortByWeight();
			break;
		case 7:
			studentBMIs.sortByBMI();
			break;
		case 8:
			exit(0);
		default:
			cout << "unknown command!" << endl;
		}
	}

	return 0;
}

void printCmdTable()
{
	cout << "Command List:" << endl;
	cout << "\t0 print this table" << endl;
	cout << "\t1 input information" << endl;
	cout << "\t2 print information of all students" << endl;
	cout << "\t3 sort by id" << endl;
	cout << "\t4 sort by name" << endl;
	cout << "\t5 sort by height" << endl;
	cout << "\t6 sort by weight" << endl;
	cout << "\t7 sort by BMI" << endl;
	cout << "\t8 quit" << endl;
}

StudentBMI *inputInfo()
{
	float height;
	float weight;
	string id;
	string name;
	StudentBMI *studentBMI = nullptr;

	cout << "id:";
	cin >> id;
	cout << "name:";
	cin >> name;
	cout << "height(m):";
	cin >> height;
	cout << "weight(kg):";
	cin >> weight;

	studentBMI = new StudentBMI(id, name, height, weight);
	studentBMI->calc();
	if (studentBMI->valid())
		return studentBMI;
	else
		return nullptr;
}

void printInfo(const StudentBMIs &studentBMIs)
{
	for each (StudentBMI a in studentBMIs.getVec())
	{
		cout << "id: " << a.getId() << " name: " << a.getName() << endl
			<< "height: " << a.getHeight() << " weight:" << a.getWeight() << endl
			<< "bmi: " << a.getBmi() << " analyze: " << a.analyze() << endl;
		cout << "---------------------------------------------" << endl;
	}
}
