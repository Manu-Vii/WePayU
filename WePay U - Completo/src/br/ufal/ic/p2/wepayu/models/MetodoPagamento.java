package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

/**
 * Classe abstrata que serve como base para todos os Métodos de Pagamento no sistema.
 * <p>
 * Define o contrato que todas as formas de pagamento concretas (ex: Banco, Correios, Em Mãos)
 * devem seguir. A classe é {@link Serializable} para permitir que o método de pagamento
 * de um empregado seja persistido junto com seus outros dados.
 * </p>
 */
public abstract class MetodoPagamento implements Serializable {

    /**
     * Método abstrato para criar e retornar uma cópia (clone) do objeto MetodoPagamento.
     * <p>
     * Cada subclasse concreta é obrigada a implementar este método, garantindo que
     * seja possível duplicar o objeto de método de pagamento de forma adequada.
     * </p>
     *
     * @return Uma nova instância que é uma cópia do objeto atual.
     */
    public abstract MetodoPagamento clone();
}