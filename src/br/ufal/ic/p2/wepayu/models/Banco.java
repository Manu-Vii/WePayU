package br.ufal.ic.p2.wepayu.models;

/**
 * Representa o método de pagamento por depósito em conta bancária.
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
     * Construtor que inicializa os dados da conta bancária.
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     */
    public Banco(String banco, String agencia, String contaCorrente) {
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getContaCorrente() { return contaCorrente; }
    public void setContaCorrente(String contaCorrente) { this.contaCorrente = contaCorrente; }

    /**
     * Cria e retorna uma cópia deste objeto Banco.
     * @return Uma cópia do objeto.
     */
    @Override
    public MetodoPagamento clone() {
        return new Banco(this.banco, this.agencia, this.contaCorrente);
    }
}