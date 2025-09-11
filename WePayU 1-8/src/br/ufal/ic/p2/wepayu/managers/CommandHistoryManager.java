package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import java.util.Stack;
import java.util.concurrent.Callable;

/**
 * Gerencia o histórico de comandos executados sobre o {@link EmpregadoRepository},
 * permitindo operações de desfazer (undo) e refazer (redo) utilizando o padrão Memento.
 * <p>
 * Cada comando executado cria um estado memento do repositório antes da execução,
 * garantindo que mudanças possam ser revertidas em caso de falha ou se o usuário desejar desfazer a operação.
 * </p>
 */
public class CommandHistoryManager {

    /**
     * Pilha de mementos para desfazer operações.
     */
    private final Stack<EmpregadoRepository.Memento> undoStack = new Stack<>();

    /**
     * Pilha de mementos para refazer operações que foram desfeitas.
     */
    private final Stack<EmpregadoRepository.Memento> redoStack = new Stack<>();

    /**
     * Instância do repositório de empregados utilizado para criar e restaurar estados.
     */
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Interface funcional que representa um comando a ser executado.
     * Permite encapsular qualquer ação que possa lançar uma exceção.
     */
    @FunctionalInterface
    public interface Command {
        /**
         * Executa a ação encapsulada pelo comando.
         *
         * @throws Exception se ocorrer algum erro durante a execução do comando
         */
        void execute() throws Exception;
    }

    /**
     * Executa um comando do tipo {@link Command} e adiciona seu estado anterior
     * à pilha de desfazer, limpando a pilha de refazer.
     *
     * @param command comando a ser executado
     * @throws Exception se ocorrer algum erro durante a execução do comando,
     *                   restaurando o estado anterior do repositório
     */
    public void execute(Command command) throws Exception {
        empregadoRepository.verificarSistemaAberto();
        EmpregadoRepository.Memento beforeState = empregadoRepository.createMemento();
        try {
            command.execute();
            undoStack.push(beforeState);
            redoStack.clear();
        } catch (Exception e) {
            empregadoRepository.setMemento(beforeState);
            throw e;
        }
    }

    /**
     * Executa um comando do tipo {@link Callable} que retorna um resultado
     * e adiciona seu estado anterior à pilha de desfazer, limpando a pilha de refazer.
     *
     * @param <T> tipo do resultado do comando
     * @param command comando a ser executado
     * @return o resultado retornado pelo comando
     * @throws Exception se ocorrer algum erro durante a execução do comando,
     *                   restaurando o estado anterior do repositório
     */
    public <T> T execute(Callable<T> command) throws Exception {
        empregadoRepository.verificarSistemaAberto();
        EmpregadoRepository.Memento beforeState = empregadoRepository.createMemento();
        try {
            T result = command.call();
            undoStack.push(beforeState);
            redoStack.clear();
            return result;
        } catch (Exception e) {
            empregadoRepository.setMemento(beforeState);
            throw e;
        }
    }

    /**
     * Desfaz a última operação executada, restaurando o estado anterior do repositório.
     *
     * @throws Exception se não houver comando para desfazer ou se o sistema estiver encerrado
     */
    public void undo() throws Exception {
        empregadoRepository.verificarSistemaAberto();
        if (undoStack.isEmpty()) {
            throw new Exception("Nao ha comando a desfazer.");
        }
        redoStack.push(empregadoRepository.createMemento());
        EmpregadoRepository.Memento previousState = undoStack.pop();
        empregadoRepository.setMemento(previousState);
    }

    /**
     * Refaz a última operação desfeita, restaurando o estado do repositório correspondente.
     *
     * @throws Exception se não houver comando para refazer ou se o sistema estiver encerrado
     */
    public void redo() throws Exception {
        empregadoRepository.verificarSistemaAberto();
        if (redoStack.isEmpty()) {
            throw new Exception("Nao ha comando a refazer.");
        }
        undoStack.push(empregadoRepository.createMemento());
        EmpregadoRepository.Memento nextState = redoStack.pop();
        empregadoRepository.setMemento(nextState);
    }
}
