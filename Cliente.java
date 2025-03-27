import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        try {
            Scanner read = new Scanner(System.in);
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Conectado ao servidor!");

            // Criando streams de entrada e saída
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Envio de dados para o servidor
            System.out.println("Insira o seu nome: ");
            String nome = read.nextLine();
            System.out.println("Insira o login: ");
            String login = read.nextLine();
            System.out.println("Insira a senha: ");
            String senha = read.nextLine();

            // Envia nome, login e senha para o servidor
            dos.writeUTF(nome);
            dos.writeUTF(login);
            dos.writeUTF(senha);

            // Recebe a resposta do servidor
            String resposta = dis.readUTF();
            System.out.println("Resposta do servidor: " + resposta);

            if (resposta.startsWith("Login bem-sucedido")) {
                boolean continuar = true;

                while (continuar) {
                    System.out.println(
                            "Escolha uma opção:\n1 - Enviar PDF\n2 - Enviar TXT\n3 - Enviar JPG\n4 - Listar pastas\n5 - Baixar arquivo\n6 - Sair");
                    String opcao = read.nextLine();
                    dos.writeUTF(opcao);

                    switch (opcao) {
                        case "1":
                            // Enviar arquivo
                            System.out.println("Digite o caminho do arquivo: ");
                            String caminhoArquivoPdf = read.nextLine();
                            enviarArquivo(caminhoArquivoPdf, dos, ".pdf");
                            break;
                        case "2":
                            // Enviar arquivo
                            System.out.println("Digite o caminho do arquivo: ");
                            String caminhoArquivoTxt = read.nextLine();
                            enviarArquivo(caminhoArquivoTxt, dos, ".txt");
                            break;
                        case "3":
                            // Enviar arquivo
                            System.out.println("Digite o caminho do arquivo: ");
                            String caminhoArquivoPng = read.nextLine();
                            enviarArquivo(caminhoArquivoPng, dos, ".png");
                            break;
                        case "4":
                            // Receber lista de arquivos do servidor
                            System.out.println("Arquivos no servidor:");
                            while (true) {
                                String arquivo = dis.readUTF();
                                if (arquivo.equals("FIM")) {
                                    break; // Finaliza quando receber "FIM"
                                }
                                System.out.println("- " + arquivo);
                            }
                            break;
                        case "5":
                            // Baixar arquivo
                            System.out.println("Digite o nome do arquivo para download: ");
                            String arquivoParaBaixar = read.nextLine();
                            dos.writeUTF(arquivoParaBaixar);
                            receberArquivo(dis, arquivoParaBaixar);
                            break;
                        case "6":
                            System.out.println("Encerrando conexão...");
                            continuar = false;
                            break;
                        default:
                            System.out.println("Opção inválida, tente novamente.");
                            break;
                    }
                }
            }

            // Fechamento de recursos
            socket.close();
            dis.close();
            dos.close();
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar arquivos ao servidor
    private static void enviarArquivo(String caminhoArquivo, DataOutputStream dos, String tipo) {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            System.out.println("Erro: Arquivo não encontrado.");
            return;
        }

        try {
            dos.writeUTF(arquivo.getName()); // Enviar nome do arquivo
            dos.writeLong(arquivo.length()); // Enviar tamanho do arquivo

            FileInputStream fis = new FileInputStream(arquivo);
            byte[] buffer = new byte[4096];
            int bytesLidos;

            // Enviar os dados do arquivo em pedaços
            while ((bytesLidos = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, bytesLidos);
            }

            fis.close();
            System.out.println("Arquivo enviado com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao enviar o arquivo.");
            e.printStackTrace();
        }
    }

    // Método para receber arquivos do servidor
    private static void receberArquivo(DataInputStream dis, String nomeArquivo) {
        try {
            long fileSize = dis.readLong(); // Lê o tamanho do arquivo
            FileOutputStream fos = new FileOutputStream(nomeArquivo);
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) > 0) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            fos.close();
            System.out.println("Arquivo recebido com sucesso: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao receber o arquivo.");
            e.printStackTrace();
        }
    }
}
