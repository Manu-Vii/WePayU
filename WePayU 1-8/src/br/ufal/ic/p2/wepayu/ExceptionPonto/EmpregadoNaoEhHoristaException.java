package br.ufal.ic.p2.wepayu.ExceptionPonto;

public class EmpregadoNaoEhHoristaException extends Exception {
    public EmpregadoNaoEhHoristaException() {
        super("Empregado nao eh horista.");
    }
}
