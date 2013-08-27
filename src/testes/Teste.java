package testes;

import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.text.html.ListView;

import com.sun.j3d.utils.geometry.GeometryInfo;

public class Teste {
	public static void imprime4ultimos(ArrayList<Integer> lista) {
		ArrayList<Integer> l1 = new ArrayList<>();
		l1.add(lista.get(lista.size() - 4));
		l1.add(lista.get(lista.size() - 3));
		l1.add(lista.get(lista.size() - 2));
		l1.add(lista.get(lista.size() - 1));
		System.out.printf("%d %d %d %d\n", l1.toArray());
	}

	public static Appearance transparente(){
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
		return ap;
	}
	
	public static Appearance wireFrame(){
		Appearance ap = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		ap.setPolygonAttributes(pa);
		return ap;
	}
	
	public static void main(String[] args) {
		GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
		// faces da frente e de traz
		ArrayList<Integer> listaCoords = new ArrayList<>();

		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				if (!(i % 2 == 1 && j % 2 == 1)) {
					listaCoords.add(6 * j + i);
					listaCoords.add(6 * j + i + 1);
					listaCoords.add(6 * j + 6 + i + 1);
					listaCoords.add(6 * j + 6 + i);
					Teste.imprime4ultimos(listaCoords);
				}
			}
			listaCoords.clear();
		}
		System.out.println();

		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				if (!(i % 2 == 1 && j % 2 == 1)) {
					listaCoords.add(6 * j + 36 + i + 1);
					listaCoords.add(6 * j + i + 36);
					listaCoords.add(6 * j + 6 + 36 + i + 1);
					listaCoords.add(6 * j + 36 + i + 1);

					Teste.imprime4ultimos(listaCoords);
				}
			}
			System.out.println();
		}
		// faces interiores
		System.out.println();
		int[] arr = { 7, 9, 19, 21 };
		for (int i : arr) {
			listaCoords.add(i);
			listaCoords.add(i + 36);
			listaCoords.add(i + 36 + 6);
			listaCoords.add(i + 6);

			Teste.imprime4ultimos(listaCoords);

			listaCoords.add(i);
			listaCoords.add(i + 1);
			listaCoords.add(i + 36 + 1);
			listaCoords.add(i + 36);

			Teste.imprime4ultimos(listaCoords);

			listaCoords.add(i + 1);
			listaCoords.add(i + 36 + 1);
			listaCoords.add(i + 36 + 1 + 6);
			listaCoords.add(i + 6 + 1);

			Teste.imprime4ultimos(listaCoords);
			
			listaCoords.add(i + 6 + 1);
			listaCoords.add(i + 36 + 6 + 1);
			listaCoords.add(i + 36 + 6);
			listaCoords.add(i + 6);

			Teste.imprime4ultimos(listaCoords);

			System.out.println();
		}
		System.out.println();
		// parte exterior
		for (int i = 0; i < 5; i++) {
			int j = i * 6;
			int k = j + 36;
			
			listaCoords.add(j);
			listaCoords.add(k);
			listaCoords.add(k + 6);
			listaCoords.add(j + 6);

			Teste.imprime4ultimos(listaCoords);
			
			listaCoords.add(i);
			listaCoords.add(i + 1);
			listaCoords.add(i + 36 + 1);
			listaCoords.add(i + 36);

			Teste.imprime4ultimos(listaCoords);
			
			listaCoords.add(j + 5);
			listaCoords.add(j + 5 + 36);
			listaCoords.add(j + 5 + 36 + 6);
			listaCoords.add(j + 5 + 6);

			Teste.imprime4ultimos(listaCoords);
			
			listaCoords.add(i + 30);
			listaCoords.add(i + 30 + 1);
			listaCoords.add(i + 30 + 36 + 1);
			listaCoords.add(i + 30 + 36);
			
			Teste.imprime4ultimos(listaCoords);
			System.out.println();
		}
		System.out.println();
	}
}
