package com.cosmogia.situation;

import com.cosmogia.situation.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.util.AttributeSet;
import android.content.res.Resources;
import java.lang.Math;

public class CompassView extends View {

	  private Paint markerPaint;
	  private Paint textPaint;
	  private Paint textPaintAngle;
	  private Paint circlePaint;
	  private Paint lubberPaint, deviationPaint1, deviationPaint2, velocityPaint, dVelocityPaint, velocityBoxPaint, glideslopePaint;
	  private String northString;
	  private String eastString;
	  private String southString;
	  private String westString;
	  private int textHeight;
	  private final double densityMultiplier = getContext().getResources().getDisplayMetrics().density;
	  private int ttextHeight;
	  private int currentWaypoint = 0;
	  
	  private static final int RED = -65536;
	  private static final int WHITE = -1;
	  private static final int GREEN = -16711936;
	  @SuppressWarnings("unused")
	  private static final int BLUE = -16776961;
	  private static final int BLACK = -16777216;
	  private static final int YELLOW = -256;
	  @SuppressWarnings("unused")
	  private static final double VMAX = 200;  
	  private static final double DVMAX = 15; // miles per hour
	  private static double DEVMAX = 1000;
	  private static final double GLIDEMAX = 30;
	  
	  private double glide = 0;// meters
	  private double trueGlide = 0;
	  private double dev = 0;
	  private double trueDev = 0;// positive is to the left
	  private double nextDistance = 0;
	  private double courseAngle = .3; // in radians
	  private double velocityExcess = 100;
	  private double trueSpeed = 0;
	  private double velocityAngle =0; // degrees
	  private double desiredVelocity = 110;
	  private double dVelocityAngle = 0; // degrees
	  private double bearing = Math.toRadians(0); // degrees
	  private double time = 0;
	  private double ATE = 0;
	  
	  public void setATE(double ate) {
		  ATE = ate;
	  }
	  
	  public void setCurrent(int i) {
		  currentWaypoint = i;
	  }

	  public void setBearing(double _bearing) {
	    bearing = Math.toRadians(_bearing);
	  }
	  
	  public void setDistance(double distance) {
		  nextDistance = distance;
	  }
	  
	  public void setGlide(double _glide) {
		  trueGlide = _glide;
		  glide = _glide;
		  if(Math.abs(glide) > GLIDEMAX) {
			  glide = Math.signum(trueGlide)*GLIDEMAX;
		  }
	  }
	  
	  public void setVelocityExcess(double _vel) {
		  trueSpeed = _vel;
		  velocityExcess = _vel;
	  }
	  
	  public void setCourseDeviation(double _dev) {
		  dev = -1*_dev;
		  trueDev = -1*_dev;
		  if(Math.abs(dev) > DEVMAX) {
			  dev = Math.signum(trueDev)*DEVMAX;
		  }
	  }
	  
	  private double scaleTime(double time) {
		  double t0 = -120;
		  double t1 = 0;
		  double d0 = 1000;
		  double d1 = 100;
		  if (time < t0)
			  return d0;
		  if (time > t1)
			  return d1;
		  return (d0-d1)*(t1-time)/(t1-t0) + d1;
	  }
	  
	  public void setCourseBearing(double angle) {
		  courseAngle = Math.toRadians(angle);
	  }
	  
	  public void setVelocityExcessAngle(double angle) {
		  velocityAngle = angle;
	  }
	  
	  public void setDesiredVelocity(double vel) {
		  desiredVelocity = vel;
	  }
	  
	  public void setDVelocityAngle(double angle) {
		  dVelocityAngle = angle;
	  }
	  
	  public void setTime(double _time) {
		  time = _time;
	  }
		
	  public CompassView(Context context) {
	    super(context);
	    initCompassView();
	  }
	
