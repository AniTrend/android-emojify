import traceback
from collections.abc import Callable
from functools import wraps
from logging import getLogger
from typing import Any

__logger = getLogger()


def run_catching(func: Callable[..., Any]) -> Callable[..., Any]:
  @wraps(func)
  def wrapper(*args: tuple[Any, ...], **kwargs: dict[str, Any]) -> Any:
    try:
      return func(*args, **kwargs)
    except Exception as e:
      trace = traceback.format_exc()  # Get full traceback details
      msg = f"An error occurred in '{func.__name__}'"
      __logger.exception(msg=msg, exc_info=e)
      raise SystemExit(trace) from e

  return wrapper
