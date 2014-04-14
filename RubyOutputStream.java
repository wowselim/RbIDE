import java.io.*;
import javax.swing.*;

public class RubyOutputStream extends OutputStream {
  private JTextArea textArea;
  
  public RubyOutputStream(JTextArea textArea) {
    this.textArea = textArea;
  }         
  
  public void write(int b) throws IOException {
    textArea.append(String.valueOf((char) b));
    textArea.setCaretPosition(textArea.getDocument().getLength());
  }
}