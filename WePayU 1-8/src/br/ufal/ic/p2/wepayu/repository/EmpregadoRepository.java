package br.ufal.ic.p2.wepayu.repository;

import br.ufal.ic.p2.wepayu.ExceptionSistema.SistemaEncerradoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.utils.XmlUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repositório responsável por gerenciar os dados de {@link Empregado}.
 * Implementa o padrão Singleton e utiliza o padrão Memento para permitir rollback do estado.
 */
public class EmpregadoRepository {
    private static EmpregadoRepository instance;

    /** Armazena os empregados em memória. */
    protected Map<String, Empregado> data = new LinkedHashMap<>();

    /** Nome do arquivo XML usado para persistência. */
    private final String filename = "empregados.xml";

    /** Indica se o sistema foi encerrado. */
    private boolean sistemaEncerrado = false;

    /**
     * Construtor privado para garantir Singleton.
     * Carrega os dados do arquivo XML ao iniciar.
     */
    private EmpregadoRepository() {
        carregarDados();
    }

    /**
     * Obtém a instância única do repositório.
     *
     * @return instância única de {@link EmpregadoRepository}.
     */
    public static synchronized EmpregadoRepository getInstance() {
        if (instance == null) {
            instance = new EmpregadoRepository();
        }
        return instance;
    }

    /**
     * Verifica se o sistema está aberto para modificações.
     *
     * @throws SistemaEncerradoException se o sistema já foi encerrado.
     */
    public void verificarSistemaAberto() throws SistemaEncerradoException {
        if (sistemaEncerrado) {
            throw new SistemaEncerradoException();
        }
    }

    /**
     * Encerra o sistema e salva os dados atuais.
     */
    public void encerrarSistema() {
        salvarDados();
        this.sistemaEncerrado = true;
    }

    /**
     * Define manualmente o status de encerramento do sistema.
     *
     * @param status true para encerrado, false para aberto.
     */
    public void setSistemaEncerrado(boolean status) {
        this.sistemaEncerrado = status;
    }

    /**
     * Classe Memento que armazena o estado do repositório.
     */
    public static class Memento {
        /** Estado salvo dos empregados. */
        private final Map<String, Empregado> state;

        /**
         * Cria um Memento com o estado atual.
         *
         * @param stateToSave mapa de empregados a ser salvo.
         */
        private Memento(Map<String, Empregado> stateToSave) {
            this.state = stateToSave.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue().clone(),
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
        }

        /**
         * Retorna o estado salvo.
         *
         * @return mapa de empregados.
         */
        private Map<String, Empregado> getSavedState() {
            return this.state;
        }
    }

    /**
     * Cria um Memento com o estado atual do repositório.
     *
     * @return objeto {@link Memento} contendo o estado salvo.
     */
    public Memento createMemento() {
        return new Memento(this.data);
    }

    /**
     * Restaura o estado do repositório a partir de um Memento.
     *
     * @param memento estado salvo anteriormente.
     */
    public void setMemento(Memento memento) {
        this.data = memento.getSavedState();
    }

    /**
     * Carrega os dados dos empregados a partir do arquivo XML.
     */
    public void carregarDados() {
        this.data = XmlUtils.carregarDados(filename);
        if (this.data == null) {
            this.data = new LinkedHashMap<>();
        }
    }

    /**
     * Salva os dados atuais dos empregados no arquivo XML.
     */
    public void salvarDados() {
        XmlUtils.salvarDados(filename, this.data);
    }

    /**
     * Remove todos os dados e reinicia o sistema.
     */
    public void zerarDados() {
        this.data.clear();
        this.sistemaEncerrado = false;
        salvarDados();
    }

    /**
     * Obtém todos os empregados.
     *
     * @return mapa de empregados.
     */
    public Map<String, Empregado> getAll() {
        return this.data;
    }

    /**
     * Obtém um empregado pelo ID.
     *
     * @param id identificador do empregado.
     * @return objeto {@link Empregado} ou null se não existir.
     */
    public Empregado getById(String id) {
        return this.data.get(id);
    }

    /**
     * Adiciona um novo empregado ao repositório.
     *
     * @param empregado empregado a ser adicionado.
     */
    public void add(Empregado empregado) {
        String nextId = String.valueOf(getNextId());
        empregado.setId(nextId);
        this.data.put(empregado.getId(), empregado);
    }

    /**
     * Remove um empregado pelo ID.
     *
     * @param id identificador do empregado.
     * @return true se removido com sucesso, false se não existir.
     */
    public boolean remove(String id) {
        if (this.data.containsKey(id)) {
            this.data.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Obtém o próximo ID sequencial para novo empregado.
     *
     * @return próximo ID disponível.
     */
    private int getNextId() {
        if (data.isEmpty()) {
            return 1;
        }
        return data.keySet().stream().mapToInt(Integer::parseInt).max().getAsInt() + 1;
    }
}
