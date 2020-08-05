package com.example.idletest;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InputName extends AppCompatActivity
{
    Button btn_ConfirmName;
    EditText txt_inputName;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();

    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private String currUID;

    String  ign;
    int chapters =1;
    int stages = 1;
    int golds = 5000;
    int fragments = 10;

    Integer [] attackHero = {188,200,311,180,177};
    Integer [] deffHero = {77,47,33,21,39};
    Integer [] magicHero = {133,200,154,240,200};
    Integer [] hpHero = {3333,2222,1311,1765,1987};
    String [] nameHero = {"1_Aldo","2_Todung","3_Eva","4_Ellen","5_Erin"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_main);
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        currUID = mAuth.getUid();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int witdhMetric = displayMetrics.widthPixels;
        int heigthMetric = displayMetrics.heightPixels;

        getWindow().setLayout((int)(witdhMetric*.9),(int)(heigthMetric*.6));

        btn_ConfirmName = findViewById(R.id.btn_confirmName);
        txt_inputName = findViewById(R.id.txt_inputName);
        txt_inputName.setText("");

        btn_ConfirmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (txt_inputName.getText().toString().matches(""))
                {
                    Toast.makeText(InputName.this, "Name Cant Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txt_inputName.getText().toString() != "")
                {
                    ign = txt_inputName.getText().toString();
                    GameDetailsDatabase gameDetailsDatabase = new GameDetailsDatabase(currUID,ign,stages,chapters,golds,fragments);
                    mRef.child(currUID).child(ign).setValue(gameDetailsDatabase);
                    for (int i=0;i<5;i++)
                    {
                        mRef.child(currUID).child(ign).child("z_hero").child(String.valueOf(nameHero[i])).child("1_level").setValue(1);
                        mRef.child(currUID).child(ign).child("z_hero").child(String.valueOf(nameHero[i])).child("2_attack").setValue(attackHero[i]);
                        mRef.child(currUID).child(ign).child("z_hero").child(String.valueOf(nameHero[i])).child("3_defense").setValue(deffHero[i]);
                        mRef.child(currUID).child(ign).child("z_hero").child(String.valueOf(nameHero[i])).child("4_magic").setValue(magicHero[i]);
                        mRef.child(currUID).child(ign).child("z_hero").child(String.valueOf(nameHero[i])).child("5_hp").setValue(hpHero[i]);
                    }

                    Log.w("user : ", "Get Curr Status" + gameDetailsDatabase);

                    String currName = txt_inputName.getText().toString();
                    Intent i = new Intent(InputName.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
