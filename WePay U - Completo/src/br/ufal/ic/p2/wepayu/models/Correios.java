package br.ufal.ic.p2.wepayu.models;

/**
 * Implementação concreta de {@link MetodoPagamento} que representa o pagamento
 * por meio de um cheque enviado pelos Correios.
 * <p>
 * Esta classe, assim como {@link EmMaos}, é stateless (não possui atributos),
 * o que simplifica sua implementação.
 * </p>
 */
public class Correios extends MetodoPagamento {

    /**
     * Construtor padrão.
     */
    public Correios() {}

    /**
     * Cria e retorna uma cópia (clone) deste objeto.
     * <p>
     * Como a classe não possui estado, a clonagem consiste em criar uma nova instância.
     * </p>
     *
     * @return Uma nova instância de {@link Correios}.
     */
    @Override
    public MetodoPagamento clone() {
        return new Correios();
    }

    /**
     * Retorna a representação em String deste método de pagamento.
     *
     * @return A string "correios".
     */
    @Override
    public String toString() {
        return "correios";
    }
}