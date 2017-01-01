package com.lfeng.pipingfactory.util;


//import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lfeng.pipingfactory.bean.Data;

import java.util.ArrayList;

/**
 * Created by gpw on 2016/7/23.
 * --加油
 */
public class JsonUtil {
    private static final String TAG = "MainActivity";

    public static Data getDate(String data) {
        Data allData = null;
        try {

            JSONObject jsonObject = JSON.parseObject(data);
//            Log.d(TAG,jsonObject.toString());

            int rowNum = jsonObject.getInteger("childRowNum");
            String name = jsonObject.getString("fieldValue");
            String value = jsonObject.getString("fieldValue");
            boolean textHorizon = jsonObject.getBoolean("textHorizon");
            int width = jsonObject.getInteger("width");

            ArrayList<ArrayList<Data>> lists = getArrayLists(jsonObject, rowNum);
            allData = new Data(name,value,rowNum,textHorizon,width,lists);
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.d(TAG, "错误");

        }
        return allData;
    }

    private static  ArrayList<ArrayList<Data>> getArrayLists(JSONObject jsonObject, int rowNum) throws JSONException {

        ArrayList<ArrayList<Data>> lists = new ArrayList<ArrayList<Data>>();
//        Log.d(TAG,"rowNum====="+rowNum);
        for(int i =0;i<rowNum;i++){
            JSONArray array =jsonObject.getJSONArray("rowData"+(i+1));
            ArrayList<Data> datas = new ArrayList<Data>();
            for(int j = 0;j<array.size();j++){
                JSONObject object =array.getJSONObject(j);
//                Log.d(TAG,"第"+j+"层数据===="+object.toString());

                String  fieldName = object.getString("fieldName");
                String fieldValue = object.getString("fieldValue");
                int viewType = object.getInteger("viewType");
                int fieldType = object.getInteger("fieldType");
                int childRowNum = object.getInteger("childRowNum");
                boolean textHorizon = object.getBoolean("textHorizon");
                int width = object.getInteger("width");

                if(childRowNum>1){
//                    Log.d(TAG,"childRowNum=="+childRowNum);
                    ArrayList<ArrayList<Data>> arraylists = getArrayLists(object, childRowNum);

                    Data data = new Data(fieldName,fieldValue,viewType,fieldType,childRowNum,textHorizon,width,arraylists);
                    datas.add(data);
//                    Log.d(TAG,"内部的数据======="+data.getFieldName());
                }else {
                    Data data = new Data(fieldName,fieldValue,viewType,fieldType,childRowNum,textHorizon,width);
                    datas.add(data);
//                    Log.d(TAG, "第一次数据======" + data.getFieldValue());
                }
            }
            lists.add(datas);
//            Log.d(TAG, "第一次数据量：" + lists.size());
//            Log.d(TAG, "第一次数据量：(内部)" + lists.get(0).size());
        }
        return lists;
    }

}
