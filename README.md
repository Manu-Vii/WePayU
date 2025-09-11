WePayU - Sistema de Folha de Pagamento
Este projeto é um sistema de folha de pagamento em Java, que gerencia empregados, lança cartões de ponto, vendas, taxas de serviço e processa a folha de pagamento. A aplicação suporta funcionalidade de desfazer/refazer e persiste os dados em um arquivo XML.

Funcionalidades Principais
O sistema oferece as seguintes funcionalidades, acessíveis através da classe Facade:

Gerenciamento de Empregados:

criarEmpregado: Cria um novo empregado com diferentes tipos (horista, assalariado, comissionado).

removerEmpregado: Remove um empregado existente.

alteraEmpregado: Altera atributos como nome, endereço, tipo, salário, comissão, sindicalização e método de pagamento.

getAtributoEmpregado: Obtém o valor de um atributo específico do empregado.

getEmpregadoPorNome: Busca o ID de um empregado pelo nome e índice.

Lançamentos:

lancaCartao: Lança um cartão de ponto para empregados horistas, registrando horas normais e extras.

lancaVenda: Lança resultados de venda para empregados comissionados.

lancaTaxaServico: Lança taxas de serviço para empregados sindicalizados.

Folha de Pagamento:

totalFolha: Calcula o valor total da folha de pagamento para uma data específica sem efetuar os pagamentos.

rodaFolha: Processa a folha de pagamento para uma data, gerando um relatório detalhado em um arquivo de saída e atualizando a data do último pagamento dos empregados.

Undo/Redo:

undo: Desfaz o último comando que alterou o estado do sistema.

redo: Refaz o último comando desfeito.

Persistência:

zerarSistema: Limpa todos os dados do sistema.

encerrarSistema: Salva todos os dados em um arquivo XML (empregados.xml) e encerra o sistema.

Estrutura do Código
A arquitetura do projeto segue o padrão Facade, encapsulando a complexidade do sistema em uma interface simplificada.

Facade.java: A classe de fachada que expõe a API do sistema e delega as chamadas para as classes de gerenciamento.

managers/: Contém as classes de gerenciamento (EmpregadoManager, FolhaPagamentoManager, etc.), que implementam a lógica de negócios para suas respectivas responsabilidades.

models/: Contém as classes que representam os modelos de dados, como Empregado, EmpregadoHorista, EmpregadoAssalariado, EmpregadoComissionado e métodos de pagamento.

repository/: A classe EmpregadoRepository é um repositório Singleton que lida com a persistência dos dados dos empregados, salvando e carregando-os de um arquivo XML.

utils/: Contém classes utilitárias, como AppUtils para formatação e análise de dados (BigDecimal e LocalDate), e XmlUtils para serialização e desserialização de dados em XML.

ExceptionEmpregados, ExceptionPonto, ExceptionServico, ExceptionSistema, ExceptionVendas: Pacotes que definem as exceções personalizadas para cada módulo do sistema.

Como Executar
A aplicação utiliza o framework EasyAccept para executar testes de aceitação definidos em arquivos de texto.

Pré-requisitos: Certifique-se de ter o JDK (Java Development Kit) instalado.

Execução: Você pode executar a classe principal Main.java para rodar os testes de aceitação. A classe Main já está configurada para executar os scripts de teste. O arquivo easyaccept.jar é a biblioteca responsável pelo framework de testes.

Persistência: O sistema salva o estado dos empregados em um arquivo chamado empregados.xml.

Relatórios: Os relatórios da folha de pagamento são gerados em arquivos de texto (.txt) nos diretórios ok ou na raiz do projeto, dependendo do teste.

Exemplo de Teste
Os arquivos em tests/ são scripts de teste que demonstram o uso da API e as funcionalidades do sistema, como us6.txt, que testa a alteração de um empregado. O resultado esperado de alguns desses testes está nos arquivos no diretório ok/, como ok/folha-2005-02-25.txt.

Notas Adicionais
Undo/Redo: O sistema mantém um histórico de comandos que alteram o estado para permitir operações de undo e redo. Comandos de consulta (como getAtributoEmpregado) não são salvos no histórico.

Datas: As datas são tratadas com o formato "d/M/uuuu".

Valores Numéricos: Valores monetários são manipulados com BigDecimal para garantir precisão e são formatados com vírgula como separador decimal.

Projeto Relacionado a Matéria de Programação Orientada a Objetos (P2), do curso de Ciências da Computação-UFAL. Professor: Mario Hozano.

=========+++++=========+++++==========+++++++++=============++++++==========
