package vega.swing;


import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Frame extends JFrame {


    public void addRange(Collection<Component> components) {
        for (Component component : components) {
            this.add(component);
        }
    }

    public void addRange(Stream<Component> componentStream) {
        this.addRange(componentStream.collect(Collectors.toList()));
    }

    private void initDefaults() {
        this.setTitle(this.getClass().getSimpleName());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
