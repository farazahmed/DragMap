package com.dragmap;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.victor.loading.rotate.RotateLoading;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;


public class MapFragment extends Fragment implements  GoogleMap.OnMarkerDragListener{
    View view;
    private EditText txt_address;
    private GoogleMap mMap;
    private Address address;
    MainActivity mainActivity;
    RotateLoading rotateLoading;
    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_map, container, false);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {

        }
    }

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setUpView();
        setUpMapIfNeeded();
        getMap().setOnMarkerDragListener(this);
        new GetUserCurrentLocation().execute();

    }

    private void setUpView() {
        txt_address = (EditText) view.findViewById(R.id.txt_address);
        rotateLoading = (RotateLoading)view.findViewById(R.id.rotateloading);
        txt_address.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txt_address.getText().toString().trim().equalsIgnoreCase("")) {
                        Address address = getLocationFromAddress(txt_address.getText().toString().trim());
                        handleAddress(address);

                    } else {
                        txt_address.setError("please enter address");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void handleAddress(Address address) {
        if (address != null) {
            mMap.clear();
            this.address = address;
            loadAddressOnLocation(address);
        } else {
            Toast.makeText(getActivity(), "Location not found, please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAddressOnLocation(Address address) {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 10));
        MarkerOptions marker = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Location");
        marker.draggable(true);
        getMap().addMarker(marker);
    }


    public Address getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 1); // returns 1 location can be changed
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            //location.getLatitude();
            //location.getLongitude();
            return location;
            //return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            this.address =  addresses.get(0);
            // String postalCode = addresses.get(0).getPostalCode();
            // String knownName = addresses.get(0).getFeatureName();

            StringBuilder stringBuilder = new StringBuilder();
            if (address != null) {
                stringBuilder.append(address + ", ");
            }
            if (city != null) {
                stringBuilder.append(city + ", ");
            }
            if (state != null) {
                stringBuilder.append(state + " ");
            }
            if (country != null) {
                stringBuilder.append(country);
            }
            txt_address.setText(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Location not found, please try again", Toast.LENGTH_LONG).show();
        }
    }


    private void loadMyLocation() {
        if(mainActivity.getLatLng() != null){
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mainActivity.getLatLng(), 10));
            MarkerOptions marker = new MarkerOptions().position(mainActivity.getLatLng()).title("You current location");
            marker.draggable(true);
            getMap().addMarker(marker);
        }

    }

    private class GetUserCurrentLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rotateLoading.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadMyLocation();
            rotateLoading.stop();

        }
    }


}
