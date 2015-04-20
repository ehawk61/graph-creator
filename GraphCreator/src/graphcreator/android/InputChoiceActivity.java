package graphcreator.android;

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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InputChoiceActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputchoice);
    }

    public void startUrlActivity(View v){
        Intent intent = new Intent(this, GraphCreatorActivity.class);
        startActivity(intent);
    }
}
