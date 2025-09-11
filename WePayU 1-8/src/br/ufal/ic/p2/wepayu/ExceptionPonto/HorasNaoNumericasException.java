package br.ufal.ic.p2.wepayu.ExceptionPonto;


public class HorasNaoNumericasException extends Exception {
    public HorasNaoNumericasException() {
        super("Horas devem ser numericas.");
    }
}