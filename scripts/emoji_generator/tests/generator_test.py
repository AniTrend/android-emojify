from unittest.mock import Mock, patch

import pytest

from emoji_generator import get_emoji, parse_emoji_data
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
    "version": 1
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
  }
]


@pytest.fixture
def mock_emojibase_data():
  with patch('emoji_generator.sources.requests.get') as mock_get:
    mock_response = Mock()
    mock_response.json.return_value = SAMPLE_EMOJI_JSON
    mock_response.raise_for_status.return_value = None
    mock_get.return_value = mock_response
    yield mock_get


def test_fetch_emoji_data(mock_emojibase_data):
  emojis = get_emoji("15.1")
  assert isinstance(emojis, list)
  assert len(emojis) == 2
  assert isinstance(emojis[0], Emoji)
  assert emojis[0].label == "grinning face"
  assert emojis[1].emoji == "👨‍👩‍👧‍👦"


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
  assert parsed[0].get("group") == "SMILEYS_PEOPLE"
  assert parsed[0].get("subgroup") == "FACE_SMILING"
  assert parsed[0].get("gender") == "FEMALE"
  assert parsed[0].get("htmlDec") == "&#128512;"
  assert parsed[0].get("supportsFitzpatrick") is True
  # Check second emoji
  assert parsed[1].get("emoji") == "👨‍👩‍👧‍👦"
  assert parsed[1].get("description") == "family: man, woman, girl, boy"
  assert parsed[1].get("group") == "SMILEYS_PEOPLE"
  assert parsed[1].get("subgroup") == "FAMILY"
  assert parsed[1].get("gender") is None
  assert parsed[1].get("htmlDec") == "&#128104;&#8205;&#128105;&#8205;&#128103;&#8205;&#128102;"
  assert parsed[1].get("supportsFitzpatrick") is None


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
