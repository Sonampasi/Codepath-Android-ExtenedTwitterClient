package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.CheckNetwork;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.utils.TwitterApplication;
import com.codepath.apps.simpletweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment implements TweetComposeFragment.ComposeTweetDialogListener {
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // load more previous tweets on scroll
                populateTimeline();
                return true;
            }
        };

        //Get the client
        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void populateTimeline() {
        final int previousTweetsLength = tweets.size();
        long max_id = 0;
        long since_id = 1;
        if (previousTweetsLength > 0) {
            max_id = tweets.get(previousTweetsLength - 1).getUid() + 1;
        }
        if (CheckNetwork.isAvailable(getActivity())) {
            client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    Log.d("Debug", jsonArray.toString());
                    addAll(Tweet.fromJsonArray(jsonArray));
                    aTweets.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Debug", errorResponse.toString());
                }
            });
        }
    }

    // bring up the dialogfragment for composing a new tweet
    public void showComposeDialog() {
        FragmentManager fm = getFragmentManager();
        TweetComposeFragment tweetComposeFragment = TweetComposeFragment.newInstance();
        tweetComposeFragment.setTargetFragment(HomeTimelineFragment.this, 300);
        tweetComposeFragment.show(fm, "fragment_compose");
    }

    public void onFinishComposeTweetDialog(String tweetText) {
        if (CheckNetwork.isAvailable(getActivity())) {
            // when the user composes a new tweet and taps the Tweet button, post it
            client.postTweet(tweetText, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    // get the new tweet and add it to the ArrayList
                    Tweet newTweet = Tweet.fromJson(json);
                    tweets.add(0, newTweet);
                    // notify the adapter
                    aTweets.notifyItemInserted(0);
                    // scroll back to display the new tweet
                    scrollToTop();
                    // display a success Toast
                    Toast toast = Toast.makeText(getActivity(), "Tweet posted!", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(0xC055ACEE);
                    TextView textView = (TextView) view.findViewById(android.R.id.message);
                    textView.setTextColor(0xFFFFFFFF);
                    toast.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getActivity(), errorResponse.toString(), Toast.LENGTH_LONG);
                }
            });
        }
    }
    public void scrollToTop() {
        // when you post a tweet or swipe-to-refresh, scroll back to display the new tweet(s)
        linearLayoutManager = (LinearLayoutManager) rvTweets.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }
}
