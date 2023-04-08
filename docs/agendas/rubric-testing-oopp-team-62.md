# Rubric: Testing

The students are not supposed to submit a specific assignment, instead, you are supposed to look into their code base. You can share this rubric with the teams and ask them for pre-filled feedback until Friday.


### Coverage

Testing is an integral part of the coding activities. Unit tests cover all parts of the application (client, server, commons). Excellent teams will also pay attention to the (unit test) code coverage of crucial system components (accordind to the build result over all components).


- Sufficient: The average MR does not only contain business logic, but also unit tests.



### Unit Testing

Classes are tested in isolation. Configurable *dependent-on-components* are passed to the *system-under-test* to avoid integration tests (for example, to avoid running a database or opening REST requests in each a test run).


- Excellent: Configurable subclasses are created to replace dependent-on-components in most of the tests.


### Indirection

The project applies the test patterns that have been covered in the lecture on *Dependency Injection*. More specifically, the test suite includes tests for indirect input/output and behavior.

- Excellent: The project contains tests that assert indirect input, indirect output, and behavior for multiple systems-under-test.


### Endpoint Testing

The REST API is tested through automated JUnit tests.

- Excellent: The project contains automated tests that cover regular and exceptional use of most endpoints.


