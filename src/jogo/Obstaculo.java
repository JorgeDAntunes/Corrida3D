package jogo;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;

public class Obstaculo {
	private TransformGroup tgLOD;
	private double x, z, zAtual;
	private static double y = 0.7;
	private TransformGroup esfera = null;
	Switch sw = new Switch(0);


	public Obstaculo(BranchGroup bg, TransformGroup tg,double x, double z) {
		this.x = x;
		this.z = z;
		tgLOD = new TransformGroup();
		tgLOD.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgLOD.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_READ);
		sw.setCapability(javax.media.j3d.Switch.ALLOW_SWITCH_WRITE);
		sw.addChild(null);
		sw.addChild(tg);
		tgLOD.addChild(sw);
		float[] distances = new float[1];
		distances[0] = 40.0f;
		
		DistanceLOD lod = new DistanceLOD(distances);
		lod.addSwitch(sw);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		lod.setSchedulingBounds(bounds);
		tgLOD.addChild(lod);

		bg.addChild(tgLOD);
		
		atualizaPosicao(0);
	}

	
	
	
	public void atualizaPosicao(double deslZ) {
		this.zAtual = z + deslZ;
		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3d(x, 0, zAtual));
		tgLOD.setTransform(tr);
		
		if (esfera!= null) {
			tr = new Transform3D();
			tr.setTranslation(new Vector3d(x, y, zAtual));
			esfera.setTransform(tr);
		}
	}

	public boolean colidiu(double xBola, double yBola) {
		
		double dx = x - xBola;
		double dy = y - yBola;
		double dz = zAtual/*-0*/;
		dx *= dx;
		dy *= dy;
		dz *= dz;
		return Math.sqrt(dx + dy + dz) < 0.6;
	}

	public static double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public double getZ() {
		return z;
	}




	public TransformGroup criaEsferaDeColisao() {
		esfera = new TransformGroup();
		esfera.addChild(new Sphere(0.2f));
		esfera.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		return esfera;
	}
}
