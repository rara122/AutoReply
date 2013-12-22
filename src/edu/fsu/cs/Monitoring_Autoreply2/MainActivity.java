package edu.fsu.cs.Monitoring_Autoreply2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.android.internal.telephony.ITelephony;

import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	Button start1, off, viewlog;
	BB receiver;
	ToggleButton toggleButton;
	String preferences;
	String theMessage;
	EditText txt;
	Integer blockedcalls = 0;
	//declare image objects
	ImageView startButton,
			  stopButton,
			  editButton,
			  saveButton,
			  cancelButton,
			  dividerImage;
		
	//text view which displays the user's current response message
	TextView responseMsgDisplay;
	//listens for phone calls
	TelephonyManager tm;
	ArrayList <String> log;
	int logCount;
	String[] Array;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Create a new list of Logs
		log = new ArrayList<String> ();
		logCount = 0;
		
		txt = (EditText) findViewById(R.id.txt);
		//Toast.makeText(getBaseContext(),"The Information Has Been Added to the Database",Toast.LENGTH_LONG).show();
		receiver = new BB();
		
		if( txt.getText().toString().equals(""))
		{
			SharedPreferences message = getSharedPreferences(preferences, 0);
			theMessage = message.getString("message", "I'm driving right now, I'll get back to you once I'm parked. \n-Sent by autoReply");
			SharedPreferences.Editor editor = message.edit();
			editor.putString("message", theMessage);
			editor.commit();
			txt.setText(theMessage);
		}
		
		
		//get handle on message display
		responseMsgDisplay = (TextView) findViewById(R.id.responseMsgDisplay);
		//populate display with the current response message
		responseMsgDisplay.setText(theMessage);
		//get on buttons
		startButton = (ImageView) findViewById(R.id.startImage);
		stopButton = (ImageView) findViewById(R.id.stopImage);
		editButton = (ImageView) findViewById(R.id.editImage);
		saveButton = (ImageView) findViewById(R.id.saveImage);
		cancelButton = (ImageView) findViewById(R.id.cancelImage);
		dividerImage = (ImageView) findViewById(R.id.dividerImage);
		viewlog = (Button) findViewById(R.id.logButton);
		
		//edit text and stop, save, and cancel buttons need to be hidden initially
		stopButton.setVisibility(View.INVISIBLE);
		saveButton.setVisibility(View.INVISIBLE);
		cancelButton.setVisibility(View.INVISIBLE);
		txt.setVisibility(View.INVISIBLE);
		viewlog.setVisibility(viewlog.INVISIBLE);
		//hide divider image
		dividerImage.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void StartReply(View view) {
	    //hide start button and edit button
		startButton.setVisibility(View.INVISIBLE);
		editButton.setVisibility(view.INVISIBLE);
		//show stop button
    	stopButton.setVisibility(View.VISIBLE);
    	//ON
    	Log.i("BB", "startReply");
    	IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    	filter.addAction("my.action.string");
		registerReceiver(receiver, filter);
		setMessage();
		//put code to make the boradcast receiver read the message
		Intent setMessage1 = new Intent("my.action.string");
		setMessage1.putExtra("message", theMessage);
		setMessage1.putExtra("respond", false);
		sendBroadcast(setMessage1);
		Log.i("BB", "Settint the argument afdter pressing on");
		
		tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		tm.listen(OnCall, PhoneStateListener.LISTEN_CALL_STATE);
		viewlog.setVisibility(viewlog.INVISIBLE);
		logCount = 0;
		log.clear();
	}
	
	public void StopReply(View view) {
	    //hide stop button
		stopButton.setVisibility(View.INVISIBLE);
		//show start button and edit button
    	startButton.setVisibility(View.VISIBLE);
    	editButton.setVisibility(view.VISIBLE);
    	//OFF
    	unregisterReceiver(receiver);
    	
    	tm.listen(OnCall, PhoneStateListener.LISTEN_NONE);
    	viewlog.setText("View Log ("+logCount+")");    		
    	
    	if(logCount != 0){
        	viewlog.setVisibility(viewlog.VISIBLE);
    	}
   }
	
	public void EditResponse(View view) {
	    //hide text view and edit button
		responseMsgDisplay.setVisibility(view.INVISIBLE);
		editButton.setVisibility(view.INVISIBLE);
		//show edit text, save button, and cancel button
		txt.setVisibility(view.VISIBLE);
		saveButton.setVisibility(view.VISIBLE);
		cancelButton.setVisibility(view.VISIBLE);
		//place divider image
		dividerImage.setVisibility(view.VISIBLE);
   }
	
	public void SaveResponse(View view) {
		setMessage();
		responseMsgDisplay.setText(theMessage);
	    //show text view and edit button
		responseMsgDisplay.setVisibility(view.VISIBLE);
		editButton.setVisibility(view.VISIBLE);
		//hide edit text, save button, and cancel button
		txt.setVisibility(view.INVISIBLE);
		saveButton.setVisibility(view.INVISIBLE);
		cancelButton.setVisibility(view.INVISIBLE);
		//hide divider image
		dividerImage.setVisibility(view.INVISIBLE);
   }
	
	public void CancelEdit(View view) {
	    //show text view and edit button
		responseMsgDisplay.setVisibility(view.VISIBLE);
		editButton.setVisibility(view.VISIBLE);
		//hide edit text, save button, and cancel button
		txt.setVisibility(view.INVISIBLE);
		saveButton.setVisibility(view.INVISIBLE);
		cancelButton.setVisibility(view.INVISIBLE);
		//hide divider image
		dividerImage.setVisibility(view.INVISIBLE);
   }
	
	public void setMessage()				// this is not being called. 
	{
		SharedPreferences message = getSharedPreferences(preferences, 0);
		theMessage = txt.getText().toString();
		SharedPreferences.Editor editor = message.edit();
		editor.putString("message", theMessage);
		editor.commit();
	}
	
	private PhoneStateListener OnCall = new PhoneStateListener() {
	    public void onCallStateChanged(int state, String incomingNumber) {
	    	if(state == TelephonyManager.CALL_STATE_RINGING){
	    		Toast.makeText(getApplicationContext(), incomingNumber, Toast.LENGTH_SHORT).show();
	    		Class c = null;
	    		Method m = null;
				try {
					c = Class.forName(tm.getClass().getName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				try {
					m = c.getDeclaredMethod("getITelephony");
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	    m.setAccessible(true);
	    	    ITelephony telephony = null;
				try {
					telephony = (ITelephony)m.invoke(tm);
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
	    	    try {
					telephony.endCall();
					blockedcalls++;
					SmsManager smsManager = SmsManager.getDefault();
		            
		            smsManager.sendTextMessage(incomingNumber, null, theMessage ,null, null);
		            //ADD TO LOG
					log.add(incomingNumber);
					logCount++;
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
	    	}
	    }
};

	public void logClick(View v){
		Array = new String[log.size()];
		Array = log.toArray(Array);
		Intent intent = new Intent (this, MyList.class);
		intent.putExtra("Array", Array);
		startActivity(intent);
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
    Log.d("YourActivity", "onNewIntent is called!");

    log.add(intent.getStringExtra("KEY"));
    logCount++;

    super.onNewIntent(intent);
	}
		
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(receiver);
	
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		//unregisterReceiver(receiver);
	
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
	}
	
	@Override
	public void onBackPressed(){
		
	}
	
	
}
