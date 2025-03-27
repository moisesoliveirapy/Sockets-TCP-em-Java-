import java.io.*;
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
            // Criar o servidor na porta 12345
            ServerSocket servidor = new ServerSocket(12345);
            System.out.println("Servidor aguardando conexões na porta 12345...");

            // Lista de usuários e senhas
            List<String> users = Arrays.asList("moises1", "moises2", "moises3");
            List<String> passwords = Arrays.asList("123", "1234", "12345");

            // Diretório raiz de armazenamento
            Path armazenamento = Paths.get("armazenamento");
            if (!Files.exists(armazenamento)) {
                Files.createDirectories(armazenamento); // Cria o diretório de armazenamento se não existir
            }

            while (true) { // Mantém o servidor ativo
                Socket conexao = servidor.accept();
                System.out.println("Cliente conectado: " + conexao.getInetAddress());

                // Criar streams de entrada e saída
                DataInputStream dis = new DataInputStream(conexao.getInputStream());
                DataOutputStream dos = new DataOutputStream(conexao.getOutputStream());

                // Ler dados do cliente
                String nome = dis.readUTF();
                String login = dis.readUTF();
                String senha = dis.readUTF();

                boolean autenticado = false;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).equals(login) && passwords.get(i).equals(senha)) {
                        autenticado = true;
                        break;
                    }
                }

                if (autenticado) {
                    System.out.println("Usuário autenticado: " + login);
                    dos.writeUTF("Login bem-sucedido! Bem-vindo, " + nome + "!");

                    // Criar pasta do usuário no diretório de armazenamento
                    Path pastaUsuario = armazenamento.resolve(nome);
                    try {
                        Files.createDirectories(pastaUsuario);
                        System.out.println("Pasta criada para o usuário: " + pastaUsuario);
                    } catch (IOException e) {
                        dos.writeUTF("Erro ao criar diretório para o usuário.");
                        e.printStackTrace();
                    }

                    // Criar subpastas PDF, JPG, TXT
                    criarSubpasta(pastaUsuario, "pdf");
                    criarSubpasta(pastaUsuario, "jpg");
                    criarSubpasta(pastaUsuario, "txt");

                    boolean chave = true;
                    while (chave) {
                        String linguagem = dis.readUTF();

                        switch (linguagem) {
                            case "1":
                                receiveFile(dis, pastaUsuario.resolve("pdf"));
                                break;
                            case "2":
                                receiveFile(dis, pastaUsuario.resolve("txt"));
                                break;
                            case "3":
                                receiveFile(dis, pastaUsuario.resolve("jpg"));
                                break;
                            case "4":
                                listarArquivosRecursivo(pastaUsuario, dos);
                                break;
                            case "5":
                                dos.writeUTF("Encerrando conexão...");
                                chave = false;
                                break;
                            default:
                                dos.writeUTF("Opção inválida. Escolha novamente.");
                        }
                    }
                } else {
                    System.out.println("Falha na autenticação para: " + login);
                    dos.writeUTF("Erro: Usuário ou senha incorretos!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void criarSubpasta(Path pastaUsuario, String subpasta) {
        Path subPastaPath = pastaUsuario.resolve(subpasta);
        try {
            if (!Files.exists(subPastaPath)) {
                Files.createDirectories(subPastaPath);
                System.out.println("Subpasta criada para o usuário: " + subPastaPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listarArquivosRecursivo(Path pasta, DataOutputStream dos) throws IOException {
        File[] arquivos = pasta.toFile().listFiles();
        if (arquivos != null && arquivos.length > 0) {
            dos.writeUTF("Conteúdo do repositório:");
            for (File file : arquivos) {
                if (file.isDirectory()) {
                    listarArquivosRecursivo(file.toPath(), dos);
                } else {
                    dos.writeUTF(file.getAbsolutePath());
                }
            }
        } else {
            dos.writeUTF("Diretório vazio ou erro ao acessar o diretório.");
        }
    }

    private static void receiveFile(DataInputStream dis, Path pastaDestino) throws IOException {
        String nomeArquivo = dis.readUTF();
        long fileSize = dis.readLong();
        Path caminhoArquivo = pastaDestino.resolve(nomeArquivo);

        try (FileOutputStream fos = new FileOutputStream(caminhoArquivo.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = dis.read(buffer, 0, Math.min(buffer.length, (int) (fileSize - totalRead)))) > 0) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            System.out.println("Arquivo recebido com sucesso: " + caminhoArquivo);
        }
    }
}
