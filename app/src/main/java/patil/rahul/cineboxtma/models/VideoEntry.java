package patil.rahul.cineboxtma.models;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

/**
 * Created by rahul on 28/2/18.
 */

public class VideoEntry {
    private final String text;
    private final String videoId;

    public VideoEntry(String text, String videoId) {
        this.text = text;
        this.videoId = videoId;
    }

    public String getText() {
        return text;
    }

    public String getVideoId() {
        return videoId;
    }

    // Utility methods for layouting.

    private static void setLayoutSize(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }
}
