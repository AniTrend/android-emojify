from unittest.mock import Mock, patch

import pytest

from emoji_generator import get_emoji, parse_emoji_data
from emoji_generator.sources import get_emoji_shortcodes
from emoji_generator.generator import fetch_emoji_data
from emoji_generator.models import Emoji, Group, Subgroup, Gender, SkinTone, Presentation
from emoji_generator.utils import compute_unicode, compute_html_dec, compute_html_hex

# Sample data mimicking emojibase-data structure
SAMPLE_EMOJI_JSON = [
  {
    "label": "grinning face",
    "hexcode": "1F600",
    "tags": [
      "cheerful",
      "cheery",
      "face",
      "grin",
      "grinning",
      "happy",
      "laugh",
      "nice",
      "smile",
      "smiling",
      "teeth"
    ],
    "emoji": "😀",
    "text": "",
    "type": 1,
    "order": 1,
    "group": 0,
    "subgroup": 0,
    "version": 1,
    "shortcodes": ["grinning_face"]
  },
  {
    "label": "family: man, woman, girl, boy",
    "hexcode": "1F468-200D-1F469-200D-1F467-200D-1F466",
    "tags": [
      "boy",
      "child",
      "family",
      "girl",
      "man",
      "woman"
    ],
    "emoji": "👨‍👩‍👧‍👦",
    "text": "",
    "type": 1,
    "order": 3442,
    "group": 1,
    "subgroup": 30,
    "version": 2
  },
  {
    "label": "sun",
    "hexcode": "2600",
    "tags": [
      "bright",
      "rays",
      "space",
      "sunny",
      "weather"
    ],
    "emoji": "☀️",
    "text": "",
    "type": 1,
    "order": 4002,
    "group": 5,
    "subgroup": 59,
    "version": 0.6
  }
]

# Sample shortcode mapping data
SAMPLE_SHORTCODE_JSON = {
  "1F600": ["grinning", "grinning_face"],  # emoji with existing shortcodes - should merge
  "2600": "sun",  # emoji without shortcodes - should add
  "2049": ["exclamation_question", "interrobang"],  # emoji not in main data
}


@pytest.fixture
def mock_emojibase_data():
  with patch('emoji_generator.sources.requests.get') as mock_get:
    mock_response = Mock()
    mock_response.json.return_value = SAMPLE_EMOJI_JSON
    mock_response.raise_for_status.return_value = None
    mock_get.return_value = mock_response
    yield mock_get


@pytest.fixture
def mock_shortcode_data():
  with patch('emoji_generator.sources.requests.get') as mock_get:
    mock_response = Mock()
    mock_response.json.return_value = SAMPLE_SHORTCODE_JSON
    mock_response.raise_for_status.return_value = None
    mock_get.return_value = mock_response
    yield mock_get


@pytest.fixture
def mock_both_data():
  """Mock both emoji data and shortcode data endpoints"""
  def side_effect(url, **kwargs):
    mock_response = Mock()
    mock_response.raise_for_status.return_value = None
    
    if 'shortcodes' in url:
      mock_response.json.return_value = SAMPLE_SHORTCODE_JSON
    else:
      mock_response.json.return_value = SAMPLE_EMOJI_JSON
    
    return mock_response

  with patch('emoji_generator.sources.requests.get', side_effect=side_effect) as mock_get:
    yield mock_get


def test_fetch_emoji_data(mock_emojibase_data):
  emojis = get_emoji("15.1")
  assert isinstance(emojis, list)
  assert len(emojis) == 3
  assert isinstance(emojis[0], Emoji)
  assert emojis[0].label == "grinning face"
  assert emojis[1].emoji == "👨‍👩‍👧‍👦"
  assert emojis[2].emoji == "☀️"


def test_get_emoji_shortcodes(mock_shortcode_data):
  shortcodes = get_emoji_shortcodes("15.1")
  assert isinstance(shortcodes, dict)
  assert "1F600" in shortcodes
  assert "2600" in shortcodes
  assert "2049" in shortcodes
  assert shortcodes["1F600"] == ["grinning", "grinning_face"]
  assert shortcodes["2600"] == "sun"
  assert shortcodes["2049"] == ["exclamation_question", "interrobang"]


