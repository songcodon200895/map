package com.example.thanhcong.map.CaculatorModules.DirectionModules;
import java.util.ArrayList;

import java.util.List;


/**

 * Created by Mai Thanh Hiep on 4/3/2016.

 */

public interface DirectionFinderListener {

    void onDirectionFinderStart(int request_code);

    void onDirectionFinderSuccess(List<Route> route,int request_code1,int request_code2);

}
