package jogo;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Alpha;
import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import jogo.Jogo.Estado;

import com.sun.j3d.utils.geometry.Sphere;

public class BehaviorDaBola extends Behavior {

	// em milisegundos
	private final static int tempoParaAVolta = 2000;
	private final static int periodo = 20;
	private final static double fps = 1000 / periodo;
	private final static double max = tempoParaAVolta / fps;
	private final static double anguloDeIteracao = Math.PI / max;
	private final static double tSubida = max / 2;
	private final static double escalaSalto = 0.004;
	private final static double escalaSombra = 0.3;
	// 1/intervaloLinha é o valor da iteração no eixo dos xx
	private final static double intervaloLinha = 40.0;
	private final static double maxDeslocamento = intervaloLinha * 3;

	public BehaviorDaBola(Bounds bounds, TransformGroup bola,
			TransformGroup sombra, ArrayList<Point3d> pontos, BranchGroup bg,
			Alpha alpha, SetterEstado alternadorDeEstado) {
		setSchedulingBounds(bounds);
		this.bola = bola;
		this.sombra = sombra;
		this.alpha = alpha;
		this.setterEstado = alternadorDeEstado;
		txtFim = Util.criaTexto("Fim", 1, new Vector3d(0, 0, 0));
		txtFim.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		bg.addChild(txtFim);
		if (mostraEsferasDeColisao)
			mostraEsferasDeColisao(bg);
	}

	private Alpha alpha;
	private int cont = 0;
	private int desloca = 0;
	private int inc = 0;
	private TransformGroup bola;
	private WakeupCriterion[] wcs;
	private TransformGroup sombra;
	private SetterEstado setterEstado;
	private boolean mostraEsferasDeColisao = false;
	private TransformGroup esfera;
	private TransformGroup txtFim;

	@Override
	public void initialize() {
		// 20 ms de periodo corresponde a 50 frames por segundo
		// (1/20)*1000 = 50

		wcs = new WakeupCriterion[2];
		wcs[0] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
		wcs[1] = new WakeupOnElapsedTime(periodo);
		wakeupOn(new WakeupOr(wcs));
	}

	@Override
	public void processStimulus(Enumeration e) {
		while (e.hasMoreElements()) {
			Object criterio = e.nextElement();
			if (criterio instanceof WakeupOnAWTEvent) {
				WakeupOnAWTEvent woea = (WakeupOnAWTEvent) criterio;
				AWTEvent[] triggers = woea.getAWTEvent();
				if (triggers[0] instanceof KeyEvent) {
					int tecla = ((KeyEvent) triggers[0]).getKeyCode();
					if (tecla == KeyEvent.VK_LEFT && desloca > -maxDeslocamento) {
						inc = -1;
						desloca--;
					}
					if (tecla == KeyEvent.VK_RIGHT && desloca < maxDeslocamento) {
						inc = 1;
						desloca++;
					}
				}
			} else if (criterio instanceof WakeupOnElapsedTime) {

				if (cont > max) {
					cont = 0;
				}
				Transform3D tr;
				Transform3D tr1;
				tr = new Transform3D();
				tr1 = new Transform3D();

				if (desloca % intervaloLinha != 0)
					desloca += inc;
				else
					inc = 0;

				double pos = desloca / intervaloLinha;

				tr1.setScale(0.4);

				tr.setTranslation(new Vector3d(0, 0, pos));

				tr.mul(tr, tr1);

				tr1.setEuler(new Vector3d(0, 0, Math.PI
						- (BehaviorDaBola.anguloDeIteracao * cont)));
				tr.mul(tr1, tr);

				double q = (tSubida - cont) * (tSubida - cont);
				q *= escalaSalto;

				if (mostraEsferasDeColisao)
					esferaDeColisao(pos, q);

				tr1.setTranslation(new Vector3d(0, -q, 0));
				tr.mul(tr1, tr);

				tr1.setEuler(new Vector3d(0, Math.PI / 2, 0));
				tr.mul(tr1, tr);

				escalaSombra(pos, -q);
				bola.setTransform(tr);
				cont++;

				double x1 = pos;
				double y1 = -q + 1.2;

				tr = new Transform3D();
				double inicio = -setterEstado.getComprimento();
				tr.setTranslation(new Vector3d(-0.7, 3, alpha.value()
						* setterEstado.getComprimento() + inicio));
				txtFim.setTransform(tr);

				for (Obstaculo obstaculo : setterEstado.getObstaculos()) {
					obstaculo.atualizaPosicao(alpha.value()
							* setterEstado.getComprimento());
					if (obstaculo.colidiu(x1, y1)) {
						alpha.pause();
						setterEstado.setEstado(Estado.perdeu);
					}
				}
			}
		}

		wcs[1] = new WakeupOnElapsedTime(periodo);
		wakeupOn(new WakeupOr(wcs));
	}

	private void esferaDeColisao(double pos, double q) {
		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3d(pos, -q + 1.2, 0));
		esfera.setTransform(tr);
	}

	private void escalaSombra(double pos, double dist) {
		Transform3D tr = new Transform3D();
		Transform3D tr1 = new Transform3D();
		tr.setTranslation(new Vector3d(pos, 0, 0));
		double escala = dist * escalaSombra;
		tr1.setScale(new Vector3d(escala, 1, escala));

		tr.mul(tr, tr1);

		tr1.setTranslation(new Vector3d(0, -1.80, 0));

		tr.mul(tr, tr1);
		sombra.setTransform(tr);
	}

	public void mostraEsferasDeColisao(BranchGroup bg) {
		esfera = new TransformGroup();
		esfera.addChild(new Sphere(0.2f));
		esfera.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		bg.addChild(esfera);
		for (Obstaculo obstaculo : setterEstado.getObstaculos()) {
			bg.addChild(obstaculo.criaEsferaDeColisao());
		}
	}
}
