import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ClientDialog {
    private final Scanner consoleInput;

    ClientDialog(final Scanner scanner) {
        consoleInput = scanner;
    }

    /**
     * Метод для диалога с пользователем
     * @return вернет команду которую выбрал пользователь
     */
    public ClientCommand resultOfDialog() {
        while (true) {
            System.out.println("Введите команду для выполнения");
            final String command = consoleInput.nextLine();
            switch (command) {
                case "exit":
                    return ClientCommand.EXIT;
                case "download":
                    return ClientCommand.DOWNLOAD;
                case "view-local":
                    return ClientCommand.VIEW_LOCAL;
                case "view-server":
                    return ClientCommand.VIEW_SERVER;
                case "help":
                    return ClientCommand.HELP;
                default:
                    return ClientCommand.UNEXPECTED_COMMAND;
            }
        }
    }

    /**
     * Спрашивает у пользователя название файла, которую он хочет скачать
     * @param names список файлов в сервере
     * @return имя файла для скачивания
     */
    public String askNameOfFileToDownload(final List<String> names) {
        System.out.println("Введите имя файла для скачивания");
        String name = consoleInput.nextLine();
        while (!names.contains(name)) {
            System.out.println("Введенная имя файла не существует в списке файлов");
            System.out.println("Если хотите выбрать другую команду - введите abort, " +
                    "иначе введите правильное имя файла");
            name = consoleInput.nextLine();
            if (name.equals("abort")) {
                return null;
            }
        }
        return name;
    }

    /**
     * Спрашивает у пользователя разрешение для скачивания
     * @param fileName название файла
     * @param sizeOfFile размер файла
     * @return вернет true если пользователь хочет скачать файл
     */
    public boolean downloadConfirmation(final String fileName, final long sizeOfFile) {
        System.out.println("sizeOfFile " + sizeOfFile);
        System.out.println("Файл: " + fileName + ", имеет размер " + sizeOfFile + " байт");
        while (true) {
            System.out.println("Если хотите его скачать введите YES, иначе NO");
            final String clientResponse = consoleInput.nextLine();
            if (clientResponse.equals("YES")) {
                return true;
            } else if (clientResponse.equals("NO")) {
                return false;
            }
        }
    }


    public String getDirectory() {
        System.out.println("Введите путь для сохранения файла");
        String path = consoleInput.nextLine();
        while (!Files.isDirectory(Paths.get(path))) {
            System.out.println("Указана несуществующая директория\n" +
                    "Если хотите отменить команду введите abort\n" +
                    "иначе введите путь для сохранения файла\n");
            path = consoleInput.nextLine();
            if (path.equals("abort")) {
                return null;
            }
        }
        return path;
    }

    public String[] askHostAndPort() {
        System.out.println("Введите хост");
        final String host = consoleInput.nextLine();
        System.out.println("Введите порт");
        final String port = consoleInput.nextLine();
        try {
            return new String[]{host, port};
        } catch (NumberFormatException numberFormatException) {
            System.out.println("значение порта не натурально число");
        }
        System.out.println("Не удалось соединиться, введена неверная хост и/или порт");
        return null;
    }
}
