import json
from collections import defaultdict
from pathlib import Path

import emoji_generator.compatibility as compatibility
from emoji_generator.compatibility import (
  CONFLICTED_ALIASES,
  CONFLICTED_ALIAS_TARGETS,
  FIXTURE_PATH,
  MERGE_FITZPATRICK_CAPABILITY_WITH_OR,
  SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK,
  SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK,
  fitzpatrick_capability_drifts,
  merge_legacy_shortcodes,
  normalize_emoji,
  shortcode_list,
)
from emoji_generator.generator import generate_catalog
from emoji_generator.models import Emoji

ROOT = Path(__file__).resolve().parents[3]
ASSET_PATH = ROOT / "emojify/src/main/assets/emoticons/emoji.json"

EXPECTED_CONFLICTED_ALIASES = frozenset(
  {
    "beetle",
    "cat",
    "city_sunset",
    "computer",
    "cow",
    "dog",
    "email",
    "frowning_face",
    "horse",
    "japan",
    "jar",
    "jolly_roger",
    "man_in_tuxedo",
    "mouse",
    "ng",
    "no",
    "o",
    "om",
    "pencil",
    "pig",
    "pirate_flag",
    "point_up",
    "point_up_2",
    "rabbit",
    "sunglasses",
    "sunny",
    "tiger",
    "train",
    "umbrella",
    "up",
    "whale",
  }
)

