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
  
  private FileTree fileTree;
  private JScrollPane fileTreePane;
  
  private JSplitPane textSplitPane;
  private RSyntaxTextArea textArea;
  private RTextScrollPane textPane;
  private JTextArea console;
  private JScrollPane infoTextPane;
  
  private JPanel statusBar;
  private JLabel statusText;
  
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
    btnNew.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("res/file-o.png")));
    btnOpen = new JButton("Open");
    btnOpen.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("res/folder-open-o.png")));
    btnSave = new JButton("Save");
    btnSave.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("res/floppy-o.png")));
    btnRun = new JButton("Run");
    btnRun.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("res/play.png")));
    menuBar.setLayout(new GridLayout(0,4));
    menuBar.add(btnNew);
    menuBar.add(btnOpen);
    menuBar.add(btnSave);
    menuBar.add(btnRun);
    add(menuBar, BorderLayout.NORTH);
    
    mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
    mainSplitPane.setResizeWeight(0.1);
    
    fileTree = new FileTree(".");
    fileTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    fileTreePane = new JScrollPane(fileTree);
    fileTreePane.setPreferredSize(new Dimension(100, 0));
    mainSplitPane.add(fileTreePane, JSplitPane.LEFT);
    
    textSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
    textSplitPane.setResizeWeight(0.8);
    
    textArea = new RSyntaxTextArea();
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
    textPane = new RTextScrollPane(textArea);
    textSplitPane.add(textPane, JSplitPane.TOP);
    
    console = new JTextArea();
    console.setEditable(false);
    infoTextPane = new JScrollPane(console);
    textSplitPane.add(infoTextPane, JSplitPane.BOTTOM);
    
    mainSplitPane.add(textSplitPane, JSplitPane.RIGHT);
    
    add(mainSplitPane, BorderLayout.CENTER);
    
    statusBar = new JPanel();
    statusBar.setBorder(BorderFactory.createEtchedBorder());
    statusBar.setLayout(new GridLayout(0, 2));
    statusText = new JLabel("Warning: Calling System.exit will cause the IDE to exit.");
    statusBar.add(statusText);
    
    add(statusBar, BorderLayout.SOUTH);
    
    initListeners();
  }   
  
  private void initListeners() {
    btnNew.addActionListener(al -> {
      textArea.setText("");
      fileName = "";
    });
    
    btnOpen.addActionListener(al -> {
      open();
    });
    
    btnSave.addActionListener(al -> {
      FileOutput.writeFile(textArea.getText(), fileName);
    });
    
    btnRun.addActionListener(al -> {
      try {
        FileOutput.writeFile(textArea.getText(), fileName);
        console.setText("");
        
        container = new ScriptingContainer();
        container.setOutput(new PrintStream(new RubyOutputStream(console)));
        container.setRunRubyInProcess(true);
        container.runScriptlet(textArea.getText());
        container.terminate();
      } catch(Exception e) {
        console.setText("");
      }
    });
    
    fileTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
          fileName = fileTree.f.getPath();
          open();
        }
      }
    });
  }
  
  private void open() {
    textArea.setText(FileInput.readFile(fileName));
    setTitle(title + " - " + fileName);
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      System.out.println("Error setting look and feel, using default one.");
    }
    
    SwingUtilities.invokeLater(() -> {
      new Gui("RbScripter").setVisible(true);
    });
  }
}