"""
Tests for public health models
"""

import pytest

from sim_sci_test_monorepo.public_health.models import HealthModel, hello_public_health


def test_hello_public_health() -> None:
    """Test the hello_public_health function."""
    result = hello_public_health()
    assert result == "Greetings from sim_sci_test_monorepo.public_health!"


def test_health_model() -> None:
    """Test the HealthModel class."""
    model = HealthModel("test_model", 1000, "Test_Location")
    assert model.name == "test_model"
    assert model.population_size == 1000
    assert model.location == "Test_Location"
    assert model.greet() == "Core utility test_model is now ready!"
    assert model.simulate() == "Simulating test_model for population of 1000 at Test_Location"
    assert model.end() == "Ending simulation for test_model"