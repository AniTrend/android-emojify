#!/usr/bin/env python3
"""Validate a generated catalog against the pre-merge generated catalog."""

from __future__ import annotations

import argparse
import json
import sys
from collections import Counter
from pathlib import Path
from typing import Any

from emoji_generator.compatibility import SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK

ALLOWED_ADDED_EMOJI = "🏴\U000e0075\U000e0073\U000e0074\U000e0078\U000e007f"
ALLOWED_FITZPATRICK_PROMOTIONS = SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK


def load_records(path: Path) -> list[dict[str, Any]]:
  with path.open(encoding="utf-8") as source:
    payload = json.load(source)
  if not isinstance(payload, list) or any(not isinstance(record, dict) for record in payload):
    msg = f"Expected a JSON array of records: {path}"
    raise ValueError(msg)
  return payload


def index_records(records: list[dict[str, Any]], path: Path) -> dict[str, dict[str, Any]]:
  indexed: dict[str, dict[str, Any]] = {}
  for record in records:
    emoji = record.get("emoji")
    if not isinstance(emoji, str):
      msg = f"Record in {path} has no emoji string: {record!r}"
      raise ValueError(msg)
    if emoji in indexed:
      msg = f"Duplicate emoji record in {path}: {emoji!r}"
      raise ValueError(msg)
    indexed[emoji] = record
  return indexed


def validate(old_path: Path, new_path: Path) -> tuple[list[str], dict[str, int]]:
  old_records = load_records(old_path)
  new_records = load_records(new_path)
  old_by_emoji = index_records(old_records, old_path)
  new_by_emoji = index_records(new_records, new_path)
  errors: list[str] = []
  shortcode_changes = Counter()
  fitzpatrick_promotions: set[str] = set()

  for emoji, old_record in old_by_emoji.items():
    new_record = new_by_emoji.get(emoji)
    if new_record is None:
      errors.append(f"Removed existing record: {emoji!r}")
      continue
    old_shortcodes = old_record.get("shortCodes")
    new_shortcodes = new_record.get("shortCodes")
    if old_shortcodes != new_shortcodes:
      shortcode_changes["records_changed"] += 1
      old_codes = old_shortcodes if isinstance(old_shortcodes, list) else []
      new_codes = new_shortcodes if isinstance(new_shortcodes, list) else []
      shortcode_changes["added_occurrences"] += sum(
        (Counter(new_codes) - Counter(old_codes)).values()
      )
      shortcode_changes["removed_occurrences"] += sum(
        (Counter(old_codes) - Counter(new_codes)).values()
      )
      if Counter(old_codes) == Counter(new_codes):
        shortcode_changes["reordered_records"] += 1

    old_other_fields = {
      key: value
      for key, value in old_record.items()
      if key not in ("shortCodes", "supportsFitzpatrick")
    }
    new_other_fields = {
      key: value
      for key, value in new_record.items()
      if key not in ("shortCodes", "supportsFitzpatrick")
    }
    if old_other_fields != new_other_fields:
      errors.append(f"Non-shortCodes fields changed for {emoji!r}")
    old_support = old_record.get("supportsFitzpatrick")
    new_support = new_record.get("supportsFitzpatrick")
    if old_support != new_support:
      if (
        emoji in ALLOWED_FITZPATRICK_PROMOTIONS
        and old_support is not True
        and new_support is True
      ):
        fitzpatrick_promotions.add(emoji)
      else:
        errors.append(f"Unexpected supportsFitzpatrick change for {emoji!r}")

  if fitzpatrick_promotions != ALLOWED_FITZPATRICK_PROMOTIONS:
    errors.append(
      "Expected only the three legacy-capable zombie records to gain supportsFitzpatrick; "
      f"found {sorted(fitzpatrick_promotions)!r}"
    )
  if shortcode_changes["removed_occurrences"]:
    errors.append(
      f"Removed shortcode occurrences are not allowed: {shortcode_changes['removed_occurrences']}"
    )

  added = [emoji for emoji in new_by_emoji if emoji not in old_by_emoji]
  if added != [ALLOWED_ADDED_EMOJI]:
    errors.append(f"Expected only the Texas flag record to be added; found {added!r}")
  elif new_by_emoji[ALLOWED_ADDED_EMOJI].get("shortCodes") != ["ustx"]:
    errors.append("The added Texas flag record must retain only the legacy alias 'ustx'")

  old_shortcodes = {
    code
    for record in old_records
    for code in (record.get("shortCodes") if isinstance(record.get("shortCodes"), list) else [])
  }
  new_shortcodes = {
    code
    for record in new_records
    for code in (record.get("shortCodes") if isinstance(record.get("shortCodes"), list) else [])
  }

  stats = {
    "old_records": len(old_records),
    "new_records": len(new_records),
    "matched_records": len(old_by_emoji) - sum("Removed existing record" in error for error in errors),
    "added_records": len(added),
    "shortcode_records_changed": shortcode_changes["records_changed"],
    "shortcode_occurrences_added": shortcode_changes["added_occurrences"],
    "shortcode_occurrences_removed": shortcode_changes["removed_occurrences"],
    "shortcode_order_only_records": shortcode_changes["reordered_records"],
    "old_distinct_shortcodes": len(old_shortcodes),
    "new_distinct_shortcodes": len(new_shortcodes),
    "distinct_shortcodes_added": len(new_shortcodes - old_shortcodes),
    "distinct_shortcodes_removed": len(old_shortcodes - new_shortcodes),
    "fitzpatrick_capability_promotions": len(fitzpatrick_promotions),
  }
  return errors, stats


def main() -> int:
  parser = argparse.ArgumentParser(description=__doc__)
  parser.add_argument("old", type=Path, help="pre-merge generated emoji.json")
  parser.add_argument("new", type=Path, help="newly generated emoji.json")
  args = parser.parse_args()

  try:
    errors, stats = validate(args.old, args.new)
  except (OSError, json.JSONDecodeError, ValueError) as error:
    print(f"Catalog diff failed: {error}", file=sys.stderr)
    return 2

  print("Structured catalog diff")
  for key, value in stats.items():
    print(f"{key}: {value}")
  if errors:
    print("Validation errors:", file=sys.stderr)
    for error in errors:
      print(f"- {error}", file=sys.stderr)
    return 1
  print(
    "All matched protected fields are unchanged; only shortCodes, approved Fitzpatrick "
    "promotions, and Texas differ."
  )
  return 0


if __name__ == "__main__":
  raise SystemExit(main())
