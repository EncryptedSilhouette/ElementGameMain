package core;

import data.ObjectComponent;
import data.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

public class Application {
    public static boolean running = false;
    private static boolean debug = false;
    private static String state = "def";
    private static Application instance;

    private int ticks = 0, tickRate = 0, TICK_LIMIT = 60,
                frames = 0, frameRate = 0, FRAME_LIMIT = 1000;
    private boolean isFullscreen = false;
    private JFrame window;
    private final Thread drawThread = new Thread(this::draw);
    private final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
    private final GraphicsDevice g_Device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private final HashMap<String, Task<Object>> tasks = new HashMap<>();
    private final HashMap<String, Task<Graphics2D>> renderTasks = new HashMap<>();

    private Application() {
        instance = this;
    }

    private void init() {
        load();
        createGameWindow();
    }

    private void load() {
        ObjectComponent.load();
        ResourceManager.load();
    }

    public void start() {
        double updateCap = 1000f / TICK_LIMIT,
               startTime, lastTime, newTime,
               unprocessedTime = 0;

        setState("config");
        createConfigWindow();

        //The following code pauses the thread reading this code using wait()
        //The window runs on a separate thread and will notify this current thread to continue when the player.json presses "Play"
        while (!running) { //if the thread unpauses without proper conditions it will be paused once again
            synchronized (this) {
                try { wait(); }
                catch (InterruptedException ignored) {
                    //Ignores any thread interrupts
                }
            }
        }

        init();
        Scene.generateScene("dev_room");

        drawThread.start();
        startTime = System.currentTimeMillis();
        lastTime = System.nanoTime();

        //Game loop
        while (running) {
            newTime = System.nanoTime();
            unprocessedTime += (newTime - lastTime) / 1000000;
            lastTime = newTime;

            while (unprocessedTime >= updateCap) {
                try {
                    unprocessedTime -= updateCap;

                    //Update
                    Handler.getInstance().update();

                    //Preform extra tasks
                    for (Task<Object> task: tasks.values()) {
                         task.task(null);
                    }
                    ticks++;

                    //Update ticks per second
                    if (System.currentTimeMillis() - startTime >= 1000) {
                        startTime = System.currentTimeMillis();
                        tickRate = ticks;
                        ticks = 0;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {

        //java's garbage collector is... fun, hence why i declare everything outside of loops when i can; this literally led to a 12.5% increase in performance...
        double frameCap = 1000f / FRAME_LIMIT,
               startTime, lastTime, newTime,
               unprocessedTime = 0;

        BufferStrategy bs;
        Graphics2D graphics = null;

        if (window.getBufferStrategy() == null) window.createBufferStrategy(2);
        bs = window.getBufferStrategy();
        startTime = System.currentTimeMillis();
        lastTime = System.nanoTime();

        //Render loop
        while (running) {
            newTime = System.nanoTime();
            unprocessedTime += (newTime - lastTime) / 1000000;
            lastTime = newTime;

            try {
                while (unprocessedTime >= frameCap) {
                    unprocessedTime -= frameCap;
                    graphics = (Graphics2D) bs.getDrawGraphics();
                    graphics.setColor(Color.black);
                    graphics.fillRect(0,0, window.getWidth(), window.getHeight());

                    //Render everything and updates frames
                    Handler.getInstance().render(graphics);

                    //Preform extra render tasks
                    for (Task<Graphics2D> renderTask: renderTasks.values()) {
                        renderTask.task(graphics);
                    }
                    frames++;

                    //update frames per second
                    if (System.currentTimeMillis() - startTime >= 1000) {
                        startTime = System.currentTimeMillis();
                        frameRate = frames;
                        frames = 0;
                    }

                    graphics.dispose();
                    bs.show();
                }
            }
            catch (Exception e){
                //If there is an error with rendering, it will attempt to fix common issues and dispose the graphics
                if (window.getBufferStrategy() == null) {
                    window.createBufferStrategy(2);
                    bs = window.getBufferStrategy();
                }
                if (graphics != null) graphics.dispose();
            }
        }
    }

    private void createConfigWindow() {
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
                synchronized (Application.getInstance()) {
                    Application.running = true;
                    Application.getInstance().notify();
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

        //sets up the components
        window.add(startButton);
        window.add(fullScreenMode);
        window.pack();

        //gets the window user ready
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setFocusable(true);
    }

    private void createGameWindow() {
        //disposes the config window and sets it to a new instance
        window.dispose();
        window = new JFrame();

        //sets up the window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(resolution.width, resolution.height);
        window.setIgnoreRepaint(true);
        window.setResizable(false);
        window.setLayout(null);

        //Gets the window user ready
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setFocusable(true);
        window.addKeyListener(Input.createKeyListener());

        //Makes the game fullscreen
        //Locks the FPS at 60 also?? IDK something about BufferStrategy this page flipping that
        if (isFullscreen) g_Device.setFullScreenWindow(window);

        addTask("debug_command", (ignored) -> {
            if (Input.getKeyState(KeyEvent.VK_SHIFT) &&
                Input.getKeyState(KeyEvent.VK_CONTROL) &&
                Input.getKeyState(KeyEvent.VK_D)) {
                showPerformanceStats(!debug);
            }
        });
    }

    //Created a task system to kinda act like C#'s delegates, fuck you java...
    //If you need more info see Task
    public void addTask(String taskID, Task<Object> task) {
        tasks.put(taskID, task);
    }

    public void addRenderTask(String taskID, Task<Graphics2D> renderTask) {
        renderTasks.put(taskID, renderTask);
    }

    public void removeTask(String taskID) {
        tasks.remove(taskID);
    }

    public void removeRenderTask(String taskID) {
        renderTasks.remove(taskID);
    }

    public static void showTickRate(boolean showFPS) {
        //renders FPS if showFPS is true
        if (showFPS) {
            instance.addRenderTask("display_tickrate", (graphics) -> {
                graphics.setColor(Color.RED);
                graphics.drawString("Ticks: " + instance.tickRate + ", Limit: " + instance.TICK_LIMIT,16, 64);
            });
        }
        else {
            instance.removeRenderTask("display_tickrate");
        }
    }

    public static void showFrameRate(boolean showFPS) {
        //renders FPS if showFPS is true
        if (showFPS) {
            instance.addRenderTask("display_framerate", (graphics) -> {
                graphics.setColor(Color.green);
                graphics.drawString("FPS: " + instance.frameRate + ", Limit: " + instance.FRAME_LIMIT, 16, 80);
            });
        }
        else {
            instance.removeRenderTask("display_framerate");
        }
    }

    public static void showPerformanceStats(boolean val) {
        debug = val;
        showTickRate(val);
        showFrameRate(val);
    }

    public static void setState(String val) {
        state = val;
    }

    public static Application CreateInstance() {
        return new Application();
    }

    public static Application getInstance() {
        return instance;
    }

    public static String getState() {
        return state;
    }

    public JFrame getWindow() {
        return window;
    }

    public static boolean debugEnabled() {
        return debug;
    }
}