EXPECTED_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK = frozenset({"🧟", "🧟‍♂️", "🧟‍♀️"})
EXPECTED_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK = frozenset(
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


def load_json_records(path: Path) -> list[dict]:
  with path.open(encoding="utf-8") as source:
    payload = json.load(source)
  assert isinstance(payload, list)
  return payload


def shortcode_index(records: list[dict]) -> dict[str, dict]:
  result: dict[str, dict] = {}
  for record in records:
    for code in shortcode_list(record.get("shortCodes")):
      assert code not in result, f"Shortcode collision for {code!r}"
      result[code] = record
  return result


def test_legacy_fixture_inventory():
  legacy_records = load_json_records(FIXTURE_PATH)
  aliases = [alias for record in legacy_records for alias in record["aliases"]]

  assert len(legacy_records) == 1603
  assert len(aliases) == 1969
  assert sum(len(record["aliases"]) > 1 for record in legacy_records) == 240
  assert sum(record.get("supports_fitzpatrick") is True for record in legacy_records) == 187


def test_legacy_aliases_merge_collision_free_and_conflicts_keep_modern_destinations():
  legacy_records = load_json_records(FIXTURE_PATH)
  current_records = load_json_records(ASSET_PATH)
  merged_records = merge_legacy_shortcodes(current_records, legacy_records)
  by_shortcode = shortcode_index(merged_records)
  by_normalized_emoji: dict[str, list[dict]] = defaultdict(list)
  for record in merged_records:
    by_normalized_emoji[normalize_emoji(record["emoji"])].append(record)

  assert CONFLICTED_ALIASES == EXPECTED_CONFLICTED_ALIASES
  assert len(CONFLICTED_ALIASES) == 31
  assert len(by_shortcode) == sum(len(shortcode_list(record.get("shortCodes"))) for record in merged_records)
  assert len(by_shortcode) == 2_590 + 784 == 3_374

  for legacy in legacy_records:
    targets = by_normalized_emoji.get(normalize_emoji(legacy["emoji"]), [])
    for alias in legacy["aliases"]:
      if alias in CONFLICTED_ALIASES:
        continue
      assert len(targets) == 1, (legacy["emoji"], alias, targets)
      assert alias in by_shortcode, alias
      assert by_shortcode[alias]["emoji"] == targets[0]["emoji"]

  for alias, expected_emoji in CONFLICTED_ALIAS_TARGETS.items():
    assert by_shortcode[alias]["emoji"] == expected_emoji

  assert by_shortcode["cat"]["emoji"].startswith("\U0001f408")
  assert by_shortcode["sunny"]["emoji"] == "🌤️"
  assert by_shortcode["jolly_roger"]["emoji"] == by_shortcode["pirate_flag"]["emoji"]


def test_canonical_order_conflicted_demotions_and_dropped_malformed_record():
  legacy_records = load_json_records(FIXTURE_PATH)
  current_records = load_json_records(ASSET_PATH)
  merged_records = merge_legacy_shortcodes(current_records, legacy_records)
  by_normalized_emoji: dict[str, list[dict]] = defaultdict(list)
  for record in merged_records:
    by_normalized_emoji[normalize_emoji(record["emoji"])].append(record)

  expected_canonicals = {
    "😄": "smile",
    "😀": "grinning",
    "👋": "wave",
    "👦": "boy",
  }
  for emoji, canonical in expected_canonicals.items():
    record = by_normalized_emoji[normalize_emoji(emoji)][0]
    assert record["shortCodes"][0] == canonical
  assert by_normalized_emoji[normalize_emoji("👦")][0]["supportsFitzpatrick"] is True

  exact_legacy_canonicals = 0
  conflicted_first_demotions = 0
  dropped_records = 0
  for legacy in legacy_records:
    retained = [alias for alias in legacy["aliases"] if alias not in CONFLICTED_ALIASES]
    targets = by_normalized_emoji.get(normalize_emoji(legacy["emoji"]), [])
    if not targets:
      dropped_records += 1
      assert all(alias in CONFLICTED_ALIASES for alias in legacy["aliases"])
      continue

    target = targets[0]
    if retained:
      assert target["shortCodes"][0] == retained[0]
    if target["shortCodes"][0] == legacy["aliases"][0]:
      exact_legacy_canonicals += 1
    else:
      conflicted_first_demotions += 1

  assert (exact_legacy_canonicals, conflicted_first_demotions, dropped_records) == (1575, 27, 1)


def test_fitzpatrick_source_drift_sets_are_pinned_and_legacy_or_current_wins():
  legacy_records = load_json_records(FIXTURE_PATH)
  current_records = load_json_records(ASSET_PATH)
  merged_records = merge_legacy_shortcodes(current_records, legacy_records)
  by_normalized_emoji: dict[str, list[dict]] = defaultdict(list)
  for record in merged_records:
    by_normalized_emoji[normalize_emoji(record["emoji"])].append(record)

  assert SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK == (
    EXPECTED_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK
  )
  assert SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK == (
    EXPECTED_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK
  )

  restored_legacy_capabilities = {
    emoji
    for emoji in EXPECTED_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK
    if by_normalized_emoji[normalize_emoji(emoji)][0].get("supportsFitzpatrick") is True
  }
  assert restored_legacy_capabilities == EXPECTED_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK
  legacy_true_current_false, legacy_false_current_true = fitzpatrick_capability_drifts(
    merged_records, legacy_records
  )
  assert legacy_true_current_false == frozenset()
  assert legacy_false_current_true == EXPECTED_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK
  assert MERGE_FITZPATRICK_CAPABILITY_WITH_OR is True


def test_fitzpatrick_capability_policy_switch_can_disable_legacy_value(monkeypatch):
  monkeypatch.setattr(compatibility, "MERGE_FITZPATRICK_CAPABILITY_WITH_OR", False)
  current_records = [{"emoji": "🧟", "supportsFitzpatrick": False, "shortCodes": []}]
  legacy_records = [{"emoji": "🧟", "supports_fitzpatrick": True, "aliases": []}]

  merged = merge_legacy_shortcodes(current_records, legacy_records)

  assert merged[0]["supportsFitzpatrick"] is False


def test_new_legacy_records_retain_legacy_fitzpatrick_capability():
  merged = merge_legacy_shortcodes(
    [],
    [{"emoji": "🧟", "supports_fitzpatrick": True, "aliases": ["zombie"]}],
  )

  assert merged[0]["supportsFitzpatrick"] is True


def test_generation_pipeline_merges_fixture_aliases_after_preset_order():
  emoji_data = [
    {
      "emoji": "😀",
      "hexcode": "1F600",
      "label": "grinning face",
      "shortcodes": ["source_alias", "grinning_face"],
      "tags": [],
      "skins": None,
    }
  ]
  preset = {"1F600": ["grinning", "grinning_face"]}
  legacy = [
    {
      "emoji": "😀",
      "description": "grinning face",
      "aliases": ["historic", "grinning"],
      "tags": [],
    }
  ]

  first = generate_catalog([Emoji.from_dict(emoji_data[0])], preset, legacy)
  second = generate_catalog([Emoji.from_dict(emoji_data[0])], preset, legacy)

  assert first[0]["shortCodes"] == ["historic", "grinning", "grinning_face", "source_alias"]
  assert json.dumps(first, ensure_ascii=False, skipkeys=True).encode() == json.dumps(
    second, ensure_ascii=False, skipkeys=True
  ).encode()


def test_merged_catalog_serialization_is_byte_stable_and_idempotent():
  legacy_records = load_json_records(FIXTURE_PATH)
  current_records = load_json_records(ASSET_PATH)
  once = merge_legacy_shortcodes(current_records, legacy_records)
  twice = merge_legacy_shortcodes(once, legacy_records)
  first_bytes = json.dumps(once, ensure_ascii=False, skipkeys=True).encode("utf-8")
  second_bytes = json.dumps(twice, ensure_ascii=False, skipkeys=True).encode("utf-8")

  assert first_bytes == second_bytes


def test_shipped_asset_is_a_legacy_merge_fixed_point():
  shipped = load_json_records(ASSET_PATH)
  legacy_records = load_json_records(FIXTURE_PATH)

  assert merge_legacy_shortcodes(shipped, legacy_records) == shipped
