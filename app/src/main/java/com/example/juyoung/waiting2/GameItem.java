package com.example.juyoung.waiting2;

public class GameItem {
    int type;
    float x;
    int width;
    int speedX;

    public GameItem(int type, float x, int width, int speedX) {
        this.type=type;
        this.x = x;

        this.width=width;
        this.speedX = speedX;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }




    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }


    @Override
    public boolean equals(Object obj) {
        GameItem gameItem =(GameItem)obj;
        if(this.x>= gameItem.getX()&&this.x<= gameItem.getX()+ gameItem.width){
            return true;
        }
        return false;
    }
}
