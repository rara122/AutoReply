package edu.fsu.cs.Monitoring_Autoreply2;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MyList extends ListActivity {
  
	@Override
		public void onCreate(Bundle bundle) {
		    super.onCreate(bundle);
		    Intent intent = getIntent();
		    String[] MOBILE_OS = intent.getStringArrayExtra("Array");
//		    String[] MOBILE_OS = {"1", "2"};
		    ArrayList <String> list = new ArrayList <String>();
		    list.add ("New");
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, list);

			setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));
//		    setListAdapter(adapter);
		  }

	
	public class MobileArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
	 
		public MobileArrayAdapter(Context context, String[] values) {
			super(context, R.layout.mylist, values);
			this.context = context;
			this.values = values;
		}
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View rowView = inflater.inflate(R.layout.mylist, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
	 
			// Change icon based on name
			String s1 = values[position];
			String s = s1.substring(0, 10);
			textView.setText(s);
			
			imageView.setImageResource(R.drawable.phone);
	 
			System.out.println(s);
	 
			if (s1.length() == 12) {
				imageView.setImageResource(R.drawable.sms);
			}/* else if (s1.equals("iOS")) {
				imageView.setImageResource(R.drawable.autoreply_logo);
			} else if (s1.equals("Blackberry")) {
				imageView.setImageResource(R.drawable.autoreply_logo);
			} else {
				imageView.setImageResource(R.drawable.autoreply_logo);
			}*/
	 
			return rowView;
		}
	}
	
}
 
