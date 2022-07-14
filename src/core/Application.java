package core;

import data.ObjectComponent;
import data.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class Application {

    static int FPS_LIMIT = 2000;
    static boolean start = false, running = false, isFullscreen = false;
    static String state = "def";
    static JFrame window;
    static Handler handler = new Handler();
    static Thread drawThread = new Thread(Application::draw);
    static final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
    static final GraphicsDevice g_Device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public static void main(String[] args) {
        createConfigWindow();

        //The following code pauses the thread reading this code using wait()
        //The window runs on a separate thread and will notify this current thread to continue when the player presses "Play"
        while (!start) { //if the thread unpauses without proper conditions it will be paused once again
            synchronized (Application.class) {
                try { Application.class.wait(); }
                catch (InterruptedException ignored) {
                    //Ignores any thread interrupts
                }
            }
        }

        running = true;
        init();
        start();
    }

    static void init() {
        load();
        createGameWindow();
    }

    static void load() {
        ObjectComponent.registerComponent("Transform", Transform::new);
    }

    static void start() {
        double updateCap = 1000f / 60,
               unprocessedTime = 0,
               lastTime, newTime;

        Debug.enableDebug();
        lastTime = System.nanoTime();
        drawThread.start();

        while (running) {
            newTime = System.nanoTime();
            unprocessedTime += (newTime - lastTime) / 1000000;
            lastTime = newTime;
            //following code gets caught up on updates when lagging behind
            while (unprocessedTime >= updateCap) {
                unprocessedTime -= updateCap;
                //update();
            }
        }
    }

    static void draw() {
        double frameCap = 1000f / FPS_LIMIT,
               unprocessedTime = 0,
               lastTime, newTime;

        BufferStrategy bs;
        Graphics2D graphics;

        if (window.getBufferStrategy() == null) window.createBufferStrategy(2);
        bs = window.getBufferStrategy();
        lastTime = System.nanoTime();

        while (running) {
            newTime = System.nanoTime();
            unprocessedTime += (newTime - lastTime) / 1000000;
            lastTime = newTime;

            if (unprocessedTime >= frameCap) {
                unprocessedTime -= frameCap;

                graphics = (Graphics2D) bs.getDrawGraphics();
                graphics.setColor(Color.black);
                graphics.fillRect(0,0, Application.window.getWidth(), Application.window.getHeight());

                //Game.render(graphics);

                graphics.dispose();
                bs.show();

                if (Debug.isDebugMode()) Debug.fps++;
            }
        }
    }

    static void createConfigWindow() {
        window = new JFrame("Config");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(960, 540);
        window.setResizable(true);
        window.setLayout(new FlowLayout());

        //Creates the start button
        JButton startButton = new JButton("Play");
        startButton.addActionListener(e -> {
            if (e.getSource() == startButton) {

                //Notifies the main thread to continue
                synchronized (Application.class) {
                    start = true;
                    Application.class.notify();
                }
            }
        });

        //Creates the Fullscreen checkbox
        JCheckBox fullScreenMode = new JCheckBox("Fullscreen");
        fullScreenMode.addActionListener(e -> {
            if (e.getSource() == fullScreenMode) {
                isFullscreen = fullScreenMode.isSelected();
            }
        });

        //Creates the commandListener
        KeyListener commandListener = new KeyListener() {
            boolean ALT_KEY0 = false, ALT_KEY1 = false, DEV_MODE = false;

            @Override public void keyTyped(KeyEvent e) { /*Ignored Override*/ }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT)    ALT_KEY0 = true;
                if (e.getKeyCode() == KeyEvent.VK_CONTROL)  ALT_KEY1 = true;
                if (e.getKeyCode() == KeyEvent.VK_D)        DEV_MODE = true;
                if (ALT_KEY0 && ALT_KEY1 && DEV_MODE) {
                    //Notifies the main thread to continue and changes the game's state to dev mode
                    synchronized (Application.class) {
                        start = true;
                        state = "dev";
                        Application.class.notify();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //Resets the key state if the player releases the key, this ensures that the player must hold down all 3 keys at the same time
                if (e.getKeyCode() == KeyEvent.VK_SHIFT)    ALT_KEY0 = false;
                if (e.getKeyCode() == KeyEvent.VK_CONTROL)  ALT_KEY1 = false;
                if (e.getKeyCode() == KeyEvent.VK_D)        DEV_MODE = false;
            }
        };

        //sets up the components
        window.add(startButton);
        window.add(fullScreenMode);
        window.pack();

        //gets the window user ready
        window.addKeyListener(commandListener);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setFocusable(true);
    }

    static void createGameWindow() {
        //disposes the config window and sets it to a new instance
        window.dispose();
        window = new JFrame();

        //sets up the window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(resolution.width, resolution.height);
        window.setResizable(false);
        window.setLayout(null);

        //Gets the window user ready
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setFocusable(true);

        //Makes the game fullscreen
        //Locks the FPS at 60 also?? IDK
        if (isFullscreen) g_Device.setFullScreenWindow(window);
    }
}
