import java.io.*;

public class FileInput {
  public static File currentFile;
  
  public static String readFile(String fileName) {
    StringBuilder file = new StringBuilder();
    try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String currentLine;
      while((currentLine = br.readLine()) != null) { 
        file.append(currentLine + '\n');
      }
      file.delete(file.toString().length() - 1, file.toString().length());
      return file.toString();
    } catch(Exception e) {
      System.out.println("Error reading file.");
    } 
    return null;
  }
}