package menu;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *  gère les input du menu dans cette version
 */
public class MenuListener extends JPanel implements ActionListener, ChangeListener{

	AbstractButton[] boutonMenu;
	Enregistreur piFenetre;
	Fenetre menu;

	public MenuListener(Fenetre menu, Enregistreur piFenetre){
		boutonMenu = new AbstractButton[32]; //0: nouvelle partie 1: charger 2 : partie rapide 3 : partie perso 4: lancer la partie
		// 5 à 28 : pour chaque joueur ( aucun, humain, ia1 , ia2, ia32, ia4) 
		// 29 et 30 : obstacle oui et non
		this.piFenetre = piFenetre;
		this.menu =menu;

	}

	public AbstractButton[] getBoutonMenu(){
		return this.boutonMenu;
	}

	
	/*
	 * gère tout les bouton qui sont dans le menu, chaque bouton est retenu dans "AbstractButton[] boutonMenu;" afin dans cette fonction
	 * de pouvoir les différencier et grâce à la boucle if de donner à l'action la bonne réaction.
	 * 
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == boutonMenu[0]){//nouvelle partie
			// Main.menu2(menu, piFenetre);
			// Jeu graphique
			Menu.menu2( piFenetre);
		}
		else if (e.getSource() == boutonMenu[1]){//charger

			piFenetre.getFenetre().getContentPane().removeAll();//ou remove(JComponent)
			Sauvegarde.chargementSauvegarde(piFenetre);
			piFenetre.getFenetre().getContentPane().revalidate();
			piFenetre.getFenetre().getContentPane().repaint();

		}else if (e.getSource() == boutonMenu[2]){ //Partie rapide
			
			Plateau grille = new Plateau(new Joueur( "joueur", 0),new Joueur( "IA2", 2),0,true, 20);
			Menu.iniSwing( 25, 50, 50 , grille, piFenetre);
			piFenetre.getFenetre().getContentPane().revalidate();
			piFenetre.getFenetre().getContentPane().repaint();
			grille.verifControleGlobal();
			grille.affichageConsole();
			grille.annonceGagnantConsole();	
		
		}
		else if (e.getSource() == boutonMenu[3]){ //sélection partie
			
			Menu.menu3(piFenetre);
			
		}else if(e.getSource() == boutonMenu[4]){ // lancer la partie
		
			demoConsole();
			lancerPartie();
			
		}else if(isBouttonJoueur(e)){//paramétrage des joueurs dans partie personnalisé
			
			changerJoueursIni(e);
			
		}else if(e.getSource() == boutonMenu[29]){//paramétrage des obstacles dans partie personnalisé
			
			piFenetre.setObstacle(true);
			
		}else if(e.getSource() == boutonMenu[30]){//paramétrage des obstacles dans partie personnalisé
			
			piFenetre.setObstacle(false);
		}
		

	}

	/**
	 * permet de retourner true si l'event envoyer correspond à un bouton permettant de paramétrer les joueurs
	 * @param e
	 * @return
	 */
	private boolean isBouttonJoueur(ActionEvent e){
		for (int i = 5; i< 29; i++){
			if(e.getSource() == boutonMenu[i]){
				return true;
			}
		}
		return false;
	}

	/**
	 * change dans l'enregistreur pIFenetre le tableau joueursIni dans lequel sont stocké les infos
	 * @param e
	 */
	private void changerJoueursIni(ActionEvent e){
		int numero = -1;
		for (int i = 5; i< 29; i++){
			if(e.getSource() == boutonMenu[i]){
				numero = i-5; //le numéro est désormais le numéro du bouton enclenché
			}
		}
		int bouton = numero%6;
		int joueur = (numero-bouton)/6;
		switch (bouton){
		case 0 : 
			bouton = -1; //on ne veut aucun joueur
			break;
		case 1 : 
			bouton = 0; //on veut humain
			break;
		case 2 : 
			bouton = 1; //on veut IA1
			break;
		case 3 : 
			bouton = 2; //on veut IA2
			break;
		case 4 : 
			bouton = 32; //on veut IA32
			break;
		case 5 : 
			bouton = 4; //on veut IA4
			break;
		default :
			bouton = 241;
			break;
		}
		piFenetre.getJoueurIni()[joueur][0] = bouton;
	}

