import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;
import java.awt.*;
import java.awt.geom.*;
import java.lang.Math;
import java.util.ArrayList;

// CONSIDER maybe this should be an interface?
public abstract class GameObject {
    protected Body m_body;
    protected float thruster;
    protected static float MAX_LIN_VEL=10.0f;
    protected static float MAX_ANG_VEL=1.0f;

    protected ArrayList<GameObjectEventListener> m_gameObjectEventListeners;
    protected int m_damage=0; // CONSIDER do i really need this here?

    public GameObject(Body b){
        m_body=b;
        thruster=0.0f;

        m_gameObjectEventListeners = new ArrayList<GameObjectEventListener>();
    }

    public Vec2 rotate(Vec2 v,float a){
        return new Vec2((float)(v.x * Math.cos(a) - v.y * Math.sin(a)),
                       (float)(v.x * Math.sin(a) + v.y * Math.cos(a)));
    }

    public Vec2 getDir(){
        return rotate(new Vec2(0,-1),m_body.getAngle());
    }

    /* tick
     *
     * do whatever per step. return false if this game object is to be removed.
     */
    public boolean tick(){ return true; }

    // TODO this should be abstracted into a GameObjectDisplay class
    // so i can make game code separate from display code to port to Android
    public abstract void draw( Graphics2D g );
    // do nothing

    // TODO shouldn't this kind of framework already be pre-packaged?
    public void addGameObjectEventListener(GameObjectEventListener l){
        m_gameObjectEventListeners.add(l);
    }

    public void removeGameObjectEventListener(GameObjectEventListener l){
        m_gameObjectEventListeners.remove(l);
    }

    protected void dispatchGameObjectCreatedEvent(GameObjectEvent e){
        for(int i=0;i<m_gameObjectEventListeners.size(); i++){
            m_gameObjectEventListeners.get(i).gameObjectCreated(e);
        }
    }  

    protected void dispatchGameObjectDestroyedEvent(GameObjectEvent e){
        for(int i=0;i<m_gameObjectEventListeners.size(); i++){
            m_gameObjectEventListeners.get(i).gameObjectDestroyed(e);
        }
    }  

    public Body getBody(){
        return m_body;
    }

    public boolean doesDamage(){
        return m_damage > 0;
    }

    public int getDamage(){
        return m_damage;
    }

    /* return true if it survives the damage done */
    public boolean applyDamage(int d){
        return true;
    }

    public boolean survivesImpact(){
        return true;
    }

    /* triggered when an object is being destroyed (e.g. make shrapnel or something) */
    public void doDestroy(){
    }

    protected void emitGameObject(GameObject go){
        dispatchGameObjectCreatedEvent(new GameObjectEvent(this,go)); 
    }
}

