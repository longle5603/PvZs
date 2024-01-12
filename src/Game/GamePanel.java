package Game;

import static GUI.GameSFX.Music.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import GameElement.Collider;
import GameElement.Position;
import InputForGame.Mouse;
import InputForGame.MyMouseListener;
import Plant.Pea;
import Plant.Peashooter;
import Plant.Sunflower;
import Sun.Sun;
import Zombie.Zombie;

public class GamePanel extends JFrame implements Runnable, Mouse {
    enum PlantType {
        None,
        Sunflower,
        Peashooter,
    }

    private MouseListener peashooterButtonMouseListener;
    public int PlantBox;
    public int PlantLane;
    public Position position;
    public Collider[] colliders;
    private Clip clip;
    private Game game;
    private double setFPS = 60;
    PlantType activePlantingBrush = PlantType.None;

    // Set of ArrayList
    // Use the zombie_units array
    // ArrayList<ArrayList<Zombie>> laneZombies;
    public ArrayList<ArrayList<Zombie>> Zombie_units;
    public ArrayList<Sun> activeSuns;
    public ArrayList<ArrayList<Pea>> PlantInField;

    // Set of Jlabel
    private JLabel timerLabel;
    JLabel NumOfSunBoard = new JLabel();
    JLabel sunScoreboard;

    // Set of image
    Image freezePeashooterImage;
    Image freezePeaImage;

    // Load zombie images
    Image normalZombieImage;
    Image coneHeadZombieImage;
    Image bucketHeadZombieImage;
    Image balloonZombieImage;

    // Set of imageicon
    JButton SunflowerButtton = new JButton();
    ImageIcon SunflowerCard = new ImageIcon("Image/Plants/Cards/SunflowerCard.png");
    JButton PeashooterButton = new JButton();
    ImageIcon PeashooterCard = new ImageIcon("Image/Plants/Cards/Peashootercard.png");
    JButton SnowPeashooterButton = new JButton();
    ImageIcon SnowPeashooterCard = new ImageIcon("Image/Plants/Cards/SnowPeaSeedCard.png");
    JButton WallnutButton = new JButton();
    ImageIcon WallnutCard = new ImageIcon("Image/Plants/Cards/Wall-nutCard.png");
    ImageIcon Peashootergif = new ImageIcon("Image/Plants/Fields/Peashooter.gif");
    ImageIcon Sunflowergif = new ImageIcon("Image/Plants/Fields/SunFlower.gif");
    ImageIcon originalImageIcon = new ImageIcon("Image/background/Frontyard.png");
    Image originalImage = originalImageIcon.getImage();
    private JLabel PeashooterGIF;
    private JLabel PlantGIF;
    // Scale factor <1 = zoom out, >1 = zoom in
    double zoomOutFactor = 1; // Adjust this factor as needed
    int scaledWidth = (int) (originalImage.getWidth(null) * zoomOutFactor);
    int scaledHeight = (int) (originalImage.getHeight(null) * zoomOutFactor);

    Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
    private JLabel label = new JLabel();

    // Set of Timer
    Timer redrawTimer;
    Timer advancerTimer;
    Timer sunProducer;
    Timer zombieProducer;

    public double getSetFPS() {
        return setFPS;
    }

    public void setSetFPS(double setFPS) {
        this.setFPS = setFPS;
    }

    private double setUPS = 60;

    public double getSetUPS() {
        return setUPS;
    }

    public void setSetUPS(double setUPS) {
        this.setUPS = setUPS;
    }

    private Thread gameThread;

    public Thread getGameThread() {
        return gameThread;
    }

    public void setGameThread(Thread gameThread) {
        this.gameThread = gameThread;
    }

    private MyMouseListener myMouseListener;

    public MyMouseListener getMyMouseListener() {
        return myMouseListener;
    }

    public void setMyMouseListener(MyMouseListener myMouseListener) {
        this.myMouseListener = myMouseListener;
    }

    // global value of Sun count
    private int Sun;

    public int getNumOfSun() {
        return Sun;
    }

    public JLabel setNumOfSun(int sunNum) {
        this.Sun = sunNum;
        this.NumOfSunBoard.setText(String.valueOf(sunNum));
        return NumOfSunBoard;
    }

    private volatile boolean isRunning = true;

    public GamePanel(Game game) {
        this.game = game;
        innitializeGamePanel();
        GamePanelMusic();
        this.start();
    }

