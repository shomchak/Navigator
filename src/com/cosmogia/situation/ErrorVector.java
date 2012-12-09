package com.cosmogia.situation;

public class ErrorVector {
	
	public final double magnitude, bearing, XTE, ATE, magVert, magHorz;
	private static int velocityForward = 10;
	
	public static void setVelocityForward(int v) {
		velocityForward = v;
	}
	
	public ErrorVector(double magnitude, double bearing, double XTE, double ATE, double altE, double magHorz) {
		super();
		this.magnitude = magnitude;
		this.bearing = bearing;
		this.XTE = XTE;
		this.ATE = ATE;
		this.magVert = altE;
		this.magHorz = magHorz;
	}
	
	public static ErrorVector errorVector(Waypoint actual, Waypoint desired) {
		// meters
		System.out.println("theta top" + desired.time);
		double lat1 = Math.toRadians(actual.lat);
		double lon1 = Math.toRadians(actual.lon);
		double lat2 = Math.toRadians(desired.lat);
		double lon2 = Math.toRadians(desired.lon);
		
		System.out.println("errorVector: lat, lon: " + lat1 +"," + lon1 + "," + lat2 + "," + lon2);
		double R = 6371000;
		double dLat = lat2-lat1;
		double dLon = lon2-lon1;
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		double magHorz = R * c;
		double magVert = desired.alt - actual.alt; // meters, positive magVert means you are too low
		double magnitude = Math.hypot(magHorz, magVert);
		
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
		
		double bearingDegrees = Math.toDegrees(Math.atan2(y,x)); // degrees
		double bearing = (bearingDegrees + 360) % 360; // shifted
		
		double XTE,ATE;
		System.out.println("theta bottom" + desired.time);
		if(desired.previous != null) {			
			double theta = courseBearing(desired, actual);
			double phi = courseBearing(desired, desired.previous);
			double beta = theta - phi;
			System.out.println("the lat and lon 1:"+ actual);
			System.out.println("the lat and lon 2:"+ desired);
			
			// Positive XTE means you are to the "left" of the course
			XTE = magHorz * Math.sin(Math.toRadians(beta));
			ATE = magHorz * Math.cos(Math.toRadians(beta));
			System.out.println("theta,phi,beta: " +theta+','+phi+','+beta);
			System.out.println("xte: " + XTE);
		}
		else {
			System.out.println("theta, got here");
			double theta = courseBearing(desired.next, actual);
			double phi = courseBearing(desired.next, desired);
			double beta = theta - phi;
			
			// Positive XTE means you are to the "left" of the course
			XTE = magHorz * Math.sin(Math.toRadians(beta));
			ATE = magHorz * Math.cos(Math.toRadians(beta));
		}
		
		ErrorVector result = new ErrorVector(magnitude,bearing,XTE,ATE,magVert,magHorz);
		
		System.out.println("errorVector: XTE= " + XTE + ", ATE = " + ATE);
		return result;
	}
	
	public static double[] velocityRequired(Waypoint actual, Waypoint desired) {
		double i = 0;
		Waypoint last = desired;
		while(last.next != null && i <= velocityForward) {
			i += 1;
			last = last.next;
		}
		ErrorVector error = errorVector(actual,last);
		double time = last.time - actual.time;
		System.out.println("time delta: " + time);
		double speed = error.magHorz/i;//time; // meters/second
		double bearing = error.bearing; // degrees
		double[] result = {speed, bearing};
		return result;
	}
	
	public static double courseBearing(Waypoint dest) {
		if(dest.previous != null) {
			Waypoint source = dest.previous;
			double dLon = Math.toRadians(dest.lon - source.lon);
			double lat1 = Math.toRadians(source.lat);
			double lat2 = Math.toRadians(dest.lat);
			
			double y = Math.sin(dLon) * Math.cos(lat2);
			double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
			
			double bearingDegrees = Math.toDegrees(Math.atan2(y,x)); // degrees
			double bearing = (bearingDegrees + 360) % 360; // shifted
			
			return bearing;
		}
		else {
			return courseBearing(dest, dest.next);
		}
	}
	
	public static double courseBearing(Waypoint source, Waypoint dest) {
		double dLon = Math.toRadians(dest.lon - source.lon);
		double lat1 = Math.toRadians(source.lat);
		double lat2 = Math.toRadians(dest.lat);
			
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
		
		double bearingDegrees = Math.toDegrees(Math.atan2(y,x)); // degrees
		double bearing = (bearingDegrees + 360) % 360; // shifted
		System.out.println("theta course bearing test: " + bearing);
			
		return bearing;
	}
	
	public static double magHorz(Waypoint source, Waypoint dest) {
		// meters
		double lat1 = Math.toRadians(source.lat);
		double lon1 = Math.toRadians(source.lon);
		double lat2 = Math.toRadians(dest.lat);
		double lon2 = Math.toRadians(dest.lon);
		
		System.out.println("errorVector: lat, lon: " + lat1 +"," + lon1 + "," + lat2 + "," + lon2);
		double R = 6371;
		double dLat = lat2-lat1;
		double dLon = lon2-lon1;
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		double magHorz = R * c;
		
		return magHorz;
	}
}