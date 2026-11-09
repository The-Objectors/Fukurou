package com.fukurou;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.EnumSet;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

public class EditorActivityHandler {
    private final EditorData data;
    
    private final Set<Window> attached = Collections.newSetFromMap(new WeakHashMap<Window, Boolean>());
    
    private final Set<KeyCode> heldKeys = EnumSet.noneOf(KeyCode.class);

    private final EventHandler<KeyEvent> pressedFilter = this::onKeyPressed;
    private final EventHandler<KeyEvent> releasedFilter = this::onKeyReleased;

    public EditorActivityHandler(EditorData data){
        this.data = data;
    }

    //Attach key press handler to opened windows in BlueJ JavaFX thread
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

    private void onKeyPressed(KeyEvent e){
        if(!heldKeys.add(e.getCode())){
            return;
        }

        if(!isEditingKey(e) || !isInEditor(e)){
            return;
        }

        data.recordKeystroke(System.nanoTime() / 1_000_000);
    }

    private void onKeyReleased(KeyEvent e){
        heldKeys.remove(e.getCode());
    }

    private boolean isEditingKey(KeyEvent e){
        KeyCode code = e.getCode();
        return !code.isModifierKey() 
                && !code.isNavigationKey()
                && !code.isFunctionKey()
                && !e.isShortcutDown();
    }

    private boolean isInEditor(KeyEvent e){
        return e.getTarget().getClass().getName().startsWith("bluej.editor");
    }
}
