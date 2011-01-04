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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 *
 * @author aschulze
 */
public class ConfigActivity extends Activity {

    private Button lBtnCancel;
    private Button lBtnSave;
    private EditText lTxfURL;
    private Activity lInstance;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.config_hvga_p);

        lBtnCancel = (Button) findViewById(R.id.cfgBtnCancel);
        lBtnSave = (Button) findViewById(R.id.cfgBtnSave);
        lTxfURL = (EditText) findViewById(R.id.cfgTxfURL);
        lInstance = this;

        lBtnCancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "DISCARDING...",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        lBtnSave.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "SAVING...",
                        Toast.LENGTH_SHORT).show();

                JWC.setURL(lTxfURL.getText().toString());
                JWC.saveSettings(lInstance);
                finish();
            }
        });
        JWC.loadSettings(this);
        lTxfURL.setText(JWC.getURL());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
