// ---------------------------------------------------------------------------
// jWebSocket - Copyright (c) 2010 Innotrade GmbH
// ---------------------------------------------------------------------------
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by the
// Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
// for more details.
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
// ---------------------------------------------------------------------------
package org.jwebsocket.android.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *
 * @author aschulze
 */
public class MainActivity extends ListActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        JWC.init();

        String[] lItems = {"Fundamentals", "Canvas Demo", "Camera Demo", "Setup"};

        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, lItems));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);


        lv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, Fundamentals.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, CanvasActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, CameraActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                        break;
                }
                //Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                //       Toast.LENGTH_SHORT).show();
            }
        });
    }
}
