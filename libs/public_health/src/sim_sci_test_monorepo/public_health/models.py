"""
Example public health functionality
"""

from sim_sci_test_monorepo.core.utils import CoreUtility


def hello_public_health():
    """A simple hello function from public health."""
    return "Hello from sim_sci_test_monorepo.public_health!"


class HealthModel(CoreUtility):
    """An example health model that extends core functionality."""

    def __init__(self, name: str, population_size: int):
        super().__init__(name)
        self.population_size = population_size

    def simulate(self) -> str:
        return f"Simulating {self.name} for population of {self.population_size}"
