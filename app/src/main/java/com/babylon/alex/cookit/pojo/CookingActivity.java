package com.babylon.alex.cookit.pojo;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.babylon.alex.cookit.R;

import java.util.Locale;

public class CookingActivity extends AppCompatActivity {

    String[] steps;
    ConstraintLayout timerLayout, textLayout;
    TextView stepText, timerText;
    Button nextStep, startTimer;
    CountDownTimer countDownTimer;
    int currentSterp = 0;
    long timeToSet = 60000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timerLayout = findViewById(R.id.timerStepLayout);
        textLayout = findViewById(R.id.textStepLayout);
        stepText = findViewById(R.id.textViewStep);
        timerText = findViewById(R.id.textViewTimer);
        nextStep = findViewById(R.id.buttonNextStep);
        startTimer = findViewById(R.id.buttonStartTimer);

        Bundle extras = getIntent().getExtras();
        setTitle("Cooking " + extras.getString("Title")+"...");
        steps = extras.getString("Steps").split(";");
        //if (!tmp[i].startsWith("timer: "))

        executeStep();

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer = new CountDownTimer(timeToSet, 1000) {
                    @Override
                    public void onTick(long l) {
                        timeToSet = l;

                        int minutes = (int)(timeToSet/1000)/60;
                        int seconds = (int)(timeToSet/1000)%60;

                        String formattedTime = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
                        timerText.setText(formattedTime);
                    }
                    @Override
                    public void onFinish() {
                        timerText.setText("Finish");
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vib.vibrate(500);
                        r.play();
                        countDownTimer.cancel();
                    }
                }.start();
            }
        });

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSterp++;
                timeToSet = 60000;
                if (countDownTimer!=null){
                    countDownTimer.cancel();
                }
                if (steps.length>currentSterp){
                    executeStep();
                }else if(steps.length==currentSterp){
                    textLayout.setVisibility(View.VISIBLE);
                    timerLayout.setVisibility(View.INVISIBLE);
                    stepText.setText("Done");
                    nextStep.setText("Close");
                    nextStep.setCompoundDrawables(null,null,null,null);
                }else{
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeStep(){
        if (steps[currentSterp].startsWith("timer: ")){
            timerLayout.setVisibility(View.VISIBLE);
            textLayout.setVisibility(View.INVISIBLE);
            timeToSet = 60000;
            int recipeMinutes = Integer.valueOf(steps[currentSterp].split(" ")[1]);
            timeToSet = timeToSet * recipeMinutes;
            int minutes = (int)(timeToSet/1000)/60;
            int seconds = (int)(timeToSet/1000)%60;

            String formattedTime = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
            timerText.setText(formattedTime);
        }else{
            textLayout.setVisibility(View.VISIBLE);
            timerLayout.setVisibility(View.INVISIBLE);
            stepText.setText(steps[currentSterp]);
        }
    }
}
