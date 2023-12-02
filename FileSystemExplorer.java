import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Objects;

public class FileSystemExplorer extends JPanel implements ActionListener {
  Color white = new Color(220, 220, 220);
  Color gray = new Color(54, 59, 65);
  Color red = new Color(243, 15, 15);
  static JButton newFileButton;
  static JButton newFolderButton;
  static JButton renameButton;
  static JButton deleteButton;
  static JButton changeDirButton;
  static JFrame frame;
  static ArrayList<FSObject> FSObjects;
  static ArrayList<FSObject> currentDirObjects;
  static ArrayList<String> usedNames;
  static ArrayList<String> directoryPath;
  static String currentDirectory = "~";
  final static int toolBarHeight = 70;
  static int fileSelected = -1;
  private int clickCount = -1;


  public FileSystemExplorer() {
    Dimension size = new Dimension(900, 600); // size of the panel
    setPreferredSize(size);
    setMaximumSize(size);
    setMinimumSize(size);
    setLayout(null);
    Rectangle buttonBounds = new Rectangle(300, 250, 100, 30);
    newFileButton = new JButton("New File");
    newFileButton.addActionListener(this);
    newFileButton.setBounds(buttonBounds);
    newFileButton.setVisible(true);
    add(newFileButton);
    newFileButton.setLocation(12, 20);
    addMouseListener(new fileListener());
    newFolderButton = new JButton("New Folder");
    newFolderButton.addActionListener(this);
    newFolderButton.setBounds(buttonBounds);
    newFolderButton.setVisible(true);
    add(newFolderButton);
    newFolderButton.setLocation(newFileButton.getWidth() + 20, 20);

    renameButton = new JButton("Rename");
    renameButton.addActionListener(this);
    renameButton.setBounds(buttonBounds);
    renameButton.setVisible(false);
    add(renameButton);
    renameButton.setLocation(2 * (newFolderButton.getWidth() + 14), 20);

    deleteButton = new JButton("Delete");
    deleteButton.addActionListener(this);
    deleteButton.setBounds(buttonBounds);
    deleteButton.setVisible(false);
    add(deleteButton);
    deleteButton.setLocation(3 * (deleteButton.getWidth() + 12), 20);

    changeDirButton = new JButton("Go Back");
    changeDirButton.addActionListener(this);
    changeDirButton.setBounds(buttonBounds);
    changeDirButton.setVisible(false);
    add(changeDirButton);
    changeDirButton.setLocation(900 - changeDirButton.getWidth() - 10, 20);
  }

  @Override
  public void paintComponent(Graphics page) {
    super.paintComponent(page);
    drawWindow(page);
    drawUI(page);
  }

  public void drawWindow(Graphics page) {
    setBackground(white);
    page.setColor(gray);
    page.fillRect(0, 0, 900, toolBarHeight);
    page.drawLine(0, 580, 900, 580);
  }

  public void drawUI(Graphics page) {
    // SET FONT
    Font font = new Font("Helvetica", Font.BOLD, 12);
    page.setFont(font);
    currentDirObjects = new ArrayList<>();
    for (FSObject cObject : FSObjects) {
      if (Objects.equals(cObject.getParentDirectory(), currentDirectory)) {
        currentDirObjects.add(cObject);
      }
    }
    for (int i = 0; i < currentDirObjects.size(); i++) {
      FSObject tempObject = currentDirObjects.get(i);

      ImageIcon imageIcon = tempObject.getImageIcon();
      int imageWidth = imageIcon.getIconWidth();
      int rowSpacingX = 40 + i * 2 * imageWidth;
      if (rowSpacingX > 900) rowSpacingX -= 864 * (int) (i / 9.0);
      int rowSpacingY = 100 * (int) (i / 9.0) + toolBarHeight + imageWidth / 2;
      Image image = imageIcon.getImage();
      page.drawImage(image, rowSpacingX, rowSpacingY, null);
      String formattedName = String.format("%3.5s", tempObject.getName());
      if (tempObject.getName().length() > 5) formattedName += "...";
      int labelX = rowSpacingX + imageWidth / formattedName.length();
      int labelY = rowSpacingY + imageWidth + 10;
      if (i == fileSelected) page.setColor(red);
      else page.setColor(Color.black);
      page.drawString(formattedName, labelX, labelY);
      page.setColor(Color.black);
      if (fileSelected >= 0) page.drawString("Selected: " + currentDirObjects.get(fileSelected).getName(), 20, 595);
    }
    repaint();
  }

