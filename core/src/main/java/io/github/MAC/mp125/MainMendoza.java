package io.github.MAC.mp125;

import com.badlogic.gdx.Game;
import io.github.MAC.mp125.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainMendoza extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
