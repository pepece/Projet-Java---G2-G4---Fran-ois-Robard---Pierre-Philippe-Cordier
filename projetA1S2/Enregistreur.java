package projetA1S2;

import javax.swing.JPanel;


/*
 * class permettant de passer les �l�ments de "partie personnaliser", facilite les 
 * probl�mes de port� et aide � l'organisation. Th�oriquement inutile
 * 
 * ici, la version presque vide... la version compl�te dans le projet avec menu graphique. 
 */

public class Enregistreur {

	private Fenetre fenetre;
	private PieceImage PI;
	private MenuListener contentPane;
	
	public Enregistreur (){
		this.fenetre= new Fenetre(false);
		this.PI = new PieceImage();
	}

	public PieceImage getPI(){
		return PI;
	}
	
	public Fenetre getFenetre(){
		return fenetre;
	}


	public void setPI(PieceImage PI){
		this.PI = PI;
	}
	
	public void setFenetre(Fenetre fenetre){
		this.fenetre = fenetre;
	}

	public void setMenuListener(MenuListener tout) {
		// TODO Auto-generated method stub
		this.contentPane = tout;
		
	}

}
