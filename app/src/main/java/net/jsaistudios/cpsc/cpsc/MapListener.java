package net.jsaistudios.cpsc.cpsc;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by ip on 7/20/18.
 */

public interface MapListener {
    void locationUpdated(LatLng loc);
}
