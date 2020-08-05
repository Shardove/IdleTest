package com.example.idletest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HeroActivity extends AppCompatActivity
{
    TextView txt_heroName,txt_currGold,txt_currFragment, txt_heroLevel,txt_prevHero,txt_nextHero,txt_upgradeGoldHero,txt_upgradeFragmentHero,
            txt_attackHero,txt_deffHero,txt_magicHero,txt_hpHero;
    Button btn_upgrade,btn_backHero;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();

    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private String currUID;
    String ign;

    Double currGolds;
    long currFragments;
    int count = 0;
    long goldcost,fragmentcost;

    ArrayList<Long> levelHero=new ArrayList<Long>();
    ArrayList<Long> attackHero=new ArrayList<Long>();
    ArrayList<Long> deffHero=new ArrayList<Long>();
    ArrayList<Long> magicHero=new ArrayList<Long>();
    ArrayList<Long> hpHero=new ArrayList<Long>();

    String [] nameHero = {"1_Aldo","2_Todung","3_Eva","4_Ellen","5_Erin"};


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_main);

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        currUID = mAuth.getUid();

        txt_heroName = findViewById(R.id.txt_heroName);
        txt_currGold = findViewById(R.id.txt_gold_hero);
        txt_currFragment = findViewById(R.id.txt_fragment_hero);
        txt_heroLevel = findViewById(R.id.txt_level_hero);
        txt_prevHero = findViewById(R.id.txt_previous_hero);
        txt_nextHero = findViewById(R.id.txt_next_hero);
        txt_upgradeGoldHero = findViewById(R.id.txt_upgradegold_hero);
        txt_upgradeFragmentHero = findViewById(R.id.txt_upgradefrag_hero);
        txt_attackHero = findViewById(R.id.txt_attack_hero);
        txt_deffHero = findViewById(R.id.txt_deff_hero);
        txt_magicHero = findViewById(R.id.txt_magicHero);
        txt_hpHero = findViewById(R.id.txt_hpHero);

        btn_upgrade = findViewById(R.id.btn_levelup_hero);
        btn_backHero = findViewById(R.id.btn_back_hero);

        btn_upgrade.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (goldcost > currGolds)
                {
                    Toast.makeText(HeroActivity.this, "Not Enough Golds", Toast.LENGTH_SHORT).show();
                }

                else if (currGolds > goldcost)
                {
                    if (fragmentcost > currFragments)
                    {
                        Toast.makeText(HeroActivity.this, "Not Enough Fragments", Toast.LENGTH_SHORT).show();
                    }
                    else
                        updateHero();
                }
            }
        });

        btn_backHero.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent backtoHome = new Intent(HeroActivity.this,MainActivity.class);
                startActivity(backtoHome);
                finish();

            }
        });

        txt_nextHero.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (count == 4)
                {
                    count =0;
                    checkingHero();
                }
                else
                    count+=1;
                    checkingHero();
            }
        });

        txt_prevHero.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (count == 0)
                {
                    count = 4;
                    checkingHero();
                }
                else
                    count-=1;
                    checkingHero();
            }
        });

        getDataHero();
    }

    private void getDataHero()
    {
        mRef.child(currUID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ignDS : snapshot.getChildren())
                {
                    ign = ignDS.getKey();
                    Log.d("as", "check IGN : " +ign);
                    currGolds = ignDS.child("golds").getValue((double.class));
                    currFragments = (long) ignDS.child("fragments").getValue();

                    for (int i=0;i<=4;i++)
                    {
                        Log.d("as", "iis: " +i);
                        Log.d("as", "check hero : " +nameHero[i]);
                        Long temp_level = (Long)ignDS.child("z_hero").child(nameHero[i]).child("1_level").getValue() ;

                        levelHero.add(i, temp_level);
                        Log.d("as", "check level : " +levelHero.get(i));

                        Long temp_attack = (Long)ignDS.child("z_hero").child(nameHero[i]).child("2_attack").getValue() ;
                        attackHero.add(i, temp_attack);
                        Log.d("as", "check attack : " +attackHero.get(i));

                        Long temp_deff = (Long)ignDS.child("z_hero").child(nameHero[i]).child("3_defense").getValue() ;
                        deffHero.add(i, temp_deff);
                        Log.d("as", "check deff : " +deffHero.get(i));

                        Long temp_magic = (Long)ignDS.child("z_hero").child(nameHero[i]).child("4_magic").getValue() ;
                        magicHero.add(i, temp_magic);
                        Log.d("as", "check magic : " +magicHero.get(i));

                        Long temp_hp = (Long)ignDS.child("z_hero").child(nameHero[i]).child("5_hp").getValue() ;
                        hpHero.add(i, temp_hp);
                        Log.d("as", "check hp : " +hpHero.get(i));

                        txt_heroLevel.setText(String.valueOf(levelHero.get(0)));
                    }
                    checkingHero();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void checkingHero()
    {
        txt_currGold.setText(String.valueOf(currGolds) );
        txt_currFragment.setText(String.valueOf(currFragments));

        txt_heroName.setText(nameHero[count]);

        txt_heroLevel.setText(String.valueOf(levelHero.get(count)));
        txt_attackHero.setText(String.valueOf(attackHero.get(count)));
        txt_deffHero.setText(String.valueOf(deffHero.get(count)));
        txt_magicHero.setText(String.valueOf(magicHero.get(count)));
        txt_hpHero.setText(String.valueOf(hpHero.get(count)));


        txt_heroLevel.setText(String.valueOf(levelHero.get(count)));
        Log.d("as","check Levels "+levelHero.get(count));
        Log.d("as", "check Gold : " +currGolds);
        Log.d("as", "check fragment : " +currFragments);



        checkingUpgradeCost();

    }
    private void checkingUpgradeCost ()
    {
        btn_upgrade.setText("Upgrade");
        btn_upgrade.setClickable(true);

        if (levelHero.get(count) < 10)
        {
            goldcost= (levelHero.get(count) *111) + (levelHero.get(count) *3);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 0;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }
        else if (levelHero.get(count) == 10)
        {
            goldcost= (levelHero.get(count) *200) + (levelHero.get(count) *10);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 1;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }
        else if (levelHero.get(count) < 20)
        {
            goldcost= (levelHero.get(count) *222) + (levelHero.get(count) *5);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 0;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }
        else if (levelHero.get(count) == 20)
        {
            goldcost= (levelHero.get(count) *400) + (levelHero.get(count) *26);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 2;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }

        else if (levelHero.get(count) < 40)
        {
            goldcost= (levelHero.get(count) *444) + (levelHero.get(count) *7);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 0;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }

        else if (levelHero.get(count) == 40)
        {
            goldcost= (levelHero.get(count) *444) + (levelHero.get(count) *56);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 5;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }

        else if (levelHero.get(count) < 79)
        {
            goldcost= (levelHero.get(count) *666) + (levelHero.get(count) *9);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 0;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }

        else if (levelHero.get(count) == 79)
        {
            goldcost= (levelHero.get(count) *666) + (levelHero.get(count) *84);
            txt_upgradeGoldHero.setText(String.valueOf(goldcost));
            fragmentcost = 10;
            txt_upgradeFragmentHero.setText(String.valueOf(fragmentcost));
        }
        if (levelHero.get(count) == 80)
        {
            txt_upgradeFragmentHero.setText("Maxed");
            txt_upgradeGoldHero.setText("Maxed");
            btn_upgrade.setText("Cant Upgrade Anymore");
            btn_upgrade.setClickable(false);
        }

    }

    private void updateHero()
    {
        currGolds -=goldcost;
        currFragments -=fragmentcost;

        long new_level;
        long new_attack;
        long new_deff;
        long new_magic;
        long new_hp;

        new_level = levelHero.get(count) +1 ;
        levelHero.set(count,new_level);


        if (levelHero.get(count) <10)
        {
            new_attack=(attackHero.get(count) + 4 + (attackHero.get(count) * 3 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 3 + (deffHero.get(count) * 3 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 4 + (magicHero.get(count) * 3 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 100 + (hpHero.get(count) * 1 / 100));
            hpHero.set(count,new_hp);
        }

        else if  (levelHero.get(count) == 10)
        {
            new_attack=(attackHero.get(count) + 50 + (attackHero.get(count) * 5 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 50 + (deffHero.get(count) * 5 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 50 + (magicHero.get(count) * 5 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 200 + (hpHero.get(count) * 2 / 100));
            hpHero.set(count,new_hp);
        }
        else if (levelHero.get(count) <20 )
        {
            new_attack=(attackHero.get(count) + 10 + (attackHero.get(count) * 4 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 5 + (deffHero.get(count) * 4 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 10 + (magicHero.get(count) * 4 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 20 + (hpHero.get(count) * 2 / 100));
            hpHero.set(count,new_hp);
        }
        else if (levelHero.get(count) == 20 )
        {
            new_attack=(attackHero.get(count) + 100 + (attackHero.get(count) * 4 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 50 + (deffHero.get(count) * 4 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 100 + (magicHero.get(count) * 4 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 1000 + (hpHero.get(count) * 3 / 100));
            hpHero.set(count,new_hp);
        }
        else if (levelHero.get(count) < 40 )
        {
            new_attack=(attackHero.get(count) +10 + (attackHero.get(count) * 4 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 5 + (deffHero.get(count) * 4 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 10 + (magicHero.get(count) * 4 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 100 + (hpHero.get(count) * 2 / 100));
            hpHero.set(count,new_hp);
        }
        else if (levelHero.get(count) == 40 )
        {
            new_attack=(attackHero.get(count) + 400 + (attackHero.get(count) * 3 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 200 + (deffHero.get(count) * 2 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 400 + (magicHero.get(count) * 3 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 1000 + (hpHero.get(count) * 3 / 100));
            hpHero.set(count,new_hp);
        }
        else if (levelHero.get(count) <79 )
        {

            new_attack=(attackHero.get(count) + 60 + (attackHero.get(count) * 3 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 30 + (deffHero.get(count) * 3 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 44 + (magicHero.get(count) * 3 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 541 + (hpHero.get(count) * 3 / 100));
            hpHero.set(count,new_hp);
        }

        else if (levelHero.get(count) == 79 )
        {
            new_attack=(attackHero.get(count) + 4545 + (attackHero.get(count) * 5 / 100));
            attackHero.set(count,new_attack);

            new_deff=(deffHero.get(count) + 3888 + (deffHero.get(count) * 4 / 100));
            deffHero.set(count,new_deff);

            new_magic=(magicHero.get(count) + 4545 + (magicHero.get(count) * 5 / 100));
            magicHero.set(count,new_magic);

            new_hp=(hpHero.get(count) + 10000 + (hpHero.get(count) * 10 / 100));
            hpHero.set(count,new_hp);
        }

        updateDatabaseHero();
        checkingHero();
    }
    private void updateDatabaseHero()
    {
        mRef.child(currUID).child(ign).child("z_hero").child(nameHero[count]).child("1_level").setValue(levelHero.get(count));
        mRef.child(currUID).child(ign).child("z_hero").child(nameHero[count]).child("2_attack").setValue(attackHero.get(count));
        mRef.child(currUID).child(ign).child("z_hero").child(nameHero[count]).child("3_defense").setValue(deffHero.get(count));
        mRef.child(currUID).child(ign).child("z_hero").child(nameHero[count]).child("4_magic").setValue(magicHero.get(count));
        mRef.child(currUID).child(ign).child("z_hero").child(nameHero[count]).child("5_hp").setValue(hpHero.get(count));

        mRef.child(currUID).child(ign).child("golds").setValue(currGolds);
        mRef.child(currUID).child(ign).child("fragments").setValue(currFragments);
    }
}
