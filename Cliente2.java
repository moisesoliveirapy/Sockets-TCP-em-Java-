import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente2 {

    public static void main(String[] args) {
        try {
            Scanner read = new Scanner(System.in);
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Conectado ao servidor!");

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            System.out.println("Insira o login: ");
            String login = read.nextLine();
            System.out.println("Insira a senha: ");
            String senha = read.nextLine();
            // Envia uma mensagem ao servidor
            dos.writeUTF(login);
            dos.writeUTF(senha);

            // Recebe resposta do servidor
            String resposta = dis.readUTF();
            System.out.println("Resposta do servidor: " + resposta);

            /*socket.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}