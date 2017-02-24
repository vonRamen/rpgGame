package com.alcovegames.fireshader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Tilemap {
	
	public Texture			texTileset = null;
	private Texture			texCampfire = null;
	public final Vector3	campFirePosition = new Vector3(14.0f, 10.0f, 0.0f);
	private	int				currentFrame = 0;
	private float			animationElapsed = 0.0f;
	private final float		frameLength = .1f;
	
	private TextureRegion[]	tileset = null;
	private TextureRegion[]	campFire = null;
	
	private final float		tileWidth = 1.0f;
	private final float		tileHeight = 1.0f;
	
	private final int		tileWidthPx = 32;
	private final int		tileHeightPx = 32;
	
	private final int		tilesPerRow, tilesPerCol;
	
	private final int		mapWidth = 20;
	private final int		mapHeight = 20;
	
	private final int		nbLayers = 2;
	
	
	
	private final int[][] 	map = {
			{178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 1, 2, 2, 66, 18, 18, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 17, 18, 18, 49, 34, 34, 178, 178, 178, 178, 178, 178, 178, 1, 2, 2, 2, 2, 2, 2, 66, 18, 49, 35, 178, 178, 178, 178, 178, 178, 178, 178, 178, 17, 18, 18, 18, 18, 18, 18, 18, 18, 19, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 17, 18, 49, 34, 34, 34, 34, 34, 34, 35, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 17, 18, 19, 178, 87, 88, 88, 88, 88, 88, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 17, 18, 19, 178, 103, 104, 104, 104, 104, 104, 178, 178, 178, 178, 178, 178, 178, 178, 178, 12, 17, 18, 19, 178, 103, 104, 104, 104, 104, 104, 105, 178, 178, 90, 11, 11, 11, 178, 178, 28, 17, 18, 19, 178, 103, 104, 104, 104, 104, 104, 105, 178, 178, 122, 43, 27, 27, 27, 27, 28, 17, 18, 19, 178, 103, 104, 104, 104, 104, 104, 105, 178, 178, 178, 178, 43, 43, 107, 60, 28, 17, 18, 19, 178, 119, 136, 104, 104, 104, 104, 105, 178, 178, 178, 178, 178, 178, 43, 43, 44, 17, 18, 19, 178, 178, 119, 136, 104, 104, 104, 105, 178, 178, 194, 194, 178, 178, 178, 178, 178, 17, 18, 19, 178, 178, 178, 119, 120, 120, 120, 121, 178, 178, 210, 210, 193, 178, 178, 178, 178, 33, 34, 35, 178, 178, 178, 178, 194, 194, 178, 178, 178, 178, 164, 164, 209, 193, 194, 194, 194, 194, 194, 194, 194, 194, 194, 195, 210, 210, 193, 194, 194, 194, 164, 164, 164, 209, 210, 210, 210, 210, 210, 210, 210, 210, 210, 211, 164, 164, 209, 210, 210, 210, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164, 164},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 226, 227, 228, 184, 185, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 241, 242, 243, 244, 200, 201, 0, 0, 0, 0, 0, 13, 14, 207, 208, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 125, 189, 29, 30, 15, 16, 0, 156, 0, 0, 0, 165, 166, 0, 0, 0, 0, 0, 0, 0, 141, 205, 45, 46, 31, 32, 0, 191, 192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 16, 0, 0, 0, 0, 61, 62, 63, 0, 0, 0, 0, 80, 0, 0, 0, 0, 0, 0, 47, 48, 0, 0, 0, 0, 93, 94, 95, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 109, 110, 111, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 168, 169, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 138, 139, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 154, 155, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 180, 181, 182, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 196, 197, 198, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 196, 197, 198, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 196, 197, 198, 229, 230, 231, 232, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 212, 213, 214, 245, 246, 247, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			};
	         

	
	public Tilemap(){
		texTileset = new Texture("data/tileset.png");
		
		
		tilesPerRow = texTileset.getWidth() / tileWidthPx;
		tilesPerCol = texTileset.getHeight() / tileHeightPx;
		tileset = new TextureRegion[(texTileset.getWidth() * texTileset.getHeight()) / (tileWidthPx*tileHeightPx)];
		
		for(int y=0;y<tilesPerCol;y++){
			for(int x=0;x<tilesPerRow;x++){
				tileset[y*tilesPerCol + x] = new TextureRegion(texTileset, x*tileWidthPx, y*tileHeightPx, tileWidthPx, tileHeightPx);
			}
		}
		
		texCampfire = new Texture("data/campfire.png");
		campFire = new TextureRegion[5];
		for(int x=0;x<5;x++){
			campFire[x] = new TextureRegion(texCampfire, x*32, 0, 32, 64);
		}
		
		
	}
	
	public void render(SpriteBatch spriteBatch, float dt){
		
		//tilemap
		for(int y=0;y<mapHeight;y++){
			for(int x=0;x<mapWidth;x++){
				for(int l=0;l<nbLayers;l++){
					
					if(map[l][y*mapHeight + x] > 0)
						spriteBatch.draw(tileset[map[l][y*mapHeight + x] - 1], (float)x*tileWidth, (float)( (mapHeight-y-1)*tileHeight), tileWidth, tileHeight);	
				}
			}
		}
		
		//campfire
		animationElapsed += dt;
		while(animationElapsed > frameLength){
		    animationElapsed -= frameLength;
		    currentFrame = (currentFrame == campFire.length - 1) ? 0 : ++currentFrame;
		}
		
		spriteBatch.draw(campFire[currentFrame], campFirePosition.x, campFirePosition.y, tileWidth, tileHeight*2.0f);
	}
	
	public void dispose(){
		if(texTileset != null)
			texTileset.dispose();
		if(texCampfire != null)
			texCampfire.dispose();
	}

}
