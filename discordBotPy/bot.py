import os
import discord
from discord.ext import commands
from dotenv import load_dotenv
from datetime import datetime
import asyncio
import mysql.connector
from mysql.connector import Error
from aiohttp import web

load_dotenv()

TOKEN = os.getenv('DC_BOT_TOKEN')
DB_URL = os.getenv('DB_URL')
DB_USERNAME = os.getenv('DB_USERNAME')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_SCHEMA = "kaimuxstatistics"
DB_TABLE = "discord_raw_message_counts"

intents = discord.Intents.default()
intents.message_content = True
bot = commands.Bot(command_prefix="!", intents=intents)

def connect_to_database():
    try:
        if ":" in DB_URL:
            host, port = DB_URL.split(":")
        else:
            host, port = DB_URL, 3306

        connection = mysql.connector.connect(
            host=host,
            port=int(port),
            user=DB_USERNAME,
            password=DB_PASSWORD,
            database=DB_SCHEMA
        )
        return connection
    except Error as e:
        print(f"Error connecting to MySQL database: {e}")
        return None

def save_message_count_to_db(user_id, message_date, message_count):
    connection = connect_to_database()
    if connection is None:
        return False

    try:
        cursor = connection.cursor()
        query = f"""
        INSERT INTO {DB_TABLE} (dc_user_id, message_date, message_count)
        VALUES (%s, %s, %s)
        """
        cursor.execute(query, (user_id, message_date, message_count))
        connection.commit()
        cursor.close()
        connection.close()
        return True
    except Error as e:
        print(f"Error saving to MySQL database: {e}")
        return False

async def count_messages(user_id, target_date):
    guild = bot.guilds[0]
    message_count = 0

    for channel in guild.text_channels:
        try:
            async for message in channel.history(limit=1000):
                if message.author.id == user_id and message.created_at.date() == target_date:
                    message_count += 1
                await asyncio.sleep(0.1)
        except discord.Forbidden:
            continue

    return message_count

async def handle_request(request):
    data = await request.json()
    user_id = data.get("user_id")
    message_date_str = data.get("message_date")

    if isinstance(message_date_str, list):
        if len(message_date_str) == 1:
            message_date_str = message_date_str[0]
        else:
            return web.json_response({"status": "error", "message": "Invalid date format. Use YYYY-MM-DD."}, status=400)

    try:
        message_date = datetime.strptime(message_date_str, "%Y-%m-%d").date()
    except ValueError:
        return web.json_response({"status": "error", "message": "Invalid date format. Use YYYY-MM-DD."}, status=400)

    message_count = await count_messages(user_id, message_date)

    if save_message_count_to_db(user_id, message_date, message_count):
        return web.json_response({"status": "success", "message_count": message_count})
    else:
        return web.json_response({"status": "error", "message": "Failed to save to database."}, status=500)

@bot.event
async def on_ready():
    print(f'Logged in as {bot.user.name}')
    await bot.tree.sync()
    print("Slash commands synced!")

    app = web.Application()
    app.router.add_post("/check-messages", handle_request)
    runner = web.AppRunner(app)
    await runner.setup()
    site = web.TCPSite(runner, "localhost", 8085)
    await site.start()
    print("HTTP server started on http://localhost:8085")

bot.run(TOKEN)
