package io.github.MAC.mp125;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainMendoza extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}