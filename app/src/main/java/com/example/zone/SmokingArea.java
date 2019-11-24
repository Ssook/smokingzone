package com.example.zone;

import org.json.JSONException;
import org.json.JSONObject;

public class SmokingArea {
    private String _AreaName;
    private String _AreaDesc;
    private String _AreaRegDate;
    private String _AreaRegUser;
    private Double _Point;
    private Double _AreaLat;
    private Double _AreaLng;
    private int _ChkAircondition;
    private int _ChkRoof;
    private int _ChkBench;
    private int _ChkInside;
    private int _Report;
    private int _AreaType;


    public SmokingArea(JSONObject SmokingAreaInfo){
        try {
            _AreaName=SmokingAreaInfo.get("name").toString();
            _AreaDesc=SmokingAreaInfo.get("desc").toString();
            _AreaRegDate=SmokingAreaInfo.get("reg_date").toString();
            _AreaRegUser=SmokingAreaInfo.get("reg_user").toString();
            _AreaLat=SmokingAreaInfo.getDouble("lat");
            _AreaLng=SmokingAreaInfo.getDouble("lng");
            _Point=SmokingAreaInfo.getDouble("point");
            _ChkAircondition=(int)SmokingAreaInfo.get("vtl");
            _ChkRoof=(int)SmokingAreaInfo.get("roof");
            _ChkBench=(int)SmokingAreaInfo.get("bench");
//            _ChkInside=(int)SmokingAreaInfo.get("inside");
            _AreaType=(int)SmokingAreaInfo.get("type");
            _Report=(int)SmokingAreaInfo.get("report");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSmokingAreaName(){
        return _AreaName;
    }

    public String getSmokingAreaDesc(){
        return _AreaDesc;
    }

    public Double getSmokingAreaPoint(){
        return _Point;
    }
}
