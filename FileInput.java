import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileInput {  
  public static String readFile(String fileName) {
    Path filePath = FileSystems.getDefault().getPath(fileName);
    try {
      return new String(Files.readAllBytes(filePath));
    } catch(Exception e) {
      System.out.println("Error reading file.");
    } 
    return null;
  }
}