#pragma once
#include <string>
#include <vector>
#include <algorithm>

#ifndef BMI_H_
#define BMI_H_

enum BmiFlag
{
	UNSET,
	UNDERWEIGHT, 
	NORMAL, 
	OVERWEIGHT, 
	FAT, 
	VERYFAT
};

class StudentBMI
{
	/*attributes*/
private:
	std::string id;
	std::string name;
	float m_height;
	float m_weight;
	float bmi;
	BmiFlag bmiFlag;
public:
	/*constructors*/
	StudentBMI(std::string id, std::string name, float height, float weight) :
		id(id), name(name), m_height(height), m_weight(weight), bmiFlag(UNSET) {}
	StudentBMI() {}

	/*set methods*/
	void setHeight(float height) { this->m_height = height; }
	void setWeight(float weight) { this->m_weight = weight; }
	void setId(std::string id) { this->id = id; }
	void setName(std::string name) { this->name = name; }

	/*get methods*/
	float getBmi() const { return this->bmi; }
	float getHeight() const { return this->m_height; }
	float getWeight() const { return this->m_weight; }
	std::string getId() const { return this->id; }
	std::string getName() const { return this->name; }

	bool valid() const { return this->m_height > 0 && this->m_weight > 0; }
	bool unset() { return this->bmiFlag == UNSET; }

	/*calculate the bmi*/
	float calc();
	/*get the bmi analyze result*/
	std::string analyze();
};

class StudentBMIs
{
private:
	std::vector<StudentBMI> vec;
public:
	void add(StudentBMI &a) { vec.push_back(a); }
	std::vector<StudentBMI> getVec() const { return this->vec; }

	void sortByName() 
	{
		std::sort(vec.begin(), vec.end(), 
			[](const StudentBMI & a, const StudentBMI & b) { return a.getName() < b.getName(); }); 
	}
	void sortById() 
	{
		std::sort(vec.begin(), vec.end(), 
			[](const StudentBMI & a, const StudentBMI & b) { return a.getId() < b.getId(); });
	}
	void sortByHeight() 
	{
		std::sort(vec.begin(), vec.end(), 
			[](const StudentBMI & a, const StudentBMI & b) { return a.getHeight() < b.getHeight(); }); 
	}
	void sortByWeight() 
	{
		std::sort(vec.begin(), vec.end(), 
			[](const StudentBMI & a, const StudentBMI & b) { return a.getWeight() < b.getWeight(); });
	}
	void sortByBMI() 
	{ 
		std::sort(vec.begin(), vec.end(), 
			[](const StudentBMI & a, const StudentBMI & b) { return a.getBmi() < b.getBmi(); }); 
	}
};

#endif
