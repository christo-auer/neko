package neko;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import clojure.java.api.Clojure;
import clojure.lang.Var;
import clojure.lang.IFn;
import java.lang.reflect.Method;

public class App extends Application {

    private static String TAG = "neko.App";
    public static Application instance;

    @Override
    public void onCreate() {
        instance = this;
        try {
            Class dalvikCLclass = Class.forName("clojure.lang.DalvikDynamicClassLoader");
            Method setContext = dalvikCLclass.getMethod("setContext", Context.class);
            setContext.invoke(null, this);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "DalvikDynamicClassLoader is not found, probably Skummet is used.");
        } catch (Exception e) {
            Log.e(TAG, "setContext method not found, check if your Clojure dependency is correct.");
        }
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
