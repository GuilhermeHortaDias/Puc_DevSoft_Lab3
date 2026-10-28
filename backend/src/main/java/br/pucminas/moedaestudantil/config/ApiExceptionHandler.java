package br.pucminas.moedaestudantil.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ErroSaida(String mensagem, Map<String, String> campos) {}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErroSaida> validacao(MethodArgumentNotValidException exception) {
        Map<String, String> campos = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErroSaida("Confira os campos informados.", campos));
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErroSaida> regra(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(new ErroSaida(exception.getReason(), Map.of()));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErroSaida> duplicidade() {
        return ResponseEntity.status(409).body(new ErroSaida("Já existe um registro com os dados únicos informados. Confira login, CPF ou identificação da operação.", Map.of()));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    ResponseEntity<ErroSaida> formato() {
        return ResponseEntity.badRequest().body(new ErroSaida("Formato dos dados inválido.", Map.of()));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ErroSaida> tamanho() {
        return ResponseEntity.badRequest().body(new ErroSaida("A foto deve ter até 2 MB e a requisição até 3 MB.", Map.of()));
    }
}
