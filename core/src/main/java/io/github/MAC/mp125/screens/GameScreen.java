package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
public class GameScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Texture bgTexture;
    private ProgressBar healthBar;
    private ProgressBar progressBar;
    private float health = 50f;

    private Texture[] actionSprites;
    private Image p1Sprite, p2Sprite;

    private Label p1ScoreLabel, p2ScoreLabel;
    private int p1Score = 0;
    private int p2Score = 0;

    // P1 Keys
    private TextButton wBtn, aBtn, sBtn, dBtn;

    // P2 Keys
    private TextButton upBtn, leftBtn, downBtn, rightBtn;

    public GameScreen(Game game) {
        this.game = game;
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
        actionSprites = new Texture[] {
            new Texture(Gdx.files.internal("default.png")),
            new Texture(Gdx.files.internal("howtoplay1.png")),
            new Texture(Gdx.files.internal("howtoplay2.png")),
            new Texture(Gdx.files.internal("howtoplay3.png"))
        };
        p1Sprite = new Image(actionSprites[0]);
        p2Sprite = new Image(actionSprites[0]);

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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update P1 Keys (Moves health right)
        if (updateButtonState(wBtn, Input.Keys.W)) {
            health += 2f;
            p1Score += 20;
        }
        if (updateButtonState(aBtn, Input.Keys.A)) {
            health += 2f;
            p1Score += 20;
        }
        if (updateButtonState(sBtn, Input.Keys.S)) {
            health += 2f;
            p1Score += 20;
        }
        if (updateButtonState(dBtn, Input.Keys.D)) {
            health += 2f;
            p1Score += 20;
        }

        // Update P2 Keys (Moves health left)
        if (updateButtonState(upBtn, Input.Keys.UP)) {
            health -= 2f;
            p2Score += 20;
        }
        if (updateButtonState(leftBtn, Input.Keys.LEFT)) {
            health -= 2f;
            p2Score += 20;
        }
        if (updateButtonState(downBtn, Input.Keys.DOWN)) {
            health -= 2f;
            p2Score += 20;
        }
        if (updateButtonState(rightBtn, Input.Keys.RIGHT)) {
            health -= 2f;
            p2Score += 20;
        }

        p1ScoreLabel.setText("Score: " + p1Score);
        p2ScoreLabel.setText("Score: " + p2Score);

        // Clamp health
        if (health >= 100)
            health = 100f;
        if (health <= 0)
            health = 0f;
        healthBar.setValue(health);

        //TODO: @grace Update Placeholder Sprite with Images
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[1])));
        else if (Gdx.input.isKeyPressed(Input.Keys.A))
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[2])));
        else if (Gdx.input.isKeyPressed(Input.Keys.S))
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[3])));
        else if (Gdx.input.isKeyPressed(Input.Keys.D))
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[1])));
        else
            p1Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[0])));

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[1])));
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[2])));
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[3])));
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[1])));
        else
            p2Sprite.setDrawable(new TextureRegionDrawable(new TextureRegion(actionSprites[0])));

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new ResultsScreen(game));
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
        if (stage != null)
            stage.dispose();
        if (skin != null)
            skin.dispose();
        if (bgTexture != null)
            bgTexture.dispose();
        if (actionSprites != null) {
            for (Texture tex : actionSprites) {
                tex.dispose();
            }
        }
    }
}
