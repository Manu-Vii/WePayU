package br.ufal.ic.p2.wepayu.ExceptionServico;

public class EmpregadoNaoEhSindicalizadoException extends Exception {
    public EmpregadoNaoEhSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }
}