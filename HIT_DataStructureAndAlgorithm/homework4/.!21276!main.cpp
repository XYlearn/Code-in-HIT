#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <chrono>
#include <random>
#include <functional>
#include <map>

const int TEST_SIZE = 15000;
const int TEST_STEP = 1000;
const int RAND_TEST_TIME = 15;
const int LAST_TEST_TIME = 15;

using namespace std;
using namespace chrono;

// buble sort
template <typename T>
vector<T> bSort(const vector<T> &);

// quick sort
template <typename T>
vector<T> qSort(const vector<T> &);

// test and return size-time result
map<int, double> test(function<vector<int>(const vector<int> &)>);

void writeRes(string filename, const map<int, double> &);

// generate random int vector of size
void randVec(vector<int> &vec, int size);

int main()
{
	auto bres = test(bSort<int>);
