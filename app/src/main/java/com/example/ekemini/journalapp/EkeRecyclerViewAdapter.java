package com.example.ekemini.journalapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EkeRecyclerViewAdapter extends
        RecyclerView.Adapter<EkeRecyclerViewAdapter.ViewHolder>{
        private Context context;
        private List<User> postInfoList;


    public EkeRecyclerViewAdapter(Context context, List<User> postList) {
        this.postInfoList = postList;
        this.context = context;

    }

    @NonNull
    @Override
    public EkeRecyclerViewAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_items,
                        parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EkeRecyclerViewAdapter
            .ViewHolder holder, final int position) {
        final User user = postInfoList.get(position);
        holder.textViewPost.setText(user.getUserPost());

        // Click event for post item in recyclerview
        holder.imageViewEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = user.getUserPost();
                String id = user.id;
                new EkePrefUtils(context).storeSelectedPost(string);
                new EkePrefUtils(context).storeSelectedItemPosition(position);
                new EkePrefUtils(context).storeSelectedDatabaseId(id);
                showDialog();
            }
        });

        // Click event for viewing a single post entry item in recyclerview
        holder.textViewPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = user.getUserPost();
                new EkePrefUtils(context).storeSelectedPost(string);
                singlePostView(string);
            }
        });

        holder.textViewPost.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Not Ready"+position, Toast.LENGTH_LONG).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return postInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewPost;
        ImageView imageViewEdit;

        ViewHolder(View itemView) {
            super(itemView);
            textViewPost = (TextView) itemView.findViewById(R.id.items);
            imageViewEdit = (ImageView) itemView.findViewById(R.id.imageEditPost);
        }
    }

    private void showDialog() {
        int mStackLevel = 0;
        mStackLevel++;

        String postToEdit = null;
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = ((FragmentActivity)context).getFragmentManager().beginTransaction();
        Fragment prev = ((FragmentActivity)context).getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = EkeDialogActivity.newInstance(mStackLevel);
        newFragment.show(ft, "Dialog");
    }

    private void singlePostView(String post){
        Intent intent = new Intent(context, EkeViewEntryActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra("post", post);
        context.startActivity(intent);
    }

}
