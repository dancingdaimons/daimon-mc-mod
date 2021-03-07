package com.daimons.daimondreams;

public class checkBtsMembers {
    boolean rpmAlive;
    int rpmTimer;
    boolean jschlattAlive;
    int jschlattTimer;
    boolean sigmaAlive;
    int sigmaTimer;
    boolean sinAlive;
    int sinTimer;
    boolean fAlive;
    int fTimer;
    boolean jimmyAlive;
    int jimmyTimer;
    boolean juhgkoopAlive;
    int juhgkoopTimer;

    public boolean getRpmAlive(){
        return rpmAlive;
    }

    public int getRpmTimer(){
        return rpmTimer;
    }

    public void setRpmAlive(boolean status){
        rpmAlive = status;
    }

    public void countdownRpmTimer(){
        rpmTimer--;
        System.out.println(rpmTimer);
    }

    public void setRpmDeath(){
        rpmTimer = 60;
        rpmAlive = false;
    }


}
