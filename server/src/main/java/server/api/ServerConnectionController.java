package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/connection")
public class ServerConnectionController {

    @GetMapping(path = {"", "/"})
    public ResponseEntity checkServer(){
        return ResponseEntity.ok().build();
    }

}
