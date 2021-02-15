package org.raul.characters;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Polygon;

import org.raul.App;

public abstract class Character {

    private Polygon character;
    private  Point2D movement;
    private boolean alive;

    public Character(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);

        this.alive = true;

        this.movement = new Point2D(0, 0);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean aliveOrNot) {
        alive = aliveOrNot;
    }

    public Polygon getCharacter() {
        return character;
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 5);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 5);
    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= 0.01;
        changeY *= 0.01;

        this.movement = this.movement.add(changeX, changeY);
    }

    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());

        if (this.character.getTranslateX() < 0) {
            this.character.setTranslateX(this.character.getTranslateX() + App.WIDTH);
        }

        if (this.character.getTranslateX() > App.WIDTH) {
            this.character.setTranslateX(this.character.getTranslateX() % App.WIDTH);
        }

        if (this.character.getTranslateY() < 0) {
            this.character.setTranslateY(this.character.getTranslateY() + App.HEIGHT);
        }

        if (this.character.getTranslateY() > App.HEIGHT) {
            this.character.setTranslateY(this.character.getTranslateY() % App.HEIGHT);
        }
    }

    public boolean collide (Character other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;

    }

    public Point2D getMovement() {
        return this.movement;
    }

    public void setMovement(Point2D point) {
        this.movement = point;
    }


}
