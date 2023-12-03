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

  public FSObject(String name, String type, String parentDirectory) {
    this.name = name;
    this.type = type;
    this.parentDirectory = parentDirectory;
    this.fullPath = this.parentDirectory + "/" + this.name;
    String filePath = this.fullPath.replace('~', '.');
    try {
      if (type.equals("file")) {
        imagePath = "./fileIcon.png";
        System.out.println(filePath);
        file = new File(filePath);
        file.createNewFile();
        file.deleteOnExit();
      } else if (type.equals("folder")) {
        imagePath = "./folderIcon.png";
        System.out.println(filePath);
        file = new File(filePath);
        file.mkdir();
        file.deleteOnExit();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.imageIcon = new ImageIcon(imagePath);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    this.fullPath = this.parentDirectory + "/" + this.name;
    String filePath = this.fullPath.replace('~', '.');
    File NewFile = new File(filePath);
    NewFile.deleteOnExit();
    file.renameTo(NewFile);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getParentDirectory() {
    return parentDirectory;
  }

  public void setParentDirectory(String parentDirectory) {
    this.parentDirectory = parentDirectory;
  }

  public ImageIcon getImageIcon() {
    return imageIcon;
  }

  public void setImageIcon(ImageIcon imageIcon) {
    this.imageIcon = imageIcon;
  }

  public String getFullPath() {
    return fullPath;
  }

  public void deleteFile() {
    if (type.equals("file")) this.file.delete();
    else if (type.equals("folder")) recursiveDirectoryDelete(file);

  }

  public void editFile() {
    Desktop desktop = Desktop.getDesktop();
    if (type.equals("file")) {
      try {
        desktop.open(this.file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void recursiveDirectoryDelete(File dir) {
    File[] allFiles = dir.listFiles();
    if (allFiles != null && dir.isDirectory()) {
      for (File currentDir : allFiles) {
        recursiveDirectoryDelete(currentDir);
      }
    }
    dir.delete();
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath + "/" + this.name;
  }
}
