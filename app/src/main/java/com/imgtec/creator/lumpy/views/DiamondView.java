/*
 * <b>Copyright (c) 2016, Imagination Technologies Limited and/or its affiliated group companies
 *  and/or licensors. </b>
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are permitted
 *  provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *      endorse or promote products derived from this software without specific prior written
 *      permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.imgtec.creator.lumpy.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.imgtec.creator.lumpy.R;


public class DiamondView extends RelativeLayout {

  private Paint paint = new Paint();
  private int colorLight;
  private int colorDark;

  public DiamondView(Context context) {
    super(context);
    init(context);
  }

  public DiamondView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public DiamondView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context) {
    init(context, null);
  }

  private void init(Context context, AttributeSet attrs) {
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(ContextCompat.getColor(context, R.color.colorAccent));


    //paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.label_border_width));
    setWillNotDraw(false);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs,
          R.styleable.DiamondView, 0, 0);
      colorLight = ContextCompat.getColor(context, a.getResourceId(R.styleable.DiamondView_diamondColorLight, R.color.colorAccent));
      colorDark = ContextCompat.getColor(context, a.getResourceId(R.styleable.DiamondView_diamondColorDark, R.color.colorAccent));
      a.recycle();
    }

  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paint.setShader(new LinearGradient(0, 0, canvas.getWidth(), canvas.getHeight(),colorLight, colorDark, Shader.TileMode.CLAMP));
    Path wallpath = new Path();
    wallpath.reset(); // only needed when reusing this path for a new build
    wallpath.moveTo(0, 0); // used for first point
    wallpath.lineTo(canvas.getWidth(), 0);
    wallpath.lineTo(canvas.getWidth(), canvas.getHeight());
    wallpath.lineTo(30, canvas.getHeight());
    wallpath.close();

    canvas.drawPath(wallpath, paint);
  }
}
