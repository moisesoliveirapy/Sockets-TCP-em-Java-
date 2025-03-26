import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(12345);
            System.out.println("Servidor aguardando conexões na porta 12345...");

            // Listas de usuários e senhas
            List<String> users = Arrays.asList("moises1", "moises2", "moises3");
            List<String> passwords = Arrays.asList("123", "1234", "12345");

            while (true) { // Mantém o servidor ativo
                Socket conexao = servidor.accept();
                System.out.println("Cliente conectado: " + conexao.getInetAddress());

                // Criar streams de entrada e saída
                DataInputStream dis = new DataInputStream(conexao.getInputStream());
                DataOutputStream dos = new DataOutputStream(conexao.getOutputStream());

                // Ler mensagem do cliente
                String nome = dis.readUTF();
                String login = dis.readUTF();
                String senha = dis.readUTF();

                boolean autenticado = false;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).equals(login) && passwords.get(i).equals(senha)) {
                        autenticado = true;
                        break;
                    }
                }//autenticação e criacao de pastas do usuario
                String caminhoDiretorio = nome;
                if (autenticado) {
                    System.out.println("Usuario autenticado" + login);
                    dos.writeUTF("Login bem-sucedido! Bem-vindo, " + nome + "!");
                    Path pastaUsuario = Paths.get(nome);
                    String path = nome;
                    File directory = new File(path);
                    if (!Files.exists(pastaUsuario)) {
                        Files.createDirectories(pastaUsuario);
                        System.out.println("Pasta criada para o usuário: " + pastaUsuario);
                    }
                    
                    while (true){
                        String linguagem = dis.readUTF();

                        switch (linguagem){
                            case "1":

                                Path subPastaPdf = pastaUsuario.resolve("PDF");
                                Files.createDirectories(subPastaPdf);
                                System.out.println("Pasta criada para o usuário: " + subPastaPdf);
                                break;
                            case "2":
                                Path subPastaITXT = pastaUsuario.resolve("TXT");
                                Files.createDirectories(subPastaITXT);
                                System.out.println("Pasta 'TXT' criada para o usuário: " + subPastaITXT);
                                break;
                            case "3":
                                dos.writeUTF("PNG");
                                break;
                            case "4":
                            if (directory.isDirectory()) {
                                // Lista todos os arquivos e diretórios no caminho especificado
                                String[] files = directory.list();
                                if (files != null && files.length > 0) {
                                    dos.writeUTF("Conteúdo do repositório:");
                                    for (String file : files) {
                                        dos.writeUTF("teste123"+file);
                                    }
                                }
                            }
                            break;
                            default:
                                dos.writeUTF("Insira uma opção correta");
                        }
                        dos.writeUTF("Caminho: " + caminhoDiretorio);
                    }

                } else {
                    System.out.println("Falha na autenticação para: " + login);
                    dos.writeUTF("Erro: Usuário ou senha incorretos!");
                }
                /*  if (login == users.get(0) && senha == passwords.get(0)) {
                    System.out.println("Senha correta");
                } else if (login == users.get(1) && senha == passwords.get(1)) {
                    System.out.println("Senha correta");
                } else if (login == users.get(2) && senha == passwords.get(2)) {
                    System.out.println("Senha correta");
                } else {
                    System.out.println("Senha incorreta");
                } */
                // Fecha a conexão com o cliente (mas mantém o servidor ativo)
                /*conexao.close();*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}