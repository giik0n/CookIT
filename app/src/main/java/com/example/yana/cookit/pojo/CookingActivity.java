package com.example.yana.cookit.pojo;

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

import com.example.yana.cookit.R;

import java.util.Locale;

public class CookingActivity extends AppCompatActivity {

    // вікно запуску готівки рецепту

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

        Bundle extras = getIntent().getExtras();// получаємо дінна про обраний зі списку рецепт
        setTitle(getString(R.string.cooking) + extras.getString("Title")+"...");
        steps = extras.getString("Steps").split(";");
        //if (!tmp[i].startsWith("timer: "))

        executeStep();// наступний шаг

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// створення потрібного таймеру
                countDownTimer = new CountDownTimer(timeToSet, 1000) {
                    @Override
                    public void onTick(long l) {// зміна тексту часу кожну секунду
                        timeToSet = l;

                        int minutes = (int)(timeToSet/1000)/60;
                        int seconds = (int)(timeToSet/1000)%60;

                        String formattedTime = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
                        timerText.setText(formattedTime);
                    }
                    @Override
                    public void onFinish() {// при завершені таймеру
                        timerText.setText(R.string.finish);
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);// мелодія
                        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);// вибрує як ти любиш
                        vib.vibrate(500);
                        r.play();
                        countDownTimer.cancel();// стоп таймера
                    }
                }.start();
            }
        });

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// наступний шаг
                currentSterp++;
                timeToSet = 60000;
                if (countDownTimer!=null){// перевірка чи є таймер
                    countDownTimer.cancel();
                }
                if (steps.length>currentSterp){//перевіряж чи можна виконати наступний крок
                    executeStep();
                }else if(steps.length==currentSterp){// якщо неможна то закриває рецепт
                    textLayout.setVisibility(View.VISIBLE);
                    timerLayout.setVisibility(View.INVISIBLE);
                    stepText.setText(R.string.done);
                    nextStep.setText(R.string.close);
                    nextStep.setCompoundDrawables(null,null,null,null);
                }else{
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// кнопка назад
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeStep(){// виконання кроку
        if (steps[currentSterp].startsWith("timer: ")){// якщо таймер
            timerLayout.setVisibility(View.VISIBLE);// показуем лучильник
            textLayout.setVisibility(View.INVISIBLE);
            timeToSet = 60000;
            int recipeMinutes = Integer.valueOf(steps[currentSterp].split(" ")[1]);
            timeToSet = timeToSet * recipeMinutes;
            int minutes = (int)(timeToSet/1000)/60;
            int seconds = (int)(timeToSet/1000)%60;

            String formattedTime = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
            timerText.setText(formattedTime);
        }else{
            textLayout.setVisibility(View.VISIBLE);// якщо не таймер просто показуе текст
            timerLayout.setVisibility(View.INVISIBLE);
            stepText.setText(steps[currentSterp]);
        }
    }
}
