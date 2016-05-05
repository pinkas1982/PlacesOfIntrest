package com.myapps.pinkas.placesofintrest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.myapps.pinkas.placesofintrest.MainActivity;
import com.myapps.pinkas.placesofintrest.R;

/**
 * Created by pinkas on 4/28/2016.
 */
public class SplashScreen extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final ImageView splashImageView = (ImageView)findViewById(R.id.splashImageView);
        final Animation splashAnim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        final Animation fadeOutAnim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);


        splashImageView.startAnimation(splashAnim);
        splashAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImageView.startAnimation(fadeOutAnim);
                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
