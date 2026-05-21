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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;

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

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Player 1 Buttons (WASD)
        wBtn = new TextButton("W", skin);
        aBtn = new TextButton("A", skin);
        sBtn = new TextButton("S", skin);
        dBtn = new TextButton("D", skin);

        Table p1Table = new Table();
        p1Table.add(wBtn).width(60).height(60).pad(5).colspan(3).row();
        p1Table.add(aBtn).width(60).height(60).pad(5);
        p1Table.add(sBtn).width(60).height(60).pad(5);
        p1Table.add(dBtn).width(60).height(60).pad(5);

        // Player 2 Buttons (Arrows)
        upBtn = new TextButton("^", skin);
        leftBtn = new TextButton("<", skin);
        downBtn = new TextButton("v", skin);
        rightBtn = new TextButton(">", skin);

        Table p2Table = new Table();
        p2Table.add(upBtn).width(60).height(60).pad(5).colspan(3).row();
        p2Table.add(leftBtn).width(60).height(60).pad(5);
        p2Table.add(downBtn).width(60).height(60).pad(5);
        p2Table.add(rightBtn).width(60).height(60).pad(5);

        // Add to root table
        rootTable.add(p1Table).expand().left().pad(50).bottom();
        rootTable.add(p2Table).expand().right().pad(50).bottom();

        System.out.println("Game Screen Started");
    }

    private void updateButtonState(TextButton btn, int keycode) {
        if (Gdx.input.isKeyPressed(keycode)) {
            btn.setColor(Color.GREEN);
        } else {
            btn.setColor(Color.WHITE);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update P1 Keys
        updateButtonState(wBtn, Input.Keys.W);
        updateButtonState(aBtn, Input.Keys.A);
        updateButtonState(sBtn, Input.Keys.S);
        updateButtonState(dBtn, Input.Keys.D);

        // Update P2 Keys
        updateButtonState(upBtn, Input.Keys.UP);
        updateButtonState(leftBtn, Input.Keys.LEFT);
        updateButtonState(downBtn, Input.Keys.DOWN);
        updateButtonState(rightBtn, Input.Keys.RIGHT);

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
    }
}
