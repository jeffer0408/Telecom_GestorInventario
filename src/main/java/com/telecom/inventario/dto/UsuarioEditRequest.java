package com.telecom.inventario.dto;

import com.telecom.inventario.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioEditRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo valido")
    private String correo;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "El celular es obligatorio")
    private String celular;

    @NotBlank(message = "El usuario es obligatorio")
    private String username;

    @NotNull(message = "Selecciona un rol")
    private Usuario.Rol rol;

    /** Opcional: si viene vacio, no se cambia la contrasena actual. */
    private String password;
}
