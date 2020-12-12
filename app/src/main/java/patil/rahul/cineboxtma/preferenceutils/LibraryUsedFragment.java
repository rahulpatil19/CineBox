package patil.rahul.cineboxtma.preferenceutils;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import patil.rahul.cineboxtma.R;

public class LibraryUsedFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pref_layout_library_used, container, false);

        TextView fresco = view.findViewById(R.id.library_fresco);
        TextView volley = view.findViewById(R.id.library_volley);
        TextView circleIndicator = view.findViewById(R.id.library_circleIndicator);
        TextView expandableText = view.findViewById(R.id.library_expandableTextView);

        volley.setMovementMethod(LinkMovementMethod.getInstance());
        circleIndicator.setMovementMethod(LinkMovementMethod.getInstance());
        expandableText.setMovementMethod(LinkMovementMethod.getInstance());
        fresco.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
