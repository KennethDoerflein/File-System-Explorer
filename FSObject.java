public class FSObject {
  private String name;
  private String type;
  private String parentDirectory;

  public FSObject(String name, String type, String parentDirectory) {
    this.name = name;
    this.type = type;
    this.parentDirectory = parentDirectory;
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
}
