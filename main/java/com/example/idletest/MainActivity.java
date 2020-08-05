package com.example.idletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private Button btn_prev_chapter, btn_next_chapter, btn_lookHero,btn_battle;

    boolean stage1_10=false,stage2_10=false,stage3_10=false;

    long  chapters =1, stage;
    long count;
    String ign,snap;


    int [] ids = {R.id.txt_stage1,R.id.txt_stage2,R.id.txt_stage3,R.id.txt_stage4,R.id.txt_stage5,R.id.txt_stage6,
            R.id.txt_stage7,R.id.txt_stage8,R.id.txt_stage9,R.id.txt_stage10};
    TextView[] arrayStage ;
    int temp;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();

    FirebaseUser currUser;
    String currUID;
    DatabaseReference mUID;


    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        currUID = mAuth.getUid();
        mUID = mRef.child(currUID);

        checking_User();

        arrayStage = new TextView[10];
        for (int i = 0;i < ids.length; i++)
        {
            temp = ids[i];
            Log.d("tag", "ids : "+ids[i]);
            Log.d("tag", "temp : "+temp);
            arrayStage[i] = findViewById(temp);
        }


        btn_next_chapter = findViewById(R.id.next_chapter);
        btn_prev_chapter = findViewById(R.id.prev_chapter);
        btn_lookHero = findViewById(R.id.btn_lookHero);
        btn_battle = findViewById(R.id.btn_battle);

        btn_lookHero.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent heroIntent = new Intent(MainActivity.this,HeroActivity.class);
                startActivity(heroIntent);
            }
        });


        btn_next_chapter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (count <7 )
                {
                    count +=1;
                    checkingChapter();
                }

            }
        });
        btn_prev_chapter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (count > 1)
                {
                    count -=1;
                    checkingChapter();
                }
            }
        });

        btn_battle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent battleIntent = new Intent(MainActivity.this,BattleActivity.class);
                startActivity(battleIntent);
                finish();
            }
        });

    }
    private void checking_User()
    {
        mUID.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    for(DataSnapshot ignDS : snapshot.getChildren())
                    {
                        ign = ignDS.getKey();
                        if (ignDS.exists()) {
                            chapters = (long) ignDS.child("chapters").getValue();
                            Log.d("as", "check Chapters : " +chapters);
                            stage = (long) ignDS.child("stages").getValue();
                            Log.d("as", "check stages : " +stage);
                            count = chapters;

                            checkingChapter();
                        }
                    }
                }

                else if (!snapshot.exists())
                {
                    Intent gameIntent = new Intent(MainActivity.this, InputName.class);
                    startActivity(gameIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }
    private void checkingChapter()
    {
        if (count > 1 )
        {
            btn_next_chapter.setClickable(true);
            btn_prev_chapter.setClickable(true);
            btn_prev_chapter.setVisibility(View.VISIBLE);
            btn_next_chapter.setVisibility(View.VISIBLE);

            if (count == chapters)
            {
                btn_next_chapter.setClickable(false);
                btn_next_chapter.setVisibility(View.INVISIBLE);
            }

        }

        else if (count == 1)
        {
            btn_prev_chapter.setClickable(false);
            btn_prev_chapter.setVisibility(View.INVISIBLE);

            if (count ==  chapters)
            {
                btn_next_chapter.setClickable(false);
                btn_next_chapter.setVisibility(View.INVISIBLE);

            }
            else if (count < chapters)
            {
                btn_next_chapter.setClickable(true);
                btn_next_chapter.setVisibility(View.VISIBLE);
            }

        }

        checkingStage();
    }

    private void checkingStage()
    {

        if (count == chapters)
        {
            for (int i = 0;i < ids.length; i++)
            {
                arrayStage[i].setTextColor(Color.rgb(0,0,0));
            }

            for (int i = 0;i < ids.length; i++)
            {
                arrayStage[i].setText(chapters + " - " + (i+1));
                arrayStage[i].setClickable(false);
            }

            for (int i = 0;i < stage; i++)
            {
                arrayStage[i].setTextColor(Color.rgb(0,255,0));
                if (i == stage-1)
                {
                    arrayStage[i].setTextColor(Color.rgb(0,0,255));
                    arrayStage[i].isClickable();
                }
            }
        }
        else if (count < chapters)
        {

            for (int i = 0;i < ids.length; i++)
            {
                arrayStage[i].setText(count + " - " + (i+1));
                arrayStage[i].setClickable(false);
            }
            for (int i = 0;i < ids.length; i++)
            {
                arrayStage[i].setTextColor(Color.rgb(0,255,0));
            }
        }

        Toast.makeText(this, "Sucess Getting Data", Toast.LENGTH_SHORT).show();
    }

}