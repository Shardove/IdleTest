package com.example.idletest;

public class GameDetailsDatabase
{

    private String currUID;
    private  String ign;
    private  int stages;
    private  int chapters;
    private  int golds;
    private  int fragments;


    public GameDetailsDatabase(String currUID, String ign, int stages, int chapters, int golds, int fragments)
    {
        this.ign = ign;
        this.stages = stages;
        this.chapters= chapters;
        this.golds = golds;
        this.fragments= fragments;

    }

    public String getcurrUID() {
        return currUID;
    }

    public String getign() {
        return ign;
    }

    public int getstages() {
        return stages;
    }

    public int getchapters() {
        return chapters;
    }

    public int getgolds() {
        return golds;
    }

    public int getfragments() {
        return fragments;
    }


}
