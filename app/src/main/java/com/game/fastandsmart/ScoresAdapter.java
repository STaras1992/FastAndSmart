package com.game.fastandsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoresAdapter extends BaseAdapter {

        private ArrayList<BestScoresActivity.DataList> records;
        private Context context;

        public ScoresAdapter(Context context, ArrayList<BestScoresActivity.DataList> records) {
            super();
            this.records = records;
            this.context = context;
        }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int i) {
        return records.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.row, parent, false);
            }
            BestScoresActivity.DataList data = records.get(position);

            ((TextView) view.findViewById(R.id.nameDynamic)).setText(data.name);
            ((TextView) view.findViewById(R.id.scoreDynamic)).setText(String.valueOf(data.score));
            //((TextView) view.findViewById(R.id.dateDynamic)).setText(data.date);

            return view;
        }
}
