package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HowToPlay implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;

    private Image image;
    private Texture nextTexture;
    private Texture backTexture;
    private Image nextButton;
    private Image backButton;

    private int page = 0;

    private String[] slides = {
            "howtoplay1.png",
            "howtoplay2.png",
            "howtoplay3.png"
    };

    public HowToPlay(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // IMAGE
        image = new Image(new Texture(Gdx.files.internal(slides[page])));
        image.setFillParent(true);
        stage.addActor(image);

        // BUTTONS
        nextTexture = new Texture(Gdx.files.internal("buttonnext.png"));
        backTexture = new Texture(Gdx.files.internal("buttonback.png"));
        nextButton = new Image(nextTexture);
        backButton = new Image(backTexture);

        nextButton.setSize(120, 50);
        backButton.setSize(120, 50);

        nextButton.setPosition(Gdx.graphics.getWidth() - nextButton.getWidth() - 20, 20);
        backButton.setPosition(20, 20);

        stage.addActor(nextButton);
        stage.addActor(backButton);

        // NEXT
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (page < slides.length - 1) {
                    page++;
                    updateImage();
                } else {
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        });

        // BACK
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (page > 0) {
                    page--;
                    updateImage();
                } else {
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        });
    }

    private void updateImage() {
        image.setDrawable(
                new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
                        new Texture(Gdx.files.internal(slides[page]))));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (nextButton != null) {
            nextButton.setPosition(width - nextButton.getWidth() - 20, 20);
        }
        if (backButton != null) {
            backButton.setPosition(20, 20);
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
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (nextTexture != null) {
            nextTexture.dispose();
        }
        if (backTexture != null) {
            backTexture.dispose();
        }
    }
}
