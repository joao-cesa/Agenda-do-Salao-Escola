package com.salaoescola.agenda.exception;

/** Operação incompatível com o estado atual do recurso (ex.: cancelar o que já está cancelado). Vira HTTP 409. */
public class EstadoInvalidoException extends RuntimeException {

    public EstadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
