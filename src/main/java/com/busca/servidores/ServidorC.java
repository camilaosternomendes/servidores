package com.busca.servidores;

import com.busca.model.Artigo;
import com.busca.util.BuscadorKMP;
import com.busca.util.CarregadorArtigos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorC { // Mudou para ServidorC

    private static final int PORT = 12347; // *** Mudou para a porta do Servidor C ***
    // Caminho do arquivo de dados para o Servidor C
    private static final String DATA_FILE = "src/main/resources/arxiv_data_part2.json"; // *** Mudou para o arquivo parte 2 ***

    private static List<Artigo> artigosBaseDados;

    public static void main(String[] args) {
        System.out.println("Servidor C: Iniciando..."); // Mudou para Servidor C

        artigosBaseDados = CarregadorArtigos.carregar(DATA_FILE);
        if (artigosBaseDados.isEmpty()) {
            System.err.println("Servidor C: Não foi possível carregar artigos. Encerrando."); // Mudou para Servidor C
            return;
        }
        System.out.println("Servidor C: Carregados " + artigosBaseDados.size() + " artigos do arquivo " + DATA_FILE); // Mudou para Servidor C

        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor C: Escutando na porta " + PORT + "..."); // Mudou para Servidor C

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Servidor C: Conexão aceita de " + clientSocket.getInetAddress().getHostAddress()); // Mudou para Servidor C
                executor.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Servidor C: Erro ao iniciar ou aceitar conexão: " + e.getMessage()); // Mudou para Servidor C
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void handleClientConnection(Socket clientSocket) {
        try (
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            String termoBusca = (String) ois.readObject();
            System.out.println("Servidor C: Recebida busca por: '" + termoBusca + "'"); // Mudou para Servidor C

            List<Artigo> resultados = new ArrayList<>();
            for (Artigo artigo : artigosBaseDados) {
                boolean foundInTitle = BuscadorKMP.search(artigo.getTitle(), termoBusca).isEmpty() ? false : true;
                boolean foundInAbstract = BuscadorKMP.search(artigo.getAbstractText(), termoBusca).isEmpty() ? false : true;

                if (foundInTitle || foundInAbstract) {
                    resultados.add(artigo);
                }
            }

            System.out.println("Servidor C: Encontrados " + resultados.size() + " resultados para '" + termoBusca + "'"); // Mudou para Servidor C

            oos.writeObject(resultados);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Servidor C: Erro na comunicação com o cliente: " + e.getMessage()); // Mudou para Servidor C
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Servidor C: Erro ao fechar o socket do cliente: " + e.getMessage()); // Mudou para Servidor C
            }
        }
    }
}