	/**
	 * pour montrer que le menu marche sur la console
	 */
	private void demoConsole(){
		System.out.println("lancer partie");
		System.out.println("tableau des joueurs:");
		for(int i =0; i<4; i++){
			System.out.print("le joueur " + (i+1) + " est : ");
			System.out.println(piFenetre.getJoueurIni()[i][0]);
			System.out.print("aussi appelé : ");
			System.out.println(piFenetre.getJoueursNoms()[i].getText());

		}
		System.out.print("Veut on des obstacles?");
		System.out.println(piFenetre.getObstacle());
		System.out.print("Le plateau est de la taille : ");
		System.out.println(piFenetre.getTailleS().getValue());
		/* fonctionne aussi:
		System.out.println("piFenetre.getTaille()");
		System.out.println(piFenetre.getTaille());
		*/
	}

	/**
	 * créer le Plateau à envoyer dans iniswing pour lancé théoriquement la partie
	 */
	private void lancerPartie(){
		Plateau grille;
		if(piFenetre.getJoueurIni()[2][0]==-1 && piFenetre.getJoueurIni()[3][0]==-1){ //si il n'y a pas de joueur trois et 4
			grille =  new Plateau(new Joueur( piFenetre.getJoueursNoms()[0].getText(), piFenetre.getJoueurIni()[0][0])
					,new Joueur( piFenetre.getJoueursNoms()[1].getText(), piFenetre.getJoueurIni()[1][0])
					,0,piFenetre.getObstacle(), piFenetre.getTaille());
		}
		else if(piFenetre.getJoueurIni()[2][0]==-1 || piFenetre.getJoueurIni()[3][0]==-1){ //si il n'y a que 3 joueurs
			int i = -1;
			if(piFenetre.getJoueurIni()[2][0]==-1){ // si les joueurs on mis aucun a 2 (joueur 3) on prend se qu'ils ont mis dans le 4 
				i = 3;
			}
			else{//l'inverse
				i=2;
			}
			grille =  new Plateau(new Joueur( piFenetre.getJoueursNoms()[0].getText(), piFenetre.getJoueurIni()[0][0])
					,new Joueur( piFenetre.getJoueursNoms()[1].getText(), piFenetre.getJoueurIni()[1][0])
					,new Joueur( piFenetre.getJoueursNoms()[i].getText(), piFenetre.getJoueurIni()[i][0])
					,0,piFenetre.getObstacle(), piFenetre.getTaille());
		}
		else{ // si on est dans une partie 4 joueurs (0 aucun)
			grille =  new Plateau(new Joueur( piFenetre.getJoueursNoms()[0].getText(), piFenetre.getJoueurIni()[0][0])
					,new Joueur( piFenetre.getJoueursNoms()[1].getText(), piFenetre.getJoueurIni()[1][0])
					,new Joueur( piFenetre.getJoueursNoms()[2].getText(), piFenetre.getJoueurIni()[2][0])
					,new Joueur( piFenetre.getJoueursNoms()[3].getText(), piFenetre.getJoueurIni()[3][0])
					,0,piFenetre.getObstacle(), piFenetre.getTaille());
		}


		Menu.iniSwing( 25, 50, 50 , grille, piFenetre);
		piFenetre.getFenetre().getContentPane().revalidate();
		piFenetre.getFenetre().getContentPane().repaint();
		
		//en théorie on ajoute ici le déroulement de la partie dont la boucle while fait bugger le jeu
		//Menu.deroulementPartieGraphique(grille, piFenetre, 0);
		
		grille.verifControleGlobal();
		grille.affichageConsole();
		grille.annonceGagnantConsole();
		
	}

	/**
	 * surveille le slider pour donner à Enregistreur.taille la bonne valeur.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == piFenetre.getTailleS()){
			piFenetre.setTaille(piFenetre.getTailleS().getValue());
		}
	}
}
