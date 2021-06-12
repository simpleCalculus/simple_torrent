import java.util.LinkedList;

public class DownloadedFiles {
    private LinkedList<String> downloadedFiles;
    public DownloadedFiles() {
        downloadedFiles = new LinkedList<>();
    }

    public void rememberFileAsDownloaded(String file) {
        downloadedFiles.add(file);
    }

    public void printListOfDownloadedFiles() {
        if (downloadedFiles == null || downloadedFiles.size() == 0) {
            System.out.println("Вы еще не загружали файл");
        } else {
            System.out.println("Ранее загруженные файлы");
            for(String fileName : downloadedFiles) {
                System.out.println(fileName);
            }
        }
    }
}
