#!/usr/bin/env python3
"""Compare the frozen 1.9.7 alias catalog with the current generated catalog."""

from __future__ import annotations

import argparse
import json
import sys
from collections import defaultdict
from pathlib import Path
from urllib.error import URLError
from urllib.request import urlopen

from emoji_generator.compatibility import (
    MERGE_FITZPATRICK_CAPABILITY_WITH_OR,
    SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK,
    SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK,
    fitzpatrick_capability_drifts,
)

ROOT = Path(__file__).resolve().parents[2]
LEGACY_PATH = Path(__file__).resolve().parent / "fixtures" / "emoji-1.9.7.json"
CURRENT_PATH = ROOT / "emojify/src/main/assets/emoticons/emoji.json"
EMOJIBASE_VERSION = "17.0.0"
PRESET_URL = (
    "https://cdn.jsdelivr.net/npm/"
    f"emojibase-data@{EMOJIBASE_VERSION}/en/shortcodes/emojibase.json"
)
FITZPATRICK_SUFFIXES = ("type_1_2", "type_3", "type_4", "type_5", "type_6")


def load_json(path: Path) -> object:
    with path.open(encoding="utf-8") as source:
        return json.load(source)


def string_list(value: object) -> list[str]:
    if isinstance(value, str):
        return [value]
    if isinstance(value, list):
        return [item for item in value if isinstance(item, str)]
    return []


def distinct_in_order(values: list[str]) -> list[str]:
    return list(dict.fromkeys(values))


def unicode_hex_key(emoji: str) -> str:
    return "-".join(f"{ord(character):X}" for character in emoji)


def fetch_preset(path: Path | None) -> tuple[dict[str, object] | None, str]:
    if path is not None:
        try:
            payload = load_json(path)
        except (OSError, json.JSONDecodeError) as error:
            return None, f"Unknown (could not read {path}: {error})"
        if not isinstance(payload, dict):
            return None, f"Unknown (preset at {path} is not a JSON object)"
        return payload, f"loaded from {path}"

    try:
        with urlopen(PRESET_URL, timeout=15) as response:
            payload = json.load(response)
    except (OSError, URLError, TimeoutError, json.JSONDecodeError) as error:
        return None, f"Unknown (fetch failed: {error})"
    if not isinstance(payload, dict):
        return None, "Unknown (the CDN response is not a JSON object)"
    return payload, f"fetched {PRESET_URL}"


def associations(records: list[dict[str, object]], field: str) -> dict[str, set[str]]:
    result: dict[str, set[str]] = defaultdict(set)
    for record in records:
        emoji = record.get("emoji")
        if not isinstance(emoji, str):
            continue
        for code in string_list(record.get(field)):
            result[code].add(emoji)
    return result


def multi_code_stats(records: list[dict[str, object]], field: str) -> tuple[int, int, list[dict[str, object]]]:
    entries = []
    for record in records:
        emoji = record.get("emoji")
        codes = distinct_in_order(string_list(record.get(field)))
        if len(codes) > 1:
            entries.append({"emoji": emoji, "codes": codes})
    maximum = max((len(entry["codes"]) for entry in entries), default=0)
    examples = sorted(entries, key=lambda entry: (-len(entry["codes"]), str(entry["emoji"])))[:10]
    return len(entries), maximum, examples


