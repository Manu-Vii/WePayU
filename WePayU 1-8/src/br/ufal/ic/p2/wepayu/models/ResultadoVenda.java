package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa o resultado de uma única venda realizada por um empregado comissionado.
 * Armazena a data e o valor da venda.
 * A classe é {@link Serializable} para permitir a persistência de seus objetos.
 */
public class ResultadoVenda implements Serializable {
    private String dataStr;
    private String valorStr;

    /**
     * Construtor padrão.
     */
    public ResultadoVenda() {}

    /**
     * Constrói um ResultadoVenda com data e valor especificados.
     *
     * @param data  A data em que a venda foi realizada.
     * @param valor O valor monetário da venda.
     */
    public ResultadoVenda(LocalDate data, BigDecimal valor) {
        setData(data);
        setValor(valor);
    }

    /**
     * Retorna a data da venda.
     *
     * @return a data como {@link LocalDate}.
     */
    public LocalDate getData() {
        if (dataStr == null) return null;
        return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/uuuu"));
    }

    /**
     * Define a data da venda.
     *
     * @param data a data a ser definida como {@link LocalDate}.
     */
    public void setData(LocalDate data) {
        if (data != null) {
            this.dataStr = data.format(DateTimeFormatter.ofPattern("d/M/uuuu"));
        } else {
            this.dataStr = null;
        }
    }

    /**
     * Retorna o valor da venda.
     *
     * @return o valor monetário como {@link BigDecimal}.
     */
    public BigDecimal getValor() {
        if (valorStr == null) return null;
        return new BigDecimal(valorStr.replace(",", "."));
    }

    /**
     * Define o valor da venda.
     *
     * @param valor o valor monetário a ser definido como {@link BigDecimal}.
     */
    public void setValor(BigDecimal valor) {
        if (valor != null) {
            this.valorStr = valor.toPlainString();
        } else {
            this.valorStr = null;
        }
    }

    /**
     * Retorna a representação em String da data.
     *
     * @return a data como {@link String}.
     */
    public String getDataStr() {
        return dataStr;
    }

    /**
     * Define a representação em String da data.
     *
     * @param dataStr a data a ser definida como {@link String}.
     */
    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    /**
     * Retorna a representação em String do valor.
     *
     * @return o valor como {@link String}.
     */
    public String getValorStr() {
        return valorStr;
    }

    /**
     * Define a representação em String do valor.
     *
     * @param valorStr o valor a ser definido como {@link String}.
     */
    public void setValorStr(String valorStr) {
        this.valorStr = valorStr;
    }

    /**
     * Cria e retorna uma cópia deste objeto.
     *
     * @return um clone deste {@link ResultadoVenda}.
     */
    @Override
    public ResultadoVenda clone() {
        return new ResultadoVenda(this.getData(), this.getValor());
    }
}