package jogo;

import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

public class Grelha extends Shape3D {
	private static Appearance ap;
	
	private static Appearance getAparencia(){
		if (ap ==null) {
			ap = new Appearance();
			//Copper
			Material mat = new Material();
			mat.setAmbientColor(0.19125f, 0.0735f, 0.0225f);
			mat.setDiffuseColor(0.7038f, 0.27048f, 0.0828f);
			mat.setSpecularColor(0.256777f, 0.137622f, 0.086014f);
			mat.setShininess(12.8f);
			ap.setMaterial(mat);
		}
		return ap;
	}
	public Grelha() {

		Point3d[] vertices = new Point3d[72];
		int[] indicesCores = new int[72];

		for (int z = 0; z < 2; z++) {
			for (int x = 0; x < 6; x++) {
				for (int y = 0; y < 6; y++) {
					int i = 36 * z + 6 * x + y;
					vertices[i] = new Point3d(x, y, z);
					indicesCores[i] = z;
				}
			}
		}

		ArrayList<Integer> listaIndices = new ArrayList<>();

		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				if (!(i % 2 == 1 && j % 2 == 1)) {
					listaIndices.add(6 * j + 36 + i + 1);
					listaIndices.add(6 * j + i + 36);
					listaIndices.add(6 * j + 6 + 36 + i);
					listaIndices.add(6 * j + 6 + 36 + i + 1);

					//Teste.imprime4ultimos(listaIndices);
				}
			}
		}

		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				if (!(i % 2 == 1 && j % 2 == 1)) {
					listaIndices.add(6 * j + i);
					listaIndices.add(6 * j + i + 1);
					listaIndices.add(6 * j + 6 + i + 1);
					listaIndices.add(6 * j + 6 + i);
					//Teste.imprime4ultimos(listaIndices);
				}
			}
		}

		// faces interiores
		int[] arr = { 7, 9, 19, 21 };
		for (int i : arr) {
			listaIndices.add(i);
			listaIndices.add(i + 36);
			listaIndices.add(i + 36 + 6);
			listaIndices.add(i + 6);

			listaIndices.add(i);
			listaIndices.add(i + 1);
			listaIndices.add(i + 36 + 1);
			listaIndices.add(i + 36);

			listaIndices.add(i + 36 + 1);
			listaIndices.add(i + 1);
			listaIndices.add(i + 6 + 1);
			listaIndices.add(i + 36 + 1 + 6);

			listaIndices.add(i + 36 + 6 + 1);
			listaIndices.add(i + 6 + 1);
			listaIndices.add(i + 6);
			listaIndices.add(i + 36 + 6);

		}

		// parte exterior
		for (int i = 0; i < 5; i++) {
			int j = i * 6;
			int k = j + 36;

			listaIndices.add(k);
			listaIndices.add(j);
			listaIndices.add(j + 6);
			listaIndices.add(k + 6);

			listaIndices.add(i + 1);
			listaIndices.add(i);
			listaIndices.add(i + 36);
			listaIndices.add(i + 36 + 1);

			listaIndices.add(j + 5);
			listaIndices.add(j + 5 + 36);
			listaIndices.add(j + 5 + 36 + 6);
			listaIndices.add(j + 5 + 6);

			listaIndices.add(i + 30);
			listaIndices.add(i + 30 + 1);
			listaIndices.add(i + 30 + 36 + 1);
			listaIndices.add(i + 30 + 36);

		}

		GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);

		int[] indices = new int[listaIndices.size()];

		for (int i = 0; i < listaIndices.size(); i++) {
			indices[i] = listaIndices.get(i);
		}

		gi.setCoordinates(vertices);
		gi.setCoordinateIndices(indices);

		NormalGenerator ng = new NormalGenerator();
		ng.generateNormals(gi);
		setGeometry(gi.getGeometryArray());
		// material e aparência
		this.setAppearance(getAparencia());
		pontoRef = vertices[50];
	}
	private Point3d pontoRef;
	public Point3d ponto(){
		return pontoRef;
	}
}
