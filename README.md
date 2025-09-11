WePayU - Sistema de Folha de Pagamento

Este projeto é um sistema de folha de pagamento em Java, que gerencia empregados, lança cartões de ponto, vendas, taxas de serviço e processa a folha de pagamento.
A aplicação possui funcionalidade de desfazer/refazer (Undo/Redo) e persistência de dados em XML.

🚀 Funcionalidades Principais

O sistema expõe suas operações através da classe Facade:

👥 Gerenciamento de Empregados

criarEmpregado → Cria um novo empregado (horista, assalariado ou comissionado).

removerEmpregado → Remove um empregado existente.

alteraEmpregado → Altera dados do empregado (nome, endereço, tipo, salário, comissão, sindicalização e método de pagamento).

getAtributoEmpregado → Obtém o valor de um atributo específico.

getEmpregadoPorNome → Busca o ID de um empregado pelo nome.

⏱️ Lançamentos

lancaCartao → Registra cartão de ponto (horas normais e extras).

lancaVenda → Registra vendas para empregados comissionados.

lancaTaxaServico → Registra taxas de serviço para sindicalizados.

💰 Folha de Pagamento

totalFolha → Calcula o valor total da folha em uma data sem efetuar os pagamentos.

rodaFolha → Processa a folha de pagamento, gera relatório .txt e atualiza a data do último pagamento.

🔄 Undo/Redo

undo → Desfaz a última alteração no sistema.

redo → Refaz a última alteração desfeita.

💾 Persistência

zerarSistema → Limpa todos os dados.

encerrarSistema → Salva os dados em empregados.xml e encerra o sistema.

🏗️ Estrutura do Código

Facade.java → Ponto de entrada simplificado da API do sistema.

managers/ → Contém os gerenciadores (ex: EmpregadoManager, FolhaPagamentoManager).

models/ → Modelos de dados (Empregado, EmpregadoHorista, EmpregadoAssalariado, EmpregadoComissionado, etc.).

repository/ → EmpregadoRepository (Singleton) que gerencia persistência em XML.

utils/ → Utilitários (AppUtils, XmlUtils).

Exceções personalizadas → Pacotes ExceptionEmpregados, ExceptionPonto, ExceptionServico, ExceptionSistema, ExceptionVendas.

⚙️ Como Executar

Pré-requisitos: Instalar o JDK (Java Development Kit).

Testes de Aceitação:

Executar a classe Main.java.

O projeto usa EasyAccept (easyaccept.jar) para rodar os testes definidos em tests/.

Persistência:

O estado é salvo em empregados.xml.

Relatórios:

Resultados da folha de pagamento são gerados em arquivos .txt nos diretórios ok/ ou na raiz do projeto.

🧪 Exemplo de Teste

O arquivo tests/us6.txt testa a alteração de um empregado.

O resultado esperado está em ok/folha-2005-02-25.txt.

📎 Notas Adicionais

Undo/Redo: Apenas comandos que alteram o estado são salvos no histórico.

Datas: Formato "d/M/uuuu".

Valores Numéricos: Manipulados com BigDecimal para precisão, com vírgula como separador decimal.

🎓 Sobre o Projeto

Projeto desenvolvido para a disciplina Programação Orientada a Objetos (P2) do curso de Ciência da Computação - UFAL.
Professor: Mario Hozano.
