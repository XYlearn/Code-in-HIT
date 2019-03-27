
#include "htree.h"
#include "hcoder.h"
#include <map>
#include <iostream>
#include <sstream>
#include <fstream>

using namespace std;

int main(int argc, char *argv[])
{
	// check process args
	if (argc != 2)
	{
		cout << "[-]Input filename missing" << endl;
		exit(0);
	}

	fstream f(argv[1]);
	if (!f.is_open())
	{
		cout << "[-]Can't open file";
		exit(0);
	}

	stringstream ss;
	ss << f.rdbuf();
	string content(ss.str());
	// close input file
	f.close();

	// init hcoder
	HCoder coder;
	coder.init(ss);	

	// encode
	string encoded = coder.encode(content);
	// decode
	string decoded = coder.decode(encoded);

	// print hcoder information
	cout << "Codes:" << endl;
	coder.printCodes();
	cout << "\nWeights:" << endl;
	coder.printWeights();
	cout << "\ncompression ratio:" << (double)encoded.size() / content.size() << endl;

	// open enc_file
	string enc_filename(argv[1]);
	enc_filename.substr(enc_filename.find_last_of('/') + 1);
	enc_filename = "./" + enc_filename + ".enc";

	fstream enc_file(enc_filename, ios::out);
	if (!enc_file.is_open())
	{
		cout << "[-]Can't open dest file/n";
		exit(0);
	}
	// write encoded to enc_file
	enc_file << encoded;
	cout << "[+]" << argv[1] << "was successfully encoded to " << enc_filename << endl;
	//close enc_file
	enc_file.close();

	cout << "Decoded Result:\n" << decoded;

	system("pause");
	return 0;
}