  public static void main(String[] args) {
    FSObjects = new ArrayList<>();
    usedNames = new ArrayList<>();
    directoryPath = new ArrayList<>();
    directoryPath.add(currentDirectory);
    frame = new JFrame("File System Explorer");
    FileSystemExplorer FSE = new FileSystemExplorer();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.add(FSE);
    frame.pack();
    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("New File")) {
      if (currentDirObjects.size() < 45) {
        String fileName = JOptionPane.showInputDialog(frame, "Enter a file name:", null);
        FSObject temp;
        if (fileName != null && fileName.isEmpty()) fileName = "tmp";

        if (fileName != null) {
          updateUsedNames();
          if (checkNameUsed(fileName)) fileName = updateName(fileName);
          temp = new FSObject(fileName, "file", currentDirectory);
          System.out.println(currentDirectory);
          FSObjects.add(temp);
          fileSelected = -1;
        }
      } else if (currentDirObjects.size() == 45) {
        JOptionPane.showMessageDialog(frame, "Error: Maximum number of objects per directory has been reached.");
      }
    } else if (e.getActionCommand().equals("New Folder")) {
      if (currentDirObjects.size() < 45) {
        updateUsedNames();
        String folderName = JOptionPane.showInputDialog(frame, "Enter a folder name:", null);
        FSObject temp;
        if (folderName != null && folderName.isEmpty()) folderName = "tmp";

        if (folderName != null) {
          if (checkNameUsed(folderName)) folderName = updateName(folderName);
          temp = new FSObject(folderName, "folder", currentDirectory);
          FSObjects.add(temp);
          fileSelected = -1;
        }
      } else if (currentDirObjects.size() == 45) {
        JOptionPane.showMessageDialog(frame, "Error: Maximum number of objects per directory has been reached.");
      }
    } else if (e.getActionCommand().equals("Rename")) {
      if (fileSelected > -1) {
        String newName = JOptionPane.showInputDialog(frame, "Enter a new name:", null);
        if (newName != null && !newName.isEmpty()) renameCurrentObject(newName);
      }
      // change over to double-click for final version
    } else if (e.getActionCommand().equals("Delete")) {
      if (fileSelected > -1) {
        updateUsedNames();
        int delete = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + currentDirObjects.get(fileSelected).getName() + "?");
        //System.out.println(delete);
        if (delete == 0) deleteCurrentObject();
      }
    } else if (e.getActionCommand().equals("Go Back")) {
      if (!currentDirectory.equals("~")) {
        directoryPath.remove(directoryPath.size() - 1);
        currentDirectory = directoryPath.get(directoryPath.size() - 1);
      }
      if (currentDirectory.equals("~")) changeDirButton.setVisible(false);
      updateUsedNames();
      fileSelected = -1;
      clickCount = -1;
    }
  }

  private static void updateUsedNames() {
    usedNames.clear();
    for (FSObject cObj : currentDirObjects) {
      usedNames.add(cObj.getName());
    }
  }

  private void deleteCurrentObject() {
    FSObject currentOBJ = currentDirObjects.get(fileSelected);
    if (currentOBJ.getType().equals("file")) {
      usedNames.remove(currentOBJ.getName());
      FSObjects.remove(currentOBJ);
      currentDirObjects.remove(fileSelected);
    } else if (currentOBJ.getType().equals("folder")) {
      String path = currentOBJ.getFullPath();
      //System.out.println(path);
      usedNames.remove(currentOBJ.getName());
      FSObjects.remove(currentOBJ);
      currentDirObjects.remove(fileSelected);
      for (int i = 0; i < FSObjects.size(); i++) {
        FSObject tmpOBJ = FSObjects.get(i);
        String tmpOBJPath = tmpOBJ.getFullPath();
        //System.out.println(tmpOBJ.getFullPath() + "\n\n");
        if (tmpOBJPath.contains(path)) {
          if (!tmpOBJ.getParentDirectory().equals(currentDirectory)) {
            usedNames.remove(tmpOBJ.getName());
            FSObjects.remove(tmpOBJ);
            currentDirObjects.remove(tmpOBJ);
            i = 0;
          }
        }
      }
    }
    /*
    System.out.println("currentDirObjects length: " + currentDirObjects.size());
    System.out.println("FSObjects length: " + FSObjects.size());
    System.out.println("usedNames length: " + usedNames.size());
    */
    fileSelected = -1;
    updateUsedNames();
    hideOBJManipulators();
  }

  private void renameCurrentObject(String newName) {
    FSObject currentOBJ = currentDirObjects.get(fileSelected);
    FSObject oldCurrentOBJ = currentDirObjects.get(fileSelected);
    String oldName = currentOBJ.getName();
    if (currentOBJ.getType().equals("file")) {
      usedNames.remove(currentOBJ.getName());
      if (checkNameUsed(newName)) newName = updateName(newName);
      currentOBJ.setName(newName);
    } else if (currentOBJ.getType().equals("folder")) {
      String oldPath = currentOBJ.getFullPath();
      //System.out.println(oldPath);
      usedNames.remove(currentOBJ.getName());
      if (checkNameUsed(newName)) newName = updateName(newName);
      currentOBJ.setName(newName);
      //System.out.println(currentOBJ.getFolderID());
      String currentOBJFID = currentOBJ.getFullPath();
      for (FSObject tmpOBJ : FSObjects) {
        String tmpFID = tmpOBJ.getFullPath();
        String tmpParentDir = tmpOBJ.getParentDirectory();
        if (oldPath.equals(tmpParentDir) && !newName.equals(oldName)) {
          tmpOBJ.setParentDirectory(currentOBJFID);
          tmpOBJ.setFullPath(currentOBJ.getFullPath());
        } else if (tmpOBJ.getParentDirectory().equals(currentDirectory)) {
          // DO NOT REMOVE THE ABOVE ELIF IT IS NEEDED
          // Will integrate better later
        } else if (tmpFID.contains(oldPath) && !newName.equals(oldName)) {
          //System.out.println(oldPath);
          int offset = newName.length() - oldName.length();
          if (offset > tmpParentDir.length()) offset = currentOBJFID.length();
          String newTmpOBJPath = currentOBJFID + tmpParentDir.substring(currentOBJFID.length() - offset);
          //System.out.println(newTmpOBJPath);
          tmpOBJ.setFullPath(newTmpOBJPath);
          tmpOBJ.setParentDirectory(newTmpOBJPath);
        }
      }
    }
    int index = FSObjects.indexOf(oldCurrentOBJ);
    FSObjects.get(index).setName(newName);
    usedNames.add(newName);
    updateUsedNames();
    fileSelected = -1;
    hideOBJManipulators();
  }

  private void hideOBJManipulators() {
    renameButton.setVisible(false);
    deleteButton.setVisible(false);
  }

  private String updateName(String fileName) {
    String updatedName;
    int counter = 1;
    do {
      updatedName = fileName + counter;
      counter++;
    } while (checkNameUsed(updatedName));
    return updatedName;
  }

  private boolean checkNameUsed(String name) {
    return usedNames.contains(name);
  }

  private class fileListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      // not used, don't remove
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // not used, don't remove
      int xCord = e.getX();
      int yCord = e.getY();
      for (int i = 0; i < currentDirObjects.size(); i++) {
        ImageIcon imageIcon = currentDirObjects.get(i).getImageIcon();
        int imageWidth = imageIcon.getIconWidth();
        int imageHeight = imageIcon.getIconWidth() + 12;
        int iAdj = i;
        if (i >= 9) iAdj = i - 9 * (i / 9);
        int xMin = 49 + iAdj * 2 * imageWidth;
        int xMax = 80 + iAdj * 2 * imageWidth;

        int yMin = 100 + ((imageHeight + 40) * (int) (i / 9.0));
        int yMax = 150 + ((imageHeight + 40) * (int) (i / 9.0));
        if (xCord >= xMin && xCord <= xMax && yCord >= yMin && yCord <= yMax) {
          renameButton.setVisible(true);
          deleteButton.setVisible(true);

          if (fileSelected == i) clickCount++;
          boolean dirty = false;
          if (clickCount % 2 == 0) {
            clickCount = -2;
            //System.out.println("Double Click Detected");
            if (fileSelected != -1 && currentDirObjects.get(fileSelected).getType().equals("folder")) {
              directoryPath.add(currentDirObjects.get(fileSelected).getFullPath());
              currentDirectory = directoryPath.get(directoryPath.size() - 1);
              updateUsedNames();
              hideOBJManipulators();
              changeDirButton.setVisible(true);
              clickCount = -1;
              dirty = true;
            }
          }
          if (!dirty) fileSelected = i;
          else fileSelected = -1;
          //System.out.println("File " + fileSelected);
          return;
        }
      }
      fileSelected = -1;
      clickCount = -1;
      hideOBJManipulators();
      //System.out.println("X Cord: " + xCord + ", Y Cord: " + yCord);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // not used, don't remove
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // not used, don't remove
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // not used, don't remove
    }
  }

}