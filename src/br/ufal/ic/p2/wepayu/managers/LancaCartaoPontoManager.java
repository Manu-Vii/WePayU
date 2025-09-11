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
 * Classe de gerenciamento para operações relacionadas a cartões de ponto de empregados horistas.
 * Permite o lançamento de cartões e a consulta de horas normais e extras trabalhadas.
 */
public class LancaCartaoPontoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Lança um cartão de ponto para um empregado horista.
     *
     * @param id O identificador do empregado.
     * @param data A data em que as horas foram trabalhadas, no formato "d/M/yyyy".
     * @param horas A quantidade de horas trabalhadas.
     * @throws IdentificacaoNulaException se o ID do empregado for nulo ou vazio.
     * @throws EmpregadoNaoExisteException se nenhum empregado for encontrado com o ID fornecido.
     * @throws EmpregadoNaoEhHoristaException se o empregado encontrado não for do tipo horista.
     * @throws DataInvalidaException se a data fornecida for inválida.
     * @throws HorasNaoNumericasException se o valor das horas não for um número válido.
     * @throws HorasNaoPositivasException se o valor das horas for menor ou igual a zero.
     * @throws Exception para outros erros inesperados.
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
     * Calcula o total de horas normais trabalhadas por um empregado horista em um determinado período.
     * Horas normais são limitadas a 8 horas por dia.
     *
     * @param id O identificador do empregado.
     * @param dataInicial A data de início do período (inclusiva).
     * @param dataFinal A data de fim do período (exclusiva).
     * @return Uma string formatada representando o total de horas normais trabalhadas.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista.
     * @throws DataInvalidaException se uma das datas for inválida.
     * @throws DataInicialAposFinalException se a data inicial for posterior à data final.
     * @throws Exception para outros erros inesperados.
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
     * Calcula o total de horas extras trabalhadas por um empregado horista em um determinado período.
     * Horas extras são todas as horas que excedem 8 horas em um único dia.
     *
     * @param id O identificador do empregado.
     * @param dataInicial A data de início do período (inclusiva).
     * @param dataFinal A data de fim do período (exclusiva).
     * @return Uma string formatada representando o total de horas extras trabalhadas.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws EmpregadoNaoEhHoristaException se o empregado não for horista.
     * @throws DataInvalidaException se uma das datas for inválida.
     * @throws DataInicialAposFinalException se a data inicial for posterior à data final.
     * @throws Exception para outros erros inesperados.
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
     * Formata um valor BigDecimal de horas em uma string.
     * Substitui o ponto decimal por vírgula e remove zeros à direita.
     *
     * @param valor O valor BigDecimal a ser formatado.
     * @return A string formatada. Se o valor for nulo, retorna "0".
     */
    private String formatarHoras(BigDecimal valor) {
        if (valor == null) {
            return "0";
        }
        return valor.stripTrailingZeros().toPlainString().replace(".", ",");
    }
}