import json
import unicodedata
from collections.abc import Mapping
from pathlib import Path
from typing import Any

from emoji_generator.utils import compute_html_dec, compute_html_hex, compute_unicode

FIXTURE_PATH = Path(__file__).resolve().parents[1] / "fixtures" / "emoji-1.9.7.json"

CONFLICTED_ALIAS_TARGETS = {
  "beetle": "🪲",
  "cat": "🐈️",
  "city_sunset": "🌇",
  "computer": "🖥️",
  "cow": "🐄",
  "dog": "🐕️",
  "email": "📧",
  "frowning_face": "😦",
  "horse": "🐎",
  "japan": "🇯🇵",
  "jar": "🫙",
  "jolly_roger": "🏴‍☠️",
  "man_in_tuxedo": "🤵‍♂️",
  "mouse": "🐁",
  "ng": "🆖",
  "no": "👎️",
  "o": "🅾️",
  "om": "🕉️",
  "pencil": "✏️",
  "pig": "🐖",
  "pirate_flag": "🏴‍☠️",
  "point_up": "👆️",
  "point_up_2": "☝️",
  "rabbit": "🐇",
  "sunglasses": "🕶️",
  "sunny": "🌤️",
  "tiger": "🐅",
  "train": "🚆",
  "umbrella": "☂️",
  "up": "🔼",
  "whale": "🐋",
}
CONFLICTED_ALIASES = frozenset(CONFLICTED_ALIAS_TARGETS)

MERGE_FITZPATRICK_CAPABILITY_WITH_OR = True
SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK = frozenset({"🧟", "🧟‍♂️", "🧟‍♀️"})
SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK = frozenset(
  {
    "👫",
    "👬",
    "👭",
    "💏",
    "💑",
    "👩‍❤️‍👩",
    "👨‍❤️‍👨",
    "👩‍❤️‍💋‍👩",
    "👨‍❤️‍💋‍👨",
    "👨‍⚖️",
    "👩‍⚖️",
  }
)


def load_legacy_records(path: Path = FIXTURE_PATH) -> list[dict[str, Any]]:
  with path.open(encoding="utf-8") as source:
    records = json.load(source)
  if not isinstance(records, list) or any(
    not isinstance(record, dict) for record in records
  ):
    msg = f"Legacy shortcode fixture must be an array of objects: {path}"
    raise ValueError(msg)
  return records


def normalize_emoji(emoji: str) -> str:
  return emoji.replace("\ufe0e", "").replace("\ufe0f", "")


def shortcode_list(value: object) -> list[str]:
  if isinstance(value, str):
    return [value]
  if isinstance(value, list):
    return [item for item in value if isinstance(item, str)]
  return []


def fitzpatrick_capability_drifts(
  current_records: list[dict[str, Any]],
  legacy_records: list[dict[str, Any]],
) -> tuple[set[str], set[str]]:
  current_by_normalized_emoji: dict[str, dict[str, Any]] = {}
  for record in current_records:
    emoji = record.get("emoji")
    if isinstance(emoji, str):
      current_by_normalized_emoji.setdefault(normalize_emoji(emoji), record)

  legacy_true_current_false: set[str] = set()
  legacy_false_current_true: set[str] = set()
  for legacy in legacy_records:
    emoji = legacy.get("emoji")
    if not isinstance(emoji, str):
      continue
    current = current_by_normalized_emoji.get(normalize_emoji(emoji))
    if current is None:
      continue
    legacy_supports = legacy.get("supports_fitzpatrick") is True
    current_supports = current.get("supportsFitzpatrick") is True
    if legacy_supports and not current_supports:
      legacy_true_current_false.add(emoji)
    elif current_supports and not legacy_supports:
      legacy_false_current_true.add(emoji)
  return legacy_true_current_false, legacy_false_current_true


