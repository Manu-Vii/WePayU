package br.ufal.ic.p2.wepayu.models;

/**
 * Implementação concreta de {@link MetodoPagamento} que representa o pagamento
 * por depósito em conta bancária.
 * <p>
 * Esta classe armazena os dados bancários necessários para realizar o depósito:
 * nome do banco, agência e número da conta corrente.
 * </p>
 */
public class Banco extends MetodoPagamento {
    private String banco;
    private String agencia;
    private String contaCorrente;

    /**
     * Construtor padrão.
     */
    public Banco() {}

    /**
     * Construtor que inicializa o método de pagamento com os dados bancários.
     *
     * @param banco         O nome do banco.
     * @param agencia       O número da agência.
     * @param contaCorrente O número da conta corrente.
     */
    public Banco(String banco, String agencia, String contaCorrente) {
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    /**
     * Retorna o nome do banco.
     *
     * @return O nome do banco.
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Define o nome do banco.
     *
     * @param banco O novo nome do banco.
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna o número da agência.
     *
     * @return O número da agência.
     */
    public String getAgencia() {
        return agencia;
    }

    /**
     * Define o número da agência.
     *
     * @param agencia O novo número da agência.
     */
    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    /**
     * Retorna o número da conta corrente.
     *
     * @return O número da conta corrente.
     */
    public String getContaCorrente() {
        return contaCorrente;
    }

    /**
     * Define o número da conta corrente.
     *
     * @param contaCorrente O novo número da conta corrente.
     */
    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    /**
     * Cria e retorna uma cópia (clone) deste objeto Banco.
     * <p>
     * Garante que os dados bancários sejam copiados para a nova instância.
     * </p>
     *
     * @return Uma nova instância de {@link Banco} com os mesmos dados bancários.
     */
    @Override
    public MetodoPagamento clone() {
        return new Banco(this.banco, this.agencia, this.contaCorrente);
    }
}