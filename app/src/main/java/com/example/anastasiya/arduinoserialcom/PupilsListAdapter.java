package com.example.anastasiya.arduinoserialcom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PupilsListAdapter extends RecyclerView.Adapter<PupilsListAdapter.ViewHolder> {
    private String[] mDataset;
    private Context context;

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

    public PupilsListAdapter(String[] myDataset) {
        mDataset = myDataset;
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
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

