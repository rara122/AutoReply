package edu.fsu.cs.Monitoring_Autoreply2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class BB extends BroadcastReceiver{
	String finalMessage;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		boolean respond = true;
		
		//the message and the boolean parameter to send or not send message;
		respond = arg1.getBooleanExtra("respond", true);
		if (respond == false)
			finalMessage = arg1.getStringExtra("message");
		Log.i("BB", finalMessage);
	
		if (respond == true)
        {
			Bundle bundle = arg1.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            
            //Toast.makeText(arg0, messages[0].getOriginatingAddress(), Toast.LENGTH_SHORT).show();
            SmsManager smsManager = SmsManager.getDefault();
            
            // added this to not send message when it is just changing the text
            
            	smsManager.sendTextMessage(messages[0].getOriginatingAddress(),null, finalMessage ,null, null);
            	Intent intent2open = new Intent(arg0, MainActivity.class);
                intent2open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                String name = "KEY";
                String value = messages[0].getOriginatingAddress() + " *";
                intent2open.putExtra(name, value);
                arg0.startActivity(intent2open);
        		
            }	
            respond = true;
            
            
	}

	
	
	
	
	
	
}
