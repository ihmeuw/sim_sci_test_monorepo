"""
Tests for config_tree
"""

from sim_sci_test_monorepo.config_tree.config_tree import ConfigTree


def test_set_and_get():
    tree = ConfigTree()
    tree.set("a.b", 42)
    assert tree.get("a.b") == 42


def test_get_default():
    tree = ConfigTree()
    assert tree.get("missing", "default") == "default"


def test_repr():
    tree = ConfigTree()
    tree.set("key", "value")
    assert "key" in repr(tree)
