import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class UIConstants {

  // Colors
  public static final Color COLOR_WHITE = new Color(220, 220, 220);
  public static final Color COLOR_GRAY = new Color(54, 59, 65);
  public static final Color COLOR_RED = new Color(243, 15, 15);
  public static final Color COLOR_BLACK = Color.black;

  // Dimensions
  public static final Dimension PANEL_SIZE = new Dimension(900, 600);
  public static final int TOOLBAR_HEIGHT = 70;
  public static final int BOTTOM_BAR_Y_AXIS = 580;
  public static final int BOTTOM_BAR_TEXT_Y_AXIS = 595;

  // Buttons
  public static final Dimension BUTTON_SIZE = new Dimension(100, 30);
  public static final int BUTTON_Y_AXIS = 20;
  public static final int NEW_FILE_BUTTON_X_AXIS = 12;
  public static final int NEW_FOLDER_BUTTON_X_AXIS = NEW_FILE_BUTTON_X_AXIS + BUTTON_SIZE.width + 8;
  public static final int RENAME_BUTTON_X_AXIS = 2 * (NEW_FOLDER_BUTTON_X_AXIS);
  public static final int DELETE_BUTTON_X_AXIS = 3 * (NEW_FOLDER_BUTTON_X_AXIS - 8) + 24;
  public static final int GO_BACK_BUTTON_X_AXIS = PANEL_SIZE.width - BUTTON_SIZE.width - 10;

  // Font
  public static final Font FONT_HELVETICA_BOLD_12 = new Font("Helvetica", Font.BOLD, 12);

  // File display
  public static final int FILE_ICON_START_X = 40;
  public static final int FILE_ICON_START_Y = 100;
  public static final int FILE_ICON_SPACING_X = 2;
  public static final int FILE_ICON_SPACING_Y = 100;
  public static final int MAX_ICONS_PER_ROW = 9;
  public static final int FILE_NAME_MAX_LENGTH = 5;
  public static final int FILE_LABEL_Y_OFFSET = 10;
  public static final int MAX_OBJECTS_PER_DIRECTORY = 45;

  // Click detection
  public static final int CLICK_X_MIN_OFFSET = 9;
  public static final int CLICK_X_MAX_OFFSET = 40;
  public static final int CLICK_Y_MIN_OFFSET = 30;
  public static final int CLICK_Y_MAX_OFFSET = 80;

  // Path
  public static final int PATH_STRING_X_OFFSET = 20;

}