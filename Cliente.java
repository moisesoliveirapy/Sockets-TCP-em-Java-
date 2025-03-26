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

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            System.out.println("Insira o seu nome: ");
            String nome = read.nextLine();
            System.out.println("Insira o login: ");
            String login = read.nextLine();
            System.out.println("Insira a senha: ");
            String senha = read.nextLine();

            // Envia uma mensagem ao servidor
            dos.writeUTF(nome);
            dos.writeUTF(login);
            dos.writeUTF(senha);

            // Recebe resposta do servidor
            String resposta = dis.readUTF();
            System.out.println("Resposta do servidor: " + resposta);

            System.out.println("Insira a linguagem\n1 - pdf\n2 - TXT\n 3 - JPG\n 4 - Listar pastas ");
            String linguagem = read.nextLine();
            // Envia uma mensagem ao servidor
            dos.writeUTF(linguagem);
            // recebe do servidor
            String linguagemEscolhida = dis.readUTF();
            System.out.println(linguagemEscolhida);
            String arquivo = dis.readUTF();
            System.out.println("A pasta do usario contem os seguintes arquivos: "+arquivo);

            /* socket.close(); */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}