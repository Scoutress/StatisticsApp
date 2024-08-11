import csv
from datetime import datetime
import os

def epoch_to_datetime(epoch):
    return datetime.fromtimestamp(int(epoch)).strftime('%Y-%m-%d %H:%M:%S')

employee_ids = {
    'ItsVaidas': 1,
    'Scoutress': 2,
    'Mboti212': 3,
    'Furija': 4,
    'Ernestasltu12': 5,
    'D0fka': 10,
    'MelitaLove': 12,
    'Libete': 13,
    'Ariena': 14,
    'Sharans': 15,
    'Beche_': 16,
    'everly': 17,
    'RichPica': 18,
    'Shizo': 19,
    'BobsBuilder': 20,
    'plrxq': 21,
    '3MAHH': 22
}

csv_directory = 'data/'

sql_filename = 'insert_all_employees_playtime_data.sql'

with open(sql_filename, mode='w') as sql_file:
    sql_file.write('-- SQL insert statements for all employees\n\n')

    for employee_name, employee_id in employee_ids.items():
        csv_filename = os.path.join(csv_directory, f'{employee_name}_playtime.csv')

        with open(csv_filename, mode='r') as csv_file:
            csv_reader = csv.reader(csv_file)
            headers = next(csv_reader)

            for row in csv_reader:
                for i in range(0, len(row), 2):
                    login_time_epoch = row[i].strip()
                    logout_time_epoch = row[i + 1].strip()

                    if login_time_epoch and logout_time_epoch:
                        server_name = headers[i].split()[0]

                        login_time = epoch_to_datetime(login_time_epoch)
                        logout_time = epoch_to_datetime(logout_time_epoch)

                        sql_statement = (
                            "INSERT INTO login_logout_times (employee_id, server_name, login_time, logout_time) "
                            "VALUES ({}, '{}', '{}', '{}');\n".format(
                                employee_id, server_name, login_time, logout_time
                            )
                        )

                        sql_file.write(sql_statement)

print("SQL insert statements for all employees were written to {}".format(sql_filename))
