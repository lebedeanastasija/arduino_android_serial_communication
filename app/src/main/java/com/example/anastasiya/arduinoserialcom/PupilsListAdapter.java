package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.squareup.picasso.Picasso;

public class PupilsListAdapter extends RecyclerView.Adapter<PupilsListAdapter.ViewHolder> {
    private String[] mDataset;
    private String[] mAvatarIds;
    private Context context;
    private Resources res;
    private FileLogger fileLogger;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            mTextView = (TextView) v.findViewById(R.id.tvPupilItem);
            mImageView = (ImageView) v.findViewById(R.id.ivPupilItem);
        }
    }

    public PupilsListAdapter(String[] myDataset, String[] avatarIds, Context ctx, Activity activity) {
        mDataset = myDataset;
        mAvatarIds = avatarIds;
        context = ctx;
        res = context.getResources();
        fileLogger = FileLogger.getInstance(context, activity);
    }

    @Override
    public PupilsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pupil_list_item, parent, false);
        PupilsListAdapter.ViewHolder vh = new PupilsListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PupilsListAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
        String url = res.getString(R.string.server_address) + "/pupils/avatar/" + mAvatarIds[position];
        Picasso.with(context)
        .load(url)
        .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

