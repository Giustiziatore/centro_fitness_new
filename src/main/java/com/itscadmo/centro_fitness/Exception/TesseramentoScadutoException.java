package com.itscadmo.centro_fitness.Exception;

public class TesseramentoScadutoException extends RuntimeException{

    private final Long clienteId;

    public TesseramentoScadutoException(String message, Long clienteId) {
        super(message);
        this.clienteId = clienteId;
    }

    public Long getClienteId() {

        return clienteId;
    }

}
