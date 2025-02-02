import traceback
from functools import wraps
from logging import getLogger
from typing import Dict, Callable, Any


__logger = getLogger()


def run_catching(func: Callable[..., Any]) -> Callable[..., Any]:
  @wraps(func)
  def wrapper(*args: tuple[Any, ...], **kwargs: Dict[str, Any]) -> Any:
    try:
      return func(*args, **kwargs)
    except Exception as e:
      trace = traceback.format_exc()  # Get full traceback details
      __logger.error(f"An error occurred in '{func.__name__}': {e}")
      raise SystemExit(trace)

  return wrapper
