package vega.swing.components;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Panel extends JPanel {

    public void addRange(Collection<Component> components) {
        for (Component c : components) {
            this.add(c);
        }
    }

    public void addRange(Stream<Component> componentStream) {
        this.addRange(componentStream.collect(Collectors.toList()));
    }
}
