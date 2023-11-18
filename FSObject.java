import javax.swing.*;

public class FSObject {
  private String name;
  private String type;
  private String parentDirectory;
  private String imagePath;
  private ImageIcon imageIcon;

  public FSObject(String name, String type, String parentDirectory) {
    this.name = name;
    this.type = type;
    this.parentDirectory = parentDirectory;
    if (type.equals("file")){
      imagePath = "./fileIcon.png";
    } else if (type.equals("folder")) {
      imagePath = "./folderIcon.png";
    }
    imageIcon = new ImageIcon(imagePath);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
