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

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CharacterSelectScreen implements Screen {
    private Game game;

    private Stage stage;
    private Skin skin;
    private Image background;

    private Table rootTable;

    // preview placeholders
    private Label p1Preview;
    private Label p2Preview;

    // character buttons
    private TextButton p1Char1Button;
    private TextButton p1Char2Button;

    private TextButton p2Char1Button;
    private TextButton p2Char2Button;

    // get ready for battle banner
    private Label battleBanner;

    public CharacterSelectScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Texture bgTexture = new Texture(Gdx.files.internal("CharacterSelectScreenBg.png"));
        background = new Image(bgTexture);
        background.setFillParent(true);

        stage.addActor(background);

        rootTable = new Table();
        rootTable.setFillParent(true);

        stage.addActor(rootTable);

        // top preview area
        p1Preview = new Label("P1 PREVIEW", skin);
        p2Preview = new Label("P2 PREVIEW", skin);

        p1Preview.setAlignment(Align.center);
        p2Preview.setAlignment(Align.center);

        // character buttons - player 1
        p1Char1Button = new TextButton("P1 Char 1", skin);
        p1Char2Button = new TextButton("P1 Char 2", skin);

        // character buttons - player 2
        p2Char1Button = new TextButton("P2 Char 1", skin);
        p2Char2Button = new TextButton("P2 Char 2", skin);

        // player 1 button area - left side
        Table p1ButtonsTable = new Table();

        p1ButtonsTable.add(p1Char1Button)
            .width(180)
            .height(70)
            .pad(10);

        p1ButtonsTable.add(p1Char2Button)
            .width(180)
            .height(70)
            .pad(10);

        // player 2 button area - left side
        Table p2ButtonsTable = new Table();

        p2ButtonsTable.add(p2Char1Button)
            .width(180)
            .height(70)
            .pad(10);

        p2ButtonsTable.add(p2Char2Button)
            .width(180)
            .height(70)
            .pad(10);

        // battle banner
        battleBanner = new Label("GET READY FOR BATTLE", skin);
        battleBanner.setAlignment(Align.center);
        battleBanner.setVisible(false); // ‼️‼️‼️ @kevin set visible true in logic part upon pressing "enter"

        battleBanner.setPosition(
            Gdx.graphics.getWidth() / 2f - 180,
            Gdx.graphics.getHeight() / 2f
        );

        stage.addActor(battleBanner);

        // MAIN LAYOUT
        // TOP PREVIEW ROW
        rootTable.add(p1Preview)
            .expandX()
            .center()
            .padTop(50);

        rootTable.add(p2Preview)
            .expandX()
            .center()
            .padTop(50);

        rootTable.row();

        // EMPTY SPACE
        rootTable.add().height(300);
        rootTable.add();

        rootTable.row();

        // BOTTOM BUTTON ROW
        rootTable.add(p1ButtonsTable)
            .expandX()
            .left()
            .padLeft(50)
            .padBottom(50);

        rootTable.add(p2ButtonsTable)
            .expandX()
            .right()
            .padRight(50)
            .padBottom(50);

        System.out.println("Character Select Screen");
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            battleBanner.setVisible(true);
            game.setScreen(new SongSelectScreen(game));
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage == null) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
