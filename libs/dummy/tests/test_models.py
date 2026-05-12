"""
Tests for dummy models
"""

import pytest

from sim_sci_test_monorepo.dummy.models import DummyModel, hello_dummy


def test_hello_dummy() -> None:
    """Test the hello_dummy function."""
    result = hello_dummy()
    assert result == "Greetings from sim_sci_test_monorepo.dummy!"


def test_dummy_model() -> None:
    """Test the DummyModel class."""
    model = DummyModel("test_model", 1000, "Test_Location")
    assert model.name == "test_model"
    assert model.population_size == 1000
    assert model.location == "Test_Location"
    assert model.greet() == "Core utility test_model is now ready!"
    assert model.simulate() == "Simulating test_model for population of 1000 at Test_Location"
    assert model.end() == "Ending simulation for test_model"
