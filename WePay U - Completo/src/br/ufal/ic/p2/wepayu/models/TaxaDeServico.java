package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo que representa uma Taxa de Serviço lançada para um empregado sindicalizado.
 * A classe armazena a data e o valor da taxa.
 * <p>
 * Nota de implementação: A data e o valor são armazenados internamente como Strings
 * para simplificar a serialização. Os métodos getters e setters fazem a conversão
 * de/para os tipos {@link LocalDate} e {@link BigDecimal}.
 * </p>
 * A classe é {@link Serializable} para permitir a sua persistência.
 */
public class TaxaDeServico implements Serializable {
    private String dataStr;
    private String valorStr;

    /**
     * Construtor padrão (sem argumentos).
     * Necessário para frameworks de serialização.
     */
    public TaxaDeServico() {}

    /**
     * Construtor que inicializa uma taxa de serviço com data e valor específicos.
     *
     * @param data  A data em que a taxa foi aplicada.
     * @param valor O valor monetário da taxa.
     */
    public TaxaDeServico(LocalDate data, BigDecimal valor) {
        setData(data);
        setValor(valor);
    }

    /**
     * Cria e retorna uma cópia (clone) deste objeto TaxaDeServico.
     *
     * @return Uma nova instância de {@link TaxaDeServico} com os mesmos valores de data e valor.
     */
    @Override
    public TaxaDeServico clone() {
        return new TaxaDeServico(this.getData(), this.getValor());
    }

    /**
     * Retorna a data da taxa de serviço.
     * Converte a string interna para um objeto {@link LocalDate}.
     *
     * @return A data da taxa, ou {@code null} se não estiver definida.
     */
    public LocalDate getData() { if (dataStr == null) return null; return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/uuuu")); }

    /**
     * Define a data da taxa de serviço.
     * Formata o objeto {@link LocalDate} para uma string antes de armazenar.
     *
     * @param data A data a ser definida.
     */
    public void setData(LocalDate data) { if (data != null) { this.dataStr = data.format(DateTimeFormatter.ofPattern("d/M/uuuu")); } else { this.dataStr = null; } }

    /**
     * Retorna o valor da taxa de serviço.
     * Converte a string interna para um objeto {@link BigDecimal}.
     *
     * @return O valor da taxa, ou {@code null} se não estiver definido.
     */
    public BigDecimal getValor() { if (valorStr == null) return null; return new BigDecimal(valorStr.replace(",", ".")); }

    /**
     * Define o valor da taxa de serviço.
     * Converte o objeto {@link BigDecimal} para uma string antes de armazenar.
     *
     * @param valor O valor a ser definido.
     */
    public void setValor(BigDecimal valor) { if (valor != null) { this.valorStr = valor.toPlainString(); } else { this.valorStr = null; } }

    /**
     * Retorna a representação em String da data.
     *
     * @return A data como String.
     */
    public String getDataStr() { return dataStr; }

    /**
     * Define a data a partir de uma String.
     *
     * @param dataStr A data como String a ser definida.
     */
    public void setDataStr(String dataStr) { this.dataStr = dataStr; }

    /**
     * Retorna a representação em String do valor.
     *
     * @return O valor como String.
     */
    public String getValorStr() { return valorStr; }

    /**
     * Define o valor a partir de uma String.
     *
     * @param valorStr O valor como String a ser definido.
     */
    public void setValorStr(String valorStr) { this.valorStr = valorStr; }
}