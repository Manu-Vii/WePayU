package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class ValorTrueOrFalseException extends Exception {
    public ValorTrueOrFalseException() {
        super("Valor deve ser true ou false.");
    }
}