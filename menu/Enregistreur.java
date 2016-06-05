package menu;


import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/*
 * class permettant de passer les éléments de "partie personnaliser", facilite les 
 * problèmes de porté et aide à l'organisation. Théoriquement inutile
 */
public class Enregistreur {

	private Fenetre fenetre;
	private PieceImage PI;
	private MenuListener contentPane;
	
	//attribue pour les partie paersonnalisé
	private  int[][] joueursIni = {{0},{0},{0},{0}}; //-1 aucun 0 humain 1 ia1 2 ia2 32 ia32 4 ia4
	private JTextField[] joueursNoms;
	private boolean obstacle = true;
	private JSlider tailleS = null;
	private int taille = 20;
	
	public Enregistreur (){
		this.fenetre= new Fenetre(false);
		this.PI = new PieceImage();
		this.joueursNoms = new JTextField[4];
	}

	public PieceImage getPI(){
		return PI;
	}
	
	public Fenetre getFenetre(){
		return fenetre;
	}
	
	public int[][] getJoueurIni(){
		return this.joueursIni;
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

	public JTextField[] getJoueursNoms(){
		return joueursNoms;
	}
	
	public void setObstacle(boolean ob){
		this.obstacle = ob;
	}
	
	public boolean getObstacle(){
		return this.obstacle;
	}
	
	public void setTailleS(JSlider js){
		this.tailleS = js;
	}
	
	public JSlider getTailleS(){
		return this.tailleS;
	}
	
	/**
	 * pas forcément nécessaire dans la mesure où getTailleS().getValue() peut faire l'affaire
	 * @param t
	 */
	public void setTaille(int t){
		this.taille = t;
	}
	
	public int getTaille(){
		return this.taille;
	}
}
