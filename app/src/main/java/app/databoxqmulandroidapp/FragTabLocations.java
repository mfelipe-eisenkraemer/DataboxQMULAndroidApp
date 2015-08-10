package app.databoxqmulandroidapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import app.databoxqmulandroidapp.model.GpsLocation;
import app.databoxqmulandroidapp.model.GpsLocationDataSource;
import app.databoxqmulandroidapp.model.MyItem;

/**
 * Created by MateusFelipe on 18/06/2015.
 */
public class FragTabLocations extends Fragment {

    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private GpsLocationDataSource gpsLocationDataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_tab_locations,container,false);
        gpsLocationDataSource = new GpsLocationDataSource(getActivity().getApplicationContext());
        setUpMapIfNeeded();
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLocations)).getMap();
        if (mMap != null) {
            startDemo();
        }
    }

    /**
     * Run the demo-specific code.
     */
    protected void startDemo(){
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        mClusterManager = new ClusterManager<MyItem>(getActivity().getApplicationContext(), getMap());
        getMap().setOnCameraChangeListener(mClusterManager);

        readItems();
    }

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }

    private void readItems() {
        gpsLocationDataSource.open();
        List<GpsLocation> gpsLocationList = gpsLocationDataSource.getAllPlaces();
        gpsLocationDataSource.close();

        List<MyItem> items = new ArrayList<MyItem>();
        for (GpsLocation gps : gpsLocationList){
            MyItem item = new MyItem( gps.getmLatitude(), gps.getmLongitude());
            items.add(item);
        }
        mClusterManager.addItems(items);
    }
}
