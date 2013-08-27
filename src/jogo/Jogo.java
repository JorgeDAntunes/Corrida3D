package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Material;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Jogo extends JFrame implements MouseListener, SetterEstado,
		ActionListener {
	/**
	 * 
	 */
	enum Estado {
		inicio, perdeu, emJogo
	};

	private static final long serialVersionUID = 1L;
	private ArrayList<Point3d> pontos;
	private BranchGroup bg;
	private Alpha alpha;
	private Estado estado = Estado.inicio;
	private ArrayList<Obstaculo> obstaculos;
	private Switch sw;
	private PickCanvas pc;
	private int comprimento = 150;
	private JMenuBar jMenuBar;
	private JMenu jMenuJogo;
	private JMenuItem jMenuItemSair;
	private JPanel panelJogo;
	private JTextField jtfVelocidade;

	public static void main(String[] args) {
		Jogo jogo = new Jogo();

		jogo.setVisible(true);
		jogo.init();
	}

	public void init() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jMenuBar = new javax.swing.JMenuBar();
		jMenuJogo = new javax.swing.JMenu();
		jMenuItemSair = new javax.swing.JMenuItem();

		jMenuJogo.setText("Jogo");
		jMenuItemSair.setText("Sair");

		jMenuJogo.setMnemonic(KeyEvent.VK_ALT);

		jMenuBar.add(jMenuJogo);
		jMenuJogo.add(jMenuItemSair);
		setJMenuBar(jMenuBar);

		jMenuItemSair.addActionListener(this);
		panelJogo = new JPanel();

		// canvas
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);
		panelJogo.setSize(800, 600);
		panelJogo.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;

		cv.setPreferredSize(new Dimension(800, 600));
		panelJogo.add(cv, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.ipadx = 20;
		panelJogo.add(new JLabel("Velocidade"), c);

		jtfVelocidade = new JTextField("180000");
		c.gridx++;
		panelJogo.add(jtfVelocidade, c);

		c.gridx++;
		c.anchor = GridBagConstraints.WEST;
		panelJogo.add(new JLabel("em milisegundos com um valor entre 1000 e 600000"), c);

		setContentPane(panelJogo);

		SimpleUniverse su = new SimpleUniverse(cv, 2);
		su.getViewingPlatform().setNominalViewingTransform();
		pack();

		pontos = new ArrayList<>();
		obstaculos = new ArrayList<>();
		bg = new BranchGroup();
		pc = new PickCanvas(cv, bg);
		cv.addMouseListener(this);
		pc.setMode(PickTool.GEOMETRY);

		criaGrafoDeCena();

		mudaView(su.getViewingPlatform().getMultiTransformGroup()
				.getTransformGroup(0));

		OrbitBehavior ob = new OrbitBehavior(cv,
				OrbitBehavior.DISABLE_TRANSLATE);
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100));
		su.getViewingPlatform().setNominalViewingTransform();
		su.getViewingPlatform().setViewPlatformBehavior(ob);

		bg.compile();
		su.addBranchGraph(bg);
	}

	private void mudaView(TransformGroup tg) {
		Transform3D tr = new Transform3D();
		Transform3D tr1 = new Transform3D();
		tr1.setEuler(new Vector3d(-Math.PI / 4, 0, 0));
		tr.setTranslation(new Vector3d(0, 6, 4));
		tr.mul(tr, tr1);
		tg.setTransform(tr);
	}

	private TransformGroup criaMapa() {
		TransformGroup tg = new TransformGroup();
		TransformGroup tgAproximador = new TransformGroup();
		tgAproximador.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		criaPlataforma(tg);

		Transform3D tr = new Transform3D();
		tr.setEuler(new Vector3d(0, -Math.PI / 2, 0));
		tgAproximador.addChild(tg);

		try {
			carregaMapa();
		} catch (IOException e) {
			e.printStackTrace();
		}

		alpha.setStartTime(System.currentTimeMillis());
		alpha.pause();
		PositionInterpolator pi = new PositionInterpolator(alpha,
				tgAproximador, tr, 1, comprimento * 2 - 10);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 1000);

		pi.setSchedulingBounds(bounds);

		tg.addChild(pi);

		return tgAproximador;
	}

	private TransformGroup criaBola() {
		TransformGroup tgBolaESombra = new TransformGroup();

		URL url = Jogo.class.getClassLoader().getResource("imagens/olhos.jpg");
		TextureLoader textureLoad = new TextureLoader(url, null);

		ImageComponent2D textureIm = textureLoad.getScaledImage(256, 256);

		// Generate the texture.
		Texture2D aTexture = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB,
				textureIm.getWidth(), textureIm.getHeight());
		aTexture.setImage(0, textureIm);
		Appearance textureApp = new Appearance();
		textureApp.setTexture(aTexture);
		TextureAttributes textureAttr = new TextureAttributes();
		textureAttr.setTextureMode(TextureAttributes.REPLACE);
		textureApp.setTextureAttributes(textureAttr);

		Material mat = new Material();
		mat.setShininess(120.0f);
		textureApp.setMaterial(mat);
		TexCoordGeneration tcg = new TexCoordGeneration(
				TexCoordGeneration.OBJECT_LINEAR,
				TexCoordGeneration.TEXTURE_COORDINATE_2);

		textureApp.setTexCoordGeneration(tcg);

		Sphere sphere = new Sphere(0.5f, Sphere.GENERATE_NORMALS, 50,
				textureApp);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 1000);
		TransformGroup tgBola = new TransformGroup();

		tgBola.addChild(sphere);
		tgBola.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		TransformGroup tgSombra = criaSombra();
		BehaviorDaBola bb = new BehaviorDaBola(bounds, tgBola, tgSombra,
				pontos, bg, alpha, this);
		tgBola.addChild(bb);
		tgBolaESombra.addChild(tgBola);
		tgBolaESombra.addChild(tgSombra);
		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3d(0, 2.05, 0));
		tgBolaESombra.setTransform(tr);
		return tgBolaESombra;
	}

	private TransformGroup criaSombra() {
		Appearance ap = new Appearance();
		ColoringAttributes colorAttr = new ColoringAttributes(0.1f, 0.1f, 0.1f,
				ColoringAttributes.FASTEST);
		ap.setColoringAttributes(colorAttr);
		TransparencyAttributes transAttr = new TransparencyAttributes(
				TransparencyAttributes.BLENDED, 0.35f);
		ap.setTransparencyAttributes(transAttr);
		PolygonAttributes polyAttr = new PolygonAttributes();
		polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(polyAttr);
		Cylinder sombra = new Cylinder(1, 0.01f, ap);
		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.addChild(sombra);
		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3d(0, 0, 0));
		tg.setTransform(tr);
		return tg;
	}

	private TransformGroup criaGrelha() {
		TransformGroup tg = new TransformGroup();
		tg.addChild(new Grelha());
		Transform3D tr = new Transform3D();
		Transform3D tr1 = new Transform3D();
		tr1.setScale(new Vector3d(0.18f, 0.2f, 0.2f));
		tr.setTranslation(new Vector3d(-0.5, 0.2, 0));
		tr.mul(tr, tr1);
		tg.setTransform(tr);
		return tg;
	}

	private void criaPlataforma(TransformGroup tg) {
		Appearance ap = new Appearance();

		Material mat = new Material();
		mat.setAmbientColor(0.19125f, 0.0735f, 0.0225f);
		mat.setDiffuseColor(0.7038f, 0.27048f, 0.0828f);
		mat.setSpecularColor(0.256777f, 0.137622f, 0.086014f);
		mat.setShininess(12.8f);
		ap.setMaterial(mat);
		
		Box box = new Box(4, 0.2f, comprimento, ap);
		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3d(0, 0, -comprimento));
		tg.addChild(box);
		tg.setTransform(tr);
	}

	private long getTempo() {
		try {
			long tempo = Long.parseLong(jtfVelocidade.getText());
			if (tempo < 1000) {
				jtfVelocidade.setText("1000");
				tempo = 1000;
				return tempo;
			}
			if (tempo > 600000) {
				jtfVelocidade.setText("600000");
				tempo = 600000;
				return tempo;
			}
			return tempo;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					null,
					"Não foi possivel intrepertar o valor "
							+ jtfVelocidade.getText()
							+ " como um inteiro\nSerá assumido o valor 180000");
			jtfVelocidade.setText("180000");
			return 180000;
		}
	}

	private void criaGrafoDeCena() {

		BackgroundSound som = new BackgroundSound();
		alpha = new Alpha(1, getTempo());

		bg.addChild(criaMapa());
		bg.addChild(criaBola());
		bg.addChild(criaSwitchDeLabels());
		MediaContainer mc = new MediaContainer(getClass().getClassLoader()
				.getResource("som/som.wav"));
		som.setSoundData(mc);
		som.setSchedulingBounds(new BoundingBox());

		bg.addChild(som);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 100);
		TextureLoader myLoader = new TextureLoader(getClass().getClassLoader()
				.getResource("imagens/cenario.jpg"), this);
		ImageComponent2D myImage = myLoader.getImage();

		Background background = new Background(myImage);

		background.setApplicationBounds(bounds);
		bg.addChild(background);
		AmbientLight light = new AmbientLight(true, new Color3f(Color.WHITE));
		light.setInfluencingBounds(bounds);
		bg.addChild(light);
		DirectionalLight dLight = new DirectionalLight(
				new Color3f(Color.green), new Vector3f(0f, -1f, 0f));
		dLight.setInfluencingBounds(bounds);
		bg.addChild(dLight);
		PointLight ptlight = new PointLight(new Color3f(Color.green),
				new Point3f(3f, 3f, 3f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bounds);
		bg.addChild(ptlight);
		PointLight ptlight2 = new PointLight(new Color3f(Color.orange),
				new Point3f(-2f, 2f, 2f), new Point3f(1f, 0f, 0f));
		ptlight2.setInfluencingBounds(bounds);
		bg.addChild(ptlight2);
	}

	private TransformGroup criaSwitchDeLabels() {
		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		sw = new Switch(0);
		sw.setCapability(Switch.ALLOW_SWITCH_WRITE);

		sw.addChild(Util.criaTexto("Jogar", 1, new Vector3d(-1, 2, 0)));
		sw.addChild(Util.criaTexto("Tente novamente", 0.5, new Vector3d(-3.5,
				4, 0)));
		sw.addChild(null);

		tg.addChild(sw);
		Billboard bb = new Billboard(tg, Billboard.ROTATE_ABOUT_POINT,
				new Point3f(0, 0, 0));
		bb.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		tg.addChild(bb);
		return tg;
	}

	private TransformGroup criaArvore() {
		Appearance apCone = new Appearance();
		apCone.setColoringAttributes(new ColoringAttributes(new Color3f(
				Color.green), ColoringAttributes.NICEST));

		TransformGroup tg = new TransformGroup();
		Cone cone = new Cone(1.5f, 2, apCone);

		Appearance apTronco = new Appearance();
		apTronco.setColoringAttributes(new ColoringAttributes(new Color3f(
				new Color(102, 51, 0)), ColoringAttributes.NICEST));

		Cylinder tronco = new Cylinder(0.5f, 2f, apTronco);

		TransformGroup tgCone = new TransformGroup();
		Transform3D tr = new Transform3D();
		tgCone.addChild(cone);
		tr.setTranslation(new Vector3d(0, 2, 0));
		tgCone.setTransform(tr);
		tg.addChild(tgCone);

		TransformGroup tgTronco = new TransformGroup();
		tgTronco.addChild(tronco);
		tg.addChild(tgTronco);

		tr = new Transform3D();
		Transform3D tr1 = new Transform3D();
		tr1.setScale(0.2);
		tr.setTranslation(new Vector3d(0, 0.4, 0));
		tr.mul(tr, tr1);
		tg.setTransform(tr);
		return tg;
	}

	private void carregaMapa() throws IOException {
		URL url = getClass().getClassLoader().getResource("niveis/nivel.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));

		String linha;

		int z = 0;
		while ((linha = in.readLine()) != null) {
			if (linha.length() != 7) {
				in.close();
				throw new RuntimeException("Cada linha tem que ter 7 elementos");
			}
			for (int x = 0; x < 7; x++) {
				char c = linha.charAt(x);
				char[] ch = { 'g', 'a', 'c' };
				for (int i = 0; i < ch.length; i++) {
					double xl = x - 3, zl = -z;
					if (ch[i] == c) {

						TransformGroup tg = null;
						switch (c) {
						case 'a':
							tg = criaArvore();
							break;
						case 'c':
							tg = criaCaixa();
							break;
						case 'g':
							tg = criaGrelha();
							break;
						default:
							break;
						}
						Obstaculo obstaculo = new Obstaculo(bg, tg, xl, zl - 10);
						obstaculos.add(obstaculo);
						break;
					}
				}
			}
			z++;
		}
		in.close();
	}

	private void carregaTextura(Appearance app, String recurso) {
		TextureLoader textLoad = new TextureLoader(getClass().getClassLoader()
				.getResource("imagens/" + recurso), this);
		ImageComponent2D textImage = textLoad.getImage();
		Texture2D texture = new Texture2D(Texture2D.BASE_LEVEL, Texture.RGB,
				textImage.getWidth(), textImage.getHeight());
		texture.setImage(0, textImage);
		app.setTexture(texture);
		TextureAttributes textAttr = new TextureAttributes();
		textAttr.setTextureMode(TextureAttributes.REPLACE);
		app.setTextureAttributes(textAttr);
		app.setMaterial(new Material());
	}

	private Shape3D caixa() {
		Appearance a = new Appearance();
		carregaTextura(a, "texturaCaixa.jpg");

		IndexedQuadArray indexedCube = new IndexedQuadArray(8,
				IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS
						| IndexedQuadArray.TEXTURE_COORDINATE_2, 24);
		Point3f[] coordCubo = { new Point3f(1.0f, 1.0f, 1.0f),
				new Point3f(-1.0f, 1.0f, 1.0f),
				new Point3f(-1.0f, -1.0f, 1.0f),
				new Point3f(1.0f, -1.0f, 1.0f), new Point3f(1.0f, 1.0f, -1.0f),
				new Point3f(-1.0f, 1.0f, -1.0f),
				new Point3f(-1.0f, -1.0f, -1.0f),
				new Point3f(1.0f, -1.0f, -1.0f) };

		TexCoord2f[] textCoord = { new TexCoord2f(1.0f, 1.0f),
				new TexCoord2f(0.0f, 1.0f), new TexCoord2f(0.0f, 0.0f),
				new TexCoord2f(1.0f, 0.0f) };
		int coordIndices[] = { 0, 1, 2, 3, 7, 6, 5, 4, 0, 3, 7, 4, 5, 6, 2, 1,
				0, 4, 5, 1, 6, 7, 3, 2 };

		int textIndices[] = { 0, 1, 2, 3, 3, 0, 1, 2, 1, 2, 3, 0, 1, 2, 3, 0,
				3, 0, 1, 2, 1, 2, 3, 0 };

		GeometryInfo gi = new GeometryInfo(indexedCube);

		indexedCube.setCoordinates(0, coordCubo);
		indexedCube.setCoordinateIndices(0, coordIndices);

		indexedCube.setNormals(0, gi.getNormals());
		indexedCube.setNormalIndices(0, gi.getNormalIndices());

		indexedCube.setTextureCoordinates(0, 0, textCoord);
		indexedCube.setTextureCoordinateIndices(0, 0, textIndices);
		Shape3D s = new Shape3D(indexedCube, a);
		return s;
	}

	private TransformGroup criaCaixa() {
		TransformGroup tg = new TransformGroup();
		tg.addChild(caixa());
		Transform3D tr = new Transform3D();
		Transform3D tr1 = new Transform3D();
		tr.setScale(0.4);
		tr1.setTranslation(new Vector3d(0, 0.6, 0));
		tr.mul(tr1, tr);
		tg.setTransform(tr);
		return tg;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

	@Override
	public void setEstado(Estado estado) {
		this.estado = estado;
		switch (estado) {
		case inicio:
			sw.setWhichChild(0);
			alpha.setStartTime(System.currentTimeMillis());
			alpha.pause();
			break;
		case emJogo:
			sw.setWhichChild(2);
			alpha.setIncreasingAlphaDuration(getTempo());
			alpha.setStartTime(System.currentTimeMillis());
			alpha.resume();
			break;
		case perdeu:
			sw.setWhichChild(1);
			alpha.pause();
			break;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		pc.setShapeLocation(e);

		PickResult result = pc.pickClosest();

		if (result != null) {
			TransformGroup r = (TransformGroup) result
					.getNode(PickResult.TRANSFORM_GROUP);
			if (r != null)
				switch (estado) {
				case inicio:
					setEstado(Estado.emJogo);
					break;
				case perdeu:
					setEstado(Estado.inicio);
					break;
				default:
					break;
				}
		}

	}

	@Override
	public List<Obstaculo> getObstaculos() {
		return obstaculos;
	}

	@Override
	public int getComprimento() {
		return comprimento;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand() == "Sair") {
			System.exit(0);
		}
	}
}
