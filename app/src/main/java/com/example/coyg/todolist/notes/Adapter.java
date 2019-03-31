package com.example.coyg.todolist.notes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coyg.todolist.R;
import com.example.coyg.todolist.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
{
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    final private ItemClickListener mItemClickListener;
    private List<TaskEntry> mTaskEntries;
    private Context mContext;



    public Adapter(Context context, ItemClickListener listener)
    {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter, parent, false);

        return new Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position)
    {
        TaskEntry taskEntry = mTaskEntries.get(position);

        String description = taskEntry.getDescription();
        String updatedAt = taskEntry.getUpdatedAt();

        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
    }

    public interface ItemClickListener
    {
        void onItemClickActionListener(String position);
    }

    @Override
    public int getItemCount()
    {
        if (mTaskEntries == null)
        {
            return 0;
        }
        return mTaskEntries.size();
    }

    public List<TaskEntry> getmTaskEntries()
    {
        return mTaskEntries;
    }

    public void setTasks(List<TaskEntry> taskEntries)
    {
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView taskDescriptionView;
        TextView updatedAtView;

        public ViewHolder(View itemView)
        {
            super (itemView);

            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            String elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickActionListener(elementId);
        }
    }
}