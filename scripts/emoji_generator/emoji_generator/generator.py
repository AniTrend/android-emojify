import json
import os

import requests

from emoji_generator.decorators import run_catching
from emoji_generator.models import Emoji
from emoji_generator.sources import get_emoji, get_emoji_shortcodes
from emoji_generator.utils import parse_emoji_data

__version: str | None


@run_catching
def fetch_emoji_data() -> list[dict]:
  """Fetch and return emoji data from both sources."""
  try:
    # Fetch emoji data and shortcodes
    emoji_list: list[Emoji] | None = get_emoji(__version)
    shortcodes_dict: dict[str, str | list[str]] | None = get_emoji_shortcodes(__version)
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
    msg = f"Failed to fetch emoji data: {e}"
    raise Exception(msg) from e


@run_catching
def initialize() -> None:
  if not __version:
    msg = "Environment variable `EMOJI_VERSION` is not set"
    raise Exception(msg)

  emoji_output = fetch_emoji_data()

  output_path = "../../emojify/src/main/assets/emoticons/emoji.json"
  os.makedirs(os.path.dirname(output_path), exist_ok=True)

  with open(output_path, "w", encoding="utf-8") as stream:
    json.dump(emoji_output, stream, ensure_ascii=False, skipkeys=True)


def main() -> None:
  global __version
  __version = os.getenv("EMOJI_VERSION")
  initialize()


if __name__ == "__main__":
  main()
