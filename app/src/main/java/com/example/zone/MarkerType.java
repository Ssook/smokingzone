package com.example.zone;

import net.daum.mf.map.api.MapPOIItem;

public abstract class MarkerType {
    abstract void setMarkerRes();
    MapPOIItem smokeMarker;
}

class CafeMarker extends  MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.cafe);
    }
}

class FoodMarker extends MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.food);
    }
}
class SchoolMarker extends MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.school);
    }
}
class CompanyMarker extends MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.company);
    }
}
class StreetMarker extends MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.street);
    }
}
class OtherMarker extends MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.other);
    }
}
class BannedMarker extends MarkerType{
    @Override
    void setMarkerRes() {
        smokeMarker.setCustomImageResourceId(R.drawable.map_pin_black);
    }
}