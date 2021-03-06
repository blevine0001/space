package de.bitowl.space.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import de.bitowl.space.Res;
import de.bitowl.space.Utils;

public class Player extends Ship{
	/**
	 * is the button to shoot currently pressed down
	 */
	public boolean isShooting;
	
	
	/**
	 * weapons the player has currently equipped
	 */
//	public Array<Weapon> weapons;
	
	/**
	 * the weapon currently equipped
	 */
	public int currentWeapon;
	public Weapon weapon;
	
	TextureRegion mgA;
	TextureRegion cockpitA;
	TextureRegion engineA;
	
	int max_life=100;
	
	
	// TODO winkel, in dem er jetzt sein sollte
	public float desAngle;
	
	public float money;
	
	public Player() {
		super(Res.atlas.findRegion("ship"));
		setOrigin(48,48);
		MAX_SPEED=500;
		ANGLE_ACC_SPEED=15;
		GameObjects.player=this;
		
		mgA=Res.atlas.findRegion("ship_doublegun");
		cockpitA=Res.atlas.findRegion("ship_cocpit");
		engineA=Res.atlas.findRegion("ship_engine");
		life=max_life;
		team=0;
	}
	
	public void addLife(float amount){
		life+=amount;
		if(life>max_life){life=max_life;}
	}

	@Override
	public void act(Chunk pChunk,float delta) {
		super.act(pChunk,delta);
		
		// winkel berechnen
		// turn around in the direction our aim is at
		float dif=desAngle-angle;
		if(Math.abs(Utils.differenceAngles(angle,desAngle))<ANGLE_ACC_SPEED*delta){
			angle=desAngle;
			accAngle=0;
			setRotation(MathUtils.radDeg*angle-90); 
		}else
		if((dif>0.05f&&dif<MathUtils.PI) || dif <-MathUtils.PI){
			accAngle=ANGLE_ACC_SPEED;
		}else{//if((dif<-0.05f&&dif>-MathUtils.PI) || dif>MathUtils.PI){
			accAngle=-ANGLE_ACC_SPEED;
		}
		
		if(isShooting){
			
			weapon.timeToNextShot-=delta;
			weapon.minTimeToNextShot-=delta;
			if(weapon.timeToNextShot<=0&&weapon.minTimeToNextShot<=0){
				if(weapon.ammo>0||weapon.maxAmmo==-1){// we still have ammo or our ammo is unlimited
					weapon.timeToNextShot=weapon.autoShootDelay;
					weapon.minTimeToNextShot=weapon.manualDelay;
					weapon.shoot(team, getCenterX(),getCenterY(),angle, null); // we have no direct aim :D
				}
				
			}
		}else{
			weapon.timeToNextShot=0;
			weapon.minTimeToNextShot-=delta;
		}
		
		// check for collision with every shot (player can as well be hit)
		if(pChunk!=null){
			for(int j=0;j<pChunk.shots.size();j++){
				checkColWithShot(pChunk,pChunk.shots.get(j));
			}
		}
	}
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		drawReg(batch,mgA);
		drawReg(batch,cockpitA);
		drawReg(batch,engineA);

		super.draw(batch, parentAlpha);
		
	}
	public void drawReg(SpriteBatch batch,TextureRegion region){
		float x = getX();
		float y = getY();
		float scaleX = getScaleX();
		float scaleY = getScaleY();

		float rotation = getRotation();
		if (scaleX == 1 && scaleY == 1 && rotation == 0)
			batch.draw(region, x , y , getWidth(),
					getHeight());
		else {
			batch.draw(region, x , y , getOriginX()
					, getOriginY() , getWidth(),
					getHeight(), scaleX, scaleY, rotation);
		}
	}
	public void switchWeapon(){
		System.out.println("switchWeapon!!");
		do{
			currentWeapon++;
			
			if(currentWeapon==Res.weapons.size){
				currentWeapon=0;
			}
			
			System.out.println("switch to: "+currentWeapon+" - "+Res.weapons.get(currentWeapon).name);
			
			weapon=Res.weapons.get(currentWeapon).getCurrent();
			// System.out.println(weapon);
			
		/*if(weapon==null){
			switchWeapon();
		}*/
		}while(weapon==null);// in case we have not bought one of the weapons yet ;)
		//System.err.println("---");
	}

	public void addMoney(float amount) {
		money+=amount;
	}
				
}
