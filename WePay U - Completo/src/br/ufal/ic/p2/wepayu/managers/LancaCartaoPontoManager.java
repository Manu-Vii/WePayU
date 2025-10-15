package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.ExceptionPonto.DataInvalidaException;
import br.ufal.ic.p2.wepayu.ExceptionPonto.DataInicialAposFinalException;
import br.ufal.ic.p2.wepayu.ExceptionPonto.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.ExceptionEmpregados.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.ExceptionPonto.HorasNaoNumericasException;
import br.ufal.ic.p2.wepayu.ExceptionPonto.HorasNaoPositivasException;
import br.ufal.ic.p2.wepayu.ExceptionPonto.IdentificacaoNulaException;
import br.ufal.ic.p2.wepayu.models.CartaoDePonto;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.utils.AppUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador responsável pelas operações relacionadas a cartões de ponto de empregados horistas,
 * incluindo o lançamento de novas horas e o cálculo de horas normais e extras.
 */
public class LancaCartaoPontoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Lança um novo cartão de ponto para um empregado horista.
     * O método valida os dados e, se forem corretos, adiciona o cartão ao empregado
     * e persiste a alteração.
     *
     * @param id    O ID do empregado.
     * @param data  A data do registro no formato "d/M/yyyy".
     * @param horas A quantidade de horas trabalhadas.
     * @throws IdentificacaoNulaException se o ID for nulo ou vazio.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws EmpregadoNaoEhHoristaException se o empregado não for do tipo horista.
     * @throws DataInvalidaException se o formato da data for inválido.
     * @throws HorasNaoNumericasException se o valor das horas não for numérico.
     * @throws HorasNaoPositivasException se o valor das horas for menor ou igual a zero.
     * @throws Exception para outras exceções genéricas.
     */
    public void lancaCartao(String id, String data, String horas) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new IdentificacaoNulaException();
        }

        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!(empregado instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate dataFormatada;
        try {
            dataFormatada = AppUtils.parseDate(data);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException();
        }

        BigDecimal horasBigDecimal;
        try {
            horasBigDecimal = AppUtils.parseBigDecimal(horas);
        } catch (NumberFormatException e) {
            throw new HorasNaoNumericasException();
        }

        if (horasBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HorasNaoPositivasException();
        }

        empregado.getCartoesPonto().add(new CartaoDePonto(dataFormatada, horasBigDecimal));
        empregadoRepository.salvarDados();
    }

    /**
     * Calcula o total de horas normais trabalhadas por um horista em um período.
     * Considera-se como hora normal um máximo de 8 horas por dia.
     *
     * @param id          O ID do empregado.
     * @param dataInicial A data de início do período.
     * @param dataFinal   A data de fim do período.
     * @return Uma string formatada com o total de horas normais (ex: "40" ou "35,5").
     * @throws EmpregadoNaoEhHoristaException se o empregado não for do tipo horista.
     * @throws DataInvalidaException se as datas estiverem em formato inválido.
     * @throws DataInicialAposFinalException se a data inicial for posterior à data final.
     * @throws Exception para outras exceções genéricas.
     */
    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado empregado = empregadoRepository.getById(id);
        if (!(empregado instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate dataInicio;
        try {
            dataInicio = AppUtils.parseDate(dataInicial);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("inicial");
        }

        LocalDate dataFim;
        try {
            dataFim = AppUtils.parseDate(dataFinal);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("final");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new DataInicialAposFinalException();
        }

        Map<LocalDate, BigDecimal> horasPorDia = new HashMap<>();
        for (CartaoDePonto cartao : empregado.getCartoesPonto()) {
            LocalDate dataCartao = cartao.getData();
            if (!dataCartao.isBefore(dataInicio) && dataCartao.isBefore(dataFim)) {
                horasPorDia.put(dataCartao, horasPorDia.getOrDefault(dataCartao, BigDecimal.ZERO).add(cartao.getHoras()));
            }
        }

        BigDecimal totalHorasNormais = BigDecimal.ZERO;
        for (BigDecimal horasDoDia : horasPorDia.values()) {
            if (horasDoDia.compareTo(new BigDecimal("8")) > 0) {
                totalHorasNormais = totalHorasNormais.add(new BigDecimal("8"));
            } else {
                totalHorasNormais = totalHorasNormais.add(horasDoDia);
            }
        }
        return formatarHoras(totalHorasNormais);
    }

    /**
     * Calcula o total de horas extras trabalhadas por um horista em um período.
     * Considera-se como hora extra qualquer hora trabalhada além da 8ª hora em um mesmo dia.
     *
     * @param id          O ID do empregado.
     * @param dataInicial A data de início do período.
     * @param dataFinal   A data de fim do período.
     * @return Uma string formatada com o total de horas extras (ex: "10" ou "5,5").
     * @throws EmpregadoNaoEhHoristaException se o empregado não for do tipo horista.
     * @throws DataInvalidaException se as datas estiverem em formato inválido.
     * @throws DataInicialAposFinalException se a data inicial for posterior à data final.
     * @throws Exception para outras exceções genéricas.
     */
    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado empregado = empregadoRepository.getById(id);
        if (!(empregado instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate dataInicio;
        try {
            dataInicio = AppUtils.parseDate(dataInicial);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("inicial");
        }

        LocalDate dataFim;
        try {
            dataFim = AppUtils.parseDate(dataFinal);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("final");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new DataInicialAposFinalException();
        }

        Map<LocalDate, BigDecimal> horasPorDia = new HashMap<>();
        for (CartaoDePonto cartao : empregado.getCartoesPonto()) {
            LocalDate dataCartao = cartao.getData();
            if (!dataCartao.isBefore(dataInicio) && dataCartao.isBefore(dataFim)) {
                horasPorDia.put(dataCartao, horasPorDia.getOrDefault(dataCartao, BigDecimal.ZERO).add(cartao.getHoras()));
            }
        }

        BigDecimal totalHorasExtras = BigDecimal.ZERO;
        for (BigDecimal horasDoDia : horasPorDia.values()) {
            if (horasDoDia.compareTo(new BigDecimal("8")) > 0) {
                totalHorasExtras = totalHorasExtras.add(horasDoDia.subtract(new BigDecimal("8")));
            }
        }
        return formatarHoras(totalHorasExtras);
    }

    /**
     * Formata um valor BigDecimal que representa horas em uma string.
     * A formatação remove zeros à direita e usa vírgula como separador decimal.
     *
     * @param valor O valor a ser formatado.
     * @return A string formatada. Retorna "0" se o valor for nulo.
     */
    private String formatarHoras(BigDecimal valor) {
        if (valor == null) {
            return "0";
        }

        return valor.stripTrailingZeros().toPlainString().replace(".", ",");
    }
}