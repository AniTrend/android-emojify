# Code Quality Setup

This project now includes comprehensive linting, formatting, and type checking tools.

## Tools Used

- **[Ruff](https://docs.astral.sh/ruff/)**: Ultra-fast Python linter and formatter (replaces flake8, black, isort, and more)
- **[MyPy](https://mypy.readthedocs.io/)**: Static type checker for Python

## Available Commands

You can run these commands using Poetry:

### Running Tests
```bash
# Run all tests
poetry run pytest

# Run tests with verbose output
poetry run pytest -v

# Run a specific test file
poetry run pytest tests/generator_test.py
```

## Pre-commit Setup (Optional)

You can set up pre-commit hooks to automatically run these checks before each commit:

1. Install pre-commit:
   ```bash
   pip install pre-commit
   ```

2. Create a `.pre-commit-config.yaml` file:
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

## Configuration

### Ruff Configuration
All Ruff settings are configured in `pyproject.toml` under `[tool.ruff]`. The configuration includes:

- Line length: 88 characters (same as Black)
- Target Python version: 3.13
- Comprehensive rule selection covering code quality, security, and style
- Specific ignores for test files and common false positives

### MyPy Configuration
MyPy settings are in `pyproject.toml` under `[tool.mypy]` with strict type checking enabled:

- Disallow untyped functions
- Warn about unused ignores
- Check for unreachable code
- Strict equality checks

### Test Configuration
Tests have relaxed linting rules in the `[tool.ruff.lint.per-file-ignores]` section.

## CI/CD Integration

A GitHub Actions workflow file (`emoji-generator-ci.yml`) is provided that:

- Runs on Python 3.13
- Caches Poetry dependencies for faster builds
- Runs all linting, formatting, type checking, and tests
- Fails the build if any checks fail

To use it, move the file to `.github/workflows/emoji-generator-ci.yml` in your repository root.

## VS Code Integration

For the best development experience in VS Code, install these extensions:

1. **Python** (Microsoft)
2. **Ruff** (Charliermarsh)
3. **Mypy Type Checker** (Microsoft)

Add these settings to your VS Code workspace settings (`.vscode/settings.json`):

```json
{
  "python.defaultInterpreterPath": "./.venv/bin/python",
  "python.linting.enabled": false,  // Disable built-in linting
  "ruff.enable": true,
  "ruff.organizeImports": true,
  "ruff.fixAll": true,
  "[python]": {
    "editor.defaultFormatter": "charliermarsh.ruff",
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
      "source.fixAll.ruff": true,
      "source.organizeImports.ruff": true
    }
  },
  "mypy-type-checker.importStrategy": "fromEnvironment",
  "python.analysis.typeCheckingMode": "strict"
}
```

## Common Issues and Solutions

### Import Sorting
Ruff automatically sorts imports. If you see import-related errors, run:
```bash
poetry run ruff check --fix .
```

### Type Errors
For type-related issues, make sure all type stubs are installed:
```bash
poetry add --group dev types-requests  # Already included
```

### Line Length
The project uses 88 characters as the line length limit (Black's default). Long lines will be automatically wrapped by the formatter where possible.

### Testing
When writing tests, you can use `assert` statements freely - they're allowed in test files via the configuration.

## Benefits

This setup provides:

- **Consistency**: Automatic code formatting ensures consistent style
- **Quality**: Comprehensive linting catches potential issues early  
- **Type Safety**: MyPy catches type-related errors before runtime
- **Speed**: Ruff is extremely fast compared to traditional tools
- **Integration**: Works seamlessly with IDEs and CI/CD pipelines
