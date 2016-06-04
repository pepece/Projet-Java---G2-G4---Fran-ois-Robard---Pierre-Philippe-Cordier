package menu;

/*
Cette classe contient quelques informations sur les jouers et possÃ¨de quelques fonction de base d'accÃ¨s Ã  ces informations

Elle contient aussi la logique relative à la troisième IA v1 et v2 qui accessoirement est bugger
*/

public class Joueur {
    
    private String nom; // Indique le nom du joueur en vue de son affichage graphique
    private int couleur,nb; //Couleur indique la couleur courante du joueur, nb indique son numÃ©ro d'ordre dans la partie
    private int isIA; // indique si le joueur est une IA (indique alors son niveau: 1, 2, 31 ou 32) ou non (0)

    //Constructeur
    public Joueur(String nom, int isIA){
        this.nom=nom;
        this.isIA=isIA;
    }
    
    //MÃ©thodes
    
    public int getCouleur(){
        return this.couleur;
    }
    
    public void setCouleur(int couleur){
        this.couleur=couleur;
    }
    
    public void setNb(int nb){
        this.nb=nb;
    }
    
    public int getNb(){
        return this.nb;
    }
    
    public String getNom(){
        return this.nom;
    }
    
    public int isIA(){
        return this.isIA;
    }
    
    public void setIsIA(int isIA){
        this.isIA = isIA;
    }
    
    /**
     * crée un tableau de  2,3 ou 4 cases avec la liste des joueurs
     * @param plateau
     * @return
     */
    public static Joueur[] tableJoueur(Plateau grille){
    	int nbJoueur = grille.getNbJoueur();
    	Joueur[] joueur = new Joueur[nbJoueur]; 	
    	joueur[0] = grille.getJoueur1();
    	joueur[1] = grille.getJoueur2();  	
    	switch(nbJoueur){
			case 3 :
				joueur[2] = grille.getJoueur3();
			break;
			case 4 :
				joueur[2] = grille.getJoueur3();
				joueur[3] = grille.getJoueur4();
	    	break;
	    	default :
	    	break;
    	}
    	return joueur;
    }
    
    /**
     * renvoie un tableau qui pour chaque joueur du tableau des joueurs met true si ia, false sinon.
     * @param joueur
     * @return
     */
    public static boolean[] tableIsIA( Joueur[] joueur){
    	int nbJoueur = joueur.length;
    	boolean[] IA = new boolean[nbJoueur];
    	for(int j = 0; j < nbJoueur; j++){
    		if (joueur[j].isIA() != 0){
    			IA[j] = true;
    		}else{
    			IA[j] = false;
    		}
    	}
    	return IA;
    }
    
