package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}