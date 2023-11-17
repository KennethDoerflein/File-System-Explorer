import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FileSystemExplorer extends JPanel implements ActionListener {
  Color white = new Color(220, 220, 220);
  Color gray = new Color(54, 59, 65);
  static JButton newFileButton;
  static JFrame frame;
  static ArrayList<FSObject> FSObjects;
  final static int toolBarHeight = 70;

  public FileSystemExplorer() {
    Dimension size = new Dimension(900, 600); // size of the panel
    setPreferredSize(size);
    setMaximumSize(size);
    setMinimumSize(size);
    setLayout(null);
    newFileButton = new JButton("New File");
    newFileButton.addActionListener(this);
    newFileButton.setBounds(300, 250, 100, 30);
    newFileButton.setVisible(true);
    add(newFileButton);
    newFileButton.setLocation(12, 20);
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
  }

  public void drawUI(Graphics page) {
    // SET FONT
    Font font = new Font("Helvetica", Font.BOLD, 12);
    page.setFont(font);
    for (int i = 0; i < FSObjects.size(); i++) {
      FSObject tempObject = FSObjects.get(i);
      String imagePath = null;

      if (tempObject.getType().equals("file")) imagePath = "./fileIcon.png";
      else if (tempObject.getType().equals("folder")) imagePath = "./folderIcon.png";

//      if (i % 2 == 0) imagePath = "./fileIcon.png";
//      else imagePath = "./folderIcon.png";

      ImageIcon temp = new ImageIcon(imagePath);
      int rowSpacingX = 40 + i * 2 * temp.getIconWidth();
      if (rowSpacingX > 900) rowSpacingX -= 865 * (int) (i / 9.0);
      int rowSpacingY = 100 * (int) (i / 9.0) + toolBarHeight + temp.getIconHeight() / 2;

      page.drawImage(temp.getImage(), rowSpacingX, rowSpacingY, null);

      int labelX = rowSpacingX + temp.getIconWidth() / 5;
      int labelY = rowSpacingY + temp.getIconHeight() + 10;
      page.drawString(tempObject.getName(), labelX, labelY);
    }
    repaint();
  }

  public static void main(String[] args) {
    FSObjects = new ArrayList<>();
    frame = new JFrame("FEObject System Explorer");
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
      String fileName = JOptionPane.showInputDialog(frame, "What would you like you name the file?", null);
      FSObject temp;
      if (fileName != null && fileName.isEmpty()) fileName = "temp";
      if (fileName != null) {
        temp = new FSObject(fileName, "file");
        FSObjects.add(temp);
      }
    }
  }

}