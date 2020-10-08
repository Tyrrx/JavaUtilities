package vega.swing.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 07.10.2020, 16:34
 */

public interface GenericActionListener<T> extends ActionListener {

    void action(GenericActionEvent<T> actionListener);

    @Override
    default void actionPerformed(ActionEvent actionEvent) {
        this.action(new GenericActionEvent<T>(
            actionEvent.getSource(),
            actionEvent.getID(),
            actionEvent.getActionCommand(),
            actionEvent.getWhen(),
            actionEvent.getModifiers()));
    }
}
