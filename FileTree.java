// Heavily inspired by http://www.javaprogrammingforums.com/java-swing-tutorials/7944-how-use-jtree-create-file-system-viewer-tree.html

// TODO: implement listener for filesystem changes
//       fix opening subdirectory files

import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FileTree extends JTree implements Runnable {  
  private FileSystemModel fs;
  private String path;
  
  public FileTree(String dir) {
    fs = new FileSystemModel(new File(dir));
    this.path = dir;
    setModel(fs);
    //addTreeSelectionListener(new TreeSelectionListener() {
    //  public void valueChanged(TreeSelectionEvent evt) {
    //    File f = (File) getLastSelectedPathComponent();
    //  }
    //});
    new Thread(this).start();
  }
  
  public void run() {
    
  }
  
  class FileSystemModel implements TreeModel {
    private File rootDir;
    
    private List listeners = Collections.synchronizedList(new ArrayList());
    
    public FileSystemModel(File rootDir) {
      this.rootDir = rootDir;
    }
    
    public Object getRoot() {
      return rootDir;
    }
    
    public Object getChild(Object parent, int index) {
      File dir = (File) parent;
      String[] children = dir.list();
      return new TreeFile(dir, children[index]);
    }
    
    public int getChildCount(Object parent) {
      File file = (File) parent;
      if(file.isDirectory()) {
        String[] children = file.list();
        if(children != null)
        return file.list().length;
      }
      return 0;
    }
    
    public boolean isLeaf(Object node) {
      File file = (File) node;
      return file.isFile();
    }
    
    public int getIndexOfChild(Object parent, Object child) {
      File dir = (File) parent;
      File file = (File) child;
      String[] children = dir.list();
      for(int i = 0; i < children.length; i++) {
        if(file.getName().equals(children[i]))
        return i;
      }
      return -1;
    }
    
    public void valueForPathChanged(TreePath path, Object value) {
      File previousFile = (File) path.getLastPathComponent();
      String fileParentPath = previousFile.getParent();
      String newFileName = (String) value;
      File targetFile = new File(fileParentPath, newFileName);
      previousFile.renameTo(targetFile);
      File parent = new File(fileParentPath);
      int[] changedChildrenIndices = { getIndexOfChild(parent, targetFile) };
      Object[] changedChildren = { targetFile };
      fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
    }
    
    private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
      TreeModelEvent evt = new TreeModelEvent(this, parentPath, indices, children);
      Iterator listenerIterator = listeners.iterator();
      TreeModelListener listener = null;
      while(listenerIterator.hasNext()) {
        listener = (TreeModelListener) listenerIterator.next();
        listener.treeNodesChanged(evt);
      }
    }
    
    public void addTreeModelListener(TreeModelListener listener) {
      listeners.add(listener);
    }
    
    public void removeTreeModelListener(TreeModelListener listener) {
      listeners.remove(listener);
    }
    
    private class TreeFile extends File {
      public TreeFile(File parent, String child) {
        super(parent, child);
      }
      
      public String toString() {
        return getName();
      }
    }
  }
  
}