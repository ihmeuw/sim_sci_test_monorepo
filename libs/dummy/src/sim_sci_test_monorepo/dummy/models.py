"""
Example dummy functionality
"""

from sim_sci_test_monorepo.core.utils import CoreUtility


def hello_dummy() -> str:
    """A simple hello function from dummy."""
    return "Greetings from sim_sci_test_monorepo.dummy!"


class DummyModel(CoreUtility):  # type: ignore[misc]
    """An example dummy model that extends core functionality."""

    def __init__(self, name: str, population_size: int, location: str) -> None:
        super().__init__(name)
        self.population_size = population_size
        self.location = location

    def simulate(self) -> str:
        return f"Simulating {self.name} for population of {self.population_size} at {self.location}"

    def end(self) -> str:
        return f"Ending simulation for {self.name}"
