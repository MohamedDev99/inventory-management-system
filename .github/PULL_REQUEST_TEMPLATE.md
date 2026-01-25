## 📝 Description

<!-- Provide a clear and concise description of what this PR does -->

### Summary of Changes

## <!-- List the main changes made in this PR -->

-
-

## 🔗 Related Issues

<!-- Link to related issues using keywords like Closes, Fixes, Resolves -->

Closes #(issue number) Related to #(issue number)

## 🏷️ Type of Change

<!-- Mark the relevant option with an "x" -->

- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 💥 Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] 📚 Documentation update
- [ ] 🎨 Code style/formatting (no functional changes)
- [ ] ♻️ Code refactoring (no functional changes)
- [ ] ⚡ Performance improvement
- [ ] ✅ Test updates
- [ ] 🔧 Configuration changes
- [ ] 🚀 CI/CD changes
- [ ] 🔒 Security fix

## 📍 Component/Module

<!-- Which part of the system does this affect? -->

- [ ] Backend (Spring Boot)
- [ ] Frontend (React)
- [ ] Database
- [ ] DevOps/Infrastructure
- [ ] CI/CD Pipeline
- [ ] Documentation
- [ ] Tests

## 🧪 Testing

<!-- Describe the tests you ran and how to reproduce them -->

### Test Coverage

- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] E2E tests added/updated
- [ ] Manual testing performed
- [ ] No tests needed (explain why)

### Test Results

```
# Paste test results here
# Backend: mvn test
# Frontend: npm run test
```

### How to Test

<!-- Provide steps for reviewers to test your changes -->

1.
2.
3.

## 📸 Screenshots/Recordings

<!-- If applicable, add screenshots or screen recordings to demonstrate changes -->
<!-- Especially important for UI changes -->

### Before

<!-- Screenshot of the old behavior -->

### After

<!-- Screenshot of the new behavior -->

## 💡 Implementation Details

<!-- Explain key technical decisions or complex implementations -->

### Key Changes

- **File/Component 1:** Description of changes
- **File/Component 2:** Description of changes

### Technical Decisions

<!-- Explain any significant technical decisions made -->

### Database Changes

<!-- If applicable, describe schema changes -->

- [ ] Migration script created
- [ ] Migration tested locally
- [ ] Rollback script prepared
- [ ] No database changes

## 🔄 Breaking Changes

<!-- If this is a breaking change, describe the impact and migration path -->

- [ ] API changes (describe)
- [ ] Database schema changes (describe)
- [ ] Configuration changes (describe)
- [ ] No breaking changes

### Migration Guide

<!-- If breaking changes exist, provide migration steps -->

## ⚠️ Known Issues/Limitations

## <!-- List any known issues or limitations with this PR -->

## 📋 Checklist

<!-- Ensure all items are checked before requesting review -->

### Code Quality

- [ ] My code follows the project's code style guidelines
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have removed any console.log, debugging code, or commented-out code
- [ ] No new warnings or errors are introduced

### Testing

- [ ] All existing tests pass locally
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Test coverage has not decreased

### Documentation

- [ ] I have made corresponding changes to the documentation
- [ ] I have updated the README if needed
- [ ] I have updated API documentation (Swagger/OpenAPI)
- [ ] I have added/updated inline code comments where necessary

### Dependencies

- [ ] I have checked for and removed any unused dependencies
- [ ] New dependencies are necessary and well-justified
- [ ] Package versions are up to date and secure
- [ ] No dependency conflicts

### Security

- [ ] I have checked for security vulnerabilities
- [ ] No sensitive data (passwords, keys, tokens) is committed
- [ ] Input validation is implemented where needed
- [ ] SQL injection prevention is in place (parameterized queries)

### Performance

- [ ] I have considered performance implications
- [ ] No N+1 query problems introduced
- [ ] Appropriate indexes are added for new database queries
- [ ] Large lists are paginated

### Git

- [ ] My branch is up to date with the target branch
- [ ] I have resolved all merge conflicts
- [ ] Commit messages follow the project's conventions
- [ ] I have squashed unnecessary commits

### Deployment

- [ ] This PR can be deployed independently
- [ ] I have tested the changes in a staging-like environment
- [ ] Rollback plan is documented (if needed)
- [ ] Environment variables are documented (if new ones added)

## 🚀 Deployment Notes

<!-- Special instructions for deployment, if any -->

- [ ] Requires database migration
- [ ] Requires configuration changes
- [ ] Requires service restart
- [ ] Can be deployed without downtime
- [ ] No special deployment requirements

### Environment Variables

<!-- List any new or changed environment variables -->

```env
# Example:
# NEW_VARIABLE=value
```

## 👀 Reviewer Notes

<!-- Any specific areas you want reviewers to focus on? -->

- Please pay special attention to:
- I'm unsure about:
- Alternative approaches considered:

## 📊 Performance Impact

<!-- If applicable, describe performance impact -->

- [ ] Performance improved
- [ ] No performance impact
- [ ] Performance degraded (explain why acceptable)

### Metrics

<!-- If you have before/after metrics -->

```
Before:
After:
```

## 🔐 Security Considerations

<!-- Any security implications or considerations -->

- [ ] Security audit performed
- [ ] No security implications
- [ ] Authentication/Authorization changes
- [ ] Input validation added/updated

## 📦 Dependencies Added/Updated

<!-- List any new or updated dependencies -->

| Package | Version | Purpose |
| ------- | ------- | ------- |
|         |         |         |

## 🎯 Post-Merge Tasks

<!-- Any tasks that need to be done after merging -->

- [ ] Update staging environment
- [ ] Notify team of changes
- [ ] Update related documentation
- [ ] Schedule production deployment
- [ ] Monitor logs after deployment

## 📝 Additional Notes

<!-- Any other information that reviewers should know -->

---

**Reviewer Checklist:**

- [ ] Code review completed
- [ ] Tests reviewed and passing
- [ ] Documentation is adequate
- [ ] No security concerns
- [ ] Ready to merge
