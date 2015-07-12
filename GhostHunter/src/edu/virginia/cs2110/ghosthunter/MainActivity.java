package edu.virginia.cs2110.ghosthunter;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	Button btnShowLocation;
	TextView locationtext;

	int x; // location of user in pixels
	int y; // location of user in pixels
	ArrayList<Ghost> ghosts;
	ArrayList<MoneyBag> money;
	int numGhosts;
	Random rand;
	MapView drawView;
	MapView2 drawView2;
	int numMoney;

	int displaywidth;
	int displayheight;

	// this checks if the user is online when the app is started
	@SuppressLint("NewApi")
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnected();
	}

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// the nexus 7 has a display of 1200x1920, but the back/home/options bar
		// at the bottom takes up some space.
		displaywidth = 1200;
		displayheight = 1824;

		// this creates random ghosts
		Random rand = new Random();
		ghosts = new ArrayList<Ghost>();
		money = new ArrayList<MoneyBag>();
		numGhosts = 10;
		numMoney = 5;
		
		for (int i = 0; i < numGhosts; i++) {
			
			int x = rand.nextInt(displaywidth);
			int y = rand.nextInt(displayheight);
			
			Ghost ghost_i = new Ghost(x, y);
			
			ghosts.add(ghost_i);
		}
		for(int i = 0; i<numMoney; i++) {
		    MoneyBag moneyBag_i = new MoneyBag(rand.nextInt(displaywidth), rand.nextInt(displayheight));
		    money.add(moneyBag_i);
		    
		}

		// if the user is not online, a view containing an error message will
		// appear instead of the mapview.
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void onLevelClick(View view)  
	{  
		if (isOnline()) {
			drawView2 = new MapView2(this, ghosts, money, displaywidth, displayheight);
			setContentView(drawView2);
			drawView2.requestFocus();
		} else {
			setContentView(R.layout.nointernet);
		}
	}  
	
	public void onEndlessClick(View view)  
	{  
		if (isOnline()) {
			drawView= new MapView(this, ghosts, money, displaywidth, displayheight);
			setContentView(drawView);
			drawView.requestFocus();
		} else {
			setContentView(R.layout.nointernet);
		}
	}  
	
	
	
	

}
