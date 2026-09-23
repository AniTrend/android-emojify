from __future__ import annotations

import json
import os
from pathlib import Path
from typing import TYPE_CHECKING, cast

import requests

from emoji_generator.compatibility import (
  load_legacy_records,
  merge_current_shortcodes,
  merge_legacy_shortcodes,
)
from emoji_generator.decorators import run_catching
from emoji_generator.sources import get_emoji, get_emoji_shortcodes
from emoji_generator.utils import parse_emoji_data

if TYPE_CHECKING:
  from collections.abc import Mapping

  from emoji_generator.models import Emoji

__version: str | None


def generate_catalog(
  emoji_list: list[Emoji],
  shortcodes_dict: Mapping[str, str | list[str]],
  legacy_records: list[dict] | None = None,
) -> list[dict]:
  merge_current_shortcodes(emoji_list, shortcodes_dict)
  current_records = cast("list[dict]", parse_emoji_data(emoji_list))
  return merge_legacy_shortcodes(
    current_records,
    load_legacy_records() if legacy_records is None else legacy_records,
  )


@run_catching
def fetch_emoji_data() -> list[dict]:
  """Fetch and return emoji data from both sources."""
  try:
    emoji_list: list[Emoji] | None = get_emoji(__version)
    shortcodes_dict: dict[str, str | list[str]] | None = get_emoji_shortcodes(__version)
    if emoji_list and shortcodes_dict is not None:
      return generate_catalog(emoji_list, shortcodes_dict)
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
