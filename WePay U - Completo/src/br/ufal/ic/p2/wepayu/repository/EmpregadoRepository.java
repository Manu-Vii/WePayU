package br.ufal.ic.p2.wepayu.repository;

import br.ufal.ic.p2.wepayu.ExceptionSistema.SistemaEncerradoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.utils.XmlUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repositório para gerenciar a coleção de objetos {@link Empregado}.
 * Esta classe implementa o padrão de projeto Singleton para garantir uma única instância
 * em toda a aplicação. Ela é responsável pela persistência (leitura e escrita em XML)
 * e pelo gerenciamento em memória dos dados dos empregados.
 *
 * Também implementa o padrão Memento para salvar e restaurar o estado da coleção de empregados,
 * facilitando funcionalidades como undo/redo.
 */
public class EmpregadoRepository {
    private static EmpregadoRepository instance;
    protected Map<String, Empregado> data = new LinkedHashMap<>();
    private final String filename = "empregados.xml";
    private boolean sistemaEncerrado = false;

    /**
     * Construtor privado para implementar o padrão Singleton.
     * Carrega os dados dos empregados do arquivo XML na inicialização.
     */
    private EmpregadoRepository() {
        carregarDados();
    }

    /**
     * Retorna a instância única (Singleton) do repositório.
     * O método é sincronizado para garantir a segurança em ambientes com múltiplas threads.
     *
     * @return A instância única de {@link EmpregadoRepository}.
     */
    public static synchronized EmpregadoRepository getInstance() {
        if (instance == null) {
            instance = new EmpregadoRepository();
        }
        return instance;
    }

    /**
     * Verifica se o sistema está em estado operacional.
     * Lança uma exceção se o sistema foi encerrado.
     *
     * @throws SistemaEncerradoException se o sistema estiver no estado "encerrado".
     */
    public void verificarSistemaAberto() throws SistemaEncerradoException {
        if (sistemaEncerrado) {
            throw new SistemaEncerradoException();
        }
    }

    /**
     * Salva os dados atuais no arquivo XML e marca o sistema como encerrado.
     * Após esta chamada, operações que modificam o estado podem ser bloqueadas.
     */
    public void encerrarSistema() {
        salvarDados();
        this.sistemaEncerrado = true;
    }

    /**
     * Define o estado operacional do sistema.
     *
     * @param status {@code true} para marcar como encerrado, {@code false} para operacional.
     */
    public void setSistemaEncerrado(boolean status) {
        this.sistemaEncerrado = status;
    }

    /**
     * Classe interna que implementa o padrão Memento.
     * Armazena um snapshot (cópia profunda) do estado do mapa de empregados em um determinado momento.
     */
    public static class Memento {
        private final Map<String, Empregado> state;

        /**
         * Construtor do Memento. Cria uma cópia profunda do estado fornecido
         * para garantir que o estado salvo não seja modificado externamente.
         *
         * @param stateToSave O mapa de empregados a ser salvo.
         */
        private Memento(Map<String, Empregado> stateToSave) {
            this.state = stateToSave.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().clone(), (e1, e2) -> e1, LinkedHashMap::new));
        }

        /**
         * Retorna o estado que foi salvo neste Memento.
         *
         * @return O mapa de empregados do estado salvo.
         */
        private Map<String, Empregado> getSavedState() {
            return this.state;
        }
    }

    /**
     * Cria um Memento contendo um snapshot do estado atual do repositório.
     *
     * @return Um objeto {@link Memento} com o estado atual.
     */
    public Memento createMemento() {
        return new Memento(this.data);
    }

    /**
     * Restaura o estado do repositório a partir de um Memento.
     * O estado do Memento é copiado profundamente para o repositório.
     *
     * @param memento O {@link Memento} do qual o estado será restaurado.
     */
    public void setMemento(Memento memento) {
        this.data = memento.getSavedState().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().clone(), (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Carrega os dados dos empregados do arquivo XML {@code empregados.xml}.
     * Se o arquivo não existir ou estiver vazio, inicializa um novo mapa.
     */
    public void carregarDados() {
        this.data = XmlUtils.carregarDados(filename);
        if (this.data == null) {
            this.data = new LinkedHashMap<>();
        }
    }

    /**
     * Salva o mapa de empregados atual no arquivo XML {@code empregados.xml}.
     */
    public void salvarDados() {
        XmlUtils.salvarDados(filename, this.data);
    }

    /**
     * Limpa todos os dados de empregados, redefine o estado do sistema para aberto
     * e salva o estado vazio no arquivo XML.
     */
    public void zerarDados() {
        this.data.clear();
        this.sistemaEncerrado = false;
        salvarDados();
    }

    /**
     * Retorna todos os empregados cadastrados.
     *
     * @return Um {@link Map} contendo todos os empregados.
     */
    public Map<String, Empregado> getAll() {
        return this.data;
    }

    /**
     * Busca um empregado pelo seu ID.
     *
     * @param id O ID do empregado a ser buscado.
     * @return O objeto {@link Empregado} correspondente, ou {@code null} se não for encontrado.
     */
    public Empregado getById(String id) {
        return this.data.get(id);
    }

    /**
     * Adiciona um novo empregado ao repositório, atribuindo-lhe um novo ID sequencial.
     *
     * @param empregado O objeto {@link Empregado} a ser adicionado.
     */
    public void add(Empregado empregado) {
        String nextId = String.valueOf(getNextId());
        empregado.setId(nextId);
        this.data.put(empregado.getId(), empregado);
    }

    /**
     * Remove um empregado do repositório com base no seu ID.
     *
     * @param id O ID do empregado a ser removido.
     * @return {@code true} se o empregado foi removido com sucesso, {@code false} caso contrário.
     */
    public boolean remove(String id) {
        if (this.data.containsKey(id)) {
            this.data.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Calcula o próximo ID disponível para um novo empregado.
     * O cálculo é feito encontrando o maior ID existente e incrementando-o.
     *
     * @return O próximo ID sequencial como um inteiro.
     */
    private int getNextId() {
        if (data.isEmpty()) {
            return 1;
        }
        return data.keySet().stream().mapToInt(Integer::parseInt).max().getAsInt() + 1;
    }
}