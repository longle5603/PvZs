package Game;

import static GUI.GameSFX.Button.*;
import static GUI.GameSFX.Music.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import GUI.GameEnd.GameOverNotification;
import GUI.GameEnd.GameWinnerNotification;
import GameElement.Collider;
import GameElement.LawnMower;
import GameElement.Position;
import InputForGame.Mouse;
import InputForGame.MyMouseListener;
import Plant.Pea;
import Plant.Peashooter;
import Plant.Plant;
import Plant.SnowPeashooter;
import Plant.Snowpea;
import Plant.Sunflower;
import Plant.wallNut;
import Sun.Sun;
import Zombie.Zombie;

public class GamePanel extends JFrame implements Runnable, Mouse {
    enum PlantType {
        None,
        Sunflower,
        Peashooter,
        Wallnut,
        SnowPeashooter,
    }

    public static final String Zombie_units = null;

    public Game gm = new Game(this);
    private JButton pauseButton;

    private volatile boolean isPaused = false;
    public int PlantBox;
    public int PlantLane;
    public int PosOfBox;
    public int PosOflane;
    public Position position;
    public Collider[] colliders;
    private Clip clip;
    private double setFPS = 60;
    private long lastFrameTime = System.nanoTime();
    PlantType activePlantingBrush = PlantType.None;

    // Set of ArrayList
    // Use the zombie_units array
    // ArrayList<ArrayList<Zombie>> laneZombies;
    private int ZombDieToWin = 50;
    public ArrayList<Sun> activeSuns;
    public ArrayList<LawnMower> lawnMowers = new ArrayList<>();
    // Set of Jlabel
    private JLabel timerLabel;
    private JLabel zombieDieLabel;
    JLabel NumOfSunBoard = new JLabel();
    JLabel sunScoreboard;

    // Set of image
    Image freezePeashooterImage;
    Image freezePeaImage;

    // Load zombie images

    // Set of imageicon
    public static boolean isGameRunning = true;
    JButton Pause = new JButton();
    ImageIcon pauseicon = new ImageIcon("Image/background/pause.png");
    ImageIcon resumeicon = new ImageIcon("Image/background/ressume.png");
    JButton SunflowerButton = new JButton();
    ImageIcon SunflowerCard = new ImageIcon("Image/Plants/Cards/SunflowerCard.png");
    JButton PeashooterButton = new JButton();
    ImageIcon PeashooterCard = new ImageIcon("Image/Plants/Cards/Peashootercard.png");
    JButton SnowPeashooterButton = new JButton();
    ImageIcon SnowPeashooterCard = new ImageIcon("Image/Plants/Cards/SnowPeaSeedCard.png");
    JButton WallnutButton = new JButton();
    ImageIcon WallnutCard = new ImageIcon("Image/Plants/Cards/Wall-nutCard.png");
    ImageIcon Wallnutgif = new ImageIcon("Image/Plants/Fields/wallnut.gif");
    ImageIcon Peashootergif = new ImageIcon("Image/Plants/Fields/Peashooter.gif");
    ImageIcon Sunflowergif = new ImageIcon("Image/Plants/Fields/SunFlower.gif");
    ImageIcon originalImageIcon = new ImageIcon("Image/background/Frontyard.png");
    ImageIcon PeaImage = new ImageIcon("Image/Plants/Fields/ProjectilePea.png");
    ImageIcon SnowPeaImage = new ImageIcon("Image/Plants/Fields/ProjectileSnowPea.png");
    ImageIcon SnowPeashootergif = new ImageIcon("Image/Plants/Fields/Snow-Pea.gif");
    Image originalImage = originalImageIcon.getImage();

    // Scale factor <1 = zoom out, >1 = zoom in
    double zoomOutFactor = 0.87; // Adjust this factor as needed
    int scaledWidth = (int) (originalImage.getWidth(null) * zoomOutFactor);
    int scaledHeight = (int) (originalImage.getHeight(null) * zoomOutFactor);

    Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
    public JLabel label = new JLabel();
    public Font ZombieDieFont = loadCustomFont("Fonts/House_Of_Terror.ttf", 20);

