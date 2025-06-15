package com.busca.servidores;

import com.busca.model.Artigo;
import com.busca.util.BuscadorKMP;
import com.busca.util.CarregadorArtigos;

import java.io.IOException;
import java.io.ObjectInputStream; // Adicionado
import java.io.ObjectOutputStream; // Adicionado
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorB {

    private static final int PORT = 12346;
    private static final String DATA_FILE = "src/main/resources/arxiv_data_part1.json";

    private static List<Artigo> artigosBaseDados;

    public static void main(String[] args) {
        System.out.println("Servidor B: Iniciando...");

        artigosBaseDados = CarregadorArtigos.carregar(DATA_FILE);
        if (artigosBaseDados.isEmpty()) {
            System.err.println("Servidor B: Não foi possível carregar artigos. Encerrando.");
            return;
        }
        System.out.println("Servidor B: Carregados " + artigosBaseDados.size() + " artigos do arquivo " + DATA_FILE);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor B: Escutando na porta " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Servidor B: Conexão aceita de " + clientSocket.getInetAddress().getHostAddress());

                // Agora chama o método de tratamento de conexão real
                executor.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Servidor B: Erro ao iniciar ou aceitar conexão: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Lida com uma conexão de cliente individual.
     * Recebe a busca, executa e envia os resultados.
     */
    private static void handleClientConnection(Socket clientSocket) {
        try (
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            String termoBusca = (String) ois.readObject();
            System.out.println("Servidor B: Recebida busca por: '" + termoBusca + "'");

            List<Artigo> resultados = new ArrayList<>();
            for (Artigo artigo : artigosBaseDados) {
                boolean foundInTitle = BuscadorKMP.search(artigo.getTitle(), termoBusca).isEmpty() ? false : true;
                boolean foundInAbstract = BuscadorKMP.search(artigo.getAbstractText(), termoBusca).isEmpty() ? false : true;

                if (foundInTitle || foundInAbstract) {
                    resultados.add(artigo);
                }
            }

            System.out.println("Servidor B: Encontrados " + resultados.size() + " resultados para '" + termoBusca + "'");

            oos.writeObject(resultados);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Servidor B: Erro na comunicação com o cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Servidor B: Erro ao fechar o socket do cliente: " + e.getMessage());
            }
        }
    }
}
