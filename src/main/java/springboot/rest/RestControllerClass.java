package springboot.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControllerClass {

    @RequestMapping("/")
    public String welcome(){
        System.out.println("welcome came");
        return "Welcome";
    }
}
