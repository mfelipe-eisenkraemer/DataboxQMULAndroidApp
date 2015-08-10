package app.databoxqmulandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.List;

import app.databoxqmulandroidapp.model.GoogleFit;
import app.databoxqmulandroidapp.model.GoogleFitDataSource;

/**
 * Created by MateusFelipe on 22/07/2015.
 */
public class FragTabSteps extends Fragment {

    private ListView listView;
    private GoogleFitDataSource googleFitDataSource;
    private List<GoogleFit> googleFitList;
    private FragTabStepsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_trend, container, false);

        listView = (ListView) v.findViewById(R.id.tab_trend_list);

        googleFitDataSource = new GoogleFitDataSource(getActivity().getApplicationContext());


        String orderQuery = " DESC ";
        googleFitDataSource.open();
        googleFitList = googleFitDataSource.getAllGoogleFit(orderQuery);
        googleFitDataSource.close();

        adapter = new FragTabStepsAdapter(getActivity().getApplicationContext(), inflater,
                R.layout.frag_tab_steps_item_row, googleFitList);

        listView.setEmptyView(v.findViewById(R.id.emptyElement));
        listView.setAdapter(adapter);

        return v;
    }
}
