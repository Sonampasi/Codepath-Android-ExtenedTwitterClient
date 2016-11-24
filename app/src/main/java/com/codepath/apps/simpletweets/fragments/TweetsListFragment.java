package com.codepath.apps.simpletweets.fragments;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.CheckNetwork;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.utils.ItemClickSupport;
import com.codepath.apps.simpletweets.utils.TwitterApplication;
import com.codepath.apps.simpletweets.utils.TwitterClient;

import java.util.ArrayList;
import java.util.List;


public class TweetsListFragment extends Fragment {

    RecyclerView rvTweets;
    ArrayList<Tweet> tweets;
    TweetsArrayAdapter aTweets;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    TwitterClient client;

    //inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        //find recycler view
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        //connect adapter to recycler view
        rvTweets.setAdapter(aTweets);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(scrollListener);

        // hook up listener for tweet tap to view tweet detail
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (CheckNetwork.isAvailable(getActivity())) {
                    showTweetDetailDialog(position);
                }
            }
        });

        return v;
    }

    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create the arraylist (data source)
        tweets = new ArrayList<>();
        //construct the adapter from data source
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        client = TwitterApplication.getRestClient();
    }

    public void addAll(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
    }

    // bring up the dialogfragment for showing a detailed view of a tweet
    private void showTweetDetailDialog(int position) {
        // pass in the user's profile image and the tweet
        FragmentManager fm = getFragmentManager();
        TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(tweets.get(position));
        tweetDetailFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        tweetDetailFragment.show(fm, "fragment_tweet_detail");
    }

}
