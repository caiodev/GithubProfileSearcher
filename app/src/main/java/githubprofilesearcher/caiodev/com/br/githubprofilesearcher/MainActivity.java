package githubprofilesearcher.caiodev.com.br.githubprofilesearcher;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL_ADDRESS = "https://api.github.com/users/";

    @BindView(R.id.search_profile) protected EditText mSearchProfile;
    @BindView(R.id.search_button) protected Button mSearchButton;

    @BindView(R.id.card_user_info) protected CardView mUserCard;
    @BindView(R.id.user_name) protected TextView mUserName;
    @BindView(R.id.user_bio) protected TextView mUserBio;
    @BindView(R.id.user_folowers) protected TextView mUserFollowers;
    @BindView(R.id.user_repos) protected TextView mUserRepos;
    @BindView(R.id.user_avatar) protected ImageView mUserAvatar;

    @BindView(R.id.repo1_name) protected TextView mFirstRepoName;
    @BindView(R.id.repo1_url) protected TextView mFirstRepoUrl;
    @BindView(R.id.repo1_watchers) protected TextView mFirstRepoWatchers;
    @BindView(R.id.repo1_issues) protected TextView mFirstRepoIssues;

    @BindView(R.id.repo2_name) protected TextView mSecondRepoName;
    @BindView(R.id.repo2_url) protected TextView mSecondRepoUrl;
    @BindView(R.id.repo2_watchers) protected TextView mSecondRepoWatchers;
    @BindView(R.id.repo2_issues) protected TextView mSecondRepoIssues;

    @BindView(R.id.repo3_name) protected TextView mThirdRepoName;
    @BindView(R.id.repo3_url) protected TextView mThirdRepoUrl;
    @BindView(R.id.repo3_watchers) protected TextView mThirdRepoWatchers;
    @BindView(R.id.repo3_issues) protected TextView mThirdRepoIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    //This method gets the text inserted into the mSearchProfile EditText, besides,
    //it checks first, if there is internet connection and second, if the EditText is empty, if these two
    //conditions are satisfied, the EditText's content will be obtained
    @OnClick(R.id.search_button)
    void run() {
        if (isNetworkAvailable()) {
            if (!isFieldEmpty(mSearchProfile)) {
                fillProfileInfo(mSearchProfile.getText().toString());
            }
        } else {
            toastMaker(getString(R.string.no_connection_error));
        }
    }

    //This method fills the requested github profile info
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

    //This method fills the requested github Repo information as Repo name, repo URL and so on
    void fillRepoInfo(String userName) {

        String requestUrl = BASE_URL_ADDRESS.concat(userName).concat("/repos");

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject rootObj = new JSONObject(jsonData);

                                mFirstRepoUrl.setText(rootObj.getString("name"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    //This method checks if there is an empty field based on the EditText object received
    boolean isFieldEmpty(EditText textField) {

        if (textField.getText().toString().equals("")) {
            textField.setError(getString(R.string.empty_field_error));
            return true;
        } else {
            return false;
        }
    }

    //It checks whether internet connection is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //It pops up a message received as parameter
    void toastMaker(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}