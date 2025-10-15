package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo que representa um Cartão de Ponto lançado para um empregado horista.
 * A classe armazena a data e a quantidade de horas trabalhadas nesse dia.
 * <p>
 * Nota de implementação: A data e as horas são armazenadas internamente como Strings
 * para simplificar a serialização. Os métodos getters e setters fazem a conversão
 * de/para os tipos {@link LocalDate} e {@link BigDecimal}.
 * </p>
 * A classe é {@link Serializable} para permitir a sua persistência.
 */
public class CartaoDePonto implements Serializable {
    private String dataStr;
    private String horasStr;

    /**
     * Construtor padrão (sem argumentos).
     * Necessário para frameworks de serialização.
     */
    public CartaoDePonto() {}

    /**
     * Construtor que inicializa um cartão de ponto com data e horas específicas.
     *
     * @param data  A data do registro de ponto.
     * @param horas A quantidade de horas trabalhadas.
     */
    public CartaoDePonto(LocalDate data, BigDecimal horas) {
        setData(data);
        setHoras(horas);
    }

    /**
     * Cria e retorna uma cópia (clone) deste objeto CartaoDePonto.
     *
     * @return Uma nova instância de {@link CartaoDePonto} com os mesmos valores de data e horas.
     */
    @Override
    public CartaoDePonto clone() {
        return new CartaoDePonto(this.getData(), this.getHoras());
    }

    /**
     * Retorna a data do cartão de ponto.
     * Converte a string interna para um objeto {@link LocalDate}.
     *
     * @return A data do registro, ou {@code null} se não estiver definida.
     */
    public LocalDate getData() { if (dataStr == null) return null; return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/uuuu")); }

    /**
     * Define a data do cartão de ponto.
     * Formata o objeto {@link LocalDate} para uma string antes de armazenar.
     *
     * @param data A data a ser definida.
     */
    public void setData(LocalDate data) { if (data != null) { this.dataStr = data.format(DateTimeFormatter.ofPattern("d/M/uuuu")); } else { this.dataStr = null; } }

    /**
     * Retorna a quantidade de horas trabalhadas.
     * Converte a string interna para um objeto {@link BigDecimal}.
     *
     * @return A quantidade de horas, ou {@code null} se não estiver definida.
     */
    public BigDecimal getHoras() { if (horasStr == null) return null; return new BigDecimal(horasStr.replace(",", ".")); }

    /**
     * Define a quantidade de horas trabalhadas.
     * Converte o objeto {@link BigDecimal} para uma string antes de armazenar.
     *
     * @param horas A quantidade de horas a ser definida.
     */
    public void setHoras(BigDecimal horas) { if (horas != null) { this.horasStr = horas.toPlainString(); } else { this.horasStr = null; } }

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
     * Retorna a representação em String das horas.
     *
     * @return As horas como String.
     */
    public String getHorasStr() { return horasStr; }

    /**
     * Define as horas a partir de uma String.
     *
     * @param horasStr As horas como String a serem definidas.
     */
    public void setHorasStr(String horasStr) { this.horasStr = horasStr; }
}