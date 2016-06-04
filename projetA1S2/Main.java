package projetA1S2;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.Thread;

import java.util.Scanner;


//JFrame new Jframe dans charger partie
//bien afficher les couleur jouables pour Èviter d'etre bloque en fin de partie
// 3 joueurs changer le final
//fct qui crÈer une grille
//opti changement de couleur

/**
 * ceci est la main pour la version menu en console et affichage graphique fonctionnelle
 */

public class Main {


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//menu();
		jeu();

	}


	/**
	 * permet de crÈer/recrÈer(initialiser) la Fenetre qui affichera la grille pendant le jeu,
	 *  organise les diffÈrents layout de la fentre de jeu; puis le mouselistener / paint
	 * @param rayon
	 * @param Xdepart
	 * @param Ydepart
	 * @param grille2
	 * @param piFenetre
	 */
	public static void iniSwing(int rayon, int Xdepart, int Ydepart, Plateau grille2, Enregistreur piFenetre){
		/*
		 * si tentative avec le menu la fenetre existe dÈja donc:
		 */
		//piFenetre.getFenetre().getContentPane().removeAll();
		//MenuListener tout = new MenuListener(piFenetre.getFenetre(), piFenetre);
		/*
		 * sinon :
		 */
		Fenetre fenetre = new Fenetre(true);
		piFenetre.setFenetre(fenetre);
		JPanel tout = new JPanel();

		//debut iniswing
		PieceImage PI = new PieceImage( piFenetre.getFenetre(), rayon, Xdepart, Ydepart , grille2);
		tout.add(PI);
		JMenuBar menuBar;
		JMenu option;
		JMenuItem menuItem;
		//CrÈer la barre du menu
		menuBar = new JMenuBar();
		//crÈe le menu
		option = new JMenu("Option");
		//crÈe l'option "sauvegarder
		menuItem = new JMenuItem("Sauvegarder");
		//crÈation du raccourci ctrl+s
		KeyStroke controleS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
		menuItem.setAccelerator(controleS);
		//association ‡ l'action lostener PI
		menuItem.addActionListener(PI);
		option.add(menuItem);
		menuBar.add(option);
		piFenetre.getFenetre().setJMenuBar(menuBar);
		tout.setLayout(new BorderLayout());
		JPanel centre = new JPanel();
		centre.setLayout(new BoxLayout(centre, BoxLayout.PAGE_AXIS));
		Color bg = new Color( 0, 0 , 128);
		centre.setBackground(bg);
		PI.setJoueurJLabel(new JLabel[grille2.getNbJoueur()]);
		Font joueurFont = new Font("arial", Font.BOLD, 20);
		for (int i=0; i<grille2.getNbJoueur(); i++){
			String s = grille2.getJoueurv2(i+1).getNom();
			PI.getJoueurJLabel()[i] = new JLabel(s);
			PI.getJoueurJLabel()[i].setFont(joueurFont);
			centre.add(PI.getJoueurJLabel()[i]);
			if(i != grille2.getNbJoueur()){
				centre.add(Box.createRigidArea(new Dimension(0,15))); //pour espacer les joueurs
			}
		}
		//pour laissÈ une petite marge ‡ droite
		JPanel pMarge = new JPanel();
		pMarge.setLayout(new BoxLayout(pMarge, BoxLayout.PAGE_AXIS));
		pMarge.add(Box.createRigidArea(new Dimension(10,0)));

		tout.setBackground(bg);
		centre.add(Box.createRigidArea(new Dimension(0, 17)));
		tout.setLayout(new BoxLayout(tout, BoxLayout.LINE_AXIS));
		tout.add(centre);
		tout.add(pMarge);
		tout.addMouseListener(PI);
		piFenetre.setPI(PI);
		piFenetre.getFenetre().getContentPane().add(tout);
		piFenetre.getFenetre().validate();
		piFenetre.getFenetre().revalidate();
		piFenetre.getFenetre().repaint();
	}

	/**
	 * appelÈ uniquement lors des parties en mode console
	 * fait jouÈ les joueur et IA tour ‡ tour jusqu'‡ la fin de la partie (getFin() == true)
	 */
	public static void deroulementPartieConsole(Plateau grille){
		int nbJoueur = grille.getNbJoueur();
		Joueur[] joueur = Joueur.tableJoueur(grille);
		while(!grille.getFin()){
			for (int i = 0; i < nbJoueur; i++){
				if(!grille.getFin()){
					grille.joueConsole(joueur[i]);
				}
				else{
					break;
				}
			}
		}
	}

	/**
	 * fait jouÈ les joueur et IA tour ‡ tour jusqu'‡ la fin de la partie (getFin() == true)
	 * appelÈ uniquement lors des parties en en mode graphique (meme si affiche aussi sur la console)
	 * les trois variables essentiel ‡ comprendre dans cette fonction sont jouant, aJoue, jouantIsIA contenue dans le Plateau grille
	 * jusqu'‡ ce que la partie soit fini, tant qu'un joueur (IA ou non) n'a pas encore jouÈ soit on attend d'avoir un ÈvÈnement depuis la fct
	 * mousePressed de la classe PieceImage soit on fait jouÈ l'IA
	 * @param grille
	 * @param piFenetre
	 */
	public static void deroulementPartieGraphique(Plateau grille, Enregistreur piFenetre, int jouant){
		System.out.println("deroul graphique");
		int nbJoueur = grille.getNbJoueur();
		Joueur[] joueur = Joueur.tableJoueur(grille);
		boolean[] isIA = Joueur.tableIsIA(joueur);
		//pour des raison de portÈ j'ai du mettre les trois variables utilisÈ ci dessous dans Plateau jouant, aJoue, jouantIsIA
		grille.setJouant(jouant); //numÈro de joueur en train de jouer dans la boucle
		grille.setAJoue(false);  //le joueur n'a pas encore jouÈ
		grille.setJouantIsIA(isIA[grille.getJouant()]); //est'il une IA (faut-il prendre en compte les inputs souris du joueur, faut-il lancÈ un joueGraphique()

		while(!grille.getFin()){// jusqu'‡ la fin
			while( grille.getAJoue() == false) {  //tant que le coup n'a pas ÈtÈ jouÈ
				if(grille.getVeutEnregistrer()){
					Sauvegarde.sauvegarde(grille, piFenetre.getPI());
				}
				if(isIA[grille.getJouant()]){
					grille.setAJoue(grille.joueIAGraphique(joueur[grille.getJouant()], piFenetre)); //si on est face a une IA on la fait jouer et on en avertie la boucle en rnvoyant true 
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//passe au joueur d'aprËs
			grille.setJouant( (grille.getJouant()+1) );
			if (grille.getJouant() >= nbJoueur){
				grille.setJouant(0);
			}
			grille.setAJoue(false); 
			grille.setJouantIsIA(isIA[grille.getJouant()]);
			piFenetre.getFenetre().revalidate();
			piFenetre.getFenetre().repaint();
		}
	}

	/**
	 * fonctiion contenant l'initialisation avec la console dans le cas o˘ on n'essaye pas d'utiliser le menu
	 */
	public static void jeu(){
		System.out.println("Bienvenue dans le jeu des 6 couleurs.");
		System.out.println("Tapez 1 pour la version minimale du jeu dans la console.");
		System.out.println("Tapez 2 pour la version graphique.");
		System.out.println("Tapez 3 pour reprendre une partie enregistr√©e.");
		Scanner scan = new Scanner(System.in);
		int a = scan.nextInt();

		boolean ok=false;

		while(ok==false){
			switch(a){
			case 1:
				// Jeu dans la console
				Plateau grille = initialisationJeuConsole();     
				grille.verifControleGlobal();
				grille.affichageConsole();
				deroulementPartieConsole(grille);
				grille.annonceGagnantConsole(); 
				ok=true;
				break;

			case 2:
				// Jeu graphique
				Plateau grille2 = initialisationJeuConsole();
				Enregistreur piFenetre = new Enregistreur();
				iniSwing( 25, 50, 50 , grille2, piFenetre);
				piFenetre.getFenetre().repaint();
				grille2.verifControleGlobal();
				//grille2.affichageConsole();
				deroulementPartieGraphique(grille2, piFenetre,0);// On commence par le premier joueur
				grille2.annonceGagnantConsole();
				ok=true;
				break;
			case 3:
				//ouverture du jeu depuis un fichier
				Enregistreur pFenetre =new Enregistreur();
				Sauvegarde.chargementSauvegarde(pFenetre);
				ok=true;
				break;

			default:
				System.out.println("Vous n'avez pas entr√É¬© une valeur correcte, r√É¬©essayez");
				a = scan.nextInt();
			}
		}
	}

	/**
	 * CrÈer un plateau dans le cas o˘ l'on n'utilise pas le menu, appelÈ par Main.jeu()
	 * @return
	 */    
	public static Plateau initialisationJeuConsole(){
		boolean ok=false;
		System.out.println("Quelle taille de la grille de jeu (nombre de cases de c√¥t√©) souhaitez-vous ?");
		Scanner scan = new Scanner(System.in);
		int a = scan.nextInt();
		while(!ok){
			if(a>4 ){
				ok=true;
			}
			else{
				System.out.println("Valeur incorrecte, r√©eessayez :");
				a = scan.nextInt();
			}
		}
		ok=false;
		int taille = a;
		System.out.println("A combien de joueurs voulez-vous jouer? (2,3 ou 4) :");
		a = scan.nextInt();
		while(!ok){
			if(a==2 | a==3 | a==4){
				ok=true;
			}
			else{
				System.out.println("Valeur incorrecte, r√©eessayez :");
				a = scan.nextInt();
			}
		}
		ok=false;
		System.out.println("Voulez vous autoriser les obstacles ? Si non tapez '0', si oui tapez '1'");
		int obstacleInt=scan.nextInt();
		while(!ok){
			if(obstacleInt==0 | obstacleInt==1){
				ok=true;
			}
			else{
				System.out.println("Valeur incorrecte, r√©essayez :");
				obstacleInt = scan.nextInt();
			}
		}
		boolean obstacle=false;
		if(obstacleInt==1){
			obstacle=true;
		}

		// cr√©ation du joueur 1
		ok=false;
		System.out.println("Voulez-vous que le premier joueur soit une IA ? Si non tapez '0', si oui tapez '1', '2','31' ou '32' respectivement pour une pour une IA de niveau 1, 2, 3-1 ou 3-2.");
		int ia=scan.nextInt();
		while(!ok){
			if(ia==0 | ia==1 | ia==2 | ia==31 | ia==32 | ia==4){
				ok=true;
			}
			else{    
				System.out.println("Valeur incorrecte, r√©essayez :");
				ia = scan.nextInt();
			}
		}
		String nom="IA 1";
		if(ia==0){
			System.out.println("Entrez le nom du premier joueur :");
			Scanner sca = new Scanner(System.in);
			nom=sca.nextLine();
		}
		Joueur A=new Joueur(nom,ia);

		// Cr√©ation du joueur 2
		ok=false;
		System.out.println("Voulez-vous que le troisi√®me joueur soit une IA ? Si non tapez '0', si oui tapez '1', '2','31', '32' ou '4' respectivement pour une pour une IA de niveau 1, 2, 3-1, 3-2 ou 4.");
		int ia2=scan.nextInt();
		while(!ok){
			if(ia2==0 | ia2==1 | ia2==2 | ia2==31 | ia2==32  | ia2==4){
				ok=true;
			}
			else{    
				System.out.println("Valeur incorrecte, r√©essayez :");
				ia2 = scan.nextInt();
			}
		}
		String nom2="IA 2";
		if(ia2==0){
			System.out.println("Entrez le nom du second joueur :");
			Scanner sca = new Scanner(System.in);
			nom2=sca.nextLine();
		}
		Joueur B=new Joueur(nom2,ia2);

		//Cr√©ation d'un plateau deux joueurs qui sera remplac√© si il y a plus de joueurs
		Plateau grille=new Plateau(A,B,0,obstacle,taille);

		//Si il y a trois ou quatre joueurs
		if(a==3){
			// Cr√©ation du joueur 3
			ok=false;
			System.out.println("Voulez-vous que le troisi√®me joueur soit une IA ? Si non tapez '0', si oui tapez '1', '2','31', '32' ou '4' respectivement pour une pour une IA de niveau 1, 2, 3-1, 3-2 ou 4.");
			int ia3=scan.nextInt();
			while(!ok){
				if(ia3==0 | ia3==1 | ia3==2 | ia3==31 | ia3==32  | ia3==4){
					ok=true;
				}
				else{    
					System.out.println("Valeur incorrecte, r√©essayez :");
					ia3 = scan.nextInt();
				}
			}
			String nom3="IA 3";
			if(ia3==0){
				System.out.println("Entrez le nom du troisi√®me joueur :");
				Scanner sca = new Scanner(System.in);
				nom3=sca.nextLine();
			}
			Joueur C=new Joueur(nom3,ia3);
			//Cr√©ation d'un plateau deux joueurs qui sera remplac√© si il y a plus de joueurs
			grille=new Plateau(A,B,C,0,obstacle,taille);
		}
		// Uniquement si il y a quatre joueurs
		if(a==4){
			// Cr√©ation du joueur 3
			ok=false;
			System.out.println("Voulez-vous que le troisi√®me joueur soit une IA ? Si non tapez '0', si oui tapez '1', '2','31', '32' ou '4' respectivement pour une pour une IA de niveau 1, 2, 3-1, 3-2 ou 4.");
			int ia3=scan.nextInt();
			while(!ok){
				if(ia3==0 | ia3==1 | ia3==2 | ia3==31 | ia3==32  | ia3==4){
					ok=true;
				}
				else{    
					System.out.println("Valeur incorrecte, r√©essayez :");
					ia3 = scan.nextInt();
				}
			}
			String nom3="IA 3";
			if(ia3==0){
				System.out.println("Entrez le nom du troisi√®me joueur :");
				Scanner sca = new Scanner(System.in);
				nom3=sca.nextLine();
			}
			Joueur C=new Joueur(nom3,ia3);
			// Cr√©ation du joueur 4
			ok=false;
			System.out.println("Voulez-vous que le troisi√®me joueur soit une IA ? Si non tapez '0', si oui tapez '1', '2','31', '32' ou '4' respectivement pour une pour une IA de niveau 1, 2, 3-1, 3-2 ou 4.");
			int ia4=scan.nextInt();
			while(!ok){
				if(ia4==0 | ia4==1 | ia4==2 | ia4==31 | ia4==32  | ia4==4){
					ok=true;
				}
				else{    
					System.out.println("Valeur incorrecte, r√©essayez :");
					ia4 = scan.nextInt();
				}
			}
			String nom4="IA 4";
			if(ia4==0){
				System.out.println("Entrez le nom du quatri√®me joueur :");
				Scanner sca = new Scanner(System.in);
				nom4=sca.nextLine();
			}
			Joueur D=new Joueur(nom4,ia4);
			//Cr√©ation d'un plateau quatre joueurs
			grille=new Plateau(A,B,C,D,0,obstacle,taille);
		}
		return grille;
	}
}
