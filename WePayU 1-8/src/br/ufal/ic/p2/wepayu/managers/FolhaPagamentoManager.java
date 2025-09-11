package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.utils.AppUtils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Classe responsável por gerenciar as operações da folha de pagamento,
 * incluindo o cálculo do total da folha e a geração de relatórios de pagamento.
 */
public class FolhaPagamentoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Calcula o valor total da folha de pagamento para uma data específica.
     * Soma o salário bruto de todos os empregados que devem ser pagos na data informada.
     *
     * @param data A data para a qual a folha de pagamento deve ser calculada, no formato "d/M/yyyy".
     * @return Um {@link BigDecimal} representando o valor total da folha, arredondado para 2 casas decimais.
     * @throws Exception se a data fornecida for inválida.
     */
    public BigDecimal totalFolha(String data) throws Exception {
        LocalDate dataAtual = AppUtils.parseDate(data);
        BigDecimal total = BigDecimal.ZERO;

        for (Empregado emp : empregadoRepository.getAll().values()) {
            if (deveSerPago(emp, dataAtual)) {
                total = total.add(calcularPagamento(emp, dataAtual));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Executa o processo de folha de pagamento para uma data específica e gera um arquivo de saída
     * com os detalhes do pagamento. Também atualiza a última data de pagamento dos empregados pagos.
     *
     * @param data A data de processamento da folha, no formato "d/M/yyyy".
     * @param saida O caminho do arquivo onde o relatório da folha de pagamento será salvo.
     * @throws Exception se a data for inválida ou ocorrer um erro de I/O ao escrever o arquivo.
     */
    public void rodaFolha(String data, String saida) throws Exception {
        LocalDate dataAtual = AppUtils.parseDate(data);
        List<Empregado> empregadosParaPagar = empregadoRepository.getAll().values().stream()
                .filter(emp -> deveSerPago(emp, dataAtual))
                .sorted(Comparator.comparing(Empregado::getNome))
                .collect(Collectors.toList());

        try (PrintWriter writer = new PrintWriter(new FileWriter(saida))) {
            writer.println("FOLHA DE PAGAMENTO DO DIA " + dataAtual);
            writer.println("====================================");
            writer.println();

            BigDecimal totalGeral = BigDecimal.ZERO;
            totalGeral = totalGeral.add(gerarRelatorio(writer, empregadosParaPagar, "horista", dataAtual));
            totalGeral = totalGeral.add(gerarRelatorio(writer, empregadosParaPagar, "assalariado", dataAtual));
            totalGeral = totalGeral.add(gerarRelatorio(writer, empregadosParaPagar, "comissionado", dataAtual));

            writer.printf(Locale.FRANCE, "TOTAL FOLHA: %.2f\n", totalGeral);
        }

        for (Empregado emp : empregadosParaPagar) {
            PagamentoInfo info = calcularPagamentoCompleto(emp, dataAtual);
            if (info.salarioLiquido.compareTo(BigDecimal.ZERO) > 0) {
                emp.setUltimaDataPagamento(dataAtual);
            }
        }
    }

    /**
     * Gera uma seção do relatório da folha de pagamento para um tipo específico de empregado.
     *
     * @param writer O {@link PrintWriter} para escrever o relatório.
     * @param empregados A lista de empregados a serem pagos.
     * @param tipo O tipo de empregado ("horista", "assalariado", "comissionado").
     * @param dataAtual A data atual da folha de pagamento.
     * @return O total bruto pago para o tipo de empregado especificado.
     */
    private BigDecimal gerarRelatorio(PrintWriter writer, List<Empregado> empregados, String tipo, LocalDate dataAtual) {
        BigDecimal totalBruto = BigDecimal.ZERO, totalDescontos = BigDecimal.ZERO, totalLiquido = BigDecimal.ZERO;
        BigDecimal totalHorasNormais = BigDecimal.ZERO, totalHorasExtras = BigDecimal.ZERO;
        BigDecimal totalFixo = BigDecimal.ZERO, totalVendas = BigDecimal.ZERO, totalComissao = BigDecimal.ZERO;

        List<Empregado> filtrados = empregados.stream().filter(e -> e.getTipo().equals(tipo)).collect(Collectors.toList());

        if (tipo.equals("horista")) {
            writer.println("===============================================================================================================================");
            writer.println("===================== HORISTAS ================================================================================================");
            writer.println("===============================================================================================================================");
            writer.printf("%-36s %5s %5s %13s %9s %15s %s\n", "Nome", "Horas", "Extra", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
            writer.println("==================================== ===== ===== ============= ========= =============== ======================================");
        } else if (tipo.equals("assalariado")) {
            writer.println("===============================================================================================================================");
            writer.println("===================== ASSALARIADOS ============================================================================================");
            writer.println("===============================================================================================================================");
            writer.printf("%-48s %13s %9s %15s %s\n", "Nome", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
            writer.println("================================================ ============= ========= =============== ======================================");
        } else {
            writer.println("===============================================================================================================================");
            writer.println("===================== COMISSIONADOS ===========================================================================================");
            writer.println("===============================================================================================================================");
            writer.printf("%-17s %8s %10s %10s %13s %9s %15s %s\n", "Nome", "Fixo", "Vendas", "Comissao", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
            writer.println("===================== ======== ======== ======== ============= ========= =============== ======================================");
        }

        for (Empregado emp : filtrados) {
            PagamentoInfo info = calcularPagamentoCompleto(emp, dataAtual);
            writer.print(formatarLinhaRelatorio(emp, info));

            totalBruto = totalBruto.add(info.salarioBruto);
            totalDescontos = totalDescontos.add(info.descontos);
            totalLiquido = totalLiquido.add(info.salarioLiquido);

            if (emp instanceof EmpregadoHorista) {
                totalHorasNormais = totalHorasNormais.add(info.horasNormais);
                totalHorasExtras = totalHorasExtras.add(info.horasExtras);
            } else if (emp instanceof EmpregadoComissionado) {
                totalFixo = totalFixo.add(info.salarioFixo);
                totalVendas = totalVendas.add(info.vendas);
                totalComissao = totalComissao.add(info.comissao);
            }
        }
        writer.println();

        if (tipo.equals("horista")) {
            writer.printf(Locale.FRANCE, "TOTAL HORISTAS %27.0f %5.0f %13.2f %9.2f %15.2f\n", totalHorasNormais, totalHorasExtras, totalBruto, totalDescontos, totalLiquido);
        } else if (tipo.equals("assalariado")) {
            writer.printf(Locale.FRANCE, "TOTAL ASSALARIADOS %43.2f %9.2f %15.2f\n", totalBruto, totalDescontos, totalLiquido);
        } else {
            writer.printf(Locale.FRANCE, "TOTAL COMISSIONADOS %10.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n", totalFixo, totalVendas, totalComissao, totalBruto, totalDescontos, totalLiquido);
        }
        writer.println();
        return totalBruto;
    }

    /**
     * Obtém uma representação em string do método de pagamento de um empregado.
     *
     * @param emp O empregado.
     * @return Uma string descrevendo o método de pagamento.
     */
    private String getMetodoPagamentoString(Empregado emp) {
        MetodoPagamento metodo = emp.getMetodoPagamento();
        if (metodo instanceof EmMaos) return "Em maos";
        if (metodo instanceof Correios) return "Correios, " + emp.getEndereco();
        if (metodo instanceof Banco banco) return banco.getBanco() + ", Ag. " + banco.getAgencia() + " CC " + banco.getContaCorrente();
        return "";
    }

    /**
     * Verifica se um empregado deve ser pago em uma determinada data, com base em seu
     * tipo e agenda de pagamento.
     *
     * @param emp O empregado a ser verificado.
     * @param data A data para a verificação.
     * @return {@code true} se o empregado deve ser pago, {@code false} caso contrário.
     */
    private boolean deveSerPago(Empregado emp, LocalDate data) {
        LocalDate dataContratacao = emp.getDataContratacao();
        if (dataContratacao != null && data.isBefore(dataContratacao)) return false;

        String tipo = emp.getTipo();
        if (tipo.equals("horista")) {
            return data.getDayOfWeek() == DayOfWeek.FRIDAY;
        } else if (tipo.equals("assalariado")) {
            LocalDate ultimoDiaUtil = data.with(TemporalAdjusters.lastDayOfMonth());
            while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
                ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
            }
            return data.equals(ultimoDiaUtil);
        } else if (tipo.equals("comissionado")) {
            if (data.getDayOfWeek() != DayOfWeek.FRIDAY) return false;
            if (dataContratacao == null) return false;
            LocalDate primeiroPagamento = dataContratacao.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            if (data.isBefore(primeiroPagamento)) return false;
            long daysBetween = ChronoUnit.DAYS.between(primeiroPagamento, data);
            return daysBetween % 14 == 0;
        }
        return false;
    }

    /**
     * Calcula o salário bruto de um empregado para uma data específica.
     *
     * @param emp O empregado.
     * @param data A data do pagamento.
     * @return O valor do salário bruto como {@link BigDecimal}.
     */
    private BigDecimal calcularPagamento(Empregado emp, LocalDate data) {
        return calcularPagamentoCompleto(emp, data).salarioBruto;
    }

    /**
     * Direciona para o método de cálculo de pagamento apropriado com base no tipo do empregado.
     *
     * @param emp O empregado para o qual o pagamento será calculado.
     * @param data A data do pagamento.
     * @return Um objeto {@link PagamentoInfo} contendo todos os detalhes do pagamento.
     */
    private PagamentoInfo calcularPagamentoCompleto(Empregado emp, LocalDate data) {
        return switch (emp.getTipo()) {
            case "horista" -> calcularPagamentoHoristaCompleto((EmpregadoHorista) emp, data);
            case "assalariado" -> calcularPagamentoAssalariadoCompleto((EmpregadoAssalariado) emp, data);
            case "comissionado" -> calcularPagamentoComissionadoCompleto((EmpregadoComissionado) emp, data);
            default -> new PagamentoInfo();
        };
    }

    /**
     * Calcula os detalhes completos do pagamento para um empregado horista.
     *
     * @param horista O empregado horista.
     * @param data A data do pagamento.
     * @return Um objeto {@link PagamentoInfo} com os detalhes do pagamento.
     */
    private PagamentoInfo calcularPagamentoHoristaCompleto(EmpregadoHorista horista, LocalDate data) {
        PagamentoInfo info = new PagamentoInfo();
        LocalDate inicioPeriodo = horista.getUltimaDataPagamento() != null ? horista.getUltimaDataPagamento().plusDays(1) : horista.getDataContratacao();
        if (inicioPeriodo == null) inicioPeriodo = LocalDate.of(2004, 12, 31);

        for (CartaoDePonto cartao : horista.getCartoesPonto()) {
            if (!cartao.getData().isBefore(inicioPeriodo) && !cartao.getData().isAfter(data)) {
                if (cartao.getHoras().compareTo(new BigDecimal("8")) > 0) {
                    info.horasNormais = info.horasNormais.add(new BigDecimal("8"));
                    info.horasExtras = info.horasExtras.add(cartao.getHoras().subtract(new BigDecimal("8")));
                } else {
                    info.horasNormais = info.horasNormais.add(cartao.getHoras());
                }
            }
        }
        BigDecimal salarioHora = horista.getSalario();
        info.salarioBruto = info.horasNormais.multiply(salarioHora).add(info.horasExtras.multiply(salarioHora.multiply(new BigDecimal("1.5"))));

        if (info.salarioBruto.compareTo(BigDecimal.ZERO) > 0) {
            info.descontos = calcularDescontosSindicais(horista, inicioPeriodo, data);
        } else {
            info.descontos = BigDecimal.ZERO;
        }

        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;
        return info;
    }

    /**
     * Calcula os detalhes completos do pagamento para um empregado assalariado.
     *
     * @param assalariado O empregado assalariado.
     * @param data A data do pagamento.
     * @return Um objeto {@link PagamentoInfo} com os detalhes do pagamento.
     */
    private PagamentoInfo calcularPagamentoAssalariadoCompleto(EmpregadoAssalariado assalariado, LocalDate data) {
        PagamentoInfo info = new PagamentoInfo();
        info.salarioBruto = assalariado.getSalario();
        LocalDate inicioPeriodo = assalariado.getUltimaDataPagamento() == null ? assalariado.getDataContratacao() : assalariado.getUltimaDataPagamento().plusDays(1);
        info.descontos = calcularDescontosSindicais(assalariado, inicioPeriodo, data);
        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;
        return info;
    }

    /**
     * Calcula os detalhes completos do pagamento para um empregado comissionado.
     *
     * @param comissionado O empregado comissionado.
     * @param data A data do pagamento.
     * @return Um objeto {@link PagamentoInfo} com os detalhes do pagamento.
     */
    private PagamentoInfo calcularPagamentoComissionadoCompleto(EmpregadoComissionado comissionado, LocalDate data) {
        PagamentoInfo info = new PagamentoInfo();
        LocalDate inicioPeriodo = comissionado.getUltimaDataPagamento() == null ? comissionado.getDataContratacao() : comissionado.getUltimaDataPagamento().plusDays(1);
        info.salarioFixo = comissionado.getSalario().multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.FLOOR);
        for (ResultadoVenda venda : comissionado.getResultadosVendas()) {
            if (!venda.getData().isBefore(inicioPeriodo) && !venda.getData().isAfter(data)) {
                info.vendas = info.vendas.add(venda.getValor());
            }
        }
        info.comissao = info.vendas.multiply(comissionado.getComissao()).setScale(2, RoundingMode.FLOOR);
        info.salarioBruto = info.salarioFixo.add(info.comissao);
        info.descontos = calcularDescontosSindicais(comissionado, inicioPeriodo, data);
        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;
        return info;
    }

    /**
     * Calcula os descontos sindicais totais (taxa sindical e taxas de serviço) para um
     * empregado dentro de um período.
     *
     * @param emp O empregado.
     * @param inicio A data de início do período.
     * @param fim A data de fim do período.
     * @return O valor total dos descontos como {@link BigDecimal}.
     */
    private BigDecimal calcularDescontosSindicais(Empregado emp, LocalDate inicio, LocalDate fim) {
        BigDecimal descontos = BigDecimal.ZERO;
        if (emp.isSindicalizado()) {
            if(inicio == null) return BigDecimal.ZERO;
            long dias = ChronoUnit.DAYS.between(inicio, fim) + 1;
            if (emp.getTaxaSindical() != null) {
                if (emp instanceof EmpregadoAssalariado) {
                    descontos = descontos.add(emp.getTaxaSindical().multiply(new BigDecimal(fim.lengthOfMonth())));
                } else {
                    descontos = descontos.add(emp.getTaxaSindical().multiply(new BigDecimal(dias)));
                }
            }
            for (TaxaDeServico taxa : emp.getTaxasDeServico()) {
                if (!taxa.getData().isBefore(inicio) && !taxa.getData().isAfter(fim)) {
                    descontos = descontos.add(taxa.getValor());
                }
            }
        }
        return descontos;
    }

    /**
     * Formata uma única linha do relatório da folha de pagamento para um empregado.
     *
     * @param emp O empregado.
     * @param info O objeto {@link PagamentoInfo} com os detalhes do pagamento.
     * @return Uma string formatada representando a linha do relatório.
     */
    private String formatarLinhaRelatorio(Empregado emp, PagamentoInfo info) {
        String metodo = getMetodoPagamentoString(emp);
        if (emp instanceof EmpregadoHorista) {
            return String.format(Locale.FRANCE, "%-36s %5.0f %5.0f %13.2f %9.2f %15.2f %s\n", emp.getNome(), info.horasNormais, info.horasExtras, info.salarioBruto, info.descontos, info.salarioLiquido, metodo);
        } else if (emp instanceof EmpregadoComissionado) {
            return String.format(Locale.FRANCE, "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s\n", emp.getNome(), info.salarioFixo, info.vendas, info.comissao, info.salarioBruto, info.descontos, info.salarioLiquido, metodo);
        } else {
            return String.format(Locale.FRANCE, "%-48s %13.2f %9.2f %15.2f %s\n", emp.getNome(), info.salarioBruto, info.descontos, info.salarioLiquido, metodo);
        }
    }

    /**
     * Classe interna privada para armazenar os dados calculados de um pagamento.
     * Serve como uma estrutura de dados para agrupar todas as informações
     * relevantes de um cálculo de folha de pagamento para um único empregado.
     */
    private class PagamentoInfo {
        BigDecimal salarioBruto = BigDecimal.ZERO, descontos = BigDecimal.ZERO, salarioLiquido = BigDecimal.ZERO;
        BigDecimal horasNormais = BigDecimal.ZERO, horasExtras = BigDecimal.ZERO;
        BigDecimal salarioFixo = BigDecimal.ZERO, vendas = BigDecimal.ZERO, comissao = BigDecimal.ZERO;
    }
}