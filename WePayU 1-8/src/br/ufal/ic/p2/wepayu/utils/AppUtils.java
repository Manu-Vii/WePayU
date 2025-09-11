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
 * Classe utilitária para formatação e análise de dados (BigDecimal e LocalDate).
 */
public class AppUtils {

    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(new Locale("pt", "BR"));
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,##0.00", SYMBOLS);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    static {
        DECIMAL_FORMATTER.setGroupingUsed(false);
    }

    /**
     * Formata um BigDecimal para uma String com duas casas decimais e separador de milhar.
     * @param value O valor BigDecimal a ser formatado.
     * @return A String formatada.
     */
    public static String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "0,00";
        }
        return DECIMAL_FORMATTER.format(value.setScale(2, RoundingMode.HALF_EVEN));
    }

    /**
     * Analisa uma String e a converte em um BigDecimal.
     * Substitui a vírgula por ponto para a conversão.
     * @param value A String a ser analisada.
     * @return O valor BigDecimal.
     */
    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new BigDecimal(value.replace(",", "."));
    }

    /**
     * Analisa uma String de data e a converte num objeto LocalDate.
     * @param dateStr A String da data (formato d/M/uuuu).
     * @return O objeto LocalDate.
     * @throws DateTimeParseException se a data for inválida.
     */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Formata um objeto LocalDate em uma String.
     * @param date O objeto LocalDate.
     * @return A String da data formatada.
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formata um valor BigDecimal como moeda.
     * @param value O valor BigDecimal.
     * @return A String formatada como moeda.
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