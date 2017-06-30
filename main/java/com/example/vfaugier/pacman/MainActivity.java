package com.example.vfaugier.pacman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.vfaugier.pacman.menu.MenuActivity;
import com.example.vfaugier.pacman.square.Block;
import com.example.vfaugier.pacman.square.BlockType;
import com.example.vfaugier.pacman.square.Square;
import com.example.vfaugier.pacman.ghost.BlueGhost;
import com.example.vfaugier.pacman.ghost.Ghost;
import com.example.vfaugier.pacman.ghost.PinkGhost;
import com.example.vfaugier.pacman.ghost.RedGhost;
import com.example.vfaugier.pacman.ghost.YellowGhost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final int PAC_MAN_DEFAULT_LIFE_COUNT = 3;
    private static final int GAME_TIMER_TIC = 100;
    private static final int TIC_COUNT_MOVING_PAC_MAN = 4;
    private static final int TIC_COUNT_MOVING_GHOSTS = 5;
    private static final int PACMAN_MOVED_MESSAGE_ID = 0;
    private static final int RED_GHOST_MOVED_MESSAGE_ID = 1;
    private static final int PINK_GHOST_MOVED_MESSAGE_ID = 2;
    private static final int YELLOW_GHOST_MOVED_MESSAGE_ID = 3;
    private static final int BLUE_GHOST_MOVED_MESSAGE_ID = 4;
    private static final String FILE_ID_KEY = "fileId";

    private Context context;

    // sprites
    private Bitmap PAC_MAN_SPRITE;
    private Bitmap RED_GHOST_SPRITE;
    private Bitmap PINK_GHOST_SPRITE;
    private Bitmap YELLOW_GHOST_SPRITE;
    private Bitmap BLUE_GHOST_SPRITE;
    private Bitmap PAC_GUM_SPRITE;
    private Bitmap WALL_SPRITE;

    // map
    private GridView pacManMap;
    private int mapWidth;
    private int fileId;
    private SquareAdapter pacManMapAdapter;
    private int pacGumCount = 0;
    private int pacManLifes = PAC_MAN_DEFAULT_LIFE_COUNT;

    // entities model
    private PacMan pacMan;
    private RedGhost redGhost;
    private PinkGhost pinkGhost;
    private YellowGhost yellowGhost;
    private BlueGhost blueGhost;

    // entities view
    private Block pacManBlock;
    private Block redGhostBlock;
    private Block pinkGhostBlock;
    private Block yellowGhostBlock;
    private Block blueGhostBlock;

    // game timer
    private Timer gameTimer;
    private Handler gameHandler;
    private int ticCount = 0;
    private boolean isGameFrozen = false;

    // score
    private int score = 0;
    private TextView scoreTextView;

    // red ghost thread
    private Thread redGhostThread;
    private Handler redGhostHandler;

    // pink ghost thread
    private Thread pinkGhostThread;
    private Handler pinkGhostHandler;

    // yellow ghost thread
    private Thread yellowGhostThread;
    private Handler yellowGhostHandler;

    // blue ghost thread
    private Thread blueGhostThread;
    private Handler blueGhostHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        fileId = getIntent().getIntExtra(FILE_ID_KEY, R.raw.map0);

        // set sprites
        PAC_MAN_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.pacman);
        RED_GHOST_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.redghost);
        PINK_GHOST_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.pinkghost);
        YELLOW_GHOST_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.yellowghost);
        BLUE_GHOST_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.blueghost);
        PAC_GUM_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.pacgum);
        WALL_SPRITE = BitmapFactory.decodeResource(getResources(), R.drawable.wall);

        // set up attributes
        pacManMap = (GridView) findViewById(R.id.pacManMap);
        scoreTextView = (TextView) findViewById(R.id.score);
        scoreTextView.setText(Integer.toString(score));
    }

    @Override
    protected void onStart() {
        super.onStart();
        createMap(false);
        initGhosts();
        initGame();
    }

    private void createMap(boolean isReseting) {
        // create map string from resource file

        InputStream txtMapFile = getResources().openRawResource(fileId);
        pacManMapAdapter = createAdapterFromFile(txtMapFile, isReseting);

        pacManMap.setAdapter(pacManMapAdapter);
    }

    private void initGame() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                ticCount++;
                if (ticCount % TIC_COUNT_MOVING_GHOSTS == 0) {
                    redGhostThread.run();
                    pinkGhostThread.run();
                    yellowGhostThread.run();
                    blueGhostThread.run();
                }
                if (ticCount % TIC_COUNT_MOVING_PAC_MAN == 0) {
                    pacManNextStep();
                    gameHandler.sendEmptyMessage(PACMAN_MOVED_MESSAGE_ID);
                }
                if (ticCount % 4 == 0 && ticCount % 5 == 0) {
                    ticCount = 0;
                }
            }
        }, 0, GAME_TIMER_TIC);
        gameHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                verifyGameLostByPacMan();
                eatPacGum();
                pacManMap.setAdapter(pacManMapAdapter);
            }
        };
    }

    private SquareAdapter createAdapterFromFile(InputStream fileToRead, boolean isReseting) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileToRead));
        ArrayList<Square> squareList = new ArrayList<Square>();
        String line;
        Square square;

        // get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int position = 0;

        try {
            if ((line = reader.readLine()) != null) {
                mapWidth = Integer.parseInt(line.split(",")[1]);
                pacManMap.setNumColumns(mapWidth);
            }
            while ((line = reader.readLine()) != null) {
                for (String bloc: line.split(",")) {
                    switch (bloc) {
                        case "1":
                            pacManBlock = new Block(PAC_MAN_SPRITE, BlockType.PAC_MAN);
                            square = new Square(new ArrayList<Block>(Arrays.asList(pacManBlock)));
                            pacMan = new PacMan(position);
                            break;
                        case "2":
                            redGhostBlock = new Block(RED_GHOST_SPRITE, BlockType.GHOST);
                            square = new Square(new ArrayList<Block>(Arrays.asList(redGhostBlock)));
                            redGhost = new RedGhost(position);
                            break;
                        case "3":
                            yellowGhostBlock = new Block(YELLOW_GHOST_SPRITE, BlockType.GHOST);
                            square = new Square(new ArrayList<Block>(Arrays.asList(yellowGhostBlock)));
                            yellowGhost = new YellowGhost(position);
                            break;
                        case "4":
                            blueGhostBlock = new Block(BLUE_GHOST_SPRITE, BlockType.GHOST);
                            square = new Square(new ArrayList<Block>(Arrays.asList(blueGhostBlock)));
                            blueGhost = new BlueGhost(position);
                            break;
                        case "5":
                            pinkGhostBlock = new Block(PINK_GHOST_SPRITE, BlockType.GHOST);
                            square = new Square(new ArrayList<Block>(Arrays.asList(pinkGhostBlock)));
                            pinkGhost = new PinkGhost(position);
                            break;
                        case "6":
                            if (isReseting) {
                                square = (Square) pacManMapAdapter.getItem(position);

                                // remove old pac-man
                                Block deadPacManBlock = square.findBlockByType(BlockType.PAC_MAN);
                                if (deadPacManBlock != null) {
                                    square.removeBlockFromList(deadPacManBlock);
                                }

                                // remove old ghosts
                                Block oldGhostBlock = square.findBlockByType(BlockType.GHOST);
                                while (oldGhostBlock != null) {
                                    square.removeBlockFromList(oldGhostBlock);
                                    oldGhostBlock = square.findBlockByType(BlockType.GHOST);
                                }
                            } else {
                                square = new Square(new ArrayList<Block>(Arrays.asList(new Block(PAC_GUM_SPRITE, BlockType.PAC_GUM))));
                                pacGumCount++;
                            }
                            break;
                        case "7":
                            square = new Square(new ArrayList<Block>(Arrays.asList(new Block(WALL_SPRITE, BlockType.WALL))));
                            break;
                        default:
                            square = new Square(new ArrayList<Block>());
                            break;
                    }
                    squareList.add(square);
                    position ++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileToRead.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new SquareAdapter(this, squareList, screenWidth / mapWidth);
    }

    private void pacManNextStep() {
        movePacMan();
    }

    private void movePacMan() {
        int pacManPosition = pacMan.getPosition();
        int newPacManPosition = getNextPosition(pacManPosition, pacMan.getNextOrientation());

        if (tryToMovePacMan(newPacManPosition)) {
            pacMan.setCurrentOrientation(pacMan.getNextOrientation());
            pacManMapAdapter.moveToPosition(pacManPosition, newPacManPosition, pacManBlock);
            pacMan.setPosition(newPacManPosition);
        } else {
            newPacManPosition = getNextPosition(pacManPosition, pacMan.getCurrentOrientation());
            if (tryToMovePacMan(newPacManPosition)) {
                pacManMapAdapter.moveToPosition(pacManPosition, newPacManPosition, pacManBlock);
                pacMan.setPosition(newPacManPosition);
            }
        }
    }

    private int getNextPosition(int position, Orientation orientation) {
        switch (orientation) {
            case UP:
                return (position - mapWidth);
            case DOWN:
                return (position + mapWidth);
            case LEFT:
                return (position - 1);
            case RIGHT:
                return (position + 1);
            default:
                return position;
        }
    }

    private boolean tryToMovePacMan(int position) {
        Square square = (Square) pacManMapAdapter.getItem(position);
        boolean canMove;
        switch (square.getShownBlockType()) {
            case WALL:
                canMove = false;
                break;
            case GHOST:
                canMove = true;
                break;
            case EMPTY:
                canMove = true;
                break;
            case PAC_GUM:
                canMove = true;
                break;
            default:
                canMove = false;
                break;
        }
        return canMove;
    }

    private boolean tryToMoveGhost(int position) {
        Square square = (Square) pacManMapAdapter.getItem(position);
        boolean canMove;
        switch (square.getShownBlockType()) {
            case WALL:
                canMove = false;
                break;
            case PAC_MAN:
                canMove = true;
                break;
            case EMPTY:
                canMove = true;
                break;
            case PAC_GUM:
                canMove = true;
                break;
            case GHOST:
                canMove = true;
                break;
            default:
                canMove = false;
                break;
        }
        return canMove;
    }

    private void moveGhost(Ghost ghost, Block ghostBlock, Handler ghostHandler, int message) {
        Orientation nextOrientation;
        int redGhostPosition = ghost.getPosition();
        int newPosition;
        ArrayList<Orientation> possibleOrientations = new ArrayList<Orientation>();

        if (tryToMoveGhost(getNextPosition(redGhostPosition, Orientation.UP))) {
            possibleOrientations.add(Orientation.UP);
        }
        if (tryToMoveGhost(getNextPosition(redGhostPosition, Orientation.DOWN))) {
            possibleOrientations.add(Orientation.DOWN);
        }
        if (tryToMoveGhost(getNextPosition(redGhostPosition, Orientation.LEFT))) {
            possibleOrientations.add(Orientation.LEFT);
        }
        if (tryToMoveGhost(getNextPosition(redGhostPosition, Orientation.RIGHT))) {
            possibleOrientations.add(Orientation.RIGHT);
        }

        nextOrientation = ghost.getNextOrientation(possibleOrientations, pacMan.getPosition(), mapWidth);
        redGhostPosition = ghost.getPosition();
        newPosition = getNextPosition(ghost.getPosition(), nextOrientation);
        pacManMapAdapter.moveToPosition(redGhostPosition, newPosition, ghostBlock);
        ghost.setPosition(newPosition);
        ghostHandler.sendEmptyMessage(message);
    }

    private void initGhosts() {
        redGhostThread = new Thread(new Runnable() {

            public void run() {
                try {
                    moveGhost(redGhost, redGhostBlock, redGhostHandler, RED_GHOST_MOVED_MESSAGE_ID);
                } catch (Throwable t) {

                }
            }
        });
        redGhostHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == RED_GHOST_MOVED_MESSAGE_ID) {
                    pacManMap.setAdapter(pacManMapAdapter);
                    verifyGameLostByGhost(redGhost);
                }
            }
        };

        pinkGhostThread = new Thread(new Runnable() {

            public void run() {
                try {
                    moveGhost(pinkGhost, pinkGhostBlock, pinkGhostHandler, PINK_GHOST_MOVED_MESSAGE_ID);
                } catch (Throwable t) {

                }
            }
        });
        pinkGhostHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == PINK_GHOST_MOVED_MESSAGE_ID) {
                    pacManMap.setAdapter(pacManMapAdapter);
                    verifyGameLostByGhost(pinkGhost);
                }
            }
        };

        yellowGhostThread = new Thread(new Runnable() {

            public void run() {
                try {
                    moveGhost(yellowGhost, yellowGhostBlock, yellowGhostHandler, YELLOW_GHOST_MOVED_MESSAGE_ID);
                } catch (Throwable t) {

                }
            }
        });
        yellowGhostHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == YELLOW_GHOST_MOVED_MESSAGE_ID) {
                    pacManMap.setAdapter(pacManMapAdapter);
                    verifyGameLostByGhost(yellowGhost);
                }
            }
        };

        blueGhostThread = new Thread(new Runnable() {

            public void run() {
                try {
                    moveGhost(blueGhost, blueGhostBlock, blueGhostHandler, BLUE_GHOST_MOVED_MESSAGE_ID);
                } catch (Throwable t) {

                }
            }
        });
        blueGhostHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == BLUE_GHOST_MOVED_MESSAGE_ID) {
                    pacManMap.setAdapter(pacManMapAdapter);
                    verifyGameLostByGhost(blueGhost);
                }
            }
        };
    }

    private void eatPacGum() {
        Square pacManSquare = (Square) pacManMapAdapter.getItem(pacMan.getPosition());
        if (pacManSquare.findBlockByType(BlockType.PAC_GUM) != null) {
            pacManSquare.removePacGum();
            pacGumCount--;
            score += 10;
            scoreTextView.setText(Integer.toString(score));
            if (pacGumCount == 0) {
                winGame();
            }
        }
    }

    private void resetGame(boolean isReseting) {
        isGameFrozen = false;
        createMap(isReseting);
        initGame();
    }

    private void pauseGame() {
        gameTimer.cancel();
        gameTimer.purge();
    }

    private void looseLife() {
        if (isGameFrozen) {
            return;
        }
        isGameFrozen = true;
        pauseGame();
        pacManLifes--;
        if (pacManLifes == 0) {
            gameOver();
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.dialog_loose_life_title)
                    .setMessage(R.string.dialog_loose_life_message)
                    .setPositiveButton(R.string.dialog_loose_life_button_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setOnCancelListener(new  DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            resetGame(true);
                        }
                    });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }

    private void winGame() {
        score += 500;
        scoreTextView.setText(Integer.toString(score));
        pauseGame();
        isGameFrozen = true;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.dialog_win_game_title)
                .setMessage(R.string.dialog_win_game_message)
                .setPositiveButton(R.string.dialog_win_game_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new  DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        resetGame(false);
                    }
                });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void gameOver() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.dialog_game_over_title)
                .setMessage(R.string.dialog_game_over_message)
                .setPositiveButton(R.string.dialog_game_over_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new  DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Intent menuIntent = new Intent(context, MenuActivity.class);
                        startActivity(menuIntent);
                        finish();
                    }
                });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void verifyGameLostByGhost(Ghost ghost) {
        // verify if pacMan is on ghost position
        Square newSquare = (Square) pacManMapAdapter.getItem(ghost.getPosition());
        Block block = newSquare.findBlockByType(BlockType.PAC_MAN);
        if (block != null) {
            looseLife();
        }
    }

    private void verifyGameLostByPacMan() {
        // verify if a ghost is on pacMan position
        Square newSquare = (Square) pacManMapAdapter.getItem(pacMan.getPosition());
        Block block = newSquare.findBlockByType(BlockType.GHOST);
        if (block != null) {
            looseLife();
        }
    }

    public void moveUp(View v) {
        pacMan.setNextOrientation(Orientation.UP);
    }

    public void moveLeft(View v) {
        pacMan.setNextOrientation(Orientation.LEFT);
    }

    public void moveRight(View v) {
        pacMan.setNextOrientation(Orientation.RIGHT);
    }

    public void moveDown(View v) {
        pacMan.setNextOrientation(Orientation.DOWN);
    }
}
