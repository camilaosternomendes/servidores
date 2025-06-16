# Sistema de Busca Distribuída de Artigos Científicos

## Visão Geral do Projeto

Este projeto implementa um sistema de busca distribuída para artigos científicos utilizando comunicação por Sockets em Java 17. O sistema é composto por três servidores (A, B e C) e um cliente. Dois dos servidores (B e C) são responsáveis por armazenar e realizar a busca em partes distintas de um grande conjunto de dados de artigos científicos do arXiv, enquanto um terceiro servidor (A) atua como orquestrador, recebendo as solicitações do cliente, distribuindo-as e agregando os resultados.

O objetivo principal é demonstrar a comunicação entre processos via sockets para construir uma aplicação distribuída de busca de dados.

## Arquitetura

O sistema segue uma arquitetura cliente-servidor distribuída com um orquestrador:

* **Cliente:** Interface de linha de comando que solicita a busca ao Servidor A.
* **Servidor A (Orquestrador):**
    * Recebe a string de busca do Cliente.
    * Conecta-se aos Servidores B e C *em paralelo*.
    * Envia a string de busca para B e C.
    * Recebe os resultados de B e C.
    * Agrega os resultados em uma única lista.
    * Envia a lista agregada de artigos de volta ao Cliente.
* **Servidor B & C (Servidores de Busca):**
    * Cada um é responsável por uma metade distinta do dataset de artigos.
    * Escutam por requisições de busca (do Servidor A).
    * Realizam a busca localmente em seu conjunto de dados.
    * Retornam todos os artigos correspondentes.

A comunicação entre todos os componentes (Cliente <-> Servidor A <-> Servidor B/C) é feita estritamente via Sockets, utilizando serialização de objetos Java (`ObjectOutputStream` e `ObjectInputStream`) para troca de dados como Strings e Listas de Artigos.

## Funcionalidades

* **Busca Distribuída:** A carga de busca é dividida entre os Servidores B e C.
* **Algoritmo KMP:** Utiliza o algoritmo Knuth-Morris-Pratt (KMP) para uma busca eficiente de substrings nos títulos e abstracts dos artigos.
* **Busca Case-Insensitive:** A busca ignora a diferença entre maiúsculas e minúsculas.
* **Retorno Completo do Artigo:** Retorna todos os detalhes (título, abstract, label) dos artigos encontrados.
* **Agregação de Resultados:** O Servidor A combina os resultados de múltiplos servidores de busca.
* **Contagem de Resultados:** O cliente exibe o número total de artigos encontrados ao final da lista.

## Tecnologias Utilizadas

* **Java 17:** Linguagem de programação principal.
* **Sockets Java (java.net.\*):** Para a comunicação em rede entre os componentes.
* **Jackson (fasterxml.jackson.\*):** Biblioteca para serialização e deserialização de objetos Java para/de JSON (usado para carregar os dados dos artigos).
* **Algoritmo KMP (Knuth-Morris-Pratt):** Implementação eficiente para busca de padrões (substrings).
* **Thread Pools (java.util.concurrent):** Para gerenciar múltiplas conexões e operações paralelas nos servidores A, B e C.

## Estrutura do Projeto

servidores-busca/
├── .vscode/             # Configurações do VS Code
│   └── settings.json
├── lib/                 # Bibliotecas externas (Jackson JARs)
├── out/                 # Classes compiladas (.class)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── busca/
│       │           ├── model/           # Classes de modelo de dados (Artigo.java)
│       │           │   └── Artigo.java
│       │           ├── util/            # Classes utilitárias (BuscadorKMP, CarregadorArtigos)
│       │           │   ├── BuscadorKMP.java
│       │           │   └── CarregadorArtigos.java
│       │           ├── servidores/      # Implementações dos servidores
│       │           │   ├── ServidorA.java
│       │           │   ├── ServidorB.java
│       │           │   └── ServidorC.java
│       │           └── Cliente.java     # Cliente de linha de comando
│       └── resources/     # Arquivos de dados JSON
│           ├── arxiv_data_part1.json
│           └── arxiv_data_part2.json
└── README.md            # Este arquivo


## Configuração do Ambiente e Instalação

1.  **Pré-requisitos:**
    * JDK 17 ou superior instalado.
    * Visual Studio Code com as "Extension Pack for Java" instaladas, ou uma IDE Java de sua preferência (IntelliJ IDEA, Eclipse).

