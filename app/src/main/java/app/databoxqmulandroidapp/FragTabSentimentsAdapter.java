package app.databoxqmulandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import app.databoxqmulandroidapp.model.TweetSentiment;

/**
 * Created by MateusFelipe on 19/06/2015.
 */
public class FragTabSentimentsAdapter extends ArrayAdapter<TweetSentiment> {

    Context context;
    int layoutResourceId;
    List<TweetSentiment> data = null;
    LayoutInflater inflater;

    public FragTabSentimentsAdapter(Context context, LayoutInflater inflater, int layoutResourceId, List<TweetSentiment> data) {
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
            holder.txtTweetTextValue = (TextView) row.findViewById(R.id.txtTweetTextValue);
            holder.txtSentimentValue = (TextView) row.findViewById(R.id.txtSentimentValue);
            holder.txtConfidenceValue = (TextView) row.findViewById(R.id.txtConfidenceValue);

            row.setTag(holder);
        } else {
            holder = (DataHolder) row.getTag();
        }

        TweetSentiment sentiment = data.get(position);
        holder.txtTweetTextValue.setText(sentiment.getmTextTweet() + "");
        holder.txtSentimentValue.setText(sentiment.getmSentiment());
        holder.txtConfidenceValue.setText(sentiment.getmConfidence() + "");

        return row;
    }

    static class DataHolder {
        TextView txtTweetTextValue;
        TextView txtSentimentValue;
        TextView txtConfidenceValue;
    }
}
