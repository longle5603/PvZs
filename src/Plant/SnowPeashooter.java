package Plant;

import java.awt.event.ActionEvent;

import javax.swing.Timer;

import Game.GamePanel;

public class SnowPeashooter extends Plant {

    private int health = 250;

    public Timer shootTimer;

    public SnowPeashooter(GamePanel parent, int x_box, int y_lineland) {
        super(parent, x_box, y_lineland);
        shootTimer = new Timer(4500, (ActionEvent Shot) -> {
            /*
             * Get the position of zombie as zombie store in an array has 5 small arrays so
             * it get y
             * mean it get which land that has zombie or the size > 0
             */
            if (gp.gm.Zombie_units.get(y_lineland).size() > 0) {
                gp.gm.SnowPeaInField.get(y_lineland).add(new Snowpea(gp, y_lineland, 360 + this.x * 120));/*
                                                                                                           * Cong thuc
                                                                                                           */
                /* Add an bullet */
            }
        });
        shootTimer.start();
    }

    @Override
    public void stop() {
        shootTimer.stop();
    }

    public void receivedamage(int calculatedDamage) {
        if (calculatedDamage > 0) {
            int newHealth = this.health - calculatedDamage;

            if (newHealth <= 0) {
                newHealth = 0; // Ensure health doesn't go below zero
                System.out.println("Peashooter has been defeated!");
            }

            System.out.println("Peashooter received damage: " + calculatedDamage);
            System.out.println("Peashooter remaining health: " + newHealth);

            this.health = newHealth; // Update the Peashooter's health
        } else {
            System.out.println("Invalid damage value. Damage must be greater than 0.");
        }
    }

}