package core;

public class Debug {

    static int fps = 0;
    private static boolean debugMode = false;

    public static synchronized void enableDebug() {

        if (debugMode) return;

        fps = 0;
        debugMode = true;

        //FPS Timer
        new Thread(()->{
            double startTime = System.currentTimeMillis();
            while (debugMode) {
                if (1000 < System.currentTimeMillis() - startTime) {
                    System.out.println(fps);
                    fps = 0;
                    startTime = System.currentTimeMillis();
                }
            }
        }).start();
    }

    public static synchronized void disableDebug() {
        debugMode = false;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }
}
