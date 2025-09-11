package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa um empregado do tipo Comissionado.
 * Este tipo de empregado recebe um salário mensal fixo mais uma comissão
 * baseada em suas vendas.
 * Herda os atributos e métodos base da classe {@link Empregado}.
 */
public class EmpregadoComissionado extends Empregado {
    private BigDecimal salarioMensal;
    private BigDecimal comissao;
    private List<ResultadoVenda> resultadosVendas = new ArrayList<>();

    /**
     * Construtor padrão.
     */
    public EmpregadoComissionado() {
        super();
    }

    /**
     * Constrói um EmpregadoComissionado com os detalhes fornecidos.
     *
     * @param id            o identificador único do empregado.
     * @param nome          o nome do empregado.
     * @param endereco      o endereço do empregado.
     * @param salarioMensal o salário mensal fixo do empregado.
     * @param comissao      a taxa de comissão sobre as vendas.
     */
    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal, BigDecimal comissao) {
        super(id, nome, endereco);
        this.salarioMensal = salarioMensal.setScale(2, RoundingMode.HALF_EVEN);
        this.comissao = comissao.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Cria e retorna uma cópia profunda deste objeto {@link EmpregadoComissionado}.
     * A cópia inclui todos os atributos da classe base {@link Empregado}, bem como
     * uma cópia da lista de resultados de vendas.
     *
     * @return um clone deste empregado.
     */
    @Override
    public Empregado clone() {
        EmpregadoComissionado clone = new EmpregadoComissionado(
                this.getId(), this.getNome(), this.getEndereco(), this.getSalario(), this.getComissao()
        );
        super.copiaAtributos(clone);

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
     * @return a string "comissionado".
     */
    @Override
    public String getTipo() {
        return "comissionado";
    }

    /**
     * Retorna o salário base mensal do empregado.
     *
     * @return o valor do salário mensal fixo como {@link BigDecimal}.
     */
    @Override
    public BigDecimal getSalario() {
        return salarioMensal;
    }

    /**
     * Retorna a taxa de comissão do empregado.
     *
     * @return a taxa de comissão como {@link BigDecimal}.
     */
    public BigDecimal getComissao() {
        return comissao;
    }

    /**
     * Define a taxa de comissão do empregado.
     *
     * @param comissao o novo valor da taxa de comissão.
     */
    public void setComissao(BigDecimal comissao) {
        this.comissao = comissao.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Define o salário base mensal do empregado.
     *
     * @param salarioMensal o novo valor do salário mensal fixo.
     */
    public void setSalarioMensal(BigDecimal salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    /**
     * Retorna a lista de resultados de vendas do empregado.
     *
     * @return uma {@link List} de {@link ResultadoVenda}.
     */
    public List<ResultadoVenda> getResultadosVendas() {
        return resultadosVendas;
    }

    /**
     * Define a lista de resultados de vendas do empregado.
     *
     * @param resultadosVendas a nova lista de resultados de vendas.
     */
    public void setResultadosVendas(List<ResultadoVenda> resultadosVendas) {
        this.resultadosVendas = resultadosVendas;
    }
}