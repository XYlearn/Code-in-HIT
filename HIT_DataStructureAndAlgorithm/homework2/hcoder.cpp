#include "hcoder.h"
#include <iostream>	//
#include <map>
#include <iterator>
#include <sstream>
#include <string>

using std::map;
using std::string;
using std::stringstream;

// get table of ascii
void HCoder::init(istream &in)
{
	char ch;
	int count = 0;
	map<char, double> count_map;	//character and occurent times
	while (true)
	{
		in.read(&ch, 1);
		if (in.eof())
			break;
		text.push_back(ch);
		//check if key is existed
		auto it = count_map.find(ch);
		if (it == count_map.end())
			count_map[ch] = 0;

		count_map[ch] += 1;
		++count;
	}
	//build htree
	for (auto ele : count_map)
	{
		add(ele.first, ele.second);
	}
	build();

	//get codetable and weighttable
	for (int i = count_map.size()-1; i != -1; --i)
		_ct[vec[i].data] = getCode(i);
	for (auto ele : count_map)
		_wt[ele.first] = ele.second / count;
}

//encode to binary string
string HCoder::encode(const string &text)
{
	char ch;		// read byte
	char byte = 0;	// dest byte
	// count buffer size;
	size_t length = text.size();

	string res;

	//first  4 bytes is the size of text
	res.push_back(char(length & 0xff));
	res.push_back(char((length >> 8) & 0xff));
	res.push_back(char((length >> 16) & 0xff));
	res.push_back(char((length >> 24) & 0xff));

	int i = 0;	// turn counter
	for(char ch : text)
	{
		// read a new byte in a new turn
		string s(_ct[ch]);

		for (auto c : s)
		{
			byte = (byte << 1) | (c == '1' ? 1 : 0);
			i++;
			// write the byte
			if (i == 8)
			{
				i = 0;
				res.push_back(byte);
				byte = 0;
			}
		}
	}
	// add message to identify left byte
	if(i)
		res.push_back(byte << (8 - i));
	return res;
}

bool getBit(char &byte)
{
	bool bit = byte >> 7;
	byte <<= 1;
	return bit;
}

//decode from binary stream
string HCoder::decode(const string &text)
{
	char byte;
	size_t root = 2 * leaves_num - 2;
	int count = 0;
	size_t temp = root;
	
	string res; //result
	
	stringstream in(text);
	in.seekg(4);
	size_t length = text[0] + (text[1] << 8) + (text[2] << 16) + (text[3] << 24);

	in >> byte;
	while (length)
	{
		if (count == 8)
		{
			in >> byte;
			count = 0;
		}

		// find leave node
		if (temp < leaves_num)
		{
			res.push_back(vec[temp].data);
			temp = root;
			//rest length decrease by 1
			length--;
		}
		// find path to leave node
		else
		{
			bool bt = getBit(byte);
			count++;
			if (bt)
				temp = vec[temp].rChild;
			else
				temp = vec[temp].lChild;
		}
	}
	return res;
}

