package io.github.MAC.mp125;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import io.github.MAC.mp125.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainMendoza extends Game {
    
    public Music bgMusic;
    private float targetVolume = 0.5f;
    private float currentVolume = 0.5f;
    private float fadeSpeed = 0.5f; // Volume units per second

    @Override
    public void create() {
        if (Gdx.files.internal("GameMusic.wav").exists()) {
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("GameMusic.wav"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(currentVolume);
            bgMusic.play();
        }
        setScreen(new MainMenuScreen(this));
    }

    public void fadeOutBGM() {
        targetVolume = 0f;
    }

    public void fadeInBGM() {
        targetVolume = 0.5f;
        if (bgMusic != null && !bgMusic.isPlaying()) {
            currentVolume = 0f;
            bgMusic.setVolume(currentVolume);
            bgMusic.play();
        }
    }

    public void stopBGM() {
        if (bgMusic != null) {
            bgMusic.stop();
            currentVolume = 0f;
            targetVolume = 0f;
        }
    }

    @Override
    public void render() {
        super.render();
        if (bgMusic != null && bgMusic.isPlaying()) {
            if (currentVolume < targetVolume) {
                currentVolume += fadeSpeed * Gdx.graphics.getDeltaTime();
                if (currentVolume > targetVolume) currentVolume = targetVolume;
                bgMusic.setVolume(currentVolume);
            } else if (currentVolume > targetVolume) {
                currentVolume -= fadeSpeed * Gdx.graphics.getDeltaTime();
                if (currentVolume <= 0f) {
                    currentVolume = 0f;
                    bgMusic.pause();
                }
                bgMusic.setVolume(currentVolume);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (bgMusic != null) {
            bgMusic.dispose();
        }
    }
}
