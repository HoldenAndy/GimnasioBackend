package com.saas.sistema.gimnasio.nucleo.configuracion;

import com.saas.sistema.gimnasio.modulos.gimnasios.GimnasioEntidad;
import com.saas.sistema.gimnasio.modulos.usuarios.RolUsuario;
import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioEntidad;
import com.saas.sistema.gimnasio.modulos.gimnasios.GimnasioRepository;
import com.saas.sistema.gimnasio.modulos.usuarios.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(GimnasioRepository gimnasioRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.gimnasioRepository = gimnasioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (gimnasioRepository.count() == 0) {

            GimnasioEntidad gimnasioEntidad = new GimnasioEntidad();
            gimnasioEntidad.setRazonSocial("GimnasioEntidad Titan Fitness");
            gimnasioEntidad.setRuc("20123456789");
            gimnasioEntidad.setCorreoContacto("admin@titanfitness.com");
            gimnasioEntidad.setTelefonoContacto("987654321");

            GimnasioEntidad gimnasioEntidadGuardado = gimnasioRepository.save(gimnasioEntidad);
            System.out.println("✅ GimnasioEntidad de prueba creado.");

            UsuarioEntidad admin = new UsuarioEntidad();
            admin.setTenantId(gimnasioEntidadGuardado.getId());
            admin.setNombre("Andrés");
            admin.setApellido("Admin");
            admin.setCorreo("andres@titanfitness.com");
            admin.setContrasenaHash(passwordEncoder.encode("123456"));
            admin.setRol(RolUsuario.ADMINISTRADOR);

            usuarioRepository.save(admin);
            System.out.println("✅ UsuarioEntidad administrador creado (andres@titanfitness.com / 123456)");
        }
    }
}