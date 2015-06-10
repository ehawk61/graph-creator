/*
 * This file is part of GraphCreator.

    GraphCreator is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GraphCreator is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GraphCreator.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright (C) 2012-2015 Jonathan L. Meek
 */
package graphcreator.android;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GraphCreatorActivity extends Activity {
    private CSVConverter converter;
	private DownloadManager downloadHandlerQueue;
	private long latestDownloadId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urllayout);
        downloadHandlerQueue =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        graphSetupButtonListener();
        converter = new CSVConverter(getApplicationContext(),"data");
    }//end onCreate
    
	public void pullDataToGraph(View mainView){
		TextView urlObject = (TextView) findViewById(R.id.url);
    	String urlText = urlObject.getText().toString();
		checkUrl(urlText,mainView); 
    }//end pullDataToGraph method
	
    public void updateStatus(View mainView){
    	TextView statusOutputTextView = ((TextView)findViewById(R.id.downloadDisplay));
    	Cursor downloadFileCursor = downloadHandlerQueue.query(new DownloadManager.Query().setFilterById(latestDownloadId));
		
    	if(downloadFileCursor.moveToFirst()){
			converter.convertCursorToDBData(downloadFileCursor, "table", statusOutputTextView, getApplicationContext());
		}
		else{
			Toast.makeText(getApplicationContext(),"Data Not Downloaded",Toast.LENGTH_LONG).show();
		}
   	}//end displayText
	
    public void addData(View v){
    	final TextView statusOutputTextView = (TextView)findViewById(R.id.downloadDisplay);    	
    	statusOutputTextView.setText("");
    	Cursor downloadFileCursor = downloadHandlerQueue.query(new DownloadManager.Query().setFilterById(latestDownloadId));
		
		if(downloadFileCursor.moveToFirst()){
			converter.convertCursorToDBData(downloadFileCursor, "rows", statusOutputTextView, getApplicationContext());

		}
		else {
			Toast.makeText(getApplicationContext(),"Data Not Found", Toast.LENGTH_LONG).show();
		}
    }//end Add Data method
    
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
          findViewById(R.id.urlButton).setEnabled(true);
        }
      };
	  
  /* Start of private methods in the class */
  	private void graphSetupButtonListener() {    
    	Button graphSetupButton = (Button) findViewById(R.id.swtichScreenButton);
    	graphSetupButton.setOnClickListener(new OnClickListener() {    		
				public void onClick(View arg0) {     
					Intent intent = new Intent(getApplicationContext(), webViewActivity.class);
					startActivity(intent);   
				}
			});
	}//end graphSetupButtonListener method

   private DownloadManager.Request startDownload(Uri url){
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
		DownloadManager.Request test = new DownloadManager.Request(url);
		test.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
		test.setAllowedOverRoaming(false);
		return test;
	}//end startDownload method
    
	private void checkUrl(String urlText, View v){
		if(urlText.equals(""))
    	{
    		Toast.makeText(getApplicationContext(),"URL Is Blank", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		Uri urlAddress = Uri.parse(urlText);
    		DownloadManager.Request downloadRequest = startDownload(urlAddress);
    		v.setEnabled(false);
    		latestDownloadId = downloadHandlerQueue.enqueue(downloadRequest);
    	}
	}//end checkUrl method


}//end GraphCreatorActivity class
