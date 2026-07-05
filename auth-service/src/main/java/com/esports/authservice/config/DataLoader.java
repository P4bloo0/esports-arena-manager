package com.esports.authservice.config;

import com.esports.authservice.models.Rol;
import com.esports.authservice.repositories.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;

    public DataLoader(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {
        crearRolSiNoExiste("ADMINISTRADOR");
        crearRolSiNoExiste("ORGANIZADOR");
        crearRolSiNoExiste("JUGADOR");
    }

    private void crearRolSiNoExiste(String nombre) {
        rolRepository.findByNombre(nombre).orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }
}
