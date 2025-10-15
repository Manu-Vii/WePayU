package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe que representa um Empregado do tipo Comissionado.
 * <p>
 * Esta subclasse de {@link Empregado} modela funcionários que recebem um salário mensal fixo
 * acrescido de uma comissão sobre as vendas que realizam.
 * </p>
 */
public class EmpregadoComissionado extends Empregado {
    private BigDecimal salarioMensal;
    private BigDecimal comissao;
    private List<ResultadoVenda> resultadosVendas = new ArrayList<>();

    /**
     * Construtor padrão (sem argumentos).
     */
    public EmpregadoComissionado() {
        super();
    }

    /**
     * Construtor para inicializar um empregado comissionado com seus dados básicos.
     *
     * @param id            O identificador único do empregado.
     * @param nome          O nome do empregado.
     * @param endereco      O endereço do empregado.
     * @param salarioMensal O valor do salário mensal fixo.
     * @param comissao      A taxa de comissão sobre as vendas.
     */
    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal, BigDecimal comissao) {
        super(id, nome, endereco);
        this.salarioMensal = salarioMensal.setScale(2, RoundingMode.HALF_EVEN);
        this.comissao = comissao.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Cria e retorna uma cópia profunda (clone) deste objeto EmpregadoComissionado.
     * <p>
     * A clonagem inclui todos os atributos da superclasse e realiza uma cópia profunda
     * da lista de {@link ResultadoVenda} para garantir que o clone seja totalmente independente.
     * </p>
     *
     * @return Uma nova instância de {@link EmpregadoComissionado} que é uma cópia exata do original.
     */
    @Override
    public Empregado clone() {
        EmpregadoComissionado clone = new EmpregadoComissionado(
                this.getId(), this.getNome(), this.getEndereco(), this.getSalario(), this.getComissao()
        );
        // Copia atributos da superclasse (sindicato, pagamento, etc.)
        super.copiaAtributos(clone);

        // Realiza a cópia profunda da lista de resultados de vendas
        if (this.getResultadosVendas() != null) {
            clone.setResultadosVendas(
                    this.getResultadosVendas().stream()
                            .map(ResultadoVenda::clone)
                            .collect(Collectors.toList())
            );
        }
        return clone;
    }

    /**
     * Retorna o tipo do empregado.
     *
     * @return A string "comissionado".
     */
    @Override
    public String getTipo() {
        return "comissionado";
    }

    /**
     * Retorna o salário base do empregado, que, para um comissionado, é o seu salário mensal fixo.
     * Este valor não inclui as comissões.
     *
     * @return O valor do salário mensal fixo.
     */
    @Override
    public BigDecimal getSalario() {
        return salarioMensal;
    }

    /**
     * Retorna a taxa de comissão do empregado.
     *
     * @return A taxa de comissão.
     */
    public BigDecimal getComissao() {
        return comissao;
    }

    /**
     * Define a taxa de comissão do empregado.
     *
     * @param comissao A nova taxa de comissão.
     */
    public void setComissao(BigDecimal comissao) {
        this.comissao = comissao.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Define o salário mensal fixo do empregado.
     *
     * @param salarioMensal O novo salário mensal fixo.
     */
    public void setSalarioMensal(BigDecimal salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    /**
     * Retorna a lista de resultados de vendas associados a este empregado.
     *
     * @return Uma {@link List} de {@link ResultadoVenda}.
     */
    public List<ResultadoVenda> getResultadosVendas() {
        return resultadosVendas;
    }

    /**
     * Define a lista de resultados de vendas para este empregado.
     *
     * @param resultadosVendas A nova lista de {@link ResultadoVenda}.
     */
    public void setResultadosVendas(List<ResultadoVenda> resultadosVendas) {
        this.resultadosVendas = resultadosVendas;
    }
}