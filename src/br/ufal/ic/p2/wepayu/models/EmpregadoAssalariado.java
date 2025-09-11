package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Representa um empregado do tipo Assalariado.
 * Este tipo de empregado recebe um salário mensal fixo.
 * Herda os atributos e métodos base da classe {@link Empregado}.
 */
public class EmpregadoAssalariado extends Empregado {
    private BigDecimal salarioMensal;

    /**
     * Construtor padrão.
     */
    public EmpregadoAssalariado() {
        super();
    }

    /**
     * Constrói um EmpregadoAssalariado com os detalhes fornecidos.
     *
     * @param id            o identificador único do empregado.
     * @param nome          o nome do empregado.
     * @param endereco      o endereço do empregado.
     * @param salarioMensal o salário mensal fixo do empregado.
     */
    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal) {
        super(id, nome, endereco);
        this.salarioMensal = salarioMensal.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Retorna o tipo do empregado.
     *
     * @return a string "assalariado".
     */
    @Override
    public String getTipo() {
        return "assalariado";
    }

    /**
     * Retorna o salário mensal do empregado.
     *
     * @return o valor do salário mensal como {@link BigDecimal}.
     */
    @Override
    public BigDecimal getSalario() {
        return salarioMensal;
    }

    /**
     * Define o salário mensal do empregado.
     *
     * @param salarioMensal o novo valor do salário mensal.
     */
    public void setSalarioMensal(BigDecimal salarioMensal) {
        this.salarioMensal = salarioMensal;
    }


    /**
     * Cria e retorna uma cópia profunda deste objeto {@link EmpregadoAssalariado}.
     * A cópia inclui todos os atributos da classe base {@link Empregado}.
     *
     * @return um clone deste empregado.
     */
    @Override
    public Empregado clone() {
        EmpregadoAssalariado clone = new EmpregadoAssalariado(
                this.getId(), this.getNome(), this.getEndereco(), this.getSalario()
        );
        super.copiaAtributos(clone);
        return clone;
    }
}