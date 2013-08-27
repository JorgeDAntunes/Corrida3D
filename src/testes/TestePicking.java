package testes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JApplet;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class TestePicking extends JApplet implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 *            the command line arguments
	 */
	PickCanvas pc;
	Appearance lit = new Appearance();
	Switch sw1;
	boolean cubo1 = false;

	public static void main(String[] args) {
		new MainFrame(new TestePicking(), 640, 480);
	}

	@Override
	public void init() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);

		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);
		cv.addMouseListener(this);

		BranchGroup bg = createSceneGraph();

		bg.compile();
		pc = new PickCanvas(cv, bg);
		pc.setMode(PickTool.GEOMETRY);

		SimpleUniverse su = new SimpleUniverse(cv);
		su.getViewingPlatform().setNominalViewingTransform();

		su.addBranchGraph(bg);

	}

	private Shape3D criaTexto() {
		Font3D font = new Font3D(new Font("Serif", Font.PLAIN, 1),
				new FontExtrusion());
		Text3D text = new Text3D(font, "Jogar");
		Appearance ap = new Appearance();
		ap.setMaterial(new Material());
		Shape3D shape = new Shape3D(text, ap);
		return shape;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		pc.setShapeLocation(e);

		PickResult result = pc.pickClosest();
		if (result != null) {
			TransformGroup tg = (TransformGroup) result
					.getNode(PickResult.TRANSFORM_GROUP);

			if (tg != null) {
				if (tg.indexOfChild(sw1) == 0) {
					cubo1 = !cubo1;
					sw1.setWhichChild(cubo1 ? 1 : 0);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public TransformGroup noSwitch(boolean i) {
		lit.setMaterial(new Material());

		Node no1;
		// if (i) {
		no1 = new Sphere(0.3f, Primitive.ENABLE_GEOMETRY_PICKING
				| Primitive.GENERATE_NORMALS, lit);
		/*
		 * } else { no1 = new Dodecahedron(); //PickTool.setCapabilities(no1,
		 * PickTool.INTERSECT_TEST);
		 * no1.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE
		 * |Shape3D.ALLOW_GEOMETRY_WRITE |Shape3D.ALLOW_PICKABLE_WRITE); }
		 */
		Box cube = new Box(0.3f, 0.3f, 0.3f, Primitive.ENABLE_GEOMETRY_PICKING
				| Primitive.GENERATE_NORMALS, lit);
		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D tr = new Transform3D();
		tr.setEuler(new Vector3d(Math.PI / 4, Math.PI / 4, Math.PI / 4));
		tr.setTranslation(new Vector3d((i ? -1 : 1) * 0.5, 0, 0));
		tg.setTransform(tr);

		Switch sw;
		sw = new Switch(0);
		sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
		sw.addChild(no1);
		sw.addChild(cube);
		tg.addChild(sw);
		if (i) {
			sw1 = sw;
		}

		return tg;
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();

		root.addChild(noSwitch(true));
		root.addChild(noSwitch(false));

		BoundingSphere bounds = new BoundingSphere();

		Background background = new Background(1.0f, 1.0f, 1.0f);
		background.setApplicationBounds(bounds);
		root.addChild(background);
		AmbientLight light = new AmbientLight(true, new Color3f(Color.red));
		light.setInfluencingBounds(bounds);
		root.addChild(light);
		PointLight ptlight = new PointLight(new Color3f(Color.green),
				new Point3f(3f, 3f, 3f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bounds);
		root.addChild(ptlight);
		PointLight ptlight2 = new PointLight(new Color3f(Color.orange),
				new Point3f(-2f, 2f, 2f), new Point3f(1f, 0f, 0f));
		ptlight2.setInfluencingBounds(bounds);
		root.addChild(ptlight2);

		return root;
	}
}