package com.cn.carigps.entity;

import java.util.List;

public class GoogleRotePlan {
	public List<Routes> routes;
	public String status; 
	public GoogleRotePlan(List<Routes> routes,String status){
		this.routes=routes;
		this.status=status;
	}
	public static class Routes{
		public Bounds bounds;
		public String copyrights;
		public List<Legs> legs;
		public Overview_polyline overview_polyline;
		public String summary;
		public List<NullClass> warnings;
		public List<NullClass> waypoint_order;
		public Routes(Bounds bounds,String copyrights,List<Legs> legs,Overview_polyline overview_polyline,String summary,List<NullClass> warnings,List<NullClass> waypoint_order){
			this.bounds=bounds;
			this.copyrights=copyrights;
			this.legs=legs;
			this.overview_polyline=overview_polyline;
			this.summary=summary;
			this.warnings=warnings;
			this.waypoint_order=waypoint_order;
		}
	}
	public static class Bounds{
		public Point northeast;
		public Point southwest;
		public Bounds(Point northeast,Point southwest){
			this.northeast=northeast;
			this.southwest=southwest;
		}
	}
	
	public static class Legs{
		public TextAndValue distance;
		public TextAndValue duration;
		public String end_address;
		public Point end_location;
		public String start_address;
		public Point start_location;
		public List<Steps> steps;
		public List<NullClass> via_waypoint;
		public Legs(TextAndValue distance,TextAndValue duration,String end_address,Point end_location,String start_address,Point start_location, List<Steps> steps,List<NullClass> via_waypoint){
			this.distance=distance;
			this.duration=duration;
			this.end_address=end_address;
			this.end_location=end_location;
			this.start_address=start_address;
			this.start_location=start_location;
			this.steps=steps;
			this.via_waypoint=via_waypoint;
			
		}
	}
	public static class Steps{
		public TextAndValue distance;
		public TextAndValue duration;
		public Point end_location;
		public String html_instructions;
		public Overview_polyline polyline;
		public Point start_location;
		public String travel_mode;
		public Steps(TextAndValue distance,TextAndValue duration,Point end_location,String html_instructions,Overview_polyline polyline,Point start_location,String travel_mode){
			this.distance=distance;
			this.duration=duration;
			this.end_location=end_location;
			this.html_instructions=html_instructions;
			this.polyline=polyline;
			this.start_location=start_location;
			this.travel_mode=travel_mode;
		}
	}
	public static class TextAndValue{
		public String text;
		public int value;
		public TextAndValue( String text,int value){
			this.text=text;
			this.value=value;
		}
	}
	public static class Overview_polyline
	{
		public String points;
		public Overview_polyline( String points){
			this.points=points;
		}
	}
	public static class NullClass{
		public NullClass(){
			
		}
	}
	public static class Point{
		public float lat;
		public float lng;
		public Point(float lat,float lng){
			this.lat=lat;
			this.lng=lng;
			
		}
	}
}
