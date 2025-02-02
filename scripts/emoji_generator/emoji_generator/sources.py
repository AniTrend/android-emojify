from typing import List, Dict

import requests

from emoji_generator.decorators import run_catching
from emoji_generator.models import Emoji

__TIME_OUT = 10


@run_catching
def get_emoji(version: str) -> List[Emoji]:
  emoji_url = f"https://cdn.jsdelivr.net/npm/emojibase-data@{version}/en/data.json"
  response = requests.get(emoji_url, timeout=__TIME_OUT)
  response.raise_for_status()
  response_data: Dict = response.json()
  emojis = [Emoji.from_dict(item) for item in response_data]
  return emojis
