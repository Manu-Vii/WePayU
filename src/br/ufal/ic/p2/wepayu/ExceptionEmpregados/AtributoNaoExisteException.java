package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class AtributoNaoExisteException extends Exception {
    public AtributoNaoExisteException() {
        super("Atributo nao existe.");
    }
}