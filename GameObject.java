import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;
import java.awt.*;
import java.awt.geom.*;
import java.lang.Math;
import java.util.ArrayList;


public class GameObject {
    protected Body m_body;
    protected float[] m_vertices;
    protected int[] x_pts; protected int[] y_pts;
    protected float thruster;
    protected static float MAX_LIN_VEL=10.0f;
    protected static float MAX_ANG_VEL=1.0f;

    protected ArrayList<GameObjectEventListener> m_gameObjectEventListeners;

    public GameObject(Body b,float[] vertices){
        m_body=b;
        m_vertices=vertices;
        thruster=0.0f;

        // hang on to int points for drawing..
        x_pts=new int[m_vertices.length/2];
        y_pts=new int[m_vertices.length/2];
        for(int i=0;i<vertices.length;i+=2){
            x_pts[i/2]=(int)(vertices[i]*Game.PPM);
            y_pts[i/2]=(int)(vertices[i+1]*Game.PPM);
        }

        m_gameObjectEventListeners = new ArrayList<GameObjectEventListener>();
    }

    public Vec2 rotate(Vec2 v,float a){
        return new Vec2((float)(v.x * Math.cos(a) - v.y * Math.sin(a)),
                       (float)(v.x * Math.sin(a) + v.y * Math.cos(a)));
    }

    public Vec2 getDir(){
        return rotate(new Vec2(0,-1),m_body.getAngle());
    }

    public void tick(){
    }

    // TODO this should be abstracted into a GameObjectDisplay class
    // so i can make game code separate from display code to port to Android
    public void draw( Graphics2D g ){
        g.setColor(Color.black);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.rotate(m_body.getAngle());

        //int mp=(int)(pixelsPerMeter);
        //g.fillRect(-mp,-mp,mp*2,mp*2);
        g.fillPolygon(x_pts,y_pts,x_pts.length);
    }

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
}


