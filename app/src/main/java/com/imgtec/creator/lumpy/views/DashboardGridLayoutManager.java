package com.imgtec.creator.lumpy.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

public class DashboardGridLayoutManager extends GridLayoutManager {

  public DashboardGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public DashboardGridLayoutManager(Context context, int spanCount) {
    super(context, spanCount);
  }

  public DashboardGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
    super(context, spanCount, orientation, reverseLayout);
  }

  @Override
  public boolean supportsPredictiveItemAnimations() {
    return true;
  }
}
