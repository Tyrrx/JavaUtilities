package vega.swing.components;

import vega.swing.events.GenericActionListener;

import javax.swing.*;

public class ToggleButton<T> extends JToggleButton {

    private T value;

    public ToggleButton() {
    }

    public ToggleButton(T value) {
        this.value = value;
    }

    public ToggleButton(Icon icon, T value) {
        super(icon);
        this.value = value;
    }

    public ToggleButton(Icon icon, boolean selected, T value) {
        super(icon, selected);
        this.value = value;
    }

    public ToggleButton(String text, T value) {
        super(text);
        this.value = value;
    }

    public ToggleButton(String text, boolean selected, T value) {
        super(text, selected);
        this.value = value;
    }

    public ToggleButton(Action a, T value) {
        super(a);
        this.value = value;
    }

    public ToggleButton(String text, Icon icon, T value) {
        super(text, icon);
        this.value = value;
    }

    public ToggleButton(String text, Icon icon, boolean selected, T value) {
        super(text, icon, selected);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void addGenericActionListener(GenericActionListener<ToggleButton<T>> listener) {
        super.addActionListener(listener);
    }

    public static void main(String[] args) {
    }
}
