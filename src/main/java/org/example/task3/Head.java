package org.example.task3;

public class Head {
    private boolean isEthereal;
    private Hair hair;

    public Head(boolean isEthereal, Hair hair) {
        this.isEthereal = isEthereal;
        this.hair = hair;
    }

    public void feelHairMovement() {
        if (isEthereal) {
            System.out.println("На бесплотной голове почувствовалось движение.");
            hair.move();
        }
    }

}