    /**
     * Il se pourrait que cette IA soit bugger elle se permet notamment de triché, à debbugger
     * 
     * bugs: stop avec bleu/jaune, capture d'autre couleur, refuse de finir la partie(indigo)
     * 
     * Dans cette fonction j'utiliserais souvent le terme optimale ou opti cela correspond à la couleur rapportant
     * le plus de points pour un joueur sans prévisions des tours suivants  (coup jouer par l'IA 2)
     * 
     * par autre j'entendrais soit l'opti (v1) soit la couleur la plus susceptible d'être choisis par l'IA 3 à
     * l'instant t de la fonction couleurIA3v2() càd couleurAJouee dans la v2.
     * 
     * Le concept est assez simple faire une fonction qui compare le nombre de point perdu en jouant une couleur par 
     * rapport à autre avec le nombre de point que perd l'adversaire (cf pointPerduAdv()) si on joue cette couleur 
     * par rapport à si on joue autre. Tous les adversaires joue leur couleur opti. Il faut donc à chaque tour anticipé
     * crée un nouveaux Plateau.
     * 
     *   ici la version v1 de la fonction où autre est l'opti
     * @param grille
     * @return
     */
    public int couleurIA3v1 ( Plateau grille){
    	int nbJoueur = grille.getNbJoueur();
    	int[] couleursDispo = grille.couleursJouables();
    	int opti = grille.choixCouleurIANiveau2(this, couleursDispo);
    	Joueur[] joueur = this.ordreJoueur(grille);  //indice : 0 l'IA cad this, 1 celui qui joue après, 2 encore après (si nbJoueur >=3), 3 encore après...
    	int totalMax = -100;
    	int total = -100;
    	int couleurAJouee = 666;
    	for (int a : couleursDispo){ //on test pour toute les couleurs que l'IA pourrait jouer laquelle obtient le meilleur score
    		//ci dessous on crée les plateaux comme il vont être joué si l'IA choisis a ou Opti et que chaque joueur suivant à sont tour joue la couleur opti pour lui (grille.choixCouleurIANiveau2(joueur, couleursDispo);)
    		Plateau plateau2emeTourA = new Plateau( grille, a, joueur[0]); //plateau après que le premier joueur (IA) est joué si il a joué a
			Plateau plateau2emeTourOpti = new Plateau( grille, opti, joueur[0]); //plateau après que le premier joueur (IA) est joué si il a joué Opti
    		int soustraire = joueur[1].pointPerduAdv( plateau2emeTourOpti, plateau2emeTourA); //quantité de point perdus par les adversaire    		
			if(nbJoueur == 3){
				Plateau plateau3emeTourA = joueur[1].nvxPlateau(plateau2emeTourA); //plateau après que le deuxieme joueur est joué si le premier (IA) a joué a
				Plateau plateau3emeTourOpti = joueur[1].nvxPlateau(plateau2emeTourOpti);//plateau après que le deuxieme joueur est joué si si le premier (IA) a joué Opti
				soustraire += joueur[2].pointPerduAdv(plateau3emeTourOpti, plateau3emeTourA);
			}
			else if(nbJoueur == 4){
				Plateau plateau3emeTourA = joueur[1].nvxPlateau(plateau2emeTourA);
				Plateau plateau3emeTourOpti = joueur[1].nvxPlateau(plateau2emeTourOpti);
				Plateau plateau4emeTourA = joueur[2].nvxPlateau(plateau3emeTourA);
				Plateau plateau4emeTourOpti = joueur[2].nvxPlateau(plateau3emeTourOpti);
				soustraire += joueur[2].pointPerduAdv(plateau3emeTourOpti, plateau3emeTourA)+ joueur[3].pointPerduAdv(plateau4emeTourOpti, plateau4emeTourA);
			}
			
			//on calcul maintenant l'intéret d'un tel jeu plus le total est haut plus la couleur est intéressante
			total = joueur[0].pointPerdu( a, grille, opti) - ( soustraire  / nbJoueur );//attention ces deux nombre sont négatifs (valeurs algébriques)
			
			if(total > totalMax){ // si a est une meilleur proposition que l'ancienne on la remplace;
				totalMax = total;
				couleurAJouee = a;
			}
		}
    	return couleurAJouee;
    }
    
    /**
     * 
     * cf, la v1 : couleurIA3v1 ( Plateau grille)
     * 
     *  ici la version v2 de la fonction où autre est  la meilleur des solutions jusqu'à présent (en prenant pour première solution l'opti)
     * @param grille
     * @return
     */
    public int couleurIA3v2 ( Plateau grille){
    	int nbJoueur = grille.getNbJoueur();
    	int[] couleursDispo = grille.couleursJouables();
    	int opti = grille.choixCouleurIANiveau2(this, couleursDispo);
    	Joueur[] joueur = this.ordreJoueur(grille);  //indice : 0 l'IA cad this, 1 celui qui joue après, 2 encore après (si nbJoueur >=3), 3 encore après...
    	int totalMax = -100;
    	int total = -100;
    	int couleurAJouee = opti;
    	int nbRepetition = 2;
	    for (int qsd = 0; qsd < nbRepetition; qsd++){
    		for (int a : couleursDispo){ //on test pour toute les couleurs que l'IA pourrait jouer laquelle obtient le meilleur score (plrs fois)
	    		//ci dessous on crée les plateaux comme il vont être joué si l'IA choisis a ou couleurAJouee et que  chaque joueur suivant à sont tour joue la couleur opti pour lui (grille.choixCouleurIANiveau2(joueur, couleursDispo);)
    			Plateau plateau2emeTourA = new Plateau( grille, a, joueur[0]); //plateau après que le premier joueur (IA) est joué si il a joué a
    			Plateau plateau2emeTourAJouee = new Plateau( grille, couleurAJouee, joueur[0]); //plateau après que le premier joueur (IA) est joué si il a joué couleurAJouee
	    		int soustraire = joueur[1].pointPerduAdv( plateau2emeTourAJouee, plateau2emeTourA); //quantité de point perdus par les adversaire    		
				if(nbJoueur == 3){
					Plateau plateau3emeTourA = joueur[1].nvxPlateau(plateau2emeTourA); //plateau après que le deuxieme joueur est joué si le premier (IA) a joué a
					Plateau plateau3emeTourAJouee = joueur[1].nvxPlateau(plateau2emeTourAJouee);//plateau après que le deuxieme joueur est joué si si le premier (IA) a joué couleurAJouee
					soustraire += joueur[2].pointPerduAdv(plateau3emeTourAJouee, plateau3emeTourA);
				}
				else if(nbJoueur == 4){
					Plateau plateau3emeTourA = joueur[1].nvxPlateau(plateau2emeTourA);
					Plateau plateau3emeTourAJouee = joueur[1].nvxPlateau(plateau2emeTourAJouee);
					Plateau plateau4emeTourA = joueur[2].nvxPlateau(plateau3emeTourA);
					Plateau plateau4emeTourAJouee = joueur[2].nvxPlateau(plateau3emeTourAJouee);
					soustraire += joueur[2].pointPerduAdv(plateau3emeTourAJouee, plateau3emeTourA)+ joueur[3].pointPerduAdv(plateau4emeTourAJouee, plateau4emeTourA);
				}
				
				//on calcul maintenant l'intéret d'un tel jeu plus le total est haut plus la couleur est intéressante
				total = joueur[0].pointPerdu( a, grille, couleurAJouee) - ( soustraire  / nbJoueur );//attention ces deux nombre sont négatifs (valeurs algébriques)
				
				if(total > totalMax){ // si a est une meilleur proposition que l'ancienne on la remplace;
					totalMax = total;
					couleurAJouee = a;
				}
			}
	    }
	    return couleurAJouee;
    }
    
