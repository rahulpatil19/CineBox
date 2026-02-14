package patil.rahul.cineboxtma;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.gms.ads.MobileAds;

import okhttp3.OkHttpClient;

/**
 * Created by rahul on 12/3/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        ImagePipelineConfig config =
                OkHttpImagePipelineConfigFactory
                        .newBuilder(this, okHttpClient)
                        .setDownsampleEnabled(true)
                        .setResizeAndRotateEnabledForNetwork(true)
                        .build();

        Fresco.initialize(this, config);

//        MobileAds.initialize(this);
    }
}