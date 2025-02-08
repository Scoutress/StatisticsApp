import os
import discord
from discord.ext import commands
from dotenv import load_dotenv
from datetime import datetime
import asyncio

# Load environment variables from .env file
load_dotenv()

# Get the bot token from the environment variable
TOKEN = os.getenv('DC_BOT_TOKEN')

# Create a bot instance with a command prefix
intents = discord.Intents.default()
intents.message_content = True  # Enable access to message content
bot = commands.Bot(command_prefix="!", intents=intents)

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
    target_date = datetime(2023, 9, 13).date()
    guild = interaction.guild
    message_count = 0

    for channel in guild.text_channels:
        try:
            async for message in channel.history(limit=1000):  # Fetch only the last 1000 messages
                if message.author.id == user_id and message.created_at.date() == target_date:
                    message_count += 1
                await asyncio.sleep(0.1)  # Add a small delay to avoid rate limits
        except discord.Forbidden:
            continue

    # Send the final response
    await interaction.followup.send(
        f"User {user_id} wrote {message_count} messages on {target_date}."
    )

# Force command sync (temporary)
@bot.command()
@commands.is_owner()  # Restrict this command to the bot owner
async def sync(ctx):
    await bot.tree.sync()
    await ctx.send("Slash commands synced!")

# Run the bot
bot.run(TOKEN)
