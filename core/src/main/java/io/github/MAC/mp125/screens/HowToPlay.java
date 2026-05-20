package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HowToPlayScreen implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;

    private Image image;
    private TextButton nextButton;
    private TextButton backButton;

    private int page = 0;

    private String[] slides = {
        "howto1.png",
        "howto2.png",
        "howto3.png"
    };

    public HowToPlayScreen(Game game) {
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
        nextButton = new TextButton("Next", skin);
        backButton = new TextButton("Back", skin);

        nextButton.setPosition(Gdx.graphics.getWidth() - 120, 20);
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
                new Texture(Gdx.files.internal(slides[page]))
            )
        );
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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
