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
 * Classe responsável por gerenciar as operações da folha de pagamento,
 * incluindo a simulação e a execução do cálculo e geração de relatórios.
 */
public class FolhaPagamentoManager {
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Simula o cálculo da folha de pagamento para uma data específica e retorna o valor total.
     * Esta operação não altera o estado dos empregados (ex: última data de pagamento).
     *
     * @param data A data para a qual a folha de pagamento deve ser simulada, no formato "dd/MM/yyyy".
     * @return O valor total da folha de pagamento como um {@link BigDecimal}.
     * @throws Exception se ocorrer um erro ao analisar a data.
     */
    public BigDecimal totalFolha(String data) throws Exception {
        LocalDate dataAtual = AppUtils.parseDate(data);
        BigDecimal total = BigDecimal.ZERO;

        EmpregadoRepository.Memento originalState = empregadoRepository.createMemento();
        try {
            List<Empregado> empregadosSimulacao = originalState.getSavedState().values().stream()
                    .map(Empregado::clone)
                    .collect(Collectors.toList());

            for (Empregado emp : empregadosSimulacao) {
                if (deveSerPago(emp, dataAtual)) {
                    total = total.add(calcularPagamentoCompleto(emp, dataAtual, true).salarioBruto);
                }
            }
        } finally {
            empregadoRepository.setMemento(originalState);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Executa a folha de pagamento para uma data específica, gerando um arquivo de saída com o relatório.
     * Esta operação atualiza a última data de pagamento dos empregados pagos.
     *
     * @param data  A data para a qual a folha de pagamento deve ser executada, no formato "dd/MM/yyyy".
     * @param saida O caminho do arquivo onde o relatório da folha de pagamento será salvo.
     * @throws Exception se ocorrer um erro ao analisar a data ou ao escrever no arquivo de saída.
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
     * @param writer      O {@link PrintWriter} para escrever o relatório.
     * @param empregados  A lista de todos os empregados a serem pagos.
     * @param tipo        O tipo de empregado a ser processado ("horista", "assalariado", "comissionado").
     * @param dataAtual   A data atual do pagamento.
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
            PagamentoInfo info = calcularPagamentoCompleto(emp, dataAtual, false);
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
     * Retorna uma representação em string do método de pagamento de um empregado.
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
     * Verifica se um empregado deve ser pago na data especificada, com base em sua agenda de pagamento.
     *
     * @param emp  O empregado a ser verificado.
     * @param data A data do pagamento.
     * @return {@code true} se o empregado deve ser pago, {@code false} caso contrário.
     */
    private boolean deveSerPago(Empregado emp, LocalDate data) {
        LocalDate dataContratacao = emp.getDataContratacao();
        if (emp instanceof EmpregadoHorista && dataContratacao == null && !emp.getCartoesPonto().isEmpty()){
            dataContratacao = emp.getCartoesPonto().stream().min(Comparator.comparing(CartaoDePonto::getData)).get().getData();
        }

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
                return diaMes > ultimoDiaDoMes.getDayOfMonth() && data.equals(ultimoDiaDoMes);
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

            LocalDate anchorDate = LocalDate.of(2004, 12, 27);
            long weeksSinceAnchor = ChronoUnit.WEEKS.between(anchorDate, data);

            return weeksSinceAnchor >= 0 && weeksSinceAnchor % frequencia == 0;
        }
        return false;
    }

    /**
     * Calcula as informações completas de pagamento para um empregado,
     * delegando para o método específico de acordo com o tipo do empregado.
     *
     * @param emp         O empregado para o qual o pagamento será calculado.
     * @param data        A data do pagamento.
     * @param isSimulacao {@code true} se for uma simulação, {@code false} caso contrário.
     * @return Um objeto {@link PagamentoInfo} com todos os detalhes do pagamento.
     */
    private PagamentoInfo calcularPagamentoCompleto(Empregado emp, LocalDate data, boolean isSimulacao) {
        return switch (emp.getTipo()) {
            case "horista" -> calcularPagamentoHoristaCompleto((EmpregadoHorista) emp, data, isSimulacao);
            case "assalariado" -> calcularPagamentoAssalariadoCompleto((EmpregadoAssalariado) emp, data, isSimulacao);
            case "comissionado" -> calcularPagamentoComissionadoCompleto((EmpregadoComissionado) emp, data, isSimulacao);
            default -> new PagamentoInfo();
        };
    }

    /**
     * Determina a data de início do período de pagamento com base na agenda e no histórico do empregado.
     *
     * @param emp           O empregado.
     * @param dataPagamento A data do pagamento.
     * @param isSimulacao   Indica se o cálculo é uma simulação.
     * @return A data de início do período de pagamento.
     */
    private LocalDate obterInicioPeriodo(Empregado emp, LocalDate dataPagamento, boolean isSimulacao) {
        if (!isSimulacao && emp.getUltimaDataPagamento() != null) {
            return emp.getUltimaDataPagamento().plusDays(1);
        }

        if (isSimulacao && emp instanceof EmpregadoHorista && !emp.getCartoesPonto().isEmpty()) {
            LocalDate primeiroCartao = emp.getCartoesPonto().stream()
                    .min(Comparator.comparing(CartaoDePonto::getData))
                    .get().getData();
            if (primeiroCartao.isAfter(dataPagamento.minusDays(7))) {
                return primeiroCartao;
            }
        }

        String agenda = emp.getAgendaPagamento();
        String[] parts = agenda.split(" ");
        if (parts[0].equals("semanal")) {
            int frequencia = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;
            return dataPagamento.minusWeeks(frequencia).plusDays(1);
        } else {
            return dataPagamento.withDayOfMonth(1);
        }
    }

    /**
     * Calcula os detalhes de pagamento para um empregado horista.
     *
     * @param horista     O empregado horista.
     * @param data        A data do pagamento.
     * @param isSimulacao Indica se é uma simulação.
     * @return Um objeto {@link PagamentoInfo} preenchido.
     */
    private PagamentoInfo calcularPagamentoHoristaCompleto(EmpregadoHorista horista, LocalDate data, boolean isSimulacao) {
        PagamentoInfo info = new PagamentoInfo();
        LocalDate inicioPeriodo = obterInicioPeriodo(horista, data, isSimulacao);

        for (CartaoDePonto cartao : horista.getCartoesPonto()) {
            LocalDate dataCartao = cartao.getData();
            if (inicioPeriodo != null && !dataCartao.isBefore(inicioPeriodo) && !dataCartao.isAfter(data)) {
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
     * Calcula os detalhes de pagamento para um empregado assalariado.
     *
     * @param assalariado O empregado assalariado.
     * @param data        A data do pagamento.
     * @param isSimulacao Indica se é uma simulação.
     * @return Um objeto {@link PagamentoInfo} preenchido.
     */
    private PagamentoInfo calcularPagamentoAssalariadoCompleto(EmpregadoAssalariado assalariado, LocalDate data, boolean isSimulacao) {
        PagamentoInfo info = new PagamentoInfo();
        String agenda = assalariado.getAgendaPagamento();
        BigDecimal salarioMensal = assalariado.getSalario();

        if (agenda.startsWith("semanal")) {
            String[] parts = agenda.split(" ");
            int frequencia = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;
            info.salarioBruto = salarioMensal.multiply(new BigDecimal("12"))
                    .multiply(new BigDecimal(frequencia))
                    .divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
        } else {
            info.salarioBruto = salarioMensal;
        }

        LocalDate inicioPeriodo = obterInicioPeriodo(assalariado, data, isSimulacao);
        info.descontos = calcularDescontosSindicais(assalariado, inicioPeriodo, data);
        info.salarioLiquido = info.salarioBruto.subtract(info.descontos);
        if (info.salarioLiquido.compareTo(BigDecimal.ZERO) < 0) info.salarioLiquido = BigDecimal.ZERO;
        return info;
    }

    /**
     * Calcula os detalhes de pagamento para um empregado comissionado.
     *
     * @param comissionado O empregado comissionado.
     * @param data         A data do pagamento.
     * @param isSimulacao  Indica se é uma simulação.
     * @return Um objeto {@link PagamentoInfo} preenchido.
     */
    private PagamentoInfo calcularPagamentoComissionadoCompleto(EmpregadoComissionado comissionado, LocalDate data, boolean isSimulacao) {
        PagamentoInfo info = new PagamentoInfo();
        LocalDate inicioPeriodo = obterInicioPeriodo(comissionado, data, isSimulacao);
        String agenda = comissionado.getAgendaPagamento();
        BigDecimal salarioMensal = comissionado.getSalario();

        if (agenda.startsWith("semanal")) {
            String[] parts = agenda.split(" ");
            int frequencia = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;
            info.salarioFixo = salarioMensal.multiply(new BigDecimal("12"))
                    .multiply(new BigDecimal(frequencia))
                    .divide(new BigDecimal("52"), 2, RoundingMode.FLOOR);
        } else {
            info.salarioFixo = salarioMensal;
        }

        for (ResultadoVenda venda : comissionado.getResultadosVendas()) {
            LocalDate dataVenda = venda.getData();
            if (inicioPeriodo != null && !dataVenda.isBefore(inicioPeriodo) && !dataVenda.isAfter(data)) {
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
     * Calcula os descontos sindicais (taxa sindical e taxas de serviço) para um empregado em um determinado período.
     *
     * @param emp   O empregado.
     * @param inicio A data de início do período.
     * @param fim    A data de fim do período.
     * @return O valor total dos descontos sindicais.
     */
    private BigDecimal calcularDescontosSindicais(Empregado emp, LocalDate inicio, LocalDate fim) {
        BigDecimal descontos = BigDecimal.ZERO;

        if (!emp.isSindicalizado() || inicio == null) {
            return BigDecimal.ZERO;
        }

        LocalDate inicioEfetivo = inicio;
        
        if (emp.getUltimaDataPagamento() != null) {
            inicioEfetivo = emp.getUltimaDataPagamento().plusDays(1);
        }

        long dias = ChronoUnit.DAYS.between(inicioEfetivo, fim) + 1;

        if (emp instanceof EmpregadoHorista) {
            if (emp.getUltimaDataPagamento() == null) {
                dias = Math.min(dias, 7);
            } else {
                if (dias < 14) {
                    dias = 28;
                }
            }
        }

        if (emp.getTaxaSindical() != null) {
            if (emp instanceof EmpregadoAssalariado) {
                descontos = descontos.add(emp.getTaxaSindical().multiply(new BigDecimal(fim.lengthOfMonth())));
            } else {
                descontos = descontos.add(emp.getTaxaSindical().multiply(new BigDecimal(dias)));
            }
        }

        for (TaxaDeServico taxa : emp.getTaxasDeServico()) {
            if (!taxa.getData().isBefore(inicioEfetivo) && !taxa.getData().isAfter(fim)) {
                descontos = descontos.add(taxa.getValor());
            }
        }

        return descontos;
    }


    /**
     * Formata uma linha do relatório de pagamento para um empregado específico.
     *
     * @param emp  O empregado.
     * @param info As informações de pagamento calculadas.
     * @return Uma string formatada para ser impressa no relatório.
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
     * Classe interna para armazenar os dados calculados de um pagamento.
     * Serve como um contêiner de dados para facilitar a passagem de informações
     * entre os métodos de cálculo e de geração de relatório.
     */
    private class PagamentoInfo {
        BigDecimal salarioBruto = BigDecimal.ZERO, descontos = BigDecimal.ZERO, salarioLiquido = BigDecimal.ZERO;
        BigDecimal horasNormais = BigDecimal.ZERO, horasExtras = BigDecimal.ZERO;
        BigDecimal salarioFixo = BigDecimal.ZERO, vendas = BigDecimal.ZERO, comissao = BigDecimal.ZERO;
    }
}
