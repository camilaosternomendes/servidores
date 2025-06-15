package com.busca.servidores;

import com.busca.model.Artigo;
import com.busca.util.CarregadorArtigos; 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List; // Para armazenar artigos
import java.util.concurrent.ExecutorService; // Para pool de threads
import java.util.concurrent.Executors;    // Para criar pool de threads

public class ServidorB {

    private static final int PORT = 12346; // Porta do Servidor B
    private static final String DATA_FILE = "src/main/resources/arxiv_data_part1.json";

    private static List<Artigo> artigosBaseDados; // Armazena os artigos carregados

    public static void main(String[] args) {
        System.out.println("Servidor B: Iniciando...");

        artigosBaseDados = CarregadorArtigos.carregar(DATA_FILE);
        if (artigosBaseDados.isEmpty()) {
            System.err.println("Servidor B: Não foi possível carregar artigos. Encerrando.");
            return;
        }
        System.out.println("Servidor B: Carregados " + artigosBaseDados.size() + " artigos do arquivo " + DATA_FILE);

        ExecutorService executor = Executors.newFixedThreadPool(10); // Pool de threads para lidar com clientes

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor B: Escutando na porta " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Servidor B: Conexão aceita de " + clientSocket.getInetAddress().getHostAddress());

                executor.submit(() -> System.out.println("Servidor B: Conexao recebida, aguardando logica de tratamento...")); // Placeholder
            }
        } catch (IOException e) {
            System.err.println("Servidor B: Erro ao iniciar ou aceitar conexão: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

}
