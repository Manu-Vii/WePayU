package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.ExceptionAgenda.AgendaNaoDisponivelException;
import br.ufal.ic.p2.wepayu.ExceptionEmpregados.*;
import br.ufal.ic.p2.wepayu.ExceptionPonto.IdentificacaoNulaException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.utils.AppUtils;
import java.math.BigDecimal;
import java.util.*;

/**
 * Gerenciador central para todas as operações de negócio relacionadas a empregados.
 * <p>
 * Esta classe encapsula a lógica para criar, ler, atualizar e remover (CRUD) empregados,
 * aplicando todas as regras de validação necessárias.
 * </p>
 */
public class EmpregadoManager {

    public EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Limpa todos os dados de empregados do sistema.
     */
    public void zerarSistema() {
        empregadoRepository.zerarDados();
    }

    /**
     * Persiste os dados atuais dos empregados.
     */
    public void encerrarSistema() {
        empregadoRepository.salvarDados();
    }

    /**
     * Remove um empregado do sistema.
     *
     * @param id O ID do empregado a ser removido.
     * @throws IdentificacaoNulaException se o ID for nulo ou vazio.
     * @throws EmpregadoNaoExisteException se nenhum empregado com o ID fornecido for encontrado.
     * @throws Exception para outras exceções.
     */
    public void removerEmpregado(String id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new IdentificacaoNulaException();
        }
        if (!empregadoRepository.remove(id)) {
            throw new EmpregadoNaoExisteException();
        }
    }

    /**
     * Busca o ID de um empregado pelo nome.
     * Como pode haver múltiplos empregados com o mesmo nome, um índice é usado para desambiguação.
     *
     * @param nome   O nome do empregado a ser buscado.
     * @param indice O índice (base 1) do empregado na lista de resultados com o mesmo nome.
     * @return O ID do empregado encontrado.
     * @throws Exception se nenhum empregado for encontrado com o nome e índice especificados.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        Map<String, Empregado> empregados = empregadoRepository.getAll();
        List<Empregado> empregadosComNome = new ArrayList<>();

        for (Empregado empregado : empregados.values()) {
            if (empregado.getNome().equals(nome)) {
                empregadosComNome.add(empregado);
            }
        }

        empregadosComNome.sort(Comparator.comparing(Empregado::getId));

        if (empregadosComNome.size() >= indice) {
            return empregadosComNome.get(indice - 1).getId();
        }

        throw new Exception("Nao ha empregado com esse nome.");
    }

    /**
     * Cria um novo empregado no sistema após validar todos os dados de entrada.
     *
     * @param nome     O nome do empregado.
     * @param endereco O endereço do empregado.
     * @param tipo     O tipo do empregado ("horista", "assalariado", "comissionado").
     * @param salario  O salário base do empregado.
     * @param comissao A taxa de comissão (apenas para comissionados).
     * @return O ID do empregado recém-criado.
     * @throws Exception Lança diversas exceções específicas de validação de dados.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        if (nome == null || nome.isEmpty())
            throw new NaoPodeSerNuloException("Nome");
        if (!nome.matches("[a-zA-Z\\s]+")) {
            throw new NomeInvalidoException();
        }
        if (endereco == null || endereco.isEmpty())
            throw new NaoPodeSerNuloException("Endereco");
        if (salario == null || salario.isEmpty())
            throw new NaoPodeSerNuloException("Salario");
        BigDecimal salarioBigDecimal;
        try {
            salarioBigDecimal = AppUtils.parseBigDecimal(salario);
        } catch (NumberFormatException e) {
            throw new NaoNumericoException("Salario");
        }
        if (salarioBigDecimal.compareTo(BigDecimal.ZERO) < 0)
            throw new NaoNegativoException("Salario");

        Empregado novoEmpregado;
        switch (tipo) {
            case "horista":
                if (comissao != null)
                    throw new TipoNaoAplicavelException();
                novoEmpregado = new EmpregadoHorista(null, nome, endereco, salarioBigDecimal);
                break;
            case "assalariado":
                if (comissao != null)
                    throw new TipoNaoAplicavelException();
                novoEmpregado = new EmpregadoAssalariado(null, nome, endereco, salarioBigDecimal);
                break;
            case "comissionado":
                if (comissao == null) {
                    throw new TipoNaoAplicavelException();
                } else if (comissao.isEmpty()) {
                    throw new ComissaoNaoPodeSerNulaException();
                }
                BigDecimal comissaoBigDecimal;
                try {
                    comissaoBigDecimal = AppUtils.parseBigDecimal(comissao);
                } catch (NumberFormatException e) {
                    throw new ComissaoDeveSerNumericaException();
                }
                if (comissaoBigDecimal.compareTo(BigDecimal.ZERO) < 0)
                    throw new ComissaoDeveSerNaoNegativaException();
                novoEmpregado = new EmpregadoComissionado(null, nome, endereco, salarioBigDecimal, comissaoBigDecimal);
                break;
            default:
                throw new TipoInvalidoException();
        }

        empregadoRepository.add(novoEmpregado);
        return novoEmpregado.getId();
    }

    /**
     * Obtém o valor de um atributo específico de um empregado.
     *
     * @param id       O ID do empregado.
     * @param atributo O nome do atributo desejado.
     * @return O valor do atributo como uma String.
     * @throws EmpregadoNaoExisteException se o empregado não for encontrado.
     * @throws Exception                   se o atributo não existir ou não for aplicável.
     */
    public String getAtributoEmpregado(String id, String atributo) throws EmpregadoNaoExisteException, Exception {
        if (id == null || id.isEmpty())
            throw new IdentificacaoNulaException();
        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null)
            throw new EmpregadoNaoExisteException();

        return switch (atributo) {
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo();
            case "salario" -> AppUtils.formatBigDecimal(empregado.getSalario());
            case "sindicalizado" -> String.valueOf(empregado.isSindicalizado());
            case "agendaPagamento" -> empregado.getAgendaPagamento();
            case "comissao" -> {
                if (empregado instanceof EmpregadoComissionado) {
                    yield AppUtils.formatBigDecimal(((EmpregadoComissionado) empregado).getComissao());
                }
                throw new EmpregadoNaoEhComissionadoException();
            }
            case "metodoPagamento" -> {
                MetodoPagamento metodo = empregado.getMetodoPagamento();
                if (metodo instanceof EmMaos) yield "emMaos";
                if (metodo instanceof Correios) yield "correios";
                if (metodo instanceof Banco) yield "banco";
                yield "";
            }
            case "banco", "agencia", "contaCorrente" -> {
                if (empregado.getMetodoPagamento() instanceof Banco banco) {
                    yield switch (atributo) {
                        case "banco" -> banco.getBanco();
                        case "agencia" -> banco.getAgencia();
                        case "contaCorrente" -> banco.getContaCorrente();
                        default -> "";
                    };
                }
                throw new EmpregadoNaoRecebeEmBancoException();
            }
            case "idSindicato", "taxaSindical" -> {
                if (empregado.isSindicalizado()) {
                    yield switch (atributo) {
                        case "idSindicato" -> empregado.getIdSindicato();
                        case "taxaSindical" -> AppUtils.formatBigDecimal(empregado.getTaxaSindical());
                        default -> "";
                    };
                }
                throw new EmpregadoNaoEhSindicalizadoException();
            }
            default -> throw new AtributoNaoExisteException();
        };
    }

    /**
     * Altera um atributo de um empregado. (Sobrecarga principal)
     * @param id O ID do empregado.
     * @param atributo O atributo a ser alterado.
     * @param valor O novo valor para o atributo.
     * @param agendaManager O gerenciador de agendas para validação.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String id, String atributo, String valor, AgendaManager agendaManager) throws Exception {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) throw new EmpregadoNaoExisteException();

        switch (atributo.toLowerCase()) {
            case "nome":
                if (valor == null || valor.isEmpty()) throw new NaoPodeSerNuloException("Nome");
                empregado.setNome(valor);
                break;
            case "endereco":
                if (valor == null || valor.isEmpty()) throw new NaoPodeSerNuloException("Endereco");
                empregado.setEndereco(valor);
                break;
            case "salario":
                alterarSalario(empregado, valor);
                break;
            case "comissao":
                alterarComissao(empregado, valor);
                break;
            case "metodopagamento":
                alterarMetodoPagamento(empregado, valor, null, null, null);
                break;
            case "agendapagamento":
                if (!agendaManager.isAgendaDisponivel(valor)) {
                    throw new AgendaNaoDisponivelException();
                }
                empregado.setAgendaPagamento(valor);
                break;
            case "sindicalizado":
                if ("false".equalsIgnoreCase(valor)) {
                    empregado.setSindicalizado(false);
                    empregado.setIdSindicato(null);
                    empregado.setTaxaSindical(null);
                } else if ("true".equalsIgnoreCase(valor)) {
                    throw new Exception(" sindicalizado deve ser true ou false.");
                } else {
                    throw new ValorTrueOrFalseException();
                }
                break;
            case "tipo":
                Empregado novoEmpregado = alterarTipo(empregado, valor, null);
                empregadoRepository.getAll().put(id, novoEmpregado);
                break;
            default:
                throw new AtributoNaoExisteException();
        }
        empregadoRepository.salvarDados();
    }

    /**
     * Altera o status de sindicalização de um empregado.
     * @param id O ID do empregado.
     * @param atributo Deve ser "sindicalizado".
     * @param valor O novo status de sindicalização.
     * @param idSindicato O ID do membro no sindicato (se valor for true).
     * @param taxaSindical A taxa sindical (se valor for true).
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String id, String atributo, boolean valor, String idSindicato, String taxaSindical) throws Exception {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) throw new EmpregadoNaoExisteException();
        if (!"sindicalizado".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();

        if (valor) {
            if (idSindicato == null || idSindicato.isEmpty()) throw new IdSindicatoNaoPodeSerNulaException();
            if (taxaSindical == null || taxaSindical.isEmpty()) throw new TaxaSindicalNaoPodeSerNulaException();
            BigDecimal taxa = null;
            try {
                taxa = AppUtils.parseBigDecimal(taxaSindical);
                if (taxa.compareTo(BigDecimal.ZERO) < 0) throw new TaxaSindicalDeveSerNaoNegativaException();
            } catch (NumberFormatException e) {
                throw new TaxaSindicalDeveSerNumericaException();
            }
            for (Empregado e : empregadoRepository.getAll().values()) {
                if (e.getIdSindicato() != null && e.getIdSindicato().equals(idSindicato) && !e.getId().equals(id)) {
                    throw new IdentificacaoSindicatoJaExisteException();
                }
            }
            empregado.setSindicalizado(true);
            empregado.setIdSindicato(idSindicato);
            empregado.setTaxaSindical(taxa);
        } else {
            empregado.setSindicalizado(false);
            empregado.setIdSindicato(null);
            empregado.setTaxaSindical(null);
        }
        empregadoRepository.salvarDados();
    }

    /**
     * Altera o método de pagamento para "banco".
     * @param id O ID do empregado.
     * @param atributo Deve ser "metodoPagamento".
     * @param valor1 Deve ser "banco".
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String id, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) throw new EmpregadoNaoExisteException();
        if (!"metodoPagamento".equalsIgnoreCase(atributo) || !"banco".equalsIgnoreCase(valor1)) throw new MetodoPagamentoInvalidoException();

        alterarMetodoPagamento(empregado, valor1, banco, agencia, contaCorrente);
        empregadoRepository.salvarDados();
    }

    /**
     * Altera o tipo de um empregado (ex: de horista para comissionado).
     * @param id O ID do empregado.
     * @param atributo Deve ser "tipo".
     * @param tipo O novo tipo do empregado.
     * @param comissaoOuSalario A nova comissão ou salário, dependendo da mudança.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String id, String atributo, String tipo, String comissaoOuSalario) throws Exception {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado empregado = empregadoRepository.getById(id);
        if (empregado == null) throw new EmpregadoNaoExisteException();
        if (!"tipo".equalsIgnoreCase(atributo)) throw new AtributoNaoExisteException();

        Empregado novoEmpregado = alterarTipo(empregado, tipo, comissaoOuSalario);
        empregadoRepository.getAll().put(id, novoEmpregado);

        empregadoRepository.salvarDados();
    }

    /**
     * Lógica interna para alterar o tipo de um empregado.
     * Cria uma nova instância do tipo correto e copia todos os atributos do antigo.
     */
    private Empregado alterarTipo(Empregado empregado, String tipo, String comissaoOuSalario) throws Exception {
        BigDecimal salarioAtual = empregado.getSalario();
        String idAtual = empregado.getId();

        Empregado novoEmpregado;
        switch (tipo.toLowerCase()) {
            case "horista":
                BigDecimal salarioHorista = salarioAtual;
                if (comissaoOuSalario != null && !comissaoOuSalario.isEmpty()) {
                    try {
                        salarioHorista = AppUtils.parseBigDecimal(comissaoOuSalario);
                        if (salarioHorista.compareTo(BigDecimal.ZERO) < 0) {
                            throw new NaoNegativoException("Salario");
                        }
                    } catch (NumberFormatException e) {
                        throw new NaoNumericoException("Salario");
                    }
                }
                novoEmpregado = new EmpregadoHorista(idAtual, empregado.getNome(), empregado.getEndereco(), salarioHorista);
                break;
            case "assalariado":
                BigDecimal salarioAssalariado = salarioAtual;
                if (comissaoOuSalario != null && !comissaoOuSalario.isEmpty()) {
                    try {
                        salarioAssalariado = AppUtils.parseBigDecimal(comissaoOuSalario);
                        if (salarioAssalariado.compareTo(BigDecimal.ZERO) < 0) {
                            throw new NaoNegativoException("Salario");
                        }
                    } catch (NumberFormatException e) {
                        throw new NaoNumericoException("Salario");
                    }
                }
                novoEmpregado = new EmpregadoAssalariado(idAtual, empregado.getNome(), empregado.getEndereco(), salarioAssalariado);
                break;
            case "comissionado":
                if (comissaoOuSalario == null || comissaoOuSalario.isEmpty()) throw new ComissaoNaoPodeSerNulaException();
                BigDecimal comissaoBigDecimal;
                try {
                    comissaoBigDecimal = AppUtils.parseBigDecimal(comissaoOuSalario);
                    if (comissaoBigDecimal.compareTo(BigDecimal.ZERO) < 0) throw new ComissaoDeveSerNaoNegativaException();
                } catch (NumberFormatException e) {
                    throw new ComissaoDeveSerNumericaException();
                }
                novoEmpregado = new EmpregadoComissionado(idAtual, empregado.getNome(), empregado.getEndereco(), salarioAtual, comissaoBigDecimal);
                break;
            default:
                throw new TipoInvalidoException();
        }

        novoEmpregado.setSindicalizado(empregado.isSindicalizado());
        novoEmpregado.setIdSindicato(empregado.getIdSindicato());
        novoEmpregado.setTaxaSindical(empregado.getTaxaSindical());
        novoEmpregado.setMetodoPagamento(empregado.getMetodoPagamento());
        novoEmpregado.setCartoesPonto(empregado.getCartoesPonto());
        novoEmpregado.setTaxasDeServico(empregado.getTaxasDeServico());
        novoEmpregado.setAgendaPagamento(empregado.getAgendaPagamento());

        return novoEmpregado;
    }

    /**
     * Lógica interna para alterar o salário de um empregado.
     */
    private void alterarSalario(Empregado empregado, String valor) throws Exception {
        if (valor == null || valor.isEmpty()) throw new NaoPodeSerNuloException("Salario");
        BigDecimal salario;
        try {
            salario = AppUtils.parseBigDecimal(valor);
            if (salario.compareTo(BigDecimal.ZERO) < 0) throw new NaoNegativoException("Salario");
        } catch (NumberFormatException e) {
            throw new NaoNumericoException("Salario");
        }
        if (empregado instanceof EmpregadoHorista) {
            ((EmpregadoHorista) empregado).setSalarioHora(salario);
        } else if (empregado instanceof EmpregadoAssalariado) {
            ((EmpregadoAssalariado) empregado).setSalarioMensal(salario);
        } else if (empregado instanceof EmpregadoComissionado) {
            ((EmpregadoComissionado) empregado).setSalarioMensal(salario);
        }
    }

    /**
     * Lógica interna para alterar a comissão de um empregado comissionado.
     */
    private void alterarComissao(Empregado empregado, String valor) throws Exception {
        if (!(empregado instanceof EmpregadoComissionado)) throw new EmpregadoNaoEhComissionadoException();
        if (valor == null || valor.isEmpty()) throw new ComissaoNaoPodeSerNulaException();
        BigDecimal comissao;
        try {
            comissao = AppUtils.parseBigDecimal(valor);
            if (comissao.compareTo(BigDecimal.ZERO) < 0) throw new ComissaoDeveSerNaoNegativaException();
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }
        ((EmpregadoComissionado) empregado).setComissao(comissao);
    }

    /**
     * Lógica interna para alterar o método de pagamento de um empregado.
     */
    private void alterarMetodoPagamento(Empregado empregado, String tipo, String banco, String agencia, String contaCorrente) throws Exception {
        if ("emMaos".equalsIgnoreCase(tipo)) {
            empregado.setMetodoPagamento(new EmMaos());
        } else if ("correios".equalsIgnoreCase(tipo)) {
            empregado.setMetodoPagamento(new Correios());
        } else if ("banco".equalsIgnoreCase(tipo)) {
            if (banco == null || banco.isEmpty()) throw new NaoPodeSerNuloException("Banco");
            if (agencia == null || agencia.isEmpty()) throw new NaoPodeSerNuloException("Agencia");
            if (contaCorrente == null || contaCorrente.isEmpty()) throw new NaoPodeSerNuloException("Conta corrente");
            empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
        } else {
            throw new MetodoPagamentoInvalidoException();
        }
    }

    /**
     * Retorna o número total de empregados cadastrados.
     * @return O número de empregados.
     */
    public int getNumeroDeEmpregados() {
        return empregadoRepository.getAll().size();
    }
}