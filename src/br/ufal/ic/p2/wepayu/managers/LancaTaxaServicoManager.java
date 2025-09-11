package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.ExceptionEmpregados.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.ExceptionServico.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.TaxaDeServico;
import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.utils.AppUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Classe de gerenciamento para operações relacionadas a taxas de serviço de membros de sindicatos.
 * Permite o lançamento de taxas de serviço e a consulta de taxas acumuladas em um período.
 */
public class LancaTaxaServicoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Encontra um empregado no repositório com base no seu ID de sindicato.
     *
     * @param idSindicato O ID de sindicato a ser procurado.
     * @return O objeto {@link Empregado} correspondente ao ID, ou {@code null} se não for encontrado.
     */
    private Empregado encontrarPorIdSindicato(String idSindicato) {
        for (Empregado empregado : empregadoRepository.getAll().values()) {
            if (Objects.equals(empregado.getIdSindicato(), idSindicato)) {
                return empregado;
            }
        }
        return null;
    }

    /**
     * Lança uma taxa de serviço para um membro do sindicato, identificado pelo seu ID de sindicato.
     *
     * @param idSindicato O identificador do membro no sindicato.
     * @param data A data em que a taxa foi aplicada, no formato "d/M/yyyy".
     * @param valor O valor da taxa de serviço.
     * @throws IdentificacaoNulaException se o ID do sindicato for nulo ou vazio.
     * @throws MembroNaoExisteException se nenhum membro for encontrado com o ID de sindicato fornecido.
     * @throws EmpregadoNaoEhSindicalizadoException se o empregado encontrado não for sindicalizado.
     * @throws DataInvalidaException se a data fornecida for inválida.
     * @throws ValorNaoNumericoException se o valor da taxa não for um número válido.
     * @throws ValorNaoPositivoException se o valor da taxa for menor ou igual a zero.
     * @throws Exception para outros erros inesperados.
     */
    public void lancaTaxaServico(String idSindicato, String data, String valor) throws Exception {
        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new IdentificacaoNulaException();
        }

        Empregado empregado = encontrarPorIdSindicato(idSindicato);
        if (empregado == null) {
            throw new MembroNaoExisteException();
        }

        if (!empregado.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }

        LocalDate dataFormatada;
        try {
            dataFormatada = AppUtils.parseDate(data);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException();
        }

        BigDecimal valorBigDecimal;
        try {
            valorBigDecimal = AppUtils.parseBigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new ValorNaoNumericoException();
        }

        if (valorBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorNaoPositivoException();
        }

        empregado.getTaxasDeServico().add(new TaxaDeServico(dataFormatada, valorBigDecimal));
        empregadoRepository.salvarDados();
    }

    /**
     * Calcula o valor total das taxas de serviço de um empregado sindicalizado em um determinado período.
     *
     * @param id O identificador do empregado (ID geral, não o de sindicato).
     * @param dataInicial A data de início do período (inclusiva).
     * @param dataFinal A data de fim do período (exclusiva).
     * @return Uma string formatada representando o valor total das taxas de serviço no período.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws EmpregadoNaoEhSindicalizadoException se o empregado não for sindicalizado.
     * @throws DataInvalidaException se uma das datas for inválida.
     * @throws Exception se a data inicial for posterior à data final ou para outros erros.
     */
    public String getTaxasServico(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        if (!empregado.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizadoException();
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
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        BigDecimal totalTaxas = BigDecimal.ZERO;
        for (TaxaDeServico taxa : empregado.getTaxasDeServico()) {
            if (!taxa.getData().isBefore(dataInicio) && taxa.getData().isBefore(dataFim)) {
                totalTaxas = totalTaxas.add(taxa.getValor());
            }
        }

        return AppUtils.formatBigDecimal(totalTaxas);
    }
}