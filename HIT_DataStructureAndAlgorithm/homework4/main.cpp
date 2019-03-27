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
	writeRes("C:\\Users\\XHWhy\\Documents\\WORK\\数据结构\\homework4\\bRes.txt", bres);
	auto qres = test(qSort<int>);
	writeRes("C:\\Users\\XHWhy\\Documents\\WORK\\数据结构\\homework4\\qRes.txt", qres);
	return 0;
}

template <typename T>
vector<T> bSort(const vector<T> &vec)
{
	vector<T> res(vec);
	auto n = vec.size();
	for (int i = 0; i < n; ++i)
	{
		bool swap_flag = false;
		for (int j = i; j < n; ++j)
			if (res[j] < res[i])
			{
				T temp = res[i];
				res[i] = res[j];
				res[j] = temp;
				swap_flag = true;
			}
		if (!swap_flag)
			break;
	}
	return res;
}

template <typename T>
vector<T> qSort(const vector<T> & vec)
{
	vector<T> res(vec);

	function<void(int,int)> _qsort = 
		[&](int left, int right)
	{
		if (left >= right)
			return;
		int i = left, j = right;
		T x = res[left];	// set the left element as pivot 
		while (i < j)
		{
			// search from right to find element less than x
			while (i < j && res[j] >= x)
				--j;
			if (i < j)	// found
				res[i++] = res[j];

			// search from left to find element larger than x
			while (i < j && res[i] <= x)
				++i;
			if (i < j)	// found
				res[j--] = res[i];
		}
		res[i] = x;
		_qsort(left, i - 1);
		_qsort(i + 1, right);
	};
	
	_qsort(0, vec.size() - 1);
	return res;
}

map<int, double> test(function<vector<int>(const vector<int> &)> sort)
{
	map<int, double> res;
	for (int i = TEST_STEP; i <= TEST_SIZE; i += TEST_STEP)
	{
		vector<int> vec;
		microseconds total = microseconds::zero();
		for (int j = 0; j != RAND_TEST_TIME; ++j)
		{
			// test with random vec
			randVec(vec, i);
			// test with same vec
			auto start = system_clock::now();
			for(int k = 0; k != LAST_TEST_TIME; ++k)
				sort(vec);
			auto end = system_clock::now();
			total += duration_cast<microseconds>(end - start);
		}
		res[i] = double(total.count()) * microseconds::period::num / microseconds::period::den;
	}
	return res;
}

void randVec(vector<int> &vec, int size)
{
	vec.clear();
	vec.reserve(size);
	default_random_engine e;
	for (int i = 0; i < size; ++i)
		vec.push_back(e());
}

void writeRes(string filename, const map<int, double> &res)
{
	fstream f(filename, ios::out | ios::trunc);
	for (auto ele : res)
		f << ele.first << " " << ele.second << endl;
	f.close();
}