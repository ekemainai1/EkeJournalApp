package com.example.ekemini.journalapp;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;

/**
 * Created by Ekemini on 10/7/2017.
 */


public class EkeDialogActivity extends DialogFragment {

        private ImageButton imageButtonEnqueue;
        private ImageButton imageButtonAdd;
        public static final String Broadcast_DELETE_POST =
                "com.example.ekemini.journalapp.DeletPost";

        int mNum;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        public static EkeDialogActivity newInstance(int num) {
            EkeDialogActivity f = new EkeDialogActivity();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments().getInt("num");

            // Pick a style based on the num.
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            switch ((mNum-1)%6) {
                case 1: style = DialogFragment.STYLE_NO_TITLE; break;
                case 2: style = DialogFragment.STYLE_NO_FRAME; break;
                case 3: style = DialogFragment.STYLE_NO_INPUT; break;
                case 4: style = DialogFragment.STYLE_NORMAL; break;
                case 5: style = DialogFragment.STYLE_NORMAL; break;
                case 6: style = DialogFragment.STYLE_NO_TITLE; break;
                case 7: style = DialogFragment.STYLE_NO_FRAME; break;
                case 8: style = DialogFragment.STYLE_NORMAL; break;
            }
            switch ((mNum-1)%6) {
                case 4: theme = android.R.style.Theme_Holo; break;
                case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
                case 6: theme = android.R.style.Theme_Holo_Light; break;
                case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
                case 8: theme = android.R.style.Theme_Holo_Light; break;
            }
            setStyle(style, theme);

        }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        window.setFlags(Window.FEATURE_CUSTOM_TITLE, Window.FEATURE_CUSTOM_TITLE);
        window.setGravity(Gravity.BOTTOM);

    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_eke_edit_dialog,
                    container, false);
            imageButtonAdd = (ImageButton) v.findViewById(R.id.add);
            imageButtonEnqueue = (ImageButton) v.findViewById(R.id.enqueue);


            // Watch for button clicks.
            imageButtonAdd.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // When button is clicked, call up to owning activity
                    EkePrefUtils ekePostId = new EkePrefUtils(getActivity());
                    String postId = ekePostId.loadSelectedDatabaseId();
                    Intent intent = new Intent();
                    intent.setAction(EkeDialogActivity.Broadcast_DELETE_POST);
                    intent.putExtra("positionId", postId);
                    getActivity().sendBroadcast(intent);
                    dismiss();
                }
            });

            imageButtonEnqueue.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // When button is clicked, call up to owning activity
                    EkePrefUtils ekePost = new EkePrefUtils(getActivity());
                    String post = ekePost.loadSelectedPost();
                    Intent intent = new Intent(getActivity(), EkeEditPostActivity.class);
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra("myPost", post);
                    startActivity(intent);
                    dismiss();
                }
            });

            return v;
        }

}
