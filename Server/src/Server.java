import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Server {
    static String PATH_TO_FILES = "Server files";

    public static void main(String[] args) {
        askDirectory();

        createServer();
    }

    private static void createServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(5000, 10, InetAddress.getByName("127.0.0.1"));
            System.out.println("Simple TCP/IP Server Waiting for client on port 5000");

            final ConnectionHandler connectionHandler = new ConnectionHandler(serverSocket, PATH_TO_FILES);
            connectionHandler.start();

            Scanner scanner = new Scanner(System.in);
            while (!scanner.nextLine().equalsIgnoreCase("exit")) {
                // do nothing until exit...(just wait for exit)
            }
            serverSocket.close();
            connectionHandler.join();
            System.out.println("Server finished");
        } catch (IOException ioException) {
            System.out.println("got IOException: " + ioException.getMessage());
        } catch (SecurityException securityException) {
            System.out.println("got SecurityException " + securityException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("got IllegalArgumentException " + illegalArgumentException.getMessage());
        } catch (InterruptedException interruptedException) {
            System.out.println("got InterruptedException " + interruptedException.getMessage());
        }
    }

    private static void askDirectory() {
        System.out.println("Введите путь к файлам");
        Scanner in = new Scanner(System.in);
        String path = in.nextLine();
        while (!Files.exists(Paths.get(path))) {
            System.out.println("Каталог не существует");
            System.out.println("Если хотите выйти введите exit, " +
                    "иначе введите правильный путь к файлам");
            path = in.nextLine();
            if (path.equals("exit")) {
                System.exit(0);
            }
        }
        PATH_TO_FILES = path;
    }
}