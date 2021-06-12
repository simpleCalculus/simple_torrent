import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс для обработки подключения клиента к серверу
 *
 * @author Sakhi Narzullozoda
 */
class ConnectionHandler extends Thread {
    private final ServerSocket serverSocket;
    private final String PATH_TO_FILES;


    public ConnectionHandler(final ServerSocket serverSocket, final String pathToFiles) {
        this.serverSocket = serverSocket;
        PATH_TO_FILES = pathToFiles;
    }

    public void run() {
        try {
            while (true) {
                System.out.println("waiting..........");
                final Socket connectedClientSocket = serverSocket.accept();
                System.out.println("main(): " + connectedClientSocket);

                final ClientHandler clientHandler = new ClientHandler(connectedClientSocket, PATH_TO_FILES);
                clientHandler.start();
            }
        } catch (Exception ignored) {

        }
    }
}
