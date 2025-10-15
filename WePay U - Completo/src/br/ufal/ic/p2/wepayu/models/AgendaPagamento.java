package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

/**
 * Representa uma agenda de pagamento que define as regras para os dias de pagamento.
 * <p>
 * Cada instância desta classe é baseada em uma descrição textual que determina
 * a frequência e o dia do pagamento (ex: "semanal 5", "mensal $").
 * </p>
 */
public class AgendaPagamento {
    private String descricao;

    /**
     * Construtor que cria uma nova agenda de pagamento com base em uma descrição.
     *
     * @param descricao A regra que define a agenda (ex: "semanal 5").
     */
    public AgendaPagamento(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Verifica se um pagamento é devido em uma data específica, com base na regra da agenda.
     * <p>
     * Nota: A implementação atual é um stub e precisa ser desenvolvida.
     * </p>
     *
     * @param data A data a ser verificada.
     * @return {@code true} se for um dia de pagamento, {@code false} caso contrário.
     */
    public boolean devePagar(LocalDate data) {

        // Lógica de verificação da data de pagamento deve ser implementada aqui.
        return false;
    }

    /**
     * Retorna a descrição da regra desta agenda de pagamento.
     *
     * @return A descrição da agenda.
     */
    public String getDescricao() {
        return descricao;
    }
}