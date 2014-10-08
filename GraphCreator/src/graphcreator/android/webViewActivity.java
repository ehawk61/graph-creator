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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import graphcreator.android.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



 public class webViewActivity extends Activity{
    	Cursor dbcursor;
    	String sqlStatement="";
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState);
    		setContentView(R.layout.datagraph);
    		Spinner Xspinner = (Spinner) findViewById(R.id.xheaderChoice);
    		Spinner Yspinner = (Spinner)findViewById(R.id.yheaderChoice);
    	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,GraphCreatorActivity.headerItems);    	      	            
    	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	    Xspinner.setAdapter(adapter);
    	    Yspinner.setAdapter(adapter);
    	}
    	
    	public void sqlStringDisplay (View v){
    		TextView text = (TextView)findViewById(R.id.sqlStringDisplay);
    		Spinner Xspinner = (Spinner)findViewById(R.id.xheaderChoice);
    		Spinner Yspinner = (Spinner)findViewById(R.id.yheaderChoice);    		
    		
    		sqlStatement = " SELECT "+Xspinner.getSelectedItem().toString()+","+ Yspinner.getSelectedItem().toString() + " FROM "+ GraphCreatorActivity.dbName;
    		Log.d("db_statement", sqlStatement);
    		text.setText(sqlStatement);
    	}//end sqlStringDisplay method
    
    	public void dbQuery(View v){
    		
    		TextView text = (TextView)findViewById(R.id.dbQueryStatus);
    		try{
    			dbcursor = GraphCreatorActivity.db.rawQuery(sqlStatement, null);
    			Log.d("row count", Integer.toString(dbcursor.getCount()));
    			Log.d("column count",Integer.toString(dbcursor.getColumnCount()) );
    		}catch(Exception e){
    			Toast.makeText(getApplicationContext(), "Query failed", Toast.LENGTH_LONG).show();
    		}  		
    		dbcursor.close();
    		text.setText("Query completed");
    		
    	}//end dbQuery method
    	
    	public void generateFile(View v){
    		RadioGroup g = (RadioGroup)findViewById(R.id.graphType);
    		int selected = g.getCheckedRadioButtonId();
    		String graph_type = ((RadioButton)findViewById(selected)).getTag().toString();
    		String graph = "";
    		dbcursor = GraphCreatorActivity.db.rawQuery(sqlStatement, null);
    		TextView text = (TextView)findViewById(R.id.dbQueryStatus);
    		
    		try{
        		File root = Environment.getExternalStorageDirectory();
        		if(root.canWrite()){
        			if(graph_type.equals("bar")){
        				File gpxfile = new File(root, "bar_graph.html");
            	        FileWriter gpxwriter = new FileWriter(gpxfile);
            	        BufferedWriter out = new BufferedWriter(gpxwriter);
            	        out.write("<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Intro to jQuery Mobile</title><link rel=\"stylesheet\" href=\"http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.css\" /><script src=\"http://code.jquery.com/jquery-1.4.4.min.js\"></script><script src=\"http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.js\"></script><script src=\"http://dl.dropbox.com/u/1124545/flot/jquery.flot.js\"></script></head>");
            	        out.write("<body><h1>Generated Bar Graph</h1>");
            	        out.write("<div id=\"placeholder\" style=\"width:600px;height:300px;\"></div>");
            	        out.write("<p>Testing generation</p>");
            	        out.write("<script type=\"text/javascript\">");
            	        out.write("$(function () {");
            	        out.write("var d1 =[");
            	        
            	        dbcursor.moveToFirst();
            	        
            	        while(dbcursor.isAfterLast()==false){            	        	
            	        	graph +="["+dbcursor.getString(0)+","+dbcursor.getString(1)+"],";            	        	
            	        	dbcursor.moveToNext();
            	        }//end while statement
            	        
            	        graph = graph.substring(0,graph.length()-1);
            	        	
            	        out.write(graph);
            	        Log.d("dataset", graph);
            	        dbcursor.close();
            	        
            	        out.write("];");
            	        out.write("$.plot($(\"#placeholder\"), [{data: d1, bars: {show:true}}]);});");
            	        out.write("</script></body></html>");
            	        out.close();
        			}//end if statement 
        			
        			else if(graph_type.equals("line")){
        				File gpxfile = new File(root, "line_graph.html");
            	        FileWriter gpxwriter = new FileWriter(gpxfile);
            	        BufferedWriter out = new BufferedWriter(gpxwriter);
            	        String html_header = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Intro to jQuery Mobile</title><link rel=\"stylesheet\" href=\"http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.css\" /><script src=\"http://code.jquery.com/jquery-1.4.4.min.js\"></script><script src=\"http://code.jquery.com/mobile/1.0a2/jquery.mobile-1.0a2.min.js\"></script><script src=\"http://dl.dropbox.com/u/1124545/flot/jquery.flot.js\"></script></head>";
            	        
            	        out.write(html_header);            	        
            	        out.write("<body><h1>Generated Line Graph</h1>");
            	        out.write("<div id=\"placeholder\" style=\"width:600px;height:300px;\"></div>");
            	        out.write("<p>Testing generation</p>");
            	        out.write("<script type=\"text/javascript\">");
            	        out.write("$(function () {");
            	        out.write("var d1 =[");
            	        
            	        dbcursor.moveToFirst();
            	        while(dbcursor.isAfterLast()==false){
            	        	graph +="["+dbcursor.getString(0)+","+dbcursor.getString(1)+"],";            	        	
            	        	dbcursor.moveToNext();
            	        }//end while statement 
            	        graph = graph.substring(0,graph.length()-1);
            	        	
            	        out.write(graph);
            	        Log.d("dataset", graph);
            	        dbcursor.close();
            	        
            	        out.write("];");
            	        out.write("$.plot($(\"#placeholder\"), [{data: d1, lines:{show:true}}]);});");
            	        out.write("</script></body></html>");
            	        out.close();
        			}//end else if statement 
        			
        		}//try statement 
        		text.setText("HTML graph Generated");
        	}catch(IOException e){
        		Toast.makeText(getApplicationContext(), "Unable to write to file", Toast.LENGTH_LONG).show();
        		e.printStackTrace();
        	}//end catch statement 
    	
    	}//end generateFile method
    	
    	public void loadScreen(View v){
    		RadioGroup g = (RadioGroup)findViewById(R.id.graphType);
    		int selected = g.getCheckedRadioButtonId();
    		String graph_type = ((RadioButton)findViewById(selected)).getTag().toString();
    		setContentView(R.layout.weblayout);
    		WebView display = (WebView)findViewById(R.id.display);
    		display.getSettings().setJavaScriptEnabled(true);
    		
    		if(graph_type.equals("bar")){
    			File bar_file = new File(Environment.getExternalStorageDirectory(),"bar_graph.html");
    			display.loadUrl("file:///"+bar_file.toString());    			
    		}
    		else if(graph_type.equals("line")){
    			File line_file = new File(Environment.getExternalStorageDirectory(),"line_graph.html");
    			display.loadUrl("file:///"+line_file.toString());
    		}
    		
    	}
    }
