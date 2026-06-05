import json
import os
from pathlib import Path
from typing import TYPE_CHECKING, cast

import requests

from emoji_generator.decorators import run_catching
from emoji_generator.sources import get_emoji, get_emoji_shortcodes
from emoji_generator.utils import parse_emoji_data

if TYPE_CHECKING:
  from emoji_generator.models import Emoji

__version: str | None


@run_catching
def fetch_emoji_data() -> list[dict]:
  """Fetch and return emoji data from both sources."""
  try:
    emoji_list: list[Emoji] | None = get_emoji(__version)
    shortcodes_dict: dict[str, str | list[str]] | None = get_emoji_shortcodes(__version)
    if emoji_list and shortcodes_dict:
      for emoji in emoji_list:
        if emoji.hexcode in shortcodes_dict:
          additional_shortcodes = shortcodes_dict[emoji.hexcode]

          if isinstance(additional_shortcodes, str):
            additional_shortcodes = [additional_shortcodes]

          if emoji.shortcodes:
            combined_shortcodes = list(set(emoji.shortcodes + additional_shortcodes))
            emoji.shortcodes = combined_shortcodes
          else:
            emoji.shortcodes = additional_shortcodes

    return cast("list[dict]", parse_emoji_data(emoji_list))
  except requests.exceptions.RequestException as e:
    msg = f"Failed to fetch emoji data: {e}"
    raise RuntimeError(msg) from e


@run_catching
def initialize() -> None:
  if not __version:
    msg = "Environment variable `EMOJI_VERSION` is not set"
    raise RuntimeError(msg)

  emoji_output = fetch_emoji_data()

  output_path = Path("../../emojify/src/main/assets/emoticons/emoji.json")
  output_path.parent.mkdir(parents=True, exist_ok=True)

  output_path.write_text(
    json.dumps(emoji_output, ensure_ascii=False, skipkeys=True),
    encoding="utf-8",
  )


def main() -> None:
  global __version  # noqa: PLW0603
  __version = os.getenv("EMOJI_VERSION")
  initialize()


if __name__ == "__main__":
  main()
