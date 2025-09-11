package br.ufal.ic.p2.wepayu.ExceptionPonto;


public class HorasNaoPositivasException extends Exception {
    public HorasNaoPositivasException() {
        super("Horas devem ser positivas.");
    }
}