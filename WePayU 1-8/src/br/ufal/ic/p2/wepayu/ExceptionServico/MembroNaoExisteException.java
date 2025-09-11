package br.ufal.ic.p2.wepayu.ExceptionServico;

public class MembroNaoExisteException extends Exception {
    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}