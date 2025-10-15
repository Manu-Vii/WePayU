package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import java.util.Stack;
import java.util.concurrent.Callable;

/**
 * Gerenciador do histórico de comandos que implementa a funcionalidade de desfazer (undo) e refazer (redo).
 * <p>
 * Esta classe utiliza os padrões de projeto Memento e Command. Ela funciona como um invólucro (wrapper)
 * para operações que modificam o estado do {@link EmpregadoRepository}.
 * </p>
 * <p>
 * Antes de cada operação, o estado atual do repositório é salvo em um Memento. Se a operação for bem-sucedida,
 * esse estado anterior é adicionado à pilha de undo. Se falhar, o estado é restaurado, garantindo
 * a atomicidade da operação (ou ela é concluída com sucesso, ou o sistema volta ao estado anterior).
 * </p>
 */
public class CommandHistoryManager {
    private final Stack<EmpregadoRepository.Memento> undoStack = new Stack<>();
    private final Stack<EmpregadoRepository.Memento> redoStack = new Stack<>();
    private final EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Interface funcional que representa um comando a ser executado.
     * Define uma operação que modifica o estado do sistema e não retorna valor.
     */
    @FunctionalInterface
    public interface Command {
        void execute() throws Exception;
    }

    /**
     * Executa um comando que não retorna valor, gerenciando o histórico de undo/redo.
     *
     * @param command A operação a ser executada.
     * @throws Exception se a operação falhar. Neste caso, o estado do sistema é revertido.
     */
    public void execute(Command command) throws Exception {
        empregadoRepository.verificarSistemaAberto(); // Garante que o sistema não está encerrado
        EmpregadoRepository.Memento beforeState = empregadoRepository.createMemento();
        try {
            command.execute();
            undoStack.push(beforeState);
            redoStack.clear(); // Uma nova ação limpa a pilha de redo
        } catch (Exception e) {
            empregadoRepository.setMemento(beforeState); // Restaura o estado em caso de erro
            throw e;
        }
    }

    /**
     * Executa um comando que retorna um valor, gerenciando o histórico de undo/redo.
     *
     * @param <T>     O tipo do valor de retorno.
     * @param command A operação ({@link Callable}) a ser executada.
     * @return O resultado da operação.
     * @throws Exception se a operação falhar. Neste caso, o estado do sistema é revertido.
     */
    public <T> T execute(Callable<T> command) throws Exception {
        empregadoRepository.verificarSistemaAberto(); // Garante que o sistema não está encerrado
        EmpregadoRepository.Memento beforeState = empregadoRepository.createMemento();
        try {
            T result = command.call();
            undoStack.push(beforeState);
            redoStack.clear(); // Uma nova ação limpa a pilha de redo
            return result;
        } catch (Exception e) {
            empregadoRepository.setMemento(beforeState); // Restaura o estado em caso de erro
            throw e;
        }
    }

    /**
     * Desfaz a última operação executada.
     * O estado atual é salvo na pilha de redo antes de restaurar o estado anterior.
     *
     * @throws Exception se não houver operações para desfazer.
     */
    public void undo() throws Exception {
        empregadoRepository.verificarSistemaAberto(); // Garante que o sistema não está encerrado
        if (undoStack.isEmpty()) {
            throw new Exception("Nao ha comando a desfazer.");
        }
        redoStack.push(empregadoRepository.createMemento());
        EmpregadoRepository.Memento previousState = undoStack.pop();
        empregadoRepository.setMemento(previousState);
    }

    /**
     * Refaz a última operação desfeita.
     * O estado atual é salvo na pilha de undo antes de restaurar o estado da pilha de redo.
     *
     * @throws Exception se não houver operações para refazer.
     */
    public void redo() throws Exception {
        empregadoRepository.verificarSistemaAberto(); // Garante que o sistema não está encerrado
        if (redoStack.isEmpty()) {
            throw new Exception("Nao ha comando a refazer.");
        }
        undoStack.push(empregadoRepository.createMemento());
        EmpregadoRepository.Memento nextState = redoStack.pop();
        empregadoRepository.setMemento(nextState);
    }
}