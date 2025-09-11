package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Representa um empregado do tipo Horista.
 * Este tipo de empregado é remunerado com base em um valor por hora trabalhada.
 * Herda os atributos e métodos base da classe {@link Empregado}.
 */
public class EmpregadoHorista extends Empregado {
    private BigDecimal salarioHora;

    /**
     * Construtor padrão.
     */
    public EmpregadoHorista() {
        super();
    }

    /**
     * Constrói um EmpregadoHorista com os detalhes fornecidos.
     *
     * @param id          o identificador único do empregado.
     * @param nome        o nome do empregado.
     * @param endereco    o endereço do empregado.
     * @param salarioHora o valor do salário por hora do empregado.
     */
    public EmpregadoHorista(String id, String nome, String endereco, BigDecimal salarioHora) {
        super(id, nome, endereco);
        this.salarioHora = salarioHora.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Retorna o tipo do empregado.
     *
     * @return a string "horista".
     */
    @Override
    public String getTipo() {
        return "horista";
    }

    /**
     * Retorna o valor do salário por hora do empregado.
     *
     * @return o valor do salário por hora como {@link BigDecimal}.
     */
    @Override
    public BigDecimal getSalario() {
        return salarioHora;
    }

    /**
     * Define o valor do salário por hora do empregado.
     *
     * @param salarioHora o novo valor do salário por hora.
     */
    public void setSalarioHora(BigDecimal salarioHora) {
        this.salarioHora = salarioHora;
    }


    /**
     * Cria e retorna uma cópia profunda deste objeto {@link EmpregadoHorista}.
     * A cópia inclui todos os atributos da classe base {@link Empregado}.
     *
     * @return um clone deste empregado.
     */
    @Override
    public Empregado clone() {
        EmpregadoHorista clone = new EmpregadoHorista(
                this.getId(), this.getNome(), this.getEndereco(), this.getSalario()
        );
        super.copiaAtributos(clone);
        return clone;
    }
}