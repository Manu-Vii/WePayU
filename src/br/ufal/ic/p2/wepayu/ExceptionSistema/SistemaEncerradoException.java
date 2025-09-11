package br.ufal.ic.p2.wepayu.ExceptionSistema;

public class SistemaEncerradoException extends Exception {
    public SistemaEncerradoException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}