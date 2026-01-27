# Contributing to Sirv Java SDK

Thank you for your interest in contributing to the Sirv Java SDK!

## Development Setup

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Building

```bash
# Clone the repository
git clone https://github.com/sirv/sirv-rest-api-java.git
cd sirv-rest-api-java

# Build the project
mvn clean install

# Run tests
mvn test
```

### Running Examples

```bash
# Set environment variables
export SIRV_CLIENT_ID="your-client-id"
export SIRV_CLIENT_SECRET="your-client-secret"

# Run an example
mvn exec:java -Dexec.mainClass="com.sirv.api.examples.BasicUsageExample"
```

## Code Style

- Follow standard Java naming conventions
- Use 4 spaces for indentation
- Keep lines under 120 characters
- Add Javadoc comments for public methods
- Write unit tests for new functionality

## Submitting Changes

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Make your changes
4. Run tests (`mvn test`)
5. Commit your changes (`git commit -am 'Add new feature'`)
6. Push to the branch (`git push origin feature/my-feature`)
7. Create a Pull Request

## Reporting Issues

- Use the GitHub issue tracker
- Include Java version and OS information
- Provide a minimal reproducible example
- Include full error messages and stack traces

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
