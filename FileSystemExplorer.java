import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
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
  static final String homeDirName = "FSE_DIR";
  static String currentDirectory = "~/" + homeDirName;
  final static int toolBarHeight = 70;
  static int fileSelected = -1;
  private int clickCount = -1;


  public FileSystemExplorer() {
    // set size restrictions
    Dimension size = new Dimension(900, 600); // size of the panel
    setPreferredSize(size);
    setMaximumSize(size);
    setMinimumSize(size);
    setLayout(null);

    // create buttons
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

  public static void main(String[] args) {
    // this contains the initial setup

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {
    }

    // create arrays for holding objects used by the file explorer
    FSObjects = new ArrayList<>();
    usedNames = new ArrayList<>();
    directoryPath = new ArrayList<>();
    directoryPath.add(currentDirectory);
    // create and name the frame(main screen element)
    frame = new JFrame("File System Explorer");
    // create new object of self
    FileSystemExplorer FSE = new FileSystemExplorer();
    // create file of home directory
    File homeDir = new File("./" + homeDirName);
    // create directory object on host system
    homeDir.mkdir();
    // delete directory when explorer is closed
    homeDir.deleteOnExit();
    // stop running when frame is closed
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame setup
    frame.setResizable(false);
    frame.add(FSE);
    frame.pack();
    frame.setVisible(true);
  }

  private static void updateUsedNames() {
    // rebuild used names array for current directory
    usedNames.clear();
    for (FSObject cObj : currentDirObjects) {
      usedNames.add(cObj.getName());
    }
  }

  @Override
  public void paintComponent(Graphics page) {
    // override paint component
    super.paintComponent(page);
    // call drawWindow to draw the outer box
    drawWindow(page);
    // call drawUI to draw the interface
    drawUI(page);
    // small sleep to decrease CPU usage
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void drawWindow(Graphics page) {
    // draw the explorer window (toolbar, background, bottom line)
    setBackground(white);
    page.setColor(gray);
    page.fillRect(0, 0, 900, toolBarHeight);
    page.drawLine(0, 580, 900, 580);
  }

  public void drawUI(Graphics page) {
    // SET FONT
    Font font = new Font("Helvetica", Font.BOLD, 12);
    page.setFont(font);
    // create array of objects in current directory
    currentDirObjects = new ArrayList<>();
    for (FSObject cObject : FSObjects) {
      if (Objects.equals(cObject.getParentDirectory(), currentDirectory)) {
        currentDirObjects.add(cObject);
      }
    }
    // draw the files
    for (int i = 0; i < currentDirObjects.size(); i++) {
      // get file icon from object
      FSObject tempObject = currentDirObjects.get(i);
      ImageIcon imageIcon = tempObject.getImageIcon();
      // get icon width
      int imageWidth = imageIcon.getIconWidth();
      // calculate spacing based on image width
      int rowSpacingX = 40 + i * 2 * imageWidth;
      if (rowSpacingX > 900) rowSpacingX -= 864 * (int) (i / 9.0);
      int rowSpacingY = 100 * (int) (i / 9.0) + toolBarHeight + imageWidth / 2;
      // get the image from the image icon
      Image image = imageIcon.getImage();
      // draw the image on the screen at the calculated place
      page.drawImage(image, rowSpacingX, rowSpacingY, null);
      // format the string name so it isn't too long
      String formattedName = String.format("%3.5s", tempObject.getName());
      // add dots to symbolize full name isn't displayed and to look at the bottom of the screen
      if (tempObject.getName().length() > 5) formattedName += "...";
      // calculate label/name coordinates
      int labelX = rowSpacingX + imageWidth / formattedName.length();
      int labelY = rowSpacingY + imageWidth + 10;
      // check if the file is selected to make the text red
      if (i == fileSelected) page.setColor(red);
      else page.setColor(Color.black);
      // draw the files name
      page.drawString(formattedName, labelX, labelY);
      page.setColor(Color.black);
      // if the file is selected draw the full name at the bottom of the screen
      if (fileSelected >= 0) page.drawString("Selected: " + currentDirObjects.get(fileSelected).getName(), 20, 595);
    }
    // get metrics from the graphics
    FontMetrics metrics = page.getFontMetrics(font);
    String pathStr = "Path: " + directoryPath.get(directoryPath.size() - 1).toString();
    // calculate the width of the text
    int adv = metrics.stringWidth(pathStr);
    int pathX = 900 - adv - 20;
    page.drawString(pathStr, pathX, 595);
    repaint(); // repaint the screen
    updateUsedNames(); // update the used names
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // listens for button presses
    if (e.getActionCommand().equals("New File")) { // check if they want to make a file
      if (currentDirObjects.size() < 45) { // limit to one page of icons per directory to reduce complexity
        // show input dialog to get filename/type
        String fileName = JOptionPane.showInputDialog(frame, "Enter a file name:", null);
        FSObject temp; // object to store new file information in
        if (fileName != null && fileName.isEmpty()) fileName = "tmp"; // set to tmp if no name is provided
        if (fileName != null) {
          if (fileName.lastIndexOf('.') == fileName.indexOf('.')) {
            //updateUsedNames();
            // check if name is used in the current directory then update it to an unused one
            if (checkNameUsed(fileName)) fileName = updateName(fileName);
            // create file object then save it, unselect files
            temp = new FSObject(fileName, "file", currentDirectory);
            //System.out.println(currentDirectory);
            FSObjects.add(temp);
            fileSelected = -1;
          } else JOptionPane.showMessageDialog(frame, "Error: Multiple '.' detected in '" + fileName + "'\nOnly include one for the file extension.");
        }
      } else if (currentDirObjects.size() == 45) { // show message is over 45 objects
        JOptionPane.showMessageDialog(frame, "Error: Maximum number of objects per directory has been reached.");
      }
    } else if (e.getActionCommand().equals("New Folder")) { // same thing as new file but for folders(directories)
      if (currentDirObjects.size() < 45) {
        //updateUsedNames();
        String folderName = JOptionPane.showInputDialog(frame, "Enter a folder name:", null);
        FSObject temp;
        if (folderName != null && folderName.isEmpty()) folderName = "tmp";

        if (folderName != null) {
          if (checkNameUsed(folderName)) folderName = updateName(folderName);
          temp = new FSObject(folderName, "folder", currentDirectory);
          //System.out.println(currentDirectory);
          FSObjects.add(temp);
          fileSelected = -1;
          clickCount = -1;
        }
      } else if (currentDirObjects.size() == 45) {
        JOptionPane.showMessageDialog(frame, "Error: Maximum number of objects per directory has been reached.");
      }
      // get new name then call method to update it
    } else if (e.getActionCommand().equals("Rename")) {
      if (fileSelected > -1) {
        String newName = JOptionPane.showInputDialog(frame, "Enter a new name:", null);
        if (newName != null && !newName.isEmpty()) renameCurrentObject(newName);
      }
    } else if (e.getActionCommand().equals("Delete")) { // delete file/directory and all associated files
      if (fileSelected > -1) {
        //updateUsedNames();
        int delete = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + currentDirObjects.get(fileSelected).getName() + "?");
        //System.out.println(delete);
        if (delete == 0) deleteCurrentObject();
      }
      // go back to previous directory
    } else if (e.getActionCommand().equals("Go Back")) {
      if (!currentDirectory.equals("~/" + homeDirName)) {
        directoryPath.remove(directoryPath.size() - 1);
        currentDirectory = directoryPath.get(directoryPath.size() - 1);
      }
      if (currentDirectory.equals("~/" + homeDirName)) changeDirButton.setVisible(false);
      //updateUsedNames();
      fileSelected = -1;
      clickCount = -1;
    }
  }

  private void deleteCurrentObject() {
    // get current object information
    FSObject currentOBJ = currentDirObjects.get(fileSelected);
    // tell the object to delete host file associated with it
    // if this is a directory that includes everything in it
    currentOBJ.deleteFile();
    if (currentOBJ.getType().equals("file")) { // check if we are deleting a file
      // remove file data from arrays
      usedNames.remove(currentOBJ.getName());
      FSObjects.remove(currentOBJ);
      currentDirObjects.remove(fileSelected);
    } else if (currentOBJ.getType().equals("folder")) { // check if we are deleting a folder/directory
      String path = currentOBJ.getFullPath(); // get full path of directory
      //System.out.println(path);
      // purge details from arrays
      usedNames.remove(currentOBJ.getName());
      FSObjects.remove(currentOBJ);
      currentDirObjects.remove(fileSelected);
      // get then remove all subdirectories and files
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
    // file is no longer selected, hide file manipulators (delete, rename)
    fileSelected = -1;
    //updateUsedNames();
    hideOBJManipulators();
  }

  private void renameCurrentObject(String newName) {
    // get current object
    FSObject currentOBJ = currentDirObjects.get(fileSelected);
    // store a copy of the old object and name
    FSObject oldCurrentOBJ = currentDirObjects.get(fileSelected);
    String oldName = currentOBJ.getName();
    // check if we are deleting a file
    if (currentOBJ.getType().equals("file")) {
      // purge name
      usedNames.remove(currentOBJ.getName());
      // check if name is used and update accordingly
      if (checkNameUsed(newName)) newName = updateName(newName);
      // tell the object to update its name, host file name
      currentOBJ.setName(newName);
    } else if (currentOBJ.getType().equals("folder")) {// check if we are updating a directory
      String oldPath = currentOBJ.getFullPath();
      //System.out.println(oldPath);
      usedNames.remove(currentOBJ.getName()); // remove name from used
      // check if new name is used and update accordingly
      if (checkNameUsed(newName)) newName = updateName(newName);
      currentOBJ.setName(newName);
      //System.out.println(currentOBJ.getFolderID());
      // get current path of directory
      String currentOBJFID = currentOBJ.getFullPath();

      // go through all the objects and update only the child objects
      for (FSObject tmpOBJ : FSObjects) {
        String tmpFID = tmpOBJ.getFullPath();
        String tmpParentDir = tmpOBJ.getParentDirectory();
        if (oldPath.equals(tmpParentDir) && !newName.equals(oldName)) {
          tmpOBJ.setParentDirectory(currentOBJFID);
          tmpOBJ.setFullPath(currentOBJ.getFullPath());
        } else if (tmpFID.contains(oldPath) && !newName.equals(oldName)) {
          if (!tmpOBJ.getParentDirectory().equals(currentDirectory)) {
            //System.out.println(oldPath);
            int offset = newName.length() - oldName.length();
            if (offset > tmpParentDir.length()) offset = currentOBJFID.length();
            String newTmpOBJPath = currentOBJFID + tmpParentDir.substring(currentOBJFID.length() - offset);
            //System.out.println(newTmpOBJPath);
            tmpOBJ.setFullPath(newTmpOBJPath);
            tmpOBJ.setParentDirectory(newTmpOBJPath);
          }
        }
        tmpOBJ.setName(tmpOBJ.getName());
      }
    }
    // rename directory
    usedNames.add(newName);
    //updateUsedNames();
    // unselect file
    fileSelected = -1;
    hideOBJManipulators();
  }

  // hide rename and delete buttons
  private void hideOBJManipulators() {
    renameButton.setVisible(false);
    deleteButton.setVisible(false);
  }

  private String updateName(String fileName) {
    // check if name is used and add a number to it until it isn't
    String[] nameParts = fileName.split("\\.");
    if (nameParts.length > 1) {
      String updatedName;
      int counter = 1;
      do {
        updatedName = nameParts[0] + counter + "." + nameParts[1];
        counter++;
      } while (checkNameUsed(updatedName));
      return updatedName;
    } else {
      String updatedName;
      int counter = 1;
      do {
        updatedName = fileName + counter;
        counter++;
      } while (checkNameUsed(updatedName));
      return updatedName;
    }
  }

  // check if name is used
  private boolean checkNameUsed(String name) {
    return usedNames.contains(name);
  }

  // mouse listener for detecting clicks
  private class fileListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      // not used, don't remove
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // get coordinates of click
      int xCord = e.getX();
      int yCord = e.getY();
      // go through all the objects in the directory and check if one was clicked
      for (int i = 0; i < currentDirObjects.size(); i++) {
        // get icon size for determining if it was clicked
        ImageIcon imageIcon = currentDirObjects.get(i).getImageIcon();
        int imageWidth = imageIcon.getIconWidth();
        int imageHeight = imageIcon.getIconWidth() + 12;

        // adjust i if we reach the end of the row
        int iAdj = i;
        if (i >= 9) iAdj = i - 9 * (i / 9);

        // calculate coordinates for where we accept a click for the object
        int xMin = 49 + iAdj * 2 * imageWidth;
        int xMax = 80 + iAdj * 2 * imageWidth;

        int yMin = 100 + ((imageHeight + 40) * (int) (i / 9.0));
        int yMax = 150 + ((imageHeight + 40) * (int) (i / 9.0));
        if (xCord >= xMin && xCord <= xMax && yCord >= yMin && yCord <= yMax) {
          renameButton.setVisible(true);
          deleteButton.setVisible(true);
          // check if we double-click the object
          if (fileSelected == i) clickCount++;
          boolean dirty = false;
          if (clickCount % 2 == 0) {
            // reset double click
            clickCount = -2;
            //System.out.println("Double Click Detected");
            // check if we clicked a file/folder or elsewhere
            FSObject currentOBJ;
            if (fileSelected != -1) {
              // get object we clicked on
              currentOBJ = currentDirObjects.get(fileSelected);
              // if it was a directory we switch to that directory
              if (currentOBJ.getType().equals("folder")) {
                directoryPath.add(currentDirObjects.get(fileSelected).getFullPath());
                currentDirectory = directoryPath.get(directoryPath.size() - 1);
                changeDirButton.setVisible(true);
              } else if (currentOBJ.getType().equals("file")) {
                // if it's a file call host device to edit it
                currentOBJ.editFile();
              }
              //updateUsedNames();
              // unselect file after double click
              hideOBJManipulators();
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
      // unselect file
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