.PHONY: install install-dev test test-core test-public-health format lint clean help

# Default target
help:
	@echo "Available targets:"
	@echo "  install          - Install both libraries in development mode"
	@echo "  install-dev      - Install development dependencies"
	@echo "  test            - Run all tests"
	@echo "  test-core       - Run core library tests"
	@echo "  test-public-health - Run public health library tests"
	@echo "  format          - Format code with black and isort"
	@echo "  lint            - Run linting with flake8"
	@echo "  clean           - Clean build artifacts"

# Install both libraries in development mode
install:
	uv pip install -e libs/core
	uv pip install -e libs/public_health

# Install development dependencies
install-dev:
	uv pip install -e "libs/core[dev]"
	uv pip install -e "libs/public_health[dev]"

# Run all tests
test:
	pytest libs/core/tests libs/public_health/tests -v

# Run core tests only
test-core:
	pytest libs/core/tests -v

# Run public health tests only
test-public-health:
	pytest libs/public_health/tests -v

# Format code
format:
	black libs/
	isort libs/

# Lint code
lint:
	flake8 libs/

# Clean build artifacts
clean:
	find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name "*.egg-info" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name "build" -exec rm -rf {} + 2>/dev/null || true
	find . -type d -name "dist" -exec rm -rf {} + 2>/dev/null || true