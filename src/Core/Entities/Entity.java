package Core.Entities;

import Core.Chunk;

import java.util.List;

public interface Entity extends Movable {
    /**
     * Whether the object can interact with the object
     *
     * @return if the object is an instance of Interactable
     */
    default boolean canInteractWith(Object o) {
        return o instanceof Interactable;
    }

    /**
     * Interact with the given object
     *
     * @param object the object to be interacted with
     */
    void interactWith(List<Interactable> object);

    /**
     * The entity will attack the tile in front of it.
     */
    default void attack() {
        throw new UnsupportedOperationException();
    }

    void getDamaged(int damage);

    int getDamage();

    /**
     * Decreases the entity's health by the damage afflicted to it.
     *
     * @param damage the amount of damage done to the entity.
     */
    default void decreaseHealth(int damage) {
        setHealth(getHealth() - damage);
    }

    int getHealth();

    void setHealth(int health);

    default void init(Chunk chunk) {

    }
}
