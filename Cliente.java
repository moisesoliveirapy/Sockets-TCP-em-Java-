import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

            // Envia o nome, login e senha para o servidor
            dos.writeUTF(nome);
            dos.writeUTF(login);
            dos.writeUTF(senha);

            // Recebe a resposta do servidor
            String resposta = dis.readUTF();
            System.out.println("Resposta do servidor: " + resposta);

            if (resposta.startsWith("Login bem-sucedido")) {
                // Solicita a escolha da linguagem e envia para o servidor
                System.out.println("Insira a linguagem:\n1 - PDF\n2 - TXT\n3 - JPG\n4 - Listar pastas\n5 - Desligar Servidor");
                String linguagem = read.nextLine();
                dos.writeUTF(linguagem);

                // Recebe resposta sobre a linguagem escolhida
                String respostaLinguagem = dis.readUTF();
                System.out.println("Resposta do servidor sobre a linguagem: " + respostaLinguagem);

                if (linguagem.equals("4")) {
                    // Caso a opção seja "Listar pastas", o servidor enviará a lista de arquivos
                    String arquivo;
                    while (!(arquivo = dis.readUTF()).equals("FIM")) {
                        System.out.println("Arquivo encontrado: " + arquivo);
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
}
