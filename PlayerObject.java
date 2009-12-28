import java.awt.*;
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;
import java.awt.geom.*;
import java.lang.Math;

public class PlayerObject extends GameObject {
    private Color color;
    private int health;
    
    public PlayerObject(Body b,float[] vertices,Color c){
        super(b,vertices);
        color=c; 
        health=100;
    }

    public void left(){
        //m_body.setAngularVelocity(-1.0f);
        if(m_body.getAngularVelocity() > -MAX_ANG_VEL){
            m_body.applyTorque(-120.0f);
        }
    }
    
    public void right(){
        //m_body.setAngularVelocity(1.0f);
        if(m_body.getAngularVelocity() < MAX_ANG_VEL){
            m_body.applyTorque(120.0f);
        }
    }

    public void stopRotate(){
        m_body.setAngularVelocity(0.0f);
    }

    public void thrust(float m){
        if(m==0){ return; }
        Vec2 lv=m_body.getLinearVelocity();
        if(Math.abs(lv.length()) < MAX_LIN_VEL){
            Vec2 bodyVec = m_body.getWorldCenter();
            Vec2 dir=getDir();
            m_body.applyForce(dir.mul(m),bodyVec);
        }
    }

    public void halt(){
        //System.out.println("halting");
        //m_body.setLinearVelocity(new Vec2(0.0f,0.0f));
        thruster=0.0f;
    }

    public void forward(){
        Vec2 d=getDir().mul(8.0f);
        //System.out.println("changing linear velcity from "+m_body.getLinearVelocity()+" to "+d);
        //m_body.setLinearVelocity(d);
        thruster=25.0f;
    }

    public void reverse(){
        //Vec2 d=getDir().mul(-8.0f);
        //System.out.println("changing linear velcity from "+m_body.getLinearVelocity()+" to "+d);
        //m_body.setLinearVelocity(d);
        thruster=-12.0f;
    }

    public void draw( Graphics2D g ){
        g.setColor(color);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.rotate(m_body.getAngle());

        //int mp=(int)(pixelsPerMeter);
        //g.fillRect(-mp,-mp,mp*2,mp*2);
        g.drawPolygon(x_pts,y_pts,x_pts.length);
    }

    public void tick(){
        thrust(thruster);
    }

    public void trigger(){
        // TODO how does a gameobject spawn a new gameobject?
    }
}

