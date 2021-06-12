import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * Класс для обслуживания одного подключённого клиента
 */
public class ClientHandler extends Thread {
    private final String PATH_TO_FILES;
    private final Socket client;
    private final ArrayList<String> directoryFilesList;

    ClientHandler(final Socket socket, final String pathToFiles) {
        client = socket;
        PATH_TO_FILES = pathToFiles;
        directoryFilesList = new ArrayList<>();
    }

    public void run() {
        try {
            System.out.println( "The Client "+ client.getInetAddress() + ":" + client.getPort() + " is connected");

            directoryFilesList.addAll(getDirectoryFileNames());

            Scanner in = new Scanner(client.getInputStream());
            PrintWriter pr = new PrintWriter(client.getOutputStream());

            sendFileNamesToClient(pr);

            while (!client.isClosed()) {
                dialogWithClient(in, pr);
            }
        } catch (Exception exception) {
            System.out.println("got Exception: " + exception.getMessage());
        }
    }

    private void sendFileNamesToClient(PrintWriter pr) {
        pr.println(directoryFilesList.size());
        for (int i = 0; i < directoryFilesList.size(); ++i) {
            pr.println(directoryFilesList.get(i));
        }
        pr.flush();
        System.out.println("Send names to client");
    }

    private void dialogWithClient(Scanner in, PrintWriter pr) throws IOException {
        String requestString = in.nextLine();
        System.out.println("Клиент хочет скачать файл " + requestString);
        String path = PATH_TO_FILES + "\\" + requestString;
        System.out.println(FileData.fileData(path));
        pr.println(FileData.fileData(path));
        pr.flush();

        String confirmation = in.nextLine();
        System.out.println(confirmation);
        if (confirmation.equals("YES")) {
            sendFile(path);
        }
        else {
            System.out.println("Клиент отказался скачать файл " + requestString);
        }
    }

    private void sendFile(String path) throws IOException {
        File file = new File(path);
        byte[] buffer = new byte[1024];
        long fileSize = file.length();
        try (FileInputStream fis = new FileInputStream(file)) {
            BufferedInputStream bis = new BufferedInputStream(fis);
            OutputStream os = client.getOutputStream();
            int bytesRead;
            while (fileSize > 0 && ((bytesRead = bis.read(buffer)) != -1)) {
                os.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }
            os.flush();
            System.out.println("Файл отправлен");
        }
    }

    private List<String> getDirectoryFileNames() throws NullPointerException {
        final File dir = Paths.get(PATH_TO_FILES).toFile();
        if (!dir.isDirectory() || dir.listFiles() == null || dir.length() == 0) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isFile)
                .filter(x -> x.length() < (1L << 37))
                .map(File::getName)
                .collect(Collectors.toList());
    }

}
