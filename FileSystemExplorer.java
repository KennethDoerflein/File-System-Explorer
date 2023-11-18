import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class FileSystemExplorer extends JPanel implements ActionListener {
  Color white = new Color(220, 220, 220);
  Color gray = new Color(54, 59, 65);
  Color red = new Color(243, 15, 15);
  static JButton newFileButton;
  static JButton newFolderButton;
  static JButton renameButton;
  static JButton deleteButton;
  static JFrame frame;
  static ArrayList<FSObject> FSObjects;
  static ArrayList<String> usedNames;
  static String currentDirectory = "home";
  final static int toolBarHeight = 70;
  static int fileSelected = -1;

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
    renameButton.setVisible(true);
    add(renameButton);
    renameButton.setLocation(2 * (newFolderButton.getWidth() + 14), 20);

    deleteButton = new JButton("Delete");
    deleteButton.addActionListener(this);
    deleteButton.setBounds(buttonBounds);
    deleteButton.setVisible(true);
    add(deleteButton);
    deleteButton.setLocation(3 * (deleteButton.getWidth() + 12), 20);
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
    for (int i = 0; i < FSObjects.size(); i++) {
      FSObject tempObject = FSObjects.get(i);

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
      if (fileSelected >= 0) page.drawString("File Selected: " + FSObjects.get(fileSelected).getName(), 20, 595);
    }
    repaint();
  }

  public static void main(String[] args) {
    FSObjects = new ArrayList<>();
    usedNames = new ArrayList<>();
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
      String fileName = JOptionPane.showInputDialog(frame, "Enter a file name:", null);
      FSObject temp;
      if (fileName != null && fileName.isEmpty()) fileName = "tmp";

      if (fileName != null) {
        if (checkNameUsed(fileName)) fileName = updateName(fileName);
        temp = new FSObject(fileName, "file", currentDirectory);
        FSObjects.add(temp);
        usedNames.add(fileName);
      }
    } else if (e.getActionCommand().equals("New Folder")) {
      String folderName = JOptionPane.showInputDialog(frame, "Enter a folder name:", null);
      FSObject temp;
      if (folderName != null && folderName.isEmpty()) folderName = "tmp";

      if (folderName != null) {
        if (checkNameUsed(folderName)) folderName = updateName(folderName);
        temp = new FSObject(folderName, "folder", currentDirectory);
        FSObjects.add(temp);
        usedNames.add(folderName);
      }
    } else if (e.getActionCommand().equals("Rename")) {
      if (fileSelected > -1) {
        String newName = JOptionPane.showInputDialog(frame, "Enter a new name:", null);
        if (newName != null && !newName.isEmpty()) renameCurrentObjet(newName);
      }
    } else if (e.getActionCommand().equals("Delete")) {
      if (fileSelected > -1) {
        int delete = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + FSObjects.get(fileSelected).getName() + "?");
        System.out.println(delete);
        if (delete == 0) deleteCurrentObjet();
      }
    }
  }

  private void deleteCurrentObjet() {
    usedNames.remove(FSObjects.get(fileSelected).getName());
    FSObjects.remove(fileSelected);
    fileSelected = -1;
  }

  private void renameCurrentObjet(String newName) {
    FSObject current = FSObjects.get(fileSelected);
    usedNames.remove(current.getName());
    if (checkNameUsed(newName)) newName = updateName(newName);
    current.setName(newName);
    usedNames.add(newName);
    fileSelected = -1;
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

  private static class fileListener implements MouseListener {
    private int clickCount = -1;
    @Override
    public void mouseClicked(MouseEvent e) {
      int xCord = e.getX();
      int yCord = e.getY();
      for (int i = 0; i < FSObjects.size(); i++) {
        ImageIcon imageIcon = FSObjects.get(i).getImageIcon();
        int imageWidth = imageIcon.getIconWidth();
        int imageHeight = imageIcon.getIconWidth() + 12;
        int iAdj = i;
        if (i >= 9) iAdj = i - 9 * (i / 9);
        int xMin = 49 + iAdj * 2 * imageWidth;
        int xMax = 80 + iAdj * 2 * imageWidth;

        int yMin = 100 + ((imageHeight + 40) * (int) (i / 9.0));
        int yMax = 150 + ((imageHeight + 40) * (int) (i / 9.0));
        if (xCord >= xMin && xCord <= xMax && yCord >= yMin && yCord <= yMax) {
          if (fileSelected == i) clickCount++;
          if (clickCount % 2 == 0) {
            clickCount = -2;
            System.out.println("Double Click Detected");
          }

          fileSelected = i;
          System.out.println("File " + fileSelected);
          return;
        }
      }
      fileSelected = -1;
      clickCount = -1;
      System.out.println("X Cord: " + xCord + ", Y Cord: " + yCord);
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // not used, don't remove
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