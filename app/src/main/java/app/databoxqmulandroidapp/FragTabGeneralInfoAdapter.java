package app.databoxqmulandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.databoxqmulandroidapp.model.PersonalData;

/**
 * Created by MateusFelipe on 19/06/2015.
 */
public class FragTabGeneralInfoAdapter extends ArrayAdapter<PersonalData>{

    Context context;
    int layoutResourceId;
    List<PersonalData> data = null;
    LayoutInflater inflater;

    public FragTabGeneralInfoAdapter(Context context, LayoutInflater inflater, int layoutResourceId, List<PersonalData> data) {
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

        if(row == null)
        {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DataHolder();
            holder.txtNameValue = (TextView)row.findViewById(R.id.txtNameValue);
            holder.txtGenderValue = (TextView)row.findViewById(R.id.txtGenderValue);
            holder.txtLocaleValue = (TextView)row.findViewById(R.id.txtLocaleValue);
            holder.txtLinkValue = (TextView)row.findViewById(R.id.txtLinkValue);
            holder.txtEmailValue = (TextView)row.findViewById(R.id.txtEmailValue);
            holder.txtAgeValue = (TextView)row.findViewById(R.id.txtAgeValue);

            row.setTag(holder);
        }
        else
        {
            holder = (DataHolder)row.getTag();
        }

        PersonalData personal = data.get(position);
        holder.txtNameValue.setText(personal.getmName());
        holder.txtGenderValue.setText(personal.getmGender());
        holder.txtLocaleValue.setText(personal.getmLocale());
        holder.txtLinkValue.setText(personal.getmLink());
        holder.txtEmailValue.setText(personal.getmEmail());

        if( personal.getmAgeMax() > 0 ){
            holder.txtAgeValue.setText(personal.getmAgeMin() + " - " + personal.getmAgeMax());
        }else{
            holder.txtAgeValue.setText(personal.getmAgeMin() + "");
        }


        return row;
    }

    static class DataHolder
    {
        TextView txtNameValue;
        TextView txtGenderValue;
        TextView txtLocaleValue;
        TextView txtLinkValue;
        TextView txtEmailValue;
        TextView txtAgeValue;
    }
}