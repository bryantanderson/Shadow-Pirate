import bagel.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 1, 2022
 *
 * @author Bryant Anderson Ciputra
 */
public class ShadowPirate extends AbstractGame{
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "ShadowPirate";
    private final Image LEVEL_0_BACKGROUND = new Image("res/background0.png");
    private final Image LEVEL_1_BACKGROUND = new Image("res/background1.png");
    private final static String WORLD_FILE_0 = "res/level0.csv";
    private final static String WORLD_FILE_1 = "res/level1.csv";
    private final static String START_MESSAGE = "PRESS SPACE TO START";
    private final static String LEVEL_COMPLETE_MESSAGE = "LEVEL COMPLETE!";
    private final static String STAGE_0_INSTRUCTION_MESSAGE = "USE ARROW KEYS TO FIND LADDER";
    private final static String STAGE_1_INSTRUCTION_MESSAGE = "FIND THE TREASURE";
    private final static String END_MESSAGE = "GAME OVER";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";
    private final static String ATTACK_MESSAGE = "PRESS S TO ATTACK";

    private final static int INSTRUCTION_OFFSET = 70;
    private final static int FONT_SIZE = 55;
    private final static int FONT_Y_POS = 402;
    private final static double REFRESH_RATE = 60/1000.0;
    private final static int LEVEL_COMPLETE_DURATION = 3000;
    private final Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private final static ArrayList<Block> blocks = new ArrayList<Block>();
    private final static ArrayList<Pirate> pirates = new ArrayList<Pirate>();
    private final static ArrayList<Bomb> bombs = new ArrayList<Bomb>();

    private Sailor sailor;
    private Blackbeard blackbeard;
    private Treasure treasure;
    private boolean gameEnd;
    private boolean stageZeroWin;
    private boolean stageOneWin;
    private boolean stageZeroRunning;
    private boolean stageOneRunning;
    private boolean finishedLoadingScreen = false;
    private int topLeftX;
    private int topLeftY;
    private int bottomRightX;
    private int bottomRightY;
    private int counter = 0;
    private boolean haveReadStageOneInfo = false;

    public ShadowPirate(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        readCSV(WORLD_FILE_0);
        stageZeroWin = false;
        stageOneWin = false;
        stageZeroRunning = false;
        stageOneRunning = false;
        gameEnd = false;
    }

    /**
     * Entry point for program
     */
    public static void main(String[] args){
        ShadowPirate game = new ShadowPirate();
        game.run();
    }

