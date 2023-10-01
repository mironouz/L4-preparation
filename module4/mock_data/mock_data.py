import csv
import datetime
from random import randint, sample

mockaroo = []

ID = 0
NAME = 1
SURNAME = 2
SUBJECT_NAME = 3
PRIMARY_SKILL = 4 

with open('mockaroo.csv') as csvfile:
    mockaroo_reader = csv.reader(csvfile)
    for row in mockaroo_reader:
        mockaroo.append(row)

def r_1000():
    return randint(1, 1000)

def r_date():
    return datetime.date(randint(2000,2010), randint(1,12),randint(1,28))

def r_phone():
    return ''.join([str(n) for n in sample(range(10), k=11, counts=[11]*10)])

def mock_students(size):
    with open('students.csv', 'w') as csvfile:
        students_writer = csv.writer(csvfile)
        for i in range(1, size + 1):
            students_writer.writerow(
                [
                    i,
                    mockaroo[r_1000()][NAME],
                    mockaroo[r_1000()][SURNAME],
                    r_date(),
                    r_phone(),
                    mockaroo[r_1000()][PRIMARY_SKILL],
                    datetime.datetime.now(),
                    datetime.datetime.now()
                ]
            )

def mock_subjects(size):
    with open('subjects.csv', 'w') as csvfile:
        subjects_writer = csv.writer(csvfile)
        for i in range(1, size + 1):
            subjects_writer.writerow(
                [
                    i,
                    f'{mockaroo[i][SUBJECT_NAME]} and {mockaroo[r_1000()][NAME]}',
                    f'{mockaroo[r_1000()][SURNAME]} {mockaroo[r_1000()][NAME]}'
                ]
            )

def mock_exam_results(size):
    with open('exam_results.csv', 'w') as csvfile:
        exam_results_writer = csv.writer(csvfile)
        for i in range(1, size + 1):
            exam_results_writer.writerow(
                [
                    randint(1, 100000),
                    randint(1, 1000),
                    randint(1, 5) 
                ]
            )

mock_students(100000)
mock_subjects(1000)
mock_exam_results(1000000)