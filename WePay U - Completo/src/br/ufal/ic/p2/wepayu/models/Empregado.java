package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe abstrata que representa um Empregado.
 * <p>
 * Esta classe serve como base para todos os tipos de empregados no sistema (Horista, Assalariado, Comissionado)
 * e encapsula os atributos e comportamentos comuns a todos eles, como identificação, dados pessoais,
 * informações sindicais e método de pagamento.
 * </p>
 * <p>
 * Subclasses são obrigadas a implementar os métodos abstratos {@link #clone()}, {@link #getTipo()} e {@link #getSalario()}.
 * </p>
 */
public abstract class Empregado implements Serializable {
    private String id;
    private String nome;
    private String endereco;
    private boolean sindicalizado;
    private MetodoPagamento metodoPagamento;
    private List<CartaoDePonto> cartoesPonto = new ArrayList<>();
    private String idSindicato;
    private BigDecimal taxaSindical;
    private List<TaxaDeServico> taxasDeServico = new ArrayList<>();
    private LocalDate ultimaDataPagamento;
    private LocalDate dataContratacao;
    private String agendaPagamento;

    /**
     * Construtor padrão.
     * Inicializa o método de pagamento padrão como "Em Mãos".
     */
    public Empregado() {
        this.metodoPagamento = new EmMaos();
    }

    /**
     * Construtor que inicializa os dados básicos de um empregado.
     * Define valores padrão para agenda de pagamento, data de contratação e última data de pagamento
     * com base no tipo concreto da instância do empregado.
     *
     * @param id       O identificador único do empregado.
     * @param nome     O nome do empregado.
     * @param endereco O endereço do empregado.
     */
    public Empregado(String id, String nome, String endereco) {
        this();
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.sindicalizado = false;

        // A data de contratação do horista é definida pelo primeiro cartão de ponto
        if (this instanceof EmpregadoHorista) {
            this.dataContratacao = null;
            this.ultimaDataPagamento = null;
            this.agendaPagamento = "semanal 5";
        } else if (this instanceof EmpregadoComissionado) {
            this.dataContratacao = LocalDate.of(2005, 1, 1);
            this.ultimaDataPagamento = LocalDate.of(2004, 12, 31);
            this.agendaPagamento = "semanal 2 5";
        } else { // Assalariado
            this.dataContratacao = LocalDate.of(2005, 1, 1);
            this.ultimaDataPagamento = LocalDate.of(2004, 12, 31);
            this.agendaPagamento = "mensal $";
        }
    }

    /**
     * Método abstrato para criar uma cópia profunda (clone) do empregado.
     * As subclasses devem implementar esta lógica para criar uma nova instância de seu próprio tipo.
     *
     * @return Uma nova instância de {@link Empregado} que é uma cópia exata do original.
     */
    public abstract Empregado clone();

    /**
     * Método auxiliar protegido para ser usado pelas implementações de {@link #clone()} nas subclasses.
     * Realiza a cópia profunda de todos os atributos comuns definidos nesta classe base
     * para o objeto clone fornecido.
     *
     * @param clone O objeto clone (da subclasse) que receberá os atributos copiados.
     */
    protected void copiaAtributos(Empregado clone) {
        clone.setId(this.getId());
        clone.setNome(this.getNome());
        clone.setEndereco(this.getEndereco());
        clone.setSindicalizado(this.isSindicalizado());
        clone.setIdSindicato(this.getIdSindicato());
        clone.setTaxaSindical(this.getTaxaSindical());
        clone.setDataContratacao(this.getDataContratacao());
        clone.setUltimaDataPagamento(this.getUltimaDataPagamento());
        clone.setAgendaPagamento(this.getAgendaPagamento());

        if (this.getMetodoPagamento() != null) {
            clone.setMetodoPagamento(this.getMetodoPagamento().clone());
        }

        if (this.getCartoesPonto() != null) {
            clone.setCartoesPonto(this.getCartoesPonto().stream().map(CartaoDePonto::clone).collect(Collectors.toList()));
        }
        if (this.getTaxasDeServico() != null) {
            clone.setTaxasDeServico(this.getTaxasDeServico().stream().map(TaxaDeServico::clone).collect(Collectors.toList()));
        }
    }

    // Getters and Setters...

    /**
     * Retorna o ID do empregado.
     * @return O ID do empregado.
     */
    public String getId() { return id; }

    /**
     * Retorna o nome do empregado.
     * @return O nome do empregado.
     */
    public String getNome() { return nome; }

    /**
     * Retorna o endereço do empregado.
     * @return O endereço do empregado.
     */
    public String getEndereco() { return endereco; }

    /**
     * Verifica se o empregado é sindicalizado.
     * @return {@code true} se for sindicalizado, {@code false} caso contrário.
     */
    public boolean isSindicalizado() { return sindicalizado; }

    /**
     * Retorna o método de pagamento do empregado.
     * @return O objeto {@link MetodoPagamento}.
     */
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }

    /**
     * Método abstrato que retorna o tipo do empregado como uma String.
     * @return O tipo do empregado (ex: "horista", "assalariado").
     */
    public abstract String getTipo();

    /**
     * Método abstrato que retorna o salário base do empregado.
     * @return O salário base.
     */
    public abstract BigDecimal getSalario();

    /**
     * Define o ID do empregado.
     * @param id O novo ID.
     */
    public void setId(String id) { this.id = id; }

    /**
     * Define o nome do empregado.
     * @param nome O novo nome.
     */
    public void setNome(String nome) { this.nome = nome; }

    /**
     * Define o endereço do empregado.
     * @param endereco O novo endereço.
     */
    public void setEndereco(String endereco) { this.endereco = endereco; }

    /**
     * Define o status de sindicalização do empregado.
     * @param sindicalizado {@code true} para sindicalizado, {@code false} caso contrário.
     */
    public void setSindicalizado(boolean sindicalizado) { this.sindicalizado = sindicalizado; }

    /**
     * Define o método de pagamento do empregado.
     * @param metodoPagamento O novo {@link MetodoPagamento}.
     */
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    /**
     * Retorna a lista de cartões de ponto do empregado.
     * @return Uma lista de {@link CartaoDePonto}.
     */
    public List<CartaoDePonto> getCartoesPonto() { return cartoesPonto; }

    /**
     * Define a lista de cartões de ponto do empregado.
     * @param cartoesPonto A nova lista de cartões de ponto.
     */
    public void setCartoesPonto(List<CartaoDePonto> cartoesPonto) { this.cartoesPonto = cartoesPonto; }

    /**
     * Retorna o ID do empregado no sindicato.
     * @return O ID do sindicato.
     */
    public String getIdSindicato() { return idSindicato; }

    /**
     * Define o ID do empregado no sindicato.
     * @param idSindicato O novo ID do sindicato.
     */
    public void setIdSindicato(String idSindicato) { this.idSindicato = idSindicato; }

    /**
     * Define a taxa sindical mensal do empregado.
     * @param taxaSindical O valor da taxa sindical.
     */
    public void setTaxaSindical(BigDecimal taxaSindical) { this.taxaSindical = taxaSindical; }

    /**
     * Retorna a taxa sindical mensal do empregado.
     * @return O valor da taxa sindical.
     */
    public BigDecimal getTaxaSindical() { return taxaSindical; }

    /**
     * Retorna a lista de taxas de serviço avulsas do empregado.
     * @return Uma lista de {@link TaxaDeServico}.
     */
    public List<TaxaDeServico> getTaxasDeServico() { return taxasDeServico; }

    /**
     * Define a lista de taxas de serviço avulsas do empregado.
     * @param taxasDeServico A nova lista de taxas de serviço.
     */
    public void setTaxasDeServico(List<TaxaDeServico> taxasDeServico) { this.taxasDeServico = taxasDeServico; }

    /**
     * Retorna a data de contratação do empregado.
     * @return A data de contratação.
     */
    public LocalDate getDataContratacao() { return dataContratacao; }

    /**
     * Define a data de contratação do empregado.
     * @param dataContratacao A nova data de contratação.
     */
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }

    /**
     * Retorna a data do último pagamento recebido pelo empregado.
     * @return A data do último pagamento.
     */
    public LocalDate getUltimaDataPagamento() { return ultimaDataPagamento; }

    /**
     * Define a data do último pagamento recebido pelo empregado.
     * @param ultimaDataPagamento A nova data do último pagamento.
     */
    public void setUltimaDataPagamento(LocalDate ultimaDataPagamento) { this.ultimaDataPagamento = ultimaDataPagamento; }

    /**
     * Retorna a descrição da agenda de pagamento do empregado.
     * @return A agenda de pagamento (ex: "semanal 5").
     */
    public String getAgendaPagamento() { return agendaPagamento; }

    /**
     * Define a agenda de pagamento do empregado.
     * @param agendaPagamento A nova agenda de pagamento.
     */
    public void setAgendaPagamento(String agendaPagamento) { this.agendaPagamento = agendaPagamento; }
}