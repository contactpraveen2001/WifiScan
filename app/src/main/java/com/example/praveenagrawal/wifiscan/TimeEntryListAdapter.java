package com.example.praveenagrawal.wifiscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by praveen.agrawal on 09/05/17.
 */

public class TimeEntryListAdapter extends ArrayAdapter<TimeEntryData>
{
    public TimeEntryListAdapter(Context context, ArrayList<TimeEntryData> timeEntryDataArrayList)
    {
        super(context,0,timeEntryDataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_time_list,parent,false);
        LinearLayout listLayout = (LinearLayout) listItemView.findViewById(R.id.layout_time_entry_item);
        TimeEntryData data = getItem(position);
        listLayout.addView(getTextView(getContext(),"Date: " + data.date));
        listLayout.addView(getTextView(getContext(),"In time: " + data.inTime));
        if (!data.outTime.equals("0"))
        {
            listLayout.addView(getTextView(getContext(),"Out time: " + data.outTime));
        }
        listLayout.addView(getTextView(getContext(), "Total Time: " + data.totalTime));
        return  listItemView;
    }

    public TextView getTextView(Context context, String data)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getDpToInt(10);
        params.topMargin = getDpToInt(10);
        TextView textView = new TextView(context);
        textView.setText(data);
        textView.setLayoutParams(params);
        return textView;
    }
    public int getDpToInt(float value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }
}
