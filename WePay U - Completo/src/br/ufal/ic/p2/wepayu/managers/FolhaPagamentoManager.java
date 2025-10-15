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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Gerenciador principal da lógica de negócio da Folha de Pagamento.
 * <p>
 * Esta classe é responsável por todas as operações de processamento da folha,
 * incluindo a identificação de empregados a serem pagos em uma data específica,
 * o cálculo detalhado de seus salários (bruto, descontos, líquido) e a geração
 * de um relatório de saída formatado.
 * </p>
 */
public class FolhaPagamentoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Calcula o valor total bruto da folha de pagamento para uma data específica.
     *
     * @param data A data para a qual a folha deve ser calculada, no formato "d/M/yyyy".
     * @return O valor total da folha como {@link BigDecimal}.
     * @throws Exception se a data for inválida.
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
     * Processa a folha de pagamento para uma data específica.
     * Este método identifica os empregados a serem pagos, calcula seus vencimentos e descontos,
     * gera um arquivo de relatório detalhado e atualiza a data do último pagamento dos empregados.
     *
     * @param data  A data do processamento da folha, no formato "d/M/yyyy".
     * @param saida O caminho do arquivo de texto onde o relatório será salvo.
     * @throws Exception se a data for inválida ou ocorrer um erro de I/O.
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
            emp.setUltimaDataPagamento(dataAtual);
        }
    }

    /**
     * Gera uma seção do relatório da folha de pagamento para um tipo específico de empregado.
     *
     * @param writer      O {@link PrintWriter} para escrever no arquivo de saída.
     * @param empregados  A lista de todos os empregados a serem pagos.
     * @param tipo        O tipo de empregado para filtrar e gerar o relatório (ex: "horista").
     * @param dataAtual   A data atual da folha de pagamento.
     * @return O valor total bruto pago para este grupo de empregados.
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
     * Converte o objeto de método de pagamento em uma string formatada para o relatório.
     * @param emp O empregado.
     * @return A descrição textual do método de pagamento.
     */
    private String getMetodoPagamentoString(Empregado emp) {
        MetodoPagamento metodo = emp.getMetodoPagamento();
        if (metodo instanceof EmMaos) return "Em maos";
        if (metodo instanceof Correios) return "Correios, " + emp.getEndereco();
        if (metodo instanceof Banco banco) return banco.getBanco() + ", Ag. " + banco.getAgencia() + " CC " + banco.getContaCorrente();
        return "";
    }

    /**
     * Determina se um empregado deve ser pago em uma data específica, com base em sua agenda de pagamento.
     * @param emp O empregado a ser verificado.
     * @param data A data a ser verificada.
     * @return {@code true} se for um dia de pagamento para o empregado, {@code false} caso contrário.
     */
    private boolean deveSerPago(Empregado emp, LocalDate data) {
        LocalDate dataContratacao = emp.getDataContratacao();
        if (dataContratacao != null && data.isBefore(dataContratacao)) {
            return false;
        }

        String agenda = emp.getAgendaPagamento();
        String[] parts = agenda.split(" ");
        String tipoAgenda = parts[0];

        if (tipoAgenda.equals("mensal")) {
            String dia = parts[1];
            if (dia.equals("$")) {
                LocalDate ultimoDiaUtil = data.with(TemporalAdjusters.lastDayOfMonth());
                while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
                }
                return data.equals(ultimoDiaUtil);
            } else {
                int diaMes = Integer.parseInt(dia);
                if (data.getDayOfMonth() == diaMes) {
                    return true;
                }
                LocalDate ultimoDiaDoMes = data.with(TemporalAdjusters.lastDayOfMonth());
                if (diaMes > ultimoDiaDoMes.getDayOfMonth() && data.equals(ultimoDiaDoMes)) {
                    return true;
                }
                return false;
            }
        } else if (tipoAgenda.equals("semanal")) {
            int frequencia, diaDaSemana;

            if (parts.length == 3) {
                frequencia = Integer.parseInt(parts[1]);
                diaDaSemana = Integer.parseInt(parts[2]);
            } else {
                frequencia = 1;
                diaDaSemana = Integer.parseInt(parts[1]);
            }

            if (data.getDayOfWeek().getValue() != diaDaSemana) {
                return false;
            }

            LocalDate anchorDate = LocalDate.of(2004, 12, 27); // Uma segunda-feira
            long weeksSinceAnchor = ChronoUnit.WEEKS.between(anchorDate, data);

            return weeksSinceAnchor >= 0 && weeksSinceAnchor % frequencia == 0;
        }
        return false;
    }

    /**
     * Calcula o salário bruto de um empregado para uma data de pagamento.
     * @param emp O empregado.
     * @param data A data do pagamento.
     * @return O valor do salário bruto.
     */
    private BigDecimal calcularPagamento(Empregado emp, LocalDate data) {
        return calcularPagamentoCompleto(emp, data).salarioBruto;
    }

    /**
     * Calcula todas as informações de pagamento (bruto, descontos, líquido, etc.) para um empregado.
     * @param emp O empregado.
     * @param data A data do pagamento.
     * @return Um objeto {@link PagamentoInfo} com todos os detalhes do pagamento.
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
     * Determina a data de início do período de pagamento com base na agenda do empregado e na data do último pagamento.
     * @param emp O empregado.
     * @param dataPagamento A data do pagamento atual.
     * @return A data de início do período a ser considerado para o cálculo.
     */
    private LocalDate obterInicioPeriodo(Empregado emp, LocalDate dataPagamento) {
        LocalDate inicioCalculado;
        String agenda = emp.getAgendaPagamento();

        String[] parts = agenda.split(" ");
        if (parts[0].equals("semanal")) {
            int frequencia = (parts.length == 2) ? 1 : Integer.parseInt(parts[1]);
            inicioCalculado = dataPagamento.minusWeeks(frequencia).plusDays(1);
        } else { // mensal
            inicioCalculado = dataPagamento.withDayOfMonth(1);
        }

        LocalDate inicioAposUltimoPagamento = emp.getUltimaDataPagamento() != null ? emp.getUltimaDataPagamento().plusDays(1) : null;

        if (inicioAposUltimoPagamento != null && inicioAposUltimoPagamento.isAfter(inicioCalculado)) {
            return inicioAposUltimoPagamento;
        }

        return inicioCalculado;
    }

    /**
     * Lógica de cálculo específica para um empregado horista.
     */
    private PagamentoInfo calcularPagamentoHoristaCompleto(EmpregadoHorista horista, LocalDate data) {
        PagamentoInfo info = new PagamentoInfo();
        LocalDate inicioPeriodo = obterInicioPeriodo(horista, data);

        for (CartaoDePonto cartao : horista.getCartoesPonto()) {
            LocalDate dataCartao = cartao.getData();
            if (!dataCartao.isBefore(inicioPeriodo) && !dataCartao.isAfter(data)) {
                BigDecimal horas = cartao.getHoras();
                if (horas.compareTo(new BigDecimal("8")) > 0) {
                    info.horasNormais = info.horasNormais.add(new BigDecimal("8"));
                    info.horasExtras = info.horasExtras.add(horas.subtract(new BigDecimal("8")));
                } else {
                    info.horasNormais = info.horasNormais.add(horas);
                }
            }
        }

        BigDecimal salarioHora = horista.getSalario();
        info.salarioBruto = info.horasNormais.multiply(salarioHora)
                .add(info.horasExtras.multiply(salarioHora.multiply(new BigDecimal("1.5"))));

        info.descontos = calcularDescontosSindicais(horista, inicioPeriodo, data);
        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;

        return info;
    }

    /**
     * Lógica de cálculo específica para um empregado assalariado.
     */
    private PagamentoInfo calcularPagamentoAssalariadoCompleto(EmpregadoAssalariado assalariado, LocalDate data) {
        PagamentoInfo info = new PagamentoInfo();
        String agenda = assalariado.getAgendaPagamento();
        BigDecimal salarioMensal = assalariado.getSalario();

        if (agenda.startsWith("semanal")) {
            String[] parts = agenda.split(" ");
            int frequencia = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;
            info.salarioBruto = salarioMensal.multiply(new BigDecimal("12"))
                    .multiply(new BigDecimal(frequencia))
                    .divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
        } else { // mensal
            info.salarioBruto = salarioMensal;
        }

        LocalDate inicioPeriodo = obterInicioPeriodo(assalariado, data);
        info.descontos = calcularDescontosSindicais(assalariado, inicioPeriodo, data);
        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;
        return info;
    }

    /**
     * Lógica de cálculo específica para um empregado comissionado.
     */
    private PagamentoInfo calcularPagamentoComissionadoCompleto(EmpregadoComissionado comissionado, LocalDate data) {
        PagamentoInfo info = new PagamentoInfo();
        LocalDate inicioPeriodo = obterInicioPeriodo(comissionado, data);

        String agenda = comissionado.getAgendaPagamento();
        BigDecimal salarioMensal = comissionado.getSalario();

        if (agenda.startsWith("semanal")) {
            String[] parts = agenda.split(" ");
            int frequencia = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;
            info.salarioFixo = salarioMensal.multiply(new BigDecimal("12"))
                    .multiply(new BigDecimal(frequencia))
                    .divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
        } else { // mensal
            info.salarioFixo = salarioMensal;
        }

        for (ResultadoVenda venda : comissionado.getResultadosVendas()) {
            LocalDate dataVenda = venda.getData();
            if (!dataVenda.isBefore(inicioPeriodo) && !dataVenda.isAfter(data)) {
                info.vendas = info.vendas.add(venda.getValor());
            }
        }

        info.comissao = info.vendas.multiply(comissionado.getComissao()).setScale(2, RoundingMode.HALF_UP);
        info.salarioBruto = info.salarioFixo.add(info.comissao);
        info.descontos = calcularDescontosSindicais(comissionado, inicioPeriodo, data);
        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;

        return info;
    }

    /**
     * Calcula o total de descontos sindicais (taxa fixa + taxas de serviço) para um empregado em um período.
     * @param emp O empregado.
     * @param inicio A data de início do período.
     * @param fim A data de fim do período.
     * @return O valor total dos descontos.
     */
    private BigDecimal calcularDescontosSindicais(Empregado emp, LocalDate inicio, LocalDate fim) {
        BigDecimal descontos = BigDecimal.ZERO;
        if (emp.isSindicalizado() && emp.getTaxaSindical() != null && inicio != null) {

            String agenda = emp.getAgendaPagamento();

            if (agenda.startsWith("semanal")) {
                long fridays = 0;
                for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusDays(1)) {
                    if (d.getDayOfWeek() == DayOfWeek.FRIDAY) {
                        fridays++;
                    }
                }
                descontos = descontos.add(emp.getTaxaSindical().multiply(new BigDecimal(fridays)));
            } else { // Mensal
                descontos = descontos.add(emp.getTaxaSindical());
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
     * Formata uma linha do relatório de pagamento para um empregado específico.
     * @param emp O empregado.
     * @param info O objeto {@link PagamentoInfo} com os dados do pagamento.
     * @return Uma string formatada para ser impressa no relatório.
     */
    private String formatarLinhaRelatorio(Empregado emp, PagamentoInfo info) {
        String metodo = getMetodoPagamentoString(emp);
        if (emp instanceof EmpregadoHorista) {
            return String.format(Locale.FRANCE, "%-36s %5.0f %5.0f %13.2f %9.2f %15.2f %s\n",
                    emp.getNome(), info.horasNormais, info.horasExtras, info.salarioBruto,
                    info.descontos, info.salarioLiquido, metodo);
        } else if (emp instanceof EmpregadoComissionado) {
            return String.format(Locale.FRANCE, "%-17s %8.2f %10.2f %10.2f %13.2f %9.2f %15.2f %s\n",
                    emp.getNome(), info.salarioFixo, info.vendas, info.comissao, info.salarioBruto,
                    info.descontos, info.salarioLiquido, metodo);
        } else { // Assalariado
            return String.format(Locale.FRANCE, "%-48s %13.2f %9.2f %15.2f %s\n",
                    emp.getNome(), info.salarioBruto, info.descontos, info.salarioLiquido, metodo);
        }
    }

    /**
     * Classe auxiliar interna para agrupar as informações calculadas de um pagamento.
     * Funciona como um DTO (Data Transfer Object) para transitar os dados entre os métodos de cálculo.
     */
    private class PagamentoInfo {
        BigDecimal salarioBruto = BigDecimal.ZERO, descontos = BigDecimal.ZERO, salarioLiquido = BigDecimal.ZERO;
        BigDecimal horasNormais = BigDecimal.ZERO, horasExtras = BigDecimal.ZERO;
        BigDecimal salarioFixo = BigDecimal.ZERO, vendas = BigDecimal.ZERO, comissao = BigDecimal.ZERO;
    }
}