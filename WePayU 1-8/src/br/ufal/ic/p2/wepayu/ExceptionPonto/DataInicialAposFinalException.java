package br.ufal.ic.p2.wepayu.ExceptionPonto;

public class DataInicialAposFinalException extends Exception {
    public DataInicialAposFinalException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
