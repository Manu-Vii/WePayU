package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class EmpregadoNaoEhSindicalizadoException extends Exception {
    public EmpregadoNaoEhSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }
}