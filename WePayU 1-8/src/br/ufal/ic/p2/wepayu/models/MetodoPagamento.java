package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

/**
 * Classe abstrata que serve como base para todos os métodos de pagamento.
 * Define o contrato que todas as formas de pagamento concretas devem seguir,
 * como a capacidade de serem clonadas.
 * A classe é {@link Serializable} para permitir a persistência de seus objetos.
 */
public abstract class MetodoPagamento implements Serializable {

    /**
     * Cria e retorna uma cópia deste objeto de método de pagamento.
     * As classes concretas devem implementar este método para garantir
     * a clonagem adequada de seus atributos.
     *
     * @return um clone deste {@link MetodoPagamento}.
     */
    public abstract MetodoPagamento clone();
}