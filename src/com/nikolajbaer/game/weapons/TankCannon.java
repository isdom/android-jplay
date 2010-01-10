package com.nikolajbaer.game.weapons;

/* jbox2d */
import org.jbox2d.dynamics.Body; 
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.*;
import com.nikolajbaer.game.objects.*;

public class TankCannon extends Weapon {
    private boolean m_shooting;
    private int m_reloadCount;
    private static float m_bulletVelocity = 10.0f;

    public TankCannon(){
        m_shooting=false;
        m_reloadCount=0;
    }

    public void triggerOn(){
        m_shooting=true;
    }

    public void triggerOff(){
        m_shooting=false;
    }

    public void tick(PlayerObject shooter){
        //System.out.println("ticking");
        if(m_shooting && m_reloadCount%10 == 0 ){
            if(shooter.getEnergy() > 10){
                shooter.drawEnergy(50);
                // TODO make it so the shooter can't get hit by their own bullets?
                Vec2 d=shooter.getDir();
                Body b=Game.game.createCircle(1.0f,0.3f);
                BulletObject bo=new BulletObject(b,50);
                b.setXForm(shooter.getBody().getWorldCenter().add(d.mul(4)),shooter.getBody().getAngle());
                b.setBullet(true);
                b.setLinearVelocity(d.mul(m_bulletVelocity).add(shooter.getBody().getLinearVelocity()));
                // TODO drain energy usage from game object if this is a beam weapon
                shooter.emitGameObject(bo); 
                m_reloadCount=0;
            }else{
                System.out.println("insufficient power, tank stall..");
            }
        }else{ /*System.out.println("reloading"); */ }
        // reload regardless
        m_reloadCount++;
    }

    public int getPortType(){ return 1; }

    public String getName(){ return "Tank Cannon"; }

    public float getVelocity(){ return m_bulletVelocity; }
}