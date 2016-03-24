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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper
{

    @Override
    public void onCreate(SQLiteDatabase p1)
    {
        // TODO: Implement this method
    }


    public Database(Context context, String dbName){
        super(context,dbName,null,1);
    }

    public void onCreate(SQLiteDatabase db, String tableHeaderSpilt){
        db.execSQL(tableHeaderSpilt);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public void dropTable(SQLiteDatabase db, String dbName){
        db.execSQL("DROP TABLE "+dbName+"IF EXISTS");
    }

}//end Database class
