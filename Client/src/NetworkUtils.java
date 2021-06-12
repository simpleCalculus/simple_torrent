import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class NetworkUtils {
    private final Socket socket;
    private final PrintWriter socketWriter;
    private final Scanner socketReader;

    NetworkUtils(final Socket socket) throws IOException {
        this.socket = socket;
        socketWriter = new PrintWriter(socket.getOutputStream(), true);
        socketReader = new Scanner(socket.getInputStream());
    }

    /**
     * Метод для скачивания файлов из сервера
     * @param pathToDirectory директорий для сохранение файла
     * @param fileName название файла
     * @param sizeOfFile размер файла
     * @param socket сокет
     * @return - возвращает true если файл успешно загрузился
     */
    public static boolean downloadFile(final String pathToDirectory,
                                       final String fileName,
                                       long sizeOfFile,
                                       final Socket socket){
        String fileOutput = pathToDirectory + "\\" + fileName;
        byte[] aByte = new byte[1024];
        try (FileOutputStream fos = new FileOutputStream(fileOutput)) {
            InputStream is = socket.getInputStream();
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead;
            System.out.print(" 100% : ");
            for (int i = 1; i <= 100; i++) {
                System.out.print(".");
            }
            System.out.println("");
            System.out.print("Progress out of 100% : ");
            long onePresentSize = sizeOfFile / 100;
            long nextPresentSize = sizeOfFile - onePresentSize;
            while ((sizeOfFile > 0) && ((bytesRead = is.read(aByte)) != -1)) {
                bos.write(aByte, 0, bytesRead);
                sizeOfFile -= bytesRead;
                // printing progress bar
                if (sizeOfFile <= nextPresentSize) {
                    nextPresentSize -= onePresentSize;
                    System.out.print(".");
                }
            }
            System.out.println("");
            bos.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void sendStringToServer(final String messageToServer) {
        socketWriter.println(messageToServer);
        socketWriter.flush();
    }

    public String getMessageFromServer() {
        return socketReader.nextLine();
    }
}
