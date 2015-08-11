package app.databoxqmulandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Date;
import java.util.List;

import app.databoxqmulandroidapp.database.DbTrend;
import app.databoxqmulandroidapp.model.Trend;
import app.databoxqmulandroidapp.model.TrendDataSource;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class FragTabTrends extends Fragment {

    private ListView listView;
    private TrendDataSource trendDataSource;
    private List<Trend> trendList;
    private FragTabTrendsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_trend, container, false);

        listView = (ListView) v.findViewById(R.id.tab_trend_list);

        trendDataSource = new TrendDataSource(getActivity().getApplicationContext());

        Date today = MyApplication.getTodayDate();
        String order = DbTrend.COLUMN_COUNT + " DESC";
        String limit = "50";

        trendDataSource.open();
        trendList = trendDataSource.getTrendsByDate(limit, order, today.getTime());
        trendDataSource.close();

        adapter = new FragTabTrendsAdapter(getActivity().getApplicationContext(), inflater,
                R.layout.frag_tab_trend_item_row, trendList);

        listView.setEmptyView(v.findViewById(R.id.emptyElement));
        listView.setAdapter(adapter);

        return v;
    }
}
