package server;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class MainTest {

    @Test
    void main() {
        String[] args = new String[0];
        Main.main(args);
        Scanner scanner = new Scanner("server/src/adminPass.txt");
        String password = scanner.next();
        assertNotEquals("", password);

    }

    @Test
    void generateServerPassword() {
        Main.generateServerPassword();
        Scanner scanner = new Scanner("server/src/adminPass.txt");
        String password = scanner.next();
        assertNotEquals("", password);
    }


}