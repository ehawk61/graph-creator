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


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class fileSpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private final Spinner fileSpinner;
    private Context appContext;

    public fileSpinnerActivity(final Spinner spinner, Context passedContext){
        fileSpinner = spinner;
        appContext = passedContext;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast toast = Toast.makeText(appContext, fileSpinner.getSelectedItem().toString()+".class",Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
