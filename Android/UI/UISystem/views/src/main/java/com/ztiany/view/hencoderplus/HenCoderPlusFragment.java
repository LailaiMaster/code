package com.ztiany.view.hencoderplus;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ztiany.view.R;
import com.ztiany.view.draw.canvas.DashView;
import com.ztiany.view.hencoderplus.camera.AnimationCameraView;
import com.ztiany.view.hencoderplus.camera.CameraView;
import com.ztiany.view.hencoderplus.drawable.DrawableView;
import com.ztiany.view.hencoderplus.text.ImageTextView;
import com.ztiany.view.hencoderplus.text.SportView;
import com.ztiany.view.hencoderplus.touch.MultiTouchView1;
import com.ztiany.view.hencoderplus.touch.MultiTouchView2;
import com.ztiany.view.hencoderplus.touch.MultiTouchView3;
import com.ztiany.view.hencoderplus.touch.ScalableImageView;
import com.ztiany.view.hencoderplus.views.AvatarView;
import com.ztiany.view.hencoderplus.views.AvatarView2;
import com.ztiany.view.hencoderplus.views.PieChart;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2019-10-02 15:08
 */
public class HenCoderPlusFragment extends Fragment {

    private List<Pair<String, ? extends View>> items = new ArrayList<>();
    private FrameLayout mFrameLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        initViews();
    }

    private void initViews() {
        items.add(new Pair<>("CameraView", new CameraView(requireContext())));
        items.add(new Pair<>("AnimationCameraView", new AnimationCameraView(requireContext())));

        items.add(new Pair<>("ImageTextView", new ImageTextView(requireContext())));
        items.add(new Pair<>("SportView", new SportView(requireContext())));

        items.add(new Pair<>("AvatarView", new AvatarView(requireContext())));
        items.add(new Pair<>("AvatarView2", new AvatarView2(requireContext())));
        items.add(new Pair<>("DashView", new DashView(requireContext())));
        items.add(new Pair<>("PieChart", new PieChart(requireContext(), null)));

        items.add(new Pair<>("PieChart", new DrawableView(requireContext())));

        items.add(new Pair<>("MaterialEditText", View.inflate(requireContext(), R.layout.hencoder_layout_material_edittext, null)));

        items.add(new Pair<>("ScalableImageView", new ScalableImageView(getContext(), null)));
        items.add(new Pair<>("MultiTouchView1", new MultiTouchView1(getContext(), null)));
        items.add(new Pair<>("MultiTouchView2", new MultiTouchView2(getContext(), null)));
        items.add(new Pair<>("MultiTouchView3", new MultiTouchView3(getContext(), null)));
        items.add(new Pair<>("TwoPager", View.inflate(requireContext(), R.layout.hencoder_layout_two_pager, null)));

        items.add(new Pair<>("drag_helper_grid_view", View.inflate(requireContext(), R.layout.hencoder_layout_drag_helper_grid_view, null)));
        items.add(new Pair<>("listener_grid_view", View.inflate(requireContext(), R.layout.hencoder_drag_listener_grid_view, null)));
        items.add(new Pair<>("drag_to_collect", View.inflate(requireContext(), R.layout.hencoder_drag_to_collect, null)));
        items.add(new Pair<>("drag_up_down", View.inflate(requireContext(), R.layout.hencoder_drag_up_down, null)));

        items.add(new Pair<>("nested_scroll", View.inflate(requireContext(), R.layout.hencoder_nested_scroll, null)));
        items.add(new Pair<>("nested_scroll_view", View.inflate(requireContext(), R.layout.hencoder_layout_nested_scroll_view, null)));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFrameLayout = new FrameLayout(requireContext());
        return mFrameLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        for (final Pair<String, ? extends View> pair : items) {
            menu.add(pair.first).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    mFrameLayout.removeAllViews();
                    int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
                    mFrameLayout.addView(pair.second, new FrameLayout.LayoutParams(matchParent, matchParent));
                    return true;
                }
            });
        }

    }

}