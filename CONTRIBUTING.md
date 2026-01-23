# Contributing to Inventory Management System

First off, thank you for considering contributing to the Inventory Management System! It's people like you that make this project such a great tool.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Workflow](#development-workflow)
- [Commit Message Standards](#commit-message-standards)
- [Branch Naming Convention](#branch-naming-convention)
- [Pull Request Process](#pull-request-process)
- [Code Style Guidelines](#code-style-guidelines)
- [Testing Requirements](#testing-requirements)
- [Documentation](#documentation)

## 📜 Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please be respectful and considerate of others.

### Our Standards

**Examples of behavior that contributes to a positive environment:**
- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

**Examples of unacceptable behavior:**
- Trolling, insulting/derogatory comments, and personal or political attacks
- Public or private harassment
- Publishing others' private information without explicit permission
- Other conduct which could reasonably be considered inappropriate

## 🤝 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

**Bug Report Template:**
```markdown
**Describe the bug**
A clear and concise description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '...'
3. Scroll down to '...'
4. See error

**Expected behavior**
A clear and concise description of what you expected to happen.

**Screenshots**
If applicable, add screenshots to help explain your problem.

**Environment:**
 - OS: [e.g. Ubuntu 22.04]
 - Browser [e.g. chrome, safari]
 - Version [e.g. 1.0.0]

**Additional context**
Add any other context about the problem here.
```

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Use a clear and descriptive title**
- **Provide a detailed description of the suggested enhancement**
- **Explain why this enhancement would be useful**
- **List any alternative solutions or features you've considered**

### Your First Code Contribution

Unsure where to begin? You can start by looking through `good-first-issue` and `help-wanted` issues:

- **Good first issues** - issues which should only require a few lines of code
- **Help wanted issues** - issues which should be a bit more involved than beginner issues

## 🔄 Development Workflow

### 1. Fork the Repository

```bash
# Fork via GitHub UI, then clone your fork
git clone https://github.com/YOUR-USERNAME/inventory-management-system.git
cd inventory-management-system

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL-OWNER/inventory-management-system.git
```

### 2. Create a Branch

```bash
# Update your fork with the latest changes
git checkout main
git pull upstream main

# Create a new branch (see naming convention below)
git checkout -b feature/your-feature-name
```

### 3. Make Your Changes

- Write clean, readable code
- Follow the code style guidelines
- Add tests for new features
- Update documentation as needed

### 4. Commit Your Changes

Follow our [Commit Message Standards](#commit-message-standards) below.

### 5. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 6. Open a Pull Request

Go to the original repository and create a pull request from your fork.

## 📝 Commit Message Standards

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification. This leads to **more readable messages** and allows us to **generate changelogs automatically**.

### Commit Message Format

Each commit message consists of a **header**, an optional **body**, and an optional **footer**.

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Header

The header is **mandatory** and must conform to the format: `<type>(<scope>): <subject>`

**Type** must be one of the following:

| Type | Description | Example |
|------|-------------|---------|
| `feat` | A new feature | `feat(auth): add JWT token refresh mechanism` |
| `fix` | A bug fix | `fix(inventory): correct stock calculation error` |
| `docs` | Documentation only changes | `docs(readme): update installation instructions` |
| `style` | Changes that don't affect code meaning (formatting, missing semicolons, etc.) | `style(backend): format code with prettier` |
| `refactor` | Code change that neither fixes a bug nor adds a feature | `refactor(api): restructure product service` |
| `perf` | Code change that improves performance | `perf(database): add index on product SKU` |
| `test` | Adding missing tests or correcting existing tests | `test(orders): add unit tests for order service` |
| `build` | Changes that affect the build system or external dependencies | `build(maven): update spring boot to 3.2.0` |
| `ci` | Changes to CI configuration files and scripts | `ci(github): add automated deployment workflow` |
| `chore` | Other changes that don't modify src or test files | `chore(deps): update dependencies` |
| `revert` | Reverts a previous commit | `revert: feat(auth): add JWT token refresh` |

**Scope** (optional) can be anything specifying the place of the commit change:
- `auth`, `inventory`, `orders`, `products`, `users`, `warehouse`
- `api`, `ui`, `database`, `docker`, `k8s`, `terraform`
- `frontend`, `backend`

**Subject** is a short description of the change:
- Use the imperative, present tense: "change" not "changed" nor "changes"
- Don't capitalize the first letter
- No dot (.) at the end
- Maximum 72 characters

#### Body (Optional)

The body should include the motivation for the change and contrast this with previous behavior.

- Use the imperative, present tense
- Wrap at 72 characters
- Explain **what** and **why**, not **how**

#### Footer (Optional)

The footer should contain:
- **Breaking Changes**: Start with `BREAKING CHANGE:` followed by a description
- **Issue References**: Reference GitHub issues that this commit closes

### Commit Message Examples

#### Simple Commit (Header Only)
```bash
git commit -m "feat(products): add barcode scanner support"
```

#### Commit with Body
```bash
git commit -m "fix(inventory): prevent negative stock values

The inventory service was allowing stock to go negative when
processing sales orders. Added validation to check available
stock before order fulfillment.

Closes #123"
```

#### Breaking Change
```bash
git commit -m "feat(api): change product endpoint response format

BREAKING CHANGE: Product API now returns quantity as an integer
instead of string. Update all API clients accordingly.

Closes #456"
```

#### Multiple Changes (Use Separate Commits)
```bash
# Don't do this:
git commit -m "feat: add login and fix styling and update docs"

# Do this instead:
git commit -m "feat(auth): add user login functionality"
git commit -m "style(ui): fix button alignment on login page"
git commit -m "docs(readme): update authentication section"
```

### More Examples

```bash
# Feature additions
feat(orders): implement purchase order approval workflow
feat(dashboard): add real-time inventory metrics
feat(api): add pagination support for product listing

# Bug fixes
fix(auth): resolve token expiration issue
fix(database): correct foreign key constraint on orders table
fix(ui): fix responsive layout on mobile devices

# Documentation
docs(api): add swagger annotations for order endpoints
docs(deployment): update kubernetes deployment guide
docs(contributing): add commit message examples

# Refactoring
refactor(services): extract common inventory logic
refactor(components): split large Dashboard component
refactor(database): optimize product query performance

# Tests
test(orders): add integration tests for order processing
test(inventory): increase code coverage to 80%
test(api): add e2e tests for authentication flow

# CI/CD
ci(github): add docker build workflow
ci(deployment): automate staging deployment
ci(quality): add code coverage reporting

# Chores
chore(deps): update spring boot to 3.2.1
chore(docker): optimize Dockerfile layers
chore(config): update application properties

# Performance
perf(api): add Redis caching for product catalog
perf(query): optimize inventory movement query
perf(frontend): implement lazy loading for routes
```

### Commit Message Validation

We use commit message linting to enforce these standards. Your commits will be automatically validated when you push to the repository.

**Local Setup (Optional but Recommended):**

```bash
# Install commitlint
npm install -g @commitlint/cli @commitlint/config-conventional

# Install husky for git hooks
npm install -g husky

# This will prevent invalid commits
```

## 🌿 Branch Naming Convention

Use descriptive branch names that follow this pattern:

```
<type>/<short-description>
```

### Branch Types

- `feature/` - New features
- `fix/` - Bug fixes
- `hotfix/` - Urgent fixes for production
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Adding or updating tests
- `chore/` - Maintenance tasks
- `ci/` - CI/CD changes

### Examples

```bash
feature/add-barcode-scanner
feature/multi-warehouse-support
fix/inventory-calculation-error
fix/login-session-timeout
hotfix/critical-security-patch
docs/update-api-documentation
refactor/simplify-order-service
test/add-inventory-unit-tests
chore/update-dependencies
ci/add-automated-deployment
```

### Branch Naming Rules

- Use lowercase
- Use hyphens to separate words
- Keep it short but descriptive (max 50 characters)
- Be specific about what the branch does

## 🔍 Pull Request Process

### Before Submitting

1. **Ensure your code builds successfully**
   ```bash
   # Backend
   cd backend && mvn clean install

   # Frontend
   cd frontend && npm run build
   ```

2. **Run all tests**
   ```bash
   # Backend
   cd backend && mvn test

   # Frontend
   cd frontend && npm run test
   ```

3. **Check code style**
   ```bash
   # Backend
   cd backend && mvn checkstyle:check

   # Frontend
   cd frontend && npm run lint
   ```

4. **Update documentation** if you've changed APIs or added features

5. **Rebase your branch** on the latest main
   ```bash
   git checkout main
   git pull upstream main
   git checkout your-branch
   git rebase main
   ```

### Pull Request Template

When creating a pull request, use this template:

```markdown
## Description
Provide a clear and concise description of what this PR does.

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Code refactoring

## Related Issues
Closes #(issue number)

## Changes Made
- List the main changes
- Be specific and concise
- Use bullet points

## Testing
Describe the tests you ran and how to reproduce them:
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing performed

## Screenshots (if applicable)
Add screenshots to help explain your changes.

## Checklist
- [ ] My code follows the code style of this project
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged and published

## Additional Notes
Any additional information or context.
```

### PR Review Process

1. **Automated Checks**: CI/CD pipeline will run automatically
2. **Code Review**: At least one maintainer must review
3. **Testing**: All tests must pass
4. **Approval**: PR must be approved before merging
5. **Merge**: Squash and merge or rebase merge (depending on project settings)

### After Your PR is Merged

1. Delete your branch (both local and remote)
   ```bash
   git branch -d feature/your-feature
   git push origin --delete feature/your-feature
   ```

2. Update your local main
   ```bash
   git checkout main
   git pull upstream main
   ```

## 🎨 Code Style Guidelines

### Backend (Java/Spring Boot)

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Maximum line length: 120 characters
- Use 4 spaces for indentation (no tabs)
- Always use curly braces for if/else/for/while
- Add JavaDoc comments for public methods

**Example:**
```java
/**
 * Retrieves a product by its SKU.
 *
 * @param sku the unique stock keeping unit identifier
 * @return the product if found
 * @throws ProductNotFoundException if product doesn't exist
 */
public Product getProductBySku(String sku) {
    return productRepository.findBySku(sku)
        .orElseThrow(() -> new ProductNotFoundException(sku));
}
```

### Frontend (TypeScript/React)

- Follow [Airbnb React/JSX Style Guide](https://airbnb.io/javascript/react/)
- Use functional components with hooks
- Use TypeScript for type safety
- Maximum line length: 100 characters
- Use 2 spaces for indentation
- Use meaningful component and variable names
- Extract reusable components

**Example:**
```typescript
interface ProductCardProps {
  product: Product;
  onEdit: (id: string) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onEdit }) => {
  const handleEdit = () => {
    onEdit(product.id);
  };

  return (
    <div className="product-card">
      <h3>{product.name}</h3>
      <p>{product.sku}</p>
      <button onClick={handleEdit}>Edit</button>
    </div>
  );
};
```

### General Guidelines

- **DRY (Don't Repeat Yourself)**: Avoid code duplication
- **KISS (Keep It Simple, Stupid)**: Prefer simple solutions
- **YAGNI (You Aren't Gonna Need It)**: Don't add functionality until needed
- **Single Responsibility Principle**: Each function/class should do one thing well
- Write self-documenting code with clear names
- Add comments only when necessary to explain "why", not "what"

## ✅ Testing Requirements

### Minimum Coverage

- **Backend**: 80% code coverage
- **Frontend**: 70% code coverage

### Testing Guidelines

1. **Unit Tests**: Test individual functions/methods in isolation
2. **Integration Tests**: Test how components work together
3. **E2E Tests**: Test complete user workflows
4. **Test Naming**: Use descriptive names that explain what is being tested

**Example Test Names:**
```java
// Good
@Test
void shouldReturnProductWhenValidSkuProvided()

@Test
void shouldThrowExceptionWhenProductNotFound()

// Bad
@Test
void testProduct()

@Test
void test1()
```

### Running Tests

```bash
# Backend - All tests
cd backend && mvn test

# Backend - Specific test
mvn test -Dtest=ProductServiceTest

# Frontend - All tests
cd frontend && npm run test

# Frontend - Watch mode
npm run test:watch

# Frontend - Coverage
npm run test:coverage
```

## 📚 Documentation

### Code Documentation

- Add JavaDoc/JSDoc comments for public APIs
- Document complex algorithms or business logic
- Keep documentation up-to-date with code changes

### README Updates

- Update README.md if you add new features
- Add screenshots for UI changes
- Update environment variables section if needed

### API Documentation

- Update Swagger/OpenAPI annotations
- Add request/response examples
- Document error codes and responses

## ❓ Questions?

If you have questions about contributing, feel free to:

- Open an issue with the `question` label
- Join our community discussions
- Reach out to the maintainers

## 🏆 Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- Our thanks and appreciation!

---

**Thank you for contributing to making this project better! 🎉**