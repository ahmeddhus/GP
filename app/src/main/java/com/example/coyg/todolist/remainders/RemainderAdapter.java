package com.example.coyg.todolist.remainders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coyg.todolist.R;
import com.example.coyg.todolist.database.RemainderEntry;
import java.util.List;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.ViewHolder>
{
    final private ItemClickListener mItemClickListener;
    private List<RemainderEntry> remainderEntries;
    private Context mContext;

    public RemainderAdapter(Context context, ItemClickListener listener)
    {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RemainderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.remainder_item, viewGroup, false);

        return new RemainderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemainderAdapter.ViewHolder viewHolder, int i)
    {
        RemainderEntry remainderEntry = remainderEntries.get(i);
        String remainder_name = remainderEntry.getLatLng ();
        viewHolder.remainder_name.setText (remainder_name);
    }

    @Override
    public int getItemCount()
    {
        if (remainderEntries == null)
        {
            return 0;
        }
        return remainderEntries.size();
    }

    public interface ItemClickListener
    {
        void onItemClickActionListener(String position);
    }

    public List<RemainderEntry> getRemainderEntriess()
    {
        return remainderEntries;
    }

    public void setRemainders(List<RemainderEntry> mRemainderEntries)
    {
        remainderEntries = mRemainderEntries;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView remainder_name;

        public ViewHolder(@NonNull View itemView)
        {
            super (itemView);

            remainder_name = itemView.findViewById(R.id.remainder_name);
        }

        @Override
        public void onClick(View view)
        {
//            String elementId = remainderEntries.get(getAdapterPosition()).getId();
//            mItemClickListener.onItemClickActionListener(elementId);
        }
    }
}
