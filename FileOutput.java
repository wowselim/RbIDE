import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileOutput {  
  public static void writeFile(String content, String fileName) {
    Path filePath = FileSystems.getDefault().getPath(fileName);
    try {
      Files.write(filePath, content.getBytes());
    } catch(Exception e) {
      System.out.println("Error writing file.");
    }     
  }
}