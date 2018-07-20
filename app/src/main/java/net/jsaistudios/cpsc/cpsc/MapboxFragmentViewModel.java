package net.jsaistudios.cpsc.cpsc;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ip on 7/1/18.
 */

public class MapboxFragmentViewModel extends Fragment implements PermissionsListener, LocationEngineListener {
    public View frag;
    public Context mainContext;
    private String mapboxTok;
    private MapView mapView;
    private MapboxMap myMapboxMap;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private NavigationMapRoute navigationMapRoute;
    public MapListener listener;
    private LatLng receivedLocBeforeMapLoaded=null;
    public int pxLeft = 10, pxUp = 10, pxRight = 10, pxDown=10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frag = inflater.inflate(R.layout.fragment_mapbox, container, false);
        mapboxTok = getString(R.string.mapbox_key);
        Mapbox.getInstance(mainContext, mapboxTok);
        mapView = (MapView) frag.findViewById(R.id.mapView);
        new ReinstantiateMapBasedOnSavedInstanceState().execute(savedInstanceState);

        return frag;
    }

    private class ReinstantiateMapBasedOnSavedInstanceState extends AsyncTask<Bundle, Void, String> {

        @Override
        protected String doInBackground(Bundle... params) {
            if(params.length>0) {
                mapView.onCreate(params[0]);
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    myMapboxMap = mapboxMap;
                    if(receivedLocBeforeMapLoaded!=null) {
                        updateCam(receivedLocBeforeMapLoaded, CPSCConstants.DEFAULT_ZOOM);
                    }
                    enableLocationPlugin();

                }
            });
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private void setCameraPosition(LatLng location) {
        myMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                location, 13));
    }

    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(mainContext)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, myMapboxMap, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    private void clearRoute() {
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }
    }
    private void updateCam(LatLng pos, int zoom) {
        if(myMapboxMap !=null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(pos) // Sets the new camera position
                    .zoom(zoom) // Sets the zoom to level 10
                    .tilt(0) // Set the camera tilt to 20
                    .build();
            myMapboxMap.setPadding(pxLeft, pxUp, pxRight, pxDown);
            myMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
            receivedLocBeforeMapLoaded = pos;
        }
    }
    private void updateCam(List<LatLng> points, boolean addMarkerPoints) {
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder()
                .includes(points);
        LatLngBounds latLngBounds = latLngBoundsBuilder.build();
        if(myMapboxMap.getCameraPosition().tilt!=0) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        myMapboxMap.setPadding(0,0,0,0);
        myMapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, pxLeft, pxUp, pxRight, pxDown));
        if(!addMarkerPoints)
            drawRoute(points);

    }


    private void drawRoute(final List<LatLng> markerPoints) {
        LatLng origin = markerPoints.get(0);
        LatLng destination = markerPoints.get(markerPoints.size()-1);

        NavigationRoute.Builder builder = NavigationRoute.builder(mainContext)
                .accessToken(Mapbox.getAccessToken())
                .origin(Point.fromLngLat(origin.getLongitude(), origin.getLatitude()))
                .destination(Point.fromLngLat(destination.getLongitude(), destination.getLatitude()));
        if(markerPoints.size()>2) {
            for(int i=1; i<markerPoints.size()-1; i++) {
                builder.addWaypoint(Point.fromLngLat(markerPoints.get(i).getLongitude(), markerPoints.get(i).getLatitude()));
            }
        }

        builder.build()
            .getRoute(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    if (response.body() == null) {
//                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                        return;
                    } else if (response.body().routes().size() < 1) {
//                            Log.e(TAG, "No routes found");
                        return;
                    }
                    DirectionsRoute currentRoute = response.body().routes().get(0);
                    if (navigationMapRoute != null) {
                        navigationMapRoute.removeRoute();
                    } else {
                        navigationMapRoute = new NavigationMapRoute(null, mapView, myMapboxMap, R.style.NavigationMapRoute);
                    }
                    navigationMapRoute.addRoute(currentRoute);
                    List<LatLng> points = new ArrayList<>();
                    List<Point> coords = LineString.fromPolyline(currentRoute.geometry(), Constants.PRECISION_6).coordinates();
                    for (Point position : coords) {
                        points.add(new LatLng(position.latitude(), position.longitude()));
                    }
                    updateCam(points, true);
                }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                        Log.e(TAG, "Error: " + throwable.getMessage());
                }
            });
    }


        @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(mainContext);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);

        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        onLocationChanged(lastLocation);
        locationEngine.addLocationEngineListener(this);
    }

    private void setCameraPosition(Location location) {
        myMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
//implement if location permission not given
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if(listener!=null) {
                listener.locationUpdated(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            setCameraPosition(location);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
