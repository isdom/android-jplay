package com.nikolajbaer.awtrender;

/* java */
import java.util.ArrayList;
import java.util.HashMap;

/* jbox2d */
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/* AWT */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;

/* local */
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;
import com.nikolajbaer.game.*;

public class JPlay extends JFrame implements ActionListener { //implements Runnable{
    private Game m_game;
    private Timer m_timer;
    private BufferedImage m_backBuffer ;
    private Graphics2D m_backGraphics;
    private HashMap<String,AWTRenderObject> m_renderObjects;

    // pixels per meter
    public static final float PPM = 10.0f;
    private int m_gameWidth=400;
    private int m_gameHeight=600;

    public JPlay(String name){
        super(name);

        m_renderObjects=new HashMap<String,AWTRenderObject>();

        setSize(m_gameWidth+10,m_gameHeight+20);
        m_backBuffer = new BufferedImage( m_gameWidth,m_gameHeight, BufferedImage.TYPE_INT_RGB ) ;
        m_backGraphics = (Graphics2D)m_backBuffer.getGraphics();
        //m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int gwidth=(int)(m_gameWidth/PPM);
        int gheight=(int)(m_gameHeight/PPM);
        m_game=new Game(gwidth,gheight); // game is in meters
        Game.game=m_game;
        m_timer = new Timer(1000/40,this);
        m_timer.setInitialDelay(500);
        m_timer.start(); 
        
        addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent e){
                // TODO parse input commands 
                // TODO keep key map
                switch(e.getKeyCode()){
                    case KeyEvent.VK_SPACE:
                        m_game.getPlayer().triggerOn();
                        break;
                    case KeyEvent.VK_UP:
                        m_game.getPlayer().forward();
                        break;
                    case KeyEvent.VK_DOWN:
                        m_game.getPlayer().reverse();
                        break;
                    case KeyEvent.VK_LEFT:
                        m_game.getPlayer().left();
                        break;
                    case KeyEvent.VK_RIGHT:
                        m_game.getPlayer().right();
                        break;
                }
            }

            public void keyReleased(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_SPACE:
                        m_game.getPlayer().triggerOff();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        m_game.getPlayer().halt();
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        m_game.getPlayer().stopRotate();
                        break;
                }

            }
            public void keyTyped(KeyEvent e){}
        });

        // Add Players
        float[] verts={-2.4f,3.2f,-2.4f,-3.2f,2.4f,-3.2f,2.4f,3.2f};

        // Spread the players out in a ring 
        Vec2 mid=new Vec2(gwidth/2.0f,gheight/2.0f);
        Vec2 offset=new Vec2(gwidth/2*0.75f,0);
        int np=2; //6;
        for(int i=0;i<np;i++){
            Body b=m_game.createRect(0.2f,-2.4f,-3.2f,4.8f,6.4f);
            //Body b=m_game.createPolygon(1.0f, verts );

            double a=(2*Math.PI)/np;
            Vec2 rv=Util.rotate( offset, (float)(i*a) );
            float ra=(float)(i*a < Math.PI ? i*a+Math.PI : i*a-Math.PI);
            b.setXForm( mid.add(rv) ,(float)(ra+Math.PI/2)); // i guess it goes from 0,1 not 1,0
            PlayerObject po=new PlayerObject(b,verts);

            if(i==0){ 
                m_game.addPlayer(new LivePlayer(po),true);
            }else{
                m_game.addPlayer(new HunterPlayer(po));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        m_game.tick();
        render();
    }

    public static void main(String[] args) {
        JPlay f = new JPlay("JPlay");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public void render( ) {
        // render to back buffer
        m_backGraphics.setColor( Color.black ) ;
        m_backGraphics.fillRect( 0,0, m_gameWidth,m_gameHeight) ;
        // render game field
        //m_game.draw(m_backGraphics);
        //m_backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        ArrayList<Renderable> renderables=m_game.getRenderables();
        AffineTransform t=m_backGraphics.getTransform();
        for(int i=0;i<renderables.size(); i++){
            Renderable r=renderables.get(i);
            AWTRenderObject ro=(AWTRenderObject)r.getRenderObject(); 
            if(ro==null){
                // TODO lookup render object via key
                // get render key  
                String k=r.getRenderKey();
                // if in m_renderObjects, then attach that,
                ro=m_renderObjects.get(k);
                // otherwise create a new one from the renderobject factory 
                // CONSIDER do i want a factory to be retrieved or an instance?
                if(ro==null){
                    // TODO add a renderobjectfactory abstract class
                    // that i can override for each render lib
                    System.out.println("Building a "+k+" Render Object");
                    //ro=new PolygonRenderObject();
                    ro=new PNGRenderObject("media/"+k+".png");
                    m_renderObjects.put(k,ro);
                }
                r.setRenderObject(ro);
            }
            float[] wt=r.getWorldTransform();
            ro.setGraphics(m_backGraphics);
            ro.setPixelRatio(PPM);
            ro.renderFromWorld(wt[0],wt[1],wt[2]);
            // TODO draw sprite k at location t
            // CONSIDER sprite lookup is expensive, should embed it in renderable as renderobject
            //  and call .renderAtTransform(), maybe setup graphics first or something..
            m_backGraphics.setTransform(new AffineTransform(t));
        }
        
        Graphics g = getGraphics();
        g.drawImage( m_backBuffer, 5,25, null );
        g.dispose();
    }    
}
