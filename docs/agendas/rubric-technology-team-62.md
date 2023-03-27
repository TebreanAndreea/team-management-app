# Rubric: Technology

The students are not supposed to submit a specific assignment, instead, you are supposed to look into their code base.

### Dependency Injection

Application uses dependency injection to connect dependent components. No use of static fields in classes.


- *Excellent:* The application (client and server) uses dependency injection everywhere to connect dependent components. The projects also binds some external types so they can be injected.



### Spring Boot

Application makes good use of the presented Spring built-in concepts to configure the server and maintain the lifecycle of the various server components.


- *Good:* The application contains example of @Controller, @RestController, and a JPA repository.


### JavaFX

Application uses JavaFX for the client and makes good use of available features (use of buttons/images/lists/formatting/…). The connected JavaFX controllers are used with dependency injection.

- *Excellent:* The JavaFX controllers are used with dependency injection.



### Communication

Application uses communication via REST requests and Websockets. The code is leveraging the canonical Spring techniques for endpoints and websocket that have been introduced in the lectures. The client uses libraries to simplify access.

- *Good:* All communication between client and server is implemented with REST or websockets.


### Data Transfer

Application defines meaningful data structures and uses Jackson to perform the de-/serialization of submitted data.

- *Good:* Application defines data structures and both client and server use Jackson to perform the de-/serialization of submitted data. If required, custom Jackson modules are provided that can de-/serialize external types.


