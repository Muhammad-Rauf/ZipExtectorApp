package com.example.zipextectorapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.zipextectorapp.Models.SliderItems;
import com.example.zipextectorapp.R;
import com.example.zipextractor.model.MainEntity;
import java.util.ArrayList;


public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>{

    private final ArrayList<MainEntity> list;
    private final ViewPager2 viewPager2;
    Context context;

    public ImageSliderAdapter(ArrayList<MainEntity> list, ViewPager2 viewPager2, Context context) {
        this.list = list;
        this.viewPager2 = viewPager2;
        this.context = context;
    }



    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_pager, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        MainEntity item = list.get(position);
      //  Glide.with(holder.itemView.getContext()).load(item.getFilePath()).transform(new RoundedCorners(20)).into(holder.sliderImage);
       // MainEntity item = list.get(position);
       Glide.with(holder.itemView.getContext()).load(item.getFilePath()).into(holder.sliderImage);


    }

    @Override
    public int getItemCount() {
        return list.size();

    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView sliderImage;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderImage = itemView.findViewById(R.id.allimageViewID);
        }
    }
}
