import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class FileSystemExplorer extends JPanel implements ActionListener {
  private final JButton renameButton;
  private final JButton deleteButton;
  private final JButton changeDirButton;
  private final JFrame frame;
  private final ArrayList<FSObject> fsObjects;
  private ArrayList<FSObject> currentDirObjects;
  private final ArrayList<String> usedNames;
  private final ArrayList<String> directoryPath;
  private static final String HOME_DIR_NAME = "FSE_DIR";
  private String currentDirectory = "~/" + HOME_DIR_NAME;
  private int fileSelected = -1;
  private final FileService fileService;

  public FileSystemExplorer(JFrame frame) {
    this.frame = frame;
    this.fileService = new FileService(HOME_DIR_NAME);
    this.fsObjects = new ArrayList<>();
    this.usedNames = new ArrayList<>();
    this.directoryPath = new ArrayList<>();
    this.directoryPath.add(currentDirectory);

    // Set size restrictions
    setPreferredSize(UIConstants.PANEL_SIZE);
    setMaximumSize(UIConstants.PANEL_SIZE);
    setMinimumSize(UIConstants.PANEL_SIZE);
    setLayout(null);

    // Create buttons
    JButton newFileButton = createButton("New File", UIConstants.NEW_FILE_BUTTON_X_AXIS);
    JButton newFolderButton = createButton("New Folder", UIConstants.NEW_FOLDER_BUTTON_X_AXIS);
    renameButton = createButton("Rename", UIConstants.RENAME_BUTTON_X_AXIS);
    deleteButton = createButton("Delete", UIConstants.DELETE_BUTTON_X_AXIS);
    changeDirButton = createButton("Go Back", UIConstants.GO_BACK_BUTTON_X_AXIS);

    renameButton.setVisible(false);
    deleteButton.setVisible(false);
    changeDirButton.setVisible(false);

    addMouseListener(new FileListener());
  }

  private JButton createButton(String text, int x) {
    JButton button = new JButton(text);
    button.addActionListener(this);
    button.setBounds(x, UIConstants.BUTTON_Y_AXIS, UIConstants.BUTTON_SIZE.width, UIConstants.BUTTON_SIZE.height);
    add(button);
    return button;
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {
    }

    JFrame frame = new JFrame("File System Explorer");
    FileSystemExplorer fse = new FileSystemExplorer(frame);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.add(fse);
    frame.pack();
    frame.setVisible(true);
  }

  private void updateUsedNames() {
    usedNames.clear();
    for (FSObject cObj : currentDirObjects) {
      usedNames.add(cObj.getName());
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawWindow(g);
    drawUI(g);
  }

  private void drawWindow(Graphics g) {
    setBackground(UIConstants.COLOR_WHITE);
    g.setColor(UIConstants.COLOR_GRAY);
    g.fillRect(0, 0, UIConstants.PANEL_SIZE.width, UIConstants.TOOLBAR_HEIGHT);
    g.drawLine(0, UIConstants.BOTTOM_BAR_Y_AXIS, UIConstants.PANEL_SIZE.width, UIConstants.BOTTOM_BAR_Y_AXIS);
  }

  private void drawUI(Graphics g) {
    g.setFont(UIConstants.FONT_HELVETICA_BOLD_12);
    updateCurrentDirObjects();
    drawFiles(g);
    drawPathString(g);
    if (fileSelected >= 0 && fileSelected < currentDirObjects.size()) {
      g.drawString("Selected: " + currentDirObjects.get(fileSelected).getName(), UIConstants.PATH_STRING_X_OFFSET,
          UIConstants.BOTTOM_BAR_TEXT_Y_AXIS);
    }
    updateUsedNames();
  }

  private void updateCurrentDirObjects() {
    currentDirObjects = new ArrayList<>();
    for (FSObject cObject : fsObjects) {
      if (Objects.equals(cObject.getParentDirectory(), currentDirectory)) {
        currentDirObjects.add(cObject);
      }
    }
  }

  private void drawFiles(Graphics g) {
    FontMetrics metrics = g.getFontMetrics(UIConstants.FONT_HELVETICA_BOLD_12);
    for (int i = 0; i < currentDirObjects.size(); i++) {
      FSObject tempObject = currentDirObjects.get(i);
      ImageIcon imageIcon = tempObject.getImageIcon();
      int imageWidth = imageIcon.getIconWidth();

      int row = i / UIConstants.MAX_ICONS_PER_ROW;
      int col = i % UIConstants.MAX_ICONS_PER_ROW;

      int rowSpacingX = UIConstants.FILE_ICON_START_X + col * UIConstants.FILE_ICON_SPACING_X * imageWidth;
      int rowSpacingY = UIConstants.FILE_ICON_START_Y + row * UIConstants.FILE_ICON_SPACING_Y;

      g.drawImage(imageIcon.getImage(), rowSpacingX, rowSpacingY, null);

      String formattedName = formatName(tempObject.getName());
      int labelWidth = metrics.stringWidth(formattedName);
      int labelX = rowSpacingX + (imageWidth - labelWidth) / 2;
      int labelY = rowSpacingY + imageWidth + UIConstants.FILE_LABEL_Y_OFFSET;

      g.setColor(i == fileSelected ? UIConstants.COLOR_RED : UIConstants.COLOR_BLACK);
      g.drawString(formattedName, labelX, labelY);
      g.setColor(UIConstants.COLOR_BLACK);
    }
  }

  private String formatName(String name) {
    if (name.length() > UIConstants.FILE_NAME_MAX_LENGTH) {
      return name.substring(0, UIConstants.FILE_NAME_MAX_LENGTH) + "...";
    }
    return name;
  }

  private void drawPathString(Graphics g) {
    FontMetrics metrics = g.getFontMetrics(UIConstants.FONT_HELVETICA_BOLD_12);
    String pathStr = "Path: " + directoryPath.get(directoryPath.size() - 1);
    int adv = metrics.stringWidth(pathStr);
    int pathX = UIConstants.PANEL_SIZE.width - adv - UIConstants.PATH_STRING_X_OFFSET;
    g.drawString(pathStr, pathX, UIConstants.BOTTOM_BAR_TEXT_Y_AXIS);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    switch (command) {
      case "New File":
        createNewObject("file");
        break;
      case "New Folder":
        createNewObject("folder");
        break;
      case "Rename":
        renameSelectedObject();
        break;
      case "Delete":
        deleteSelectedObject();
        break;
      case "Go Back":
        goBack();
        break;
    }
  }

  private void createNewObject(String type) {
    if (currentDirObjects.size() >= UIConstants.MAX_OBJECTS_PER_DIRECTORY) {
      JOptionPane.showMessageDialog(frame, "Error: Maximum number of objects per directory has been reached.");
      return;
    }
    String prompt = type.equals("file") ? "Enter a file name:" : "Enter a folder name:";
    String name = JOptionPane.showInputDialog(frame, prompt, null);
    if (name == null)
      return;
    if (name.isEmpty())
      name = "tmp";

    if (!isNameValid(name)) {
      return;
    }

    if (type.equals("file") && name.lastIndexOf('.') != name.indexOf('.')) {
      JOptionPane.showMessageDialog(frame,
          "Error: Multiple '.' detected in '" + name + "'\nOnly include one for the file extension.");
      return;
    }

    if (checkNameUsed(name)) {
      name = updateName(name);
    }

    FSObject newObject = new FSObject(name, type, currentDirectory);
    try {
      fileService.createFile(newObject);
      fsObjects.add(newObject);
      fileSelected = -1;
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(frame, "Error creating file: " + ex.getMessage(), "File System Error",
          JOptionPane.ERROR_MESSAGE);
    }
    repaint();
  }

  private void renameSelectedObject() {
    if (fileSelected > -1) {
      String newName = JOptionPane.showInputDialog(frame, "Enter a new name:", null);
      if (newName != null && !newName.isEmpty()) {
        if (isNameValid(newName)) {
          renameCurrentObject(newName);
        }
      }
    }
  }

  private boolean isNameValid(String name) {
    String illegalChars = "/\\:*?\"<>|";
    for (char c : illegalChars.toCharArray()) {
      if (name.indexOf(c) >= 0) {
        JOptionPane.showMessageDialog(frame,
            "Error: A file name can't contain any of the following characters:\n" + illegalChars);
        return false;
      }
    }
    return true;
  }

  private void deleteSelectedObject() {
    if (fileSelected > -1) {
      int confirm = JOptionPane.showConfirmDialog(frame,
          "Are you sure you want to delete " + currentDirObjects.get(fileSelected).getName() + "?");
      if (confirm == 0) {
        deleteCurrentObject();
      }
    }
  }

  private void goBack() {
    if (!currentDirectory.equals("~/" + HOME_DIR_NAME)) {
      directoryPath.remove(directoryPath.size() - 1);
      currentDirectory = directoryPath.get(directoryPath.size() - 1);
    }
    if (currentDirectory.equals("~/" + HOME_DIR_NAME)) {
      changeDirButton.setVisible(false);
    }
    fileSelected = -1;
    repaint();
  }

  private void deleteCurrentObject() {
    FSObject currentOBJ = currentDirObjects.get(fileSelected);
    try {
      fileService.deleteFile(currentOBJ);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(frame, "Error deleting file: " + ex.getMessage(), "File System Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (currentOBJ.getType().equals("folder")) {
      String path = currentOBJ.getFullPath();
      fsObjects.removeIf(fsObj -> fsObj.getFullPath().equals(path) || fsObj.getFullPath().startsWith(path + "/"));
    } else {
      fsObjects.remove(currentOBJ);
    }

    usedNames.remove(currentOBJ.getName());
    currentDirObjects.remove(fileSelected);

    fileSelected = -1;
    hideOBJManipulators();
    repaint();
  }

  private void renameCurrentObject(String newName) {
    FSObject currentOBJ = currentDirObjects.get(fileSelected);
    String oldName = currentOBJ.getName();

    if (checkNameUsed(newName)) {
      newName = updateName(newName);
    }

    try {
      fileService.renameFile(currentOBJ, newName); // This also updates the name in the FSObject
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(frame, "Error renaming file: " + ex.getMessage(), "File System Error",
          JOptionPane.ERROR_MESSAGE);
      return; // Stop execution if rename fails
    }

    if (currentOBJ.getType().equals("folder")) {
      String oldPath = currentOBJ.getFullPath().replace(newName, oldName);
      String newPath = currentOBJ.getFullPath();

      for (FSObject tmpOBJ : fsObjects) {
        if (tmpOBJ.getParentDirectory().startsWith(oldPath)) {
          String newParentPath = tmpOBJ.getParentDirectory().replaceFirst(java.util.regex.Pattern.quote(oldPath),
              newPath);
          tmpOBJ.setParentDirectory(newParentPath);
        }
      }
    }

    usedNames.remove(oldName);
    usedNames.add(newName);
    fileSelected = -1;
    hideOBJManipulators();
    repaint();
  }

  private void hideOBJManipulators() {
    renameButton.setVisible(false);
    deleteButton.setVisible(false);
  }

  private String updateName(String fileName) {
    String baseName;
    String extension = "";

    if (fileName.contains(".")) {
      baseName = fileName.substring(0, fileName.lastIndexOf('.'));
      extension = fileName.substring(fileName.lastIndexOf('.'));
    } else {
      baseName = fileName;
    }

    int counter = 1;
    String updatedName;
    do {
      updatedName = baseName + counter + extension;
      counter++;
    } while (checkNameUsed(updatedName));
    return updatedName;
  }

  private boolean checkNameUsed(String name) {
    return usedNames.contains(name);
  }

  private class FileListener implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
      int xCord = e.getX();
      int yCord = e.getY();

      for (int i = 0; i < currentDirObjects.size(); i++) {
        if (isClickOnObject(i, xCord, yCord)) {
          fileSelected = i;
          if (e.getClickCount() == 2) {
            handleDoubleClick(i);
          } else {
            renameButton.setVisible(true);
            deleteButton.setVisible(true);
          }
          repaint();
          return;
        }
      }

      // If no object was clicked, deselect
      fileSelected = -1;
      hideOBJManipulators();
      repaint();
    }

    private boolean isClickOnObject(int i, int x, int y) {
      ImageIcon imageIcon = currentDirObjects.get(i).getImageIcon();
      int imageWidth = imageIcon.getIconWidth();
      int imageHeight = imageIcon.getIconHeight();

      int row = i / UIConstants.MAX_ICONS_PER_ROW;
      int col = i % UIConstants.MAX_ICONS_PER_ROW;

      int xMin = UIConstants.FILE_ICON_START_X + col * UIConstants.FILE_ICON_SPACING_X * imageWidth
          - UIConstants.CLICK_X_MIN_OFFSET;
      int xMax = xMin + imageWidth + UIConstants.CLICK_X_MAX_OFFSET;
      int yMin = UIConstants.FILE_ICON_START_Y + row * UIConstants.FILE_ICON_SPACING_Y - UIConstants.CLICK_Y_MIN_OFFSET;
      int yMax = yMin + imageHeight + UIConstants.CLICK_Y_MAX_OFFSET;

      return (x >= xMin && x <= xMax && y >= yMin && y <= yMax);
    }

    private void handleDoubleClick(int i) {
      FSObject currentOBJ = currentDirObjects.get(i);
      if (currentOBJ.getType().equals("folder")) {
        directoryPath.add(currentOBJ.getFullPath());
        currentDirectory = currentOBJ.getFullPath();
        changeDirButton.setVisible(true);
      } else if (currentOBJ.getType().equals("file")) {
        try {
          fileService.editFile(currentOBJ);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(frame, "Error opening file: " + ex.getMessage(), "File System Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
      fileSelected = -1;
      hideOBJManipulators();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
  }
}