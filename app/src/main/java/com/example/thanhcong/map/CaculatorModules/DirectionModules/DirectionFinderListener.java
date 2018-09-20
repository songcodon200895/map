package com.example.thanhcong.map.CaculatorModules.DirectionModules;
import java.util.ArrayList;

import java.util.List;



public interface DirectionFinderListener {

    void onDirectionFinderStart(int request_code,int request_code3);

    void onDirectionFinderSuccess(List<Route> route,int request_code1,int request_code2);

    void onDistanceDurationSuccess(List<Route>routes,int code);

}
