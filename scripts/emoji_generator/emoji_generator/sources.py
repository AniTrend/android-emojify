import requests

from emoji_generator.decorators import run_catching
from emoji_generator.models import Emoji

__TIME_OUT = 10


@run_catching
def get_emoji(version: str) -> list[Emoji]:
  emoji_url = f"https://cdn.jsdelivr.net/npm/emojibase-data@{version}/en/data.json"
  response = requests.get(emoji_url, timeout=__TIME_OUT)
  response.raise_for_status()
  response_data: dict = response.json()
  emojis = [Emoji.from_dict(item) for item in response_data]
  return emojis


@run_catching
def get_emoji_shortcodes(version: str) -> dict[str, str | list[str]]:
  """
  Fetch emoji shortcodes from the emojibase CDN. The structure will be the emoji hexcode as the key
  and the shortcodes as the value. e.g. {"2049": ["exclamation_question","interrobang"]}

  Returns a dictionary mapping hexcodes to shortcode strings or lists.
  """
  shortcode_url = f"https://cdn.jsdelivr.net/npm/emojibase-data@{version}/en/shortcodes/emojibase.json"
  response = requests.get(shortcode_url, timeout=__TIME_OUT)
  response.raise_for_status()
  response_data: dict[str, str | list[str]] = response.json()
  return response_data
