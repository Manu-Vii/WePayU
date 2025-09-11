package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class TaxaSindicalDeveSerNumericaException extends Exception {
    public TaxaSindicalDeveSerNumericaException() {
        super("Taxa sindical deve ser numerica.");
    }
}