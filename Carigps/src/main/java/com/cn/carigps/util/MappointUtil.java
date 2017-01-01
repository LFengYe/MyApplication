package com.cn.carigps.util;

import com.baidu.mapapi.utils.CoordinateConverter;

public class MappointUtil {
    CoordinateConverter coordinateconverter;

    public MappointUtil() {
        coordinateconverter = new CoordinateConverter();
    }

    /*
    public LatLng ConverterGPStoBaidu(LatLng srcCoord) {
        coordinateconverter.coord(srcCoord);
        coordinateconverter.from(CoordinateConverter.CoordType.GPS);

        return coordinateconverter.convert();
    }
    */

}
