package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;
import io.github.MAC.mp125.notes.GameNote;
import io.github.MAC.mp125.notes.smParser;
import io.github.MAC.mp125.notes.HitDetector;
import io.github.MAC.mp125.notes.HitDetector.HitGrade;

public class GameScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Texture bgTexture;
    private ProgressBar healthBar;
    private ProgressBar progressBar;
    private float health = 50f;

    // Note Tracking
    private ConcurrentLinkedQueue<GameNote> p1Notes;
    private ConcurrentLinkedQueue<GameNote> p2Notes;
    private long currentSongTimeMs = 0;
    private ShapeRenderer shapeRenderer;
    private float receptorY = 600f; // Near top of screen
    private float laneWidth = 60f;
    private float noteSize = 50f;
    private float scrollSpeedFactor = 0.5f;

    private Music backgroundMusic;
    private String songName;

    // Animation fields
    private static final int FRAME_COLS = 3, FRAME_ROWS = 3; // Adjust based on actual sprite sheet
    private Animation<TextureRegion> p1AnimW, p1AnimA, p1AnimS, p1AnimD;
    private Animation<TextureRegion> p2AnimUp, p2AnimLeft, p2AnimDown, p2AnimRight;
    private Animation<TextureRegion> p1CurrentAnim, p2CurrentAnim;
    private Texture p1SpriteSheet;
    private Texture p2SpriteSheet;
    private float p1StateTime = 0f;
    private float p2StateTime = 0f;
    private TextureRegion p1IdleFrame, p2IdleFrame;
    private Image p1Sprite, p2Sprite;

    private Label p1ScoreLabel, p2ScoreLabel;
    private int p1Score = 0;
    private int p2Score = 0;

    // P1 Keys
    private TextButton wBtn, aBtn, sBtn, dBtn;

    // P2 Keys
    private TextButton upBtn, leftBtn, downBtn, rightBtn;

    public GameScreen(Game game, String songName) {
        this.game = game;
        this.songName = songName;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        bgTexture = new Texture(Gdx.files.internal("GameScreenBGPlaceholder.png"));
        Image bgImage = new Image(bgTexture);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Player 1 Buttons (WASD)
        wBtn = new TextButton("W", skin);
        aBtn = new TextButton("A", skin);
        sBtn = new TextButton("S", skin);
        dBtn = new TextButton("D", skin);

        Table p1Table = new Table();
        p1Table.add(wBtn).width(60).height(60).pad(5);
        p1Table.add(aBtn).width(60).height(60).pad(5);
        p1Table.add(sBtn).width(60).height(60).pad(5);
        p1Table.add(dBtn).width(60).height(60).pad(5);

        // Player 2 Buttons (Arrows)
        upBtn = new TextButton("^", skin);
        leftBtn = new TextButton("<", skin);
        downBtn = new TextButton("v", skin);
        rightBtn = new TextButton(">", skin);

        Table p2Table = new Table();
        p2Table.add(upBtn).width(60).height(60).pad(5);
        p2Table.add(leftBtn).width(60).height(60).pad(5);
        p2Table.add(downBtn).width(60).height(60).pad(5);
        p2Table.add(rightBtn).width(60).height(60).pad(5);

        // Add to root table
        rootTable.add(p1Table).expand().left().pad(50).top();

        healthBar = new ProgressBar(0, 100, 1, false, skin);
        healthBar.setValue(health);
        rootTable.add(healthBar).expand().center().top().padTop(70).width(300);

        rootTable.add(p2Table).expand().right().pad(50).top();

        // Placeholders for player sprites | TODO: @grace Change Image/s for Sprites
        rootTable.row();
        // Load Sprite Sheets
        p1SpriteSheet = new Texture(Gdx.files.internal("aleiancharsel.png"));
        TextureRegion[][] p1Tmp = TextureRegion.split(p1SpriteSheet,
                p1SpriteSheet.getWidth() / FRAME_COLS,
                p1SpriteSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] p1Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                p1Frames[index++] = p1Tmp[i][j];
            }
        }
        p1AnimW = new Animation<>(0.15f, p1Frames[4], p1Frames[5]);
        p1AnimA = new Animation<>(0.15f, p1Frames[1], p1Frames[2]);
        p1AnimS = new Animation<>(0.15f, p1Frames[2], p1Frames[3]);
        p1AnimD = new Animation<>(0.15f, p1Frames[3], p1Frames[4]);
        p1CurrentAnim = p1AnimW;
        p1IdleFrame = p1Frames[0];

        p2SpriteSheet = new Texture(Gdx.files.internal("kooacharsel.png"));
        TextureRegion[][] p2Tmp = TextureRegion.split(p2SpriteSheet,
                p2SpriteSheet.getWidth() / FRAME_COLS,
                p2SpriteSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] p2Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                p2Frames[index++] = p2Tmp[i][j];
            }
        }
        p2AnimUp = new Animation<>(0.15f, p2Frames[4], p2Frames[5]);
        p2AnimLeft = new Animation<>(0.15f, p2Frames[1], p2Frames[2]);
        p2AnimDown = new Animation<>(0.15f, p2Frames[2], p2Frames[3]);
        p2AnimRight = new Animation<>(0.15f, p2Frames[3], p2Frames[4]);
        p2CurrentAnim = p2AnimUp;
        p2IdleFrame = p2Frames[0];

        p1Sprite = new Image(p1IdleFrame);
        p2Sprite = new Image(p2IdleFrame);

        rootTable.add(p1Sprite).width(150).height(150).expand().center();
        rootTable.add().expand().center(); // spacer for the middle column
        rootTable.add(p2Sprite).width(150).height(150).expand().center();

        rootTable.row();
        p1ScoreLabel = new Label("Score: 0", skin);
        p2ScoreLabel = new Label("Score: 0", skin);
        rootTable.add(p1ScoreLabel).expand().center().top();
        rootTable.add().expand().center();
        rootTable.add(p2ScoreLabel).expand().center().top();

        rootTable.row();
        progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setValue(50f);
        rootTable.add(progressBar).colspan(3).expand().fillX().bottom().pad(50);

        // Init Notes and Renderer
        shapeRenderer = new ShapeRenderer();

        // Load and play music
        if (Gdx.files.internal(songName + ".wav").exists()) {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(songName + ".wav"));
            backgroundMusic.play();
        }

        p1Notes = smParser.parseChart(songName + ".sm", 1);
        p2Notes = smParser.parseChart(songName + ".sm", 2);

        System.out.println("Game Screen Started");
    }

    private boolean updateButtonState(TextButton btn, int keycode) {
        if (Gdx.input.isKeyPressed(keycode)) {
            btn.setColor(Color.GREEN);
        } else {
            btn.setColor(Color.WHITE);
        }
        return Gdx.input.isKeyJustPressed(keycode);
    }

    private void processHitAttempt(ConcurrentLinkedQueue<GameNote> notes, int laneIndex, boolean isP1) {
        Iterator<GameNote> iterator = notes.iterator();
        while (iterator.hasNext()) {
            GameNote note = iterator.next();
            if (note.laneIndex == laneIndex) {
                HitGrade grade = HitDetector.evaluateHit(note.targetTimeMs, currentSongTimeMs);
                if (grade != HitGrade.NONE && grade != HitGrade.MISS) {
                    iterator.remove();
                    int points = 0;
                    float hpChange = 0;
                    switch (grade) {
                        case PERFECT:
                            points = 50;
                            hpChange = 2f;
                            break;
                        case AMAZING:
                            points = 40;
                            hpChange = 1.5f;
                            break;
                        case GOOD:
                            points = 30;
                            hpChange = 1f;
                            break;
                        case MEH:
                            points = 10;
                            hpChange = 0.5f;
                            break;
                        case BAD:
                            points = 0;
                            hpChange = 0f;
                            break;
                        default:
                            break;
                    }
                    if (isP1) {
                        p1Score += points;
                        health += hpChange;
                    } else {
                        p2Score += points;
                        health -= hpChange;
                    }
                    return; // Hit processed, don't hit multiple notes at once
                }
            }
        }
    }

    private void updateAndDrawNotes(ConcurrentLinkedQueue<GameNote> notes, float startX, boolean isP1) {
        Iterator<GameNote> iterator = notes.iterator();
        while (iterator.hasNext()) {
            GameNote note = iterator.next();
            if (HitDetector.hasMissed(note.targetTimeMs, currentSongTimeMs)) {
                iterator.remove();
                if (isP1)
                    health -= 2f; // P1 misses
                else
                    health += 2f; // P2 misses
                continue;
            }

            float timeRemaining = note.targetTimeMs - currentSongTimeMs;
            float noteY = receptorY - (timeRemaining * scrollSpeedFactor);

            // Draw if it's on screen
            if (noteY > -noteSize && noteY < Gdx.graphics.getHeight()) {
                switch (note.laneIndex) {
                    case 0:
                        shapeRenderer.setColor(Color.PURPLE);
                        break;
                    case 1:
                        shapeRenderer.setColor(Color.CYAN);
                        break;
                    case 2:
                        shapeRenderer.setColor(Color.GREEN);
                        break;
                    case 3:
                        shapeRenderer.setColor(Color.RED);
                        break;
                    default:
                        shapeRenderer.setColor(Color.WHITE);
                        break;
                }
                float noteX = startX + (note.laneIndex * laneWidth);
                shapeRenderer.rect(noteX, noteY, noteSize, noteSize);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            // Synchronize note position using actual audio position
            currentSongTimeMs = (long) (backgroundMusic.getPosition() * 1000);
        } else {
            currentSongTimeMs += (long) (delta * 1000);
        }

        // Update P1 Keys
        boolean wPressed = updateButtonState(wBtn, Input.Keys.W);
        boolean aPressed = updateButtonState(aBtn, Input.Keys.A);
        boolean sPressed = updateButtonState(sBtn, Input.Keys.S);
        boolean dPressed = updateButtonState(dBtn, Input.Keys.D);

        if (aPressed)
            processHitAttempt(p1Notes, 0, true);
        if (sPressed)
            processHitAttempt(p1Notes, 1, true);
        if (wPressed)
            processHitAttempt(p1Notes, 2, true);
        if (dPressed)
            processHitAttempt(p1Notes, 3, true);

        // Update P2 Keys
        boolean upPressed = updateButtonState(upBtn, Input.Keys.UP);
        boolean leftPressed = updateButtonState(leftBtn, Input.Keys.LEFT);
        boolean downPressed = updateButtonState(downBtn, Input.Keys.DOWN);
        boolean rightPressed = updateButtonState(rightBtn, Input.Keys.RIGHT);

        if (leftPressed)
            processHitAttempt(p2Notes, 0, false);
        if (downPressed)
            processHitAttempt(p2Notes, 1, false);
        if (upPressed)
            processHitAttempt(p2Notes, 2, false);
        if (rightPressed)
            processHitAttempt(p2Notes, 3, false);

        p1ScoreLabel.setText("Score: " + p1Score);
        p2ScoreLabel.setText("Score: " + p2Score);

        // Clamp health
        if (health >= 100)
            health = 100f;
        if (health <= 0)
            health = 0f;
        healthBar.setValue(health);

        // Apply animation frame corresponding to key
        boolean p1Moving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            p1CurrentAnim = p1AnimW;
            p1Moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            p1CurrentAnim = p1AnimA;
            p1Moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            p1CurrentAnim = p1AnimS;
            p1Moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            p1CurrentAnim = p1AnimD;
            p1Moving = true;
        }

        if (p1Moving) {
            p1StateTime += delta;
            p1Sprite.setDrawable(new TextureRegionDrawable(p1CurrentAnim.getKeyFrame(p1StateTime, true)));
        } else {
            p1StateTime = 0f;
            p1Sprite.setDrawable(new TextureRegionDrawable(p1IdleFrame));
        }

        boolean p2Moving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            p2CurrentAnim = p2AnimUp;
            p2Moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            p2CurrentAnim = p2AnimLeft;
            p2Moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            p2CurrentAnim = p2AnimDown;
            p2Moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            p2CurrentAnim = p2AnimRight;
            p2Moving = true;
        }

        if (p2Moving) {
            p2StateTime += delta;
            p2Sprite.setDrawable(new TextureRegionDrawable(p2CurrentAnim.getKeyFrame(p2StateTime, true)));
        } else {
            p2StateTime = 0f;
            p2Sprite.setDrawable(new TextureRegionDrawable(p2IdleFrame));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new ResultsScreen(game));
            dispose(); // Free up resources when leaving the screen
            return; // Prevent further rendering with disposed resources
        }

        stage.act(delta);
        stage.draw();

        // Draw Notes and Receptors over the stage
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Draw Receptors (Hollow)
        float p1StartX = Gdx.graphics.getWidth() * 0.1f;
        float p2StartX = Gdx.graphics.getWidth() * 0.7f;

        // Adjust receptor height dynamically based on screen size
        receptorY = Gdx.graphics.getHeight() - 100f;

        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            shapeRenderer.rect(p1StartX + (i * laneWidth), receptorY, noteSize, noteSize);
            shapeRenderer.rect(p2StartX + (i * laneWidth), receptorY, noteSize, noteSize);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        updateAndDrawNotes(p1Notes, p1StartX, true);
        updateAndDrawNotes(p2Notes, p2StartX, false);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        if (stage == null)
            return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (backgroundMusic != null) {
            backgroundMusic.stop(); // Ensure music stops playing when the screen is hidden
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        if (p1SpriteSheet != null) {
            p1SpriteSheet.dispose();
        }
        if (p2SpriteSheet != null) {
            p2SpriteSheet.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }

    }
}
