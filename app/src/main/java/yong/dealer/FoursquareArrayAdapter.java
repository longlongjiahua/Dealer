package yong.dealer;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FoursquareArrayAdapter extends ArrayAdapter<FoursquareVenue> {
	private final Context context;
	private LatLng userPosition;
	private ArrayList<FoursquareVenue> foursquareVenues;

	public FoursquareArrayAdapter(Context context, ArrayList<FoursquareVenue> foursquareVenues) {
		super(context, R.layout.one_row_foursquare, foursquareVenues);
		this.context = context;
		this.foursquareVenues = foursquareVenues;
        //Log.i("Name0::", foursquareVenues.get(0).getName());
        super.sort(new Util().new PositionComparator());
	}

	@Override
		public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        if (convertView == null) {

            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.one_row_foursquare, parent, false);
        } else {
            rowView = convertView;
        }
        final FoursquareVenue im = (FoursquareVenue) super.getItem(position);
        TextView tvName = (TextView) rowView.findViewById(R.id.tv_foursquare_name);
        TextView tvAddr = (TextView) rowView.findViewById(R.id.tv_foursquare_address);
        TextView tvDistance = (TextView) rowView.findViewById(R.id.tv_foursquare_distance);
        Log.i("Name::", im.getName());
        tvName.setText(im.getName());
        tvDistance.setText(""+im.getDistance()+"m");
        String address;
        if(im.getAddress()==null || im.getAddress().isEmpty()){
                address = "Address not available";
        }
        else {
                address = im.getAddress();
        }
        tvAddr.setText(address);


        return rowView;
    }


}
