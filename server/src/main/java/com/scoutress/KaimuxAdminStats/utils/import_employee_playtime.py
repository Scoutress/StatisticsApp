import csv
from datetime import datetime
import os

# Funkcija, skirta konvertuoti epochos laiką į DATETIME formatą
def epoch_to_datetime(epoch):
    return datetime.fromtimestamp(int(epoch)).strftime('%Y-%m-%d %H:%M:%S')

# Darbuotojų vardai ir jų ID
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

# CSV failų direktorija
csv_directory = 'data/'

# SQL failo pavadinimas
sql_filename = 'insert_all_employees_playtime_data.sql'

# Sukurkite ir atidarykite SQL failą rašymui
with open(sql_filename, mode='w') as sql_file:
    # SQL failo pradžioje sukurkite SQL komentarą
    sql_file.write('-- SQL insert statements for all employees\n\n')

    # Pereikime per kiekvieną darbuotoją ir jo failą
    for employee_name, employee_id in employee_ids.items():
        # Nurodykite kiekvieno darbuotojo CSV failo pilną kelią
        csv_filename = os.path.join(csv_directory, f'{employee_name}_playtime.csv')

        # Atidarykite CSV failą ir skaitykite duomenis
        with open(csv_filename, mode='r') as csv_file:
            csv_reader = csv.reader(csv_file)
            headers = next(csv_reader)  # Nuskaitome antraštės eilutę

            # Eikite per kiekvieną eilutę CSV faile
            for row in csv_reader:
                # Eikite per kiekvieną stulpelį poromis (prisijungimas, atsijungimas)
                for i in range(0, len(row), 2):
                    login_time_epoch = row[i].strip()
                    logout_time_epoch = row[i + 1].strip()

                    # Patikriname, ar login_time_epoch ir logout_time_epoch nėra tušti
                    if login_time_epoch and logout_time_epoch:
                        server_name = headers[i].split()[0]  # Ištraukiame serverio pavadinimą iš antraštės

                        # Konvertuojame epochos laiką į DATETIME formatą
                        login_time = epoch_to_datetime(login_time_epoch)
                        logout_time = epoch_to_datetime(logout_time_epoch)

                        # Sukuriame SQL INSERT komandą be duration
                        sql_statement = (
                            "INSERT INTO login_logout_times (employee_id, server_name, login_time, logout_time) "
                            "VALUES ({}, '{}', '{}', '{}');\n".format(
                                employee_id, server_name, login_time, logout_time
                            )
                        )

                        # Rašome SQL komandą į failą
                        sql_file.write(sql_statement)

print("SQL insert statements for all employees were written to {}".format(sql_filename))
