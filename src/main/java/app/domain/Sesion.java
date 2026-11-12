package app.domain;

import app.domain.enums.Rol;

/** Identidad del usuario autenticado; cada caso de uso la recibe para autorizar la operación. */
public record Sesion(String idCuenta, String idPerfil, String email, Rol rol) {
}
