package com.ztiany.recyclerview.itemtouch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ztiany.view.R;
import com.ztiany.recyclerview.itemtouch.help.ItemTouchHelperAdapter;
import com.ztiany.recyclerview.itemtouch.help.ItemTouchHelperViewHolder;
import com.ztiany.recyclerview.itemtouch.help.OnStartDragListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = RecyclerAdapter.class.getSimpleName();
    private List<String> mData;
    private LayoutInflater mLayoutInflater;
    private OnStartDragListener mOnStartDragListener;

    private RecyclerAdapter(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
        mData = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            mData.add(String.valueOf(i));
        }
    }

    RecyclerAdapter(LayoutInflater layoutInflater, OnStartDragListener listener) {
        this(layoutInflater);
        mOnStartDragListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_touch_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTextView.setText(String.format("position = %s", mData.get(position)));
        holder.mHandIv.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mOnStartDragListener != null) {
                    mOnStartDragListener.onStartDrag(holder);
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        TextView mTextView;
        ImageView mHandIv;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
            mHandIv = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.GRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

}