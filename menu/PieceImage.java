package menu;


//d�di�e � l'affichage graphique
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Rectangle; 


import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.AWTException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
/*
	cette classe permet d'afficher la grille en mode graphique en particulier gr�ce � la fonction paint()
	elle est pens� pour �tre resizable. g�re aussi les inputs de souris li�e � ce que veullent jou� les humains

 */

public class PieceImage extends JComponent  implements MouseListener, ActionListener, ItemListener {
	private int rayon;   //rayon d'un hexagone
	private int Xdepart; //X et Y du point en haut � gauche des hexagones
	private int Ydepart;
	private double demiLargeur; //et oui, la demi largeur n'est pas le rayon; on est pas dans un cercle...
	private double demiCote; //longueur de la moiti� d'un cot� de l'hexagone
	private Plateau plateau;  //plateau � affich�
	private Fenetre fenetre;
	private JLabel[] joueurJLabel; //contient le JLabel qui affiche le nom du joueur !!! joueur 1 en indice 0
	private BasicStroke stroke; //pour d�finir la trait utiliser pour faire les contours de l'hexagone
	private int strokeWidth; // � conserver pour espac� les polygones
	private Color[] couleur; //les couleurs des hexa
	private Color[] couleurJoueur; //couleur autour des hexa du joueur

	/**
	 * pour l'enregistreur (peut-�tre obsol�te)
	 */
	public PieceImage(){
	}

	/**
	 * 
	 * @param fenetre
	 * @param rayon
	 * @param Xdepart
	 * @param Ydepart
	 * @param plateau
	 */
	public PieceImage( Fenetre fenetre, int rayon, int Xdepart, int Ydepart, Plateau plateau){
		this.fenetre = fenetre;
		/* obsol�te car d�finie � chaque paint() pour qu'il soit resizable
		this.rayon = rayon;  
		this.Xdepart = Xdepart;  
		this.Ydepart = Ydepart;
		 */
		this.plateau = plateau;

		/*obsol�te
		this.demiLargeur = rayon * Math.cos( 30 * Math.PI /180 );
		this.demiCote = rayon * Math.sin( 30 * Math.PI /180 );
		this.strokeWidth = (int) rayon/10;
		this.stroke = new BasicStroke(this.strokeWidth);
		 */

		//ci dessous les couleurs du jeu d'abord les hexas et apr�s les joueurs
		this.couleur = new Color[7];
		this.couleur[0] = new Color( 255, 0, 0); // rouge
		this.couleur[1] = new Color( 255, 128, 0); //orange
		this.couleur[2] = new Color( 255, 255, 0); //jaune 
		this.couleur[3] = new Color(0, 255, 0); //vert
		this.couleur[4] = new Color( 0, 128, 255); //bleu clair
		this.couleur[5] = new Color( 255, 0, 255); //indigo
		this.couleur[6] = new Color( 164, 164, 164); //obstacle, gris
		this.couleurJoueur = new Color[5];
		this.couleurJoueur[0] = new Color( 0, 0, 0); //personne, noir
		this.couleurJoueur[1] = new Color( 160, 83, 0); //joueur1, marron
		this.couleurJoueur[2] = new Color( 10, 130,10); //j2, vert foncer
		this.couleurJoueur[3] = new Color( 0, 0, 200); //j3, bleu foncer
		this.couleurJoueur[4] = new Color( 110, 0, 67); //j4 bordeaux foncer
	}

	public Fenetre getFenetre(){
		return this.fenetre;
	}

	public int getXdepart() {
		// TODO Auto-generated method stub
		return this.Xdepart;
	}

	public int getYdepart() {
		// TODO Auto-generated method stub
		return this.Ydepart;
	}  

	public JLabel[] getJoueurJLabel(){
		return this.joueurJLabel;
	}

	public void setJoueurJLabel(JLabel[] a){
		this.joueurJLabel = a;
	}

	/**
	 * probl�me sur le joueur entour� c'est le joueur d'avant probleme du placament du 
	 */
	private void bordJoueur(){
		for (int i=0; i < this.plateau.getNbJoueur(); i++ ){
			Border bord = BorderFactory.createLineBorder(Color.BLACK,0);
			if(this.plateau.getJouant() == i){
				bord = BorderFactory.createLineBorder(couleurJoueur[i+1], 5);
			}
			this.joueurJLabel[i].setBorder(bord);				
		}
	}

