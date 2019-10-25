import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        
        System.out.println("Enter a port number");
        Scanner portNumber = new Scanner(System.in);
        int port = Integer.parseInt(portNumber.next());

        // tries to open a server with the given port number
        try (ServerSocket server = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = server.accept();
                System.out.println("New client connected");

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                String command = reader.readLine();
                CommandProcessor commandProcessor = new CommandProcessor();
                String result = commandProcessor.process(command);
                
                System.out.println(result);
                writer.println(result);

                }
                

        } catch (IOException e) {
            System.out.println("\nexception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
