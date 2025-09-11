package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class ComissaoDeveSerNaoNegativaException extends Exception {
    public ComissaoDeveSerNaoNegativaException() {
        super("Comissao deve ser nao-negativa.");
    }
}