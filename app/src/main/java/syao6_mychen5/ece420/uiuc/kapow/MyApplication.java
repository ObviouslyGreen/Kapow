package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{

    private static Context context;

    public static Context getAppContext()
    {
        return MyApplication.context;
    }

    public void onCreate()
    {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
}
