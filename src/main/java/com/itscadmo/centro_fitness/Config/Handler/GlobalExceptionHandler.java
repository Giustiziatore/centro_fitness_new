package com.itscadmo.centro_fitness.Config.Handler;

import com.itscadmo.centro_fitness.DTO.ServiceError;
import com.itscadmo.centro_fitness.Exception.TesseramentoScadutoException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Gestione Risorsa Non Trovata (404 NOT FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServiceError gestisciRisorsaNonTrovata(WebRequest req, EntityNotFoundException ex) {
        log.warn("RISORSA NON TROVATA (404): {}", ex.getMessage());
        return creaRispostaErrore(req, ex.getMessage());
    }

    // 2. Gestione Errore di Logica di Business (400 BAD REQUEST)
    @ExceptionHandler(TesseramentoScadutoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceError gestisciEccezioneBusiness(WebRequest req, TesseramentoScadutoException ex) {
        // Logga il cliente che ha causato l'errore per la diagnostica
        log.warn("ERRORE DI BUSINESS (400): Tesseramento scaduto per Cliente ID [{}]. Messaggio: {}",
                ex.getClienteId(), ex.getMessage());
        return creaRispostaErrore(req, ex.getMessage());
    }

    // 3. Gestione Errori di Validazione (@Valid fallito) (400 BAD REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceError gestisciArgomentoNonValido(WebRequest req, MethodArgumentNotValidException ex) {

        // Estrae tutti i messaggi di errore e li concatena in un'unica stringa
        String messaggioDettagliato = ex.getBindingResult().getFieldErrors().stream()

                // Filtro elemento nullo prima di tentare il mapping.
                .filter(Objects::nonNull)

                .map(violazione -> {

                    String campo = violazione.getField();
                    String messaggio = violazione.getDefaultMessage() != null ? violazione.getDefaultMessage() : "Errore sconosciuto";
                    return campo.concat(" : ").concat(messaggio);
                })
                .collect(Collectors.joining(" | "));

        log.warn("VALIDAZIONE FALLITA (400): Campi non validi: {}", messaggioDettagliato);
        return creaRispostaErrore(req, "Validazione fallita: " + messaggioDettagliato);
    }

    // 4. Gestione Generale di Fallimento (500 INTERNAL SERVER ERROR)
    // Cattura tutte le altre eccezioni non gestite.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServiceError gestisciErroreGenerico(WebRequest req, Exception ex) {
        log.error("ERRORE INTERNO (500): Causa: {}", ex.getMessage(), ex); // Logga l'intera traccia (ex)
        return creaRispostaErrore(req, "Si è verificato un errore interno al server.");
    }

    // --- Metodo Helper per Creare la Risposta di Errore (Privato) ---
    private ServiceError creaRispostaErrore(WebRequest req, String messaggio) {
        // WebRequest non ha accesso diretto a getRequestURI(), dobbiamo castare a HttpServletRequest
        HttpServletRequest httpreq = (HttpServletRequest) req.resolveReference("request");

        String PercorsoAPI = "N/D";
        if(httpreq != null) {
            PercorsoAPI = httpreq.getRequestURI();
        }

        return new ServiceError(new Date(), PercorsoAPI, messaggio);
    }
}