    private int PlacedPeashoter;

    public void innitializeGamePanel() {
        setTitle("Plants VS Zombies");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(originalImage.getWidth(null), originalImage.getHeight(null));
        setResizable(true);

        // Load zombie images
        ImageIcon normalZombieimage = new ImageIcon("Image/Zombie/normalzombie.gif");
        ImageIcon coneHeadZombieImage = new ImageIcon("Image/Zombie/coneheadzombie.gif");
        ImageIcon bucketHeadZombieImage = new ImageIcon("Image/Zombie/bucketheadzombie.gif");
        ImageIcon balloonZombieImage = new ImageIcon("Image/Zombie/balloonzombie.gif");

        label.setIcon(scaledImageIcon);
        label.setBounds(0, 0, originalImage.getWidth(null), originalImage.getHeight(null));

        timerLabel = new JLabel("FPS = 0| UPS = 0| Time On Game = 0");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        timerLabel.setForeground(new Color(0x006600));
        timerLabel.setBounds(800, 20, 300, 30);
        add(timerLabel);
        add(label);
        JPanel ButtonPanel = new JPanel(new FlowLayout());
        SunflowerButtton.setIcon(SunflowerCard);
        SunflowerButtton.setBounds(50, 30, SunflowerCard.getIconWidth(), SunflowerCard.getIconHeight());
        SunflowerButtton.setBorder(BorderFactory.createLineBorder(new Color(0x818FB4), 3));
        SunflowerButtton.setFocusable(true);
        SunflowerButtton.setHorizontalAlignment(JButton.CENTER);
        SunflowerButtton.setVerticalAlignment(JButton.CENTER);
        SunflowerButtton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                int x = evt.getX();
                int y = evt.getY();
                Position position = new Position(x, y); // Assuming y is for Lane and x is for Box
                int lane = position.Lane(y);
                int box = position.Box(x);
                System.out.println("Sunflower Released at Lane: " + lane + ", Box: " + box);
            }
        });
        PeashooterButton.setIcon(PeashooterCard);
        PeashooterButton.setBounds(50, 215, PeashooterCard.getIconWidth(), PeashooterCard.getIconHeight());
        PeashooterButton.setBorder(BorderFactory.createLineBorder(new Color(0x818FB4), 3));
        PeashooterButton.setFocusable(true);
        PeashooterButton.setHorizontalAlignment(JButton.CENTER);
        PeashooterButton.setVerticalAlignment(JButton.CENTER);
        PeashooterGIF = new JLabel();
        // PeashooterButton.setAction(new AbstractAction() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // activePlantingBrush = PlantType.Peashooter;
        // }
        // });
        peashooterButtonMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Code to get an image when the button is clicked
                // PeashooterGIF != null
                PlacedPeashoter = 0;
                PeashooterGIF.setIcon(Peashootergif);
                PeashooterGIF.setBounds(e.getXOnScreen() - Peashootergif.getIconWidth() / 2,
                        e.getYOnScreen() - Peashootergif.getIconHeight() / 2, Peashootergif.getIconWidth(),
                        Peashootergif.getIconHeight());
                repaint();

                // Change the MouseListener dynamically
                PeashooterButton.removeMouseListener(peashooterButtonMouseListener); // Remove the current MouseListener
                label.addMouseListener(new LabelMouseListener()); // Add a new MouseListener
                label.addMouseMotionListener(new LabelMouseMotionListener()); // Add a new MouseMotionListener
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Handle mouse press event
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Code to get an image when the button is clicked
                // PeashooterGIF != null

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Handle mouse enter event
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Code to move the GIF when the mouse leaves the button

            }
        };
        PeashooterButton.addMouseListener(peashooterButtonMouseListener);

        PeashooterButton.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Handle mouse drag event
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        SnowPeashooterButton.setIcon(SnowPeashooterCard);
        SnowPeashooterButton.setBounds(50, 400, SnowPeashooterCard.getIconWidth(), SnowPeashooterCard.getIconHeight());
        SnowPeashooterButton.setBorder(BorderFactory.createLineBorder(new Color(0x818FB4), 3));
        SnowPeashooterButton.setFocusable(true);
        SnowPeashooterButton.setHorizontalAlignment(JButton.CENTER);
        SnowPeashooterButton.setVerticalAlignment(JButton.CENTER);
        SnowPeashooterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                int x = evt.getX();
                int y = evt.getY();
                Position position = new Position(x, y); // Assuming y is for Lane and x is for Box
                int lane = position.Lane(y);
                int box = position.Box(x);
                System.out.println("Peashooter Released at Lane: " + lane + ", Box: " + box);
            }
        });
        WallnutButton.setIcon(WallnutCard);
        WallnutButton.setBounds(50, 585, WallnutCard.getIconWidth(), WallnutCard.getIconHeight());
        WallnutButton.setBorder(BorderFactory.createLineBorder(new Color(0x818FB4), 3));
        WallnutButton.setFocusable(true);
        WallnutButton.setHorizontalAlignment(JButton.CENTER);
        WallnutButton.setVerticalAlignment(JButton.CENTER);
        WallnutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                int x = evt.getX();
                int y = evt.getY();
                Position position = new Position(x, y); // Assuming y is for Lane and x is for Box
                int lane = position.Lane(y);
                int box = position.Box(x);
                System.out.println("Peashooter Released at Lane: " + lane + ", Box: " + box);
            }
        });
        label.add(SnowPeashooterButton);
        label.add(WallnutButton);
        label.add(SunflowerButtton);
        label.add(PeashooterButton);
        getContentPane().add(label);
        pack();
        setLocationRelativeTo(null);
        // Sun making
        // Sun count

        ImageIcon sunshow = new ImageIcon("Image/Picture holder.PNG");
        JLabel sunshowLabel = new JLabel(sunshow);
        sunshowLabel.setBounds(100, 0, 470, 100);
        label.add(sunshowLabel);

        int InitialnumOfSun = 150;
        setNumOfSun(InitialnumOfSun);
        NumOfSunBoard.setFont(new Font("Arial", Font.BOLD, 20));
        NumOfSunBoard.setForeground(new Color(0, 0, 0));
        NumOfSunBoard.setBounds(218, 0, 1400, 166);
        sunshowLabel.add(NumOfSunBoard);

        activeSuns = new ArrayList<>();
        // 6 seconds 1 sun
        Timer sunProducer = new Timer(600000, (ActionEvent e) -> {
            System.out.println("Add sun");
            Random rnd = new Random();
            // Game Field from 313 = minX to maxX = 1270 or 1273, yMin = 85 to 650= maxY
            // This is the range x and y of Field
            Sun newSun = new Sun(this, rnd.nextInt(887) + 313, 0, rnd.nextInt(300) + 350);
            activeSuns.add(newSun);
            label.add(newSun);

            Timer sunTimer = new Timer(60, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    // Update the position of each sun in activeSuns
                    Iterator<Sun> iterator = activeSuns.iterator();
                    while (iterator.hasNext()) {
                        Sun sun = iterator.next();
                        sun.FallSun();
                    }
                }
            });
            // Start the timer only if it's not already running
            if (!sunTimer.isRunning()) {
                sunTimer.start();
            }
        });
        sunProducer.start();

        // Zombie producer
        /*
         * zombieProducer = new Timer(7000, (ActionEvent e) -> {
         * Random rnd = new Random();
         * int l = rnd.nextInt(5);
         * int t = rnd.nextInt(100);
         * Zombie z = null;
         * String[] allZombieTypes = {"NormalZombie", "ConeHeadZombie",
         * "BucketHeadZombie", "BalloonZombie"};
         * int randomZombieIndex = rnd.nextInt(allZombieTypes.length);
         * String selectedZombieType = allZombieTypes[randomZombieIndex];
         * z = Zombie.getZombie(selectedZombieType, GamePanel.this, l);
         * Zombie_units.get(l).add(z);
         * });
         * zombieProducer.start();
         */

        // Manage the zombie and plant in 5 line

        Zombie_units = new ArrayList<>();
        Zombie_units.add(new ArrayList<>()); // line 1
        Zombie_units.add(new ArrayList<>()); // line 2
        Zombie_units.add(new ArrayList<>()); // line 3
        Zombie_units.add(new ArrayList<>()); // line 4
        Zombie_units.add(new ArrayList<>()); // line 5

        PlantInField = new ArrayList<>();
        PlantInField.add(new ArrayList<>()); // line 1
        PlantInField.add(new ArrayList<>()); // line 2
        PlantInField.add(new ArrayList<>()); // line 3
        PlantInField.add(new ArrayList<>()); // line 4
        PlantInField.add(new ArrayList<>()); // line 5

        /*
         * colliders = new Collider[45];
         * for (int i = 0; i < 45; i++) {
         * Collider a = new Collider();
         * a.setLocation(44 + (i % 9) * 100, 109 + (i / 9) * 120);
         * a.setAction(new PlantActionListener((i % 9), (i / 9)));
         * colliders[i] = a;
         * label.add(a);
         * }
         */

        // Draw all of components after a very short a mount of time or make it to move
        /*
         * redrawTimer = new Timer(25, (ActionEvent e) -> {
         * repaint();
         * });
         * redrawTimer.start();
         */

        /*
         * advancerTimer = new Timer(60, (ActionEvent e) -> advance());
         * advancerTimer.start();
         */

    }

    private void advance() {

        for (int i = 0; i < 5; i++) {
            /*
             * for (Zombie z : laneZombies.get(i)) {
             * z.advance();
             * }
             */
            for (int j = 0; j < PlantInField.get(i).size(); j++) {
                Pea p = PlantInField.get(i).get(j);
                p.advance();
            }
        }

        for (int i = 0; i < activeSuns.size(); i++) {
            activeSuns.get(i).FallSun();
        }

    }

    // Make the jpanel to remove the sun after being destroy
    public void removeSun(Sun sun) {
        activeSuns.remove(sun);
        label.remove(sun);
        label.revalidate();
        label.repaint();
    }

    public void GamePanelMusic() {
        File soundIngameFile = new File("Sound/IngameSound.wav");
        MusicStart(soundIngameFile);
    }

    public void start() {
        gameThread = new Thread(this) {
        };
        gameThread.start();
    }

    public void pauseGame() {
        isRunning = false;
    }

    public void resumeGame() {
        isRunning = true;
    }

    @Override
    public void run() {
        double lastTimeCheck = System.currentTimeMillis();
        double timePerFrame = Math.pow(10, 9) / setFPS;
        double timePerUpdate = Math.pow(10, 9) / setUPS;
        double lastTimeFPS = System.nanoTime();
        double lastTimeUPS = System.nanoTime();
        double startTime = System.currentTimeMillis();
        double countTime = 0;
        int updateGame = 0;
        int frame = 0;
        double now;
        while (true) {
            now = System.nanoTime();
            countTime = System.currentTimeMillis();
            // Render
            if (now - lastTimeFPS >= timePerFrame) {
                frame++;
                lastTimeFPS = System.nanoTime();
                SwingUtilities.invokeLater(() -> repaint());
            }
            // Update
            if (now - lastTimeUPS >= timePerUpdate) {
                updateGame++;
                lastTimeUPS = System.nanoTime();
            }
            // FPS Counter & UPS Counter
            if (System.currentTimeMillis() - lastTimeCheck >= 1000) {
                String rs = "FPS: " + frame + "| UPS: " + updateGame + "| Time On Game: "
                        + (int) (countTime - startTime) / 1000 + " s";
                SwingUtilities.invokeLater(() -> timerLabel.setText(rs));
                updateGame = 0;
                frame = 0;
                lastTimeCheck = System.currentTimeMillis();
            }
        }
    }

    void initializeInput() {
        myMouseListener = new MyMouseListener(this);
        this.addMouseListener(myMouseListener);
        this.addMouseMotionListener(myMouseListener);
        requestFocus();
    }

    @Override
    public void mouseClicked(int x, int y) {
    }

    @Override
    public void mousePressed(int x, int y) {
    }

    @Override
    public void mouseOver(int x, int y) {
    }

    @Override
    public void mouseReleased(int x, int y) {
        Position position = new Position(y, x); // Assuming y is for Lane and x is for Box
        int lane = position.Lane(y);
        int box = position.Box(x);
        System.out.println("Released at Lane: " + lane + ", Box: " + box);
    }

    @Override
    public void mouseEntered(int x, int y) {
    }

    @Override
    public void mouseExited(int x, int y) {
    }

    @Override
    public void mouseDragged(int x, int y) {
    }

    int mouseX, mouseY;

    @Override
    public void mouseMoved(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    // Make Plant

    class PlantActionListener implements ActionListener {

        int x, y;

        public PlantActionListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (activePlantingBrush == PlantType.Sunflower) {
                if (getNumOfSun() >= 50) {
                    colliders[x + y * 9].setPlant(new Sunflower(GamePanel.this, x, y));
                    setNumOfSun(getNumOfSun() - 50);
                }
            }
            if (activePlantingBrush == PlantType.Peashooter) {
                if (getNumOfSun() >= 100) {
                    colliders[x + y * 9].setPlant(new Peashooter(GamePanel.this, x, y));
                    setNumOfSun(getNumOfSun() - 100);
                }
            }
            activePlantingBrush = PlantType.None;
        }
    }

    // Finding correct formular in process

    /*
     * @Override
     * public void paint(Graphics graphic) {
     * super.paint(graphic);
     * double zoomOutFactor = 1.1618;
     * // Convert ImageIcon to Image
     * Image bgImage = background_image.getImage();
     * /*
     * Image peashooterImage = Sunflowergif.getImage();
     * Image sunflowerImage = Peashootergif.getImage();
     * Image PeaBullet = PeaImage.getImage();
     */
    /*
     * Scale the backgruond image
     * AffineTransform at = AffineTransform.getScaleInstance(1 / zoomOutFactor, 1 /
     * zoomOutFactor);
     * ((Graphics2D) graphic).drawImage(bgImage, at, this);
     * 
     * /*
     * Plant Generation
     * for (int i = 0; i < 45; i++) {
     * Collider c = colliders[i];
     * if (c.assignedPlant != null) {
     * Plant WhichPlant = c.assignedPlant;
     * if (WhichPlant instanceof Peashooter) {
     * graphic.drawImage(peashooterImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120,
     * null);
     * }
     * if (WhichPlant instanceof Sunflower) {
     * graphic.drawImage(sunflowerImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120,
     * null);
     * }
     * }
     * }
     * 
     * // Bullet generation for normal Pea
     * for (int i = 0; i < 5; i++) {
     * // For zombie not now
     * /*
     * for (Zombie z : laneZombies.get(i)) {
     * if (z instanceof NormalZombie) {
     * g.drawImage(normalZombieImage, z.posX, 109 + (i * 120), null);
     * } else if (z instanceof ConeHeadZombie) {
     * g.drawImage(coneHeadZombieImage, z.posX, 109 + (i * 120), null);
     * }
     * }
     * 
     * for (int j = 0; j < PlantInField.get(i).size(); j++) {
     * Pea p = PlantInField.get(i).get(j);
     * if (p instanceof Pea) {
     * graphic.drawImage(PeaBullet, p.posX, 130 + (i * 120), null);
     * }
     * }
     * }
     * }
     */

    private class LabelMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("Click!");
            int x = e.getXOnScreen();
            int y = e.getYOnScreen();
            Position position = new Position(x, y);
            System.out.printf("x = %d, y = %d", x, y);
            System.out.println();
            position = new Position(x, y);
            int box = position.Box(x);// get the box number
            int lane = position.Lane(y);// get the Lane number
            System.out.printf("Box is: %d. Lane is: %d", box, lane);
            System.out.println();

            if (PeashooterGIF != null && PlacedPeashoter == 0) {
                // Need a check if there is a plant or not
                PlantBox = position.BoxDraw(x);
                PlantLane = position.LaneDraw(y);
                PlantGIF = new JLabel();
                PlantGIF.setIcon(Peashootergif);
                PlantGIF.setBounds(PlantBox, PlantLane, Peashootergif.getIconWidth(), Peashootergif.getIconHeight());
                label.add(PlantGIF);
                PlacedPeashoter = 1;
                label.remove(PeashooterGIF);
                repaint();
            }
            if (PlacedPeashoter == 1) {
                PeashooterButton.addMouseListener(peashooterButtonMouseListener);
                label.removeMouseListener(this);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateGIFPosition(e);
        }
    }

    private class LabelMouseMotionListener implements MouseMotionListener {
        @Override
        public void mouseMoved(MouseEvent e) {
            updateGIFPosition(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // Code for the new MouseMotionListener on the label
        }
    }

    private void updateGIFPosition(MouseEvent e) {
        // Code for updating the position of the GIF
        if (PeashooterGIF != null && PlacedPeashoter == 0) {
            PeashooterGIF.setIcon(Peashootergif);
            PeashooterGIF.setBounds(
                    e.getXOnScreen(),
                    e.getYOnScreen(),
                    Peashootergif.getIconWidth(),
                    Peashootergif.getIconHeight());
            label.add(PeashooterGIF);
            repaint();
        } else if (PlacedPeashoter == 1) {
            label.remove(PeashooterGIF);
            repaint();
        }
    }
}