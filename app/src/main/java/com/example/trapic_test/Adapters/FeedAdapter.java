package com.example.trapic_test.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trapic_test.Model.Event;
import com.example.trapic_test.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedImageAdapter> {

    private Context ctx;
    private List<Event> feed;

    public FeedAdapter(Context ctx, List<Event> feed){

        this.ctx = ctx;
        this.feed = feed;

    }
    @NonNull
    @Override
    public FeedImageAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.feed_item, parent, false);

        return new FeedImageAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedImageAdapter holder, int position) {
        Event event = feed.get(position);
        Picasso.with(ctx).load(event.getEvent_image()).fit().centerCrop().into(holder.post_img);
    }

    @Override
    public int getItemCount() {
        return feed.size();
    }

    public class FeedImageAdapter extends RecyclerView.ViewHolder{
        TextView user_name;
        ImageView post_img;
        public FeedImageAdapter(View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.posted_by);
            post_img = itemView.findViewById(R.id.post_img);
        }
    }
}
