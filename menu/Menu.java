package menu;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Scanner;

//JFrame new Jframe dans charger partie
//bien afficher les couleur jouables pour éviter d'etre bloque en fin de partie
//3 joueurs changer le final
//fct qui créer une grille
//opti changement de couleur

/**
 * ceci est la main pour la version menu graphique et affichage graphique bugger
 */

public class Menu {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		menu();
		//jeu();			
	}

	/**
	 * permet de créer/recréer(initialiser) la Fenetre qui affichera la grille pendant le jeu,
	 *  organise les différents layout de la fentre de jeu; puis le mouselistener / paint
	 * @param rayon
	 * @param Xdepart
	 * @param Ydepart
	 * @param grille2
	 * @param piFenetre
	 */
	public static void iniSwing(int rayon, int Xdepart, int Ydepart, Plateau grille2, Enregistreur piFenetre){
		/*
		 * si tentative sans le menu la fenetre n'existe pas encore:
		 */
		//Fenetre fenetre = new Fenetre(true);
		//piFenetre.setFenetre(fenetre);
		//JPanel tout = new JPanel();

		/*
		 * sinon :
		 */

		piFenetre.getFenetre().getContentPane().removeAll();
		MenuListener tout = new MenuListener(piFenetre.getFenetre(), piFenetre);
		PieceImage PI = new PieceImage( piFenetre.getFenetre(), rayon, Xdepart, Ydepart , grille2);
		tout.add(PI);
		JMenuBar menuBar;
		JMenu option;
		JMenuItem menuItem;
		//Créer la barre du menu
		menuBar = new JMenuBar();
		//crée le menu
		option = new JMenu("Option");
		//crée l'option "sauvegarder
		menuItem = new JMenuItem("Sauvegarder");
		//création du raccourci ctrl+s
		KeyStroke controleS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
		menuItem.setAccelerator(controleS);
		//association à l'action lostener PI
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
		//pour laissé une petite marge à droite
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
	 * appelé uniquement lors des parties en mode console
	 * fait joué les joueur et IA tour à tour jusqu'à la fin de la partie (getFin() == true)
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
	 * fait joué les joueur et IA tour à tour jusqu'à la fin de la partie (getFin() == true)
	 * appelé uniquement lors des parties en en mode graphique (meme si affiche aussi sur la console)
	 * les trois variables essentiel à comprendre dans cette fonction sont jouant, aJoue, jouantIsIA contenue dans le Plateau grille
	 * jusqu'à ce que la partie soit fini, tant qu'un joueur (IA ou non) n'a pas encore joué soit on attend d'avoir un événement depuis la fct
	 * mousePressed de la classe PieceImage soit on fait joué l'IA
	 * @param grille
	 * @param piFenetre
	 */
	public static void deroulementPartieGraphique(Plateau grille, Enregistreur piFenetre, int jouant){
		System.out.println("deroul graphique");
		int nbJoueur = grille.getNbJoueur();
		Joueur[] joueur = Joueur.tableJoueur(grille);
		boolean[] isIA = Joueur.tableIsIA(joueur);
		//pour des raison de porté j'ai du mettre les trois variables utilisé ci dessous dans Plateau jouant, aJoue, jouantIsIA
		grille.setJouant(jouant); //numéro de joueur en train de jouer dans la boucle
		grille.setAJoue(false);  //le joueur n'a pas encore joué
		grille.setJouantIsIA(isIA[grille.getJouant()]); //est'il une IA (faut-il prendre en compte les inputs souris du joueur, faut-il lancé un joueGraphique()


		/**************************************************************
		 * c'est ici qu'intervient le bug quand cette boucle n'est pas en commentaire (et que les passages à adapter le sont) 
		 * le jeu bug dans iniSwing avant même que déroulement partie graphique soit appelé quand bien même si elle
		 */


		/*while(!grille.getFin()){// jusqu'à la fin
			System.out.println("grande boucle");
			while( grille.getAJoue() == false) {  //tant que le coup n'a pas été joué
				System.out.println("pb");
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
			System.out.println("jouant");
			System.out.println(grille.getJouant());
			System.out.println("nbjoueur");
			System.out.println(grille.getNbJoueur());
			//passe au joueur d'après
			grille.setJouant( (grille.getJouant()+1) );
            if (grille.getJouant() >= nbJoueur){
            	grille.setJouant(0);
            }
            grille.setAJoue(false); 
    		grille.setJouantIsIA(isIA[grille.getJouant()]);
    		Container cont = piFenetre.getFenetre().getContentPane();
    		System.out.println("cont");
    		System.out.println(cont);

    		piFenetre.getFenetre().revalidate();
    		piFenetre.getFenetre().repaint();
        }*/
	}

	
	/**
	 * fonctiion contenant l'initialisation avec la console dans le cas où on n'essaye pas d'utiliser le menu
	 */
	
	/*public static void jeu(){
		System.out.println("Bienvenue dans le jeu des 6 couleurs.");
		System.out.println("Tapez 1 pour la version minimale du jeu dans la console.");
		System.out.println("Tapez 2 pour la version graphique.");
		System.out.println("Tapez 3 pour tester la fonction de sauvegarde.");
		System.out.println("Tapez 4 pour reprendre une partie enregistrÃ©e.");
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
			case 4:
				//ouverture du jeu depuis un fichier
				// chargementSauvegarde();
				ok=true;
				break;

			default:
				System.out.println("Vous n'avez pas entrÃƒÂ© une valeur correcte, rÃƒÂ©essayez");
				a = scan.nextInt();
			}
		}
	}*/

	
	/**
	 * à partir d'ici relatif au menu graphique, absent de main.java
	 */


	/**
	 * créer la page du menu permettant de personnalisée une partie/ tout les inputs sont gérer dans MenuListener et sauvegarder à l'aide d'Enregistreur
	 * @param piFenetre
	 * @return
	 */
	public static void menu3( Enregistreur piFenetre){
		piFenetre.getFenetre().getContentPane().removeAll();
		MenuListener tout = new MenuListener(piFenetre.getFenetre(), piFenetre);
		
		/*
		 * on découpe le centre en 7 étages,
		 * le slider, les 4 joueurs, obstacle et lancer partie
		 */
		JPanel centre = new JPanel();
		JPanel[] C = new JPanel[7];
		int espacement = 35; //entre les différents étages
		centre.add(Box.createRigidArea(new Dimension(0, espacement))); // créer une boite vide pour former l'espacement
		for( int i = 0; i<7; i++){ //initialise les 7 étages
			C[i] = new JPanel();
			C[i].setLayout(new BoxLayout(C[i], BoxLayout.LINE_AXIS));
			C[i].setBackground(Color.YELLOW);
			centre.add(C[i]);
			centre.add(Box.createRigidArea(new Dimension(0, espacement)));
		}
		JLabel tailleL = new JLabel("taille");
		JLabel obstacleL = new JLabel("Obstacle");
		JButton OK = new JButton("lancer la partie");
		OK.setBackground(Color.YELLOW);
		OK.addActionListener(tout);
		tout.getBoutonMenu()[4]= OK;
		JTextField[] joueursTF = new JTextField[4];
		JSlider tailleS = new JSlider(9, 50, 20);
		piFenetre.setTailleS(tailleS);
		tailleS.setBackground(Color.YELLOW);
		tailleS.addChangeListener(tout);
		tailleS.setMinorTickSpacing(1); 
		tailleS.setMajorTickSpacing(3);
		tailleS.setPaintTicks(true);
		tailleS.setPaintLabels(true);
		C[0].add(tailleL);
		C[0].add(tailleS);
		C[5].add(obstacleL);
		C[6].add(OK);
		ButtonGroup ob = new ButtonGroup();
		JRadioButton oui = new JRadioButton("oui");
		ob.add(oui);
		oui.addActionListener(tout);
		oui.setBackground(Color.YELLOW);
		tout.getBoutonMenu()[29]= oui;
		C[5].add(oui);
		oui.setSelected(true);
		JRadioButton non = new JRadioButton("non");
		ob.add(non);
		non.setBackground(Color.YELLOW);
		non.addActionListener(tout);
		tout.getBoutonMenu()[30]= non;
		C[5].add(non);
		int espJoueur = 200;
		for( int i = 0; i<4; i++){ // créer les étages des 4 joueurs
			C[i+1].add(Box.createRigidArea(new Dimension(espJoueur, 0)));
			joueursTF[i]= new JTextField("joueur " + (i+1));
			joueursTF[i].requestFocus();
			piFenetre.getJoueursNoms()[i] = joueursTF[i];
			joueursTF[i].setSize(new Dimension( 50, 10));//useless?
			C[i+1].add(joueursTF[i]);

			ButtonGroup bG = new ButtonGroup(); // groupe de bouton contenant aucun, humain, et les 4 IA
			if (i>1){
				JRadioButton aucun = new JRadioButton("aucun");
				bG.add(aucun);
				aucun.addActionListener(tout);
				tout.getBoutonMenu()[4+i*6+1]= aucun;//4+i*6+1   4 car on a déja attribué 5 bouton, i*6 car à chaque i on ajoute 6 bouton, +1 car aucun est le premier bouton 
				C[i+1].add(aucun);
				aucun.setBackground(Color.YELLOW);
			}else{
				tout.getBoutonMenu()[4+i*6+1]= null;
			}
			JRadioButton humain = new JRadioButton("humain");
			humain.setBackground(Color.YELLOW);
			bG.add(humain);
			humain.addActionListener(tout);
			tout.getBoutonMenu()[4+i*6+2]= humain;
			C[i+1].add(humain);
			humain.setSelected(true);
			JRadioButton IA1 = new JRadioButton("IA1");
			bG.add(IA1);
			IA1.setBackground(Color.YELLOW);
			IA1.addActionListener(tout);
			tout.getBoutonMenu()[4+i*6+3]= IA1;
			C[i+1].add(IA1);
			JRadioButton IA2 = new JRadioButton("IA2");
			bG.add(IA2);
			IA2.addActionListener(tout);
			IA2.setBackground(Color.YELLOW);
			tout.getBoutonMenu()[4+i*6+4]= IA2;
			C[i+1].add(IA2);
			JRadioButton IA32 = new JRadioButton("IA32(bugger)");
			bG.add(IA32);
			IA32.addActionListener(tout);
			IA32.setBackground(Color.YELLOW);
			tout.getBoutonMenu()[4+i*6+5]= IA32;
			C[i+1].add(IA32);
			JRadioButton IA4 = new JRadioButton("IA4");
			bG.add(IA4);
			IA4.setBackground(Color.YELLOW);
			IA4.addActionListener(tout);
			tout.getBoutonMenu()[4+i*6+6]= IA4;
			C[i+1].add(IA4);
			C[i+1].add(Box.createRigidArea(new Dimension(espJoueur, 0)));
		}

		tout.setLayout(new BoxLayout(tout, BoxLayout.LINE_AXIS));
		centre.setLayout(new BoxLayout(centre, BoxLayout.PAGE_AXIS));

		Color bg = new Color( 0, 0 , 128);
		centre.setBackground(bg);
		tout.setBackground(bg);

		tout.add(centre);
		piFenetre.getFenetre().getContentPane().add(tout);
		piFenetre.getFenetre().revalidate();
		piFenetre.getFenetre().repaint();
	}

	/**
	 * choix entre partie rapide et personnalisé
	 * @param piFenetre
	 */
	public static void menu2( Enregistreur piFenetre){
		piFenetre.getFenetre().getContentPane().removeAll();
		MenuListener tout = new MenuListener(piFenetre.getFenetre(), piFenetre);
		tout.setLayout(new BorderLayout());
		JPanel centre = new JPanel();
		centre.setLayout(new BoxLayout(centre, BoxLayout.PAGE_AXIS));
		JButton PR = new JButton("partie rapide");
		JButton PP = new JButton("partie personnalisée");
		PR.addActionListener(tout);
		PP.addActionListener(tout);
		tout.getBoutonMenu()[2]=PR;
		tout.getBoutonMenu()[3]=PP;


		Font titleFont = new Font("CASTELLAR", Font.PLAIN, 40);
		Font nPR = new Font("", Font.PLAIN, 30);
		Font cPP = new Font("", Font.PLAIN, 30);
		Border bord = BorderFactory.createLineBorder(Color.BLACK,2);
		PR.setFont(nPR);
		PP.setFont(cPP);
		PP.setBorder(bord);
		PR.setBorder(bord);
		PP.setBackground(Color.YELLOW);
		PR.setBackground(Color.YELLOW);
		PP.setForeground(Color.RED);
		PR.setForeground(Color.RED);
		Color bg = new Color( 0, 0 , 128);
		centre.setBackground(bg);
		tout.setBackground(bg);
		centre.add(PR);
		centre.add(Box.createRigidArea(new Dimension(0, 17)));
		centre.add(PP);
		tout.setLayout(new BoxLayout(tout, BoxLayout.LINE_AXIS));
		tout.add(Box.createRigidArea(new Dimension(110, 0))); //bricolage
		tout.add(centre);
		piFenetre.getFenetre().getContentPane().add(tout);
		piFenetre.getFenetre().revalidate();
		piFenetre.getFenetre().repaint();

	}

	/*
	 * choix entre nouvelle partie ou charger
	 */
	public static void menu(){
		Fenetre menu = new Fenetre(true);
		Enregistreur piFenetre = new Enregistreur();
		piFenetre.setFenetre(menu);
		MenuListener tout = new MenuListener(menu, piFenetre);
		tout.setLayout(new BorderLayout());
		JPanel centre = new JPanel();
		centre.setLayout(new BoxLayout(centre, BoxLayout.PAGE_AXIS));
		//image ne fonctionne pas
		//ImageIcon imageTitre = new ImageIcon("C:\\Users\\Pierre-Philippe\\workspace\\projetA1S2\\src\\projetA1S2\6couleurs.jpg");
		//System.out.println(imageTitre);
		//titre.setIcon(imageTitre);
		JLabel titre = new JLabel("Jeu des 6 Couleurs");
		JButton nP = new JButton("nouvelle partie");
		JButton cP = new JButton("charger");
		nP.addActionListener(tout);
		cP.addActionListener(tout);
		tout.getBoutonMenu()[0]=nP;
		tout.getBoutonMenu()[1]=cP;
		Font titleFont = new Font("CASTELLAR", Font.PLAIN, 40);
		Font nPF = new Font("", Font.PLAIN, 30);
		Font cPF = new Font("", Font.PLAIN, 30);
		Border bord = BorderFactory.createLineBorder(Color.BLACK,2);
		titre.setFont(titleFont);
		titre.setForeground(Color.RED);
		nP.setFont(nPF);
		cP.setFont(cPF);
		nP.setBorder(bord);
		cP.setBorder(bord);
		nP.setBackground(Color.YELLOW);
		cP.setBackground(Color.YELLOW);
		nP.setForeground(Color.RED);
		cP.setForeground(Color.RED);
		Color bg = new Color( 0, 0 , 128);
		centre.setBackground(bg);
		tout.setBackground(bg);
		centre.add(Box.createRigidArea(new Dimension(0, 100)));
		centre.add(titre);
		centre.add(Box.createRigidArea(new Dimension(0, 40)));
		centre.add(nP);
		centre.add(Box.createRigidArea(new Dimension(0, 17)));
		centre.add(cP);
		tout.setLayout(new BoxLayout(tout, BoxLayout.LINE_AXIS));
		tout.add(Box.createRigidArea(new Dimension(110, 0))); //bricolage
		tout.add(centre);
		menu.getContentPane().add(tout);
		menu.validate();

	}

}
