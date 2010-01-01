import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;
import java.awt.*;

// CONSIDER awkward, could be refactored
public class PolygonGameObject extends GameObject {
    protected float[] m_vertices;
    protected int[] x_pts; protected int[] y_pts;

    public PolygonGameObject(Body b,float[] vertices){
        super(b);
        m_vertices=vertices;
        // hang on to int points for drawing..
        x_pts=new int[m_vertices.length/2];
        y_pts=new int[m_vertices.length/2];
        for(int i=0;i<vertices.length;i+=2){
            x_pts[i/2]=(int)(vertices[i]*Game.PPM);
            y_pts[i/2]=(int)(vertices[i+1]*Game.PPM);
        }
    }

    public void draw( Graphics2D g ){ 
        g.setColor(Color.black);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.rotate(m_body.getAngle());

        //int mp=(int)(pixelsPerMeter);
        //g.fillRect(-mp,-mp,mp*2,mp*2);
        g.fillPolygon(x_pts,y_pts,x_pts.length);
    }

}
