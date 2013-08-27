package testes;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Switch;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class TesteLOD extends Applet {
	public static void main(String[] args) {
		new MainFrame(new TesteLOD(), 480, 480);
	}

	BufferedImage[] images = new BufferedImage[3];

	public void init() {
		// create canvas
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);
		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);
		BranchGroup bg = createSceneGraph();
		bg.compile();
		SimpleUniverse su = new SimpleUniverse(cv);
		ViewingPlatform viewingPlatform = su.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();
		// orbit behavior to zoom and rotate the view
		OrbitBehavior orbit = new OrbitBehavior(cv, OrbitBehavior.REVERSE_ZOOM
				| OrbitBehavior.REVERSE_ROTATE
				| OrbitBehavior.DISABLE_TRANSLATE);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);
		su.addBranchGraph(bg);
	}

	public TransformGroup criaBola(double x) {
		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3d(x, 0, 0));
		TransformGroup tg = new TransformGroup(tr);

		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.addChild(objTrans);
		// a switch to hold the different levels
		Switch sw = new Switch(0);
		sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_READ);
		sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_WRITE);
		objTrans.addChild(sw);
		// 4 levels of globes
		loadImages();
		Appearance ap = createAppearance(0);
		sw.addChild(new Sphere(0.4f, Primitive.GENERATE_TEXTURE_COORDS, 40, ap));
		ap = createAppearance(1);
		sw.addChild(new Sphere(0.4f, Primitive.GENERATE_TEXTURE_COORDS, 10, ap));
		ap = createAppearance(2);
		sw.addChild(new Sphere(0.4f, Primitive.GENERATE_TEXTURE_COORDS, 4, ap));
		ap = new Appearance();
		ap.setColoringAttributes(new ColoringAttributes(0f, 0f, 0.5f,
				ColoringAttributes.FASTEST));
		sw.addChild(new Sphere(0.4f, Sphere.GENERATE_NORMALS, 5, ap));
		// the DistanceLOD behavior
		float[] distances = new float[3];
		distances[0] = 5.0f;
		distances[1] = 10.0f;
		distances[2] = 25.0f;
		DistanceLOD lod = new DistanceLOD(distances);
		lod.addSwitch(sw);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				10.0);
		lod.setSchedulingBounds(bounds);
		objTrans.addChild(lod);
		// background
		Background background = new Background(1.0f, 1.0f, 1.0f);
		background.setApplicationBounds(bounds);
		tg.addChild(background);

		return tg;
	}

	public BranchGroup createSceneGraph() {
		Point3d p3d = new Point3d();
		BranchGroup objRoot = new BranchGroup();
		TransformGroup tg = new TransformGroup();
		tg.addChild(criaBola(0.5));
		tg.addChild(criaBola(-0.5));
		objRoot.addChild(tg);
		return objRoot;
	}

	void loadImages() {
		URL filename = getClass().getClassLoader().getResource(
				"imagens/tronco2.jpg");
		try {
			images[0] = ImageIO.read(filename);
			AffineTransform xform = AffineTransform.getScaleInstance(0.5, 0.5);
			AffineTransformOp scaleOp = new AffineTransformOp(xform, null);
			for (int i = 1; i < 3; i++) {
				images[i] = scaleOp.filter(images[i - 1], null);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	Appearance createAppearance(int i) {
		Appearance appear = new Appearance();
		ImageComponent2D image = new ImageComponent2D(
				ImageComponent2D.FORMAT_RGB, images[i]);
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
				image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		texture.setEnable(true);
		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		appear.setTexture(texture);
		return appear;
	}
}
