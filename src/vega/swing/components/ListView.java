package vega.swing.components;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 07.10.2020, 18:34
 */

public class ListView<T extends Component> extends JPanel {

    private List<T> components;
    private GridBagConstraints gbc = new GridBagConstraints();

    public ListView() {
        super(new GridBagLayout());
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor =  GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        this.components = new LinkedList<>();
    }

    public ListView(List<T> components) {
        this();
        this.components = components;
    }

    public ListView<T> addComponent(T component) {
        this.components.add(component);
        this.updateView();
        return this;
    }

    public ListView<T> removeComponent(T component) {
        this.components.remove(component);
        this.updateView();
        return this;
    }

    private void updateView() {
        this.removeAll();
        for (Component component : components) {
            this.add(component, gbc);
        }
        this.revalidate();
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame jFrame = new JFrame();
        ListView<JPanel> jButtonComponentList = new ListView<>();

        for (int i = 0; i < 10; i++) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new GridLayout(2,1));
            JButton jButton = new JButton(String.valueOf(i));
            JButton jButton1 = new JButton(String.valueOf(i));
            jPanel.add(jButton);
            jPanel.add(jButton1);
            jButtonComponentList.addComponent(jPanel);
        }
        JScrollPane comp = new JScrollPane(jButtonComponentList);
        jFrame.add(comp);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
