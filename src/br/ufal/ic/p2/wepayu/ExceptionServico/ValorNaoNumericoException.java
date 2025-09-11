package br.ufal.ic.p2.wepayu.ExceptionServico;

public class ValorNaoNumericoException extends Exception {
    public ValorNaoNumericoException() {
        super("Valor deve ser numerico.");
    }
}