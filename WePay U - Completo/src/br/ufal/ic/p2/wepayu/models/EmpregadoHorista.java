package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe que representa um Empregado do tipo Horista.
 * <p>
 * Esta é uma subclasse de {@link Empregado} e define o comportamento específico
 * para funcionários que são remunerados com base nas horas trabalhadas, possuindo
 * um salário-hora.
 * </p>
 */
public class EmpregadoHorista extends Empregado {
    private BigDecimal salarioHora;

    /**
     * Construtor padrão (sem argumentos).
     */
    public EmpregadoHorista() {
        super();
    }

    /**
     * Construtor para inicializar um empregado horista com seus dados básicos.
     *
     * @param id          O identificador único do empregado.
     * @param nome        O nome do empregado.
     * @param endereco    O endereço do empregado.
     * @param salarioHora O valor do salário por hora do empregado.
     */
    public EmpregadoHorista(String id, String nome, String endereco, BigDecimal salarioHora) {
        super(id, nome, endereco);
        // Garante que o salário seja armazenado com 2 casas decimais e arredondamento bancário
        this.salarioHora = salarioHora.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Retorna o tipo do empregado.
     *
     * @return A string "horista".
     */
    @Override
    public String getTipo() {
        return "horista";
    }

    /**
     * Retorna o salário base do empregado, que, para um horista, é o seu salário por hora.
     *
     * @return O valor do salário por hora.
     */
    @Override
    public BigDecimal getSalario() {
        return salarioHora;
    }

    /**
     * Define o valor do salário por hora do empregado.
     *
     * @param salarioHora O novo valor do salário por hora.
     */
    public void setSalarioHora(BigDecimal salarioHora) {
        this.salarioHora = salarioHora;
    }


    /**
     * Cria e retorna uma cópia profunda (clone) deste objeto EmpregadoHorista.
     * <p>
     * A clonagem inclui todos os atributos da superclasse {@link Empregado}.
     * </p>
     *
     * @return Uma nova instância de {@link EmpregadoHorista} que é uma cópia exata do original.
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