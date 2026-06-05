from __future__ import annotations

from dataclasses import dataclass
from enum import Enum
from typing import TypeVar

# Type aliases
Unicode = str
Emoticon = str
Hexcode = str
Shortcode = str

_E = TypeVar("_E", bound=Enum)


# In the TypeScript definition, gender is given as `0` for female and `1` for male.
class Gender(Enum):
  FEMALE = 0
  MALE = 1

  @classmethod
  def _missing_(cls, value: object) -> Gender | None:  # noqa: ARG003
    return None


# The presentation type where 0 represents text and 1 represents emoji.
class Presentation(Enum):
  TEXT = 0
  EMOJI = 1

  @classmethod
  def _missing_(cls, value: object) -> Presentation:  # noqa: ARG003
    return Presentation.EMOJI


# For skin tone, values range from 1 (light) to 5 (dark). You can also get an array for multi-tone.
class SkinTone(Enum):
  LIGHT = 1
  MEDIUM_LIGHT = 2
  MEDIUM = 3
  MEDIUM_DARK = 4
  DARK = 5

  @classmethod
  def _missing_(cls, value: object) -> SkinTone | None:  # noqa: ARG003
    return None


# The group (categorical) is defined in the data.
class Group(Enum):
  SMILEYS_PEOPLE = "Smileys & People"
  ANIMALS_NATURE = "Animals & Nature"
  FOOD_DRINK = "Food & Drink"
  TRAVEL_PLACES = "Travel & Places"
  ACTIVITIES = "Activities"
  OBJECTS = "Objects"
  SYMBOLS = "Symbols"
  FLAGS = "Flags"

  @classmethod
  def _missing_(cls, value: object) -> Group | None:  # noqa: ARG003
    return None


class Subgroup(Enum):
  # Smileys & People
  FACE_SMILING = "face-smiling"
  FACE_AFFECTION = "face-affection"
  FACE_TONGUE = "face-tongue"
  FACE_NEUTRAL_SKEPTICAL = "face-neutral-skeptical"
  FACE_SLEEPY = "face-sleepy"
  FACE_HAND = "face-hand"
  FACE_UNWELL = "face-unwell"
  FACE_HAT = "face-hat"
  FACE_GLASSES = "face-glasses"
  FACE_CONCERNED = "face-concerned"
  PERSON = "person"
  PERSON_GESTURE = "person-gesture"
  PERSON_ROLE = "person-role"
  PERSON_FANTASY = "person-fantasy"
  PERSON_ACTIVITY = "person-activity"
  PERSON_SYMBOL = "person-symbol"
  FAMILY = "family"
  PERSON_OBJECT = "person-object"
  HAND_FINGERS_OPEN = "hand-fingers-open"
  HAND_SINGLE_FINGER = "hand-single-finger"
  HAND_FINGERS_PARTIAL = "hand-fingers-partial"
  HANDS = "hands"
  HAND_PROP = "hand-prop"

  # Animals & Nature
  ANIMAL_MAMMAL = "animal-mammal"
  ANIMAL_BIRD = "animal-bird"
  ANIMAL_AMPHIBIAN = "animal-amphibian"
  ANIMAL_REPTILE = "animal-reptile"
  ANIMAL_MARINE = "animal-marine"
  ANIMAL_INSECT = "animal-insect"
  PLANT_FLOWER = "plant-flower"
  PLANT_OTHER = "plant-other"

  # Food & Drink
  FOOD_FRUIT = "food-fruit"
  FOOD_VEGETABLE = "food-vegetable"
  FOOD_PREPARED = "food-prepared"
  FOOD_SWEET = "food-sweet"
  FOOD_ASIAN = "food-asian"
  FOOD_MARINE = "food-marine"
  FOOD_BEVERAGE = "food-beverage"

  # Travel & Places
  TRAVEL_TRANSPORT_AIR = "travel-transport-air"
  TRAVEL_TRANSPORT_GROUND = "travel-transport-ground"
  TRAVEL_TRANSPORT_WATER = "travel-transport-water"
  TRAVEL_MAP = "travel-map"
  TRAVEL_PLACE = "travel-place"
  TRAVEL_BUILDING = "travel-building"
  TRAVEL_SIGN = "travel-sign"
  TRAVEL_SPACE = "travel-space"
  TRAVEL_WEATHER = "travel-weather"

  # Activities
  ACTIVITY = "activity"
  SPORT = "sport"

  # Objects
  OBJECT = "object"
  OBJECT_WEATHER = "object-weather"
  OBJECT_LIGHT = "object-light"
  OBJECT_MUSIC = "object-music"
  OBJECT_COMPUTER = "object-computer"
  OBJECT_PHONE = "object-phone"
  OBJECT_TOOL = "object-tool"
  OBJECT_OFFICE = "object-office"
  OBJECT_MEDICINE = "object-medicine"
  OBJECT_SCIENCE = "object-science"
  OBJECT_RELIGION = "object-religion"
  OBJECT_SYMBOL = "object-symbol"
  OBJECT_GAME = "object-game"
  OBJECT_CRAFT = "object-craft"

  # Symbols
  SYMBOL = "symbol"
  SYMBOL_ARROW = "symbol-arrow"
  SYMBOL_MATH = "symbol-math"
  SYMBOL_LETTER = "symbol-letter"
  SYMBOL_NUMBER = "symbol-number"
  SYMBOL_SHAPE = "symbol-shape"

  # Flags
  FLAG = "flag"

  @classmethod
  def _missing_(cls, value: object) -> Subgroup | None:  # noqa: ARG003
    return None


