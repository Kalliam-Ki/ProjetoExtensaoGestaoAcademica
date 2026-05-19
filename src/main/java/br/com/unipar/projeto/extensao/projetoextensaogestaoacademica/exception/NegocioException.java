package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception;

public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}