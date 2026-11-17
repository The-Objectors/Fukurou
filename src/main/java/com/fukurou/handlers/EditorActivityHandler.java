package com.fukurou.handlers;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.WeakHashMap;

import com.fukurou.telemetry.EditorData;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

/**
 * Tracks editor activity through keystroke logging.
 * <p>
 * Hooks into the JavaFX thread of BlueJ to log keystrokes.
 * </p>
 * {@link EditorData} handles keystroke metric calculations.
 */

public class EditorActivityHandler {
    /** Receives a timestamp for each counted keystroke. */
    private final EditorData data;

    /** Windows that already have the key filters attached; weakly held so closed windows can be collected. */
    private final Set<Window> attached = Collections.newSetFromMap(new WeakHashMap<>());

    /** Keys currently held down, used to ignore auto-repeat presses. Cleared when a window loses focus. */
    private final Set<KeyCode> heldKeys = EnumSet.noneOf(KeyCode.class);

    /** Event filter for {@link KeyEvent#KEY_PRESSED}. */
    private final EventHandler<KeyEvent> pressedFilter = this::onKeyPressed;
    /** Event filter for {@link KeyEvent#KEY_RELEASED}. */
    private final EventHandler<KeyEvent> releasedFilter = this::onKeyReleased;

    /**
     * Creates a handler that reports keystrokes to the given editor data.
     *
     * @param data the editor metrics to record keystrokes in
     */
    public EditorActivityHandler(EditorData data){
        this.data = data;
    }

    /**
     * Attaches key filters to every open JavaFX window, and to any window opened
     * afterwards. The work is scheduled on the JavaFX application thread.
     */
    public void start(){
        Platform.runLater(() -> {
            for(Window w : Window.getWindows()){
                attach(w);
            }
            Window.getWindows().addListener((ListChangeListener<Window>) change ->{
                while(change.next()){
                    for(Window w : change.getAddedSubList()){
                        attach(w);
                    }
                }
            });
        });
    }

    /**
     * Adds the key filters and a focus listener to a window, once per window.
     *
     * @param w the window to attach to
     */
    private void attach(Window w){
        if(!attached.add(w)){
            return;
        }
        w.addEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
        w.addEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);

        w.focusedProperty().addListener((obs, wasFocused, isFocused) ->{
            if(!isFocused){
                heldKeys.clear();
            }
        });
    }

    /**
     * Records a keystroke if the key was not already held, is an editing key,
     * and was typed in a BlueJ editor.
     *
     * @param e the key-pressed event
     */
    private void onKeyPressed(KeyEvent e){
        if(!heldKeys.add(e.getCode())){
            return;
        }

        if(!isEditingKey(e) || !isInEditor(e)){
            return;
        }

        data.recordKeystroke(System.nanoTime() / 1_000_000);
    }

    /**
     * Marks the key as released.
     *
     * @param e the key-released event
     */
    private void onKeyReleased(KeyEvent e){
        heldKeys.remove(e.getCode());
    }

    /**
     * Whether the key counts as an edit: not a modifier, navigation or function
     * key, and not pressed with the shortcut modifier.
     *
     * @param e the key event
     * @return {@code true} if the key is an editing key
     */
    private boolean isEditingKey(KeyEvent e){
        KeyCode code = e.getCode();
        return !code.isModifierKey()
                && !code.isNavigationKey()
                && !code.isFunctionKey()
                && !e.isShortcutDown();
    }

    /**
     * Whether the event target belongs to BlueJ's editor (its class is in a
     * {@code bluej.editor} package).
     *
     * @param e the key event
     * @return {@code true} if the event targets a BlueJ editor component
     */
    private boolean isInEditor(KeyEvent e){
        return e.getTarget().getClass().getName().startsWith("bluej.editor");
    }
}
