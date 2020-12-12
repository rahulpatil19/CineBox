package patil.rahul.cineboxtma;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by rahul on 12/3/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
