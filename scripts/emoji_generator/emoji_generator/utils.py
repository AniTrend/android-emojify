from typing import Any
import unicodedata

from emoji_generator.decorators import run_catching
from emoji_generator.models import Emoji


@run_catching
def compute_unicode(emoji: str) -> str:
  """
  Return a string containing Java-style Unicode escape sequences (\\uXXXX)
  for each character in `s`. Characters above U+FFFF are converted
  to UTF-16 surrogate pairs.

  Examples:
    - '🤣' => '\\uD83E\\uDD23'
    - '😀' => '\\uD83D\\uDE00'
  """
  out = []
  for ch in emoji:
    code_point = ord(ch)
    # If it's in the Basic Multilingual Plane, a single \uXXXX is fine
    if code_point <= 0xFFFF:
      out.append(f"\\u{code_point:04X}")
    else:
      # Convert to a surrogate pair
      cp_prime = code_point - 0x10000  # subtract the BMP limit
      high = 0xD800 + (cp_prime >> 10)
      low = 0xDC00 + (cp_prime & 0x3FF)
      out.append(f"\\u{high:04X}\\u{low:04X}")
  return "".join(out)


@run_catching
def compute_html_dec(emoji: str) -> str:
  """Convert emoji to decimal HTML entities."""
  return "".join(f"&#{ord(char)};" for char in emoji)


@run_catching
def compute_html_hex(emoji: str) -> str:
  """Convert emoji to hexadecimal HTML entities."""
  return "".join(f"&#x{ord(char):x};" for char in emoji)


@run_catching
def as_dict_with_no_none(data: Any) -> Any:
  if isinstance(data, dict):
    return {k: as_dict_with_no_none(v) for k, v in data.items() if v is not None}
  if isinstance(data, list):
    return [as_dict_with_no_none(item) for item in data]
  return data


@run_catching
def parse_emoji_data(data: list[Emoji]) -> list[dict]:
  emojis = []

  for item in data:
    normalized_emoji = unicodedata.normalize("NFC", item.emoji)
    result: dict = {
      "emoji": item.emoji,
      "description": item.label,
      "tags": item.tags,
      "shortCodes": item.shortcodes,
      "unicode": compute_unicode(normalized_emoji),
      "htmlDec": compute_html_dec(normalized_emoji),
      "htmlHex": compute_html_hex(normalized_emoji),
      "supportsFitzpatrick": len(item.skins) > 0 if item.skins is not None else None,
    }
    emojis.append(as_dict_with_no_none(result))

  return emojis
