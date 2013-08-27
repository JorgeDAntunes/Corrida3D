package testes;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.TextArea;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class TestBillboard extends Applet {
	  public static void main(String[] args) {
	    new MainFrame(new TestBillboard(), 480, 480);
	  }

	  public void init() {
	    // create canvas
	    GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
	    Canvas3D cv = new Canvas3D(gc);
	    setLayout(new BorderLayout());
	    add(cv, BorderLayout.CENTER);
	    TextArea ta = new TextArea("",3,30,TextArea.SCROLLBARS_NONE);
	    ta.setText("Rotation: Drag with left button\n");
	    ta.append("Translation: Drag with right button\n");
	    ta.append("Zoom: Hold Alt key and drag with left button");
	    ta.setEditable(false);
	    add(ta, BorderLayout.SOUTH);
	    BranchGroup root = new BranchGroup();
	    // axes
	    Transform3D tr = new Transform3D();
	    tr.setScale(0.5);
	    //tr.setTranslation(new Vector3d(-0.8, -0.7, -0.5));
	    TransformGroup tg = new TransformGroup(tr);
	    root.addChild(tg);
	    AxesBillboard axes = new AxesBillboard();
	    tg.addChild(axes);
	    BoundingSphere bounds = new BoundingSphere();
	    //light
	    AmbientLight light = new AmbientLight(true, new Color3f(Color.blue));
	    light.setInfluencingBounds(bounds);
	    root.addChild(light);
	    PointLight ptlight = new PointLight(new Color3f(Color.white),
	      new Point3f(0f,0f,2f), new Point3f(1f,0f,0f));
	    ptlight.setInfluencingBounds(bounds);
	    root.addChild(ptlight);
	    //background
	    Background background = new Background(1.0f, 1.0f, 1.0f);
	    background.setApplicationBounds(bounds);
	    root.addChild(background);
	    root.compile();
	    SimpleUniverse su = new SimpleUniverse(cv);
	    su.getViewingPlatform().setNominalViewingTransform();
	    // viewplatform motion
	    OrbitBehavior orbit = new OrbitBehavior(cv);
	    orbit.setSchedulingBounds(new BoundingSphere());
	    su.getViewingPlatform().setViewPlatformBehavior(orbit);
	    
	    su.addBranchGraph(root);
	  }  
	}

