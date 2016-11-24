package com.codepath.apps.simpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.activities.ProfileActivity;
import com.codepath.apps.simpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvUserName, tvBody, tvCreatedAt, tvScreenName;
        public ImageView ivProfileImage;

        public ViewHolder(final View itemView) {

            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedat);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            // Attach a click listener to the entire row view
            ivProfileImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Tweet tweet = mTweets.get(position);
                // We can access the data within the views
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user",tweet.getUser());
                i.putExtra("screen_name",tweet.getUser().getScreenName());
                mContext.startActivity(i);
            }
        }
    }

    // Store a member variable for the tweets
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;

    // Pass in the tweet array into the constructor
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Set item views based on your views and data model
        TextView userName = holder.tvUserName;
        userName.setText(tweet.getUser().getName() + " ");
        TextView body = holder.tvBody;
        body.setText(tweet.getBody());
        TextView create_at = holder.tvCreatedAt;
        create_at.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        TextView screen_name = holder.tvScreenName;
        screen_name.setText("@" + tweet.getUser().getScreenName());
        ImageView profileImage = holder.ivProfileImage;
        String profileImageUrl = tweet.getUser().getProfileImageUrl();
        if (!TextUtils.isEmpty(profileImageUrl)) {
            Picasso.with(getContext()).load(profileImageUrl).into(profileImage);
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        relativeDate = relativeDate.replaceAll(" ago", "");
        relativeDate = relativeDate.replaceAll("0 seconds", "now");
        relativeDate = relativeDate.replaceAll(" seconds", "s");
        relativeDate = relativeDate.replaceAll(" second", "s");
        relativeDate = relativeDate.replaceAll(" minutes", "m");
        relativeDate = relativeDate.replaceAll(" minute", "m");
        relativeDate = relativeDate.replaceAll(" hours", "h");
        relativeDate = relativeDate.replaceAll(" hour", "h");
        relativeDate = relativeDate.replaceAll(" days", "d");
        relativeDate = relativeDate.replaceAll(" day", "d");

        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
