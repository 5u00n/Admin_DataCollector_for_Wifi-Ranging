package com.projectopel.admindatacollector.UI.ViewData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.projectopel.admindatacollector.R;

import java.util.ArrayList;

public class GatheredDataAdapter extends ArrayAdapter<GatheredDataModel> {

    public GatheredDataAdapter(@NonNull Context context, ArrayList<GatheredDataModel> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        GatheredDataModel gatheredDataModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_view, parent, false);
        }
        // Lookup view for data population
        TextView bs, ss, lat, lon;
        bs = convertView.findViewById(R.id.bssid_text);
        ss = convertView.findViewById(R.id.ssid_text);
        lat = convertView.findViewById(R.id.lat_text);
        lon = convertView.findViewById(R.id.long_text);
        // Populate the data into the template view using the data object
        bs.setText(gatheredDataModel.BSSID);
        ss.setText(gatheredDataModel.SSID);
        lat.setText(gatheredDataModel.LAT);
        lon.setText(gatheredDataModel.LON);


        return convertView;
    }
}
