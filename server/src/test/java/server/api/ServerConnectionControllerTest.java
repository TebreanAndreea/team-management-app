package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ServerConnectionControllerTest {

    ServerConnectionController controller;
    @Mock
    ServerProperties serverProperties;
    @BeforeEach
    public void setup(){
        controller = new ServerConnectionController(serverProperties);
    }
    @Test
    void checkServer() {
        assertEquals(ResponseEntity.ok().build(), controller.checkServer());
    }

    @Test
    void returnServer() {
        when(serverProperties.getPort()).thenReturn(8080);
        String server =  controller.returnServer().getBody();
        verify(serverProperties).getPort();
        assertEquals(server, "8080");

    }
}