	  public CompassView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initCompassView();
	  }
	
	  public CompassView(Context context, 
	                     AttributeSet ats, 
	                     int defaultStyle) {
	    super(context, ats, defaultStyle);
	    initCompassView();
	  }

  protected void initCompassView() {
    setFocusable(true);

    Resources r = this.getResources();

    circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    circlePaint.setColor(r.getColor(R.color.background_color));
    circlePaint.setStrokeWidth(10);
    circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

    northString = r.getString(R.string.cardinal_north);
    eastString = r.getString(R.string.cardinal_east);
    southString = r.getString(R.string.cardinal_south);
    westString = r.getString(R.string.cardinal_west);

    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setColor(r.getColor(R.color.text_color));
    textPaint.setTextAlign(Paint.Align.CENTER);
    textPaintAngle = new Paint();
    
    ttextHeight = (int)(textPaint.measureText("www")*densityMultiplier);
    System.out.println("ttextHeight: " + ttextHeight);
    
    lubberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    lubberPaint.setColor(BLACK);
    lubberPaint.setStrokeWidth(3);
    
    deviationPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    deviationPaint1.setColor(WHITE);
    deviationPaint1.setStrokeWidth(10);
    
    deviationPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    deviationPaint2.setColor(GREEN);
    deviationPaint2.setStrokeWidth(7);
    
    velocityPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    velocityPaint.setColor(RED);
    velocityPaint.setStrokeWidth(5);
    
    dVelocityPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    dVelocityPaint.setColor(GREEN);
    dVelocityPaint.setStrokeWidth(10);
    
    
    velocityBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    velocityBoxPaint.setColor(WHITE);
    velocityBoxPaint.setStrokeWidth(2);
    velocityBoxPaint.setStyle(Paint.Style.STROKE);
    
    glideslopePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    glideslopePaint.setColor(YELLOW);
    glideslopePaint.setStrokeWidth(4);
    glideslopePaint.setStyle(Paint.Style.FILL);

    textHeight = (int)textPaint.measureText("yY");

    markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    markerPaint.setColor(r.getColor(R.color.marker_color));
  }
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
    // The compass is a circle that fills as much space as possible.
    // Set the measured dimensions by figuring out the shortest boundary,
    // height or width.
    int measuredWidth = measure(widthMeasureSpec);
    int measuredHeight = measure(heightMeasureSpec);
    

    int d = Math.min(measuredWidth, measuredHeight);

    setMeasuredDimension(d, measuredHeight);

  }

  private int measure(int measureSpec) {
    int result = 0; 

    // Decode the measurement specifications.
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec); 

    if (specMode == MeasureSpec.UNSPECIFIED) {
      // Return a default size of 200 if no bounds are specified. 
      result = 200;
    } else {
      // As you want to fill the available space
      // always return the full available bounds.
      result = specSize;
    } 
    return result;
  }
  
  @SuppressLint("DrawAllocation")
