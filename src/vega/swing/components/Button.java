package vega.swing.components;


import vega.swing.events.GenericActionListener;

import javax.swing.*;

public class Button<T> extends JButton {

    public Button(String text, Icon icon, T value) {
        super(text, icon);
        this.value = value;
    }

    public Button(String text, T value) {
        super(text);
        this.value = value;
    }

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void addGenericActionListener(GenericActionListener<Button<T>> listener) {
        super.addActionListener(listener);
    }
}
