package com.example.coyg.todolist.remainders;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coyg.todolist.R;
import com.example.coyg.todolist.database.RemainderEntry;
import java.util.List;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.ViewHolder>
{
    private List<RemainderEntry> remainderEntries;
    private Context mContext;

    public RemainderAdapter(Context context)
    {
        mContext = context;
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
    public void onBindViewHolder(@NonNull RemainderAdapter.ViewHolder viewHolder, final int i)
    {
        final RemainderEntry remainderEntry = remainderEntries.get(i);
        String remainder_name = remainderEntry.getName ();
        viewHolder.remainder_name.setText (remainder_name);

        viewHolder.rv.setOnClickListener (new View.OnClickListener ()
        {
            @Override
            public void onClick(View v)
            {
                double lat = remainderEntry.getLat ();
                double lng = remainderEntry.getLng ();
                String type = remainderEntry.getType ();

                Intent intent = new Intent (mContext, MapsActivity.class);
                intent.putExtra (MapsActivity.LAT_SAVED, lat);
                intent.putExtra (MapsActivity.LNG_SAVED, lng);
                intent.putExtra (MapsActivity.TYPE_SAVED, type);
                mContext.startActivity (intent);
            }
        });
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

    public List<RemainderEntry> getRemainderEntriess()
    {
        return remainderEntries;
    }

    public void setRemainders(List<RemainderEntry> mRemainderEntries)
    {
        remainderEntries = mRemainderEntries;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rv;
        TextView remainder_name;

        public ViewHolder(@NonNull View itemView)
        {
            super (itemView);

            remainder_name = itemView.findViewById(R.id.remainder_name);
            rv = itemView.findViewById (R.id.rv);
        }
    }
}
