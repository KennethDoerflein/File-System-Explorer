import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FileService {

  private final String homeDirName;

  public FileService(String homeDirName) {
    this.homeDirName = homeDirName;
    File homeDir = new File("./" + homeDirName);
    homeDir.mkdir();
    homeDir.deleteOnExit();
  }

  public void createFile(FSObject fsObject) throws IOException {
    String filePath = fsObject.getFullPath().replace('~', '.');
    File file = new File(filePath);
    boolean success;
    if (fsObject.getType().equals("file")) {
      success = file.createNewFile();
    } else { // folder
      success = file.mkdir();
    }
    if (!success) {
      throw new IOException("Failed to create " + fsObject.getType() + ": " + file.getPath());
    }
    file.deleteOnExit();
  }

  public void deleteFile(FSObject fsObject) throws IOException {
    File file = new File(fsObject.getFullPath().replace('~', '.'));
    boolean success;
    if (fsObject.getType().equals("file")) {
      success = file.delete();
    } else { // folder
      success = recursiveDirectoryDelete(file);
    }

    if (!success) {
      throw new IOException("Failed to delete " + fsObject.getName());
    }
  }

  public void renameFile(FSObject fsObject, String newName) throws IOException {
    String oldPath = fsObject.getFullPath().replace('~', '.');
    String tempName = fsObject.getName();
    fsObject.setName(newName);
    String newPath = fsObject.getFullPath().replace('~', '.');
    File oldFile = new File(oldPath);
    File newFile = new File(newPath);
    if (!oldFile.renameTo(newFile)) {
      fsObject.setName(tempName); // Revert name on failure
      throw new IOException("Failed to rename file to " + newName);
    }
    recursiveDeleteOnExit(new File("./" + homeDirName));
  }

  public void editFile(FSObject fsObject) throws IOException {
    Desktop desktop = Desktop.getDesktop();
    if (fsObject.getType().equals("file")) {
      desktop.open(new File(fsObject.getFullPath().replace('~', '.')));
    }
  }

  private boolean recursiveDirectoryDelete(File dir) {
    File[] allFiles = dir.listFiles();
    if (allFiles != null) {
      for (File f : allFiles) {
        if (!recursiveDirectoryDelete(f)) {
          return false;
        }
      }
    }
    return dir.delete();
  }

  private void recursiveDeleteOnExit(File obj) {
    File[] allFiles = obj.listFiles();
    if (allFiles != null) {
      for (File currentDir : allFiles) {
        currentDir.deleteOnExit();
        recursiveDeleteOnExit(currentDir);
      }
    }
    obj.deleteOnExit();
  }
}