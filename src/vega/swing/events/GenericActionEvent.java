package vega.swing.events;

import java.awt.event.ActionEvent;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 07.10.2020, 16:40
 */

public class GenericActionEvent<T> extends ActionEvent {

    public GenericActionEvent(Object source, int id, String command, long when, int modifiers) {
        super(source, id, command, when, modifiers);
    }

    @Override
    public T getSource() {
        return (T) super.getSource();
    }
}
