package com.imgtec.creator.lumpy.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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
