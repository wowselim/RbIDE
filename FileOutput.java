import java.io.*;

public class FileOutput {
  public static File currentFile;
  
  public static void writeFile(String content, String fileName) {
    try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName)) ) {
      bw.write(content);
    } catch(Exception e) {
      System.out.println("Error writing file.");
    }
  }
}