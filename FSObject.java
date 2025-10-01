import javax.swing.ImageIcon;

public class FSObject {
  private String fullPath;
  private String name;
  private String type;
  private String parentDirectory;
  private ImageIcon imageIcon;

  public FSObject(String name, String type, String parentDirectory) {
    this.name = name;
    this.type = type;
    this.parentDirectory = parentDirectory;
    this.fullPath = this.parentDirectory + "/" + this.name;

    String imagePath;
    if (type.equals("file")) {
      imagePath = "./fileIcon.png";
    } else { // folder
      imagePath = "./folderIcon.png";
    }
    this.imageIcon = new ImageIcon(imagePath);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    updateFullPath();
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
    updateFullPath();
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

  private void updateFullPath() {
    this.fullPath = this.parentDirectory + "/" + this.name;
  }
}