2.  **Clone ou Baixe o Projeto:**
    * Baixe ou clone este repositório para sua máquina local.

3.  **Baixe as Dependências do Jackson:**
    * Baixe os seguintes arquivos JAR e coloque-os na pasta `lib/` do projeto:
        * `jackson-databind-2.15.2.jar`
        * `jackson-core-2.15.2.jar`
        * `jackson-annotations-2.15.2.jar`
    * Você pode encontrá-los no Maven Central (https://mvnrepository.com/artifact/com.fasterxml.jackson.core/).

4.  **Configure o VS Code (se estiver usando):**
    * Abra a pasta **raiz do projeto** (`servidores-busca`) no VS Code.
    * Certifique-se de que o arquivo `.vscode/settings.json` contenha as seguintes configurações para o classpath:
        ```json
        {
            "java.project.referencedLibraries": [
                "lib/**/*.jar"
            ],
            "java.project.sourcePaths": [
                "src/main/java"
            ],
            "java.project.outputPath": "out"
        }
        ```
    * Reinicie o VS Code após configurar.

5.  **Prepare os Arquivos de Dados:**
    * Coloque seus arquivos JSON de artigos (ex: `arxiv_data_part1.json` e `arxiv_data_part2.json`) dentro da pasta `src/main/resources/`. Cada arquivo será carregado por um servidor de busca diferente (B ou C).

## Como Executar

Para executar o sistema, você precisará iniciar cada componente em sua ordem correta, idealmente em terminais separados.

1.  **Abra 4 terminais separados** (no VS Code, vá em `Terminal > New Terminal`).

2.  **Inicie os Servidores na seguinte ordem (um em cada terminal):**

    * **Terminal 1: Servidor A (porta 12345)**
      ###Windows
      ```bash 
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*;out" com.busca.servidores.ServidorA
        ```
        ###Linux/MacOS
        ```bash
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*:out" com.busca.servidores.ServidorA
        ```
        *Aguarde a mensagem: `Servidor A: Escutando na porta 12345 por clientes...`*
      
    * **Terminal 2: Servidor B (porta 12346)**
         ###Windows
         ```bash 
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*;out" com.busca.servidores.ServidorB
        ```
        ###Linux/MacOS
        ```bash
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*:out" com.busca.servidores.ServidorB
        ```
        *Aguarde a mensagem: `Servidor B: Escutando na porta 12346...`*

    * **Terminal 3: Servidor C (porta 12347)**
         ###Windows
         ```bash 
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*;out" com.busca.servidores.ServidorC
        ```
        ###Linux/MacOS
        ```bash
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*:out" com.busca.servidores.ServidorC
        ```
        *Aguarde a mensagem: `Servidor C: Escutando na porta 12347...`*

3.  **Inicie o Cliente (no quarto terminal):**

    * **Terminal 4: Cliente**
         ###Windows
         ```bash 
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*;out" com.busca.Cliente
        ```
        ###Linux/MacOS
        ```bash
        javac -d out -cp "lib/*" $(find src -name "*.java")
        java -cp "lib\*:out" com.busca.Cliente
        ```
        *Você verá: `Digite sua busca: `*

4.  **Interaja com o Cliente:**
    * Digite um termo de busca (ex: "deep learning", "quantum computing", "eclipsing binaries").
    * Pressione Enter.
    * Observe os resultados exibidos no terminal do Cliente, incluindo o abstract completo e a contagem total no final.
    * Você também pode observar as mensagens de log nos terminais dos servidores, mostrando a comunicação e o processamento das requisições.

## Próximos Passos (Possíveis Melhorias)

* **Interface Web:** Desenvolver um front-end web (e.g., com Spring Boot, como discutido) para substituir o cliente de linha de comando.
* **Tratamento de Falhas:** Implementar mecanismos para lidar com servidores offline ou falhas de comunicação.
* **Otimização da Busca:** Explorar índices invertidos ou outras estruturas de dados para acelerar a busca em datasets muito grandes.
* **Balanceamento de Carga:** Distribuir os dados de forma mais inteligente entre os servidores.
* **Escalabilidade:** Adicionar mais servidores de busca (D, E, etc.).
* **Protocolo de Comunicação:** Definir um protocolo mais robusto ou usar ferramentas como Protocol Buffers.

---

**Participação:**

* Camila Osterno
* Cariny Saldanha
* Breno Gonçalves 
* Ingrid Bonifacio
