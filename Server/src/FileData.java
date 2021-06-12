import java.io.File;

public class FileData {
    /**
     * Метод вернёт информацию о файле
     * @param path путь к файлу
     * @return информация о размере файла
     */
    public static String fileData(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return "-1";
        }
        return "Файл: " + file.getName() + ", имеет размер  " + file.length() + " байт";
    }
}
