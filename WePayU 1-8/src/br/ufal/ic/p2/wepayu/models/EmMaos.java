package br.ufal.ic.p2.wepayu.models;

/**
 * Representa o método de pagamento em mãos.
 */
public class EmMaos extends MetodoPagamento {

    /**
     * Construtor padrão.
     */
    public EmMaos() {}

    /**
     * Retorna a representação em string do método de pagamento.
     * @return A string "emMaos".
     */
    @Override
    public String toString() {
        return "emMaos";
    }

    /**
     * Cria e retorna uma cópia deste objeto EmMaos.
     * @return Uma cópia do objeto.
     */
    @Override
    public MetodoPagamento clone() {
        return new EmMaos();
    }
}