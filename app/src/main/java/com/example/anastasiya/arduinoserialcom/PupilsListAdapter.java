package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.PupilHttpRequestTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PupilsListAdapter extends RecyclerView.Adapter<PupilsListAdapter.ViewHolder> {
    private List<String> mPupilNames;
    private List<String> mAvatarIds;
    private List<String> mPupilIds;
    private Context context;
    private Resources res;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public ImageView mImageView;
        public View mView;
        public ImageButton mImageButton;

        private ItemClickListener itemClickListener;
        private ItemClickListener removeClickListener;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            v.setOnClickListener(this);

            mTextView = (TextView) v.findViewById(R.id.tvPupilItem);
            mImageView = (ImageView) v.findViewById(R.id.ivPupilItem);
            mImageButton = (ImageButton) v.findViewById(R.id.ibDeletePupil);
            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   removeClickListener.onClick(v, getAdapterPosition());
                }
            });
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setRemoveClickListener(ItemClickListener itemClickListener) {
            this.removeClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }

    }

    public PupilsListAdapter(List<String> pupilNames, List<String> pupilIds, List<String> avatarIds, Context ctx, Activity activity) {
        mPupilNames = pupilNames;
        mPupilIds = pupilIds;
        mAvatarIds = avatarIds;
        context = ctx;
        res = context.getResources();
    }

    @Override
    public PupilsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pupil_list_item, parent, false);
        PupilsListAdapter.ViewHolder vh = new PupilsListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PupilsListAdapter.ViewHolder holder, int position) {

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(context, "Click: " + mPupilNames.get(position), Toast.LENGTH_LONG).show();
            }
        });

        holder.setRemoveClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                PupilHttpRequestTask asyncTask = new PupilHttpRequestTask(new IAsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        Toast.makeText(context, "Ученик удален успешно.", Toast.LENGTH_LONG).show();
                    }
                }, context, null);
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "removeById", mPupilIds.get(position));

                mPupilNames.remove(position);
                mPupilIds.remove(position);
                mAvatarIds.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mPupilNames.size());
            }
        });

        holder.mTextView.setText(mPupilNames.get(position));
        String url = res.getString(R.string.server_address) + "/pupils/avatar/" + mAvatarIds.get(position);
        Picasso.with(context)
        .load(url)
        .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mPupilNames.size();
    }
}

