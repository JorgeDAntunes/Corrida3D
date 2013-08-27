package jogo;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

public class Util {
	public static TransformGroup criaTexto(String string, double e, Vector3d v) {
		TransformGroup tg = new TransformGroup();
		Font3D font = new Font3D(new Font("Serif", Font.PLAIN, 1),
				new FontExtrusion());
		Text3D text = new Text3D(font, string);
		Appearance ap = new Appearance();
		
		Material mat = new Material();
		mat.setAmbientColor(0.19125f, 0.0735f, 0.0225f);
		mat.setDiffuseColor(0.7038f, 0.27048f, 0.0828f);
		mat.setSpecularColor(0.256777f, 0.137622f, 0.086014f);
		mat.setShininess(12.8f);
		ap.setMaterial(mat);

		
		Shape3D shape = new Shape3D(text, ap);
		tg.addChild(shape);
		Transform3D tr = new Transform3D();
		Transform3D tr1 = new Transform3D();
		tr.setTranslation(v);
		tr1.setScale(e);
		tr.mul(tr1, tr);
		tg.setTransform(tr);
		return tg;
	}

}