@Override 
  protected void onDraw(Canvas canvas) {
	  DEVMAX = scaleTime(time);
	  if(Math.abs(dev) > DEVMAX) {
		  dev = Math.signum(trueDev)*DEVMAX;
		  System.out.println("dev Max: " + DEVMAX);
	  }
	  System.out.println("first dev: " + dev);
	  setKeepScreenOn(true);

    int mMeasuredWidth = getMeasuredWidth();
    int mMeasuredHeight = getMeasuredHeight();
    

    
    //textPaint.setTextSize((int)(mMeasuredWidth/80.0));



//    circle
    int px = getMeasuredWidth() / 2;
    int py = getMeasuredWidth() /2 ;
    int radius = (int) (py*0.7);
    py = (int)(radius*1/0.8);
    
    // glideslope ticks
    int tickLength = (int) ((mMeasuredWidth - 2*radius)*0.8/2);
    int tickSpacing = (int) (2*radius/5.0);
    int tickStart = py-radius+(int)(tickSpacing/2.0);
    
    // glideslope
    int glideHeight = py - (int)(glide*radius/GLIDEMAX);
    int glideThick = 5;
    int glideLength = (int)(tickLength*0.8);
    
//    velocity arrows
    int vSpace = mMeasuredHeight - radius - py-20; 
    int vOriginX = px;
    int vOriginY = mMeasuredHeight - vSpace + (int) (vSpace/2.0);
    int lx = vOriginX - 5;
    int rx = vOriginX + 5;
        
    dVelocityAngle = dVelocityAngle - velocityAngle;
    velocityAngle = 0;
    
    double vx = velocityExcess*Math.sin(Math.toRadians(velocityAngle));
    double vy = velocityExcess*Math.cos(Math.toRadians(velocityAngle));
    double dx = desiredVelocity*Math.sin(Math.toRadians(dVelocityAngle));
    double dy = desiredVelocity*Math.cos(Math.toRadians(dVelocityAngle));
    
    double er = Math.hypot(dx-vx, dy-vy);
    if(er>DVMAX) {
    	er = vSpace/2.0;
    }
    else {
    	er = er*vSpace/2.0/DVMAX;
    }
    double ea = Math.atan2(dx-vx, dy-vy);
    
    int ex = (int) (vOriginX + er*Math.sin(ea));
    int ey = (int) (vOriginY - er*Math.cos(ea));
    

    courseAngle = courseAngle - bearing;
    
//    course select pointer
    int course1Startx = (int) (px + ((int) radius*.6*Math.sin(courseAngle)));
    int course1Starty = (int) (py - ((int) radius*.6*Math.cos(courseAngle)));
    int course1Endx = (int) (px + ((int) radius*Math.sin(courseAngle)));
    int course1Endy = (int) (py - ((int) radius*Math.cos(courseAngle)));
    
    int course2Startx = (int) (px - ((int) radius*.6*Math.sin(courseAngle)));
    int course2Starty = (int) (py + ((int) radius*.6*Math.cos(courseAngle)));
    int course2Endx = (int) (px - ((int) radius*Math.sin(courseAngle)));
    int course2Endy = (int) (py + ((int) radius*Math.cos(courseAngle)));
    
//    course deviation indicator
    int devMaxDeflection = (int)(radius*0.66); 
    
    int courseDevStartx = (int) (course1Startx - ((int) devMaxDeflection*dev/DEVMAX*Math.cos(courseAngle)));
    int courseDevStarty = (int) (course1Starty - ((int) devMaxDeflection*dev/DEVMAX*Math.sin(courseAngle)));
    int courseDevEndx = (int) (course2Startx - ((int) devMaxDeflection*dev/DEVMAX*Math.cos(courseAngle)));
    int courseDevEndy = (int) (course2Starty - ((int) devMaxDeflection*dev/DEVMAX*Math.sin(courseAngle)));
    
    System.out.println("deviation: " + dev);
    
    int dot1x = px;
    int dot1y = py;
    int dot2x = px + (int)(devMaxDeflection/2.0*Math.cos(courseAngle));
    int dot2y = py + (int)(devMaxDeflection/2.0*Math.sin(courseAngle));
    int dot3x = px - (int)(devMaxDeflection/2.0*Math.cos(courseAngle));
    int dot3y = py - (int)(devMaxDeflection/2.0*Math.sin(courseAngle));
    int dot4x = px + (int)(devMaxDeflection*Math.cos(courseAngle));
    int dot4y = py + (int)(devMaxDeflection*Math.sin(courseAngle));
    int dot5x = px - (int)(devMaxDeflection*Math.cos(courseAngle));
    int dot5y = py - (int)(devMaxDeflection*Math.sin(courseAngle));
    
    // Draw the background
    canvas.drawCircle(px, py, radius, circlePaint);
    // Rotate our perspective so that the ‘top’ is
    // facing the current bearing.
    
    canvas.save();
    
    System.out.println("bearing: " + Math.toDegrees(bearing));
    canvas.rotate((float)-Math.toDegrees(bearing), px, py);
    
    @SuppressWarnings("unused")
	int textWidth = (int)textPaint.measureText("W");
    int cardinalX = px;//-20;//(int)(textWidth/1);
    int cardinalY = py-radius+textHeight+25;

    textPaint.setTextSize(50f);
    textPaintAngle.set(textPaint); 
    textPaintAngle.setTextSize(25f);
    textPaintAngle.setTextAlign(Paint.Align.CENTER);

    // Draw the marker every 15 degrees and text every 45.
    for (int i = 0; i < 24; i++) {
      // Draw a marker.
      canvas.drawLine(px, py-radius, px, py-radius+10, markerPaint);

      canvas.save();
      canvas.translate(0, textHeight);

      // Draw the cardinal points
      if (i % 6 == 0) {
        String dirString = "";
        switch (i) {
          case(0)  : {
                       dirString = northString;
//                       int arrowY = 2*textHeight;
//                       canvas.drawLine(px, arrowY, px-5, 3*textHeight,
//                                       markerPaint);
//                       canvas.drawLine(px, arrowY, px+5, 3*textHeight, 
//                                       markerPaint);
                       break;
                     }
          case(6)  : dirString = eastString; break;
          case(12) : dirString = southString; break;
          case(18) : dirString = westString; break;
        }
        canvas.drawText(dirString, cardinalX, cardinalY, textPaint);
      } 

      else if (i % 2 == 0) {//3 == 0) {
        // Draw the text every alternate 45deg
        String angle = Integer.toString(i*15);
//        float angleTextWidth = textPaint.measureText(angle);

        int angleTextX = (int)(px);//-angleTextWidth/3);
        int angleTextY = py-radius+textHeight+20;
        canvas.drawText(angle, angleTextX, angleTextY, textPaintAngle);
      }
      canvas.restore();

      canvas.rotate(15, px, py);
    }
    canvas.restore();
    
    // glideslope ticks
    for(int i = 0; i < 5; i++) {
    	canvas.drawLine(0, tickStart + tickSpacing*i, tickLength, tickStart + tickSpacing*i, velocityBoxPaint);
    	canvas.drawLine(mMeasuredWidth-tickLength, tickStart + tickSpacing*i, mMeasuredWidth, tickStart + tickSpacing*i, velocityBoxPaint);
    }
    
    // glideslope
    
    
    System.out.println(glideHeight);
    canvas.drawLine(0,glideHeight-glideThick,glideLength,glideHeight,glideslopePaint);
    canvas.drawLine(0,glideHeight+glideThick,glideLength,glideHeight,glideslopePaint);
    canvas.drawLine(mMeasuredWidth,glideHeight-glideThick,mMeasuredWidth-glideLength,glideHeight,glideslopePaint);
    canvas.drawLine(mMeasuredWidth,glideHeight+glideThick,mMeasuredWidth-glideLength,glideHeight,glideslopePaint);
	

    
//    course select pointer
    canvas.drawLine(course1Startx,course1Starty,course1Endx,course1Endy,deviationPaint1);
    canvas.drawLine(course2Startx,course2Starty,course2Endx,course2Endy,deviationPaint1);
    canvas.drawCircle(course1Endx, course1Endy, 5, velocityPaint);
    
    
//    course deviation bar
    canvas.drawLine(courseDevStartx,courseDevStarty,courseDevEndx,courseDevEndy,deviationPaint2);
    
    // course deviation dots
    canvas.drawCircle(px, py, 5, textPaint);
    canvas.drawCircle(dot2x, dot2y, 5, textPaint);
    canvas.drawCircle(dot3x, dot3y, 5, textPaint);
    canvas.drawCircle(dot4x, dot4y, 5, textPaint);
    canvas.drawCircle(dot5x, dot5y, 5, textPaint);
    
//  lubber line
  canvas.drawLine(px, 0, px, (int) (radius*0.9), lubberPaint);
    
//    velocity excess arrow
  canvas.drawLine(lx,vOriginY,ex,ey,velocityPaint);
  canvas.drawLine(rx,vOriginY,ex,ey,velocityPaint);
  canvas.drawLine(vOriginX-50, vOriginY, vOriginX + 50, vOriginY, velocityBoxPaint);
  canvas.drawLine(vOriginX, vOriginY+50, vOriginX, vOriginY-50, velocityBoxPaint);
    
//    draw velocity box
  canvas.drawCircle(vOriginX, vOriginY, vSpace/2, velocityBoxPaint);

  // write time
//  canvas.drawText(Double.toString(time), px, vOriginY + 80, textPaint);
  int textStart = vOriginY + 100;
  canvas.drawText("Time: " + Integer.toString((int)time), px, textStart, textPaint);
  canvas.drawText("XTE: " + Integer.toString((int)trueDev),px,textStart+ttextHeight, textPaint);
  canvas.drawText("ATE: " + Integer.toString((int)ATE),px,textStart+2*ttextHeight, textPaint);  
  canvas.drawText("Speed er: " + Integer.toString((int)(desiredVelocity-trueSpeed)),px,textStart + 3*ttextHeight, textPaint);
  //canvas.drawText("Current: " + Integer.toString((int)currentWaypoint) + " m",px,textStart + 3*ttextHeight, textPaint);
  
  
  

  }
}