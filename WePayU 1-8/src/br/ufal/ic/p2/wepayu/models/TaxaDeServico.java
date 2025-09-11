package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma única taxa de serviço lançada para um empregado sindicalizado.
 * Armazena a data em que a taxa foi aplicada e o seu valor.
 * A classe é {@link Serializable} para permitir a persistência de seus objetos.
 */
public class TaxaDeServico implements Serializable {
    private String dataStr;
    private String valorStr;

    /**
     * Construtor padrão.
     */
    public TaxaDeServico() {}

    /**
     * Constrói uma TaxaDeServico com data and valor especificados.
     *
     * @param data  A data da cobrança da taxa.
     * @param valor O valor monetário da taxa.
     */
    public TaxaDeServico(LocalDate data, BigDecimal valor) {
        setData(data);
        setValor(valor);
    }

    /**
     * Cria e retorna uma cópia deste objeto.
     *
     * @return um clone desta {@link TaxaDeServico}.
     */
    @Override
    public TaxaDeServico clone() {
        return new TaxaDeServico(this.getData(), this.getValor());
    }

    /**
     * Retorna a data da taxa de serviço.
     *
     * @return a data como {@link LocalDate}.
     */
    public LocalDate getData() {
        if (dataStr == null) return null; return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/uuuu"));
    }

    /**
     * Define a data da taxa de serviço.
     *
     * @param data a data a ser definida como {@link LocalDate}.
     */
    public void setData(LocalDate data) {
        if (data != null) { this.dataStr = data.format(DateTimeFormatter.ofPattern("d/M/uuuu")); }
        else { this.dataStr = null; }
    }

    /**
     * Retorna o valor da taxa de serviço.
     *
     * @return o valor monetário como {@link BigDecimal}.
     */
    public BigDecimal getValor() {
        if (valorStr == null) return null;
        return new BigDecimal(valorStr.replace(",", "."));
    }

    /**
     * Define o valor da taxa de serviço.
     *
     * @param valor o valor monetário a ser definido como {@link BigDecimal}.
     */
    public void setValor(BigDecimal valor) {
        if (valor != null) { this.valorStr = valor.toPlainString(); }
        else { this.valorStr = null; }
    }

    /**
     * Retorna a representação em String da data.
     *
     * @return a data como {@link String}.
     */
    public String getDataStr()
    { return dataStr; }

    /**
     * Define a representação em String da data.
     *
     * @param dataStr a data a ser definida como {@link String}.
     */
    public void setDataStr(String dataStr)
    { this.dataStr = dataStr; }

    /**
     * Retorna a representação em String do valor.
     *
     * @return o valor como {@link String}.
     */
    public String getValorStr()
    { return valorStr; }

    /**
     * Define a representação em String do valor.
     *
     * @param valorStr o valor a ser definido como {@link String}.
     */
    public void setValorStr(String valorStr)
    { this.valorStr = valorStr; }
}