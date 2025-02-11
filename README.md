![Minecraft_Administration_Statistics](https://github.com/Scoutress/StatisticsApp/assets/142579732/b190c1c6-b044-44a3-a2a5-e7c33ee7b741)

This is an application for the collection and analysis of Kaimux Minecraft project administration statistics.

Currently, the program is only intended for personal use with a local database.

---
## Project Status

:construction: <sub>Work in Progress (WIP)</sub>

---
## History

In the middle of 2023, on my own initiative, I decided to collect the statistical data of the players belonging to the administration team and analyze them, turning them into productivity values ​​in the final version. Based on these values, it is possible to judge whether administrators are ready to be promoted or not.

I first created this system in MS Excel and then moved it to Google Sheets. Now, I'm slowly trying to recreate all the calculations in Java.

The operation of the program consists of analyzing several types of data. What data I can analyze at the moment: the time admins are on the servers (playtime), responding to Minecraft support messages (MC tickets), complaints, and activity on Discord channels (DC messages).

---
## Playtime

Thanks to the CoreProtect plugin, I can monitor when certain players/administrators connect to the servers and when they disconnect. This data is stored in a local (server-side) SQLite database from which the required data is retrieved.

---
## MC Tickets

The Kaimux project website provides data on when and how many admins responded to Minecraft servers tickets. This data is transferred from this site using API and further analyzed in my application.

---
## Complaints
If an administrator uses his/her admin powers incorrectly or abuses them, every player could fill out a complaint form with some proof. If the complaint is correct, that administrator gets one complaint and it will remain in history for the rest of admin's work time. A certain number of complaints means the dismissal of the administrator.

---
## DC messages

After created custom Discord bot, now I can automaticaly get data, how many messages each administrator wrote each day. This date is send to my local database and later further processed.
