import csv
import os
from datetime import datetime

csv_path = "C:/Users/Asus/Documents/GitHub/StatisticsApp/server/src/main/java/com/scoutress/KaimuxAdminStats/utils/data/minecraftTickets/minecraft_tickets.csv"
sql_filename = "insert_minecraft_tickets.sql"

employee_ids = {
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

with open(sql_filename, mode='w') as sql_file:
    sql_file.write('-- SQL insert statements for minecraft_tickets\n\n')

    with open(csv_path, mode='r', encoding='utf-8') as csv_file:
        csv_reader = csv.reader(csv_file)
        
        headers = next(csv_reader)
        employee_names = headers[1:]

        for row in csv_reader:
            date = row[0]
            for index, ticket_count in enumerate(row[1:]):
                employee_name = employee_names[index]
                ticket_count = ticket_count.strip()

                if employee_name in employee_ids and ticket_count:
                    try:
                        ticket_count = int(ticket_count)
                        if ticket_count == 0:
                            continue
                    except ValueError:
                        continue

                    employee_id = employee_ids[employee_name]
                    sql_statement = (
                        f"INSERT INTO minecraft_tickets (employee_id, ticket_count, date) "
                        f"VALUES ({employee_id}, {ticket_count}, '{date}');\n"
                    )
                    sql_file.write(sql_statement)

print(f"SQL file '{sql_filename}' successfully generated.")
