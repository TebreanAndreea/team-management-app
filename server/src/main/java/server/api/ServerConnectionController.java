package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/connection")
public class ServerConnectionController {

    @Autowired
    private ServerProperties serverProperties;
    @GetMapping(path = {"", "/"})
    public ResponseEntity checkServer(){
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = {"getServer"})
    public ResponseEntity<String> returnServer()
    {
        Integer check = serverProperties.getPort();
        ResponseEntity<String> rep= ResponseEntity.ok(Integer.toString(check));
        return rep;
    }


}
