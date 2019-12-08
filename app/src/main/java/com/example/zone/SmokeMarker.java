package com.example.zone;

import net.daum.mf.map.api.MapPOIItem;

public class SmokeMarker {
    MapPOIItem _marker;
    private int _smokingAreaType;

    public SmokeMarker(MapPOIItem smokeMarker, int smokingAreaType) {
        _marker=smokeMarker;
        _smokingAreaType=smokingAreaType;
    }
    public int getSmokingAreaType (){
        return _smokingAreaType;
    }

    public MapPOIItem getSmokeMarker(){
        return _marker;
    }
}
