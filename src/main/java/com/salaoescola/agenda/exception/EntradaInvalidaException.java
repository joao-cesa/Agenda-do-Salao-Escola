package com.salaoescola.agenda.exception;

/**
 * Dado enviado pelo cliente que não passa nas regras (ex.: serviço inexistente no corpo da requisição).
 * Vira HTTP 400. As regras de expediente/domingo (#12) devem reutilizar esta exceção.
 */
public class EntradaInvalidaException extends RuntimeException {

    public EntradaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
