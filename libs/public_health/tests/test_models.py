"""
Tests for public health models
"""

import pytest

from sim_sci_test_monorepo.public_health.models import HealthModel, hello_public_health


def test_hello_public_health():
    """Test the hello_public_health function."""
    result = hello_public_health()
    assert result == "Hello from sim_sci_test_monorepo.public_health!"


def test_health_model():
    """Test the HealthModel class."""
    model = HealthModel("test_model", 1000)
    assert model.name == "test_model"
    assert model.population_size == 1000
    assert model.greet() == "Core utility test_model is ready!"
    assert model.simulate() == "Simulating test_model for population of 1000"
