package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CharacterSelectScreen implements Screen {
    private Game game;

    private Stage stage;
    private Skin skin;
    private Image background;

    private Table rootTable;

    // get ready for battle banner
    private Label battleBanner;

    // remember sprite selected
    public static int selectedP1SpriteIndex = 0;
    public static int selectedP2SpriteIndex = 0;

    // Sprite carousel fields | TODO: @grace Change colors to sprite assets |
    // Update: Resolved
    private Image p1Sprite, p2Sprite;
    private Texture[] characterSprites;
    private int p1SpriteIndex = 0;
    private int p2SpriteIndex = 0;

    public CharacterSelectScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // BACKGROUND
        Texture bgTexture = new Texture(Gdx.files.internal("CharacterSelectScreenBg.png"));
        background = new Image(bgTexture);
        background.setFillParent(true);

        stage.addActor(background);

        // MAIN TABLE
        rootTable = new Table();
        rootTable.setFillParent(true);

        stage.addActor(rootTable);

        // CHARACTER TEXTURES
        characterSprites = new Texture[] {
                new Texture(Gdx.files.internal("meeracharsel.png")),
                new Texture(Gdx.files.internal("kooacharsel.png")),
                new Texture(Gdx.files.internal("aleiancharsel.png"))
        };

        // CHARACTER IMAGES
        p1Sprite = new Image(characterSprites[0]);
        p2Sprite = new Image(characterSprites[0]);

        // PLAYER LABELS
        Label p1Label = new Label("PLAYER 1", skin);
        Label p2Label = new Label("PLAYER 2", skin);

        p1Label.setAlignment(Align.center);
        p2Label.setAlignment(Align.center);

        // READY LABEL
        battleBanner = new Label("PRESS ENTER TO CONTINUE", skin);
        battleBanner.setAlignment(Align.center);

        // LAYOUT

        // TOP LABELS
        rootTable.add(p1Label)
                .expandX()
                .left()
                .padLeft(120)
                .padTop(40);

        rootTable.add(p2Label)
                .expandX()
                .right()
                .padRight(120)
                .padTop(40);

        rootTable.row();

        // BIG CHARACTER SPRITES
        rootTable.add(p1Sprite)
                .width(500)
                .height(500)
                .expand()
                .left()
                .padLeft(50)
                .padTop(20);

        rootTable.add(p2Sprite)
                .width(500)
                .height(500)
                .expand()
                .right()
                .padRight(50)
                .padTop(20);

        rootTable.row();

        // CONTROLS TEXT
        Label p1Controls = new Label("A / D", skin);
        Label p2Controls = new Label("LEFT / RIGHT", skin);

        p1Controls.setAlignment(Align.center);
        p2Controls.setAlignment(Align.center);

        rootTable.add(p1Controls)
                .left()
                .padLeft(220)
                .padBottom(20);

        rootTable.add(p2Controls)
                .right()
                .padRight(180)
                .padBottom(20);

        rootTable.row();

        // ENTER LABEL
        rootTable.add(battleBanner)
                .colspan(2)
                .center()
                .padBottom(40);

        System.out.println("Character Select Screen");
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // P1 Image Carousel
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            p1SpriteIndex--;
            if (p1SpriteIndex < 0)
                p1SpriteIndex = characterSprites.length - 1;
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(characterSprites[p1SpriteIndex])));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            p1SpriteIndex++;
            if (p1SpriteIndex >= characterSprites.length)
                p1SpriteIndex = 0;
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(characterSprites[p1SpriteIndex])));
        }

        // P2 Image Carousel
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            p2SpriteIndex--;
            if (p2SpriteIndex < 0)
                p2SpriteIndex = characterSprites.length - 1;
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(characterSprites[p2SpriteIndex])));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            p2SpriteIndex++;
            if (p2SpriteIndex >= characterSprites.length)
                p2SpriteIndex = 0;
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(characterSprites[p2SpriteIndex])));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            selectedP1SpriteIndex = p1SpriteIndex;
            selectedP2SpriteIndex = p2SpriteIndex;
            battleBanner.setVisible(true);
            game.setScreen(new SongSelectScreen(game));
        }

        stage.act(delta);
        stage.draw();
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
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (characterSprites != null) {
            for (Texture tex : characterSprites) {
                tex.dispose();
            }
        }
    }
}
