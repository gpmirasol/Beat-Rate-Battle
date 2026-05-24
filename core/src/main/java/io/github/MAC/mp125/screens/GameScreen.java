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
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameScreen implements Screen {
    private Game game;
    private Stage stage;
    private Stage overlayStage;
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
    private String bgName;

    // Animation fields
    private static final int FRAME_COLS = 3, FRAME_ROWS = 6; // Todo: Adjust based on sprite sheet Update: Done
    private static final int SPRITE_SIZE = 682;
    private Animation<TextureRegion> p1AnimW, p1AnimA, p1AnimS, p1AnimD, p1AnimMiss;
    private Animation<TextureRegion> p2AnimUp, p2AnimLeft, p2AnimDown, p2AnimRight, p2AnimMiss;
    private Animation<TextureRegion> p1CurrentAnim, p2CurrentAnim;
    private Texture p1SpriteSheet;
    private Texture p2SpriteSheet;
    private float p1StateTime = 0f;
    private float p2StateTime = 0f;
    private float p1MissTimer = 0f;
    private float p2MissTimer = 0f;
    private float p1LabelTimer = 0f;
    private float p2LabelTimer = 0f;
    private Image p1HitGradeImage, p2HitGradeImage;
    private Animation<TextureRegion> indAnimPerfect, indAnimGood, indAnimMeh, indAnimMiss;
    private Animation<TextureRegion> p1CurrentIndAnim, p2CurrentIndAnim;
    private float p1IndStateTime = 0f, p2IndStateTime = 0f;
    private Animation<TextureRegion> p1AnimIdle, p2AnimIdle;
    private Image p1Sprite, p2Sprite;

    private Label p1ScoreLabel, p2ScoreLabel;
    private Image p1StreakImage, p2StreakImage;
    private Texture indicatorSpriteSheet;
    private TextureRegion[] counterFrames;
    private int p1Score = 0;
    private int p2Score = 0;
    private int p1PerfectStreak = 0;
    private int p2PerfectStreak = 0;
    private float p1HardModeTimer = 0f;
    private float p2HardModeTimer = 0f;
    private float p1DebuffCooldownTimer = 0f;
    private float p2DebuffCooldownTimer = 0f;
    private ProgressBar p1DebuffBar;
    private ProgressBar p2DebuffBar;
    private io.github.MAC.mp125.PlayerStats p1Stats = new io.github.MAC.mp125.PlayerStats();
    private io.github.MAC.mp125.PlayerStats p2Stats = new io.github.MAC.mp125.PlayerStats();
    private long totalSongDurationMs = 0;
    private boolean isGameOver = false;
    private float gameOverTimer = 0f;
    private Image p1EndLabel, p2EndLabel;
    private Texture winMessage = new Texture(Gdx.files.internal("labelyouwin.png"));
    private Texture loseMessage = new Texture(Gdx.files.internal("labelyoulose.png"));

    private Music countdownAudio;
    private boolean isCountingDown = false;

    private boolean isPaused = false;
    private Stage pauseStage;
    private Texture blackPixelTexture;
    private Texture btnPlayTex;
    private Texture btnMenuTex;

    // P1 Keys
    private Image wBtn, aBtn, sBtn, dBtn;

    // P2 Keys
    private Image upBtn, leftBtn, downBtn, rightBtn;

    private Texture texLeft, texDown, texUp, texRight;
    private Texture texPressedLeft, texPressedDown, texPressedUp, texPressedRight;
    private TextureRegionDrawable drawLeft, drawDown, drawUp, drawRight;
    private TextureRegionDrawable drawPressedLeft, drawPressedDown, drawPressedUp, drawPressedRight;
    private Texture flyTexLeft, flyTexDown, flyTexUp, flyTexRight;

    // Spritesheets
    String[] characterSpriteSheets = { "meeraspritesheet.png", "kooaspritesheet.png", "aleianspritesheet.png" };
    String p1SpriteChoice = characterSpriteSheets[CharacterSelectScreen.selectedP1SpriteIndex];
    String p2SpriteChoice = characterSpriteSheets[CharacterSelectScreen.selectedP2SpriteIndex];

    public GameScreen(Game game, String songName, String bgName) {
        this.game = game;
        this.songName = songName;
        this.bgName = bgName;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        overlayStage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        bgTexture = new Texture(Gdx.files.internal(bgName + "background.png"));
        Image bgImage = new Image(bgTexture);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table characterTable = new Table();
        characterTable.setFillParent(true);
        stage.addActor(characterTable);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        texLeft = new Texture(Gdx.files.internal("basenote_left.png"));
        texDown = new Texture(Gdx.files.internal("basenote_down.png"));
        texUp = new Texture(Gdx.files.internal("basenote_up.png"));
        texRight = new Texture(Gdx.files.internal("basenote_right.png"));

        texPressedLeft = new Texture(Gdx.files.internal("notepressed_left.png"));
        texPressedDown = new Texture(Gdx.files.internal("notepressed_down.png"));
        texPressedUp = new Texture(Gdx.files.internal("notepressed_up.png"));
        texPressedRight = new Texture(Gdx.files.internal("notepressed_right.png"));

        drawLeft = new TextureRegionDrawable(texLeft);
        drawDown = new TextureRegionDrawable(texDown);
        drawUp = new TextureRegionDrawable(texUp);
        drawRight = new TextureRegionDrawable(texRight);

        drawPressedLeft = new TextureRegionDrawable(texPressedLeft);
        drawPressedDown = new TextureRegionDrawable(texPressedDown);
        drawPressedUp = new TextureRegionDrawable(texPressedUp);
        drawPressedRight = new TextureRegionDrawable(texPressedRight);

        flyTexLeft = new Texture(Gdx.files.internal("flyingnote_left.png"));
        flyTexDown = new Texture(Gdx.files.internal("flyingnote_down.png"));
        flyTexUp = new Texture(Gdx.files.internal("flyingnote_up.png"));
        flyTexRight = new Texture(Gdx.files.internal("flyingnote_right.png"));

        // Player 1 Buttons (WASD)
        wBtn = new Image(texUp);
        aBtn = new Image(texLeft);
        sBtn = new Image(texDown);
        dBtn = new Image(texRight);

        Table p1Table = new Table();
        p1Table.add(aBtn).width(60).height(60).pad(5);
        p1Table.add(sBtn).width(60).height(60).pad(5);
        p1Table.add(wBtn).width(60).height(60).pad(5);
        p1Table.add(dBtn).width(60).height(60).pad(5);

        // Player 2 Buttons (Arrows)
        upBtn = new Image(texUp);
        leftBtn = new Image(texLeft);
        downBtn = new Image(texDown);
        rightBtn = new Image(texRight);

        Table p2Table = new Table();
        p2Table.add(leftBtn).width(60).height(60).pad(5);
        p2Table.add(downBtn).width(60).height(60).pad(5);
        p2Table.add(upBtn).width(60).height(60).pad(5);
        p2Table.add(rightBtn).width(60).height(60).pad(5);

        // Add to root table
        rootTable.add(p1Table).expand().left().pad(50).top();

        healthBar = new ProgressBar(0f, 100f, 0.01f, false, skin);
        healthBar.setValue(health);
        healthBar.setAnimateDuration(0.1f);

        Table middleTable = new Table();
        middleTable.add(healthBar).colspan(2).center().top().padTop(70).width(300);
        middleTable.row();

        p1HitGradeImage = new Image();
        p2HitGradeImage = new Image();
        middleTable.add(p1HitGradeImage).padRight(20).padTop(20).width(100).height(100);
        middleTable.add(p2HitGradeImage).padLeft(20).padTop(20).width(100).height(100);

        middleTable.row();
        p1StreakImage = new Image();
        p2StreakImage = new Image();
        p1StreakImage.setVisible(false);
        p2StreakImage.setVisible(false);
        middleTable.add(p1StreakImage).padRight(20).padTop(10).width(80).height(40);
        middleTable.add(p2StreakImage).padLeft(20).padTop(10).width(80).height(40);

        middleTable.row();
        p1DebuffBar = new ProgressBar(0f, 10f, 0.1f, false, skin);
        p2DebuffBar = new ProgressBar(0f, 10f, 0.1f, false, skin);
        p1DebuffBar.setAnimateDuration(0.1f);
        p2DebuffBar.setAnimateDuration(0.1f);
        middleTable.add(p1DebuffBar).padRight(20).padTop(10).width(100);
        middleTable.add(p2DebuffBar).padLeft(20).padTop(10).width(100);

        rootTable.add(middleTable).expand().center().top();

        rootTable.add(p2Table).expand().right().pad(50).top();

        // Placeholders for player sprites | TODO: @grace Change Image/s for Sprites
        rootTable.row();
        // Load Sprite Sheets
        p1SpriteSheet = new Texture(Gdx.files.internal(p1SpriteChoice));
        TextureRegion[][] p1Tmp = TextureRegion.split(p1SpriteSheet, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion[] p1Frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                p1Frames[index++] = p1Tmp[i][j];
            }
        }

        p1AnimIdle = new Animation<>(0.10f, p1Tmp[0]);
        p1AnimW = new Animation<>(0.10f, p1Tmp[1]);
        p1AnimA = new Animation<>(0.10f, p1Tmp[2]);
        p1AnimS = new Animation<>(0.10f, p1Tmp[3]);
        p1AnimD = new Animation<>(0.10f, p1Tmp[4]);
        p1AnimMiss = new Animation<>(0.10f, p1Tmp[5]);
        p1CurrentAnim = p1AnimIdle;

        p2SpriteSheet = new Texture(Gdx.files.internal(p2SpriteChoice));
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

        p2AnimIdle = new Animation<>(0.10f, p2Tmp[0]);
        p2AnimUp = new Animation<>(0.15f, p2Tmp[1]);
        p2AnimLeft = new Animation<>(0.15f, p2Tmp[2]);
        p2AnimDown = new Animation<>(0.15f, p2Tmp[3]);
        p2AnimRight = new Animation<>(0.15f, p2Tmp[4]);
        p2AnimMiss = new Animation<>(0.10f, p2Tmp[5]);
        p2CurrentAnim = p2AnimIdle;

        Texture counterSpriteSheet = new Texture(Gdx.files.internal("counterspritesheet.png"));
        TextureRegion[][] counterTmp = TextureRegion.split(counterSpriteSheet,
                counterSpriteSheet.getWidth() / 2,
                counterSpriteSheet.getHeight() / 5);
        counterFrames = new TextureRegion[10];
        int counterIndex = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                counterFrames[counterIndex++] = counterTmp[i][j];
            }
        }

        indicatorSpriteSheet = new Texture(Gdx.files.internal("indicatorspritesheet.png"));
        TextureRegion[][] indTmp = TextureRegion.split(indicatorSpriteSheet,
                indicatorSpriteSheet.getWidth() / 3,
                indicatorSpriteSheet.getHeight() / 6);

        indAnimPerfect = new Animation<>(0.10f, indTmp[0]);
        indAnimGood = new Animation<>(0.10f, indTmp[2]);
        indAnimMeh = new Animation<>(0.10f, indTmp[3]);
        indAnimMiss = new Animation<>(0.10f, indTmp[5]);

        p1CurrentIndAnim = indAnimPerfect;
        p2CurrentIndAnim = indAnimPerfect;

        p1Sprite = new Image(p1AnimIdle.getKeyFrame(0));
        p2Sprite = new Image(p2AnimIdle.getKeyFrame(0));

        p1HitGradeImage.setVisible(false);
        p2HitGradeImage.setVisible(false);

        characterTable.add(p1Sprite).width(450).height(450).expand().center();
        characterTable.add().expand().center(); // spacer for the middle column
        characterTable.add(p2Sprite).width(450).height(450).expand().center();
        p1ScoreLabel = new Label("Score: 0", skin);
        p2ScoreLabel = new Label("Score: 0", skin);
        rootTable.add(p1ScoreLabel).expand().center().top();
        rootTable.add().expand().center();
        rootTable.add(p2ScoreLabel).expand().center().top();

        // Create end game overlay layout
        Table overlayTable = new Table();
        overlayTable.setFillParent(true);
        overlayStage.addActor(overlayTable);

        p1EndLabel = new Image(winMessage);
        p2EndLabel = new Image(winMessage);
        p1EndLabel.setVisible(false);
        p2EndLabel.setVisible(false);
        overlayTable.add(p1EndLabel).expand().center().size(300, 100);
        overlayTable.add().expand().center();
        overlayTable.add(p2EndLabel).expand().center().size(300, 100);

        rootTable.row();
        progressBar = new ProgressBar(0f, 100f, 0.01f, false, skin);
        progressBar.setValue(50f);
        progressBar.setAnimateDuration(0.1f);
        rootTable.add(progressBar).colspan(3).expand().fillX().bottom().pad(50);

        // Init Notes and Renderer
        shapeRenderer = new ShapeRenderer();

        // Load and play music
        if (Gdx.files.internal(songName + ".wav").exists()) {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(songName + ".wav"));
        }

        if (Gdx.files.internal("Countdown.wav").exists()) {
            countdownAudio = Gdx.audio.newMusic(Gdx.files.internal("Countdown.wav"));
            countdownAudio.play();
            isCountingDown = true;
        } else {
            isCountingDown = false;
            if (backgroundMusic != null) {
                backgroundMusic.play();
            }
        }

        p1Notes = smParser.parseChart(songName + "_easy.sm", 1);
        p2Notes = smParser.parseChart(songName + "_easy.sm", 2);

        for (GameNote note : p1Notes) {
            long end = note.isHoldNote ? note.endTimeMs : note.targetTimeMs;
            if (end > totalSongDurationMs) {
                totalSongDurationMs = end;
            }
        }
        totalSongDurationMs += 2000; // Pad end by 2 seconds

        pauseStage = new Stage(new ScreenViewport());
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.7f);
        pixmap.fill();
        blackPixelTexture = new Texture(pixmap);
        pixmap.dispose();

        Image darkBg = new Image(blackPixelTexture);
        darkBg.setFillParent(true);

        Table pauseTable = new Table();
        pauseTable.setFillParent(true);

        btnPlayTex = new Texture(Gdx.files.internal("buttonplay.png"));
        btnMenuTex = new Texture(Gdx.files.internal("buttonbacktomenu.png"));

        ImageButton resumeBtn = new ImageButton(new TextureRegionDrawable(btnPlayTex));
        ImageButton menuBtn = new ImageButton(new TextureRegionDrawable(btnMenuTex));

        pauseTable.center();
        pauseTable.add(resumeBtn).size(btnPlayTex.getWidth() / 3f, btnPlayTex.getHeight() / 3f).padBottom(10).row();
        pauseTable.add(menuBtn).size(btnMenuTex.getWidth() / 2f, btnMenuTex.getHeight() / 2f);

        pauseStage.addActor(darkBg);
        pauseStage.addActor(pauseTable);

        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false;
                isCountingDown = true;
                if (countdownAudio != null) {
                    countdownAudio.stop();
                    countdownAudio.play();
                }
                Gdx.input.setInputProcessor(null);
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        System.out.println("Game Screen Started");
    }

    private boolean updateButtonState(Image btn, TextureRegionDrawable unpressed, TextureRegionDrawable pressed,
            int keycode) {
        if (Gdx.input.isKeyPressed(keycode)) {
            btn.setDrawable(pressed);
            btn.setColor(Color.WHITE); // Ensure no green tint is left
        } else {
            btn.setDrawable(unpressed);
            btn.setColor(Color.WHITE);
        }
        return Gdx.input.isKeyJustPressed(keycode);
    }

    private void processHitAttempt(ConcurrentLinkedQueue<GameNote> notes, int laneIndex, boolean isP1) {
        Iterator<GameNote> iterator = notes.iterator();
        boolean hitRegistered = false;
        while (iterator.hasNext()) {
            GameNote note = iterator.next();
            if (note.laneIndex == laneIndex) {
                HitGrade grade = HitDetector.evaluateHit(note.targetTimeMs, currentSongTimeMs);
                if (grade != HitGrade.NONE && grade != HitGrade.MISS) {
                    if (note.isHoldNote) {
                        note.isBeingHeld = true;
                    } else {
                        iterator.remove();
                    }
                    int points = 0;
                    float hpChange = 0;
                    switch (grade) {
                        case PERFECT:
                            points = 50;
                            hpChange = 0.1f;
                            break;
                        case GOOD:
                            points = 30;
                            hpChange = 0.05f;
                            break;
                        case MEH:
                            points = 10;
                            hpChange = 0.025f;
                            break;
                        default:
                            break;
                    }
                    if (isP1) {
                        p1Stats.recordHit(grade);
                        if (p2HardModeTimer > 0)
                            points *= 2;
                        p1Score += points;
                        p1Stats.score = p1Score;
                        health += hpChange;
                        if (grade == HitGrade.PERFECT) {
                            if (p1DebuffCooldownTimer <= 0) {
                                p1PerfectStreak++;
                                if (p1PerfectStreak == 10) {
                                    setOpponentChart(false, true);
                                    p2HardModeTimer = 3.0f;
                                    p1DebuffCooldownTimer = 10.0f;
                                }
                                if (p1PerfectStreak > 10)
                                    p1PerfectStreak = 1;
                            } else {
                                p1PerfectStreak = 0;
                            }
                        } else {
                            p1PerfectStreak = 0;
                        }

                        if (p1PerfectStreak > 0) {
                            p1StreakImage.setDrawable(new TextureRegionDrawable(counterFrames[p1PerfectStreak - 1]));
                            p1StreakImage.setVisible(true);
                        } else {
                            p1StreakImage.setVisible(false);
                        }

                        switch (grade) {
                            case PERFECT:
                                p1CurrentIndAnim = indAnimPerfect;
                                break;
                            case GOOD:
                                p1CurrentIndAnim = indAnimGood;
                                break;
                            case MEH:
                                p1CurrentIndAnim = indAnimMeh;
                                break;
                            case MISS:
                                p1CurrentIndAnim = indAnimMiss;
                                break;
                            default:
                                p1CurrentIndAnim = indAnimPerfect;
                                break;
                        }
                        p1IndStateTime = 0f;
                        p1HitGradeImage.setVisible(true);
                        p1LabelTimer = 1.0f;
                    } else {
                        p2Stats.recordHit(grade);
                        if (p1HardModeTimer > 0)
                            points *= 2;
                        p2Score += points;
                        p2Stats.score = p2Score;
                        health -= hpChange;
                        if (grade == HitGrade.PERFECT) {
                            if (p2DebuffCooldownTimer <= 0) {
                                p2PerfectStreak++;
                                if (p2PerfectStreak == 10) {
                                    setOpponentChart(true, true);
                                    p1HardModeTimer = 3.0f;
                                    p2DebuffCooldownTimer = 10.0f;
                                }
                                if (p2PerfectStreak > 10)
                                    p2PerfectStreak = 1;
                            } else {
                                p2PerfectStreak = 0;
                            }
                        } else {
                            p2PerfectStreak = 0;
                        }

                        if (p2PerfectStreak > 0) {
                            p2StreakImage.setDrawable(new TextureRegionDrawable(counterFrames[p2PerfectStreak - 1]));
                            p2StreakImage.setVisible(true);
                        } else {
                            p2StreakImage.setVisible(false);
                        }

                        switch (grade) {
                            case PERFECT:
                                p2CurrentIndAnim = indAnimPerfect;
                                break;
                            case GOOD:
                                p2CurrentIndAnim = indAnimGood;
                                break;
                            case MEH:
                                p2CurrentIndAnim = indAnimMeh;
                                break;
                            case MISS:
                                p2CurrentIndAnim = indAnimMiss;
                                break;
                            default:
                                p2CurrentIndAnim = indAnimPerfect;
                                break;
                        }
                        p2IndStateTime = 0f;
                        p2HitGradeImage.setVisible(true);
                        p2LabelTimer = 1.0f;
                    }
                    hitRegistered = true;
                    return;
                }
            }
        }

        if (!hitRegistered) {
            if (isP1) {
                p1Stats.recordMiss();
                health -= 1f;
                p1MissTimer = 0.3f;
                p1CurrentIndAnim = indAnimMiss;
                p1IndStateTime = 0f;
                p1HitGradeImage.setVisible(true);
                p1LabelTimer = 1.0f;
                p1PerfectStreak = 0;
                p1StreakImage.setVisible(false);
            } else {
                p2Stats.recordMiss();
                health += 1f;
                p2MissTimer = 0.3f;
                p2CurrentIndAnim = indAnimMiss;
                p2IndStateTime = 0f;
                p2HitGradeImage.setVisible(true);
                p2LabelTimer = 1.0f;
                p2PerfectStreak = 0;
                p2StreakImage.setVisible(false);
            }
        }
    }

    private void setOpponentChart(boolean isP1TheOpponent, boolean isHard) {
        String chartSuffix = isHard ? "_hard.sm" : "_easy.sm";
        String chartPath = songName + chartSuffix;
        if (!Gdx.files.internal(chartPath).exists()) {
            return;
        }

        int targetPlayerID = isP1TheOpponent ? 1 : 2;
        ConcurrentLinkedQueue<GameNote> parsedNotes = smParser.parseChart(chartPath, targetPlayerID);

        long safeTime = currentSongTimeMs + 1000;
        ConcurrentLinkedQueue<GameNote> finalQueue = new ConcurrentLinkedQueue<>();

        ConcurrentLinkedQueue<GameNote> currentNotes = isP1TheOpponent ? p1Notes : p2Notes;

        for (GameNote oldNote : currentNotes) {
            if (oldNote.targetTimeMs <= safeTime) {
                finalQueue.add(oldNote);
            }
        }

        for (GameNote newNote : parsedNotes) {
            if (newNote.targetTimeMs > safeTime) {
                finalQueue.add(newNote);
            }
        }

        if (isP1TheOpponent) {
            p1Notes = finalQueue;
        } else {
            p2Notes = finalQueue;
        }
    }

    private void updateAndDrawNotes(ConcurrentLinkedQueue<GameNote> notes, float startX, boolean isP1) {
        Iterator<GameNote> iterator = notes.iterator();
        while (iterator.hasNext()) {
            GameNote note = iterator.next();
            if (!isPaused) {
                if (HitDetector.hasMissed(note.targetTimeMs, currentSongTimeMs) && !note.isBeingHeld) {
                    iterator.remove();
                    if (isP1) {
                        p1Stats.recordMiss();
                        health -= 1f;
                        p1MissTimer = 0.3f;
                        p1CurrentIndAnim = indAnimMiss;
                        p1IndStateTime = 0f;
                        p1HitGradeImage.setVisible(true);
                        p1LabelTimer = 1.0f;
                        p1PerfectStreak = 0;
                        p1StreakImage.setVisible(false);
                    } else {
                        p2Stats.recordMiss();
                        health += 1f;
                        p2MissTimer = 0.3f;
                        p2CurrentIndAnim = indAnimMiss;
                        p2IndStateTime = 0f;
                        p2HitGradeImage.setVisible(true);
                        p2LabelTimer = 1.0f;
                        p2PerfectStreak = 0;
                        p2StreakImage.setVisible(false);
                    }
                    continue;
                }

                if (note.isHoldNote && note.isBeingHeld) {
                    boolean isPressed = false;
                    if (isP1) {
                        if (note.laneIndex == 0)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.A);
                        else if (note.laneIndex == 1)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.S);
                        else if (note.laneIndex == 2)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.W);
                        else if (note.laneIndex == 3)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.D);
                    } else {
                        if (note.laneIndex == 0)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
                        else if (note.laneIndex == 1)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
                        else if (note.laneIndex == 2)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
                        else if (note.laneIndex == 3)
                            isPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
                    }

                    if (!isPressed) {
                        iterator.remove();
                        if (isP1) {
                            p1Stats.recordMiss();
                            health -= 1f;
                            p1MissTimer = 0.3f;
                            p1CurrentIndAnim = indAnimMiss;
                            p1IndStateTime = 0f;
                            p1HitGradeImage.setVisible(true);
                            p1LabelTimer = 1.0f;
                            p1PerfectStreak = 0;
                            p1StreakImage.setVisible(false);
                        } else {
                            p2Stats.recordMiss();
                            health += 1f;
                            p2MissTimer = 0.3f;
                            p2CurrentIndAnim = indAnimMiss;
                            p2IndStateTime = 0f;
                            p2HitGradeImage.setVisible(true);
                            p2LabelTimer = 1.0f;
                            p2PerfectStreak = 0;
                            p2StreakImage.setVisible(false);
                        }
                        continue;
                    } else if (currentSongTimeMs >= note.endTimeMs) {
                        iterator.remove();
                        if (isP1) {
                            int holdPoints = (p2HardModeTimer > 0) ? 20 : 10;
                            p1Score += holdPoints;
                            health += 0.5f;
                        } else {
                            int holdPoints = (p1HardModeTimer > 0) ? 20 : 10;
                            p2Score += holdPoints;
                            health -= 0.5f;
                        }
                        continue;
                    }
                }
            } // End of !isPaused block

            float timeRemaining = note.targetTimeMs - currentSongTimeMs;
            float noteY = receptorY - (timeRemaining * scrollSpeedFactor);

            // Draw if it's on screen
            if (noteY > -noteSize && noteY < Gdx.graphics.getHeight()) {
                Texture texToDraw = flyTexLeft;
                switch (note.laneIndex) {
                    case 0:
                        texToDraw = flyTexLeft;
                        break;
                    case 1:
                        texToDraw = flyTexDown;
                        break;
                    case 2:
                        texToDraw = flyTexUp;
                        break;
                    case 3:
                        texToDraw = flyTexRight;
                        break;
                }
                float noteX = startX + (note.laneIndex * laneWidth);
                stage.getBatch().draw(texToDraw, noteX, noteY, noteSize, noteSize);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !isGameOver) {
            if (!isPaused) {
                isPaused = true;
                if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                }
                if (countdownAudio != null && countdownAudio.isPlaying()) {
                    countdownAudio.pause();
                }
                Gdx.input.setInputProcessor(pauseStage);
            } else {
                isPaused = false;
                isCountingDown = true;
                if (countdownAudio != null) {
                    countdownAudio.stop();
                    countdownAudio.play();
                }
                Gdx.input.setInputProcessor(null);
            }
        }

        if (!isPaused) {

            if (isCountingDown) {
                if (countdownAudio != null && !countdownAudio.isPlaying()) {
                    isCountingDown = false;
                    if (backgroundMusic != null) {
                        backgroundMusic.play();
                    }
                }
            } else if (isGameOver) {
                gameOverTimer += delta;
                if (gameOverTimer >= 3.0f) {
                    game.setScreen(new ResultsScreen(game, p1Stats, p2Stats));
                    dispose();
                    return;
                }
            } else {
                if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                    // Synchronize note position using actual audio position
                    currentSongTimeMs = (long) (backgroundMusic.getPosition() * 1000);
                } else {
                    currentSongTimeMs += (long) (delta * 1000);
                }

                if (p1HardModeTimer > 0) {
                    p1HardModeTimer -= delta;
                    if (p1HardModeTimer <= 0) {
                        p1HardModeTimer = 0f;
                        setOpponentChart(true, false);
                    }
                }

                if (p2HardModeTimer > 0) {
                    p2HardModeTimer -= delta;
                    if (p2HardModeTimer <= 0) {
                        p2HardModeTimer = 0f;
                        setOpponentChart(false, false);
                    }
                }

                if (p1DebuffCooldownTimer > 0) {
                    p1DebuffCooldownTimer -= delta;
                    if (p1DebuffCooldownTimer <= 0) {
                        p1DebuffCooldownTimer = 0f;
                    }
                    p1DebuffBar.setValue(p1DebuffCooldownTimer);
                } else {
                    p1DebuffBar.setValue(p1PerfectStreak);
                }

                if (p2DebuffCooldownTimer > 0) {
                    p2DebuffCooldownTimer -= delta;
                    if (p2DebuffCooldownTimer <= 0) {
                        p2DebuffCooldownTimer = 0f;
                    }
                    p2DebuffBar.setValue(p2DebuffCooldownTimer);
                } else {
                    p2DebuffBar.setValue(p2PerfectStreak);
                }

                // Update P1 Keys
                boolean wPressed = updateButtonState(wBtn, drawUp, drawPressedUp, Input.Keys.W);
                boolean aPressed = updateButtonState(aBtn, drawLeft, drawPressedLeft, Input.Keys.A);
                boolean sPressed = updateButtonState(sBtn, drawDown, drawPressedDown, Input.Keys.S);
                boolean dPressed = updateButtonState(dBtn, drawRight, drawPressedRight, Input.Keys.D);

                if (aPressed)
                    processHitAttempt(p1Notes, 0, true);
                if (sPressed)
                    processHitAttempt(p1Notes, 1, true);
                if (wPressed)
                    processHitAttempt(p1Notes, 2, true);
                if (dPressed)
                    processHitAttempt(p1Notes, 3, true);

                // Update P2 Keys
                boolean upPressed = updateButtonState(upBtn, drawUp, drawPressedUp, Input.Keys.UP);
                boolean leftPressed = updateButtonState(leftBtn, drawLeft, drawPressedLeft, Input.Keys.LEFT);
                boolean downPressed = updateButtonState(downBtn, drawDown, drawPressedDown, Input.Keys.DOWN);
                boolean rightPressed = updateButtonState(rightBtn, drawRight, drawPressedRight, Input.Keys.RIGHT);

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

                if (totalSongDurationMs > 0) {
                    float progress = ((float) currentSongTimeMs / totalSongDurationMs) * 100f;
                    if (progress > 100f)
                        progress = 100f;
                    progressBar.setValue(progress);
                }

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

                if (p1MissTimer > 0) {
                    p1MissTimer -= delta;
                    p1CurrentAnim = p1AnimMiss;
                    p1Moving = true;
                }

                if (!p1Moving) {
                    if (p1CurrentAnim != p1AnimIdle) {
                        p1CurrentAnim = p1AnimIdle;
                        p1StateTime = 0f; // Reset only once when switching to idle
                    }
                }
                p1StateTime += delta;
                p1Sprite.setDrawable(new TextureRegionDrawable(p1CurrentAnim.getKeyFrame(p1StateTime, true)));

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

                if (p2MissTimer > 0) {
                    p2MissTimer -= delta;
                    p2CurrentAnim = p2AnimMiss;
                    p2Moving = true;
                }

                if (!p2Moving) {
                    if (p2CurrentAnim != p2AnimIdle) {
                        p2CurrentAnim = p2AnimIdle;
                        p2StateTime = 0f; // Reset only once when switching to idle
                    }
                }
                p2StateTime += delta;
                p2Sprite.setDrawable(new TextureRegionDrawable(p2CurrentAnim.getKeyFrame(p2StateTime, true)));

                if (p1LabelTimer > 0) {
                    p1LabelTimer -= delta;
                    p1IndStateTime += delta;
                    p1HitGradeImage
                            .setDrawable(new TextureRegionDrawable(p1CurrentIndAnim.getKeyFrame(p1IndStateTime, true)));
                    if (p1LabelTimer <= 0)
                        p1HitGradeImage.setVisible(false);
                }
                if (p2LabelTimer > 0) {
                    p2LabelTimer -= delta;
                    p2IndStateTime += delta;
                    p2HitGradeImage
                            .setDrawable(new TextureRegionDrawable(p2CurrentIndAnim.getKeyFrame(p2IndStateTime, true)));
                    if (p2LabelTimer <= 0)
                        p2HitGradeImage.setVisible(false);
                }

                if (health >= 100f || health <= 0f) {
                    isGameOver = true;
                    if (backgroundMusic != null) {
                        backgroundMusic.stop();
                    }
                    if (health >= 100f) {
                        p1EndLabel.setDrawable(new TextureRegionDrawable(new TextureRegion(winMessage)));
                        p1EndLabel.setColor(Color.GREEN);
                        p2EndLabel.setDrawable(new TextureRegionDrawable(new TextureRegion(loseMessage)));
                        p2EndLabel.setColor(Color.RED);
                    } else {
                        p1EndLabel.setDrawable(new TextureRegionDrawable(new TextureRegion(loseMessage)));
                        p1EndLabel.setColor(Color.RED);
                        p2EndLabel.setDrawable(new TextureRegionDrawable(new TextureRegion(winMessage)));
                        p2EndLabel.setColor(Color.GREEN);
                    }
                    p1EndLabel.setVisible(true);
                    p2EndLabel.setVisible(true);
                } else {
                    // Check if the song/level has finished
                    boolean isFinished = false;
                    if (backgroundMusic != null) {
                        // If music was playing and has now stopped (and we're past the first second to
                        // avoid early triggers)
                        if (!backgroundMusic.isPlaying() && currentSongTimeMs > 1000) {
                            isFinished = true;
                        }
                    } else {
                        // Fallback if no music file is present: check if all notes are cleared and at
                        // least 1 second has passed
                        if (p1Notes.isEmpty() && p2Notes.isEmpty() && currentSongTimeMs > 1000) {
                            isFinished = true;
                        }
                    }

                    if (isFinished) {
                        game.setScreen(new ResultsScreen(game, p1Stats, p2Stats));
                        dispose();
                        return;
                    }
                }
            } // End of !isGameOver block

            stage.act(delta);
            overlayStage.act(delta);

        } // End of !isPaused block

        stage.draw();

        // Draw Notes and Receptors over the stage
        // Dynamically align hitboxes to the actual UI buttons
        com.badlogic.gdx.math.Vector2 p1Pos = aBtn.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0));
        com.badlogic.gdx.math.Vector2 p1NextPos = sBtn.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0));
        com.badlogic.gdx.math.Vector2 p2Pos = leftBtn.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0));

        float p1StartX = p1Pos.x;
        float p2StartX = p2Pos.x;
        receptorY = p1Pos.y;

        // Calculate lane width based on the distance between the first two buttons
        laneWidth = p1NextPos.x - p1Pos.x;
        noteSize = aBtn.getWidth();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawHoldBodies(p1Notes, p1StartX);
        drawHoldBodies(p2Notes, p2StartX);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.getBatch().begin();
        updateAndDrawNotes(p1Notes, p1StartX, true);
        updateAndDrawNotes(p2Notes, p2StartX, false);
        stage.getBatch().end();

        overlayStage.draw();

        if (isPaused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage == null)
            return;
        stage.getViewport().update(width, height, true);
        if (overlayStage != null) {
            overlayStage.getViewport().update(width, height, true);
        }
        if (pauseStage != null) {
            pauseStage.getViewport().update(width, height, true);
        }
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
        if (countdownAudio != null) {
            countdownAudio.stop();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (overlayStage != null) {
            overlayStage.dispose();
        }
        if (pauseStage != null) {
            pauseStage.dispose();
        }
        if (blackPixelTexture != null) {
            blackPixelTexture.dispose();
        }
        if (btnPlayTex != null) {
            btnPlayTex.dispose();
        }
        if (btnMenuTex != null) {
            btnMenuTex.dispose();
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
        if (countdownAudio != null) {
            countdownAudio.dispose();
        }
        if (texLeft != null)
            texLeft.dispose();
        if (texDown != null)
            texDown.dispose();
        if (texUp != null)
            texUp.dispose();
        if (texRight != null)
            texRight.dispose();
        if (texPressedLeft != null)
            texPressedLeft.dispose();
        if (texPressedDown != null)
            texPressedDown.dispose();
        if (texPressedUp != null)
            texPressedUp.dispose();
        if (texPressedRight != null)
            texPressedRight.dispose();
        if (flyTexLeft != null)
            flyTexLeft.dispose();
        if (flyTexDown != null)
            flyTexDown.dispose();
        if (flyTexUp != null)
            flyTexUp.dispose();
        if (flyTexRight != null)
            flyTexRight.dispose();
    }

    private void drawHoldBodies(ConcurrentLinkedQueue<GameNote> notes, float startX) {
        for (GameNote note : notes) {
            if (note.isHoldNote) {
                float timeRemaining = note.targetTimeMs - currentSongTimeMs;
                float noteY = receptorY - (timeRemaining * scrollSpeedFactor);

                float endTimeRemaining = note.endTimeMs - currentSongTimeMs;
                float endNoteY = receptorY - (endTimeRemaining * scrollSpeedFactor);

                if (note.isBeingHeld || noteY > receptorY) {
                    noteY = receptorY;
                }

                float height = noteY - endNoteY;
                if (height < 0)
                    continue;

                float noteX = startX + (note.laneIndex * laneWidth);

                shapeRenderer.setColor(0.5f, 0.8f, 1f, 0.7f); // Semi-transparent blue
                shapeRenderer.rect(noteX + noteSize * 0.3f, endNoteY + noteSize * 0.5f, noteSize * 0.4f, height);
            }
        }
    }
}
