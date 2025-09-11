package br.ufal.ic.p2.wepayu.managers;

import br.ufal.ic.p2.wepayu.ExceptionEmpregados.*;
import br.ufal.ic.p2.wepayu.ExceptionPonto.IdentificacaoNulaException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.utils.AppUtils;
import java.math.BigDecimal;
import java.util.*;

/**
 * Gerencia a criação, remoção e alteração de empregados.
 * Também lida com a obtenção de atributos de empregados.
 */
public class EmpregadoManager {

    public EmpregadoRepository empregadoRepository = EmpregadoRepository.getInstance();

    /**
     * Zera todos os dados do repositório de empregados.
     */
    public void zerarSistema() {
        empregadoRepository.zerarDados();
    }

    /**
     * Encerra o sistema, salvando os dados.
     */
    public void encerrarSistema() {
        empregadoRepository.salvarDados();
    }

    /**
     * Remove um empregado do repositório.
     * @param id ID do empregado a ser removido.
     * @throws Exception se o ID for nulo, vazio ou o empregado não existir.
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
     * Busca o ID de um empregado pelo nome e índice.
     * @param nome Nome do empregado.
     * @param indice O índice na lista de empregados com o mesmo nome (ordenado por ID).
     * @return O ID do empregado.
     * @throws Exception se não houver empregado com o nome ou índice especificado.
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
     * Cria um novo empregado com os dados fornecidos.
     * @param nome Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param tipo Tipo de empregado.
     * @param salario Salário do empregado.
     * @param comissao Comissão do empregado (opcional).
     * @return O ID do empregado recém-criado.
     * @throws Exception se os dados de entrada forem inválidos.
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
     * Obtém o valor de um atributo de um empregado.
     * @param id ID do empregado.
     * @param atributo Nome do atributo.
     * @return O valor do atributo formatado como String.
     * @throws Exception se o empregado ou atributo não existirem.
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
     * Altera um atributo simples de um empregado.
     * @param id ID do empregado.
     * @param atributo Nome do atributo a ser alterado.
     * @param valor Novo valor do atributo.
     * @throws Exception se o empregado ou atributo não existirem, ou o valor for inválido.
     */
    public void alteraEmpregado(String id, String atributo, String valor) throws Exception {
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
            case "sindicalizado":
                if ("false".equalsIgnoreCase(valor)) {
                    empregado.setSindicalizado(false);
                    empregado.setIdSindicato(null);
                    empregado.setTaxaSindical(null);
                } else if ("true".equalsIgnoreCase(valor)) {
                    throw new DadosBancariosDevemSerFornecidosException();
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
     * @param id ID do empregado.
     * @param atributo Deve ser "sindicalizado".
     * @param valor Novo status (true ou false).
     * @param idSindicato ID do sindicato.
     * @param taxaSindical Taxa sindical.
     * @throws Exception se os dados forem inválidos.
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
     * Altera o método de pagamento para banco.
     * @param id ID do empregado.
     * @param atributo Deve ser "metodoPagamento".
     * @param valor1 Deve ser "banco".
     * @param banco Nome do banco.
     * @param agencia Número da agência.
     * @param contaCorrente Número da conta corrente.
     * @throws Exception se os dados forem inválidos.
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
     * Altera o tipo de empregado.
     * @param id ID do empregado.
     * @param atributo Deve ser "tipo".
     * @param tipo Novo tipo.
     * @param comissaoOuSalario Novo valor para comissão ou salário.
     * @throws Exception se os dados forem inválidos.
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
     * Altera o tipo de empregado, criando uma nova instância e copiando os dados.
     * @param empregado O empregado original.
     * @param tipo O novo tipo de empregado.
     * @param comissaoOuSalario Novo valor para comissão ou salário (opcional).
     * @return A nova instância do empregado.
     * @throws Exception se os dados forem inválidos.
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

        return novoEmpregado; //retornar novo
    }

    /**
     * Altera o salário de um empregado.
     * @param empregado O empregado.
     * @param valor O novo valor do salário.
     * @throws Exception se o valor for inválido.
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
     * Altera a comissão de um empregado comissionado.
     * @param empregado O empregado.
     * @param valor O novo valor da comissão.
     * @throws Exception se o empregado não for comissionado ou o valor for inválido.
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
     * Altera o método de pagamento de um empregado.
     * @param empregado O empregado.
     * @param tipo O tipo de método de pagamento (emMaos, correios, banco).
     * @param banco Nome do banco (se o tipo for "banco").
     * @param agencia Número da agência (se o tipo for "banco").
     * @param contaCorrente Número da conta corrente (se o tipo for "banco").
     * @throws Exception se o tipo for inválido ou os dados bancários estiverem faltando.
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
     * Obtém o número total de empregados no repositório.
     * @return O número de empregados.
     */
    public int getNumeroDeEmpregados() {
        return empregadoRepository.getAll().size();
    }
}