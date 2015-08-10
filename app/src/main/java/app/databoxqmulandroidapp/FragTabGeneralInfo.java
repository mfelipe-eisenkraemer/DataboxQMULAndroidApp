package app.databoxqmulandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import app.databoxqmulandroidapp.model.PersonalData;
import app.databoxqmulandroidapp.model.PersonalDataDataSource;

/**
 * Created by MateusFelipe on 18/06/2015.
 */
public class FragTabGeneralInfo extends Fragment {

    private ListView listView;
    private PersonalDataDataSource personalDataDataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_general_info,container,false);

        listView = (ListView) v.findViewById(R.id.tab_general_list);

        personalDataDataSource = new PersonalDataDataSource(getActivity().getApplicationContext());
        List<PersonalData> personalDataList;

        personalDataDataSource.open();
        personalDataList = personalDataDataSource.getAllPersonalData();
        personalDataDataSource.close();

        FragTabGeneralInfoAdapter adapter = new FragTabGeneralInfoAdapter(getActivity().getApplicationContext(), inflater,
                R.layout.frag_tab_general_info_item_row, personalDataList);

        listView.setAdapter(adapter);
        listView.setEmptyView(v.findViewById(R.id.emptyElement));

        return v;
    }
}
