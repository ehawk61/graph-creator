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
	DownloadManager downloadHandlerQueue;
	long latestDownloadId;
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
        downloadHandlerQueue =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
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
	
    public void updateStatus(View mainView){
    	TextView statusOutputTextView = ((TextView)findViewById(R.id.downloadDisplay));
    	Cursor downloadFileCursor = downloadHandlerQueue.query(new DownloadManager.Query().setFilterById(latestDownloadId));
		
    	if(downloadFileCursor.moveToFirst()){
			convertCursorToDBData(downloadFileCursor,"table",statusOutputTextView);
		}
		else{
			Toast.makeText(getApplicationContext(),"Data not downloaded",Toast.LENGTH_LONG).show();
		}
   	}//end displayText
	
    public void addData(View v){
    	final TextView statusOutputTextView = (TextView)findViewById(R.id.downloadDisplay);    	
    	statusOutputTextView.setText("");
    	Cursor downloadFileCursor = downloadHandlerQueue.query(new DownloadManager.Query().setFilterById(latestDownloadId));
		
		if(downloadFileCursor.moveToFirst()){
			convertCursorToDBData(downloadFileCursor,"rows",statusOutputTextView);
			
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
    		latestDownloadId = downloadHandlerQueue.enqueue(downloadRequest);
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
   	}//end dbTableCreation method
	
	private void dbRowAddition(){
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
	
	private void convertCursorToDBData(Cursor fileCursor, String dbOperation, TextView statusView){
		if(dbOperation.equals("table")){
			String fileNameString =	fileCursor.getString(fileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));	
    		fileCursor.moveToFirst();
			createFileInputStreamFromData(fileNameString); 
   			dbTableCreation();
			statusView.setText(tableHeaderSpilt);
		}

		else if(dbOperation.equals("rows")){
			String fileNameString =	fileCursor.getString(fileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));	
    		fileCursor.moveToFirst();
			createFileInputStreamFromData(fileNameString); 
   			dbRowAddition();
			statusView.setText("Finished importing to database");
		}
		else{
			Toast.makeText(getApplicationContext(),"Invaild database operation",Toast.LENGTH_LONG).show();
		}
	}//end convertCursorToDBData method
			
}//end GraphCreatorActivity class
