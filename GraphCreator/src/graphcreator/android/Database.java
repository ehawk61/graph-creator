package graphcreator.android;

import android.content.*;
import android.database.sqlite.*;

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
