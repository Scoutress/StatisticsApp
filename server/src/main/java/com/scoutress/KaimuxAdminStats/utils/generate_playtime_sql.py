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
    'D0fka': 6,
    'MelitaLove': 7,
    'Libete': 8,
    'Ariena': 9,
    'Beche_': 11,
    'everly': 12,
    'RichPica': 13,
    'Shizo': 14,
    'BobsBuilder': 15,
    'plrxq': 16,
    '3MAHH': 17
}

csv_directory = 'data/playtime/'

sql_filename = 'insert_all_employees_playtime_data.sql'

with open(sql_filename, mode='w') as sql_file:
    sql_file.write('-- Ensure the table exists\n')
    sql_file.write("""
    CREATE TABLE IF NOT EXISTS login_logout_times (
        id INT AUTO_INCREMENT PRIMARY KEY,
        employee_id INT NOT NULL,
        server_name VARCHAR(255) NOT NULL,
        login_time DATETIME NOT NULL,
        logout_time DATETIME NOT NULL
    );
    \n\n""")

    sql_file.write('-- SQL insert statements for all employees\n\n')

    for employee_name, employee_id in employee_ids.items():
        csv_filename = os.path.join(csv_directory, f'{employee_name}_playtime.csv')

        if not os.path.exists(csv_filename):
            print(f"Warning: File {csv_filename} not found. Skipping.")
            continue

        with open(csv_filename, mode='r') as csv_file:
            csv_reader = csv.reader(csv_file)
            headers = next(csv_reader)

            server_columns = {}
            for idx, header in enumerate(headers):
                server_action = header.split('_')[-1]
                server_name = '_'.join(header.split('_')[:-1])
                if server_name not in server_columns:
                    server_columns[server_name] = {}
                server_columns[server_name][server_action] = idx

            for row in csv_reader:
                for server_name, actions in server_columns.items():
                    connect_index = actions.get('connect')
                    disconnect_index = actions.get('disconnect')

                    if connect_index is not None and disconnect_index is not None:
                        login_time_epoch = row[connect_index].strip()
                        logout_time_epoch = row[disconnect_index].strip()

                        if login_time_epoch and logout_time_epoch:
                            login_time = epoch_to_datetime(login_time_epoch)
                            logout_time = epoch_to_datetime(logout_time_epoch)

                            sql_statement = (
                                "INSERT INTO login_logout_times (employee_id, server_name, login_time, logout_time) "
                                "VALUES ({}, '{}', '{}', '{}');\n".format(
                                    employee_id, server_name, login_time, logout_time
                                )
                            )
                            sql_file.write(sql_statement)

print(f"SQL insert statements for all employees were written to {sql_filename}")
