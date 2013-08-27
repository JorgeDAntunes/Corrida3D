package testes;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.AudioDevice;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointLight;
import javax.media.j3d.PointSound;
import javax.media.j3d.Sound;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Sound3D extends Applet {

    public static void main(String[] args) {
        new MainFrame(new Sound3D(), 480, 480);
    }

    public void init() {
        // create canvas
        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D cv = new Canvas3D(gc);
        setLayout(new BorderLayout());
        add(cv, BorderLayout.CENTER);
        SimpleUniverse su = new SimpleUniverse(cv);

        //System.setProperty("j3d.audiodevice", "com.sun.j3d.audioengines.javasound.JavaSoundMixer");
        //AudioDevice audioDev = su.getViewer().createAudioDevice();

        AudioDevice audioDev = new JavaSoundMixer(su.getViewer().getPhysicalEnvironment());
	audioDev.initialize();

        BranchGroup bg = createSceneGraph();
        bg.compile();
        su.getViewingPlatform().setNominalViewingTransform();
        su.addBranchGraph(bg);
    }

    public BranchGroup createSceneGraph() {
        // root
        BranchGroup objRoot = new BranchGroup();
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(Math.random() - 0.5, Math.random() - 0.5,
                Math.random() - 0.5));
        trans.setScale(0.3);
        TransformGroup objTrans = new TransformGroup(trans);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRoot.addChild(objTrans);

        // visual object
        //Appearance ap = new Appearance();
        //ap.setMaterial(new Material());
        //Shape3D shape = new Shape3D(new Tetrahedron(), ap);
        //objTrans.addChild(shape);
        objTrans.addChild(loadOBJ());

        // behaviors
        BoundingSphere bounds =
                new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        // rotation
        MouseRotate rotator = new MouseRotate(objTrans);
        rotator.setSchedulingBounds(bounds);
        objRoot.addChild(rotator);
        // translation
        MouseTranslate translator = new MouseTranslate(objTrans);
        translator.setSchedulingBounds(bounds);
        objTrans.addChild(translator);
        // zoom
        MouseZoom zoom = new MouseZoom(objTrans);
        zoom.setSchedulingBounds(bounds);
        objTrans.addChild(zoom);

        BoundingSphere soundBounds =
                new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
 
        // sound
        BackgroundSound bsound = new BackgroundSound();
        URL url = this.getClass().getClassLoader().getResource("som/river.wav");
        MediaContainer mc = new MediaContainer(url);
        bsound.setSoundData(mc);
        bsound.setLoop(Sound.INFINITE_LOOPS);
        bsound.setSchedulingBounds(soundBounds);
        bsound.setInitialGain(0.1f);
        bsound.setEnable(true);

        //PointSound pSound = new PointSound();
        //url = this.getClass().getClassLoader().getResource("som/bird.wav");
        //url = this.getClass().getClassLoader().getResource("som/bird.wav");
        //mc = new MediaContainer(url);
        //pSound.setSoundData(mc);
        //pSound.setLoop(Sound.INFINITE_LOOPS);
        //pSound.setInitialGain(1f);
        float[] distances = {1f, 20f};
        float[] gains = {1f, 0.001f};
        //pSound.setDistanceGain(distances, gains);
        //pSound.setSchedulingBounds(soundBounds);
        //pSound.setEnable(true);
        //objTrans.addChild(pSound);

        //light
        AmbientLight light = new AmbientLight(true, new Color3f(Color.blue));
        light.setInfluencingBounds(bounds);
        objRoot.addChild(light);
        PointLight ptlight = new PointLight(new Color3f(Color.white),
                new Point3f(0f, 0f, 2f), new Point3f(1f, 0.3f, 0f));
        ptlight.setInfluencingBounds(bounds);
        objRoot.addChild(ptlight);

        //background
        url = getClass().getClassLoader().getResource("images/clouds.jpg");
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(url);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bi);
        Background background = new Background(image);
        background.setApplicationBounds(bounds);
        objRoot.addChild(background);
        return objRoot;
    }

    protected BranchGroup loadOBJ() {
        
        ObjectFile f1 = new ObjectFile();
        Scene s1 = null;

        URL url = this.getClass().getClassLoader().getResource("images/birdy.obj");
        try {
            s1 = f1.load(url);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
       
        return s1.getSceneGroup();
    }
}
