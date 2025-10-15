package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.ExceptionEmpregados.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.ExceptionVendas.DataInvalidaException;
import br.ufal.ic.p2.wepayu.ExceptionVendas.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.ExceptionVendas.IdentificacaoNulaException;
import br.ufal.ic.p2.wepayu.ExceptionVendas.ValorNaoNumericoException;
import br.ufal.ic.p2.wepayu.ExceptionVendas.ValorNaoPositivoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.ResultadoVenda;
import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Gerenciador responsável pelas operações de lançamento e consulta de resultados de vendas
 * para empregados comissionados.
 */
public class LancaResultadoVendaManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Lança um novo resultado de venda para um empregado comissionado.
     * O método valida todos os parâmetros e, se corretos, adiciona a venda ao empregado
     * e persiste os dados.
     *
     * @param id    O ID do empregado que realizou a venda.
     * @param data  A data da venda no formato "d/M/yyyy".
     * @param valor O valor da venda como uma string (ex: "150,00").
     * @throws IdentificacaoNulaException se o ID do empregado for nulo ou vazio.
     * @throws EmpregadoNaoExisteException se nenhum empregado com o ID fornecido for encontrado.
     * @throws EmpregadoNaoEhComissionadoException se o empregado não for do tipo comissionado.
     * @throws DataInvalidaException se a data estiver em um formato inválido.
     * @throws ValorNaoNumericoException se o valor não for um número válido.
     * @throws ValorNaoPositivoException se o valor for menor ou igual a zero.
     * @throws Exception para outras exceções genéricas.
     */
    public void lancaVenda(String id, String data, String valor) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new IdentificacaoNulaException();
        }

        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!(empregado instanceof EmpregadoComissionado)) {
            throw new EmpregadoNaoEhComissionadoException();
        }

        LocalDate dataFormatada;
        try {
            dataFormatada = LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException();
        }

        BigDecimal valorBigDecimal;
        try {
            valorBigDecimal = new BigDecimal(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValorNaoNumericoException();
        }

        if (valorBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorNaoPositivoException();
        }

        ((EmpregadoComissionado) empregado).getResultadosVendas().add(new ResultadoVenda(dataFormatada, valorBigDecimal));
        empregadoRepository.salvarDados();
    }

    /**
     * Calcula o valor total de vendas realizadas por um empregado comissionado em um determinado período.
     *
     * @param id          O ID do empregado.
     * @param dataInicial A data de início do período no formato "d/M/yyyy".
     * @param dataFinal   A data de fim do período no formato "d/M/yyyy".
     * @return Uma string formatada representando o somatório das vendas no período (ex: "123,45").
     * @throws EmpregadoNaoEhComissionadoException se o empregado com o ID fornecido não for comissionado.
     * @throws DataInvalidaException se a data inicial ou final for inválida.
     * @throws Exception se a data inicial for posterior à data final.
     */
    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado empregado = empregadoRepository.getById(id);
        if (!(empregado instanceof EmpregadoComissionado)) {
            throw new EmpregadoNaoEhComissionadoException();
        }

        LocalDate dataInicio;
        try {
            dataInicio = LocalDate.parse(dataInicial, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("inicial");
        }
        LocalDate dataFim;
        try {
            dataFim = LocalDate.parse(dataFinal, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException("final");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        BigDecimal totalVendas = BigDecimal.ZERO;
        for (ResultadoVenda venda : ((EmpregadoComissionado) empregado).getResultadosVendas()) {
            if (!venda.getData().isBefore(dataInicio) && venda.getData().isBefore(dataFim)) {
                totalVendas = totalVendas.add(venda.getValor());
            }
        }

        return formatarValor(totalVendas);
    }

    /**
     * Formata um valor {@link BigDecimal} para uma string com duas casas decimais,
     * usando vírgula como separador decimal e sem agrupamento de milhares.
     *
     * @param valor O valor a ser formatado.
     * @return A string formatada. Retorna "0,00" se o valor for nulo.
     */
    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "0,00";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        df.setGroupingUsed(false);
        return df.format(valor);
    }
}