    // Set of Timer
    Timer redrawTimer;
    Timer advancerTimer;
    Timer sunProducer;
    Timer zombieProducer;
    Timer plantTimer;
    Timer bulletTimer;

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

    public GamePanel(Game game, int ZombDieToWin) {
        innitializeGamePanel();
        GamePanelMusic();
        this.start();
        this.ZombDieToWin = ZombDieToWin;
    }

    private int PlacedPeashoter;
    public int[] LaneTopLeft;
    public int[] BoxTopLeft;

    public void innitializeGamePanel() {
        setTitle("Plants VS Zombies");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(originalImage.getWidth(null), originalImage.getHeight(null));
        setResizable(true);

        // Load zombie images
        // Other code...
        Pause.setIcon(pauseicon);
        label.setIcon(scaledImageIcon);
        label.setBounds(0, 0, originalImage.getWidth(null), originalImage.getHeight(null));
        Pause.setBounds(1200, 20, pauseicon.getIconWidth(), pauseicon.getIconHeight());
        Pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame();
                togglePause();
            }
        });
        add(Pause);
        timerLabel = new JLabel("FPS = 0| UPS = 0| Time On Game = 0");
        timerLabel.setFont(ZombieDieFont);
        timerLabel.setForeground(new Color(0x006600));
        timerLabel.setBounds(500, 20, 300, 30);
        add(timerLabel);
        add(label);
        zombieDieLabel = new JLabel("ZOMBIE DIE: 0");
        zombieDieLabel.setFont(ZombieDieFont);
        zombieDieLabel.setForeground(Color.red);
        zombieDieLabel.setBounds(900, 20, 300, 30);
        label.add(zombieDieLabel);
        LawnMower lawnMower1 = new LawnMower(this, 200, 125);
        label.add(lawnMower1);
        lawnMower1.setBounds(200, 125, lawnMower1.getWidth(), lawnMower1.getHeight());
        LawnMower lawnMower2 = new LawnMower(this, 200, 255);
        label.add(lawnMower2);
        lawnMower2.setBounds(200, 255, lawnMower2.getWidth(), lawnMower2.getHeight());
        LawnMower lawnMower3 = new LawnMower(this, 200, 385);
        label.add(lawnMower3);
        lawnMower3.setBounds(200, 385, lawnMower3.getWidth(), lawnMower3.getHeight());
        LawnMower lawnMower4 = new LawnMower(this, 200, 505);
        label.add(lawnMower4);
        lawnMower4.setBounds(200, 505, lawnMower4.getWidth(), lawnMower4.getHeight());
        LawnMower lawnMower5 = new LawnMower(this, 200, 630);
        label.add(lawnMower5);
        lawnMower5.setBounds(200, 630, lawnMower5.getWidth(), lawnMower5.getHeight());
        lawnMowers.add(lawnMower1);
        lawnMowers.add(lawnMower2);
        lawnMowers.add(lawnMower3);
        lawnMowers.add(lawnMower4);
        lawnMowers.add(lawnMower5);

        SunflowerButton.setIcon(SunflowerCard);
        SunflowerButton.setBounds(50, 30, SunflowerCard.getIconWidth(), SunflowerCard.getIconHeight());
        SunflowerButton.setFocusable(true);
        SunflowerButton.setHorizontalAlignment(JButton.CENTER);
        SunflowerButton.setVerticalAlignment(JButton.CENTER);
        SunflowerButton.addActionListener((ActionEvent e) -> {
            activePlantingBrush = PlantType.Sunflower;
        });

        PeashooterButton.setIcon(PeashooterCard);
        PeashooterButton.setBounds(50, 215, PeashooterCard.getIconWidth(), PeashooterCard.getIconHeight());
        PeashooterButton.setFocusable(true);
        PeashooterButton.setHorizontalAlignment(JButton.CENTER);
        PeashooterButton.setVerticalAlignment(JButton.CENTER);
        PeashooterButton.addActionListener((ActionEvent e) -> {
            activePlantingBrush = PlantType.Peashooter;
        });

        SnowPeashooterButton.setIcon(SnowPeashooterCard);
        SnowPeashooterButton.setBounds(50, 400, SnowPeashooterCard.getIconWidth(), SnowPeashooterCard.getIconHeight());
        SnowPeashooterButton.setFocusable(true);
        SnowPeashooterButton.setHorizontalAlignment(JButton.CENTER);
        SnowPeashooterButton.setVerticalAlignment(JButton.CENTER);
        SnowPeashooterButton.addActionListener((ActionEvent e) -> {
            activePlantingBrush = PlantType.SnowPeashooter;
        });
        WallnutButton.setIcon(WallnutCard);
        WallnutButton.setBounds(50, 585, WallnutCard.getIconWidth(), WallnutCard.getIconHeight());
        WallnutButton.setFocusable(true);
        WallnutButton.setHorizontalAlignment(JButton.CENTER);
        WallnutButton.setVerticalAlignment(JButton.CENTER);
        WallnutButton.addActionListener((ActionEvent e) -> {
            activePlantingBrush = PlantType.Wallnut;
        });

        label.add(SnowPeashooterButton);
        label.add(WallnutButton);
        label.add(SunflowerButton);
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
        // Place to set initial number of sun
        int InitialnumOfSun = 150;
        setNumOfSun(InitialnumOfSun);
        NumOfSunBoard.setFont(new Font("Arial", Font.BOLD, 20));
        NumOfSunBoard.setForeground(new Color(0, 0, 0));
        NumOfSunBoard.setBounds(218, 0, 1400, 166);
        sunshowLabel.add(NumOfSunBoard);

        activeSuns = new ArrayList<>();
        // 6 seconds 1 sun
        Timer sunProducer = new Timer(6000, (ActionEvent e) -> {
            System.out.println("Add sun");
            Random rnd = new Random();
            // Game Field from 313 = minX to maxX = 1270 or 1273, yMin = 85 to 650= maxY
            // This is the range x and y of Field
            Sun newSun = new Sun(this, rnd.nextInt(887) + 313, 0, rnd.nextInt(300) + 350);
            activeSuns.add(newSun);
            label.add(newSun);
        });
        sunProducer.start();
        advancerTimer = new Timer(60, (ActionEvent e) -> advance());
        advancerTimer.start();

        // Zombie producer
        zombieProducer = new Timer(15000, (ActionEvent e) -> {
            Random rnd = new Random();
            int l = rnd.nextInt(5);
            int y = laneSpawn(l);
            int x = 900;
            Zombie z = null;
            String[] allZombieTypes = { "NormalZombie", "ConeHeadZombie",
                    "BucketHeadZombie", "BalloonZombie" };
            int randomZombieIndex = rnd.nextInt(allZombieTypes.length);
            String selectedZombieType = allZombieTypes[randomZombieIndex];
            z = Zombie.getZombie(selectedZombieType, GamePanel.this, l);
            System.out.printf("Add z at land %d", l);
            gm.Zombie_units.get(l).add(z);
            label.add(z);
            z.setBounds(x, y, z.getWidth(), z.getHeight());
        });
        zombieProducer.start();

        // Set the box where plant will be add and action in each box for all field
        colliders = new Collider[45];
        LaneTopLeft = new int[] { 121, 250, 381, 511, 646 };
        BoxTopLeft = new int[] { 315, 442, 543, 654, 755, 871, 964, 1070, 1175 };
        for (int i = 0; i < 45; i++) {
            int Box = i % 9;
            int Land = i / 9;
            Position position = new Position(BoxTopLeft[Box], LaneTopLeft[Land]);
            PosOfBox = position.BoxDraw(BoxTopLeft[Box]);
            PosOflane = position.LaneDraw(LaneTopLeft[Land]);
            Collider a = new Collider();
            a.setLocation(PosOfBox, PosOflane);
            // i % 9 = Box, i / 9 = Lane
            a.setAction(new PlantActionListener(Box, Land));
            colliders[i] = a;
            label.add(a);
        }

        // Update bullet plant
        redrawTimer = new Timer(1000, (ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> repaint());
        });
        redrawTimer.start();
    }

    public void removeDieZombie(Zombie zom) {
        label.remove(zom);
        label.revalidate();
    }

    private void togglePause() {
        if (isGameRunning) {
            resumeGame();
            Pause.setIcon(pauseicon);
        } else {
            pauseGame();
            Pause.setIcon(resumeicon);
        }
        isGameRunning = !isGameRunning;
    }

    // Trigger advanced method in each class
    private void advance() {

        for (int i = 0; i < gm.Zombie_units.size(); i++) {

            for (Zombie z : gm.Zombie_units.get(i)) {
                int newX = z.posX - z.speed;
                int y = laneSpawn(z.myLane);
                z.setPosX(newX);
                z.setBounds(newX, y, z.getWidth(), z.getHeight());
                // System.out.println("Zombie is at " + newX + " and speed is " + z.getSpeed());
                z.advance();
            }

            for (int j = 0; j < gm.PeaInField.get(i).size(); j++) {
                Pea p = gm.PeaInField.get(i).get(j);
                p.advance();
            }

            for (int j = 0; j < gm.SnowPeaInField.get(i).size(); j++) {
                Snowpea Sp = gm.SnowPeaInField.get(i).get(j);
                Sp.advance();
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
        isGameRunning = false;
    }

    public void resumeGame() {
        isGameRunning = true;
    }

    private int frame = 0;
    private double lastTimeFPS = System.nanoTime();
    private double now;
    private int updateGame = 0;
    private double lastTimeUPS = System.nanoTime();
    private double lastTimeCheck = System.currentTimeMillis();
    private double countTime = 0;

    @Override
    public void run() {

        double timePerFrame = Math.pow(10, 9) / setFPS;
        double timePerUpdate = Math.pow(10, 9) / setUPS;

        double startTime = System.currentTimeMillis();

        while (isGameRunning) {
            now = System.nanoTime();
            countTime = System.currentTimeMillis();
            // Render
            if (now - lastTimeFPS >= timePerFrame) {
                frame++;
                lastTimeFPS = System.nanoTime();
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
            this.updateZombieDielabel(Pea.zombieDie);
            if (Pea.zombieDie == ZombDieToWin) {
                stopGameThread();
                GameWinnner(this);
            }
        }
    }

    public void updateZombieDielabel(int x) {
        zombieDieLabel.setText("ZOMBIE DIE: " + x);
    }

    public void GameWinnner(GamePanel gamePanel) {
        MusicStop();
        GameWinnerNotification gameWinnerNotification = new GameWinnerNotification();
    }

    public void GameOver(GamePanel gamePanel) {
        stopGameThread();
        MusicStop();
        GameOverNotification gameOverNotification = new GameOverNotification();
    }

    public void stopGameThread() {
        isGameRunning = false;
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
                    // Set place that bullet fire
                    colliders[x + y * 9].setPlant(new Sunflower(GamePanel.this, x, y));
                    // new Peashooter(GamePanel.this, x, y) position where the pea bullet fire
                    setNumOfSun(getNumOfSun() - 50);
                }
            }

            else if (activePlantingBrush == PlantType.Peashooter) {
                if (getNumOfSun() >= 100) {
                    // Set place that bullet fire
                    colliders[x + y * 9].setPlant(new Peashooter(GamePanel.this, x, y));
                    // new Peashooter(GamePanel.this, x, y) position where the pea bullet fire
                    setNumOfSun(getNumOfSun() - 100);
                }
            } else if (activePlantingBrush == PlantType.SnowPeashooter) {
                if (getNumOfSun() >= 150) {
                    // Set place that bullet fire
                    colliders[x + y * 9].setPlant(new SnowPeashooter(GamePanel.this, x, y));
                    // new Peashooter(GamePanel.this, x, y) position where the pea bullet fire
                    setNumOfSun(getNumOfSun() - 150);
                }
            } else if (activePlantingBrush == PlantType.Wallnut) {
                if (getNumOfSun() >= 50) {
                    // Set place that bullet fire
                    colliders[x + y * 9].setPlant(new wallNut(GamePanel.this, x, y));
                    // new Peashooter(GamePanel.this, x, y) position where the pea bullet fire
                    setNumOfSun(getNumOfSun() - 50);
                }
            }
            activePlantingBrush = PlantType.None;
        }
    }

    // Finding correct formular in process
    private int DrawBox;
    private int DrawLane;

    @Override
    public void paint(Graphics graphic) {
        super.paint(graphic);
        // Plant Generation
        for (int i = 0; i < 45; i++) {
            int Box = i % 9;
            int Land = i / 9;
            Position position = new Position(BoxTopLeft[Box], LaneTopLeft[Land]);
            DrawBox = position.BoxDraw(BoxTopLeft[Box]);
            DrawLane = position.LaneDraw(LaneTopLeft[Land]);
            Collider c = colliders[i];
            if (c.assignedPlant != null) {
                Plant WhichPlant = c.assignedPlant;
                if (WhichPlant instanceof Peashooter) {
                    // Draw ImageIcon as Image
                    Peashootergif.paintIcon(this, graphic, DrawBox, DrawLane);
                } else if (WhichPlant instanceof Sunflower) {
                    // Draw ImageIcon as Image
                    Sunflowergif.paintIcon(this, graphic, DrawBox, DrawLane);
                } else if (WhichPlant instanceof SnowPeashooter) {
                    // Draw ImageIcon as Image
                    SnowPeashootergif.paintIcon(this, graphic, DrawBox, DrawLane);
                } else if (WhichPlant instanceof wallNut) {
                    // Draw ImageIcon as Image
                    Wallnutgif.paintIcon(this, graphic, DrawBox, DrawLane);
                }
            }

        }
        // Bullet generation for normal Pea
        drawPeaBullets(graphic);
    }

    public void drawPeaBullets(Graphics graphic) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < gm.PeaInField.get(i).size(); j++) {
                Pea p = gm.PeaInField.get(i).get(j);

                // Check if the object is an instance of Pea
                if (p instanceof Pea) {
                    // Draw ImageIcon as Image for Pea
                    if (i == 2 || i == 3 || i == 4) {
                        PeaImage.paintIcon(this, graphic, p.posX + 40, 107 + (i * 137));
                    } else {
                        PeaImage.paintIcon(this, graphic, p.posX + 40, 130 + (i * 140));
                    }
                }
            }
        }

        // Now, handle Snowpea separately (outside the inner loop)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < gm.SnowPeaInField.get(i).size(); j++) {
                Snowpea Sp = gm.SnowPeaInField.get(i).get(j);

                // Check if the object is an instance of Snowpea
                if (Sp instanceof Snowpea) {
                    // Draw ImageIcon as Image for Snowpea
                    if (i == 2 || i == 3 || i == 4) {
                        SnowPeaImage.paintIcon(this, graphic, Sp.posX + 40, 107 + (i * 137)); // Use Sp instead of p
                    } else {
                        SnowPeaImage.paintIcon(this, graphic, Sp.posX + 40, 130 + (i * 140)); // Use Sp instead of p
                    }
                }
            }
        }
    }

    public int laneSpawn(int x) {
        if (x == 0)
            return 70;
        else if (x == 1) {
            return 200;
        } else if (x == 2) {
            return 335;
        } else if (x == 3) {
            return 455;
        } else
            return 590;
    }
}