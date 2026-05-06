package com.aibert.dosw.domain.exceptions;

public class GamificationProfileNotFoundException extends RuntimeException {
    public GamificationProfileNotFoundException() {
        super("Perfil de gamificación no encontrado");
    }
}
