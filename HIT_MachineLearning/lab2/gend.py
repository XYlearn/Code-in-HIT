import csv

with open("sex") as f:
    sex = list(map(int, filter(lambda s:len(s), f.read().split('\n'))))

with open("ages") as f:
    ages = list(map(int, filter(lambda s:len(s), f.read().split('\n'))))

with open("credits") as f:
    credit = list(map(lambda x:int(x)//10000, filter(lambda s:len(s), f.read().split('\n'))))

data = list(zip(sex, ages, credit))
with open("bank_data", "w+") as f:
    writer = csv.writer(f, delimiter=',')
    for row in data:
        writer.writerow(row)
