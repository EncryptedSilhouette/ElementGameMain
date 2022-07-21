package core;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Objects;

public class Input {
    private static final HashMap<String, Integer> registeredKeys = new HashMap<>();
    private static final HashMap<Integer, Boolean> activeKeys = new HashMap<>();

    public synchronized static boolean getKeyState(int keyCode) {
        return activeKeys.get(keyCode) != null &&
               activeKeys.get(keyCode);
    }

    public synchronized static boolean getKeyState(String inputID) {
        return activeKeys.get(registeredKeys.get(inputID)) != null &&
               activeKeys.get(registeredKeys.get(inputID));
    }

    private synchronized static void setActiveState(int keyCode, boolean active) {
        activeKeys.put(keyCode, active);
    }

    public static void registerKey(String inputID, int keyCode) {
        registeredKeys.put(inputID, keyCode);
    }

    //The whole reason for all this chaos, is to give more control to the update thread to limit usage of the window thread.
    //The ONLY thing the KeyListener will do is update the state of the pressed key, it will not handle any logic.
    //This is to prevent any weird behavior that would be caused by processing logic on the window's event thread.
    public static KeyListener createKeyListener() {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                setActiveState(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setActiveState(e.getKeyCode(), false);
            }

            @Override
            public void keyTyped(KeyEvent e) { /*Ignored*/ }
        };
    }

    public static MouseListener createMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { /*Ignored*/ }
            @Override
            public void mousePressed(MouseEvent e) { /*Ignored*/ }
            @Override
            public void mouseReleased(MouseEvent e) { /*Ignored*/ }
            @Override
            public void mouseEntered(MouseEvent e) { /*Ignored*/ }
            @Override
            public void mouseExited(MouseEvent e) { /*Ignored*/ }
        };
    }

    public static MouseWheelListener createMouseWheelListener() {
        //since the MouseWheelListener only contains a single method, it basically acts as a functional interface, hence the lambda expression here
        return e -> { /*logic or sm IDK*/ };
    }
}
