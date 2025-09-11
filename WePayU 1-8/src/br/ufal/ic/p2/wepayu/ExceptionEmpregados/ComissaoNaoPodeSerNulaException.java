package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class ComissaoNaoPodeSerNulaException extends Exception {
    public ComissaoNaoPodeSerNulaException() {
        super("Comissao nao pode ser nula.");
    }
}