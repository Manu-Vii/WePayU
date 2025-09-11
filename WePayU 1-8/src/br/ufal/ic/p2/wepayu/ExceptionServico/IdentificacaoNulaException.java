package br.ufal.ic.p2.wepayu.ExceptionServico;

public class IdentificacaoNulaException extends Exception {
    public IdentificacaoNulaException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}