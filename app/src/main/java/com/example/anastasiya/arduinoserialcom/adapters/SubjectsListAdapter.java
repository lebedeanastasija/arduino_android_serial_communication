package com.example.anastasiya.arduinoserialcom.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiya.arduinoserialcom.R;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.SubjectHttpRequestTask;

import java.util.List;

public class SubjectsListAdapter extends RecyclerView.Adapter<SubjectsListAdapter.ViewHolder> {
    private List<String> mSubjectNames;
    private List<String> mShortNames;
    private List<String> mSubjectIds;
    private Context context;
    private Resources res;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public TextView mSubjectName;
        public View mView;
        public ImageButton mImageButton;

        private ItemClickListener itemClickListener;
        private ItemClickListener removeClickListener;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            v.setOnClickListener(this);

            mTextView = (TextView) v.findViewById(R.id.tvSubjectItem);
            mSubjectName = (TextView) v.findViewById(R.id.tvSubjectName);
            mImageButton = (ImageButton) v.findViewById(R.id.ibDeleteSubject);
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

    public SubjectsListAdapter(List<String> subjectNames, List<String> shortNames, List<String> subjectIds, Context ctx, Activity activity) {
        mSubjectNames = subjectNames;
        mShortNames = shortNames;
        mSubjectIds = subjectIds;
        context = ctx;
        res = context.getResources();
    }

    @Override
    public SubjectsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_list_item, parent, false);
        SubjectsListAdapter.ViewHolder vh = new SubjectsListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SubjectsListAdapter.ViewHolder holder, int position) {

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(context, "Click: " + mSubjectNames.get(position), Toast.LENGTH_LONG).show();
            }
        });

        holder.setRemoveClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                SubjectHttpRequestTask asyncTask = new SubjectHttpRequestTask(new IAsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        Toast.makeText(context, "Предмет удален успешно.", Toast.LENGTH_LONG).show();
                    }
                }, context, null);
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "removeById", mSubjectIds.get(position));

                mSubjectNames.remove(position);
                mShortNames.remove(position);
                mSubjectIds.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mSubjectNames.size());
            }
        });

        holder.mTextView.setText(mShortNames.get(position));
        holder.mSubjectName.setText(mSubjectNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mSubjectNames.size();
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(mSubjectIds.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
