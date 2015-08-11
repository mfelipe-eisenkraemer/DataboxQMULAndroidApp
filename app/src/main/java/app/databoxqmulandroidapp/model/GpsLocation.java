package app.databoxqmulandroidapp.model;

import android.location.Location;

public class GpsLocation {

	private long id;
	private double mLatitude;
	private double mLongitude;
	private float mAccuracy;
	private Location mLocation;

	public GpsLocation(Location location) {
		setmLocation(location);
		setmAccuracy(location.getAccuracy());
		setmLatitude(location.getLatitude());
		setmLongitude(location.getLongitude());
	}

	public GpsLocation() {}

	public void setLocation(Location location) {
		setmLocation(location);
	}

	public android.location.Location getLocation() {
		return getmLocation();
	}

	public boolean intersects(Location location) {
		double tolerance = 1000;
		return (getmLocation().distanceTo(location) <= tolerance);
	}

	@Override
	public String toString(){
		return "Latitude: " + mLatitude + " Longitude: " + mLongitude + " Accurancy: " + mAccuracy;

	}
	public double getmLatitude() {
		return mLatitude;
	}
	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}
	public double getmLongitude() {
		return mLongitude;
	}
	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
	public float getmAccuracy() {
		return mAccuracy;
	}
	public void setmAccuracy(float mAccuracy) {
		this.mAccuracy = mAccuracy;
	}
	public android.location.Location getmLocation() {
		return mLocation;
	}
	public void setmLocation(Location mLocation) {
		this.mLocation = mLocation;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
