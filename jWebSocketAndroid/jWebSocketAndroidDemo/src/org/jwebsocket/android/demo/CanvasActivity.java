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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 *
 * @author aschulze
 */
public class CanvasActivity extends Activity {

    LinearLayout mLinearLayout;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Create a LinearLayout in which to add the ImageView
        mLinearLayout = new LinearLayout(this);

        // get the display metric (width and height)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int width = metrics.widthPixels;
        final int height = metrics.heightPixels;

        final Bitmap lBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas lCanvas = new Canvas(lBmp);
        final ImageView lImgView = new ImageView(this);

        lImgView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lImgView.setImageBitmap(lBmp);
        lImgView.setScaleType(ImageView.ScaleType.CENTER);
        lImgView.setPadding(0, 0, 0, 0);

        final Paint bck = new Paint();
        bck.setARGB(0xff, 0x80, 0x80, 0x80);
        lCanvas.drawRect(0, 0, width, height, bck);

        final Paint p = new Paint();
        p.setARGB(0xff, 0xff, 0x00, 0xff);
  
        mLinearLayout.addView(lImgView);
        setContentView(mLinearLayout);

        lImgView.setOnTouchListener(new OnTouchListener() {

            // start and end coordinates for a single line
            float sx, sy, ex, ey;

            public boolean onTouch(View aView, MotionEvent aME) {

                Rect lRect = new Rect();
                Window lWindow = getWindow();
                lWindow.getDecorView().getWindowVisibleDisplayFrame(lRect);
                int lStatusBarHeight = lRect.top;
                int lContentViewTop =
                        lWindow.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                final int lTitleBarHeight = lContentViewTop - lStatusBarHeight;

                int lAction = aME.getAction();

                float lX = aME.getX();
                float lY = aME.getY();

                switch (lAction) {
                    case MotionEvent.ACTION_DOWN:
                        ex = lX;
                        ey = lY + lTitleBarHeight;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sx = ex;
                        sy = ey;
                        ex = lX;
                        ey = lY + lTitleBarHeight;
                        lCanvas.drawLine(sx, sy, ex, ey, p);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                lImgView.invalidate();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu aMenu) {
        MenuInflater lMenInfl = getMenuInflater();
        lMenInfl.inflate(R.menu.canvas_menu, aMenu);
        return true;
    }
}
