package edu.virginia.cs2110.ghosthunter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MapView extends View implements LocationListener {

	Paint paint = new Paint();
	int x; // x coordinate
	int y; // y coordinate
	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	static double originalLatitude; // latitude from initialized object
	static double originalLongitude; // longitude from initialized object

	double latitude; // latitude
	double longitude; // longitude

	boolean dialogup;
	Ghost closeGhost;
	static double WIDTH;
	static double HEIGHT;
	// 0.00001 degrees is 1.1132 meter.
	final static double meterToDegreesConversionFactor = 0.00001 / 1.1132;
	boolean killed;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

	// Declaring a Location Manager
	private final Context mContext;
	private LocationManager locationManager;

	ArrayList<Ghost> ghosts;
	ArrayList<MoneyBag> money;
ArrayList<Bomb> bombs;	

	int points = 0;
	int killCount = 0;

	// this loads the image from a google static maps URL
	public Drawable loadImageFromURL(String url) {
		// this takes care of the android.os.NetworkOnMainThreadException

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, url);
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	// the constructor sets up everything! and also sets a google static map as
	// the background.
	public MapView(Context context, ArrayList<Ghost> ghosts, ArrayList<MoneyBag> money, int x, int y) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);

		paint.setAntiAlias(true);
		mContext = context;
		this.ghosts = ghosts;
		this.money = money;

		Random rand = new Random();
		for(int i = 0; i<10; i++) {
		    Bomb bomb = new Bomb(rand.nextInt((int) WIDTH), rand.nextInt((int) HEIGHT));
		    bombs.add(bomb);
		    
		}
		
		
		Location l = getLocation();

		originalLatitude = l.getLatitude();
		originalLongitude = l.getLongitude();
		WIDTH = (double) x;
		HEIGHT = (double) y;
		String mapURL = "http://maps.googleapis.com/maps/api/staticmap?center="
				+ originalLatitude + "," + originalLongitude
				+ "&zoom=15&size=400x608&scale=2&maptype=roadmap&sensor=true";
		Drawable d = loadImageFromURL(mapURL);
		setBackground(d);
	}

	// public void drawMap(Canvas c) {
	// // constructs the URL of the png image of the map
	// String mapURL = "http://maps.googleapis.com/maps/api/staticmap?center="
	// + originalLatitude + "," + originalLongitude
	// + "&zoom=15&size=400x640&scale=2&maptype=roadmap&sensor=true";
	//
	// // this takes care of the android.os.NetworkOnMainThreadException
	// StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	// .permitAll().build();
	// StrictMode.setThreadPolicy(policy);
	//
	// // initializes the bitmap and url
	// Bitmap mFinalbitmap = null;
	// URL url = null;
	//
	// // creates the url and bitmap
	// try {
	// url = new URL(mapURL);
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// try {
	// mFinalbitmap = BitmapFactory.decodeStream(url.openConnection()
	// .getInputStream());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// // draws the map accordingly (and fits it to the screen)
	// Rect r = new Rect(0, 0, 1200, 1920);
	// c.drawBitmap(mFinalbitmap, null, r, paint);
	// }

	public void onDraw(Canvas canvas) {
		// calling drawMap in onDraw slows the ghost down (because it's
		// constantly being redrawn)
		// drawMap(canvas);

		Location l = getLocation();
		x = translateX(l.getLongitude());
		y = translateY(l.getLatitude());

		// this draws the player (a pacman)
		Rect player = new Rect(x - 40, y - 40, x + 40, y + 40);

		canvas.drawBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.player),
				null, player, paint);

		// this draws the ghosts
		for (Ghost g : ghosts) {
			g.move((int) WIDTH, (int) HEIGHT, 40);
			Rect r2 = new Rect((int) g.getX() - 40, (int) g.getY() - 40,
					(int) g.getX() + 40, (int) g.getY() + 40);
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.ghost), null, r2, paint);
		}
		
		for(MoneyBag m: money) {
 			paint.setColor(Color.BLUE);
			canvas.drawCircle((float) m.getX(), (float) m.getY(), 80, paint);
 		}

		paint.setColor(Color.BLACK);
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"Fonts/Ghoulish.ttf");
		paint.setTypeface(tf);

		paint.setTextSize(100);
		canvas.drawText("Points: " + points + " Kill Count: " + killCount, 5,
				85, paint);

		paint.setTextSize(50);
		canvas.drawText("ori.latitude: " + originalLatitude
				+ ", ori.longitude :" + originalLongitude, 5, 150, paint);
		canvas.drawText("cur latitude: " + l.getLatitude()
				+ ", cur longitude :" + l.getLongitude(), 5, 200, paint);
		canvas.drawText("ori.x: " + "600" + ", ori.y: " + "912", 5, 250, paint);
		canvas.drawText("cur x: " + x + ", cur y: " + y, 5, 300, paint);

		collideBones(x, y);
		collideGhost(player);
		collideMoney(player);

		invalidate();
	}
	
	
	public boolean bombGhosts(int x_, int y_) { //tests to see if user is close to any of the ghosts.
		//if it is, point variable closeGhost to that ghost-- this is so that the kill method can reference the same ghost and remove it 
		for (Ghost g : ghosts) {
		//here I am just calculating the distance between a ghost and the user and seeing if it less than 3 pixels/meters.
			if(Math.pow(Math.pow((g.getX()-x_),2) + Math.pow((g.getY()-y_),2),.5) <= 30) {
				closeGhost = g;
				return true;
			}
		}
		return false;
	}
			
	public void detonateBomb(int x_, int y_) { //tests to see if user is close to any of the ghosts.
		//if it is, point variable closeGhost to that ghost-- this is so that the kill method can reference the same ghost and remove it
		for (final Ghost g : ghosts) {
		for(Bomb b : bombs) {
			//here I am just calculating the distance between a ghost and the user and seeing if it less than 3 pixels/meters.
			if(Math.pow(Math.pow((b.getX1()-x_),2) + Math.pow((b.getY1()-y_),2),.5) <= 20) {
				if (dialogup == false) {
					dialogup = true;
					final Dialog dialog = new Dialog(mContext);
					dialog.setContentView(R.layout.killdialog);
					dialog.setTitle("You found a bomb! Bomb ghosts!");
					Button dialogButton = (Button) dialog
							.findViewById(R.id.dialogButtonOK);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (bombGhosts(x,y)) {
								killed = true;
								ghosts.remove(g);
								dialogup = false;
								dialog.dismiss();
								killCount = killCount + 100;
							}

						}
					});

					dialog.show();
				}
			
			}
		}
		}
			
	}

	public void collideBones(int x, int y) { // tests to see if user is close to
												// any of the Bones.
		for (final Ghost g : ghosts) {
			// here I am just calculating the distance between the user and a
			// bone and seeing if it less than 1 pixels/meters.
			if (Math.pow(
					Math.pow((g.getBone().getBoneX() - x), 2)
							+ Math.pow((g.getBone().getBoneY() - y), 2), .5) <= 20) {
				if (dialogup == false) {

					dialogup = true;

					final Dialog dialog = new Dialog(mContext);
					dialog.setContentView(R.layout.killdialog);
					dialog.setTitle("You found a bone. Kill Ghost!");

					Button dialogButton = (Button) dialog
							.findViewById(R.id.dialogButtonOK);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							killed = true;
							ghosts.remove(g);
							dialogup = false;
							dialog.dismiss();
							points = points + 100;

						}
					});

					
			
					
					dialog.show();

					final Timer t = new Timer();
					t.schedule(new TimerTask() {
						public void run() {
							dialog.dismiss(); // when the task active then close
												// the dialog
							t.cancel(); // also just top the timer thread,
										// otherwise, you may receive a crash
										// report
							if (killed == false) {
								g.nullBone();
							}
							dialogup = false;
						}
					}, 1000); // after 2 second (or 2000 miliseconds), the task
								// will be active.

				}

			}
		}

	}
	
	
	public void collideMoney(Rect player) {
		// tests to see if user is close to any of the ghosts.

		// if it is, point variable closeGhost to that ghost-- this is so that
		// the kill method can reference the same ghost and remove it
		for (MoneyBag g : money) {
			// seeing if the ghost has collided with the player by checking if
			// their images have intersected
			if (player.intersect(new Rect((int) g.getX() - 40,
					(int) g.getY() - 40, (int) g.getX() + 40,
					(int) g.getY() + 40))) {
				points += 200;
				money.remove(g);
				
			}
				
			}
	}


	public boolean collideGhost(Rect player) {
		// tests to see if user is close to any of the ghosts.

		// if it is, point variable closeGhost to that ghost-- this is so that
		// the kill method can reference the same ghost and remove it
		for (Ghost g : ghosts) {
			// seeing if the ghost has collided with the player by checking if
			// their images have intersected
			if (player.intersect(new Rect((int) g.getX() - 40,
					(int) g.getY() - 40, (int) g.getX() + 40,
					(int) g.getY() + 40))) {
				if (dialogup == false) {
					closeGhost = g;
					killed = false;
					dialogup = true;

					final Dialog dialog = new Dialog(mContext);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.killdialog);

					Button dialogButton = (Button) dialog
							.findViewById(R.id.dialogButtonOK);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							killed = true;
							ghosts.remove(closeGhost);
							Random rand = new Random();
							ghosts.add(new Ghost(rand.nextInt((int) WIDTH), rand.nextInt((int) HEIGHT)));
							dialogup = false;
							dialog.dismiss();
							points = points + 100;
							killCount++;
						}
					});
					dialog.show();
					
					Button ignore = (Button) dialog.findViewById(R.id.dialogButtonIgnore);
					// if button is clicked, close the custom dialog
					ignore.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							dialog.dismiss();
		

						}
					});
					
					

					final Timer t = new Timer();
					t.schedule(new TimerTask() {
						public void run() {
							dialog.dismiss(); // when the task active then close
												// the dialog
							t.cancel(); // also just top the timer thread,
										// otherwise, you may receive a crash
										// report
							if (killed == false) {
								points = points - 50;
							}
							dialogup = false;
						}
					}, 1000); // after 2 second (or 2000 miliseconds), the task
								// will be active.
				}
			}
		}
		return false;

	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(MapView.this);
		}
	}

	// /**
	// * Function to get latitude
	// * */
	// public double getLatitude() {
	// if (location != null) {
	// latitude = location.getLatitude();
	// }
	//
	// // return latitude
	// return latitude;
	// }
	//
	// /**
	// * Function to get longitude
	// * */
	// public double getLongitude() {
	// if (location != null) {
	// longitude = location.getLongitude();
	// }
	// // return longitude
	// return longitude;
	// }

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * launch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	// calculates where the player should be on the screen (in pixels) for x
	// coordinates(longitude)
	public static int translateX(double newLong) {
		// 15 is the zoom. 1 pixel is about 0.0000429 degrees.
		double pixelInXToDegreesConversionFactor = (360.0 / 256.0)
				/ Math.pow(2, 15);
		pixelInXToDegreesConversionFactor /= 3;
		// how many degrees you have moved longitude wise
		double difference = newLong - originalLongitude;
		// converts that degrees to pixels
		difference /= pixelInXToDegreesConversionFactor;
		// adds the difference to the starting point (x=600)
		double newX = WIDTH / 2 + difference;
		return (int) newX;
	}

	// calculates where the player should be on the screen (in pixels) for y
	// coordinates(latitude)
	public static int translateY(double newLat) {
		// 15 is zoom. 1 pixel is about 0.0000003804 degrees.
		// not using this because it's kinda messed up.
		// double pixelInYToDegreesConversionFactor = (360.0 / 256.0)
		// / Math.pow(2, 15)
		// * Math.log(Math.tan(Math.PI / 4 + originalLatitude / 2))
		// / originalLatitude;
		// pixelInYToDegreesConversionFactor /= 3;

		double pixelInYToDegreesConversionFactor = (360.0 / 256.0)
				/ Math.pow(2, 15);
		pixelInYToDegreesConversionFactor /= 3;
		// how many degrees you have moved latitude wise
		double difference = originalLatitude - newLat;
		// converts that degrees to pixels
		difference /= pixelInYToDegreesConversionFactor;
		// adds the difference to the starting point (y=960)

		double newY = HEIGHT / 2 + difference;
		return (int) newY;
	}

}