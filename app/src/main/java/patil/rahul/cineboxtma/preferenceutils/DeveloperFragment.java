package patil.rahul.cineboxtma.preferenceutils;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import patil.rahul.cineboxtma.R;

public class DeveloperFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pref_layout_developer, container, false);

        TextView emailTextView = view.findViewById(R.id.follow_gmail);
        TextView facebookTextView = view.findViewById(R.id.follow_facebook);
        TextView instagramTextView = view.findViewById(R.id.follow_instagram);

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { String[] emailAddress = new String[]{"rahulpatil.19rp@gmail.com"};
             Intent intent = new Intent(Intent.ACTION_SENDTO);
             intent.setData(Uri.parse("mailto:"));
             intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
             if (intent.resolveActivity(getActivity().getPackageManager())!= null){
                 startActivity(intent);
                 dismiss();
             }
            }
        });

        facebookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.facebook.com/100022450005982");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                    dismiss();
                }
            }
        });

        instagramTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.instagram.com/rahulpatil.19");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                    dismiss();
                }
            }
        });
        return view;
    }
}
