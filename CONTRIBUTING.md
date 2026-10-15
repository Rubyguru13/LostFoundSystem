# Contributing to Lost & Found Management System

Thank you for your interest in contributing! We welcome contributions from the community.

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- Clear description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Your environment (OS, Java version, JavaFX version, MySQL version)

### Suggesting Features

Feature requests are welcome! Please provide:
- Clear description of the feature
- Use cases and benefits
- Proposed implementation (if you have ideas)

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow existing code style
   - Add comments for complex logic
   - Update documentation if needed

4. **Test your changes**
   - Ensure application compiles
   - Test all affected features
   - Verify database operations work correctly

5. **Commit your changes**
   ```bash
   git commit -m "Add: Brief description of your changes"
   ```
   Use prefixes: `Add:`, `Fix:`, `Update:`, `Remove:`

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Provide clear description
   - Reference any related issues
   - Include screenshots for UI changes

## Code Style Guidelines

### Java
- Use meaningful variable and method names
- Follow camelCase naming convention
- Add JavaDoc comments for public methods
- Keep methods focused and concise
- Use parameterized queries for database operations

### JavaFX UI
- Follow existing styling patterns
- Maintain the blue theme (#2196F3, #E3F2FD, #BBDEFB)
- Ensure responsive design
- Add proper error handling

### Database
- Use prepared statements
- Add appropriate indexes
- Document schema changes
- Provide migration scripts if needed

## Development Setup

1. Install prerequisites (see README.md)
2. Clone your fork
3. Set up MySQL database
4. Configure database credentials
5. Compile and test

## Testing

Before submitting:
- [ ] Application compiles without errors
- [ ] All features work as expected
- [ ] No console errors or warnings
- [ ] Database operations complete successfully
- [ ] UI is responsive and styled correctly

## Questions?

Feel free to create an issue for any questions about contributing!

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the code, not the person
- Help others learn and grow

Thank you for contributing! 🎉
