package com.salaoescola.agenda.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.salaoescola.agenda.dto.ErroResponseDTO;
import com.salaoescola.agenda.exception.EntradaInvalidaException;
import com.salaoescola.agenda.exception.EstadoInvalidoException;
import com.salaoescola.agenda.exception.RecursoNaoEncontradoException;

/**
 * Traduz exceções em respostas JSON claras. Falhas de banco ficam no log do servidor; o cliente só
 * recebe uma mensagem genérica, sem SQL, host, usuário ou stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .sorted()
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos.", detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> tratarCorpoIlegivel(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Confira o JSON e o formato das datas (ex.: 2026-12-01T14:00:00).", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> tratarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + ex.getName() + "'.", null);
    }

    @ExceptionHandler(EntradaInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> tratarEntradaInvalida(EntradaInvalidaException ex) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<ErroResponseDTO> tratarEstadoInvalido(EstadoInvalidoException ex) {
        return resposta(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErroResponseDTO> tratarBancoIndisponivel(DataAccessResourceFailureException ex) {
        log.error("Banco de dados indisponível", ex);
        return resposta(HttpStatus.SERVICE_UNAVAILABLE,
                "Banco de dados indisponível no momento. Tente novamente em instantes.", null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErroResponseDTO> tratarErroDePersistencia(DataAccessException ex) {
        log.error("Erro de persistência", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível concluir a operação no banco de dados.", null);
    }

    private ResponseEntity<ErroResponseDTO> resposta(HttpStatus status, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(status)
                .body(new ErroResponseDTO(status.value(), status.getReasonPhrase(), mensagem, detalhes));
    }
}