def format_unicode(emoji: str) -> str:
    return " ".join(f"U+{ord(character):04X}" for character in emoji)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--legacy", type=Path, default=LEGACY_PATH, help="frozen 1.9.7 JSON")
    parser.add_argument("--current", type=Path, default=CURRENT_PATH, help="current generated JSON")
    parser.add_argument(
        "--preset-json",
        type=Path,
        help="use a previously fetched Emojibase preset JSON instead of fetching it",
    )
    args = parser.parse_args()

    try:
        legacy_payload = load_json(args.legacy)
        current_payload = load_json(args.current)
    except (OSError, json.JSONDecodeError) as error:
        print(f"Could not load input JSON: {error}", file=sys.stderr)
        return 2
    if not isinstance(legacy_payload, list) or not isinstance(current_payload, list):
        print("Both input files must contain JSON arrays of emoji records.", file=sys.stderr)
        return 2

    legacy = [record for record in legacy_payload if isinstance(record, dict)]
    current = [record for record in current_payload if isinstance(record, dict)]
    old_occurrences = [code for record in legacy for code in string_list(record.get("aliases"))]
    current_occurrences = [code for record in current for code in string_list(record.get("shortCodes"))]
    old_codes = set(old_occurrences)
    current_codes = set(current_occurrences)
    old_alias_to_emoji = associations(legacy, "aliases")
    current_code_to_emoji = associations(current, "shortCodes")
    old_pairs = {
        (code, record["emoji"])
        for record in legacy
        if isinstance(record.get("emoji"), str)
        for code in string_list(record.get("aliases"))
    }
    current_by_emoji = {
        record["emoji"]: record
        for record in current
        if isinstance(record.get("emoji"), str)
    }
    legacy_by_emoji = {
        record["emoji"]: record
        for record in legacy
        if isinstance(record.get("emoji"), str)
    }

    exact_pairs = set()
    reassigned_pairs = set()
    unrepresented_pairs = set()
    for code, emoji in old_pairs:
        destinations = current_code_to_emoji.get(code, set())
        if emoji in destinations:
            exact_pairs.add((code, emoji))
        elif destinations:
            reassigned_pairs.add((code, emoji))
        else:
            unrepresented_pairs.add((code, emoji))
    variation_selector_only_pairs = {
        (code, emoji)
        for code, emoji in reassigned_pairs
        if all(
            destination.replace("\ufe0e", "").replace("\ufe0f", "")
            == emoji.replace("\ufe0e", "").replace("\ufe0f", "")
            for destination in current_code_to_emoji[code]
        )
    }
    other_reassignment_pairs = reassigned_pairs - variation_selector_only_pairs

    lost_codes = sorted(old_codes - current_codes)
    new_codes = sorted(current_codes - old_codes)
    old_collision_codes = {
        code: sorted(emojis) for code, emojis in old_alias_to_emoji.items() if len(emojis) > 1
    }
    current_collision_codes = {
        code: sorted(emojis) for code, emojis in current_code_to_emoji.items() if len(emojis) > 1
    }
    legacy_multi_count, legacy_max_codes, legacy_multi_examples = multi_code_stats(legacy, "aliases")
    current_multi_count, current_max_codes, current_multi_examples = multi_code_stats(current, "shortCodes")

    old_fitzpatrick = [record for record in legacy if record.get("supports_fitzpatrick") is True]
    legacy_true_current_false, legacy_false_current_true = fitzpatrick_capability_drifts(
        current, legacy
    )
    fitz_pairs = {
        (code, record["emoji"])
        for record in old_fitzpatrick
        if isinstance(record.get("emoji"), str)
        for code in string_list(record.get("aliases"))
    }
    fitz_aliases = sorted({code for code, _ in fitz_pairs})
    no_current_codes = sorted(
        emoji
        for emoji in legacy_by_emoji
        if not string_list(current_by_emoji.get(emoji, {}).get("shortCodes"))
    )
    orphaned_legacy_pairs = {
        (code, emoji) for code, emoji in old_pairs if emoji in set(no_current_codes)
    }

    preset, preset_source = fetch_preset(args.preset_json)
    preset_by_hex = {str(key).upper(): value for key, value in preset.items()} if preset else {}

    def preset_first(emoji: str) -> str | None:
        return string_list(preset_by_hex.get(unicode_hex_key(emoji)))[0:1][0] if string_list(
            preset_by_hex.get(unicode_hex_key(emoji))
        ) else None

    current_first = {
        emoji: distinct_in_order(string_list(record.get("shortCodes")))
        for emoji, record in current_by_emoji.items()
    }
    canonical = {emoji: string_list(record.get("aliases")) for emoji, record in legacy_by_emoji.items()}
    legacy_records = list(legacy_by_emoji.items())
    canonical_results: dict[str, dict[str, object]] = {}
    for rule in ("A legacy-first", "B current-first-deterministic", "C emojibase-preset-first"):
        matches = 0
        available = 0
        mismatches = []
        unavailable = 0
        for emoji, record in legacy_records:
            historic = canonical[emoji][0] if canonical[emoji] else None
            if rule.startswith("A"):
                candidate = historic
                if candidate is None:
                    codes = current_first.get(emoji, [])
                    candidate = codes[0] if codes else None
            elif rule.startswith("B"):
                codes = current_first.get(emoji, [])
                candidate = codes[0] if codes else None
            else:
                candidate = preset_first(emoji) if preset is not None else None
            if candidate is None:
                unavailable += 1
                continue
            available += 1
            if candidate == historic:
                matches += 1
            elif len(mismatches) < 10:
                mismatches.append((emoji, historic, candidate))
        canonical_results[rule] = {
            "matches": matches,
            "available": available,
            "unavailable": unavailable,
            "mismatches": mismatches,
        }

    print("Shortcode compatibility audit")
    print(f"Legacy file: {args.legacy}")
    print(f"Current file: {args.current}")
    print(f"Emojibase preset: {preset_source}")
    print(f"Emojibase preset URL: {PRESET_URL}")
    print()
    print("Dataset inventory")
    print(f"1.9.7 emoji records: {len(legacy)}")
    print(f"1.9.7 alias count: {len(old_occurrences)} occurrences, {len(old_codes)} distinct")
    print(f"1.9.7 multi-alias emoji records: {legacy_multi_count}")
    print(f"1.9.7 supports_fitzpatrick records: {len(old_fitzpatrick)}")
    print(f"Current emoji records: {len(current)}")
    print(
        "current shortcode count: "
        f"{len(current_occurrences)} occurrences, {len(current_codes)} distinct"
    )
    print()
    print("Retention and mapping")
    print(f"Exact aliases still represented: {len(old_codes & current_codes)} / {len(old_codes)} distinct")
    print(f"Legacy aliases no longer represented: {len(lost_codes)} distinct values")
    print(f"New shortcodes not present in 1.9.7: {len(new_codes)} distinct values")
    print(f"New shortcode examples: {', '.join(new_codes[:20])}")
    print(f"Legacy alias-to-emoji mappings: {len(old_pairs)} distinct pairs")
    print(f"Mappings preserved on the same emoji: {len(exact_pairs)}")
    print(f"Mappings reassigned to other emoji only: {len(reassigned_pairs)}")
    print(
        "  of these, variation-selector-only Unicode differences: "
        f"{len(variation_selector_only_pairs)}; other Unicode mappings: "
        f"{len(other_reassignment_pairs)}"
    )
    print(f"Mappings not represented anywhere: {len(unrepresented_pairs)}")
    print(f"Legacy emoji with no current shortcode at all: {len(no_current_codes)}")
    print(f"Legacy pairs on emoji with no current shortcode: {len(orphaned_legacy_pairs)}")
    print()
    print("alias -> multiple emoji collisions")
    print(f"1.9.7 collision aliases: {len(old_collision_codes)}")
    for code, emojis in sorted(old_collision_codes.items())[:12]:
        print(f"  {code}: {' | '.join(f'{emoji} ({format_unicode(emoji)})' for emoji in emojis)}")
    print(f"Current collision shortcodes: {len(current_collision_codes)}")
    for code, emojis in sorted(current_collision_codes.items())[:12]:
        print(f"  {code}: {' | '.join(f'{emoji} ({format_unicode(emoji)})' for emoji in emojis)}")
    print()
    print("emoji -> multiple shortcode cases")
    print(f"1.9.7: {legacy_multi_count} emoji, max {legacy_max_codes} aliases")
    for entry in legacy_multi_examples:
        print(f"  {entry['emoji']} ({format_unicode(entry['emoji'])}): {', '.join(entry['codes'])}")
    print(f"Current: {current_multi_count} emoji, max {current_max_codes} shortcodes")
    for entry in current_multi_examples:
        print(f"  {entry['emoji']} ({format_unicode(entry['emoji'])}): {', '.join(entry['codes'])}")
    print()
    print("Fitzpatrick-capable legacy aliases and full token forms")
    policy = "legacy OR current" if MERGE_FITZPATRICK_CAPABILITY_WITH_OR else "current-only"
    restored_source_exceptions = {
        emoji
        for emoji in SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK
        if any(
            item.get("emoji") == emoji and item.get("supportsFitzpatrick") is True
            for item in current
        )
    }
    print(f"Fitzpatrick capability policy: {policy}")
    print(
        "Source legacy true, current false exceptions: "
        f"{len(SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK)}; restored by merge="
        f"{restored_source_exceptions == SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK}"
    )
    for emoji in sorted(SOURCE_LEGACY_TRUE_CURRENT_FALSE_FITZPATRICK):
        record = next(
            item
            for item in old_fitzpatrick
            if isinstance(item.get("emoji"), str)
            and item["emoji"].replace("\ufe0e", "").replace("\ufe0f", "")
            == emoji.replace("\ufe0e", "").replace("\ufe0f", "")
        )
        aliases = distinct_in_order(string_list(record.get("aliases")))
        forms = [f":{alias}|{suffix}:" for alias in aliases for suffix in FITZPATRICK_SUFFIXES]
        print(f"  {emoji}: legacy forms={','.join(forms)}")
    print(
        "Source legacy false, current true exceptions: "
        f"{len(SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK)}; remains after merge="
        f"{legacy_false_current_true == SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK}"
    )
    for emoji in sorted(SOURCE_LEGACY_FALSE_CURRENT_TRUE_FITZPATRICK):
        print(f"  {emoji} ({format_unicode(emoji)})")
    print(
        "Merged legacy true, current false exceptions: "
        f"{len(legacy_true_current_false)}"
    )
    print(f"Fitzpatrick-capable legacy aliases: {len(old_fitzpatrick)} emoji records")
    print(f"Legacy Fitzpatrick alias pairs: {len(fitz_pairs)}; distinct aliases: {len(fitz_aliases)}")
    for record in sorted(old_fitzpatrick, key=lambda item: str(item.get("emoji"))):
        emoji = record.get("emoji")
        aliases = distinct_in_order(string_list(record.get("aliases")))
        forms = [f":{alias}|{suffix}:" for alias in aliases for suffix in FITZPATRICK_SUFFIXES]
        print(f"  {emoji} ({format_unicode(emoji)}): aliases={','.join(aliases)}; forms={','.join(forms)}")
    print()
    print("Canonical rule evaluation against 1.9.7 first alias")
    print(f"Emojis compared: {len(legacy_records)}")
    for rule, result in canonical_results.items():
        if rule.startswith("C") and preset is None:
            print(f"{rule}: Unknown because the preset could not be read.")
            continue
        denominator = result["available"]
        percentage = 100 * result["matches"] / denominator if denominator else 0.0
        print(
            f"{rule}: {result['matches']}/{denominator} available exact matches "
            f"({percentage:.2f}%), unavailable={result['unavailable']}"
        )
        for emoji, historic, candidate in result["mismatches"]:
            print(
                f"  mismatch {emoji} ({format_unicode(emoji)}): "
                f"historic={historic!r}, candidate={candidate!r}"
            )
    if preset is not None:
        preset_missing = sum(preset_first(emoji) is None for emoji, _ in legacy_records)
        print(f"Emojibase preset entries unavailable for legacy emoji: {preset_missing}")
    print()
    print("Round-trip sample candidates")
    sample_emojis = ("😄", "😀", "👦")
    for emoji in sample_emojis:
        old = legacy_by_emoji.get(emoji)
        old_first = string_list(old.get("aliases"))[0] if old and string_list(old.get("aliases")) else None
        now_codes = current_first.get(emoji, [])
        now_first = now_codes[0] if now_codes else None
        upstream_first = preset_first(emoji) if preset is not None else None
        suffix = "|type_6" if emoji == "👦" else ""
        values = (old_first, now_first, upstream_first)
        rendered = [f":{value}{suffix}:" if value is not None else "Unknown" for value in values]
        print(
            f"{emoji} ({format_unicode(emoji)}{'+ U+1F3FF' if emoji == '👦' else ''}): "
            f"A={rendered[0]}, B={rendered[1]}, C={rendered[2]}"
        )
    print()
    print("Appendix: every legacy alias no longer represented, with legacy emoji mapping")
    for code in lost_codes:
        mapped = sorted(old_alias_to_emoji[code])
        print(f"  {code}: {' | '.join(f'{emoji} ({format_unicode(emoji)})' for emoji in mapped)}")
    print()
    print("Appendix: every legacy alias mapping reassigned away from its legacy emoji")
    for code, emoji in sorted(reassigned_pairs):
        destinations = sorted(current_code_to_emoji[code])
        print(
            f"  {code}: legacy={emoji} ({format_unicode(emoji)}); current="
            f"{' | '.join(f'{item} ({format_unicode(item)})' for item in destinations)}"
        )
    print()
    print("Appendix: every reassigned alias whose Unicode mapping is not variation-selector-only")
    for code, emoji in sorted(other_reassignment_pairs):
        destinations = sorted(current_code_to_emoji[code])
        print(
            f"  {code}: legacy={emoji} ({format_unicode(emoji)}); current="
            f"{' | '.join(f'{item} ({format_unicode(item)})' for item in destinations)}"
        )
    print()
    print("Appendix: every legacy emoji with no current shortcode at all")
    for emoji in no_current_codes:
        aliases = sorted(code for code, old_emoji in old_pairs if old_emoji == emoji)
        print(f"  {emoji} ({format_unicode(emoji)}): {', '.join(aliases)}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
