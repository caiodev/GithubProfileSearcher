package githubprofilesearcher.caiodev.com.br.githubprofilesearcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String BASE_URL_ADDRESS = "https://api.github.com/users/";
    private EditText mSearchProfile;
    private Button mSearchButton;
    private CardView mUserCard;
    private CardView mUserCardRepo1;
    private CardView mUserCardRepo2;
    private CardView mUserCardRepo3;
    private TextView mUserName;
    private TextView mUserBio;
    private TextView mUserFollowers;
    private TextView mUserRepos;
    private ImageView mUserAvatar;

    private TextView mFirstRepoName;
    private TextView mFirstRepoUrl;
    private TextView mFirstRepoWatchers;
    private TextView mFirstRepoIssues;

    private TextView mSecondRepoName;
    private TextView mSecondRepoUrl;
    private TextView mSecondRepoWatchers;
    private TextView mSecondRepoIssues;

    private TextView mThirdRepoName;
    private TextView mThirdRepoUrl;
    private TextView mThirdRepoWatchers;
    private TextView mThirdRepoIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchProfile = findViewById(R.id.search_profile);

        mSearchButton = findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillProfileInfo(mSearchProfile.getText().toString());
            }
        });
    }

    void fillProfileInfo(String profile) {
        String requestUrl = BASE_URL_ADDRESS.concat(profile);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("getProfileInfo", "FAIL");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string();
                Log.i("getProfileInfo", jsonData);

                if (response.isSuccessful()) {
                    mUserCard = findViewById(R.id.card_user_info);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject rootObj = new JSONObject(jsonData);

                                mUserName = findViewById(R.id.user_name);
                                mUserBio = findViewById(R.id.user_bio);
                                mUserFollowers = findViewById(R.id.user_folowers);
                                mUserRepos = findViewById(R.id.user_repos);
                                mUserAvatar = findViewById((R.id.user_avatar));

                                mUserName.setText(rootObj.getString(getString(R.string.login_alias)));

                                if (rootObj.getString(getString(R.string.bio_alias)).equals(getString(R.string.null_string))) {
                                    mUserBio.setText(getString(R.string.null_bio_message));

                                } else {
                                    mUserBio.setText(rootObj.getString(getString(R.string.bio_alias)));
                                }

                                if (rootObj.getInt(getString(R.string.followers_alias)) <= 1) {
                                    mUserFollowers.setText(getString(R.string.number_of_followers_passing_attributes,
                                            rootObj.getInt(getString(R.string.followers_alias)),
                                            getString(R.string.number_of_followers_singular)));

                                } else {
                                    mUserFollowers.setText(getString(R.string.number_of_followers_passing_attributes,
                                            rootObj.getInt(getString(R.string.followers_alias)),
                                            getString(R.string.number_of_followers_plural)));
                                }

                                if (rootObj.getInt(getString(R.string.public_repos_alias)) <= 1) {
                                    mUserRepos.setText(getString(R.string.number_of_repos_passing_attributes,
                                            rootObj.getInt(getString(R.string.public_repos_alias)),
                                            getString(R.string.number_of_repos_singular)));

                                } else {
                                    mUserRepos.setText(getString(R.string.number_of_repos_passing_attributes,
                                            rootObj.getInt(getString(R.string.public_repos_alias)),
                                            getString(R.string.number_of_repos_plural)));
                                }

                                try {
                                    Glide.with(MainActivity.this)
                                            .load(rootObj.getString(getString(R.string.avatar_image_url)))
                                            .asBitmap()
                                            .fitCenter()
                                            .into(mUserAvatar);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mUserCard.setVisibility(View.VISIBLE);
                                fillRepoInfo(rootObj.getString(getString(R.string.login_alias)));
                            } catch (JSONException e) {
                                mUserCard.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    void fillRepoInfo(String userName) {

        String requestUrl = BASE_URL_ADDRESS.concat(userName);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("getProfileInfo", "FAIL");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string();
                Log.i("getProfileInfo", jsonData);

                if (response.isSuccessful()) {

                    mUserCardRepo1 = findViewById(R.id.card_repo1);
                    mUserCardRepo2 = findViewById(R.id.card_repo2);
                    mUserCardRepo3 = findViewById(R.id.card_repo3);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject rootObj = new JSONObject(jsonData);

                                mFirstRepoName = findViewById(R.id.repo1_name);

                                mFirstRepoUrl = findViewById(R.id.repo1_url);
                                mFirstRepoUrl.setText(rootObj.getString(getString(R.string.repos_url_alias)));

                                mFirstRepoWatchers = findViewById(R.id.repo1_watchers);

                                mFirstRepoIssues = findViewById(R.id.repo1_issues);


                                mSecondRepoName = findViewById(R.id.repo2_name);

                                mSecondRepoUrl = findViewById(R.id.repo2_url);

                                mSecondRepoWatchers = findViewById(R.id.repo2_watchers);

                                mSecondRepoIssues = findViewById(R.id.repo2_issues);


                                mThirdRepoName = findViewById(R.id.repo3_name);

                                mThirdRepoUrl = findViewById(R.id.repo3_url);

                                mThirdRepoWatchers = findViewById(R.id.repo3_watchers);

                                mThirdRepoIssues = findViewById(R.id.repo3_issues);

                                mUserName = findViewById(R.id.user_name);
                                mUserBio = findViewById(R.id.user_bio);
                                mUserFollowers = findViewById(R.id.user_folowers);
                                mUserRepos = findViewById(R.id.user_repos);
                                mUserAvatar = findViewById((R.id.user_avatar));

                                mUserName.setText(rootObj.getString(getString(R.string.login_alias)));

                                if (rootObj.getString(getString(R.string.bio_alias)).equals(getString(R.string.null_string))) {
                                    mUserBio.setText(getString(R.string.null_bio_message));

                                } else {
                                    mUserBio.setText(rootObj.getString(getString(R.string.bio_alias)));
                                }

                                if (rootObj.getInt(getString(R.string.followers_alias)) <= 1) {
                                    mUserFollowers.setText(getString(R.string.number_of_followers_passing_attributes,
                                            rootObj.getInt(getString(R.string.followers_alias)),
                                            getString(R.string.number_of_followers_singular)));

                                } else {
                                    mUserFollowers.setText(getString(R.string.number_of_followers_passing_attributes,
                                            rootObj.getInt(getString(R.string.followers_alias)),
                                            getString(R.string.number_of_followers_plural)));
                                }

                                if (rootObj.getInt(getString(R.string.public_repos_alias)) <= 1) {
                                    mUserRepos.setText(getString(R.string.number_of_repos_passing_attributes,
                                            rootObj.getInt(getString(R.string.public_repos_alias)),
                                            getString(R.string.number_of_repos_singular)));

                                } else {
                                    mUserRepos.setText(getString(R.string.number_of_repos_passing_attributes,
                                            rootObj.getInt(getString(R.string.public_repos_alias)),
                                            getString(R.string.number_of_repos_plural)));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}