package com.alcovegames.fireshader;

import org.lwjgl.input.Mouse;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class Game implements ApplicationListener {

	public enum ShaderSelection{
		Default,
		Ambiant,
		Light,
		Final
	};

	
	//used for drawing
	private SpriteBatch batch;
	private OrthographicCamera cam;
	private OrthographicCamera cam2d;
	private boolean	lightMove = false;
	private boolean lightOscillate = false;
	private Texture light;
	private FrameBuffer fbo;
	private BitmapFont	bitmapFont;
	private Tilemap tilemap;
	
	//out different shaders. currentShader is just a pointer to the 4 others
	private ShaderSelection	shaderSelection = ShaderSelection.Default;
	private ShaderProgram currentShader;
	private ShaderProgram defaultShader;
	private ShaderProgram ambientShader;
	private ShaderProgram lightShader;
	private ShaderProgram finalShader;
	
	//values passed to the shader
	public static final float ambientIntensity = .7f;
	public static final Vector3 ambientColor = new Vector3(0.3f, 0.3f, 0.7f);

	//used to make the light flicker
	public float zAngle;
	public static final float zSpeed = 15.0f;
	public static final float PI2 = 3.1415926535897932384626433832795f * 2.0f;

	//read our shader files
	final String vertexShader = new FileHandle("data/vertexShader.glsl").readString();
	final String defaultPixelShader = new FileHandle("data/defaultPixelShader.glsl").readString();
	final String ambientPixelShader = new FileHandle("data/ambientPixelShader.glsl").readString();
	final String lightPixelShader =  new FileHandle("data/lightPixelShader.glsl").readString();
	final String finalPixelShader =  new FileHandle("data/pixelShader.glsl").readString();
	
	//change the shader selection
	public void setShader(ShaderSelection ss){
		shaderSelection = ss;
		
		if(ss == ShaderSelection.Final){
			currentShader = finalShader;
		}
		else if(ss == ShaderSelection.Ambiant){
			currentShader = ambientShader;
		}
		else if(ss == ShaderSelection.Light){
			currentShader = lightShader;
		}
		else{
			ss = ShaderSelection.Default;
			currentShader = defaultShader;
		}
	}
	
	@Override
	public void create() {

		ShaderProgram.pedantic = false;
		defaultShader = new ShaderProgram(vertexShader, defaultPixelShader);
		ambientShader = new ShaderProgram(vertexShader, ambientPixelShader);
		lightShader = new ShaderProgram(vertexShader, lightPixelShader);
		finalShader = new ShaderProgram(vertexShader, finalPixelShader);
		setShader(shaderSelection);


		ambientShader.begin();
		ambientShader.setUniformf("ambientColor", ambientColor.x, ambientColor.y,
				ambientColor.z, ambientIntensity);
		ambientShader.end();
		

		lightShader.begin();
		lightShader.setUniformi("u_lightmap", 1);
		lightShader.end();
		
		finalShader.begin();
		finalShader.setUniformi("u_lightmap", 1);
		finalShader.setUniformf("ambientColor", ambientColor.x, ambientColor.y,
				ambientColor.z, ambientIntensity);
		finalShader.end();
		
		
		//declare all stuff we need to draw
		batch = new SpriteBatch();
		tilemap = new Tilemap();
		light = new Texture("data/light.png");
		bitmapFont = new BitmapFont();
		bitmapFont.setUseIntegerPositions(false);
		bitmapFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.setScale(1.0f/32.0f);
		
		//input processing
		Gdx.input.setInputProcessor(new InputAdapter() {
			
			public boolean scrolled(int amount){
				cam.zoom += (float)amount * 0.04f;
				cam.update();
				return false;
			}
			public boolean keyUp(int keycode) {
				if(keycode == Keys.LEFT){
					cam.translate(-1.0f, 0.0f);
					cam.update();
				}
				else if(keycode == Keys.RIGHT){
					cam.translate(1.0f, 0.0f);
					cam.update();
				}
				else if(keycode == Keys.UP){
					cam.translate(0.0f, 1.0f);
					cam.update();
				}
				else if(keycode == Keys.DOWN){
					cam.translate(0.0f, -1.0f);
					cam.update();
				}
				else if(keycode == Keys.NUM_1){
					setShader(ShaderSelection.Default);
				}
				else if(keycode == Keys.NUM_2){
					setShader(ShaderSelection.Ambiant);
				}
				else if(keycode == Keys.NUM_3){
					setShader(ShaderSelection.Light);
				}
				else if(keycode == Keys.NUM_4){
					setShader(ShaderSelection.Final);
				}
				else if(keycode == Keys.SPACE){
					lightOscillate = !lightOscillate;
				}
				
				return false;
			}
			public boolean touchUp(int x, int y, int pointer, int button) {
				lightMove = !lightMove;
				return false;
			}
		});
	}

	@Override
	public void resize(final int width, final int height) {
		cam = new OrthographicCamera(20.0f, 20.0f * height / width);
		cam.position.set(cam.viewportWidth / 2.0f, cam.viewportHeight / 2.0f, 0.0f);
		cam.update();
		
		cam2d = new OrthographicCamera(20.0f, 20.0f * height / width);
		cam2d.position.set(cam.viewportWidth / 2.0f, cam.viewportHeight / 2.0f, 0.0f);
		cam2d.update();


		fbo = new FrameBuffer(Format.RGBA8888, width, height, false);
		 
		lightShader.begin();
		lightShader.setUniformf("resolution", width, height);
		lightShader.end();

		finalShader.begin();
		finalShader.setUniformf("resolution", width, height);
		finalShader.end();
	}

	@Override
	public void render() {
		final float dt = Gdx.graphics.getRawDeltaTime();
		
		if(lightMove){
			tilemap.campFirePosition.set(Mouse.getX(), Gdx.graphics.getHeight() - Mouse.getY(), 0.0f);
			cam.unproject(tilemap.campFirePosition);
		}
		
		
		
		zAngle += dt * zSpeed;
		while(zAngle > PI2)
			zAngle -= PI2;

		
		//draw the light to the FBO
		fbo.begin();
		batch.setProjectionMatrix(cam.combined);
		batch.setShader(defaultShader);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		float lightSize = lightOscillate? (4.75f + 0.25f * (float)Math.sin(zAngle) + .2f*MathUtils.random()):5.0f;
		batch.draw(light, tilemap.campFirePosition.x - lightSize*0.5f + 0.5f,tilemap.campFirePosition.y + 0.5f - lightSize*0.5f, lightSize, lightSize);
		batch.end();
		fbo.end();
		
		//draw the actual scene
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(cam.combined);
		batch.setShader(currentShader);
		batch.begin();
		fbo.getColorBufferTexture().bind(1); //this is important! bind the FBO to the 2nd texture unit
		light.bind(0); //we force the binding of a texture on first texture unit to avoid artefacts
					   //this is because our default and ambiant shader dont use multi texturing...
					   //youc can basically bind anything, it doesnt matter
		tilemap.render(batch, dt);
		batch.end();
		
		//debug information
		batch.setProjectionMatrix(cam2d.combined);
		batch.setShader(defaultShader);
		batch.begin();
		float x = 0.0f;
		bitmapFont.setColor(shaderSelection==ShaderSelection.Default?Color.YELLOW:Color.WHITE);
		x += bitmapFont.draw(batch, "1=Default Shader", x, cam2d.viewportHeight).width;
		bitmapFont.setColor(shaderSelection==ShaderSelection.Ambiant?Color.YELLOW:Color.WHITE);
		x += bitmapFont.draw(batch, " 2=Ambiant Light", x, cam2d.viewportHeight).width;
		bitmapFont.setColor(shaderSelection==ShaderSelection.Light?Color.YELLOW:Color.WHITE);
		x += bitmapFont.draw(batch, " 3=Light Shader", x, cam2d.viewportHeight).width;
		bitmapFont.setColor(shaderSelection==ShaderSelection.Final?Color.YELLOW:Color.WHITE);
		x += bitmapFont.draw(batch, " 4=Final Shader", x, cam2d.viewportHeight).width;
		x = 0.0f;
		bitmapFont.setColor(lightMove?Color.YELLOW:Color.WHITE);
		x += bitmapFont.draw(batch, "click=light control (" +lightMove+ ")", x, cam2d.viewportHeight-bitmapFont.getLineHeight()).width;
		bitmapFont.setColor(lightOscillate?Color.YELLOW:Color.WHITE);
		x += bitmapFont.draw(batch, " space=light flicker (" +lightOscillate+ ")", x, cam2d.viewportHeight-bitmapFont.getLineHeight()).width;
		x = 0.0f;
		bitmapFont.setColor(Color.WHITE);
		x += bitmapFont.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps", x, cam2d.viewportHeight-bitmapFont.getLineHeight()*2.0f).width;
		batch.end();
		
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		finalShader.dispose();
		lightShader.dispose();
		ambientShader.dispose();
		defaultShader.dispose();
		light.dispose();
		fbo.dispose();
		bitmapFont.dispose();
		tilemap.dispose();
	}
}