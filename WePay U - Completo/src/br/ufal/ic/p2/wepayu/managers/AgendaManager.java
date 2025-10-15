package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.ExceptionAgenda.AgendaJaExisteException;
import br.ufal.ic.p2.wepayu.ExceptionAgenda.DescricaoAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.utils.XmlUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Gerenciador responsável por todas as operações relacionadas às Agendas de Pagamento.
 * <p>
 * Esta classe controla a criação, validação e persistência de agendas de pagamento,
 * tanto as padrões do sistema quanto as customizadas pelos usuários.
 * </p>
 */
public class AgendaManager {
    private final Set<String> agendasDisponiveis = new HashSet<>();
    private final String filename = "agendas.xml";

    /**
     * Construtor do AgendaManager.
     * Inicializa o gerenciador com as agendas de pagamento padrão e carrega
     * quaisquer agendas customizadas que tenham sido salvas anteriormente.
     */
    public AgendaManager() {
        // Adiciona as agendas padrão do sistema
        agendasDisponiveis.add("semanal 5");
        agendasDisponiveis.add("mensal $");
        agendasDisponiveis.add("semanal 2 5");

        // Carrega agendas customizadas do arquivo XML
        carregarDados();
    }

    /**
     * Carrega as agendas customizadas do arquivo de persistência (XML)
     * e as adiciona ao conjunto de agendas disponíveis.
     */
    private void carregarDados() {
        Set<String> agendasSalvas = XmlUtils.carregarAgendas(filename);
        agendasDisponiveis.addAll(agendasSalvas);
    }

    /**
     * Salva as agendas de pagamento customizadas no arquivo de persistência.
     * As agendas padrão não são salvas para evitar redundância.
     */
    public void salvarDados() {
        // Filtra apenas as agendas que não são padrão
        Set<String> agendasCustomizadas = new HashSet<>(agendasDisponiveis);
        agendasCustomizadas.remove("semanal 5");
        agendasCustomizadas.remove("mensal $");
        agendasCustomizadas.remove("semanal 2 5");
        XmlUtils.salvarAgendas(filename, agendasCustomizadas);
    }

    /**
     * Restaura o estado inicial das agendas de pagamento, mantendo apenas as padrões
     * e removendo o arquivo de persistência de agendas customizadas.
     */
    public void zerarDados() {
        agendasDisponiveis.clear();
        agendasDisponiveis.add("semanal 5");
        agendasDisponiveis.add("mensal $");
        agendasDisponiveis.add("semanal 2 5");

        // Deleta o arquivo de agendas customizadas
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Cria uma nova agenda de pagamentos customizada.
     *
     * @param descricao A descrição da nova agenda (ex: "mensal 10", "semanal 2 6").
     * @throws AgendaJaExisteException se uma agenda com a mesma descrição já existir.
     * @throws DescricaoAgendaInvalidaException se a descrição não seguir o formato esperado.
     */
    public void criarAgendaDePagamentos(String descricao) throws AgendaJaExisteException, DescricaoAgendaInvalidaException {
        validarDescricao(descricao);
        if (!agendasDisponiveis.add(descricao)) {
            throw new AgendaJaExisteException();
        }
    }

    /**
     * Verifica se uma determinada agenda de pagamento está disponível no sistema.
     *
     * @param descricao A descrição da agenda a ser verificada.
     * @return {@code true} se a agenda existe, {@code false} caso contrário.
     */
    public boolean isAgendaDisponivel(String descricao) {
        return agendasDisponiveis.contains(descricao);
    }

    /**
     * Valida a sintaxe e a semântica da descrição de uma agenda de pagamento.
     * <p>
     * Formatos válidos:
     * <ul>
     * <li>"mensal &lt;dia&gt;" (dia 1-28) ou "mensal $"</li>
     * <li>"semanal &lt;dia&gt;" (dia 1-7)</li>
     * <li>"semanal &lt;frequencia&gt; &lt;dia&gt;" (frequencia 1-52, dia 1-7)</li>
     * </ul>
     * </p>
     * @param descricao A descrição da agenda a ser validada.
     * @throws DescricaoAgendaInvalidaException se a descrição for inválida.
     */
    private void validarDescricao(String descricao) throws DescricaoAgendaInvalidaException {
        String[] parts = descricao.split(" ");
        if (parts.length < 1 || parts.length > 3) throw new DescricaoAgendaInvalidaException();

        String tipo = parts[0];

        try {
            if (tipo.equals("mensal")) {
                if (parts.length != 2) throw new DescricaoAgendaInvalidaException();
                if (parts[1].equals("$")) return;
                int dia = Integer.parseInt(parts[1]);
                if (dia < 1 || dia > 28) throw new DescricaoAgendaInvalidaException();
            } else if (tipo.equals("semanal")) {
                if (parts.length == 2) {
                    int dia = Integer.parseInt(parts[1]);
                    if (dia < 1 || dia > 7) throw new DescricaoAgendaInvalidaException();
                } else if (parts.length == 3) {
                    int freq = Integer.parseInt(parts[1]);
                    int dia = Integer.parseInt(parts[2]);
                    if (freq < 1 || freq > 52) throw new DescricaoAgendaInvalidaException();
                    if (dia < 1 || dia > 7) throw new DescricaoAgendaInvalidaException();
                } else {
                    throw new DescricaoAgendaInvalidaException();
                }
            } else {
                throw new DescricaoAgendaInvalidaException();
            }
        } catch (NumberFormatException e) {
            throw new DescricaoAgendaInvalidaException();
        }
    }
}