package br.ufal.ic.p2.wepayu.managers;

/**
 * Classe principal de gerenciamento que atua como um container ou agregador
 * para todos os outros gerenciadores de subsistemas da aplicação.
 * <p>
 * O objetivo desta classe é centralizar a criação e o acesso aos diferentes
 * managers, simplificando a injeção de dependências e o acoplamento entre
 * as diferentes partes da lógica de negócio.
 * </p>
 */
public class MainManager {
    public EmpregadoManager empregadoManager = new EmpregadoManager();
    public LancaCartaoPontoManager lancaCartaoPontoManager = new LancaCartaoPontoManager();
    public LancaResultadoVendaManager lancaResultadoVendaManager = new LancaResultadoVendaManager();
    public LancaTaxaServicoManager lancaTaxaServicoManager = new LancaTaxaServicoManager();
    public FolhaPagamentoManager folhaPagamentoManager = new FolhaPagamentoManager();
    public AgendaManager agendaManager = new AgendaManager();


    /**
     * Retorna a instância do gerenciador de empregados.
     *
     * @return A instância de {@link EmpregadoManager}.
     */
    public EmpregadoManager getEmpregadoManager() {
        return empregadoManager;
    }

    /**
     * Retorna a instância do gerenciador de cartões de ponto.
     *
     * @return A instância de {@link LancaCartaoPontoManager}.
     */
    public LancaCartaoPontoManager getLancaCartaoPontoManager() {
        return lancaCartaoPontoManager;
    }

    /**
     * Retorna a instância do gerenciador de resultados de venda.
     *
     * @return A instância de {@link LancaResultadoVendaManager}.
     */
    public LancaResultadoVendaManager getLancaResultadoVendaManager() {
        return lancaResultadoVendaManager;
    }

    /**
     * Retorna a instância do gerenciador de taxas de serviço.
     *
     * @return A instância de {@link LancaTaxaServicoManager}.
     */
    public LancaTaxaServicoManager getLancaTaxaServicoManager() {
        return lancaTaxaServicoManager;
    }

    /**
     * Retorna a instância do gerenciador da folha de pagamento.
     *
     * @return A instância de {@link FolhaPagamentoManager}.
     */
    public FolhaPagamentoManager getFolhaPagamentoManager() {
        return folhaPagamentoManager;
    }

    /**
     * Retorna a instância do gerenciador de agendas de pagamento.
     *
     * @return A instância de {@link AgendaManager}.
     */
    public AgendaManager getAgendaManager() {
        return agendaManager;
    }
}