@dataclass
class Emoji:
  # Emoji presentation unicode character.
  emoji: Unicode

  # If applicable, an emoticon representing the emoji character.
  # It can be a single emoticon string or a list of them.
  emoticon: Emoticon | list[Emoticon] | None = None

  # If applicable, the gender of the emoji character. `0` for female, `1` for male.
  gender: Gender | None = None

  # The categorical group the emoji belongs to.
  group: Group | None = None

  # The hexadecimal representation of the emoji Unicode codepoint.
  hexcode: Hexcode = ""

  # A localized description, provided by CLDR.
  label: str = ""

  # The order in which emoji should be displayed on a device.
  order: int | None = None

  # List of shortcodes without surrounding colons.
  shortcodes: list[Shortcode] | None = None

  # If applicable, an array of emoji objects for each skin tone modification.
  skins: list[Emoji] | None = None

  # The categorical subgroup the emoji belongs to.
  subgroup: Subgroup | None = None

  # An array of localized keywords for searching/filtering.
  tags: list[str] | None = None

  # Text presentation unicode character.
  text: Unicode = ""

  # If applicable, the skin tone of the emoji character.
  # `tone` can be a single SkinTone or a list of them.
  tone: SkinTone | list[SkinTone] | None = None

  # The default presentation of the emoji character. `0` for text, `1` for emoji.
  type: Presentation = Presentation.EMOJI

  # Version the emoji was added.
  version: float = 0.0

  @classmethod
  def from_dict(cls, data: dict) -> Emoji:
    """
    Create an Emoji instance from a dictionary (e.g. parsed from JSON).
    """

    def parse_enum(enum_class: type[_E], value: int | str | None) -> _E | None:
      if value is None:
        return None
      try:
        return enum_class(value)
      except ValueError:
        if isinstance(value, str):
          try:
            return enum_class[value.capitalize()]
          except KeyError:
            return None
        return None

    emoticon_data: str | list[str] | None = data.get("emoticon")

    gender_value: int | None = data.get("gender")
    gender: Gender | None = parse_enum(Gender, gender_value)

    group_value: str | None = data.get("group")
    group: Group | None = parse_enum(Group, group_value)

    sub_group_value: str | None = data.get("subgroup")
    sub_group: Subgroup | None = parse_enum(Subgroup, sub_group_value)

    type_value: int | None = data.get("type")
    type_enum: Presentation = parse_enum(Presentation, type_value) or Presentation.EMOJI

    tone_value: int | list[int] | None = data.get("tone")
    tone: SkinTone | list[SkinTone] | None = None
    if isinstance(tone_value, list):
      tones: list[SkinTone] = []
      for t in tone_value:
        skin_tone: SkinTone | None = parse_enum(SkinTone, t)
        if skin_tone is not None:
          tones.append(skin_tone)
      tone = tones or None
    elif tone_value is not None:
      tone = parse_enum(SkinTone, tone_value)

    skins_data: list[dict] | None = data.get("skins")
    skins: list[Emoji] | None = (
      [cls.from_dict(skin) for skin in skins_data] if skins_data else None
    )

    shortcodes = data.get("shortcodes")
    if shortcodes is not None and not isinstance(shortcodes, list):
      shortcodes = [shortcodes]

    return cls(
      emoji=data.get("emoji", ""),
      emoticon=emoticon_data,
      gender=gender,
      group=group,
      hexcode=data.get("hexcode", ""),
      label=data.get("label", ""),
      order=data.get("order"),
      shortcodes=shortcodes,
      skins=skins,
      subgroup=sub_group,
      tags=data.get("tags"),
      text=data.get("text", ""),
      tone=tone,
      type=type_enum,
      version=float(data.get("version", 0.0)),
    )
