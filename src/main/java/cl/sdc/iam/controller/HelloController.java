package cl.sdc.iam.controller; // O un nuevo paquete controller

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/hello-secured")
    public String helloSecured() {
        return "¡Hola! Si ves esto, estas autenticado.";
    }
}