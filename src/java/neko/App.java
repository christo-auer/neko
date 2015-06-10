package neko;

import android.app.Application;
import android.util.Log;
import clojure.java.api.Clojure;
import clojure.lang.DalvikDynamicClassLoader;
import clojure.lang.Var;
import clojure.lang.IFn;

public class App extends Application {

    private static String TAG = "neko.App";
    public static Application instance;

    @Override
    public void onCreate() {
        instance = this;
        DalvikDynamicClassLoader.setContext(this);
    }

    // This method is only necessary for asynchronous loading. Clojure is
    // perfectly capable of loading itself the first time anything from it is
    // called.
    public static void loadClojure() {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("neko.tools.repl"));

        IFn init = Clojure.var("neko.tools.repl", "init");
        init.invoke();
    }

    public static void loadAsynchronously(final String activityClass, final Runnable callback) {
        new Thread(Thread.currentThread().getThreadGroup(),
                   new Runnable(){
                       @Override
                       public void run() {
                           loadClojure();

                           try {
                               Class.forName(activityClass);
                           } catch (ClassNotFoundException e) {
                               Log.e(TAG, "Failed loading activity " + activityClass, e);
                           }

                           callback.run();
                       }
                   },
                   "ClojureLoadingThread",
                   1048576 // = 1MB, thread stack size in bytes
                   ).start();
    }

}
