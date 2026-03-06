package org.example.task3;

public class Scene {
    private Camera camera;
    private Character character;

    public Scene(Character character) {
        this.character = character;
        this.camera = new Camera();
    }

    public void simulateScene() {
        character.moveTowards(new Console());

        System.out.println("Но это был всего лишь...");
        camera.zoom();

        System.out.println(character.getName() + " догадался, что это эффект съемки.");
    }
}
