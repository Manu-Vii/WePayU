package br.ufal.ic.p2.wepayu.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Classe utilitária com métodos estáticos para operações comuns na aplicação.
 * Fornece funcionalidades para formatação e parsing de valores numéricos (BigDecimal)
 * e datas (LocalDate), seguindo as convenções do locale pt-BR.
 */
public class AppUtils {

    /**
     * Símbolos de formatação decimal para o locale Português (Brasil),
     * garantindo o uso da vírgula como separador decimal.
     */
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(new Locale("pt", "BR"));

    /**
     * Formatador decimal padrão para a aplicação, configurado para duas casas decimais
     * e sem agrupamento de milhares. Ex: 1234.56 -> "1234,56".
     */
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,##0.00", SYMBOLS);

    /**
     * Formatador de data estrito para o padrão "dia/mês/ano" (d/M/uuuu).
     * O {@link ResolverStyle#STRICT} garante que datas inválidas (ex: 30/02/2024) não sejam aceitas.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Bloco de inicialização estático para configurar o formatador decimal.
     * Desativa o uso de separadores de agrupamento (milhares).
     */
    static {
        DECIMAL_FORMATTER.setGroupingUsed(false);
    }



    /**
     * Formata um valor {@link BigDecimal} para uma String com duas casas decimais e vírgula como separador.
     *
     * @param value O valor BigDecimal a ser formatado.
     * @return A representação em String do valor (ex: "1500,50"). Retorna "0,00" se o valor for nulo.
     */
    public static String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "0,00";
        }
        // Utiliza HALF_EVEN para arredondamento bancário, que é o padrão em muitas operações financeiras.
        return DECIMAL_FORMATTER.format(value.setScale(2, RoundingMode.HALF_EVEN));
    }

    /**
     * Converte uma String (potencialmente com vírgula decimal) em um {@link BigDecimal}.
     *
     * @param value A String a ser convertida.
     * @return O objeto {@link BigDecimal} correspondente. Retorna {@code null} se a String for nula ou vazia.
     */
    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Substitui a vírgula pelo ponto para que o construtor do BigDecimal possa interpretar corretamente.
        return new BigDecimal(value.replace(",", "."));
    }

    /**
     * Converte uma String no formato "d/M/yyyy" para um objeto {@link LocalDate}.
     *
     * @param dateStr A String da data a ser convertida.
     * @return O objeto {@link LocalDate} correspondente.
     * @throws DateTimeParseException se a String não estiver no formato válido e estrito.
     */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Formata um objeto {@link LocalDate} para uma String no formato "d/M/yyyy".
     *
     * @param date O objeto {@link LocalDate} a ser formatado.
     * @return A representação em String da data. Retorna {@code null} se a data for nula.
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formata um valor {@link BigDecimal} como uma string de moeda no padrão brasileiro (pt-BR).
     * Utiliza separador de milhares e vírgula para decimais.
     *
     * @param value O valor BigDecimal a ser formatado.
     * @return A String formatada como moeda (ex: "1.234,56"). Retorna "0,00" se o valor for nulo.
     */
    public static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "0,00";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);
    }
}