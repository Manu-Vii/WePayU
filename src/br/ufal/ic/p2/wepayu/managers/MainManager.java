package br.ufal.ic.p2.wepayu.managers;

/**
 * Classe principal que atua como um agregador ou fachada (Facade) para todos os outros
 * gerenciadores do sistema. Ela centraliza o acesso às diferentes funcionalidades,
 * como gerenciamento de empregados, lançamento de pontos, vendas, taxas e folha de pagamento.
 */
public class MainManager {

    /**
     * Instância do gerenciador de empregados, responsável pelas operações de CRUD de empregados.
     */
    public EmpregadoManager empregadoManager = new EmpregadoManager();

    /**
     * Instância do gerenciador de cartões de ponto, para registrar horas de horistas.
     */
    public LancaCartaoPontoManager lancaCartaoPontoManager = new LancaCartaoPontoManager();

    /**
     * Instância do gerenciador de resultados de venda, para registrar vendas de comissionados.
     */
    public LancaResultadoVendaManager lancaResultadoVendaManager = new LancaResultadoVendaManager();

    /**
     * Instância do gerenciador de taxas de serviço, para registrar taxas de sindicalizados.
     */
    public LancaTaxaServicoManager lancaTaxaServicoManager = new LancaTaxaServicoManager();

    /**
     * Instância do gerenciador da folha de pagamento, responsável por calcular e gerar a folha.
     */
    public FolhaPagamentoManager folhaPagamentoManager = new FolhaPagamentoManager();

    /**
     * Retorna a instância do gerenciador de empregados.
     *
     * @return a instância de {@link EmpregadoManager}.
     */
    public EmpregadoManager getEmpregadoManager() {
        return empregadoManager;
    }

    /**
     * Retorna a instância do gerenciador de cartões de ponto.
     *
     * @return a instância de {@link LancaCartaoPontoManager}.
     */
    public LancaCartaoPontoManager getLancaCartaoPontoManager() {
        return lancaCartaoPontoManager;
    }

    /**
     * Retorna a instância do gerenciador de resultados de venda.
     *
     * @return a instância de {@link LancaResultadoVendaManager}.
     */
    public LancaResultadoVendaManager getLancaResultadoVendaManager() {
        return lancaResultadoVendaManager;
    }

    /**
     * Retorna a instância do gerenciador de taxas de serviço.
     *
     * @return a instância de {@link LancaTaxaServicoManager}.
     */
    public LancaTaxaServicoManager getLancaTaxaServicoManager() {
        return lancaTaxaServicoManager;
    }

    /**
     * Retorna a instância do gerenciador da folha de pagamento.
     *
     * @return a instância de {@link FolhaPagamentoManager}.
     */
    public FolhaPagamentoManager getFolhaPagamentoManager() {
        return folhaPagamentoManager;
    }
}