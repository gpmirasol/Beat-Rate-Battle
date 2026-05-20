package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ResultsScreen implements Screen {

    private Game game;

    private Stage stage;
    private Skin skin;

    public ResultsScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // =========================
        // SPRITES (PLACEHOLDERS)
        // =========================

        Image p1Sprite = new Image(new Texture("p1.png"));
        Image p2Sprite = new Image(new Texture("p2.png"));

        // =========================
        // STATS TABLE (CENTER)
        // =========================

        Label.LabelStyle style = skin.get(Label.LabelStyle.class);

        Table statsTable = new Table();

        statsTable.add(makeRow("TOTAL NOTES", "0", "0", style)).row();
        statsTable.add(makeRow("MAX COMBO", "0", "0", style)).row();
        statsTable.add(makeRow("PERFECT", "0", "0", style)).row();
        statsTable.add(makeRow("GOOD", "0", "0", style)).row();
        statsTable.add(makeRow("MEH", "0", "0", style)).row();
        statsTable.add(makeRow("MISS", "0", "0", style)).row();

        statsTable.add(makeScoreRow("SCORE", "0000", "0000", style));

        // =========================
        // ROOT LAYOUT
        // =========================

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(p1Sprite).expandX().left().pad(30);
        root.add(statsTable).expand().center();
        root.add(p2Sprite).expandX().right().pad(30);

        root.row();

        // =========================
        // BACK BUTTON
        // =========================

        TextButton backButton = new TextButton("BACK TO MENU", skin);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        root.add(backButton).colspan(3).padTop(40);
    }

    // =========================
    // ROW (0 | LABEL | 0)
    // =========================

    private Table makeRow(String label, String p1, String p2, Label.LabelStyle style) {

        Table row = new Table();

        Label left = new Label(p1, style);
        Label mid = new Label(label, style);
        Label right = new Label(p2, style);

        row.add(left).width(80);
        row.add(mid).width(200);
        row.add(right).width(80);

        return row;
    }

    // =========================
    // SCORE ROW (BIGGER TEXT)
    // =========================

    private Table makeScoreRow(String label, String p1, String p2, Label.LabelStyle style) {

        Table row = new Table();

        Label left = new Label(p1, style);
        Label mid = new Label(label, style);
        Label right = new Label(p2, style);

        left.setFontScale(1.3f);
        right.setFontScale(1.3f);

        row.add(left).width(100);
        row.add(mid).width(200);
        row.add(right).width(100);

        return row;
    }

    // =========================
    // BASIC SCREEN METHODS
    // =========================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
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
