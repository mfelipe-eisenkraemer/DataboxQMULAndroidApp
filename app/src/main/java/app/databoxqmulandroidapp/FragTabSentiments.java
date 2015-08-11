package app.databoxqmulandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.List;

import app.databoxqmulandroidapp.model.TweetSentiment;
import app.databoxqmulandroidapp.model.TweetSentimentDataSource;

/**
 * Created by MateusFelipe on 18/06/2015.
 */
public class FragTabSentiments extends Fragment {

    private ListView listView;
    private TweetSentimentDataSource tweetSentimentDataSource;
    private List<TweetSentiment> tweetSentimentList;
    private FragTabSentimentsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_sentiments,container,false);

        listView = (ListView) v.findViewById(R.id.tab_sentiment_list);

        tweetSentimentDataSource = new TweetSentimentDataSource(getActivity().getApplicationContext());


        tweetSentimentDataSource.open();
        tweetSentimentList = tweetSentimentDataSource.getAllAvaiableForAnalysisTweetSentiment();
        tweetSentimentDataSource.close();

        adapter = new FragTabSentimentsAdapter(getActivity().getApplicationContext(), inflater,
                R.layout.frag_tab_sentiments_item_row, tweetSentimentList);

        listView.setEmptyView(v.findViewById(R.id.emptyElement));
        listView.setAdapter(adapter);

        return v;
    }
}