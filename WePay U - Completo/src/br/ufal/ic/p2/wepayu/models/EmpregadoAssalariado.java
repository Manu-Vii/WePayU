package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe que representa um Empregado do tipo Assalariado.
 * <p>
 * Esta é uma subclasse de {@link Empregado} e define o comportamento para funcionários
 * que recebem um salário mensal fixo, independentemente das horas trabalhadas.
 * </p>
 */
public class EmpregadoAssalariado extends Empregado {
    private BigDecimal salarioMensal;

    /**
     * Construtor padrão (sem argumentos).
     */
    public EmpregadoAssalariado() {
        super();
    }

    /**
     * Construtor para inicializar um empregado assalariado com seus dados básicos.
     *
     * @param id            O identificador único do empregado.
     * @param nome          O nome do empregado.
     * @param endereco      O endereço do empregado.
     * @param salarioMensal O valor do salário mensal do empregado.
     */
    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal) {
        super(id, nome, endereco);
        // Garante que o salário seja armazenado com 2 casas decimais e arredondamento bancário
        this.salarioMensal = salarioMensal.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Retorna o tipo do empregado.
     *
     * @return A string "assalariado".
     */
    @Override
    public String getTipo() {
        return "assalariado";
    }

    /**
     * Retorna o salário do empregado, que, para um assalariado, é o seu salário mensal fixo.
     *
     * @return O valor do salário mensal.
     */
    @Override
    public BigDecimal getSalario() {
        return salarioMensal;
    }

    /**
     * Define o valor do salário mensal do empregado.
     *
     * @param salarioMensal O novo valor do salário mensal.
     */
    public void setSalarioMensal(BigDecimal salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    /**
     * Cria e retorna uma cópia profunda (clone) deste objeto EmpregadoAssalariado.
     * <p>
     * A clonagem inclui todos os atributos da superclasse {@link Empregado}.
     * </p>
     *
     * @return Uma nova instância de {@link EmpregadoAssalariado} que é uma cópia exata do original.
     */
    @Override
    public Empregado clone() {
        EmpregadoAssalariado clone = new EmpregadoAssalariado(
                this.getId(), this.getNome(), this.getEndereco(), this.getSalario()
        );
        // Chama o método da superclasse para copiar os atributos comuns (sindicato, pagamento, etc.)
        super.copiaAtributos(clone);
        return clone;
    }
}