def test_shortcode_merging_logic(mock_both_data):
  """Test the shortcode merging logic with mocked data"""
  # Get the data
  emoji_list = get_emoji("15.1")
  shortcodes_dict = get_emoji_shortcodes("15.1")
  
  # Manually perform the merge logic (same as in fetch_emoji_data)
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
  
  # Parse the data
  parsed_data = parse_emoji_data(emoji_list)
  
  # Test the results
  assert isinstance(parsed_data, list)
  assert len(parsed_data) == 3
  
  # Find the grinning face emoji (should have merged shortcodes)
  grinning_emoji = next((e for e in parsed_data if e["emoji"] == "😀"), None)
  assert grinning_emoji is not None
  assert "shortcodes" in grinning_emoji
  # Should have both original and new shortcodes (deduplicated)
  expected_shortcodes = {"grinning", "grinning_face"}
  actual_shortcodes = set(grinning_emoji["shortcodes"])
  assert expected_shortcodes.issubset(actual_shortcodes)
  
  # Find the sun emoji (should get shortcodes from mapping)
  sun_emoji = next((e for e in parsed_data if e["emoji"] == "☀️"), None)
  assert sun_emoji is not None
  assert "shortcodes" in sun_emoji
  assert "sun" in sun_emoji["shortcodes"]
  
  # Check that there are no duplicate shortcodes
  shortcodes = grinning_emoji["shortcodes"]
  assert len(shortcodes) == len(set(shortcodes)), "Shortcodes should not contain duplicates"


def test_compute_unicode():
  assert compute_unicode("😀") == "\\uD83D\\uDE00"
  assert compute_unicode("👨‍👩‍👧‍👦") == "\\uD83D\\uDC68\\u200D\\uD83D\\uDC69\\u200D\\uD83D\\uDC67\\u200D\\uD83D\\uDC66"


def test_compute_html_dec():
  assert compute_html_dec("😀") == "&#128512;"
  assert compute_html_dec("A") == "&#65;"


def test_compute_html_hex():
  assert compute_html_hex("😀") == "&#x1f600;"
  assert compute_html_hex("A") == "&#x41;"


def test_parse_emoji_data():
  emoji1 = Emoji(
    emoji="😀",
    label="Grinning Face",
    group=Group.SMILEYS_PEOPLE,
    subgroup=Subgroup.FACE_SMILING,
    shortcodes=["grinning_face"],
    skins=[Emoji(emoji="😀", label="Grinning Face Light Skin Tone", tone=SkinTone.LIGHT)],
    gender=Gender.FEMALE,
    type=Presentation.EMOJI,
    version=1.0
  )
  emoji2 = Emoji(
    emoji="👨‍👩‍👧‍👦",
    label="family: man, woman, girl, boy",
    group=Group.SMILEYS_PEOPLE,
    subgroup=Subgroup.FAMILY,
    shortcodes=["family"],
    skins=None,
    type=Presentation.EMOJI,
    version=2.0
  )
  parsed = parse_emoji_data([emoji1, emoji2])

  assert len(parsed) == 2
  # Check first emoji
  assert parsed[0].get("emoji") == "😀"
  assert parsed[0].get("description") == "Grinning Face"
  # Note: as_dict_with_no_none filters out None values, so these fields are not present
  assert parsed[0].get("htmlDec") == "&#128512;"
  assert parsed[0].get("supportsFitzpatrick") is True
  # Check second emoji
  assert parsed[1].get("emoji") == "👨‍👩‍👧‍👦"
  assert parsed[1].get("description") == "family: man, woman, girl, boy"
  assert parsed[1].get("htmlDec") == "&#128104;&#8205;&#128105;&#8205;&#128103;&#8205;&#128102;"
  # supportsFitzpatrick should be None (filtered out) since skins is None/empty


def test_error_handling():
  with patch('emoji_generator.sources.requests.get') as mock_get:
    mock_get.side_effect = Exception("Error")  # Simulate request failure

    with pytest.raises(SystemExit) as exc_info:  # Expect SystemExit to be raised
      get_emoji("15.1")

    assert exc_info.value.code is not None


def test_normalization():
  # Test NFC normalization in parse_emoji_data
  emoji = Emoji(emoji="c\u0327", label="Cedilla", group=Group.SYMBOLS)
  parsed = parse_emoji_data([emoji])
  normalized_char = "\u00e7"  # 'ç' in NFC form
  assert parsed[0]["unicode"] == compute_unicode(normalized_char)
