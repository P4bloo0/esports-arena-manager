package com.esports.registrationservice.controllers;
import org.springframework.web.bind.annotation.*;
import com.esports.registrationservice.services.InscripcionServiceImpl;
@RestController
public class CuposController {
 private final InscripcionServiceImpl service; public CuposController(InscripcionServiceImpl service) { this.service=service; }
 @GetMapping("/api/v1/inscripciones/cupos/{id}") public long occupied(@PathVariable Long id) { return service.ocupados(id); }
}
