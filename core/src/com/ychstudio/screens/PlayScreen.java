package com.ychstudio.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.PacMan;
import com.ychstudio.components.MovementComponent;
import com.ychstudio.components.PlayerComponent;
import com.ychstudio.components.StateComponent;
import com.ychstudio.components.TextureComponent;
import com.ychstudio.components.TransformComponent;
import com.ychstudio.gamesys.GameManager;
import com.ychstudio.systems.MovementSystem;
import com.ychstudio.systems.PlayerSystem;
import com.ychstudio.systems.RenderSystem;
import com.ychstudio.systems.StateSystem;

public class PlayScreen implements Screen {

    private final float WIDTH = 19.0f;
    private final float HEIGHT = 23.0f;

    private PacMan game;
    private SpriteBatch batch;
    private AssetManager assetManager;

    private FitViewport viewport;
    private OrthographicCamera camera;

    private Engine engine;
    
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public PlayScreen(PacMan game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        assetManager = GameManager.instance.assetManager;
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.translate(WIDTH / 2, HEIGHT / 2);
        camera.update();

        batch = new SpriteBatch();
        engine = new Engine();
        engine.addSystem(new PlayerSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new StateSystem());
        engine.addSystem(new RenderSystem(batch));

        MovementComponent playerMovement = new MovementComponent();

        Entity player = new Entity();
        player.add(new PlayerComponent());
        player.add(new TransformComponent(7f, 4.3f));
        player.add(playerMovement);
        player.add(new StateComponent());
        TextureAtlas textureAtlas = assetManager.get("images/actors.pack", TextureAtlas.class);
        player.add(new TextureComponent(new TextureRegion(textureAtlas.findRegion("Pacman"), 0, 0, 16, 16)));

        engine.addEntity(player);
        
        // load map
        tiledMap = new TmxMapLoader().load("map/map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f, batch);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        
        batch.setProjectionMatrix(camera.combined);
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }

}
