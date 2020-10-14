package com.game.fastandsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflator;
    ArrayList<StageActivity.StageSlide> mSlides;

    public SliderAdapter(Context context, ArrayList<StageActivity.StageSlide> slidesList){
        mContext = context;
        mSlides = slidesList;
        mLayoutInflator =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSlides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View slide = mLayoutInflator.inflate(R.layout.single_stage,null);

        ImageView image = slide.findViewById(R.id.single_stage_image);
        TextView name = slide.findViewById(R.id.single_stage_name);
        TextView desc = slide.findViewById(R.id.description_view);

        image.setImageResource(mSlides.get(position).getImageId());
        name.setText(mSlides.get(position).getName());
        desc.setText(mSlides.get(position).getmDescription());

        container.addView(slide);

        return slide;
    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((View)object));
    }
}
