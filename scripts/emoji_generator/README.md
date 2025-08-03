# emoji-generator

A Python utility for generating and transforming emoji data from emojibase sources.

## Installation

This project uses [Poetry](https://python-poetry.org/) for dependency management. Make sure you have Poetry installed, then run:

```bash
poetry install
```

## Usage

### Running the Emoji Generator

Set the emoji version environment variable and run the generator:

```bash
export EMOJI_VERSION="15.1"
poetry run emoji-generator
```

Or run it directly:

```bash
EMOJI_VERSION="15.1" poetry run emoji-generator
```

### Running Tests

```bash
# Run all tests
poetry run pytest

# Run tests with verbose output
poetry run pytest -v

# Run a specific test file
poetry run pytest tests/generator_test.py
```

## Code Quality Commands

This project includes comprehensive linting, formatting, and type checking tools.

### Linting

```bash
# Check for linting issues
ruff check .

# Fix linting issues automatically where possible
ruff check --fix .
```

### Formatting

```bash
# Format code automatically
ruff format --fix .

# Check if code is properly formatted (no changes)
ruff format .
```

### Type Checking

```bash
# Run type checking
poetry run mypy .
```

### Testing

```bash
# Run tests
poetry run pytest

# Run tests with verbose output
poetry run pytest -v

# Or use pytest directly
poetry run pytest
poetry run pytest -v
```

## Development Setup

The project uses:
- **[Ruff](https://docs.astral.sh/ruff/)**: Ultra-fast Python linter and formatter
- **[MyPy](https://mypy.readthedocs.io/)**: Static type checker for Python
- **[pytest](https://docs.pytest.org/)**: Testing framework

### Pre-commit Hooks (Optional)

To automatically run code quality checks before each commit:

1. Install pre-commit:
   ```bash
   pip install pre-commit
   ```

2. Create a `.pre-commit-config.yaml` file in the project root:
   ```yaml
   repos:
     - repo: https://github.com/astral-sh/ruff-pre-commit
       rev: v0.8.6
       hooks:
         - id: ruff
           args: [--fix]
         - id: ruff-format
     - repo: https://github.com/pre-commit/mirrors-mypy
       rev: v1.17.0
       hooks:
         - id: mypy
           additional_dependencies: [types-requests]
   ```

3. Install the hooks:
   ```bash
   pre-commit install
   ```

## Project Structure

```
emoji_generator/
├── __init__.py
├── decorators.py    # Error handling decorators
├── generator.py     # Main generator logic
├── models.py        # Data models and enums
├── sources.py       # Data source fetching
└── utils.py         # Utility functions

tests/
├── __init__.py
└── generator_test.py  # Test suite

pyproject.toml       # Project configuration
```

## Configuration

All tool configurations are in `pyproject.toml`:
- **Ruff**: Linting and formatting rules
- **MyPy**: Type checking settings
- **Poetry**: Dependencies and scripts

For detailed configuration information, see [CODE_QUALITY.md](CODE_QUALITY.md).

## CI/CD

A GitHub Actions workflow is provided in `emoji-generator-ci.yml`. Move this file to `.github/workflows/` in your repository root to enable continuous integration.

The workflow runs:
- Code linting with Ruff
- Code formatting checks
- Type checking with MyPy
- All tests with pytest

## Contributing

1. Ensure all code quality checks pass: `poetry run check-all`
2. Run the test suite: `poetry run pytest`
3. Follow the existing code style and patterns
4. Add tests for new functionality
