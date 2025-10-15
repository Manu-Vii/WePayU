package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.managers.CommandHistoryManager;
import br.ufal.ic.p2.wepayu.managers.FolhaPagamentoManager;
import br.ufal.ic.p2.wepayu.managers.MainManager;
import br.ufal.ic.p2.wepayu.utils.AppUtils;

/**
 * A classe Facade implementa o padrão de projeto Facade para fornecer uma interface simplificada
 * e unificada para um conjunto complexo de subsistemas de gerenciamento do sistema WePayU.
 * Ela delega as chamadas dos clientes para os objetos gerenciadores apropriados, como
 * {@link MainManager}, {@link FolhaPagamentoManager} e {@link CommandHistoryManager}.
 *
 * Esta classe é o principal ponto de entrada para todas as operações do sistema.
 */
public class Facade {

    private final MainManager mainManager = new MainManager();
    private final FolhaPagamentoManager folhaPagamentoManager = new FolhaPagamentoManager();
    private final CommandHistoryManager commandHistory = new CommandHistoryManager();

    /**
     * Construtor da classe Facade.
     * Inicializa o sistema, definindo o status do repositório de empregados como não encerrado,
     * permitindo que as operações sejam executadas.
     */
    public Facade() {
        br.ufal.ic.p2.wepayu.repository.EmpregadoRepository.getInstance().setSistemaEncerrado(false);
    }

    /**
     * Zera todos os dados do sistema, incluindo empregados e agendas de pagamento.
     * Esta operação é encapsulada pelo {@link CommandHistoryManager} para permitir undo/redo.
     * @throws Exception se ocorrer um erro durante a limpeza dos dados.
     */
    public void zerarSistema() throws Exception {
        commandHistory.execute(() -> {
            mainManager.getEmpregadoManager().zerarSistema();
            mainManager.getAgendaManager().zerarDados();
        });
    }

    /**
     * Encerra o sistema, salvando os dados persistentes (como agendas de pagamento)
     * e marcando o repositório como encerrado.
     */
    public void encerrarSistema() {
        mainManager.getAgendaManager().salvarDados();
        mainManager.empregadoManager.empregadoRepository.encerrarSistema();
    }

    /**
     * Cria uma nova agenda de pagamentos customizada.
     * @param descricao A descrição da agenda (ex: "semanal 5", "mensal $").
     * @throws Exception se a descrição da agenda for inválida.
     */
    public void criarAgendaDePagamentos(String descricao) throws Exception {
        mainManager.getAgendaManager().criarAgendaDePagamentos(descricao);
    }