	/**
	 * � chaque appel de paint on reset les attribut dans cette fonction en fonction du rayon qui est relatif � la taille de la fenetre(getHeight())
	 * afin que la grille est une taille appropri�
	 * @param rayon
	 */
	private void setAttribute(int rayon){
		this.rayon = rayon;  
		this.demiLargeur = rayon * Math.cos( 30 * Math.PI /180 );
		this.demiCote = rayon * Math.sin( 30 * Math.PI /180 );
		this.strokeWidth = (int) rayon/10;
		this.stroke = new BasicStroke(this.strokeWidth);
		int ligne = this.plateau.getCases().length;
		this.Ydepart = (this.getHeight()- (rayon*(ligne+1) + (int) (demiCote * (ligne-1) + 2*(ligne)*strokeWidth)))/2;   //(rayon*ligne + (int) (6*strokeWidth + demiCote * ligne + 2*ligne*strokeWidth)))/2;// 15/this.plateau.getTaille()*4; // la moiti� de la place qu'on laisse en marge au dessus et en dessous (cf taille du rayon envoyer dans cette fct quand appel�)  
		this.Xdepart = 20;
	}

	/**
	 * permet de dessiner la grille (et de l'entretenir � l'aide de la fonction repaint())
	 * En premier on imprime les polygones selon leur couleur et apr�s on dessine les contours pour d�termin� l'appartenance
	 * Elle dessine aussi le background de la grille ainsi que les couleurs dispo, pr�vu pour �tre resizable
	 * 
	 * @param Graphics g (la fonction n'a pas a �tre appel�)
	 */
	public void paint(Graphics g) { //paintComponent
		if (plateau != null){
			Graphics2D g2 = (Graphics2D) g;
			if(this.joueurJLabel != null) {
				bordJoueur();				
			}

			int[][] cases = plateau.getCases();
			int[][] casesControle = plateau.getCasesControle();
			int ligne = cases.length;
			int colonne = cases.length;
			int x = 0;
			int y = 0;
			int rayon =(int) ((0.9*this.fenetre.getHeight())/(ligne+1+ (Math.sin( 30 * Math.PI /180 ))*(ligne-1) + ( (2*ligne +6) /10 ))); //le rayon est calcul� de mani�re � ce que toute la grille (rectangle gris) face 90/100 de l'�cran
			setAttribute(rayon);

			g2.setStroke(stroke);
			double decal = 0; //+demiLargeur correspond au d�calage pour que les polygones soit en quinquonce


			int xRect1 = (int) (Xdepart - strokeWidth);
			int yRect1= (int) (Ydepart - strokeWidth);
			int xRect2 = (int) (demiLargeur*2*(colonne) + demiLargeur + strokeWidth + 2*(colonne)*strokeWidth);
			int yRect2=   rayon*(ligne+1) + (int) (demiCote * (ligne-1) + 2*(ligne)*strokeWidth);

			g2.setColor(Color.GRAY);	//background
			g2.fill(new Rectangle( (int) (xRect1-3*strokeWidth), (int) (yRect1-3*strokeWidth),  
					(int) (xRect2+6*strokeWidth),  
					(int) (yRect2+6*strokeWidth) ));
			g2.setColor(Color.BLACK);//contour du background
			g2.draw(new Rectangle( (int) (xRect1-3*strokeWidth), (int) (yRect1-3*strokeWidth),  
					(int) (xRect2+6*strokeWidth),  
					(int) (yRect2+6*strokeWidth)));	//background*/

			for (int i = 0; i < ligne ; i++){

				y = (Ydepart + rayon) + rayon*i + (int)( demiCote * i + 2*i*strokeWidth);
				if (i%2 != 0){
					decal = strokeWidth; // + (int) (- demiLargeur + demiLargeur); 
				}
				else{
					decal = - demiLargeur;
				}

				for (int j= 0; j< colonne; j++){
					x =  Xdepart + (int) (demiLargeur*2*(j+1) + decal + 2*j*strokeWidth);
					g2.setColor(this.couleur[cases[i][j]]); //prend la couleur de l'hexa a affich�

					Polygon p = new Polygon();
					for (int a = 0; a < 6; a++){
						p.addPoint((int) (x + rayon * Math.cos( (a * 2 + 1) * Math.PI / 6) ), (int) (y + rayon * Math.sin((a * 2 + 1) * Math.PI / 6)));
					}
					g2.fillPolygon(p); //dessine le coeur du polygone de la couleur voulue

					g2.setColor(this.couleurJoueur[casesControle[i][j]]);
					g2.drawPolygon(p); //dessine le contour du polygone selon l'appartenance			
				}
			}
			// on dessine les couleurs dispo sur le cot�
			int[] coulDispo = this.plateau.couleursJouables();
			x =  Xdepart + (int) (demiLargeur*2*(ligne+2) + decal + 2*(ligne+1)*strokeWidth);
			int i = (ligne/2)-(coulDispo.length/2);
			for (int coul : coulDispo){
				y = (Ydepart + rayon) + rayon*i + (int)( demiCote * i + 2*i*strokeWidth);
				g2.setColor(this.couleur[coul]); //prend la couleur de l'hexa a affich�				
				i++;
				Polygon p = new Polygon();
				for (int a = 0; a < 6; a++){
					p.addPoint((int) (x + rayon * Math.cos( (a * 2 + 1) * Math.PI / 6) ), (int) (y + rayon * Math.sin((a * 2 + 1) * Math.PI / 6)));
				}
				g2.fillPolygon(p); //dessine le coeur du polygone de la couleur voulue	
			}

		}
	}