def _apply_fitzpatrick_capability_policy(
  current_record: dict[str, Any],
  legacy_record: dict[str, Any],
) -> None:
  if MERGE_FITZPATRICK_CAPABILITY_WITH_OR and (
    current_record.get("supportsFitzpatrick") is True
    or legacy_record.get("supports_fitzpatrick") is True
  ):
    current_record["supportsFitzpatrick"] = True


def _new_legacy_record(record: dict[str, Any], shortcodes: list[str]) -> dict[str, Any]:
  emoji = record["emoji"]
  normalized_emoji = unicodedata.normalize("NFC", emoji)
  result: dict[str, Any] = {
    "emoji": emoji,
    "description": record.get("description", ""),
  }
  if "tags" in record and record["tags"] is not None:
    result["tags"] = record["tags"]
  result["shortCodes"] = shortcodes
  result["unicode"] = compute_unicode(normalized_emoji)
  result["htmlDec"] = compute_html_dec(normalized_emoji)
  result["htmlHex"] = compute_html_hex(normalized_emoji)
  _apply_fitzpatrick_capability_policy(result, record)
  return result


def merge_legacy_shortcodes(
  current_records: list[dict[str, Any]],
  legacy_records: list[dict[str, Any]],
) -> list[dict[str, Any]]:
  """Merge accepted legacy aliases ahead of current shortcodes, preserving order."""
  result = [dict(record) for record in current_records]
  exact_targets: dict[str, int] = {}
  normalized_targets: dict[str, list[int]] = {}

  for index, record in enumerate(result):
    emoji = record.get("emoji")
    if not isinstance(emoji, str):
      continue
    if emoji in exact_targets:
      msg = f"Current catalog contains duplicate emoji records: {emoji!r}"
      raise ValueError(msg)
    exact_targets[emoji] = index
    normalized_targets.setdefault(normalize_emoji(emoji), []).append(index)

  for legacy in legacy_records:
    emoji = legacy.get("emoji")
    if not isinstance(emoji, str):
      msg = f"Legacy record has no emoji string: {legacy!r}"
      raise ValueError(msg)  # noqa: TRY004

    target_index = exact_targets.get(emoji)
    if target_index is None:
      candidates = normalized_targets.get(normalize_emoji(emoji), [])
      if len(candidates) > 1:
        msg = f"Legacy emoji has multiple normalized current targets: {emoji!r}"
        raise ValueError(msg)
      target_index = candidates[0] if candidates else None

    retained_aliases = [
      code
      for code in shortcode_list(legacy.get("aliases"))
      if code not in CONFLICTED_ALIASES
    ]
    if target_index is None:
      if retained_aliases:
        new_record = _new_legacy_record(legacy, list(dict.fromkeys(retained_aliases)))
        target_index = len(result)
        result.append(new_record)
        exact_targets[emoji] = target_index
        normalized_targets.setdefault(normalize_emoji(emoji), []).append(target_index)
      continue

    record = result[target_index]
    current_shortcodes = shortcode_list(record.get("shortCodes"))
    record["shortCodes"] = list(dict.fromkeys(retained_aliases + current_shortcodes))
    _apply_fitzpatrick_capability_policy(record, legacy)

  shortcode_targets: dict[str, Any] = {}
  for record in result:
    emoji = record.get("emoji")
    for code in shortcode_list(record.get("shortCodes")):
      previous_target = shortcode_targets.get(code)
      if previous_target is not None and previous_target != emoji:
        msg = f"Shortcode {code!r} maps to both {previous_target!r} and {emoji!r}"
        raise ValueError(msg)
      shortcode_targets[code] = emoji

  return result


def merge_current_shortcodes(
  emoji_list: list[Any], shortcode_map: Mapping[str, object]
) -> None:
  """Apply preset aliases first, then any source-only values, with stable deduplication."""
  for emoji in emoji_list:
    preset_value = shortcode_map.get(emoji.hexcode)
    if preset_value is None:
      continue
    preset_shortcodes = shortcode_list(preset_value)
    source_shortcodes = shortcode_list(emoji.shortcodes)
    emoji.shortcodes = list(dict.fromkeys(preset_shortcodes + source_shortcodes))