    /**
     * Cria um novo empregado no sistema. Esta é uma sobrecarga para empregados comissionados.
     * @param nome O nome do empregado.
     * @param endereco O endereço do empregado.
     * @param tipo O tipo do empregado ("horista", "assalariado", "comissionado").
     * @param salario O salário base do empregado.
     * @param comissao A taxa de comissão (para empregados comissionados).
     * @return O ID único do empregado criado.
     * @throws Exception se os dados de entrada forem inválidos.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        return commandHistory.execute(() -> mainManager.getEmpregadoManager().criarEmpregado(nome, endereco, tipo, salario, comissao));
    }

    /**
     * Cria um novo empregado no sistema. Esta é uma sobrecarga para empregados não comissionados.
     * @param nome O nome do empregado.
     * @param endereco O endereço do empregado.
     * @param tipo O tipo do empregado ("horista", "assalariado").
     * @param salario O salário do empregado.
     * @return O ID único do empregado criado.
     * @throws Exception se os dados de entrada forem inválidos.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        return criarEmpregado(nome, endereco, tipo, salario, null);
    }

    /**
     * Remove um empregado do sistema com base em seu ID.
     * @param id O ID do empregado a ser removido.
     * @throws Exception se o ID do empregado não for encontrado.
     */
    public void removerEmpregado(String id) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().removerEmpregado(id));
    }

    /**
     * Lança um cartão de ponto para um empregado horista.
     * @param emp O ID do empregado.
     * @param data A data do registro (formato "d/M/yyyy").
     * @param horas O número de horas trabalhadas.
     * @throws Exception se o empregado não for horista ou os dados forem inválidos.
     */
    public void lancaCartao(String emp, String data, String horas) throws Exception {
        commandHistory.execute(() -> mainManager.getLancaCartaoPontoManager().lancaCartao(emp, data, horas));
    }

    /**
     * Lança o resultado de uma venda para um empregado comissionado.
     * @param emp O ID do empregado.
     * @param data A data da venda (formato "d/M/yyyy").
     * @param valor O valor da venda.
     * @throws Exception se o empregado não for comissionado ou os dados forem inválidos.
     */
    public void lancaVenda(String emp, String data, String valor) throws Exception {
        commandHistory.execute(() -> mainManager.getLancaResultadoVendaManager().lancaVenda(emp, data, valor));
    }

    /**
     * Lança uma taxa de serviço para um membro de sindicato.
     * @param idSindicato O ID do membro no sindicato.
     * @param data A data da cobrança (formato "d/M/yyyy").
     * @param valor O valor da taxa de serviço.
     * @throws Exception se o membro do sindicato não for encontrado ou os dados forem inválidos.
     */
    public void lancaTaxaServico(String idSindicato, String data, String valor) throws Exception {
        commandHistory.execute(() -> mainManager.getLancaTaxaServicoManager().lancaTaxaServico(idSindicato, data, valor));
    }

    /**
     * Altera um atributo de um empregado.
     * @param emp O ID do empregado.
     * @param atributo O nome do atributo a ser alterado (ex: "nome", "endereco", "agendaPagamento").
     * @param valor1 O novo valor para o atributo.
     * @throws Exception se o empregado ou o atributo não existirem, ou se o valor for inválido.
     */
    public void alteraEmpregado(String emp, String atributo, String valor1) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, valor1, mainManager.getAgendaManager()));
    }

    /**
     * Altera o status sindical de um empregado.
     * @param emp O ID do empregado.
     * @param atributo Deve ser "sindicalizado".
     * @param valor O novo status sindical (true para sindicalizado, false caso contrário).
     * @param idSindicato O ID do membro no sindicato (se aplicável).
     * @param taxaSindical A taxa sindical a ser cobrada (se aplicável).
     * @throws Exception se os parâmetros forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, boolean valor, String idSindicato, String taxaSindical) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical));
    }

    /**
     * Altera o método de pagamento de um empregado para depósito bancário.
     * @param emp O ID do empregado.
     * @param atributo Deve ser "metodoPagamento".
     * @param valor1 Deve ser "banco".
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     * @throws Exception se os parâmetros forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente));
    }

    /**
     * Altera o tipo de um empregado (ex: de horista para assalariado).
     * @param emp O ID do empregado.
     * @param atributo Deve ser "tipo".
     * @param tipo O novo tipo do empregado.
     * @param comissaoOuSalario O novo salário ou taxa de comissão, dependendo do novo tipo.
     * @throws Exception se os parâmetros forem inválidos.
     */
    public void alteraEmpregado(String emp, String atributo, String tipo, String comissaoOuSalario) throws Exception {
        commandHistory.execute(() -> mainManager.getEmpregadoManager().alteraEmpregado(emp, atributo, tipo, comissaoOuSalario));
    }

    /**
     * Executa o processamento da folha de pagamento para uma data específica.
     * @param data A data para a qual a folha será rodada (formato "d/M/yyyy").
     * @param saida O caminho do arquivo de saída onde o resultado será salvo.
     * @throws Exception se ocorrer um erro durante o processamento.
     */
    public void rodaFolha(String data, String saida) throws Exception {
        commandHistory.execute(() -> folhaPagamentoManager.rodaFolha(data, saida));
    }

    /**
     * Desfaz a última operação que modificou o estado do sistema.
     * @throws Exception se não houver operações para desfazer.
     */
    public void undo() throws Exception {
        commandHistory.undo();
    }

    /**
     * Refaz a última operação desfeita.
     * @throws Exception se não houver operações para refazer.
     */
    public void redo() throws Exception {
        commandHistory.redo();
    }

    /**
     * Obtém o valor de um atributo específico de um empregado.
     * @param id O ID do empregado.
     * @param atributo O nome do atributo desejado (ex: "nome", "salario").
     * @return O valor do atributo como uma String formatada.
     * @throws Exception se o empregado ou o atributo não forem encontrados.
     */
    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        return mainManager.getEmpregadoManager().getAtributoEmpregado(id, atributo);
    }

    /**
     * Busca o ID de um empregado pelo seu nome.
     * Útil quando múltiplos empregados podem ter o mesmo nome.
     * @param nome O nome do empregado a ser buscado.
     * @param indice O índice do empregado na lista de resultados (começando em 1).
     * @return O ID do empregado encontrado.
     * @throws Exception se nenhum empregado for encontrado com o nome e índice fornecidos.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        return mainManager.getEmpregadoManager().getEmpregadoPorNome(nome, indice);
    }

    /**
     * Calcula o total de horas normais trabalhadas por um empregado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data de início do período (formato "d/M/yyyy").
     * @param dataFinal A data de fim do período (formato "d/M/yyyy").
     * @return O total de horas normais como uma String formatada.
     * @throws Exception se o empregado não for horista ou as datas forem inválidas.
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaCartaoPontoManager().getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o total de horas extras trabalhadas por um empregado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data de início do período (formato "d/M/yyyy").
     * @param dataFinal A data de fim do período (formato "d/M/yyyy").
     * @return O total de horas extras como uma String formatada.
     * @throws Exception se o empregado não for horista ou as datas forem inválidas.
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaCartaoPontoManager().getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o valor total das vendas realizadas por um empregado comissionado em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data de início do período (formato "d/M/yyyy").
     * @param dataFinal A data de fim do período (formato "d/M/yyyy").
     * @return O valor total das vendas como uma String formatada.
     * @throws Exception se o empregado não for comissionado ou as datas forem inválidas.
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaResultadoVendaManager().getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o valor total das taxas de serviço cobradas de um membro do sindicato em um período.
     * @param emp O ID do empregado.
     * @param dataInicial A data de início do período (formato "d/M/yyyy").
     * @param dataFinal A data de fim do período (formato "d/M/yyyy").
     * @return O valor total das taxas de serviço como uma String formatada.
     * @throws Exception se o empregado não for sindicalizado ou as datas forem inválidas.
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        return mainManager.getLancaTaxaServicoManager().getTaxasServico(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o custo total da folha de pagamento em uma data específica.
     * @param data A data do pagamento.
     * @return O valor total da folha como uma String formatada.
     * @throws Exception se a data for inválida.
     */
    public String totalFolha(String data) throws Exception {
        return AppUtils.formatBigDecimal(folhaPagamentoManager.totalFolha(data));
    }

    /**
     * Retorna o número total de empregados cadastrados no sistema.
     * @return O número de empregados como uma String.
     */
    public String getNumeroDeEmpregados() {
        return String.valueOf(mainManager.getEmpregadoManager().getNumeroDeEmpregados());
    }
}