package id.ac.its.mahmud.spaceship;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

	private final int ICRAFT_X = 40;
    private final int ICRAFT_Y = 60;
    private final int B_WIDTH = 400;
    private final int B_HEIGHT = 300;
    private final int DELAY = 15;
    private Timer timer;
    private SpaceShip spaceShip;
    private boolean ingame;
    private List<Asteroid> asteroids;

    private final int[][] pos = {
    		{2380, 29}, {2500, 59}, {1380, 89},
            {780, 109}, {580, 139}, {680, 239},
            {790, 259}, {760, 50}, {790, 150},
            {980, 209}, {560, 45}, {510, 70},
            {930, 159}, {590, 80}, {530, 60},
            {940, 59}, {990, 30}, {920, 200},
            {900, 259}, {660, 50}, {540, 90},
            {810, 220}, {860, 20}, {740, 180},
            {820, 128}, {490, 170}, {700, 30}
        };
    
    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.BLUE);
        setFocusable(true);
        ingame = true;

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        spaceShip = new SpaceShip(ICRAFT_X, ICRAFT_Y);
        initAsteroids();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void initAsteroids() {
        
        asteroids = new ArrayList<>();

        for (int[] p : pos) {
            asteroids.add(new Asteroid(p[0], p[1]));
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (ingame) {

            doDrawing(g);

        } else {

            drawGameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {

    	if (spaceShip.isVisible()) {
            g.drawImage(spaceShip.getImage(), spaceShip.getX(), spaceShip.getY(),this);
        }

        List<Missile> ms = spaceShip.getMissiles();

        for (Missile missile : ms) {
            if (missile.isVisible()) {
                g.drawImage(missile.getImage(), missile.getX(), 
                        missile.getY(), this);
            }
        }

        for (Asteroid asteroid : asteroids) {
            if (asteroid.isVisible()) {
                g.drawImage(asteroid.getImage(), asteroid.getX(), asteroid.getY(), this);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("Asteroid left: " + asteroids.size(), 5, 15);
    }

    private void drawGameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fm = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

    	inGame();
        updateMissiles();
        updateSpaceShip();
        updateAsteroids();
        
        checkCollisions();
        repaint();
    }
    
    private void inGame() {

        if (!ingame) {
            timer.stop();
        }
    }
    
    private void updateSpaceShip() {

    	if (spaceShip.isVisible()) {
            
            spaceShip.move();
        }
    }
    
    private void updateMissiles() {

        List<Missile> missiles = spaceShip.getMissiles();

        for (int i = 0; i < missiles.size(); i++) {

            Missile missile = missiles.get(i);

            if (missile.isVisible()) {

                missile.move();
            } else {

                missiles.remove(i);
            }
        }
    }

    private void updateAsteroids() {

        if (asteroids.isEmpty()) {

            ingame = false;
            return;
        }

        for (int i = 0; i < asteroids.size(); i++) {

            Asteroid a = asteroids.get(i);
            
            if (a.isVisible()) {
                a.move();
            } else {
                asteroids.remove(i);
            }
        }
    }

    public void checkCollisions() {

        Rectangle r3 = spaceShip.getBounds();

        for (Asteroid asteroid : asteroids) {
            
            Rectangle r2 = asteroid.getBounds();

            if (r3.intersects(r2)) {
                
                spaceShip.setVisible(false);
                asteroid.setVisible(false);
                ingame = false;
            }
        }

        List<Missile> ms = spaceShip.getMissiles();

        for (Missile m : ms) {

            Rectangle r1 = m.getBounds();

            for (Asteroid asteroid : asteroids) {

                Rectangle r2 = asteroid.getBounds();

                if (r1.intersects(r2)) {
                    
                    m.setVisible(false);
                    asteroid.setVisible(false);
                }
            }
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            spaceShip.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            spaceShip.keyPressed(e);
        }
    }
}