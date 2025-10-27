package cl.sdc.iam.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de ejemplo para probar la autorización basada en roles.
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * Endpoint público accesible sin autenticación.
     *
     * @return Mensaje de saludo público.
     */
    @GetMapping("/hello-public")
    public String helloPublic() {
        return "¡Hola! Endpoint Público.";
    }

    /**
     * Endpoint accesible solo para usuarios autenticados (cualquier rol).
     *
     * @return Mensaje de saludo seguro.
     */
    @GetMapping("/hello-secured")
    @PreAuthorize("isAuthenticated()")
    public String helloSecured() {
        return "¡Hola! Si ves esto, estás autenticado.";
    }

    /**
     * Endpoint accesible solo para usuarios con ROLE_USER.
     *
     * @return Mensaje de saludo para usuarios.
     */
    @GetMapping("/hello-user")
    @PreAuthorize("hasRole('USER')")
    public String helloUser() {
        return "¡Hola Usuario!";
    }

    /**
     * Endpoint accesible solo para usuarios con ROLE_STAFF
     *
     * @return Mensaje de saludo para administradores.
     */
    @GetMapping("/hello-staff")
    @PreAuthorize("hasRole('STAFF')")
    public String helloStaff() {
        return "¡Hola staff!";
    }

    /**
     * Endpoint accesible solo para usuarios con ROLE_STAFF
     *
     * @return Mensaje de saludo para administradores.
     */
    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloAdmin() {
        return "¡Hola admin!";
    }

    /**
     * Endpoint accesible solo para usuarios con ROLE_SUPERADMIN.
     *
     * @return Mensaje de saludo para el super administrador.
     */
    @GetMapping("/hello-superadmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String helloSuperAdmin() {
        return "¡Hola Super Admin!";
    }

    /**
     * Endpoint accesible para usuarios con ROLE_ADMIN o ROLE_SUPERADMIN.
     *
     * @return Mensaje de saludo para el equipo de gestión.
     */
    @GetMapping("/hello-management")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String helloManagement() {
        return "¡Hola Equipo de Gestión!";
    }
}