package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class IdentificacaoSindicatoJaExisteException extends Exception {
    public IdentificacaoSindicatoJaExisteException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
