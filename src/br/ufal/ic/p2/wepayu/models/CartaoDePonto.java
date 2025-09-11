package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa um único registro de cartão de ponto para um empregado horista.
 * Armazena a data e a quantidade de horas trabalhadas.
 * A classe é {@link Serializable} para permitir a persistência de seus objetos.
 */
public class CartaoDePonto implements Serializable {
    private String dataStr;
    private String horasStr;

    /**
     * Construtor padrão.
     */
    public CartaoDePonto() {}

    /**
     * Constrói um CartaoDePonto com data e horas especificadas.
     *
     * @param data A data do registro de ponto.
     * @param horas A quantidade de horas trabalhadas.
     */
    public CartaoDePonto(LocalDate data, BigDecimal horas) {
        setData(data);
        setHoras(horas);
    }

    /**
     * Cria e retorna uma cópia deste objeto.
     *
     * @return um clone deste {@link CartaoDePonto}.
     */
    @Override
    public CartaoDePonto clone() {
        return new CartaoDePonto(this.getData(), this.getHoras());
    }

    /**
     * Retorna a data do registro de ponto.
     *
     * @return a data como {@link LocalDate}.
     */
    public LocalDate getData() {
        if (dataStr == null) return null;
        return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/uuuu"));
    }

    /**
     * Define a data do registro de ponto.
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
     * Retorna a quantidade de horas trabalhadas.
     *
     * @return a quantidade de horas como {@link BigDecimal}.
     */
    public BigDecimal getHoras() {
        if (horasStr == null) return null;
        return new BigDecimal(horasStr.replace(",", "."));
    }

    /**
     * Define a quantidade de horas trabalhadas.
     *
     * @param horas a quantidade de horas a ser definida como {@link BigDecimal}.
     */
    public void setHoras(BigDecimal horas) {
        if (horas != null) {
            this.horasStr = horas.toPlainString();
        } else {
            this.horasStr = null;
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
     * Retorna a representação em String das horas.
     *
     * @return as horas como {@link String}.
     */
    public String getHorasStr() {
        return horasStr;
    }

    /**
     * Define a representação em String das horas.
     *
     * @param horasStr as horas a serem definidas como {@link String}.
     */
    public void setHorasStr(String horasStr) {
        this.horasStr = horasStr;
    }
}