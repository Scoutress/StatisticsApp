import os
import discord
from discord.ext import commands
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Get the bot token from the environment variable
TOKEN = os.getenv('DC_BOT_TOKEN')

# Create a bot instance with a command prefix
bot = commands.Bot(command_prefix="!", intents=discord.Intents.default())

# Event: When the bot is ready
@bot.event
async def on_ready():
    print(f'Logged in as {bot.user.name}')

# Command: /test
@bot.tree.command(name="test", description="Start a test")
async def test(interaction: discord.Interaction):
    await interaction.response.send_message("Test was started")

# Sync commands (only needed once or when commands are updated)
@bot.event
async def on_ready():
    await bot.tree.sync()
    print(f'Logged in as {bot.user.name}')

# Run the bot
bot.run(TOKEN)
