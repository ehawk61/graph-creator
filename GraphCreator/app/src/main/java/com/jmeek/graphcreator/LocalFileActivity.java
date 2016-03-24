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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

public class LocalFileActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localurllayout);
	
	}
	
	public void fileButtonDialog(View p1){
		Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_LONG).show();
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		startActivityForResult(intent,1);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(getApplicationContext(),"Got it!", Toast.LENGTH_LONG).show();
	}



}
