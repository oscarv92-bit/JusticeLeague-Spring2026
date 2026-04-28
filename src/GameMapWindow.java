import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class GameMapWindow {

    private JFrame  frame;
    private boolean visible;

    public GameMapWindow() {
        frame = new JFrame("Dunkin City Map");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        ImageIcon mapIcon = new ImageIcon("src/image/dunkin_map.png");
        JLabel    mapLabel = new JLabel(mapIcon);

        JScrollPane scrollPane = new JScrollPane(mapLabel);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setSize(1000, 750);
        frame.setLocationRelativeTo(null);
        visible = false;
    }

    public void showMap() {
        frame.setVisible(true);
        visible = true;
    }

    public void hideMap() {
        frame.setVisible(false);
        visible = false;
    }

    public boolean isVisible() { return visible; }
}