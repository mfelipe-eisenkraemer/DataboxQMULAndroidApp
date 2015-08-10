package app.databoxqmulandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.databoxqmulandroidapp.model.GoogleFit;

/**
 * Created by MateusFelipe on 22/07/2015.
 */
public class FragTabStepsAdapter extends ArrayAdapter<GoogleFit> {

    Context context;
    int layoutResourceId;
    List<GoogleFit> data = null;
    LayoutInflater inflater;

    public FragTabStepsAdapter(Context context, LayoutInflater inflater, int layoutResourceId, List<GoogleFit> data) {
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
            holder.txtDateValue = (TextView) row.findViewById(R.id.txtDateValue);
            holder.txtStepsValue = (TextView) row.findViewById(R.id.txtStepsValue);

            row.setTag(holder);
        } else {
            holder = (DataHolder) row.getTag();
        }

        GoogleFit googleFit = data.get(position);
        holder.txtDateValue.setText(googleFit.getDateString() + "");
        holder.txtStepsValue.setText(googleFit.getNumSteps() + "");

        return row;
    }

    static class DataHolder {
        TextView txtDateValue;
        TextView txtStepsValue;
    }
}
