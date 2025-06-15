package com.busca; // Este pacote está na raiz do 'com.busca', não em 'util'

import com.busca.model.Artigo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Cliente {

    private static final String SERVER_A_HOST = "localhost";
    private static final int SERVER_A_PORT = 12345; // Porta do Servidor A

    public static void main(String[] args) {
        System.out.println("Cliente iniciado.");
        Scanner scanner = new Scanner(System.in);

        // Usamos try-with-resources para garantir que o socket e os streams sejam fechados
        try (
            Socket socket = new Socket(SERVER_A_HOST, SERVER_A_PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            System.out.println("Conectado ao Servidor A em " + SERVER_A_HOST + ":" + SERVER_A_PORT);

            System.out.print("Digite sua busca: ");
            String termoBusca = scanner.nextLine();

            // 1. Envia o termo de busca para o Servidor A
            System.out.println("Enviando busca para o Servidor A...");
            oos.writeObject(termoBusca);
            oos.flush(); // Garante que a busca seja enviada imediatamente

            // 2. Recebe a lista de artigos do Servidor A
            System.out.println("Aguardando resultados...");
            List<Artigo> resultados = (List<Artigo>) ois.readObject();

// 3. Exibe os resultados
System.out.println("\n--- Resultados da Busca ---");
if (resultados.isEmpty()) {
    System.out.println("Nenhum artigo encontrado para a busca: '" + termoBusca + "'");
} else {
    for (Artigo artigo : resultados) {
        System.out.println(artigo); // Artigo.toString() já formata a saída
    }
    // A linha foi movida para AQUI:
    System.out.println("--- Fim dos resultados ---"); // Opcional: Adicionar uma linha para demarcar o fim
    System.out.println("Total de artigos encontrados: " + resultados.size()); // A nova posição da linha
}

        } catch (IOException e) {
            System.err.println("Erro de conexão ou comunicação com o servidor: " + e.getMessage());
            // e.printStackTrace(); // Descomente para depuração completa
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao deserializar objeto. Classe não encontrada: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            scanner.close(); // Fecha o scanner
            System.out.println("Cliente encerrado.");
        }
    }
}
