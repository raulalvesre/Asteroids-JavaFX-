package org.raul;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.raul.characters.Asteroid;
import org.raul.characters.Character;
import org.raul.characters.Projectile;
import org.raul.characters.Ship;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class App extends Application {

    public static int WIDTH = 600;
    public static int HEIGHT = 400;
    private Pane space;
    private List<Character> asteroids;
    private List<Character> projectiles;

    //se rodar aqui direto, da erro
    public static void almostMain() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        projectiles = new ArrayList<>();
        asteroids = new ArrayList<>();

        //create space
        space = new Pane();
        space.setPrefSize(WIDTH, HEIGHT);

        //create and add Points score
        Text text = new Text(10, 20, "Points: 0");
        space.getChildren().add(text);

        // the real points
        AtomicInteger points = new AtomicInteger();

        //adding ship and asteroids to space
        space.getChildren().add(ship.getCharacter());
        addAsteroidsToSpace();

        Scene cena = new Scene(space);

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        cena.setOnKeyPressed((event) -> pressedKeys.put(event.getCode(), true));
        cena.setOnKeyReleased((event) -> pressedKeys.put(event.getCode(), false));

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                //seta o que acontece quando as teclas do jogo são pressionadas
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectiles.size() < 3) {
                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);

                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                    space.getChildren().add(projectile.getCharacter());
                }

                //td mundo se movendo
                ship.move();
                asteroids.forEach(Character::move);
                projectiles.forEach(Character::move);

                //cuida da mecanica de colisão asteroid e projectile
                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                        }
                    });

                    if (!projectile.isAlive()) {
                        text.setText("Points: " + points.addAndGet(1000));
                    }
                });

                removeDeadCharacters(projectiles);
                removeDeadCharacters(asteroids);

                //responsavel por fazer a animação parar caso a nave colida
                asteroids.forEach(as -> {
                    if (ship.collide(as)) {
                        stop();
                    }
                });

                //cria asteroids randomicamente enquanto o jogo roda
                if (Math.random() < 0.005) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        space.getChildren().add(asteroid.getCharacter());
                    }
                }
            }
        }.start();

        stage.setTitle("Asteroids!");
        stage.setScene(cena);
        stage.show();
    }


    private void addAsteroidsToSpace() {
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        asteroids.forEach(asteroids -> space.getChildren().add(asteroids.getCharacter()));
    }

    private void removeDeadCharacters (List<Character> characterList) {
        characterList.stream()
                .filter(character -> !character.isAlive())
                .forEach(character -> space.getChildren().remove(character.getCharacter()));

        characterList.removeAll(characterList.stream()
                .filter(character -> !character.isAlive())
                .collect(Collectors.toList()));
    }
}
