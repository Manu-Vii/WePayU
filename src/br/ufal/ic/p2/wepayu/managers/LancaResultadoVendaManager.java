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
 * Classe de gerenciamento para operações relacionadas a resultados de vendas de empregados comissionados.
 * Permite o lançamento de vendas e a consulta de vendas realizadas em um período.
 */
public class LancaResultadoVendaManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Lança um resultado de venda para um empregado comissionado.
     *
     * @param id O identificador do empregado.
     * @param data A data da venda no formato "d/M/yyyy".
     * @param valor O valor da venda.
     * @throws IdentificacaoNulaException se o ID do empregado for nulo ou vazio.
     * @throws EmpregadoNaoExisteException se nenhum empregado for encontrado com o ID fornecido.
     * @throws EmpregadoNaoEhComissionadoException se o empregado encontrado não for do tipo comissionado.
     * @throws DataInvalidaException se a data fornecida for inválida.
     * @throws ValorNaoNumericoException se o valor da venda não for um número válido.
     * @throws ValorNaoPositivoException se o valor da venda for menor ou igual a zero.
     * @throws Exception para outros erros inesperados.
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
     * Calcula o valor total das vendas realizadas por um empregado comissionado em um determinado período.
     *
     * @param id O identificador do empregado.
     * @param dataInicial A data de início do período (inclusiva).
     * @param dataFinal A data de fim do período (exclusiva).
     * @return Uma string formatada representando o valor total das vendas no período.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws EmpregadoNaoEhComissionadoException se o empregado não for comissionado.
     * @throws DataInvalidaException se uma das datas for inválida.
     * @throws Exception se a data inicial for posterior à data final ou para outros erros.
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
     * Formata um valor BigDecimal para uma string com duas casas decimais e vírgula como separador.
     *
     * @param valor O valor BigDecimal a ser formatado.
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