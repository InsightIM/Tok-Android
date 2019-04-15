package im.tox.tox4j.impl.jni;

public class ToxLoadJniLibrary {
    public synchronized static void load(String name) {
        try {
            System.loadLibrary(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
