package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.managers.CommandHistoryManager;
import br.ufal.ic.p2.wepayu.managers.FolhaPagamentoManager;
import br.ufal.ic.p2.wepayu.managers.MainManager;
import br.ufal.ic.p2.wepayu.utils.AppUtils;

/**
 * A classe Facade é o ponto de entrada principal para a interface do sistema.
 * Ela delega a maioria das chamadas de método para as classes Manager apropriadas,
 * gerenciando o histórico de comandos para operações de desfazer e refazer.
 */
public class Facade {

    private final MainManager mainManager = new MainManager();
    private final FolhaPagamentoManager folhaPagamentoManager = new FolhaPagamentoManager();
    private final CommandHistoryManager commandHistory = new CommandHistoryManager();

    /**
     * Construtor que garante que o sistema está aberto para receber comandos.
     */
    public Facade() {
        br.ufal.ic.p2.wepayu.repository.EmpregadoRepository.getInstance().setSistemaEncerrado(false);
    }

    /**
     * Zera todos os dados do sistema.
     * @throws Exception se ocorrer um erro durante a operação.
     */
    public void zerarSistema() throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().zerarSistema());
    }

    /**
     * Encerra o sistema, salvando os dados.
     */
    public void encerrarSistema() {
        mainManager.getEmpregadoManager().encerrarSistema();
        mainManager.getEmpregadoManager().empregadoRepository.encerrarSistema();
    }

    /**
     * Cria um novo empregado.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo de empregado (e.g., "horista", "assalariado").
     * @param salario Salário do empregado.
     * @param comissao Comissão do empregado (apenas para comissionados).
     * @return O ID do empregado recém-criado.
     * @throws Exception se os dados forem inválidos ou houver um erro.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        return commandHistory.execute(() -> mainManager.getEmpregadoManager().criarEmpregado(nome, endereco, tipo, salario, comissao));
    }

    /**
     * Cria um novo empregado sem comissao.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo de empregado (e.g., "horista", "assalariado").
     * @param salario Salário do empregado.
     * @return O ID do empregado recém-criado.
     * @throws Exception se os dados forem inválidos ou houver um erro.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        return criarEmpregado(nome, endereco, tipo, salario, null);
    }

    /**
     * Remove um empregado do sistema.
     * @param id ID do empregado a ser removido.
     * @throws Exception se o empregado não existir.
     */
    public void removerEmpregado(String id) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().removerEmpregado(id));
    }

    /**
     * Lança um cartão de ponto para um empregado horista.
     * @param emp ID do empregado.
     * @param data Data do lançamento.
     * @param horas Horas trabalhadas.
     * @throws Exception se o empregado não for horista ou os dados forem inválidos.
     */
    public void lancaCartao(String emp, String data, String horas) throws Exception {
        commandHistory.execute(() -> mainManager.getLancaCartaoPontoManager().lancaCartao(emp, data, horas));
    }

    /**
     * Lança um resultado de venda para um empregado comissionado.
     * @param emp ID do empregado.
     * @param data Data da venda.
     * @param valor Valor da venda.
     * @throws Exception se o empregado não for comissionado ou os dados forem inválidos.
     */
    public void lancaVenda(String emp, String data, String valor) throws Exception {
        commandHistory.execute(() -> mainManager.getLancaResultadoVendaManager().lancaVenda(emp, data, valor));
    }

    /**
     * Lança uma taxa de serviço para um empregado sindicalizado.
     * @param idSindicato ID do sindicato.
     * @param data Data do lançamento.
     * @param valor Valor da taxa.
     * @throws Exception se o membro não existir, não for sindicalizado ou os dados forem inválidos.
     */
    public void lancaTaxaServico(String idSindicato, String data, String valor) throws Exception {
        commandHistory.execute(() -> mainManager.getLancaTaxaServicoManager().lancaTaxaServico(idSindicato, data, valor));
    }

    /**
     * Altera um atributo de um empregado.
     * @param emp ID do empregado.
     * @param atributo Nome do atributo a ser alterado.
     * @param valor Novo valor do atributo.
     * @throws Exception se o empregado ou atributo não existirem, ou os dados forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, valor));
    }

    /**
     * Altera o status de sindicalização de um empregado.
     * @param emp ID do empregado.
     * @param atributo Deve ser "sindicalizado".
     * @param valor Novo status (true ou false).
     * @param idSindicato ID do sindicato.
     * @param taxaSindical Taxa sindical.
     * @throws Exception se os dados forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, boolean valor, String idSindicato, String taxaSindical) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical));
    }

    /**
     * Altera o método de pagamento para banco.
     * @param emp ID do empregado.
     * @param atributo Deve ser "metodoPagamento".
     * @param valor1 Deve ser "banco".
     * @param banco Nome do banco.
     * @param agencia Número da agência.
     * @param contaCorrente Número da conta corrente.
     * @throws Exception se os dados forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente));
    }

    /**
     * Altera o tipo de empregado.
     * @param emp ID do empregado.
     * @param atributo Deve ser "tipo".
     * @param tipo Novo tipo.
     * @param comissaoOuSalario Novo valor para comissão ou salário.
     * @throws Exception se os dados forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, String tipo, String comissaoOuSalario) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, tipo, comissaoOuSalario));
    }

    /**
     * Roda a folha de pagamento para uma data específica e salva a saída em um arquivo.
     * @param data Data do processamento.
     * @param saida Nome do arquivo de saída.
     * @throws Exception se os dados forem inválidos.
     */
    public void rodaFolha(String data, String saida) throws Exception {
        commandHistory.execute(() -> folhaPagamentoManager.rodaFolha(data, saida));
    }

    /**
     * Desfaz o último comando que alterou o estado do sistema.
     * @throws Exception se não houver comando a desfazer.
     */
    public void undo() throws Exception {
        commandHistory.undo();
    }

    /**
     * Refaz o último comando desfeito.
     * @throws Exception se não houver comando a refazer.
     */
    public void redo() throws Exception {
        commandHistory.redo();
    }

    /**
     * Obtém o valor de um atributo de um empregado.
     * @param id ID do empregado.
     * @param atributo Nome do atributo.
     * @return O valor do atributo formatado como String.
     * @throws Exception se o empregado ou atributo não existirem.
     */
    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        return mainManager.getEmpregadoManager().getAtributoEmpregado(id, atributo);
    }

    /**
     * Obtém o ID de um empregado pelo nome e índice.
     * @param nome Nome do empregado.
     * @param indice Índice do empregado (útil para nomes duplicados).
     * @return O ID do empregado.
     * @throws Exception se o empregado não for encontrado.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        return mainManager.getEmpregadoManager().getEmpregadoPorNome(nome, indice);
    }

    /**
     * Obtém o total de horas normais trabalhadas por um empregado horista em um período.
     * @param emp ID do empregado.
     * @param dataInicial Data de início do período.
     * @param dataFinal Data de fim do período.
     * @return O total de horas normais.
     * @throws Exception se o empregado não for horista ou os dados forem inválidos.
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaCartaoPontoManager().getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de horas extras trabalhadas por um empregado horista em um período.
     * @param emp ID do empregado.
     * @param dataInicial Data de início do período.
     * @param dataFinal Data de fim do período.
     * @return O total de horas extras.
     * @throws Exception se o empregado não for horista ou os dados forem inválidos.
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaCartaoPontoManager().getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de vendas realizadas por um empregado comissionado em um período.
     * @param emp ID do empregado.
     * @param dataInicial Data de início do período.
     * @param dataFinal Data de fim do período.
     * @return O total de vendas.
     * @throws Exception se o empregado não for comissionado ou os dados forem inválidos.
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaResultadoVendaManager().getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de taxas de serviço pagas por um empregado sindicalizado em um período.
     * @param emp ID do empregado.
     * @param dataInicial Data de início do período.
     * @param dataFinal Data de fim do período.
     * @return O total de taxas.
     * @throws Exception se o empregado não for sindicalizado ou os dados forem inválidos.
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaTaxaServicoManager().getTaxasServico(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o total da folha de pagamento para uma data específica.
     * @param data Data do processamento.
     * @return O total da folha de pagamento formatado como String.
     * @throws Exception se os dados forem inválidos.
     */
    public String totalFolha(String data) throws Exception {
        return AppUtils.formatBigDecimal(folhaPagamentoManager.totalFolha(data));
    }

    /**
     * Obtém o número total de empregados.
     * @return O número de empregados como String.
     */
    public String getNumeroDeEmpregados() {
        return String.valueOf(mainManager.getEmpregadoManager().getNumeroDeEmpregados());
    }
}