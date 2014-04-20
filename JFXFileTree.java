import java.awt.event.*;
import javax.swing.*;

import java.io.File;

import java.nio.file.*;
import java.nio.file.attribute.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

//ugly filetree class;
//(JTrees are worse)
public class JFXFileTree extends JFXPanel {
  private TreeView<String> treeView;
  private Scene scene;
  
  public JFXFileTree() {
    setPreferredSize(new java.awt.Dimension(100, 0));
    String hostName = "computer";
    TreeItem<String> rootNode = new TreeItem<>("Current Directory", new ImageView(new Image(
    ClassLoader.getSystemResourceAsStream("res/folder-open-o.png"))));
    Iterable<Path> rootDirectories = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
    for(Path name : rootDirectories) {
      FilePathTreeItem treeNode = new FilePathTreeItem(name);
      rootNode.getChildren().add(treeNode);
    }
    rootNode.setExpanded(true);
    
    treeView = new TreeView<>(rootNode);
    initFXComponents();
  }  
  
  private void initFXComponents() {
    Platform.runLater(() -> {
      scene = createScene();
      setScene(scene);
    });
  }
  
  private Scene createScene() {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root);
    
    root.setCenter(treeView);
    
    return(scene);
  }
  
  private class FilePathTreeItem extends TreeItem<String> {
    public Image folderCollapseImage =
    new Image(ClassLoader.getSystemResourceAsStream("res/arrow-right.png"));
    public Image folderExpandImage =
    new Image(ClassLoader.getSystemResourceAsStream("res/arrow-down.png"));
    public Image fileImage =
    new Image(ClassLoader.getSystemResourceAsStream("res/file-text.png"));
    
    public String fullPath;
    public boolean isDirectory;
    
    public FilePathTreeItem(Path file) {
      super(file.toString());
      fullPath = file.toString();
      
      if(Files.isDirectory(file)) {
        isDirectory = true;
        setGraphic(new ImageView(folderCollapseImage));
      } else {
        isDirectory = false;
        setGraphic(new ImageView(fileImage));
      }
      
      if (fullPath.endsWith(File.separator)) {
        String value = file.toString();
        int indexOf = value.lastIndexOf(File.separator);
        if(indexOf > 0) {
          setValue(value.substring(indexOf + 1));
        } else {
          setValue(value);
        }
      }
      
      addEventHandlers();
    }
    
    private void addEventHandlers() {
      addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
        public void handle(Event evt) {
          FilePathTreeItem source = (FilePathTreeItem) evt.getSource();
          if(source.isDirectory && source.isExpanded()) {
            ImageView iv = (ImageView) source.getGraphic();
            iv.setImage(folderExpandImage);
          }
          try {
            if(source.getChildren().isEmpty()) {
              Path path = Paths.get(source.fullPath);
              BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
              if(attributes.isDirectory()) {
                DirectoryStream<Path> dir = Files.newDirectoryStream(path);
                for(Path file : dir) {
                  FilePathTreeItem treeNode = new FilePathTreeItem(file);
                  source.getChildren().add(treeNode);
                }
              } else {
                // rescan directory ?
              }
            } 
          } catch(Exception e) {
            e.printStackTrace();
          } 
        }
      });
      
      addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
        public void handle(Event evt){
          FilePathTreeItem source =(FilePathTreeItem) evt.getSource();
          if(source.isDirectory && !source.isExpanded()){
            ImageView iv = (ImageView) source.getGraphic();
            iv.setImage(folderCollapseImage);
          }
        }
      });
    }
  }
}