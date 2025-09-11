package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class DadosBancariosDevemSerFornecidosException extends Exception {
    public DadosBancariosDevemSerFornecidosException() {
        super("Dados bancarios devem ser fornecidos.");
    }
}