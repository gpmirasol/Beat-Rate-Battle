package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class MainMenuScreen implements Screen {
    private Stage stage;
    private Table table;
    private Image background;

    private ImageButton playButton;
    private Texture playTexture;
    private ImageButton howToPlayButton;
    private Texture howToPlayTexture;

    private Skin skin;
    private Game game;

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Texture bgTexture = new Texture(Gdx.files.internal("bg.png"));
        background = new Image(bgTexture);
        background.setFillParent(true);
        stage.addActor(background);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.center();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        playTexture = new Texture(Gdx.files.internal("buttonplay.png"));
        playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playTexture)));
        howToPlayTexture = new Texture(Gdx.files.internal("buttonhowtoplay.png"));
        howToPlayButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(howToPlayTexture)));

        table.add(playButton).width(250).height(80).pad(10);
        table.row();
        table.add(howToPlayButton).width(250).height(80);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CharacterSelectScreen(game));
            }
        });

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HowToPlay(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        // Draw screen here, "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (playTexture != null) {
            playTexture.dispose();
        }
    }
}
