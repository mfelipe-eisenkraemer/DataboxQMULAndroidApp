package app.databoxqmulandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import app.databoxqmulandroidapp.model.Trend;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class FragTabTrendsAdapter extends ArrayAdapter<Trend> {

    Context context;
    int layoutResourceId;
    List<Trend> data = null;
    LayoutInflater inflater;

    public FragTabTrendsAdapter(Context context, LayoutInflater inflater, int layoutResourceId, List<Trend> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.inflater = inflater;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DataHolder holder = null;

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DataHolder();
            holder.txtTermValue = (TextView) row.findViewById(R.id.txtTermValue);
            holder.txtCountValue = (TextView) row.findViewById(R.id.txtCountValue);

            row.setTag(holder);
        } else {
            holder = (DataHolder) row.getTag();
        }

        Trend trend = data.get(position);
        holder.txtTermValue.setText(trend.getmTerm() + "");
        holder.txtCountValue.setText(trend.getmCount() + "");

        return row;
    }

    static class DataHolder {
        TextView txtTermValue;
        TextView txtCountValue;
    }
}
