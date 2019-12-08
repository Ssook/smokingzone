package com.example.zone;

import org.json.JSONException;
import org.json.JSONObject;

public class SmokingArea {
    private int _AreaNo;
    private String _AreaName;
    private String _AreaDesc;
    private String _AreaRegDate;
    private String _AreaRegUser;
    private Double _Point;
    private Double _AreaLat;
    private Double _AreaLng;
    private String _ChkAircondition;
    private String _ChkRoof;
    private String _ChkBench;
    private String _ChkInside;
    private int _Report;
    private int _AreaType;
    private String _ImgUrl;


    public SmokingArea(JSONObject SmokingAreaInfo) {
        try {
            _AreaNo= Integer.parseInt(SmokingAreaInfo.get("no").toString());
            _AreaName = SmokingAreaInfo.get("name").toString();
            _AreaDesc = SmokingAreaInfo.get("desc").toString();
            _AreaRegDate = SmokingAreaInfo.get("reg_date").toString();
            _AreaRegUser = SmokingAreaInfo.get("reg_user").toString();
            _AreaLat = SmokingAreaInfo.getDouble("lat");
            _AreaLng = SmokingAreaInfo.getDouble("lng");
            _Point = SmokingAreaInfo.getDouble("point");
            _ImgUrl = SmokingAreaInfo.getString("img_url");
            _ChkAircondition=SmokingAreaInfo.getString("vtl");
            _ChkRoof=SmokingAreaInfo.getString("roof");
            _ChkBench = SmokingAreaInfo.getString("bench");
//            _ChkInside=SmokingAreaInfo.get("inside");//서버쪽에 추가필요
            _AreaType = Integer.parseInt(SmokingAreaInfo.getString("type"));
            _Report = Integer.parseInt(SmokingAreaInfo.getString("report"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSmokingAreaName() {
        return _AreaName;
    }

    public String getSmokingAreaDesc() {
        return _AreaDesc;
    }

    public Double getSmokingAreaPoint() {
        return _Point;
    }

    public Double getSmokingAreaLat() {
        return _AreaLat;
    }

    public Double getSmokingAreaLng() { return _AreaLng; }

    public int getSmokingAreaType() {
        return _AreaType;
    }

    public String getSmokingAreaBench() {
        return _ChkBench;
    }

    public String getSmokingAreaRoof() {
        return _ChkRoof;
    }

    public String getSmokingAreaAircondition() {
        return _ChkAircondition;
    }

    public String getSmokingAreaRegUser() {
        return _AreaRegUser;
    }

    public String getSmokingAreaImgUrl() {
        return _ImgUrl;
    }

    public int getSmokinAreaNo(){
        return _AreaNo;
    }
}
