package io.github.MAC.mp125.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ResultsScreen implements Screen {

    private Game game;

    private Stage stage;
    private Skin skin;

    private io.github.MAC.mp125.PlayerStats p1Stats;
    private io.github.MAC.mp125.PlayerStats p2Stats;

    // Sprite assets
    private Texture numbersSheet;
    private Texture resultsSheet;
    private Texture winBanner;
    private Texture loseBanner;
    private Texture p1PortraitTex;
    private Texture p2PortraitTex;
    private Texture bgTexture;

    private TextureRegion[] digitRegions;
    private TextureRegion[] labelRegions;
    private Sound clickSound;
    private com.badlogic.gdx.audio.Music bgMusic;
    private Image background;

    public ResultsScreen(Game game, io.github.MAC.mp125.PlayerStats p1Stats, io.github.MAC.mp125.PlayerStats p2Stats) {
        this.game = game;
        this.p1Stats = p1Stats != null ? p1Stats : new io.github.MAC.mp125.PlayerStats();
        this.p2Stats = p2Stats != null ? p2Stats : new io.github.MAC.mp125.PlayerStats();
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        bgTexture = new Texture(Gdx.files.internal("resultscreenbg.png"));
        background = new Image(bgTexture);
        background.setFillParent(true);
        stage.addActor(background);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("clickButton.ogg"));
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("resultsMusic.ogg"));
        bgMusic.setLooping(true);
        bgMusic.play();

        numbersSheet = new Texture(Gdx.files.internal("numbersspritesheet.png"));
        TextureRegion[][] numTemp = TextureRegion.split(numbersSheet, numbersSheet.getWidth() / 2,
                numbersSheet.getHeight() / 5);
        digitRegions = new TextureRegion[10];
        digitRegions[0] = numTemp[4][1];
        digitRegions[1] = numTemp[0][0];
        digitRegions[2] = numTemp[0][1];
        digitRegions[3] = numTemp[1][0];
        digitRegions[4] = numTemp[1][1];
        digitRegions[5] = numTemp[2][0];
        digitRegions[6] = numTemp[2][1];
        digitRegions[7] = numTemp[3][0];
        digitRegions[8] = numTemp[3][1];
        digitRegions[9] = numTemp[4][0];

        resultsSheet = new Texture(Gdx.files.internal("resultsspritesheet.png"));
        TextureRegion[][] resTemp = TextureRegion.split(resultsSheet, resultsSheet.getWidth(),
                resultsSheet.getHeight() / 7);
        labelRegions = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            labelRegions[i] = resTemp[i][0];
        }

        winBanner = new Texture(Gdx.files.internal("labelyouwin.png"));
        loseBanner = new Texture(Gdx.files.internal("labelyoulose.png"));

        String[] characterPortraits = { "meerabig.png", "kooabig.png", "aleianbig.png" };
        String p1Choice = characterPortraits[CharacterSelectScreen.selectedP1SpriteIndex];
        String p2Choice = characterPortraits[CharacterSelectScreen.selectedP2SpriteIndex];

        p1PortraitTex = new Texture(Gdx.files.internal(p1Choice));
        p2PortraitTex = new Texture(Gdx.files.internal(p2Choice));

        // =========================
        // PORTRAIT CONTAINERS (LEFT & RIGHT)
        // =========================
        Table p1Container = new Table();
        Image p1Portrait = new Image(p1PortraitTex);

        Table p2Container = new Table();
        Image p2Portrait = new Image(p2PortraitTex);

        Image p1Outcome = null;
        Image p2Outcome = null;

        if (p1Stats.score > p2Stats.score) {
            p1Outcome = new Image(winBanner);
            p2Outcome = new Image(loseBanner);
        } else if (p2Stats.score > p1Stats.score) {
            p1Outcome = new Image(loseBanner);
            p2Outcome = new Image(winBanner);
        }

        if (p1Outcome != null) {
            p1Container.add(p1Outcome).size(180, 60).padBottom(-20).row();
        }
        p1Container.add(p1Portrait).size(400, 400);

        if (p2Outcome != null) {
            p2Container.add(p2Outcome).size(180, 60).padBottom(-20).row();
        }
        p2Container.add(p2Portrait).size(400, 400);

        p1Container.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        p2Container.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        // =========================
        // STATS TABLE (CENTER)
        // =========================
        Table statsTable = new Table();
        statsTable.add(makeRow(labelRegions[0], p1Stats.totalNotes, p2Stats.totalNotes)).padTop(50).padBottom(-20)
                .row();
        statsTable.add(makeRow(labelRegions[1], p1Stats.maxCombo, p2Stats.maxCombo)).padBottom(-20).row();
        statsTable.add(makeRow(labelRegions[2], p1Stats.perfects, p2Stats.perfects)).padBottom(-20).row();
        statsTable.add(makeRow(labelRegions[3], p1Stats.goods, p2Stats.goods)).padBottom(-20).row();
        statsTable.add(makeRow(labelRegions[4], p1Stats.mehs, p2Stats.mehs)).padBottom(-20).row();
        statsTable.add(makeRow(labelRegions[5], p1Stats.misses, p2Stats.misses)).padBottom(-20).row();

        statsTable.add(makeScoreRow(labelRegions[6], p1Stats.score, p2Stats.score));

        // =========================
        // PORTRAIT BACKGROUND LAYOUT
        // =========================
        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);
        stage.addActor(backgroundTable);

        backgroundTable.add(p1Container).expandX().left().pad(20).padBottom(40);
        backgroundTable.add().expandX();
        backgroundTable.add(p2Container).expandX().right().pad(20).padBottom(40);

        // =========================
        // ROOT LAYOUT (FOREGROUND)
        // =========================
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(statsTable).expand().center().padTop(50);

        root.row();

        // =========================
        // BACK BUTTON
        // =========================
        Image backButton = new Image(new Texture(Gdx.files.internal("buttonbacktomenu.png")));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                if (bgMusic != null) {
                    bgMusic.stop();
                }
                if (game instanceof io.github.MAC.mp125.MainMendoza) {
                    ((io.github.MAC.mp125.MainMendoza) game).fadeInBGM();
                }
                game.setScreen(new MainMenuScreen(game));
            }
        });

        root.add(backButton).colspan(3)
                .size(240, 80)
                .padTop(10)
                .padBottom(20);
    }

    private Table renderNumber(int number, TextureRegion[] digitRegions, float scale) {
        Table table = new Table();
        String digits = String.valueOf(number);
        float baseWidth = (float) numbersSheet.getWidth() / 2f;
        float baseHeight = (float) numbersSheet.getHeight() / 5f;
        float width = baseWidth * scale;
        float height = baseHeight * scale;

        for (int i = 0; i < digits.length(); i++) {
            char c = digits.charAt(i);
            int digitIndex = c - '0';
            Image digitImg = new Image(new TextureRegionDrawable(digitRegions[digitIndex]));
            table.add(digitImg).size(width, height).pad(2);
        }
        return table;
    }

    private Table makeRow(TextureRegion labelRegion, int p1Val, int p2Val) {
        Table row = new Table();

        Table leftNum = renderNumber(p1Val, digitRegions, 0.28f);

        float labelWidth = (float) resultsSheet.getWidth() * 0.20f;
        float labelHeight = ((float) resultsSheet.getHeight() / 7f) * 0.20f;
        Image labelImg = new Image(new TextureRegionDrawable(labelRegion));

        Table rightNum = renderNumber(p2Val, digitRegions, 0.28f);

        row.add(leftNum).width(160).right().padRight(20);
        row.add(labelImg).size(labelWidth, labelHeight).center();
        row.add(rightNum).width(160).left().padLeft(20);

        return row;
    }

    private Table makeScoreRow(TextureRegion labelRegion, int p1Val, int p2Val) {
        Table row = new Table();

        Table leftNum = renderNumber(p1Val, digitRegions, 0.40f);

        float labelWidth = (float) resultsSheet.getWidth() * 0.3f;
        float labelHeight = ((float) resultsSheet.getHeight() / 7f) * 0.3f;
        Image labelImg = new Image(new TextureRegionDrawable(labelRegion));

        Table rightNum = renderNumber(p2Val, digitRegions, 0.40f);

        row.add(leftNum).width(200).right().padRight(20);
        row.add(labelImg).size(labelWidth, labelHeight).center();
        row.add(rightNum).width(200).left().padLeft(20);

        return row;
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
        if (stage != null) {
            stage.getViewport().update(width, height, true);
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
        if (numbersSheet != null)
            numbersSheet.dispose();
        if (resultsSheet != null)
            resultsSheet.dispose();
        if (winBanner != null)
            winBanner.dispose();
        if (loseBanner != null)
            loseBanner.dispose();
        if (p1PortraitTex != null)
            p1PortraitTex.dispose();
        if (p2PortraitTex != null)
            p2PortraitTex.dispose();
        if (clickSound != null)
            clickSound.dispose();
        if (bgMusic != null)
            bgMusic.dispose();
        if (bgTexture != null)
            bgTexture.dispose();
    }
}
