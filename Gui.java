import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

import org.jruby.embed.ScriptingContainer;

public class Gui extends JFrame { 
  private String title;
  
  private JPanel menuBar;
  private JButton btnNew;
  private JButton btnOpen;
  private JButton btnSave;
  private JButton btnRun;
  
  private JSplitPane mainSplitPane;
  
  private JTree fileTree;
  private JScrollPane fileTreePane;
  
  private JSplitPane textSplitPane;
  private RSyntaxTextArea textArea;
  private RTextScrollPane textPane;
  private JTextArea infoTextArea;
  private JScrollPane infoTextPane;
  
  private ScriptingContainer container;
  
  // single-file mode
  // for testing purposes only
  private String fileName = "script.rb";
  
  public Gui(String title) {
    this(title, 640, 480);
  }
  
  public Gui(String title, int width, int height) {
    super(title);
    this.title = title;
    prepareFrame(width, height);
    initComponents();
  }
  
  private void prepareFrame(int width, int height) {
    setIconImage(Toolkit.getDefaultToolkit().getImage("res/icon.png"));
    setSize(width, height);
    setMinimumSize(new Dimension(width, height));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE); 
  }
  
  private void initComponents() {
    setLayout(new BorderLayout());
    
    menuBar = new JPanel();
    menuBar.setPreferredSize(new Dimension(10, 30));
    btnNew = new JButton("New");
    btnOpen = new JButton("Open");
    btnSave = new JButton("Save");
    btnRun = new JButton("Run");
    menuBar.setLayout(new GridLayout(0,4));
    menuBar.add(btnNew);
    menuBar.add(btnOpen);
    menuBar.add(btnSave);
    menuBar.add(btnRun);
    add(menuBar, BorderLayout.NORTH);
    
    mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
    mainSplitPane.setResizeWeight(0.1);
    
    fileTree = new JTree();
    fileTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    fileTreePane = new JScrollPane(fileTree);
    mainSplitPane.add(fileTreePane, JSplitPane.LEFT);
    
    textSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
    textSplitPane.setResizeWeight(0.8);
    
    textArea = new RSyntaxTextArea();
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
    textArea.setCodeFoldingEnabled(true);
    textPane = new RTextScrollPane(textArea);
    textSplitPane.add(textPane, JSplitPane.TOP);
    
    infoTextArea = new JTextArea();
    infoTextArea.setEditable(false);
    infoTextPane = new JScrollPane(infoTextArea);
    textSplitPane.add(infoTextPane, JSplitPane.BOTTOM);
    
    mainSplitPane.add(textSplitPane, JSplitPane.RIGHT);
    
    add(mainSplitPane, BorderLayout.CENTER);
    
    initListeners();
  }   
  
  private void initListeners() {
    btnNew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        textArea.setText("");
      }
    });
    
    btnOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        textArea.setText(FileInput.readFile(fileName));
        setTitle(title + " - " + fileName);
      }
    });
    
    btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        FileOutput.writeFile(textArea.getText(), fileName);
      }
    });
    
    btnRun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        try {
          infoTextArea.setText("");
          
          container = new ScriptingContainer();
          container.setOutput(new PrintStream(new RubyOutputStream(infoTextArea)));
          container.runScriptlet(textArea.getText());
        } catch(Exception e) {
          infoTextArea.setText("");
        }
      }
    });
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      System.out.println("Error setting look and feel, using default one.");
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new Gui("RbScripter").setVisible(true);
      }
    });
  }
}