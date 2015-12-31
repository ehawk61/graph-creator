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

package com.jmeek.graphcreator;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WebViewActivity extends Activity{
    Cursor dbcursor;
    String sqlStatement="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datagraph);
        Spinner Xspinner = (Spinner) findViewById(R.id.xheaderChoice);
        Spinner Yspinner = (Spinner)findViewById(R.id.yheaderChoice);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,CSVConverter.headerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Xspinner.setAdapter(adapter);
        Yspinner.setAdapter(adapter);
    }

    public void sqlStringDisplay (View v){
        TextView text = (TextView)findViewById(R.id.sqlStringDisplay);
        Spinner Xspinner = (Spinner)findViewById(R.id.xheaderChoice);
        Spinner Yspinner = (Spinner)findViewById(R.id.yheaderChoice);

        sqlStatement = " SELECT "+Xspinner.getSelectedItem().toString()+","+ Yspinner.getSelectedItem().toString() + " FROM "+ CSVConverter.dbName;
        Log.d("db_statement", sqlStatement);
        text.setText(sqlStatement);
    }//end sqlStringDisplay method

    public void dbQuery(View v){

        TextView text = (TextView)findViewById(R.id.dbQueryStatus);
        try{
            dbcursor = CSVConverter.db.rawQuery(sqlStatement, null);
            Log.d("row count", Integer.toString(dbcursor.getCount()));
            Log.d("column count",Integer.toString(dbcursor.getColumnCount()) );
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Query failed", Toast.LENGTH_LONG).show();
        }
        dbcursor.close();
        text.setText("Query Completed");

    }//end dbQuery method

    public void generateFile(View v){
        RadioGroup g = (RadioGroup)findViewById(R.id.graphType);
        int selected = g.getCheckedRadioButtonId();
        String graph_type = findViewById(selected).getTag().toString();
        dbcursor = CSVConverter.db.rawQuery(sqlStatement, null);
        TextView text = (TextView)findViewById(R.id.dbQueryStatus);

        try{
            File root = Environment.getExternalStorageDirectory();

            if(root.canWrite()){
                if(graph_type.equals("bar")){
                    File barGraphFile = new File(root, "bar_graph.html");
                    FileWriter barGraphFileWriter = new FileWriter(barGraphFile);
                    BufferedWriter barGraphBufferedFileWriter = new BufferedWriter(barGraphFileWriter);

                    barGraphBufferedFileWriter.write("<!DOCTYPE html>\n");
                    barGraphBufferedFileWriter.write("\t<html>\n");

                    addHtmlHeadStarter(barGraphBufferedFileWriter);
                    addFlotJsScriptStarter(barGraphBufferedFileWriter);
                    addFlotArraytoJsScript(barGraphBufferedFileWriter);
                    addFlotOptionstoJsScript(barGraphBufferedFileWriter,graph_type);
                    addFlotJsScriptEnder(barGraphBufferedFileWriter);

                    barGraphBufferedFileWriter.write("\t\t<body>\n\t\t\t<h1>Generated Bar Graph</h1>\n");
                    barGraphBufferedFileWriter.write("\t\t\t<div id=\"placeholder\" style=\"width:600px;height:300px;\"></div>\n");
                    barGraphBufferedFileWriter.write("\t\t\t<p>Testing generation</p>");
                    barGraphBufferedFileWriter.write("\n\t\t</body>\n</html>");

                    barGraphBufferedFileWriter.close();
                }//end if statement

                else if(graph_type.equals("line")){
                    File gpxfile = new File(root, "line_graph.html");
                    FileWriter gpxwriter = new FileWriter(gpxfile);
                    BufferedWriter out = new BufferedWriter(gpxwriter);

                    out.write("<!DOCTYPE html>\n");
                    out.write("\t<html>\n");

                    addHtmlHeadStarter(out);
                    addFlotJsScriptStarter(out);
                    addFlotArraytoJsScript(out);
                    addFlotOptionstoJsScript(out,graph_type);
                    addFlotJsScriptEnder(out);

                    out.write("\t\t<body>\n\t\t\t<h1>Generated Line Graph</h1>\n");
                    out.write("\t\t\t<div id=\"placeholder\" style=\"width:600px;height:300px;\"></div>\n");
                    out.write("\t\t\t<p>Testing generation</p>");
                    out.write("\n\t\t</body>\n</html>");

                    out.close();
                }//end else if statement

            }//try statement
            text.setText("HTML Graph Generated");
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), "Unable to write to file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }//end catch statement

    }//end generateFile method

    public void loadScreen(View v){
        RadioGroup g = (RadioGroup)findViewById(R.id.graphType);
        int selected = g.getCheckedRadioButtonId();
        String graph_type = findViewById(selected).getTag().toString();
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

    private void addHtmlHeadStarter(BufferedWriter out)throws IOException{
        out.write("\t\t<head>\n");
        out.write("\t\t\t<meta charset=\"utf-8\" />\n");
        out.write("\t\t\t<title>GraphCreator Results</title>\n");
        out.write("\t\t\t<script src=\"file:///android_asset/js/jquery.min.js\"></script>\n");
        out.write("\t\t\t<script src=\"file:///android_asset/js/jquery.flot.min.js\"></script>\n");

    }

    private void addFlotJsScriptStarter(BufferedWriter out)throws IOException{
        out.write("\t\t\t<script type=\"text/javascript\">\n");
        out.write("\t\t\t$(function () {\n");
        out.write("\t\t\t\tvar d1 =[");
        convertDataToJSArray(out);
        out.write("];\n");
    }

    private void addFlotArraytoJsScript(BufferedWriter out)throws IOException{

        out.write("\t\t\t\tvar data = [\n");
        out.write("\t\t\t\t\t{\n");
        out.write("\t\t\t\t\t\tdata: d1\n");
        out.write("\t\t\t\t\t\t\n");
        out.write("\t\t\t\t\t}\n");
        out.write("\t\t\t\t];\n");
    }

    private void addFlotOptionstoJsScript(BufferedWriter out, String graphType) throws IOException{
        if(graphType.equals("bar")){
            out.write("\t\t\t\tvar options =\n");
            out.write("\t\t\t\t\t{\n");
            out.write("\t\t\t\t\t\tbars:\n");
            out.write("\t\t\t\t\t\t\t{\n");
            out.write("\t\t\t\t\t\t\t\tshow:true,\n");
            out.write("\t\t\t\t\t\t\t\talign: 'center',\n");
            out.write("\t\t\t\t\t\t\t\tbarWidth: 0.3\n");
            out.write("\t\t\t\t\t\t\t}\n");
            out.write("\t\t\t\t\t};\n");
        }
        else if(graphType.equals("line")){
            out.write("\t\t\t\tvar options =\n");
            out.write("\t\t\t\t\t{\n");
            out.write("\t\t\t\t\t\tlines:\n");
            out.write("\t\t\t\t\t\t\t{\n");
            out.write("\t\t\t\t\t\t\t\tshow:true,\n");
            out.write("\t\t\t\t\t\t\t\tlineWidth: 0.3\n");
            out.write("\t\t\t\t\t\t\t}\n");
            out.write("\t\t\t\t\t};\n");
        }

    }

    private void addFlotJsScriptEnder(BufferedWriter out) throws IOException{
        out.write("\t\t\t\t$.plot($(\"#placeholder\"), data, options);\n");
        out.write("\t\t});\n");
        out.write("\t\t\t</script>\n");
        out.write("\t\t</head>\n");
    }

    private void convertDataToJSArray(BufferedWriter out)throws IOException{
        String graphData ="";
        dbcursor.moveToFirst();
        int xAxisPlaceHolder = 0;

        while(!dbcursor.isAfterLast()){
            graphData +="["+xAxisPlaceHolder+","+dbcursor.getString(1)+"],";
            dbcursor.moveToNext();
            xAxisPlaceHolder++;
        }//end while statement

        graphData = graphData.substring(0,graphData.length()-1);

        out.write(graphData);
        Log.d("dataset", graphData);
        dbcursor.close();
    }

}

