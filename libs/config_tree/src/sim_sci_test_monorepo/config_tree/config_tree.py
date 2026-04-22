"""
Example configuration tree implementation.
"""

from typing import Any


class ConfigTree:
    """A simple hierarchical configuration tree."""

    def __init__(self) -> None:
        self._data: dict[str, Any] = {}

    def set(self, key: str, value: Any) -> None:
        """Set a configuration value."""
        self._data[key] = value

    def get(self, key: str, default: Any = None) -> Any:
        """Get a configuration value."""
        return self._data.get(key, default)

    def __repr__(self) -> str:
        return f"ConfigTree({self._data})"
