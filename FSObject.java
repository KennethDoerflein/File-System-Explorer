import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FSObject {
  private String fullPath;
  private String name;
  private String type;
  private String parentDirectory;
  private String imagePath;
  private ImageIcon imageIcon;
  private File file;
  File homeDir = new File("./" + FileSystemExplorer.homeDirName);

  public FSObject(String name, String type, String parentDirectory) {
    this.name = name;
    this.type = type;
    this.parentDirectory = parentDirectory;
    // create path
    this.fullPath = this.parentDirectory + "/" + this.name;
    // change ~ to . for host file creation
    String filePath = this.fullPath.replace('~', '.');
    try {
      if (type.equals("file")) {
        // set image and create file on host system
        imagePath = "./fileIcon.png";
        //System.out.println(filePath);
        file = new File(filePath);
        file.createNewFile();
        file.deleteOnExit(); // delete on exit
      } else if (type.equals("folder")) {
        // set image and create directory on host system
        imagePath = "./folderIcon.png";
        //System.out.println(filePath);
        file = new File(filePath);
        file.mkdir();
        file.deleteOnExit(); // delete on exit
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.imageIcon = new ImageIcon(imagePath); // create image icon from image
  }

  public String getName() { // get object name
    return name;
  }

  public void setName(String name) { // update object name
    this.name = name;
    this.fullPath = this.parentDirectory + "/" + this.name; // remake path
    // remake host object with new name
    String filePath = this.fullPath.replace('~', '.');
    File NewFile = new File(filePath);
    NewFile.deleteOnExit();
    file.renameTo(NewFile);
    file.deleteOnExit();
    recursiveDeleteOnExit(homeDir); // set all objects to delta on exit
  }

  public String getType() {
    return type; // return object type
  }

  public void setType(String type) {
    this.type = type; // set object type
  }

  public String getParentDirectory() {
    return parentDirectory; // get parent directory
  }

  public void setParentDirectory(String parentDirectory) {
    this.parentDirectory = parentDirectory; // set parent directory
  }

  public ImageIcon getImageIcon() {
    return imageIcon; // get image icon
  }

  public void setImageIcon(ImageIcon imageIcon) {
    this.imageIcon = imageIcon; // set image icon
  }

  public String getFullPath() {
    return fullPath; // get full object path
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath + "/" + this.name; // set objects path
  }

  public void deleteFile() {
    // delete object, change method based on directory or file
    if (type.equals("file")) this.file.delete();
    else if (type.equals("folder")) recursiveDirectoryDelete(file);
  }

  public void editFile() {
    // call the host system to open its default editor for that file type
    Desktop desktop = Desktop.getDesktop();
    if (type.equals("file")) {
      try {
        desktop.open(this.file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // recursively delete all objects in deleted directory on host
  public void recursiveDirectoryDelete(File dir) {
    File[] allFiles = dir.listFiles();
    if (allFiles != null && dir.isDirectory()) {
      for (File currentDir : allFiles) {
        recursiveDirectoryDelete(currentDir);
      }
    }
    dir.delete();
  }

  // set all created files to delete when the program exits
  public void recursiveDeleteOnExit(File dir) {
    File[] allFiles = dir.listFiles();
    if (allFiles != null && dir.isDirectory()) {
      for (File currentDir : allFiles) {
        recursiveDirectoryDelete(currentDir);
      }
    }
    dir.deleteOnExit();
  }
}
