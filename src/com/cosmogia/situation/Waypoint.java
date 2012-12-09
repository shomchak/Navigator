package com.cosmogia.situation;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;


public class Waypoint {
	
	public final double lat,lon,alt,time;
	public Waypoint previous, next;
	
	public Waypoint(double lat, double lon, double alt, double time, Waypoint previous, Waypoint next) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.time = time;
		this.previous = previous;
		this.next = next;
	}
	
	public static ArrayList<Waypoint> getWaypoints(Context context, String filename) {
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(filename);
			DataInputStream dataIO = new DataInputStream(is);
			String strLine = null;
			if((strLine = dataIO.readLine()) == null) {
				System.out.println("file is empty, idiot!");
			}
			
		    Waypoint head = null;
		    Waypoint last = null;
		    while((strLine = dataIO.readLine()) != null) {
		    	System.out.println("about to read the string");
		        Waypoint curr = waypoint_from_string(strLine, last);
		        if (head == null) {
		            head = curr;
		            last = curr;
		            continue;
		        }
		        last.next = curr;
		        last = curr;				
			}
		    
			dataIO.close();
			is.close();

		    ArrayList<Waypoint> Waypoints = new ArrayList<Waypoint>();
			Waypoint current = head;
			while(current != null) {
				//arraylist.append(head.next)
				System.out.println("adding current");
				Waypoints.add(current);
				
				current = current.next;
			}
			if (head == null) {
				Waypoints = null;
				return Waypoints;
			}
			return Waypoints;
		}
		catch (Exception e) {
			e.printStackTrace();
			ArrayList<Waypoint> Waypoints = new ArrayList<Waypoint>();
			Waypoints = null;
			return Waypoints;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(lat+","+lon+","+alt+","+time+"\n");
		return result.toString();
	}
	
	public static Waypoint getNearestWaypoint(ArrayList<Waypoint> course, double time) {
		System.out.println("getting nearest waypoint");
		System.out.println("got current time");
		System.out.println(course.size());
		for(int i = 0; i < course.size(); i++) {
			System.out.println("nearest, total" + i + "," + course.size());
			if(course.get(i).time >= time) {
				System.out.println("nearest: " + i + course.get(i).time);
				return course.get(i);
			}
		}
		return course.get(course.size()-1);
	}
	
	private static Waypoint waypoint_from_string(String line, Waypoint gprev)
	{
	    System.out.println(line);
		String[] gdata = line.split(",");
	    double glat,glon,galt,gtime;
	    glat = Double.valueOf(gdata[0]);
	    glon = Double.valueOf(gdata[1]);
	    galt = Double.valueOf(gdata[2]);
	    gtime = Double.valueOf(gdata[3]);
	    return new Waypoint(glat,glon,galt,gtime,gprev, null);
	}

}

