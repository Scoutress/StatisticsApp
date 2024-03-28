![Minecraft_Administration_Statistics](https://github.com/Scoutress/StatisticsApp/assets/142579732/b190c1c6-b044-44a3-a2a5-e7c33ee7b741)

This is an application for the collection and analysis of Kaimux Minecraft project administration statistics.

Currently, the program is only intended for personal use with a local database.

---
## Project Status

---
## History

In the middle of 2023, on my own initiative, I decided to collect the statistical data of the players belonging to the administration team and analyze them, turning them into productivity values ​​in the final version. Based on these values, it is possible to judge whether administrators are ready to be promoted or not.

I first created this system in MS Excel, then moved it to Google Sheets, and now I'm slowly trying to recreate all the calculations in Java.

The operation of the program consists of analyzing several types of data. These are: the time admins are on the servers (playtime), responding to Minecraft support messages (MC tickets), and responding to Discord support messages (DC tickets).

---
## Playtime

Thanks to the CoreProtect plugin, I have the ability to monitor when certain players/administrators connect to the servers and when they disconnect. This data is stored in a local SQLite database from which the required data is retrieved.

---
## MC Tickets

The Kaimux project website provides data on when and how many admins responded to Minecraft servers tickets. This data is transferred from this site (currently manually) and further analyzed in my application.

---
## DC Tickets

Responses to Discord tickets are also currently manually taken from the Kaimux project site and further analyzed in my app.
