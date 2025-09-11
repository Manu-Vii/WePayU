package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class IdSindicatoNaoPodeSerNulaException extends Exception {
    public IdSindicatoNaoPodeSerNulaException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}