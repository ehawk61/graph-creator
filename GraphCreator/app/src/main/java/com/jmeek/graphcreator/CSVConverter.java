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

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CSVConverter {
    private InputStream fileInputStream;
    private String tableHeader;
    private String tableHeaderSpilt;
    private Context passedContext;
    static String dbName;
    private Database database = new Database(passedContext, dbName);
    static String [] headerItems;
    static SQLiteDatabase db;

    public CSVConverter(Context passedContext, String dbName) {
        this.passedContext = passedContext;
        this.dbName = dbName;
    }

    public InputStream createFileInputStreamFromData(String fileNameString, Context applicationContext){
        try{
            fileInputStream = new FileInputStream(fileNameString);
        }catch(FileNotFoundException e){
            String stackTrace = Log.getStackTraceString(e);
            Toast.makeText(applicationContext, stackTrace, Toast.LENGTH_LONG).show();
        }//end try/catch statement
        return fileInputStream;
    }//end createFileInputStreamFromData method

    public void convertCursorToDBData(Cursor fileCursor, String dbOperation, TextView statusView, Context passedContext){
        InputStream inputStreamFromConverter;
        if(dbOperation.equals("table")){
            String fileNameString =	fileCursor.getString(fileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            fileCursor.moveToFirst();
            inputStreamFromConverter = createFileInputStreamFromData(fileNameString, passedContext);
            dbTableCreation(inputStreamFromConverter);
            statusView.setText(tableHeaderSpilt);
        }

        else if(dbOperation.equals("rows")){
            String fileNameString =	fileCursor.getString(fileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            fileCursor.moveToFirst();
            inputStreamFromConverter = createFileInputStreamFromData(fileNameString,passedContext);
            dbRowAddition(inputStreamFromConverter);
            statusView.setText("Finished importing to database");
        }
        else{
            Toast.makeText(passedContext,"Invaild database operation",Toast.LENGTH_LONG).show();
        }
    }//end convertCursorToDBData method

    private void dbTableCreation(InputStream fileInputStream){
        try{
            Scanner scanner = new Scanner(new InputStreamReader(fileInputStream));
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
            Log.getStackTraceString(t);
        }//end try/catch statement
    }//end dbTableCreation method


    private void dbRowAddition(InputStream fileInputStream){
        try{
            Scanner scanner = new Scanner(new InputStreamReader(fileInputStream));
            ArrayList<String> HeaderArray = new ArrayList<String>(Arrays.asList(headerItems));
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
            Log.getStackTraceString(t);
        }//end try/catch statement
    }//end dbRowAddition method

}
