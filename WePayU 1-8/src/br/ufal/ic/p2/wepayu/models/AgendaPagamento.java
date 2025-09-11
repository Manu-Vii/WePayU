package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

/**
 * Representa a agenda de pagamentos de um empregado.
 */
public class AgendaPagamento {
    private String descricao;

    /**
     * Construtor da classe AgendaPagamento.
     * @param descricao A descrição da agenda de pagamento (ex: "semanal 5", "mensal $").
     */
    public AgendaPagamento(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Verifica se um empregado deve ser pago em uma data específica.
     * @param data A data a ser verificada.
     * @return {@code true} se o pagamento for devido, caso contrário {@code false}.
     */
    public boolean devePagar(LocalDate data) {
        return false;
    }

    /**
     * Retorna a descrição da agenda de pagamento.
     * @return A descrição da agenda.
     */
    public String getDescricao() {
        return descricao;
    }
}