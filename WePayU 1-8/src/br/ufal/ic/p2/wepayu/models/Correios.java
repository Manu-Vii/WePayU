package br.ufal.ic.p2.wepayu.models;

/**
 * Representa o método de pagamento por Correios.
 */
public class Correios extends MetodoPagamento {

    /**
     * Construtor padrão.
     */
    public Correios() {}

    /**
     * Cria e retorna uma cópia deste objeto Correios.
     * @return Uma cópia do objeto.
     */
    @Override
    public MetodoPagamento clone() {
        return new Correios();
    }

    /**
     * Retorna a representação em string do método de pagamento.
     * @return A string "correios".
     */
    @Override
    public String toString() {
        return "correios";
    }
}