    /**
     * renvoie en 0 le joueur qui joue, en 1 le joueur d'après, en 2 d'encore après (si nbJoueur >=3), en 3 le dernier joueur (si nbJoueur >= 4 ) 
     * @param grille
     * @param nbJoueur
     * @return
     */
    public Joueur[] ordreJoueur(Plateau grille){
    	int nbJoueur = grille.getNbJoueur();
    	Joueur[] joueurTrie = new Joueur[nbJoueur];
    	Joueur[] joueur = tableJoueur(grille); //dresse la liste des joueurs 1puis 2puis 3 puis 4
    	
    	for (int i= 0; i<nbJoueur; i++){
    		if (((this.getNb()+i)%(nbJoueur))-1 >= 0){
    			joueurTrie[i] = joueur[((this.getNb()+i)%(nbJoueur))-1];    		
    		}
    		else{
    			joueurTrie[i] = joueur[(nbJoueur + (((this.getNb()+i)%(nbJoueur))-1))];
    		}
    	}
    	return joueurTrie;	
    }
    
    private  Plateau nvxPlateau( Plateau ancienneGrille){
    	int[] couleursDispo = ancienneGrille.couleursJouables();
    	int couleurOptiAdversaire = ancienneGrille.choixCouleurIANiveau2(this, couleursDispo); //c'est ici qu'il faut bidouillé si l'on veut anticipé autre chose que l'opti comme choix des joueurs suivants
    	return new Plateau( ancienneGrille, couleurOptiAdversaire, this);
    }
    
	/**
	 * renvoie le nombre de point que this (normalement l'IA qui joue) perd a joué "a" plutôt qu'a joué autre (ça couleur opti  si dans v1, couleurAJouee si dans v2)
	 * attention valeurs algébrique donc forcément négative (possiblement 0) si autre = opti dans v1;
	 * @param a
	 * @param grille
	 * @return
	 */
    private int pointPerdu(int a, Plateau grille, int autre){
    	return  ( grille.comptePointsHypothetiquesJoueurParCouleur(this, a) - 
    			grille.comptePointsHypothetiquesJoueurParCouleur(this, autre) );
    }
    
    /**
     * 
     *  nombre de point perdu par l'adversaire en jouant son opti quand l'IA joue Autre plutot que a  cad :( point qu'il aurait eu si l'IA joue Autre(couleurAJouee dans v2 ou Opti dans v1) - point qu'il a si l'IA joue a) 
     *  Donc si Autre est mieux pour lui le return est positif donc in fine le total diminue (à cause de la soustraction)
     * @param GrilleAutre
     * @param GrilleA
     * @return
     */
    private int pointPerduAdv(Plateau grilleAutre, Plateau grilleA){
    	int[] couleursDispoAutre = grilleAutre.couleursJouables();
    	int couleurOptiAdvAutre = grilleAutre.choixCouleurIANiveau2(this, couleursDispoAutre);
    	int[] couleursDispoA = grilleA.couleursJouables();
    	int couleurOptiAdvA = grilleA.choixCouleurIANiveau2(this, couleursDispoA);
    	int pointAutre = grilleAutre.comptePointsHypothetiquesJoueurParCouleur(this, couleurOptiAdvAutre);
    	int pointA = grilleA.comptePointsHypothetiquesJoueurParCouleur(this, couleurOptiAdvA);
    		
    	return (pointAutre - pointA);
    }
}