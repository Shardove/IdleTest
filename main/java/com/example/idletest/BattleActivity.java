package com.example.idletest;

import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BattleActivity extends AppCompatActivity
{
    TextView txt_battleStage;
    ImageView image_Hero1,image_Hero2,image_Hero3,image_Hero4,image_Hero5,
            image_Enemy1,image_Enemy2,image_Enemy3,image_Enemy4,image_Enemy5;

    int [] idHero = {R.id.hp_hero1,R.id.hp_hero2,R.id.hp_hero3,R.id.hp_hero4,R.id.hp_hero5};
    int [] idEnemy = {R.id.hp_enemy1,R.id.hp_enemy2,R.id.hp_enemy3,R.id.hp_enemy4,R.id.hp_enemy5};
    ImageView [] arrayHpHero;
    ImageView [] arrayHpEnemy;

    int tempArrayHero,tempArrayEnemy;


    ArrayList<Long> levelHero=new ArrayList<Long>();
    ArrayList<Long> attackHero=new ArrayList<Long>();
    ArrayList<Long> deffHero=new ArrayList<Long>();
    ArrayList<Long> magicHero=new ArrayList<Long>();
    ArrayList<Long> hpHero=new ArrayList<Long>();

    String [] nameHero = {"1_Aldo","2_Todung","3_Eva","4_Ellen","5_Erin"};
    String [] nameEnemy={"1_tank", "2_swordsman","3_archer", "4_mage", "5_healer"};

    ArrayList<Long> tempLevelEnemy=new ArrayList<Long>();
    ArrayList<Long> fullHpHero=new ArrayList<Long>();
    ArrayList<Long> fullHpEnemy=new ArrayList<Long>();


    ArrayList<Long> levelEnemy=new ArrayList<Long>();
    ArrayList<Long> attackEnemy=new ArrayList<Long>();
    ArrayList<Long> deffEnemy=new ArrayList<Long>();
    ArrayList<Long> magicEnemy=new ArrayList<Long>();
    ArrayList<Long> hpEnemy=new ArrayList<Long>();

    long [] initAttackEnemy = {120,180,280,120,133};
    long [] initDeffEnemy = {50,40,20,22,30};
    long [] initMagicEnemy = {70,180,100,300,190};
    long [] initHpEnemy = {3523,2823,1234,2154,1543};


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();

    FirebaseAuth mAuth;
    FirebaseUser currUser;
    String currUID,ign;

    int firstTime,targetEnemy,targetHero;

    long currStage, currChapter;
    Boolean hero1attack = false, hero2attack = false, hero3attack = false,hero4attack = false,hero5attack = false,
            enemy1attack = false, enemy2attack = false, enemy3attack = false,enemy4attack = false,enemy5attack = false;

    Handler handler = new Handler();
    private ClipDrawable mImageDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battle_main);

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        currUID = mAuth.getUid();

        image_Hero1 = findViewById(R.id.image_hero1);
        image_Hero2 = findViewById(R.id.image_hero2);
        image_Hero3 = findViewById(R.id.image_hero3);
        image_Hero4 = findViewById(R.id.image_hero4);
        image_Hero5 = findViewById(R.id.image_hero5);

        image_Enemy1 = findViewById(R.id.image_enemy1);
        image_Enemy2 = findViewById(R.id.image_enemy2);
        image_Enemy3 = findViewById(R.id.image_enemy3);
        image_Enemy4 = findViewById(R.id.image_enemy4);
        image_Enemy4 = findViewById(R.id.image_enemy5);

        arrayHpHero = new ImageView[5];
        arrayHpEnemy = new ImageView[5];

        for (int i =0; i<5; i++)
        {
            tempArrayHero = idHero[i];
            tempArrayEnemy = idEnemy[i];

            arrayHpHero[i]= findViewById(tempArrayHero);
            arrayHpEnemy[i]= findViewById(tempArrayEnemy);
        }

        txt_battleStage = findViewById(R.id.txt_battlestage);
        getDatabase();

    }

    private void getDatabase()
    {
        mRef.child(currUID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ignDS : snapshot.getChildren())
                {
                    ign = ignDS.getKey();
                    Log.d("as", "battle ign : " +ign);

                    currStage = (long)ignDS.child("stages").getValue();
                    currChapter = (long)ignDS.child("chapters").getValue();
                    txt_battleStage.setText(String.valueOf(currChapter+ " - "+currStage));

                    for (int i=0;i<5;i++)
                    {
                        Log.d("as", "battle check hero : " +nameHero[i]);
                        Long temp_level = (Long)ignDS.child("z_hero").child(nameHero[i]).child("1_level").getValue() ;

                        levelHero.add(i, temp_level);
                        Log.d("as", "battle check level : " +levelHero.get(i));

                        Long temp_attack = (Long)ignDS.child("z_hero").child(nameHero[i]).child("2_attack").getValue() ;
                        attackHero.add(i, temp_attack);
                        Log.d("as", "battle check attack : " +attackHero.get(i));

                        Long temp_deff = (Long)ignDS.child("z_hero").child(nameHero[i]).child("3_defense").getValue() ;
                        deffHero.add(i, temp_deff);
                        Log.d("as", "battle check deff : " +deffHero.get(i));

                        Long temp_magic = (Long)ignDS.child("z_hero").child(nameHero[i]).child("4_magic").getValue() ;
                        magicHero.add(i, temp_magic);
                        Log.d("as", "battle check magic : " +magicHero.get(i));

                        Long temp_hp = (Long)ignDS.child("z_hero").child(nameHero[i]).child("5_hp").getValue() ;
                        hpHero.add(i, temp_hp);
                        Log.d("as", "battle check hp : " +hpHero.get(i));
                    }

                    if (ign != "" && hpHero.get(4)!= null &&firstTime ==0)
                    {
                        fullHpHero = hpHero;
                        generateEnemyStats();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateEnemyStats()
    {
        firstTime =1;

        long new_level;
        long new_attack;
        long new_deff;
        long new_magic;
        long new_hp;

        if (currStage == 1)
        {
            new_level =currStage;
        }
        else
        {
            new_level = (currChapter) + currStage;
        }

        Log.d("tag", "enemy level"+ new_level);

        for (int i =0; i < 5;i++)
        {
            tempLevelEnemy.add(i, Long.valueOf(1));
            Log.d("tag", "enemy temp level"+ tempLevelEnemy);
            attackEnemy.add(i,initAttackEnemy[i]);
            Log.d("tag", "enemy attack"+ attackEnemy);
            deffEnemy.add(i,initDeffEnemy[i]);
            Log.d("tag", "enemy deff"+ deffEnemy);
            magicEnemy.add(i,initMagicEnemy[i]);
            Log.d("tag", "enemy magic"+ magicEnemy);
            hpEnemy.add(i,initHpEnemy[i]);
            Log.d("tag", "enemy hp"+ hpEnemy);

            if (tempLevelEnemy.get(i) != new_level )
            {
                for (int temp = 1; temp < new_level-1;temp ++)
                {
                    Log.d("tag", "enemy temp status"+temp + tempLevelEnemy +" "+ attackEnemy.get(i) +" "+ deffEnemy.get(i) +" "+ magicEnemy.get(i)
                            +" "+ hpEnemy.get(i));

                    if (tempLevelEnemy.get(i) <10 && tempLevelEnemy .get(i) > 1)
                    {
                        new_attack=(attackEnemy.get(i) + 4 + (attackEnemy.get(i) * 3 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 3 + (deffEnemy.get(i) * 3 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 4 + (magicEnemy.get(i) * 3 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 100 + (hpEnemy.get(i) * 1 / 100));
                        hpEnemy.set(i,new_hp);

                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if  (tempLevelEnemy.get(i) == 10)
                    {
                        new_attack=(attackEnemy.get(i) + 50 + (attackEnemy.get(i) * 5 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 50 + (deffEnemy.get(i) * 5 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 50 + (magicEnemy.get(i) * 5 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 200 + (hpEnemy.get(i) * 2 / 100));
                        hpEnemy.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if (tempLevelEnemy.get(i) <20 )
                    {
                        new_attack=(attackEnemy.get(i) + 10 + (attackEnemy.get(i) * 4 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 5 + (deffEnemy.get(i) * 4 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 10 + (magicEnemy.get(i) * 4 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 20 + (hpEnemy.get(i) * 2 / 100));
                        hpHero.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if (tempLevelEnemy.get(i) == 20 )
                    {
                        new_attack=(attackEnemy.get(i) + 100 + (attackEnemy.get(i) * 4 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 50 + (deffEnemy.get(i) * 4 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 100 + (magicEnemy.get(i) * 4 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 1000 + (hpEnemy.get(i) * 3 / 100));
                        hpEnemy.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if (tempLevelEnemy.get(i) < 40 )
                    {
                        new_attack=(attackEnemy.get(i) +10 + (attackEnemy.get(i) * 4 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 5 + (deffEnemy.get(i) * 4 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 10 + (magicEnemy.get(i) * 4 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 100 + (hpEnemy.get(i) * 2 / 100));
                        hpEnemy.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if (tempLevelEnemy.get(i) == 40 )
                    {
                        new_attack=(attackEnemy.get(i) + 400 + (attackEnemy.get(i) * 3 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 200 + (deffEnemy.get(i) * 2 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 400 + (magicEnemy.get(i) * 3 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 1000 + (hpEnemy.get(i) * 3 / 100));
                        hpEnemy.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if (tempLevelEnemy.get(i) <79 )
                    {
                        new_attack=(attackEnemy.get(i) + 60 + (attackEnemy.get(i) * 3 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 30 + (deffEnemy.get(i) * 3 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 44 + (magicEnemy.get(i) * 3 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 541 + (hpEnemy.get(i) * 3 / 100));
                        hpEnemy.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1)  ;
                    }

                    else if (tempLevelEnemy.get(i) == 79 )
                    {
                        new_attack=(attackEnemy.get(i) + 4545 + (attackEnemy.get(i) * 5 / 100));
                        attackEnemy.set(i,new_attack);

                        new_deff=(deffEnemy.get(i) + 3888 + (deffEnemy.get(i) * 4 / 100));
                        deffEnemy.set(i,new_deff);

                        new_magic=(magicEnemy.get(i) + 4545 + (magicEnemy.get(i) * 5 / 100));
                        magicEnemy.set(i,new_magic);

                        new_hp=(hpEnemy.get(i) + 10000 + (hpEnemy.get(i) * 10 / 100));
                        hpEnemy.set(i,new_hp);
                        tempLevelEnemy.set(i, tempLevelEnemy.get(i)+1) ;
                    }
                }
            }

        }
        fullHpEnemy = hpEnemy;
        updateDatabaseEnemy();
    }

    private void updateDatabaseEnemy()
    {
        for (int i = 0; i< tempLevelEnemy.size() ; i++)
        {
            Log.d("tag", "checkall : "+currUID +" " +ign +" " +tempLevelEnemy.get(i) +" " +attackEnemy.get(i));
            mRef.child(currUID).child(ign).child("z_enemy").child(nameEnemy[i]).child("1_level").setValue(tempLevelEnemy.get(i));
            mRef.child(currUID).child(ign).child("z_enemy").child(nameEnemy[i]).child("2_attack").setValue(attackEnemy.get(i));
            mRef.child(currUID).child(ign).child("z_enemy").child(nameEnemy[i]).child("3_defense").setValue(deffEnemy.get(i));
            mRef.child(currUID).child(ign).child("z_enemy").child(nameEnemy[i]).child("4_magic").setValue(magicEnemy.get(i));
            mRef.child(currUID).child(ign).child("z_enemy").child(nameEnemy[i]).child("5_hp").setValue(hpEnemy.get(i));
        }

        tankTimer();
        swordsmanTimer();
        archerTimer();
        mageTimer();
        healerTimer();

    }

    private void tankTimer()
    {
        if (!enemy1attack && !hero1attack)
        {
            new CountDownTimer(2000, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {
                    if (hpEnemy.get(0) >=1)
                    {
                        enemy1attack = true;
                    }

                    if (hpHero.get(0) >=1)
                    {
                        hero1attack = true;
                    }

                    checkingTarget();
                }
            }.start();
        }

    }
    private void swordsmanTimer()
    {
        if (!enemy2attack && !hero2attack)
        {
            new CountDownTimer(1800, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {
                    if (hpEnemy.get(1) >=1)
                    {
                        enemy2attack = true;
                    }

                    if (hpHero.get(1) >=1)
                    {
                        hero2attack = true;
                    }

                    checkingTarget();
                }
            }.start();
        }

    }
    private void archerTimer()
    {
        if (!enemy3attack && !hero3attack)
        {
            new CountDownTimer(1600, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {
                    if (hpEnemy.get(2) >=1)
                    {
                        enemy3attack = true;
                    }

                    if (hpHero.get(2) >=1)
                    {
                        hero3attack = true;
                    }

                    checkingTarget();
                }
            }.start();
        }

    }
    private void mageTimer ()
    {
        if (!enemy4attack && !hero4attack)
        {
            new CountDownTimer(1750, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {
                    if (hpEnemy.get(3) >=1)
                    {
                        enemy4attack = true;
                    }

                    if (hpHero.get(3) >=1)
                    {
                        hero4attack = true;
                    }

                    checkingTarget();
                }
            }.start();
        }

    }
    private void healerTimer()
    {
        if (!enemy5attack && !hero5attack)
        {
            new CountDownTimer(1900, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {
                    if (hpEnemy.get(4) >=1)
                    {
                        enemy5attack = true;
                    }

                    if (hpHero.get(4) >=1)
                    {
                        hero5attack = true;
                    }

                    checkingTarget();
                }
            }.start();
        }

    }

    private void checkingTarget()
    {
        if (hpEnemy.get(4) <=0)
        {
            Log.d("tag", "You WON");
            if (currStage == 10)
            {
                currChapter+=1;
                currStage = 1;
                mRef.child(currUID).child(ign).child("stages").child("stages").setValue(currStage);
                mRef.child(currUID).child(ign).child("stages").child("chapters").setValue(currChapter);
                Intent winIntent = new Intent(BattleActivity.this,MainActivity.class);
                startActivity(winIntent);
                finish();
            }

            else
            {
                currStage +=1;
                mRef.child(currUID).child(ign).child("stages").setValue(currStage);
                Intent winIntent = new Intent(BattleActivity.this,MainActivity.class);
                startActivity(winIntent);
                finish();
            }
        }

        else
        {
            for(int i = 0; i <5; i++)
            {
                if (hpEnemy.get(i) >= 1)
                {
                    targetEnemy = i;
                    attackTargetEnemy();
                    break;
                }
            }

            for (int j = 0;j<5; j ++)
            {
                if (hpHero.get(j) >= 1)
                {
                    targetHero = j;
                    attackTargetHero();
                    break;
                }
                else if (j == 4 && hpHero.get(j) <=0)
                {
                    Log.d("tag", "You LOSE");
                    Intent loseIntent = new Intent(BattleActivity.this,MainActivity.class);
                    startActivity(loseIntent);
                    finish();

                    break;
                }
            }
        }

    }
    private void attackTargetEnemy()
    {
        long damageToEnemy;
        long tempHpEnemy;
        long hpEnemyDiff;

        if (hero1attack)
        {
            if (hpHero.get(0) >=1 )
            {
                damageToEnemy = attackHero.get(0) - deffEnemy.get(targetEnemy);
                tempHpEnemy = hpEnemy.get(targetEnemy) - damageToEnemy ;

                hpEnemy.set(targetEnemy, tempHpEnemy);
                Log.d("tag", "Aldo Attack : tempHPEnemy : "+targetEnemy +" = " + hpEnemy.get(targetEnemy));

                hpEnemyDiff = (hpEnemy.get(targetEnemy) /fullHpEnemy.get(targetEnemy)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpEnemyDiff);

            }

            hero1attack = false;
            tankTimer();
        }

        else if(hero2attack)
        {
            if (hpHero.get(1) >=1 )
            {
                damageToEnemy = attackHero.get(1) - deffEnemy.get(targetEnemy);
                tempHpEnemy = hpEnemy.get(targetEnemy) - damageToEnemy ;

                hpEnemy.set(targetEnemy, tempHpEnemy);
                Log.d("tag", "Todung Attack : tempHPEnemy : "+targetEnemy +" = " + hpEnemy.get(targetEnemy));

                hpEnemyDiff = (hpEnemy.get(targetEnemy) /fullHpEnemy.get(targetEnemy)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpEnemyDiff);

            }
            hero2attack = false;
            swordsmanTimer();
        }

        else if (hero3attack)
        {
            if (hpHero.get(2) >=1 )
            {
                damageToEnemy = attackHero.get(2) - deffEnemy.get(targetEnemy);
                tempHpEnemy = hpEnemy.get(targetEnemy) - damageToEnemy ;

                hpEnemy.set(targetEnemy, tempHpEnemy);
                Log.d("tag", "Eva Attack : tempHPEnemy : "+targetEnemy +" = " + hpEnemy.get(targetEnemy));

                hpEnemyDiff = (hpEnemy.get(targetEnemy) /fullHpEnemy.get(targetEnemy)) *10000;
                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);

                clipDrawable.setLevel((int) hpEnemyDiff);
            }

            hero3attack = false;
            archerTimer();
        }

        else if (hero4attack)
        {
            if (hpHero.get(3) >=1 )
            {
                damageToEnemy = attackHero.get(3) - deffEnemy.get(targetEnemy);
                tempHpEnemy = hpEnemy.get(targetEnemy) - damageToEnemy ;

                hpEnemy.set(targetEnemy, tempHpEnemy);
                Log.d("tag", "Ellen Attack : tempHPEnemy : "+targetEnemy +" = " + hpEnemy.get(targetEnemy));

                hpEnemyDiff = (hpEnemy.get(targetEnemy) /fullHpEnemy.get(targetEnemy)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpEnemyDiff);

            }
            hero4attack = false;
            mageTimer();
        }

        else if(hero5attack)
        {
            if (hpHero.get(4) >=1 )
            {
                damageToEnemy = attackHero.get(4) - deffEnemy.get(targetEnemy);
                tempHpEnemy = hpEnemy.get(targetEnemy) - damageToEnemy ;

                hpEnemy.set(targetEnemy, tempHpEnemy);
                Log.d("tag", "Erin Attack : tempHPEnemy : "+targetEnemy +" = " + hpEnemy.get(targetEnemy));

                hpEnemyDiff = (hpEnemy.get(targetEnemy) /fullHpEnemy.get(targetEnemy)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpEnemyDiff);

            }
            hero5attack = false;
            healerTimer();
        }
    }


    private void attackTargetHero()
    {
        long damageToHero;
        long tempHpHero;
        long hpHeroDiff;

        if (enemy1attack)
        {
            if (hpEnemy.get(0) >=1 )
            {
                damageToHero = attackEnemy.get(0) - deffHero.get(targetHero);
                tempHpHero = hpHero.get(targetHero) - damageToHero ;

                hpHero.set(targetHero, tempHpHero);
                Log.d("tag", "Enemy Tank : tempHPHero : "+targetHero +" = " + hpHero.get(targetHero));

                hpHeroDiff = (hpHero.get(targetHero) /fullHpHero.get(targetHero)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpHeroDiff);

            }

            enemy1attack = false;
            tankTimer();
        }

        else if (enemy2attack)
        {
            if (hpEnemy.get(1) >=1 )
            {
                damageToHero = attackEnemy.get(1) - deffHero.get(targetHero);
                tempHpHero = hpHero.get(targetHero) - damageToHero ;

                hpHero.set(targetHero, tempHpHero);
                Log.d("tag", "Enemy Swordsman : tempHPHero : "+targetHero +" = " + hpHero.get(targetHero));

                hpHeroDiff = (hpHero.get(targetHero) /fullHpHero.get(targetHero)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpHeroDiff);

            }
            enemy2attack = false;
            swordsmanTimer();
        }

        else if (enemy3attack)
        {
            if (hpEnemy.get(2) >=1 )
            {
                damageToHero = attackEnemy.get(2) - deffHero.get(targetHero);
                tempHpHero = hpHero.get(targetHero) - damageToHero ;

                hpHero.set(targetHero, tempHpHero);
                Log.d("tag", "Enemy Archer : tempHPHero : "+targetHero +" = " + hpHero.get(targetHero));

                hpHeroDiff = (hpHero.get(targetHero) /fullHpHero.get(targetHero)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpHeroDiff);

            }

            enemy3attack = false;
            archerTimer();
        }

        else if (enemy4attack)
        {
            if (hpEnemy.get(3) >=1 )
            {
                damageToHero = attackEnemy.get(3) - deffHero.get(targetHero);
                tempHpHero = hpHero.get(targetHero) - damageToHero ;

                hpHero.set(targetHero, tempHpHero);
                Log.d("tag", "Enemy Mage : tempHPHero : "+targetHero +" = " + hpHero.get(targetHero));

                hpHeroDiff = (hpHero.get(targetHero) /fullHpHero.get(targetHero)) *10000;

                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                clipDrawable.setLevel((int) hpHeroDiff);

            }

            enemy4attack = false;
            mageTimer();
        }

        else if (enemy5attack)
        {
            if (hpEnemy.get(4) >=1 )
            {
                damageToHero = attackEnemy.get(4) - deffHero.get(targetHero);
                tempHpHero = hpHero.get(targetHero) - damageToHero ;

                hpHero.set(targetHero, tempHpHero);
                Log.d("tag", "Enemy Healer : tempHPHero : "+targetHero +" = " + hpHero.get(targetHero));

                hpHeroDiff = (hpHero.get(targetHero) /fullHpHero.get(targetHero)) *10000;
                ClipDrawable clipDrawable = new ClipDrawable(arrayHpHero[targetHero].getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);

                clipDrawable.setLevel((int) hpHeroDiff);

            }

            enemy5attack = false;
            healerTimer();
        }

    }
}
