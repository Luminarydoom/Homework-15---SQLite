package nyc.c4q.marvelcomicsdb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import nyc.c4q.marvelcomicsdb.loginDatabase.User;
import nyc.c4q.marvelcomicsdb.loginDatabase.UserDatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String SHARED_PREFS_KEY = "sharedPrefsTesting";
    private EditText email, password;
    private ImageView background;
    private Button signIn, signUp;
    private CheckBox checkBox;
    private SharedPreferences login;
    private UserDatabaseHelper userDatabaseHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        background = findViewById(R.id.login_bgd);
        int imagesToShow[] = {
                R.drawable.bgd_storm,
                R.drawable.bgd_thor,
                R.drawable.bgd_captian_america,
                R.drawable.bgd_dare_devil,
                R.drawable.bgd_hulk,
                R.drawable.bgd_iron_man,
                R.drawable.bgd_spiderman};

        checkBox = findViewById(R.id.checkbox_remember_me);

        email = findViewById(R.id.edit_text_login_email);
        password = findViewById(R.id.edit_text_login_password);

        login = getApplicationContext().getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        if (login.getBoolean("isChecked", false)) {
            email.setText(login.getString("email", null));
            password.setText(login.getString("password", null));
            checkBox.setChecked(login.getBoolean("isChecked", false));
        }

        signIn = findViewById(R.id.button_login);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = login.edit();
                if (checkBox.isChecked()) {
                    editor.putString("email", email.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putBoolean("isChecked", checkBox.isChecked());
                    editor.commit();
                } else {
                    editor.putBoolean("isChecked", checkBox.isChecked());
                    editor.commit();
                }

                verifyUser();
            }
        });

        signUp = findViewById(R.id.link_create_account);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                finish();
            }
        });

        animate(background, imagesToShow, 0, true);


    }

    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {
        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration);
        fadeOut.setDuration(fadeOutDuration + timeBetween);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void verifyUser() {
        try {
            userDatabaseHelper = new UserDatabaseHelper(getApplicationContext());
            user = userDatabaseHelper.getUserAuth(email.getText().toString(), password.getText().toString());

            List<User> userList = userDatabaseHelper.getUserList();
            for (User user : userList) {
                Log.d("CHECK DATABASE", user.getName() + " " + user.getEmail() + " " + user.getPassword());
            }

            if (user == null) {
                email.setError("Please Enter a Valid Email");
                password.setError("Please Enter a Valid Password");
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
