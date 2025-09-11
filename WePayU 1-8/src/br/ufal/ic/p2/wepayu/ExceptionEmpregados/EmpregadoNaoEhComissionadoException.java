package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class EmpregadoNaoEhComissionadoException extends Exception {
    public EmpregadoNaoEhComissionadoException() {
        super("Empregado nao eh comissionado.");
    }
}