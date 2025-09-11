package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class ComissaoDeveSerNumericaException extends Exception {
    public ComissaoDeveSerNumericaException() {
        super("Comissao deve ser numerica.");
    }
}