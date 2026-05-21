package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SongSelectScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;

    private Table rootTable;

    // full screen dynamic background (preview system)
    private Image songPreviewBackground;

    // song buttons
    private TextButton song1Button;
    private TextButton song2Button;
    private TextButton song3Button;

    // overlay text
    private Label startBanner;

    // preview (placeholder for now)
    private Label songPreview;

    public SongSelectScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // initial song preview background (default state)
        songPreviewBackground = new Image(
                new Texture(Gdx.files.internal("bg.png")));

        songPreviewBackground.setFillParent(true);
        stage.addActor(songPreviewBackground);

        // root layout
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // song buttons
        song1Button = new TextButton("SONG 1", skin);
        song2Button = new TextButton("SONG 2", skin);
        song3Button = new TextButton("SONG 3", skin);

        ChangeListener playListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        };

        song1Button.addListener(playListener);
        song2Button.addListener(playListener);
        song3Button.addListener(playListener);

        Table songBar = new Table();

        songBar.add(song1Button).width(180).height(70).pad(10);
        songBar.add(song2Button).width(180).height(70).pad(10);
        songBar.add(song3Button).width(180).height(70).pad(10);

        rootTable.add(songBar).top().padTop(20);
        rootTable.row();

        // center preview text (fallback)
        songPreview = new Label("SONG PREVIEW AREA", skin);
        rootTable.add(songPreview).expand().center();

        // overlay: start banner
        startBanner = new Label("PRESS ENTER TO PLAY", skin);
        rootTable.add(songPreview).expand().center();

        startBanner = new Label("PRESS ENTER TO PLAY", skin);

        startBanner.setPosition(
                Gdx.graphics.getWidth() / 2f - 150, 60);

        startBanner.setVisible(true);
        stage.addActor(startBanner);

        System.out.println("Song Select Screen");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
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
    }
}

// todo: kevin can implement songPreviewBackground.setDrawable(...) safely
// todo: or later upgrade to fade transitions, shader effects without touching
// UI layout :DD yay
// todo: smooth fade between images (optional)
