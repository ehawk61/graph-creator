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
    
    Copyright (C) 2012-2014 Jonathan L. Meek
 */

package graphcreator.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class GraphCreatorActivity extends Activity {
    /** Called when the activity is first created. */
	InputStream in;
	DownloadManager mgr;
	long latestDownload;
	String tableHeader;
	static String [] headerItems;
	String tableHeaderSpilt;
	static final String dbName ="data";
	Database database = new Database(this, dbName);
	static SQLiteDatabase db;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mgr =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        graphSetupButtonListener();
    }//end onCreate
    
    public void graphSetupButtonListener() {    
    	Button graphSetupButton = (Button) findViewById(R.id.swtichScreenButton);
    	graphSetupButton.setOnClickListener(new OnClickListener() {    		
    		public void onClick(View arg0) {     
    			Intent intent = new Intent(getApplicationContext(), webViewActivity.class);
    			startActivity(intent);   
    		}
     
    	});
	}//end graphSetupButtonListener method

	public void pullDataToGraph(View mainView){
		TextView urlObject = (TextView) findViewById(R.id.url);
    	String urlText = urlObject.getText().toString();
		checkUrl(urlText,mainView); 
    }//end pullDataToGraph method
	
    public void displayText(View v){
    	TextView text = ((TextView)findViewById(R.id.downloadDisplay));
    	Cursor c = mgr.query(new DownloadManager.Query().setFilterById(latestDownload));
		
    	if(c.moveToFirst()){
			String fileNameString =	c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));	
    		c.moveToFirst();
			createFileInputStreamFromData(fileNameString); 
   			dbTableCreation();
			text.setText(tableHeaderSpilt);
		}
		else{
			Toast.makeText(getApplicationContext(),"Data not downloaded",Toast.LENGTH_LONG).show();
		}
   	}//end displayText
	
	
    public void addData(View v){
    	final TextView downloadDisplay = (TextView)findViewById(R.id.downloadDisplay);    	
    	downloadDisplay.setText("");
    	Cursor downloadFileCursor = mgr.query(new DownloadManager.Query().setFilterById(latestDownload));
		
		if(downloadFileCursor.moveToFirst()){
    		downloadFileCursor.moveToFirst();
    		String fileNameString =	downloadFileCursor.getString(downloadFileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
    	    	
    		try{
    			in = new FileInputStream(fileNameString);
    		}catch(FileNotFoundException e){
    			e.printStackTrace();
    		}//end try/catch statement
    		dbRowAdditionThreadRun();
    		downloadDisplay.setText("Finished importing to database");
		}
		else {
			Toast.makeText(getApplicationContext(),"Data Not found", Toast.LENGTH_LONG).show();
		}
    }//end Add Data method
    
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
          findViewById(R.id.urlButton).setEnabled(true);
        }
      };
	  
  /* Start of private methods in the class */
  
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
    		Toast.makeText(getApplicationContext(),"URL is blank", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		Uri urlAddress = Uri.parse(urlText);
    		DownloadManager.Request downloadRequest = startDownload(urlAddress);
    		v.setEnabled(false);
    		latestDownload = mgr.enqueue(downloadRequest);
    	}
	}//end checkUrl method
	
	private void createFileInputStreamFromData(String fileNameString){
		try{
			in = new FileInputStream(fileNameString);
		}catch(FileNotFoundException e){
			String stackTrace = Log.getStackTraceString(e);
			Toast.makeText(getApplicationContext(),stackTrace,Toast.LENGTH_LONG).show();
		}//end try/catch statement
	}//end createFileInputStreamFromData method
	
    private void dbTableCreation(){
		Thread t = new Thread(new Runnable(){
    			public void run(){
    				try{
    					Scanner scanner = new Scanner(new InputStreamReader(in));
    		    		tableHeader = scanner.nextLine();
    		    		scanner.close();
    		    		headerItems = tableHeader.split(",");
    		    		tableHeaderSpilt = "CREATE TABLE "+ dbName +" (";
    					for(String item: headerItems){
    						String corrected_item1 = item.replace(" ", "_");
    						String corrected_item2 = corrected_item1.replaceAll("\\W","");

    						String tableHeaderSetup = corrected_item2 +" STRING,";
    						tableHeaderSpilt += tableHeaderSetup;
    					}
    					tableHeaderSpilt = tableHeaderSpilt.substring(0,tableHeaderSpilt.length()-1);
    					tableHeaderSpilt += ")";
    					db = database.getWritableDatabase();
    					db.execSQL("DROP TABLE IF EXISTS "+dbName);
    					db.execSQL(tableHeaderSpilt);
    				}catch (Throwable t){

    				}//end try/catch statement
    			}//end run method
    		});

		t.start();      	
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}//end try/catch statement
	}//end dbTableCreation method
	
	private void dbRowAdditionThreadRun(){
		Thread t = new Thread(new Runnable(){
			public void run(){
				dbRowAddition();
    		}//end run method
		});

		t.start();
		try{
			t.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}//end try/catch statement
		
	}//end dbRowAdditionThreadRun method
	
	public void dbRowAddition(){
		try{
			Scanner scanner = new Scanner(new InputStreamReader(in));    		    	
			ArrayList <String> HeaderArray = new ArrayList<String>(Arrays.asList(headerItems));
			String column_listings = "";

			for(int i = 0; i<HeaderArray.size(); i++){
				column_listings +=HeaderArray.get(i)+",";
			}
	
			column_listings = column_listings.substring(0, column_listings.length()-1);
			scanner.nextLine();

			while (scanner.hasNextLine()){
				String curr = scanner.nextLine();   		    		
				Log.d("scanner output", curr);
				String sql_insert_statement="INSERT INTO "+dbName+" ("+column_listings+") VALUES ("+curr+");";
				Log.d("sql_statement", sql_insert_statement);
				db.execSQL(sql_insert_statement);
			}

			scanner.close();    	  	
		}catch (Throwable t){

		}//end try/catch statement
	}//end dbRowAddition method
		
	
}//end DownloadManagerActivity class
