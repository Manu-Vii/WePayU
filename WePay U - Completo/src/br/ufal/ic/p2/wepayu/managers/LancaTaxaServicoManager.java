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
 * Gerenciador responsável pelas operações relacionadas ao lançamento e consulta de taxas de serviço
 * para empregados sindicalizados.
 */
public class LancaTaxaServicoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Método auxiliar para encontrar um empregado com base em sua identificação no sindicato.
     *
     * @param idSindicato O ID do membro do sindicato.
     * @return O objeto {@link Empregado} correspondente, ou {@code null} se nenhum for encontrado.
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
     * Lança uma nova taxa de serviço para um membro do sindicato.
     * Realiza validações nos dados de entrada e, se válidos, adiciona a taxa ao empregado
     * e persiste a alteração.
     *
     * @param idSindicato O ID do membro do sindicato a quem a taxa se aplica.
     * @param data        A data em que a taxa foi cobrada, no formato "d/M/yyyy".
     * @param valor       O valor da taxa, como uma string (ex: "10,50").
     * @throws IdentificacaoNulaException se o ID do sindicato for nulo ou vazio.
     * @throws MembroNaoExisteException se nenhum membro com o ID fornecido for encontrado.
     * @throws EmpregadoNaoEhSindicalizadoException se o empregado encontrado não for sindicalizado.
     * @throws DataInvalidaException se a data estiver em um formato inválido.
     * @throws ValorNaoNumericoException se o valor não for um número válido.
     * @throws ValorNaoPositivoException se o valor for menor ou igual a zero.
     * @throws Exception para outras exceções genéricas.
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
     * Calcula o total de taxas de serviço de um empregado dentro de um intervalo de datas.
     *
     * @param id          O ID do empregado (não o ID do sindicato).
     * @param dataInicial A data de início do período, no formato "d/M/yyyy".
     * @param dataFinal   A data de fim do período, no formato "d/M/yyyy".
     * @return Uma string formatada representando o somatório das taxas no período.
     * @throws EmpregadoNaoExisteException se o empregado com o ID fornecido não existir.
     * @throws EmpregadoNaoEhSindicalizadoException se o empregado não for membro do sindicato.
     * @throws DataInvalidaException se a data inicial ou final for inválida.
     * @throws Exception se a data inicial for posterior à data final.
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