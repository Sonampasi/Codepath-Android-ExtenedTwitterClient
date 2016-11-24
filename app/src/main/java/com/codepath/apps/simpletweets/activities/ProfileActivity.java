package com.codepath.apps.simpletweets.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.CheckNetwork;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //get user object
        User user = (User) getIntent().getSerializableExtra("user");
        if (CheckNetwork.isAvailable(this)) {
            //populate header
            populateProfileHeader(user);
        } else {
            Toast.makeText(this, "Networks Unavailable!!", Toast.LENGTH_SHORT);
        }
        //get the screen name form the activity that launches it
        String screenName = getIntent().getStringExtra("screen_name");
        if (savedInstanceState == null) {
            //create the user timeline fragment
            UserTimelineFragment frgamentUser = UserTimelineFragment.newInstance(screenName);
            //display user fragment within this activity(dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, frgamentUser);
            ft.commit();
        }
    }

    public void populateProfileHeader(User user) {
        TextView tvFullName = (TextView) findViewById(R.id.tvFullName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvFullName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvFollowers.setText(user.getFollowersCount() + " Followers");
        tvFollowing.setText(user.getFriendsCount() + " Following");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