	/**
	 * enregistrement de la partie
	 * 
	 * li�e au bouton du MenuBar
	 */
	public void actionPerformed(ActionEvent e) {
		//premettra de lanc� une sauvegarde d�s que le tour de la personne � fini (ou que l'humain ne fait rien)
		this.plateau.setVeutEnregistrer(true);
	}


	/**
	 * permet d'�couter les inputs de la souris pendant la partie pour d�termin� la couleur que le joueur souhaite jou� et la lui faire jou�
	 * 
	 * quand un clique est effectu� la fct v�rifie que c'est bien au tour d'un humain puis regarde la couleur (awt Color) du pixel. Si cette couleur 
	 * correspond � une des couleurs disponibles le coup est jou� pour le joueur humain dont c'est le tour puis pour le bon deroulementPartieGraphique()
	 * on signal que le coup est jou� (aJoue = true) 
	 * Si le coup n'est pas disponible ou obstacle ou simplement pas une couleur du jeu il ne se passe rien
	 */
	public void mousePressed(MouseEvent e) { 
		try {
			if(!this.plateau.getJouantIsIA()){ //ne prendre les �venements que si la personne en train de jouer n'est pas une ia
				Robot robot=new Robot();
				PointerInfo MInfo= MouseInfo.getPointerInfo();
				Point point = MInfo.getLocation();
				Color couleurRecup= robot.getPixelColor((int) point.getX(),(int) point.getY());
				int couleurIdent = 404;
				for(int i = 0; i <= 6; i++){				 			
					if (this.couleur[i].getRGB() == couleurRecup.getRGB()){
						couleurIdent = i;/*
						System.out.print("on a bien identifier la couleur : ");
						System.out.print(i);
						System.out.print(",    ");
						System.out.print("couleurRecup : ");
						System.out.println(couleurRecup);*/
						break;
					}
				}
				if( (couleurIdent < 6) && (couleurIdent >= 0)){
					int[] couleursDispo = this.plateau.couleursJouables();
					boolean dispo = false;
					for(int cj : couleursDispo){
						if(cj == couleurIdent){
							//System.out.println("la couleur a �t� jou�");
							this.plateau.joue(this.plateau.getJouantv2(), couleurIdent);
							this.plateau.setAJoue(true); 
						}
					}
					if(!dispo){
						//System.out.println("la couleur est pas dispo");
					}

				}
				else if (couleurIdent == 6){
					//System.out.println("ceci est un obstacle");
				}
				else if (couleurIdent == 404){
					/*System.out.println("404 error. Couleur not found!");
					System.out.println("couleurRecup : ");
					System.out.println(couleurRecup);*/
				}else{
					/*System.out.println("couleurIdent :");
					System.out.println(couleurIdent);
					System.out.println("couleurRecup : ");
					System.out.println(couleurRecup);*/
				}
			}
			else{
				//System.out.println("l'IA est en train de r�fl�chir");
			}
		}
		catch (AWTException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public int getRayon() {
		// TODO Auto-generated method stub
		return this.rayon;
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub

	}

}
