package jogo;

import java.util.List;

import jogo.Jogo.Estado;

public interface SetterEstado {
	void setEstado(Estado estado);
	List<Obstaculo> getObstaculos();
	int getComprimento();
}
