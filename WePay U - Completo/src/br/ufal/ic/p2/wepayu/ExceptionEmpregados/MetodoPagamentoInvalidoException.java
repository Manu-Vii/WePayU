package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class MetodoPagamentoInvalidoException extends Exception {
    public MetodoPagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }
}