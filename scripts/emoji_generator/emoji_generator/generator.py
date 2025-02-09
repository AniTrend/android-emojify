import json
import os
from typing import List, Dict, Optional, NoReturn

import requests

from emoji_generator.decorators import run_catching
from emoji_generator.sources import get_emoji
from emoji_generator.models import Emoji
from emoji_generator.utils import parse_emoji_data

__version: Optional[str]

@run_catching
def fetch_emoji_data() -> List[Dict]:
  """Fetch and return emoji data from both sources."""
  try:
    response: Optional[List[Emoji]] = get_emoji(__version)
    return parse_emoji_data(response)
  except requests.exceptions.RequestException as e:
    raise Exception(f"Failed to fetch data: {e}")

@run_catching
def initialize() -> NoReturn:
  if not __version:
    raise Exception("Environment variable `EMOJI_VERSION` is not set")

  emoji_output = fetch_emoji_data()

  output_path = '../../emojify/src/main/assets/emoticons/emoji.json'
  os.makedirs(os.path.dirname(output_path), exist_ok=True)

  with open(output_path, 'w', encoding='utf-8') as stream:
    json.dump(emoji_output, stream, ensure_ascii=False, skipkeys=True)


if __name__ == "__main__":
  __version = os.getenv('EMOJI_VERSION')
  initialize()
