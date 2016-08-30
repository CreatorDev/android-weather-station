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

package com.imgtec.creator.lumpy.adapters;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Interface to listen for a move and drop event from a {@link ItemTouchHelper.Callback}.
 *
 */
public interface ItemTouchHelperAdapter {

  /**
   * Called when an item has been dragged far enough to trigger a move. This is called every time
   * an item is shifted, and <strong>not</strong> at the end of a "drop" event.<br/>
   * <br/>
   * Implementations should call {@link RecyclerView.Adapter#notifyItemMoved(int, int)} after
   * adjusting the underlying data to reflect this move.
   *
   * @param fromPosition start position of the moved item.
   * @param toPosition   resolved position of the moved item.
   * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
   * @see RecyclerView.ViewHolder#getAdapterPosition()
   */
  void onItemMove(int fromPosition, int toPosition);


  /**
   * Called when an item has finished it's move and got dropped.
   * @param formPosition start position of the moved item
   * @param toPosition drop position of the moved item
   * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
   * @see RecyclerView.ViewHolder#getAdapterPosition()
   */
  void onItemDropped(int formPosition, int toPosition);
}
