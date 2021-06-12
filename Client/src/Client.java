import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static final String AVAILABLE_COMMANDS = "\tВсе выполняемые команды\n" +
            "\tЕсли хотите выйти введите exit\n" +
            "\tЕсли хотите загружать файл введите download\n" +
            "\tЕсли хотите посмотреть ранее загруженные файлы введите view-local\n" +
            "\tЕсли хотите посмотреть доступные на сервере файлы введите view-server\n";

    public static void main(String[] args) throws InterruptedException {
        ClientDialog clientDialog = new ClientDialog(new Scanner(System.in));

        String[] hostPort = clientDialog.askHostAndPort();

        if (hostPort != null) {
            final DownloadedFiles downloadedFiles = new DownloadedFiles();

            startClientActivity(hostPort[0], Integer.parseInt(hostPort[1]), downloadedFiles);
        }
    }

    /**
     *
     * @param host Хост сервера
     * @param port порт сервера
     * @param downloadedFiles класс для просмотра скачанных файлов и для добавление нового файла
     *                        в список скачаных файлов
     */
    public static void startClientActivity(final String host, int port, DownloadedFiles downloadedFiles) {
        try (Socket socket = new Socket(host, port)) {
            ClientDialog clientDialog = new ClientDialog(new Scanner(System.in));
            ClientCommand clientCommand;
            NetworkUtils networkUtils = new NetworkUtils(socket);

            final List<String> serverFileNames = readFileNames(networkUtils);
            showServerFiles(serverFileNames);

            System.out.println(AVAILABLE_COMMANDS);
            do {
                clientCommand = clientDialog.resultOfDialog();
                switch (clientCommand) {
                    case HELP:
                        System.out.println(AVAILABLE_COMMANDS);
                        break;
                    case VIEW_LOCAL:
                        downloadedFiles.printListOfDownloadedFiles();
                        break;
                    case VIEW_SERVER:
                        showServerFiles(serverFileNames);
                        break;
                    case DOWNLOAD:
                        final String fileName = clientDialog.askNameOfFileToDownload(serverFileNames);
                        if (fileName != null) {
                            long sizeOfFile = getSizeOfFile(fileName, networkUtils);
                            if (clientDialog.downloadConfirmation(fileName, sizeOfFile)) {
                                final String pathToSaveFile = clientDialog.getDirectory();
                                if (pathToSaveFile != null) {
                                    networkUtils.sendStringToServer("YES");

                                    if (NetworkUtils.downloadFile(pathToSaveFile, fileName, sizeOfFile, socket)) {
                                        downloadedFiles.rememberFileAsDownloaded(fileName);
                                        System.out.println("Файл успешно загружен");
                                    }
                                }
                                else {
                                    networkUtils.sendStringToServer("NO");
                                }
                            }
                            else {
                                networkUtils.sendStringToServer("NO");
                            }
                        }
                        break;
                    case UNEXPECTED_COMMAND:
                        System.out.println("Введена не корректная команда");
                        System.out.println(AVAILABLE_COMMANDS);
                        break;
                }
            } while (clientCommand != ClientCommand.EXIT);

        } catch (UnknownHostException e) {
            System.out.println("got UnknownHostException " + e.getMessage());
        } catch (IOException e) {
            System.out.println("got IOException " + e.getMessage());
        }
    }

    private static long getSizeOfFile(final String fileName, final NetworkUtils networkUtils) {
        networkUtils.sendStringToServer(fileName); // отправляем название файла на сервер
        final String line = networkUtils.getMessageFromServer(); // получаем от сервера строку содержащёю размер файла
        final String[] strings = line.split(" "); // парсим полученную строку
        return Long.parseLong(strings[strings.length - 2]);
    }

    private static ArrayList<String> readFileNames(final NetworkUtils networkUtils) {
        final ArrayList<String> files = new ArrayList<>();
        final int filesNumber = Integer.parseInt(networkUtils.getMessageFromServer());
        for (int i = 0; i < filesNumber; ++i) {
            String line = networkUtils.getMessageFromServer();
            files.add(line);
        }
        return files;
    }

    private static void showServerFiles(final List<String> serverFileNames) {
        System.out.println("На сервере доступны следующие файлы : ");
        for (final String fileName : serverFileNames) {
            System.out.println(fileName);
        }
    }
}
