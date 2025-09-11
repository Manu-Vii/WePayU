package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class NomeInvalidoException extends Exception {
    public NomeInvalidoException() {
        super("Nome deve conter apenas letras e espacos.");
    }
}