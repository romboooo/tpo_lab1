package org.example.task3;

public class Character {
    private String name;
    private final Head head;
    private final Position position;

    public Character() {
        Hair hair = new Hair("Черные");
        this.head = new Head(true, hair);
        this.position = new Position();
    }
    public void moveTowards(Console console) {
        System.out.println(name + " начал медленно двигаться к пульту.");
        position.moveTo(console);
        head.feelHairMovement();
    }

    public String getName(){
        return name;
    }
}