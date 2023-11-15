import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileSystemExplorer extends JPanel {
  Color white = new Color(255, 255, 255);
  Color gray = new Color(54, 59, 65);

  public FileSystemExplorer() {
    Dimension size = new Dimension(900, 600); // size of the panel
    setPreferredSize(size);
    setMaximumSize(size);
    setMinimumSize(size);
    setLayout(null);
  }

  @Override
  public void paintComponent(Graphics page) {
    super.paintComponent(page);
    drawBoard(page);
  }

  public void drawBoard(Graphics page) {
    setBackground(white);
    page.setColor(gray);
    page.fillRect(0, 0, 900, 70);

  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("File System Explorer");
    frame.getContentPane();
    FileSystemExplorer gamePanel = new FileSystemExplorer();
    frame.add(gamePanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setVisible(true);
  }
}