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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SongSelectScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;

    // remember song selection
    public static int selectedSongIndex = 0;

    // song base names matching assets
    private String[] songNames = { "Apple", "Beauty and a Beat", "Gentleman" };

    private String[] bgNames = { "apple", "beauty", "gentleman" };

    private Table rootTable;

    // full screen dynamic background (preview system)
    private Image songPreviewBackground;

    private Texture appleBackgroundTexture;
    private Texture beautyBackgroundTexture;
    private Texture gentlemanBackgroundTexture;

    // song buttons
    private TextButton song1Button;
    private TextButton song2Button;
    private TextButton song3Button;

    // overlay text
    private Label startBanner;

    private Music previewMusic;
    private int currentPlayingIndex = -1;
    private float previewDelayTimer = 0f;

    public SongSelectScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // BACKGROUND TEXTURES
        appleBackgroundTexture = new Texture(Gdx.files.internal("applebackground.png"));
        beautyBackgroundTexture = new Texture(Gdx.files.internal("beautybackground.png"));
        gentlemanBackgroundTexture = new Texture(Gdx.files.internal("gentlemanbackground.png"));

        // DEFAULT BACKGROUND
        songPreviewBackground = new Image(appleBackgroundTexture);

        songPreviewBackground.setFillParent(true);
        stage.addActor(songPreviewBackground);

        // root layout
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // song buttons
        song1Button = new TextButton(songNames[0], skin);
        song2Button = new TextButton(songNames[1], skin);
        song3Button = new TextButton(songNames[2], skin);

        song1Button.setTouchable(Touchable.disabled);
        song2Button.setTouchable(Touchable.disabled);
        song3Button.setTouchable(Touchable.disabled);

        Table songBar = new Table();

        songBar.add(song1Button).width(180).height(70).pad(10);
        songBar.add(song2Button).width(180).height(70).pad(10);
        songBar.add(song3Button).width(180).height(70).pad(10);

        rootTable.add(songBar).top().padTop(20);
        rootTable.row();

        // center preview text (fallback)

        startBanner = new Label("PRESS ENTER TO PLAY", skin);

        rootTable.row();

        rootTable.add(startBanner)
                .colspan(1)
                .center()
                .padBottom(40);

        System.out.println("Song Select Screen");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            selectedSongIndex--;
            if (selectedSongIndex < 0)
                selectedSongIndex = 2;
            previewDelayTimer = 0f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            selectedSongIndex++;
            if (selectedSongIndex > 2)
                selectedSongIndex = 0;
            previewDelayTimer = 0f;
        }

        song1Button.setColor(Color.WHITE);
        song2Button.setColor(Color.WHITE);
        song3Button.setColor(Color.WHITE);

        if (selectedSongIndex == 0)
            song1Button.setColor(Color.GREEN);
        else if (selectedSongIndex == 1)
            song2Button.setColor(Color.GREEN);
        else if (selectedSongIndex == 2)
            song3Button.setColor(Color.GREEN);

        if (currentPlayingIndex != selectedSongIndex) {
            if (previewMusic != null) {
                previewMusic.stop();
                previewMusic.dispose();
                previewMusic = null;
            }
            previewDelayTimer += delta;
            if (previewDelayTimer >= 1.0f) {
                String songName = songNames[selectedSongIndex];
                if (Gdx.files.internal(songName + ".wav").exists()) {
                    previewMusic = Gdx.audio.newMusic(Gdx.files.internal(songName + ".wav"));
                    previewMusic.setLooping(true);
                    previewMusic.play();
                }
                currentPlayingIndex = selectedSongIndex;
            }
        }

        // CHANGE BACKGROUND BASED ON SONG
        if (selectedSongIndex == 0) {
            songPreviewBackground.setDrawable(
                new Image(appleBackgroundTexture).getDrawable());
        }

        else if (selectedSongIndex == 1) {
            songPreviewBackground.setDrawable(
                new Image(beautyBackgroundTexture).getDrawable());
        }

        else if (selectedSongIndex == 2) {
            songPreviewBackground.setDrawable(
                new Image(gentlemanBackgroundTexture).getDrawable());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (previewMusic != null) {
                previewMusic.stop();
            }
            game.setScreen(new GameScreen(game, songNames[selectedSongIndex],bgNames[selectedSongIndex]));
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
        if (previewMusic != null) {
            previewMusic.stop();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        appleBackgroundTexture.dispose();
        beautyBackgroundTexture.dispose();
        gentlemanBackgroundTexture.dispose();
        if (previewMusic != null) {
            previewMusic.dispose();
        }
    }
}
