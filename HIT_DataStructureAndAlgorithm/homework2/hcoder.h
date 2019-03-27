#ifndef _HCODER_H_
#define _HCODER_H_

#include "htree.h"	//for htree
#include <map>	//for map
#include <string>
#include <iostream>
#include <sstream>

using std::map;
using std::pair;
using std::string;
using std::istream;
using std::ostream;

// a coder of ascii
class HCoder : public HTree<char, double>
{
private:
	map<char, string> _ct;	//code table
	map<char, double> _wt;	//weight table
	string text;

public:

	// get code table of ascii and weight table of characters
	void init(istream &in);
	// get code table of ascii and weight table of characters
	void init(string s)
	{
		std::stringstream is(s);
		init(is);
	}

	// encode to binary stream
	string  encode(const string &);

	// decode from binary stream
	string decode(const string &);

	// get weight of character
	double weightof(char ch) { if (_wt.find(ch) != _wt.end()) return _wt[ch]; else return 0; }
	
	// print Character Weight table
	void printWeights()
	{
		for (auto ele : _wt)
			std::cout << ele.first << " : " << ele.second << std::endl;
	}

	// print Character Code Table
	void printCodes()
	{
		for (auto ele : _ct)
			std::cout << ele.first << " : " << ele.second << std::endl;
	}

};

#endif
