package com.imgtec.creator.lumpy.adapters;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

  private final ItemTouchHelperAdapter adapter;
  private Integer from, to;

  public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return false;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                        RecyclerView.ViewHolder target) {
    if (from == null) {
      from = viewHolder.getAdapterPosition();
    }
    to = target.getAdapterPosition();

    adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
    if (from != null && to != null) {
      adapter.onItemDropped(from, to);
    }
    from = null;
    to = null;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    adapter.onItemDismiss(viewHolder.getAdapterPosition());
  }



}
