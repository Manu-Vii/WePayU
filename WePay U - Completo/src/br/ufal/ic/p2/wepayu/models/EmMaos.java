package br.ufal.ic.p2.wepayu.models;

/**
 * Implementação concreta de {@link MetodoPagamento} que representa o pagamento "em mãos".
 * <p>
 * Esta classe modela o cenário em que o empregado recebe seu salário em dinheiro diretamente.
 * Por não possuir estado (nenhum atributo), a sua implementação é simples.
 * </p>
 */
public class EmMaos extends MetodoPagamento {

    /**
     * Construtor padrão.
     */
    public EmMaos() {}

    /**
     * Retorna a representação em String deste método de pagamento.
     *
     * @return A string "emMaos".
     */
    @Override
    public String toString() {
        return "emMaos";
    }

    /**
     * Cria e retorna uma cópia (clone) deste objeto.
     * <p>
     * Como a classe não possui estado, a clonagem consiste em criar uma nova instância.
     * </p>
     *
     * @return Uma nova instância de {@link EmMaos}.
     */
    @Override
    public MetodoPagamento clone() {
        return new EmMaos();
    }
}