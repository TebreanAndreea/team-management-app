package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SomeControllerTest {

    @Test
    void index() {
        SomeController controller = new SomeController();
        assertEquals("Hello world!", controller.index());
    }
}