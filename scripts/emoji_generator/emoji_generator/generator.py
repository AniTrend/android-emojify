import json
import os
from typing import List, Dict, Optional, Union

import requests

from emoji_generator.decorators import run_catching
from emoji_generator.sources import get_emoji, get_emoji_shortcodes
from emoji_generator.models import Emoji
from emoji_generator.utils import parse_emoji_data

__version: Optional[str]

@run_catching
def fetch_emoji_data() -> List[Dict]:
  """Fetch and return emoji data from both sources."""
  try:
    # Fetch emoji data and shortcodes
    emoji_list: Optional[List[Emoji]] = get_emoji(__version)
    shortcodes_dict: Optional[Dict[str, Union[str, List[str]]]] = get_emoji_shortcodes(__version)
    
    # Merge shortcodes into emoji objects
    if emoji_list and shortcodes_dict:
      for emoji in emoji_list:
        # Look up shortcodes by hexcode
        if emoji.hexcode in shortcodes_dict:
          additional_shortcodes = shortcodes_dict[emoji.hexcode]
          
          # Handle the case where shortcodes_dict value might be a string or list
          if isinstance(additional_shortcodes, str):
            additional_shortcodes = [additional_shortcodes]
          
          # Merge with existing shortcodes
          if emoji.shortcodes:
            # Combine existing and new shortcodes, removing duplicates
            combined_shortcodes = list(set(emoji.shortcodes + additional_shortcodes))
            emoji.shortcodes = combined_shortcodes
          else:
            # Use the shortcodes from the mapping
            emoji.shortcodes = additional_shortcodes
    
    return parse_emoji_data(emoji_list)
  except requests.exceptions.RequestException as e:
    raise Exception(f"Failed to fetch data: {e}")

@run_catching
def initialize() -> None:
  if not __version:
    raise Exception("Environment variable `EMOJI_VERSION` is not set")

  emoji_output = fetch_emoji_data()

  output_path = '../../emojify/src/main/assets/emoticons/emoji.json'
  os.makedirs(os.path.dirname(output_path), exist_ok=True)

  with open(output_path, 'w', encoding='utf-8') as stream:
    json.dump(emoji_output, stream, ensure_ascii=False, skipkeys=True)

def main() -> None:
  global __version
  __version = os.getenv('EMOJI_VERSION')
  initialize()

if __name__ == "__main__":
  main()
