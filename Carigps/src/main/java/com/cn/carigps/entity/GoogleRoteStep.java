package com.cn.carigps.entity;

import com.cn.carigps.entity.GoogleRotePlan.Overview_polyline;
import com.cn.carigps.entity.GoogleRotePlan.Point;
import com.cn.carigps.entity.GoogleRotePlan.TextAndValue;

public class GoogleRoteStep {
	public static class Steps{
		private TextAndValue distance;
		private TextAndValue duration;
		private Point end_location;
		private String html_instructions;
		private Overview_polyline polyline;
		private Point start_location;
		private String travel_mode;
		public TextAndValue getDistance() {
			return distance;
		}
		public void setDistance(TextAndValue distance) {
			this.distance = distance;
		}
		public TextAndValue getDuration() {
			return duration;
		}
		public void setDuration(TextAndValue duration) {
			this.duration = duration;
		}
		public Point getEnd_location() {
			return end_location;
		}
		public void setEnd_location(Point end_location) {
			this.end_location = end_location;
		}
		public String getHtml_instructions() {
			return html_instructions;
		}
		public void setHtml_instructions(String html_instructions) {
			this.html_instructions = html_instructions;
		}
		public Overview_polyline getPolyline() {
			return polyline;
		}
		public void setPolyline(Overview_polyline polyline) {
			this.polyline = polyline;
		}
		public Point getStart_location() {
			return start_location;
		}
		public void setStart_location(Point start_location) {
			this.start_location = start_location;
		}
		public String getTravel_mode() {
			return travel_mode;
		}
		public void setTravel_mode(String travel_mode) {
			this.travel_mode = travel_mode;
		}
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
}
