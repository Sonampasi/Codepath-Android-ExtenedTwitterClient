package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                return true;
            }
        };

        //Get the client
        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void populateTimeline() {
        if (CheckNetwork.isAvailable(getActivity())) {
            client.getMentionsTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    Log.d("Debug", jsonArray.toString());
                    addAll(Tweet.fromJsonArray(jsonArray));
                    aTweets.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Debug", errorResponse.toString());
                    Toast.makeText(getActivity(), errorResponse.toString(), Toast.LENGTH_LONG);
                }
            });
        }
    }
}
