package com.busca.servidores;

import com.busca.model.Artigo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*; // Para ExecutorService, Future, Callable

public class ServidorA {

    private static final int PORT = 12345; // Porta para o Servidor A escutar clientes
    private static final String SERVER_B_HOST = "localhost";
    private static final int SERVER_B_PORT = 12346;
    private static final String SERVER_C_HOST = "localhost";
    private static final int SERVER_C_PORT = 12347;

    public static void main(String[] args) {
        System.out.println("Servidor A: Iniciando...");

        // Pool de threads para lidar com múltiplos clientes que se conectam ao Servidor A
        ExecutorService clientHandlerExecutor = Executors.newFixedThreadPool(5); // Ex: 5 clientes simultâneos

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor A: Escutando na porta " + PORT + " por clientes...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Servidor A: Conexão aceita de cliente " + clientSocket.getInetAddress().getHostAddress());
                clientHandlerExecutor.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Servidor A: Erro ao iniciar ou aceitar conexão de cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            clientHandlerExecutor.shutdown(); // Desliga o pool quando o servidor encerrar
        }
    }

    /**
     * Lida com uma conexão de um cliente principal.
     * Recebe a busca do cliente, distribui para B e C, agrega e retorna resultados.
     */
    private static void handleClientConnection(Socket clientSocket) {
        // Pool de threads para as requisições aos servidores B e C
        ExecutorService workerRequestExecutor = Executors.newFixedThreadPool(2); // Duas threads: uma para B, outra para C

        try (
            ObjectInputStream clientOis = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream clientOos = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            // 1. Recebe a String de busca do cliente
            String termoBusca = (String) clientOis.readObject();
            System.out.println("Servidor A: Recebida busca do cliente por: '" + termoBusca + "'");

            // 2. Cria tarefas (Callable) para enviar a busca aos Servidores B e C
            // Callable<List<Artigo>> permite que a tarefa retorne um valor (a lista de artigos)
            Callable<List<Artigo>> taskB = () -> requestSearchResults(SERVER_B_HOST, SERVER_B_PORT, termoBusca, "B");
            Callable<List<Artigo>> taskC = () -> requestSearchResults(SERVER_C_HOST, SERVER_C_PORT, termoBusca, "C");

            // 3. Submete as tarefas ao ExecutorService e obtém objetos Future
            Future<List<Artigo>> futureB = workerRequestExecutor.submit(taskB);
            Future<List<Artigo>> futureC = workerRequestExecutor.submit(taskC);

            List<Artigo> resultadosTotais = new ArrayList<>();

            try {
                // 4. Obtém os resultados das tarefas (bloqueia até que cada uma esteja completa)
                List<Artigo> resultadosB = futureB.get(); // Bloqueia e obtém resultado de B
                if (resultadosB != null) {
                    resultadosTotais.addAll(resultadosB);
                }
                System.out.println("Servidor A: Recebidos " + (resultadosB != null ? resultadosB.size() : 0) + " resultados do Servidor B.");

                List<Artigo> resultadosC = futureC.get(); // Bloqueia e obtém resultado de C
                if (resultadosC != null) {
                    resultadosTotais.addAll(resultadosC);
                }
                System.out.println("Servidor A: Recebidos " + (resultadosC != null ? resultadosC.size() : 0) + " resultados do Servidor C.");

            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Servidor A: Erro ao obter resultados dos servidores B/C: " + e.getMessage());
                e.printStackTrace();
            }

            // 5. Envia a lista total de resultados de volta para o cliente original
            System.out.println("Servidor A: Total de " + resultadosTotais.size() + " resultados agregados. Enviando para o cliente.");
            clientOos.writeObject(resultadosTotais);
            clientOos.flush();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Servidor A: Erro na comunicação com o cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            workerRequestExecutor.shutdown(); // Desliga o pool de threads para requisições a B/C
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Servidor A: Erro ao fechar o socket do cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Método auxiliar para fazer uma requisição de busca a um servidor worker (B ou C).
     */
    private static List<Artigo> requestSearchResults(String host, int port, String termoBusca, String serverName) {
        List<Artigo> resultados = new ArrayList<>();
        // CUIDADO AQUI: ao criar ObjectOutputStream, ele escreve um cabeçalho.
        // O ObjectInputStream do outro lado precisa ser criado ANTES de qualquer readObject().
        // E o ObjectOutputStream precisa ser criado ANTES de qualquer writeObject().
        // A ordem de criação desses streams é crucial quando você está construindo a conexão.
        // No nosso caso: Servidor A (Cliente de B/C) -> envia string, então cria OOS.
        // Servidor B/C (Servidor de A) -> espera string, então cria OIS.
        // Isso está correto com a implementação anterior.

        try (
            Socket workerSocket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(workerSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(workerSocket.getInputStream())
        ) {
            System.out.println("Servidor A: Conectado ao Servidor " + serverName + " em " + host + ":" + port);

            // 1. Envia o termo de busca
            oos.writeObject(termoBusca);
            oos.flush();

            // 2. Recebe a lista de resultados
            resultados = (List<Artigo>) ois.readObject();
            System.out.println("Servidor A: Resultados recebidos do Servidor " + serverName + ".");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Servidor A: Erro ao comunicar com Servidor " + serverName + " em " + host + ":" + port + ": " + e.getMessage());
            //e.printStackTrace(); // Descomente para depuração completa
            return null; // Retorna null em caso de erro para indicar falha na comunicação
        }
        return resultados;
    }
}