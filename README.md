# 📌 WePayU - Sistema de Folha de Pagamento

Este projeto é um **sistema de folha de pagamento em Java**, que gerencia empregados, lança cartões de ponto, vendas, taxas de serviço, processa a folha de pagamento e permite a gestão de agendas de pagamento.
A aplicação possui **Undo/Redo** (desfazer/refazer) e **persistência de dados em XML**.

---

## 🚀 Funcionalidades Principais

O sistema expõe suas operações através da classe **`Facade`**:

### 👥 Gerenciamento de Empregados
- **`criarEmpregado`** → Cria um novo empregado (horista, assalariado ou comissionado).
- **`removerEmpregado`** → Remove um empregado existente.
- **`alteraEmpregado`** → Altera dados do empregado (nome, endereço, tipo, salário, comissão, sindicalização, método de pagamento e agenda de pagamento).
- **`getAtributoEmpregado`** → Obtém o valor de um atributo específico.
- **`getEmpregadoPorNome`** → Busca o ID de um empregado pelo nome.

### ⏱️ Lançamentos
- **`lancaCartao`** → Registra cartão de ponto (horas normais e extras).
- **`lancaVenda`** → Registra vendas para empregados comissionados.
- **`lancaTaxaServico`** → Registra taxas de serviço para sindicalizados.

### 💰 Folha de Pagamento
- **`totalFolha`** → Calcula o valor total da folha em uma data sem efetuar os pagamentos.
- **`rodaFolha`** → Processa a folha de pagamento, gera relatório `.txt` e atualiza a data do último pagamento.

### 🗓️ Agendas de Pagamento
- **`criarAgendaDePagamentos`** → Cria novas agendas de pagamento personalizadas (ex: "mensal 1", "semanal 2 3").
- Por padrão, o sistema utiliza as agendas: `semanal 5` (horistas), `mensal $` (assalariados) e `semanal 2 5` (comissionados).
- Empregados podem alterar sua agenda para qualquer uma das disponíveis no sistema.

### 🔄 Undo/Redo
- **`undo`** → Desfaz a última alteração no sistema.
- **`redo`** → Refaz a última alteração desfeita.

### 💾 Persistência
- **`zerarSistema`** → Limpa todos os dados de empregados e agendas.
- **`encerrarSistema`** → Salva os dados em `empregados.xml` e `agendas.xml` e encerra o sistema.

---

## 🏗️ Estrutura do Código

- **`Facade.java`** → Interface simplificada da API do sistema.
- **`managers/`** → Classes de gerenciamento (ex: `EmpregadoManager`, `FolhaPagamentoManager`, `AgendaManager`).
- **`models/`** → Modelos de dados (`Empregado`, `EmpregadoHorista`, `EmpregadoAssalariado`, `EmpregadoComissionado`, etc.).
- **`repository/`** → `EmpregadoRepository` (Singleton) gerenciando persistência em XML.
- **`utils/`** → Utilitários (`AppUtils`, `XmlUtils`).
- **Exceções personalizadas** → Pacotes `ExceptionAgenda`, `ExceptionEmpregados`, `ExceptionPonto`, `ExceptionServico`, `ExceptionSistema`, `ExceptionVendas`.

---

## ⚙️ Como Executar

1.  **Pré-requisitos**: Instalar o **JDK** (Java Development Kit).
2.  **Testes de Aceitação**:
    -   Executar a classe `Main.java`.
    -   O projeto usa **EasyAccept** (`easyaccept.jar`) para rodar testes definidos em `tests/`.
3.  **Persistência**:
    -   O estado dos empregados é salvo em `empregados.xml`.
    -   As agendas de pagamento personalizadas são salvas em `agendas.xml`.
4.  **Relatórios**:
    -   Resultados da folha de pagamento são gerados em arquivos `.txt`.

---

## 🧪 Exemplo de Teste

-   O arquivo `tests/us10.txt` testa a criação e atribuição de **agendas de pagamento personalizadas**.
-   O arquivo `tests/us6.txt` testa a **alteração de um empregado**.

---

## 📎 Notas Adicionais

-   **Undo/Redo**: Apenas comandos que **alteram o estado** do sistema são salvos no histórico para desfazer/refazer.
-   **Datas**: Formato `"d/M/uuuu"`.
-   **Valores Numéricos**: Manipulados com `BigDecimal` para precisão, com vírgula como separador decimal.

---

## 🎓 Sobre o Projeto

Projeto desenvolvido para a disciplina **Programação Orientada a Objetos (P2)** do curso de **Ciência da Computação - UFAL**.
**Professor:** Mario Hozano.
