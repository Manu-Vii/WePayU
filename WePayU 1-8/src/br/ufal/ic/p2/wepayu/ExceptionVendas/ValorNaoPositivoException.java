package br.ufal.ic.p2.wepayu.ExceptionVendas;

public class ValorNaoPositivoException extends Exception {
    public ValorNaoPositivoException() {
        super("Valor deve ser positivo.");
    }
}