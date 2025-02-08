import os
import discord
from discord.ext import commands
from dotenv import load_dotenv
from datetime import datetime
import asyncio
import mysql.connector
from mysql.connector import Error

# Load environment variables from .env file
load_dotenv()

# Get the bot token and database credentials from the environment variables
TOKEN = os.getenv('DC_BOT_TOKEN')
DB_URL = os.getenv('DB_URL')
DB_USERNAME = os.getenv('DB_USERNAME')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_SCHEMA = "kaimuxstatistics"
DB_TABLE = "discord_raw_message_counts"

# Create a bot instance with a command prefix
intents = discord.Intents.default()
intents.message_content = True  # Enable access to message content
bot = commands.Bot(command_prefix="!", intents=intents)

# Function to connect to the MySQL database
def connect_to_database():
    try:
        # Split the DB_URL into host and port
        if ":" in DB_URL:
            host, port = DB_URL.split(":")
        else:
            host, port = DB_URL, 3306  # Default MySQL port is 3306

        connection = mysql.connector.connect(
            host=host,
            port=int(port),
            user=DB_USERNAME,
            password=DB_PASSWORD,
            database=DB_SCHEMA  # Database name (kaimuxstatistics)
        )
        return connection
    except Error as e:
        print(f"Error connecting to MySQL database: {e}")
        return None

# Function to save message count to the database
def save_message_count_to_db(user_id, message_date, message_count):
    connection = connect_to_database()
    if connection is None:
        return False

    try:
        cursor = connection.cursor()
        query = f"""
        INSERT INTO {DB_TABLE} (username, message_date, message_count)
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

# Event: When the bot is ready
@bot.event
async def on_ready():
    print(f'Logged in as {bot.user.name}')
    await bot.tree.sync()  # Sync all slash commands
    print("Slash commands synced!")

# Command: /test
@bot.tree.command(name="test", description="Start a test")
async def test(interaction: discord.Interaction):
    await interaction.response.send_message("Test was started")

# Command: /check-msgs
@bot.tree.command(name="check-msgs", description="Check how many messages a user wrote on a specific date")
async def check_msgs(interaction: discord.Interaction):
    print("/check-msgs command triggered!")  # Debug print
    await interaction.response.defer()  # Acknowledge the interaction immediately

    user_id = 508674128006479872
    target_date = datetime(2025, 1, 18).date()
    guild = interaction.guild
    message_count = 0

    # Count messages
    for channel in guild.text_channels:
        try:
            async for message in channel.history(limit=1000):  # Fetch only the last 1000 messages
                if message.author.id == user_id and message.created_at.date() == target_date:
                    message_count += 1
                await asyncio.sleep(0.1)  # Add a small delay to avoid rate limits
        except discord.Forbidden:
            continue

    # Save to database
    if save_message_count_to_db(user_id, target_date, message_count):
        await interaction.followup.send(
            f"Message count for user {user_id} on {target_date} saved to the database."
        )
    else:
        await interaction.followup.send(
            f"Failed to save message count for user {user_id} on {target_date}. Check the console for errors."
        )

# Force command sync (temporary)
@bot.command()
@commands.is_owner()  # Restrict this command to the bot owner
async def sync(ctx):
    await bot.tree.sync()
    await ctx.send("Slash commands synced!")

# Run the bot
bot.run(TOKEN)
