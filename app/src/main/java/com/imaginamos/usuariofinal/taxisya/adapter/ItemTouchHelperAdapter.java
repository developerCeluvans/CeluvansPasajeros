package com.imaginamos.usuariofinal.taxisya.adapter;

/**
 * Created by leo on 9/8/15.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
