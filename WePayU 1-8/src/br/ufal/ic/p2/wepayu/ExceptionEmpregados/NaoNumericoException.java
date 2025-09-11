package br.ufal.ic.p2.wepayu.ExceptionEmpregados;

public class NaoNumericoException extends Exception {

    public NaoNumericoException(String atributo) {
        super(atributo + " deve ser numerico.");
    }

    public NaoNumericoException() {
        super("Comissao deve ser numerico.");
    }

}