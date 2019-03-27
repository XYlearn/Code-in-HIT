#include "BMI.h"

float StudentBMI::calc()
{
	float height = this->m_height;
	float weight = this->m_weight;
	float bmi;
	if (this->valid())
	{
		bmi = weight / (height * height);
		this->bmi = bmi;
		if (bmi < 18.5)
			this->bmiFlag = UNDERWEIGHT;
		else if (bmi < 24)
			this->bmiFlag = NORMAL;
		else if (bmi < 28)
			this->bmiFlag = OVERWEIGHT;
		else if (bmi < 32)
			this->bmiFlag = FAT;
		else
			this->bmiFlag = VERYFAT;
	}
	return bmi;
}

std::string StudentBMI::analyze()
{
	switch (this->bmiFlag)
	{
	case UNSET:
		return "Unset";
	case UNDERWEIGHT:
		return "Underweight";
	case NORMAL:
		return "Normal";
	case OVERWEIGHT:
		return "Overweight";
	case FAT:
		return "Fat";
	case VERYFAT:
		return "VeryFat";
	default:
		return "Unknown";
	}
}
