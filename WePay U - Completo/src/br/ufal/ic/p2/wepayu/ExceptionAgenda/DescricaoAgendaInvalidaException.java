package br.ufal.ic.p2.wepayu.ExceptionAgenda;

public class DescricaoAgendaInvalidaException extends Exception {
    public DescricaoAgendaInvalidaException() {
        super("Descricao de agenda invalida");
    }
}