    /**
     * Method used to read file and create objects
     */
    private void readCSV(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;
            if ((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Sailor")){
                    sailor = new Sailor(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
            }

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Block")){
                    blocks.add(new Block(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Pirate")){
                    pirates.add(new Pirate(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Elixir")){
                    sailor.setElixir(new Elixir(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Sword")){
                    sailor.setSword(new Sword(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Potion")){
                    sailor.setPotion(new Potion(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Block") && stageZeroWin) {
                    bombs.add(new Bomb(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Treasure")) {
                    treasure = new Treasure(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
                if (sections[0].equals("Blackbeard")) {
                    blackbeard = new Blackbeard(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
                // read in the level bounds
                if (sections[0].equals("TopLeft")){
                    topLeftX = Integer.parseInt(sections[1]);
                    topLeftY = Integer.parseInt(sections[2]);
                }
                if (sections[0].equals("BottomRight")) {
                    bottomRightX = Integer.parseInt(sections[1]);
                    bottomRightY = Integer.parseInt(sections[2]);
                }
                // send the coordinates of the level bounds to the Sailor
                sailor.setBorders(topLeftY, topLeftX, bottomRightY, bottomRightX);
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Performs a state update. Pressing escape key,
     * allows game to exit.
     */
    @Override
    public void update(Input input){
        if (!stageZeroRunning && !stageZeroWin){
            drawStartScreen(input);
        } else if (!stageOneRunning && stageZeroWin) {
            // once stage zero is won, load in the level complete screen and start a timer for it
            if (!finishedLoadingScreen) {
                drawEndScreen(LEVEL_COMPLETE_MESSAGE);
                counter++;
                if ((counter / REFRESH_RATE) >= LEVEL_COMPLETE_DURATION) {
                    finishedLoadingScreen = true;
                }
            }
            if (finishedLoadingScreen) {
                drawStartScreen(input);
            }
        }
        // once stage one starts running, read in the world wile for stage 1
        if (stageOneRunning && !haveReadStageOneInfo) {
            pirates.removeAll(pirates);
            readCSV(WORLD_FILE_1);
            haveReadStageOneInfo = true;
        }
        // skip to level 1
        if (input.wasPressed(Keys.W)) {
            stageZeroWin = true;
            stageZeroRunning = false;
        }

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        if (gameEnd){
            drawEndScreen(END_MESSAGE);
        }

        if (stageOneWin) {
            drawEndScreen(WIN_MESSAGE);
        }

        // when game is running
        if (((stageZeroRunning && !stageZeroWin) || (stageOneRunning && !stageOneWin)) && !gameEnd) {
            if (stageZeroRunning) {
                LEVEL_0_BACKGROUND.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
            } else if (stageOneRunning) {
                LEVEL_1_BACKGROUND.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
            }

            sailor.update(input, blocks, bombs, pirates, blackbeard, treasure, stageZeroRunning);

            if (stageZeroRunning) {
                for (Block block : blocks) {
                    block.update();
                }
            }

            for (Pirate pirate : pirates) {
                pirate.update(isAtBoundary(pirate.getX(), pirate.getY()), blocks, bombs,
                        stageZeroRunning, sailor.getX(), sailor.getY());
            }
            if (stageOneRunning) {
                // treasure, items, bombs, and blackbeard only appear in stage 1
                for (Bomb bomb : bombs) {
                    bomb.update();
                }
                treasure.update();
                blackbeard.update(isAtBoundary(blackbeard.getX(), blackbeard.getY()),
                        blocks, bombs, stageZeroRunning, sailor.getX(), sailor.getY());
            }
            // sailor dies, game over
            if (sailor.isDead()){
                gameEnd = true;
            }

            if (sailor.hasWonStageZero()){
                stageZeroWin = true;
                stageZeroRunning = false;
            }

            if (sailor.hasWonStageOne()) {
                stageOneWin = true;
            }
        }
    }

    /**
     * Method used to draw the start screen instructions
     */
    private void drawStartScreen(Input input){
        FONT.drawString(START_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(START_MESSAGE)/2.0)),
                FONT_Y_POS);
        FONT.drawString(ATTACK_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(ATTACK_MESSAGE)/2.0)),
                FONT_Y_POS + INSTRUCTION_OFFSET);
        // prints the instruction message for stage Zero
        if (!stageZeroRunning && !stageZeroWin) {
            FONT.drawString(STAGE_0_INSTRUCTION_MESSAGE, (Window.getWidth() / 2.0 -
                            (FONT.getWidth(STAGE_0_INSTRUCTION_MESSAGE) / 2.0)),
                    (FONT_Y_POS + INSTRUCTION_OFFSET + INSTRUCTION_OFFSET));
            if (input.wasPressed(Keys.SPACE)){
                stageZeroRunning = true;
            }
        }
        // prints the instruction message for stage One
        if (stageZeroWin && !stageOneRunning) {
            FONT.drawString(STAGE_1_INSTRUCTION_MESSAGE, (Window.getWidth() / 2.0 -
                            (FONT.getWidth(STAGE_1_INSTRUCTION_MESSAGE) / 2.0)),
                    (FONT_Y_POS + INSTRUCTION_OFFSET + INSTRUCTION_OFFSET));
            if (input.wasPressed(Keys.SPACE)){
                stageOneRunning = true;
            }
        }
    }

    /**
     * Method used to draw end screen messages
     */
    private void drawEndScreen(String message){
        FONT.drawString(message, (Window.getWidth()/2.0 - (FONT.getWidth(message)/2.0)), FONT_Y_POS);
    }

    /**
     * Method used to check whether a coordinate is outside the level bounds
     */
    private boolean isAtBoundary(double x, double y) {
            return (x <= topLeftX) || (x >= bottomRightX) || (y <= topLeftY) || (y >= bottomRightY);

    }
}