package com.cn.wetrack.util;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {  
    /** 
     * Receives a JSONObject and returns a list of lists containing latitude and 
     * longitude 
     */  
	List<Integer> lens=new ArrayList<Integer>();
	public DirectionsJSONParser(){
		
	}
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {  
  
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();  
        JSONArray jRoutes = null;  
        JSONArray jLegs = null;  
        JSONArray jSteps = null; 
        
  
        try {  
  
            jRoutes = jObject.getJSONArray("routes");  
  
            /** Traversing all routes */  
            for (int i = 0; i < (jRoutes).size(); i++) {  
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs"); 

                List path = new ArrayList<HashMap<String, String>>();  
  
                /** Traversing all legs */  
                for (int j = 0; j < ( jLegs).size(); j++) {  
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");  
                  JSONObject distance=((JSONObject) jLegs.get(j)).getJSONObject("distance");
                  lens.add(Integer.parseInt(distance.getString("value")));
                    /** Traversing all steps */  
                    for (int k = 0; k < (jSteps).size(); k++) {  
                        String polyline = "";  
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps  
                                .get(k)).get("polyline")).get("points");  
                        List<LatLng> list = decodePoly(polyline);  
  
                        /** Traversing all points */  
                        for (int l = 0; l < list.size(); l++) {  
                            HashMap<String, String> hm = new HashMap<String, String>();  
                            hm.put("lat",  
                                    Double.toString(((LatLng) list.get(l)).latitude));  
                            hm.put("lng",  
                                    Double.toString(((LatLng) list.get(l)).longitude));  
                            path.add(hm);  
                        }  
                    }  
                    routes.add(path);  
                }  
            }  
        } catch (JSONException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
        }  
        return routes;  
    }  
  
    /** 
     * Method to decode polyline points Courtesy : 
     * jeffreysambells.com/2010/05/27 
     * /decoding-polylines-from-google-maps-direction-api-with-java 
     * */  
    public List<LatLng> decodePoly(String encoded) {  
  
        List<LatLng> poly = new ArrayList<LatLng>();  
        int index = 0, len = encoded.length();  
        int lat = 0, lng = 0;  
  
        while (index < len) {  
            int b, shift = 0, result = 0;  
            do {  
                b = encoded.charAt(index++) - 63;  
                result |= (b & 0x1f) << shift;  
                shift += 5;  
            } while (b >= 0x20);  
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));  
            lat += dlat;  
  
            shift = 0;  
            result = 0;  
            do {  
                b = encoded.charAt(index++) - 63;  
                result |= (b & 0x1f) << shift;  
                shift += 5;  
            } while (b >= 0x20);  
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));  
            lng += dlng;  
  
            LatLng p = new LatLng((((double) lat / 1E5)),  
                    (((double) lng / 1E5)));  
            poly.add(p);  
        }  
        return poly;  
    }  
    /** 
	 * 通过起点终点，组合成url 
	 *  
	 * @param origin 
	 * @param dest 
	 * @return 
	 */  
	public String getDirectionsUrl(LatLng origin, LatLng dest,int method) {  
	  
	    // Origin of route  
	    String str_origin = "origin=" + origin.latitude + ","  
	            + origin.longitude;  
	  
	    // Destination of route  
	    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;  
	  
	    // Sensor enabled  
	    String sensor = "sensor=false";  
	  
	    // Travelling Mode  
	    String mode="";
	    switch (method) {
		case 0:
			  mode = "mode=driving"; 
			break;
		case 1:
			  mode = "mode=walking"; 
			break;

		default:
			break;
		}
	    
	      
	    //waypoints,116.32885,40.036675  
//	    String waypointLatLng = "waypoints="+"40.036675"+","+"116.32885";  
	  
	    // Building the parameters to the web service  
	    String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"  
	            + mode;  
	  
	    // Output format  
	    String output = "json";  
	  
	    // Building the url to the web service  
	    String url = "https://maps.googleapis.com/maps/api/directions/"  
	            + output + "?" + parameters;  
//	    System.out.println("getDerectionsURL--->: " + url); 
//	    url="https://maps.googleapis.com/maps/api/directions/json?origin=39.99709957757345,116.31184045225382&destination=39.999158391497214,116.3154639095068&sensor=false&mode=driving";
	    return url;  
	    
	}  
	/** A method to download json data from url */  
	public String downloadUrl(String strUrl) throws IOException {  
	    String data = "";  
	    InputStream iStream = null;  
	    HttpURLConnection urlConnection = null;  
	    try {  
	        URL url = new URL(strUrl);  
	  
	        // Creating an http connection to communicate with url  
	        urlConnection = (HttpURLConnection) url.openConnection();  
	  
	        // Connecting to url  
	        urlConnection.connect();  
	  
	        // Reading data from url  
	        iStream = urlConnection.getInputStream();  
	  
	        BufferedReader br = new BufferedReader(new InputStreamReader(  
	                iStream));  
	  
	        StringBuffer sb = new StringBuffer();  
	  
	        String line = "";  
	        while ((line = br.readLine()) != null) {  
	            sb.append(line);  
	        }  
	  
	        data = sb.toString();  
	  
	        br.close();  
	  
	    } catch (Exception e) {  
	        Log.d("Exception while downloading url", e.toString());  
	    } finally {  
	    	if(iStream!=null)
	        iStream.close();  
	        urlConnection.disconnect();  
	    }  
//	    System.out.println("url:" + strUrl + "---->   downloadurl:" + data);  
	    return data;  
	}  
	/*获取路径长度集合*/
	public List<Integer> Getlengths(){
		return lens;
	}
}  
