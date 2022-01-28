package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.googlemaps.Clases.DatosModels;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlemaps.databinding.ActivityMapsBinding;
import com.google.gson.JsonArray;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    RequestQueue requestQueue;
    List<DatosModels> Informacion = new ArrayList<>();
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        if (googleMap != null) {
            //cuando el mapa esté completamente cargado
            mMap = googleMap;
            // habilitar controles de zoom
            LatLng uteq = new LatLng(-1.0118506915092784, -79.46892724061738);
            CameraUpdate Cupdate = CameraUpdateFactory.newLatLngZoom(uteq, 17);
            mMap.moveCamera(Cupdate);
            //esta clase tiene el método para el clic del mapa, por ello se le asigna this

            mMap.setInfoWindowAdapter(MapsActivity.this);

            //genericPoinst();
            CasoVolley();
        } else {
            Toast.makeText(MapsActivity.this, "Esta nulo el Mapa", Toast.LENGTH_SHORT).show();
        }
    }

    private void CasoVolley() {
        Log.e("main", "Volley");
        //mostrar mensaje en la aplicacion movil
        Toast.makeText(MapsActivity.this, "Volley", Toast.LENGTH_SHORT).show();
        String url = "https://my-json-server.typicode.com/VivianZamora/ApiUniversidad/db";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(MapsActivity.this, "try", Toast.LENGTH_SHORT).show();
                    JSONArray Datos;
                    Datos = response.getJSONArray("Datos");
                    Toast.makeText(MapsActivity.this, "afor", Toast.LENGTH_SHORT).show();
                    JSONObject element;
                    for (int i = 0; i < Datos.length(); i++) {
                        element = Datos.getJSONObject(i);
                        DatosModels D = new DatosModels(element.getString("Name"), element.getString("direction"), element.getString("authority"), element.getString("contact"), element.getString("logo"), element.getString("url"), element.getDouble("lat"), element.getDouble("lng"));
                        Informacion.add(D);
                    }
                    setPoints(Informacion);

                } catch (Exception e) {
                    Log.e("error: ", e.getMessage());
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("mi_Retrofit", "onFailure: " + error.getMessage());
                Toast toast = Toast.makeText(MapsActivity.this, "Error en Volley", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        requestQueue.add(jsonRequest);
    }


    public void setPoints(List<DatosModels> points) {
        //recorrer la lista
        LatLng uteq = null;
        for (int ind = 0; ind < points.size(); ind++) {
            //obtener un item de la lista
            DatosModels tmp = points.get(ind);

            Marker map = mMap.addMarker(
                    new MarkerOptions().position(new LatLng(tmp.getLat(), tmp.getLng()))
                            .title(tmp.getName())
                            .snippet(tmp.getDirection())

            );
            map.setTag(tmp);

            //cierra el modal una vez haya cargado la api

            Bitmap bm = null;

            //rescalar icono
            bm = Bitmap.createScaledBitmap(bm, 100, 120, false);
            //asignar icono
            map.setIcon(BitmapDescriptorFactory.fromBitmap(bm));
        }
        if (uteq != null) {
            CameraPosition camPos = new CameraPosition.Builder()
                    .target(uteq)
                    .zoom(13)
                    .bearing(45)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(camUpd3);
        }
    }


    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoView = LayoutInflater.from(context).inflate(R.layout.activity_items, null);

        TextView fname = (TextView) infoView.findViewById(R.id.facultyName);
        TextView fdecano = (TextView) infoView.findViewById(R.id.facultyDecano);
        TextView femail = (TextView) infoView.findViewById(R.id.facultyemail);
        TextView flocat = (TextView) infoView.findViewById(R.id.facultyLocation);
        Button fbtn = (Button) infoView.findViewById(R.id.facultyvisite);
        ImageView flogo = (ImageView) infoView.findViewById(R.id.facultylogo);

        //btengo el objeto localización
        DatosModels tmp = (DatosModels) marker.getTag();
        fname.setText(tmp.getName());
        fdecano.setText(tmp.getAuthority());
        femail.setText(tmp.getContact());
        flocat.setText(tmp.getDirection());

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Hola", Toast.LENGTH_SHORT).show();

            }
        });

        //tmp.getUrl()
        Toast.makeText(MapsActivity.this, "Logo", Toast.LENGTH_SHORT).show();


        Picasso.get().load(tmp.getLogo()).resize(200, 200).into(flogo);



        return infoView;
    }


    public class MarkerCallback implements Callback {
        private Marker markerToRefresh;

        public MarkerCallback(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }

        @Override
        public void onError(Exception e) {
            Toast.makeText(MapsActivity.this, "errorPicasso:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}