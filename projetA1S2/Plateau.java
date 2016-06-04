package projetA1S2;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

/*
Cette classe contient les information liee au plateau de jeu et donc necessaires a son fonctionnement
C'est dans cette classe que se trouvent la plupart des fonctions du jeu car elle necessitent souvent un acces
facile et rapide a l'ensemble des informations sur l'etat du plateau de jeu et sur l'etat du jeu lui meme.
*/

public class Plateau {
    private int nbJoueur;
    private Joueur joueur1,joueur2,joueur3,joueur4;
    private int typeTableau, taille;
    private int[][] cases, casesControle;
    private boolean fini;
    private boolean obstacle;
    private boolean aJoue; //utilise dans Main.deroulementPartieGraphique() pour savoir si le joueur a deja joue
    private boolean jouantIsIA; //utiliser dans Main.deroulementPartieGraphique() pour savoir si le joueur est une IA ou un joueur lambda
    private int jouant; //numero du joueur en train de joue
    private boolean veutEnregistrer;
    
    /*Constructeurs :
    Les trois fonctions suivantes permettent d'initialiser un plateau a partir de ses joueurs
    Elles construisents un plateau constituee de cases hexagonales remplie avec les differentes couleurs de maniere aleatoire
    (informations sur les couleurs contenues dans le tableau a  2 dimentions 'cases')
    Le premier joueur est plaece en haut a gauche, le 2e en bas a droite
    S'ils sont presents le 3e joueur est en haut a gauche et le 4e en bas a  droite
    Le tableau casesControle de base est cree indiquant les emplacement des differents joueurs.
    Ces fonctions font en sorte que chaque joueur ait bien une couleur differente de celle des autres
    Elles verifient que chaque joueur n'a pas plusieurs cases au depart: tant que les cases adjacentes a la case de base d'un joueur
    sont de la meme couleur que celle-ci on en rechoisit une autre au hasard.
    Si on a autorise les obstacles, la couleur des cases est choisie entre 0 et 6 sachant que 6 est un obstacle (gris)
    Dans ce cas le constrcteur verifie qu'un joueur ne possede pas un obstacle au depart, et si c'est le cs il change sa couleur
    /*
    
    
    /**
    * cree une grille qui est le tour suivant ancienneGrille ou le Joueur joueur joue la couleur couleurJouee.
    *
    * @param ancienneGrille
    * @param couleurJouer
    * @param joueur
    * @param nbJoueur
    */
    public Plateau(Plateau ancienneGrille2, int couleurJouee, Joueur joueur){
        this.nbJoueur = ancienneGrille2.nbJoueur;
        this.joueur1 = ancienneGrille2.joueur1;
        this.joueur2 = ancienneGrille2.joueur2;
        if (nbJoueur >= 3){
            this.joueur3 = ancienneGrille2.joueur3;
        }
        if (nbJoueur == 4){
            this.joueur4 = ancienneGrille2.joueur4;
        }
        this.taille = ancienneGrille2.taille;
        this.typeTableau = ancienneGrille2.typeTableau;
        this.fini = ancienneGrille2.fini;
        this.obstacle = ancienneGrille2.obstacle;
        
        this.cases = new int[this.taille][this.taille];
        this.casesControle = new int[this.taille][this.taille];
        for(int i= 0; i< this.taille; i++){
            for(int j= 0; j< this.taille; j++){
                this.cases[i][j] = ancienneGrille2.cases[i][j];
                this.casesControle[i][j] = ancienneGrille2.casesControle[i][j];
            }
        }
        //c'est maintenant une copie de l'ancien sauf que cases et cases controle ne sont pas les pointeur de l'ancienne grille mais une copie des tableaux correspondant element par element
        //ils'agit maintenant de passer au tour suivant
        this.changementCouleur(joueur, couleurJouee);
        this.verifControle(joueur);
        this.regleAvancee();
    }
    
    
    public Plateau(Joueur joueur1, Joueur joueur2,int typeTableau,boolean obstacle, int taille){
        //Creation d'un plateau pour deux joueurs
        this.obstacle=obstacle;
        this.joueur1=joueur1;
        this.joueur1.setNb(1);
        this.joueur2=joueur2;
        this.joueur2.setNb(2);
        this.nbJoueur=2;
        this.veutEnregistrer=false;
        this.typeTableau=typeTableau;
        this.taille=taille;
        int[][] Controle=new int[this.taille][this.taille];
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                Controle[i][j]=0;
            }
        }
        Controle[0][0]=1;
        Controle[this.taille-1][this.taille-1]=2;
        this.casesControle=Controle;
        int[][] caz=new int[this.taille][this.taille];
        Random rand=new Random();
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(this.obstacle){
                    caz[i][j]=rand.nextInt(7);
                }
                else{
                    caz[i][j]=rand.nextInt(6);
                }
            }
        }
        //Il faut verifier qu'un joueur n'a pas une couleur obstacle
        if(caz[0][0]==6){
            caz[0][0]=rand.nextInt(6);
        }
        this.joueur1.setCouleur(caz[0][0]);
        while(caz[this.taille-1][this.taille-1]==caz[0][0] | caz[this.taille-1][this.taille-1]==6){
            caz[this.taille-1][this.taille-1]=rand.nextInt(6);
        }
        this.joueur2.setCouleur(caz[this.taille-1][this.taille-1]);
        //Il faut maintenant faire en sorte que chaque joueur n'aie qu'une seule case en debut de partie, donc que les cases
        // autour de sa case de depart ne soient pas de la meme couleur que celle-ci
        // Il faut aussi que le joueur ne soit pas bloque par des obstacles des le depart
        while(caz[0][1]==caz[0][0] | caz[0][1]==6){
            caz[0][1]=rand.nextInt(6);
        }
        while(caz[1][0]==caz[0][0] | caz[1][0]==6){
            caz[1][0]=rand.nextInt(6);
        }
        while(caz[this.taille-2][this.taille-1]==caz[this.taille-1][this.taille-1] | caz[this.taille-2][this.taille-1]==6){
            caz[this.taille-2][this.taille-1]=rand.nextInt(6);
        }
        while(caz[this.taille-1][this.taille-2]==caz[this.taille-1][this.taille-1] | caz[this.taille-1][this.taille-2]==6){
            caz[this.taille-1][this.taille-2]=rand.nextInt(6);
        }
        if(this.taille%2==0){// On verifie cette case seulement si le numero de ligne est pair
            while(caz[this.taille-2][this.taille-2]==caz[this.taille-1][this.taille-1] | caz[this.taille-2][this.taille-2]==6){
                caz[this.taille-2][this.taille-2]=rand.nextInt(6);
            }
        }
        this.cases=caz;
    }
    
    
    public Plateau(Joueur joueur1, Joueur joueur2,Joueur joueur3, int typeTableau,boolean obstacle, int taille){
        this.veutEnregistrer=false;
        //Creation d'un plateau pour trois joueurs
        this.obstacle=obstacle;
        this.joueur1=joueur1;
        this.joueur1.setNb(1);
        this.joueur2=joueur2;
        this.joueur2.setNb(2);
        this.joueur3=joueur3;
        this.joueur3.setNb(3);
        this.nbJoueur=3;
        this.typeTableau=typeTableau;
        this.taille=taille;
        int[][] Controle=new int[this.taille][this.taille];
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                Controle[i][j]=0;
            }
        }
        Controle[0][0]=1;
        Controle[this.taille-1][this.taille-1]=2;
        Controle[0][this.taille-1]=3;// Le joueur 3 commence en haut a  droite
        Controle[this.taille-1][0]=3;
        this.casesControle=Controle;
        int[][] caz=new int[this.taille][this.taille];
        Random rand=new Random();
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(this.obstacle){
                    caz[i][j]=rand.nextInt(7);
                }
                else{
                    caz[i][j]=rand.nextInt(6);
                }
            }
        }//Il faut verifier qu'un joueur n'a pas une couleur obstacle
        while(caz[0][0]==6){
            caz[0][0]=rand.nextInt(6);
        }
        this.joueur1.setCouleur(caz[0][0]);
        while(caz[this.taille-1][this.taille-1]==caz[0][0] | caz[this.taille-1][this.taille-1]==6){
            caz[this.taille-1][this.taille-1]=rand.nextInt(6);
        }
        this.joueur2.setCouleur(caz[this.taille-1][this.taille-1]);
        while(caz[0][this.taille-1]==caz[0][0] | caz[0][this.taille-1]==caz[this.taille-1][this.taille-1] | caz[0][this.taille-1]==6){
            caz[0][this.taille-1]=rand.nextInt(6);
        }
        this.joueur3.setCouleur(caz[0][this.taille-1]);
        //Il faut maintenant faire en sorte que chaque joueur n'aie qu'une seule case en debut de partie, donc que les cases
        // autour de sa case de depart ne soient pas de la meme couleur que celle-ci, et ne soient pas non plus des obstacles
        while(caz[0][1]==caz[0][0] | caz[0][1]==6){
            caz[0][1]=rand.nextInt(6);
        }
        while(caz[1][0]==caz[0][0] | caz[1][0]==6){
            caz[1][0]=rand.nextInt(6);
        }
        while(caz[this.taille-2][this.taille-1]==caz[this.taille-1][this.taille-1] | caz[this.taille-2][this.taille-1]==6){
            caz[this.taille-2][this.taille-1]=rand.nextInt(6);
        }
        while(caz[this.taille-1][this.taille-2]==caz[this.taille-1][this.taille-1] | caz[this.taille-1][this.taille-2]==6){
            caz[this.taille-1][this.taille-2]=rand.nextInt(6);
        }
        while(caz[this.taille-2][this.taille-2]==caz[this.taille-1][this.taille-1] | caz[this.taille-2][this.taille-2]==6){
            caz[this.taille-2][this.taille-2]=rand.nextInt(6);
        }
        while(caz[0][this.taille-2]==caz[0][this.taille-1] | caz[0][this.taille-2]==6){
            caz[0][this.taille-2]=rand.nextInt(6);
        }
        while(caz[1][this.taille-1]==caz[0][this.taille-1] | caz[1][this.taille-1]==6){
            caz[1][this.taille-1]=rand.nextInt(6);
        }
        while(caz[1][this.taille-2]==caz[0][this.taille-1] | caz[1][this.taille-2]==6){
            caz[1][this.taille-2]=rand.nextInt(6);
        }
        // On rajoute une autre case au joueur 3 de maniere a  ce qu'il ne soit pas trop desequilibre par sa position entre deux joueurs
        caz[this.taille-1][0]=caz[0][this.taille-1];
        
        this.cases=caz;
    }
    
    
    public Plateau(Joueur joueur1, Joueur joueur2,Joueur joueur3,Joueur joueur4,int typeTableau,boolean obstacle, int taille){
        //Creation d'un plateau pour quatre joueurs
        this.obstacle=obstacle;
        this.joueur1=joueur1;
        this.joueur1.setNb(1);
        this.joueur2=joueur2;
        this.joueur2.setNb(2);
        this.joueur3=joueur3;
        this.joueur3.setNb(3);
        this.joueur4=joueur4;
        this.joueur4.setNb(4);
        this.veutEnregistrer=false;
        this.nbJoueur=4;
        this.typeTableau=typeTableau;
        this.taille=taille;
        int[][] Controle=new int[this.taille][this.taille];
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                Controle[i][j]=0;
            }
        }
        Controle[0][0]=1;
        Controle[this.taille-1][this.taille-1]=2;
        Controle[0][this.taille-1]=3;// Le joueur 3 commence en haut a  droite
        Controle[this.taille-1][0]=4;// Le joueur 4 commence en bas a  gauche
        this.casesControle=Controle;
        int[][] caz=new int[this.taille][this.taille];
        Random rand=new Random();
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(this.obstacle){
                    caz[i][j]=rand.nextInt(7);
                }
                else{
                    caz[i][j]=rand.nextInt(6);
                }
            }
        }
        while(caz[0][0]==6){
            caz[0][0]=rand.nextInt(6);
        }
        this.joueur1.setCouleur(caz[0][0]);
        while(caz[this.taille-1][this.taille-1]==caz[0][0] | caz[this.taille-1][this.taille-1]==6){
            caz[this.taille-1][this.taille-1]=rand.nextInt(6);
        }
        this.joueur2.setCouleur(caz[this.taille-1][this.taille-1]);
        while(caz[0][this.taille-1]==caz[0][0] | caz[0][this.taille-1]==caz[this.taille-1][this.taille-1] | caz[0][this.taille-1]==6){
            caz[0][this.taille-1]=rand.nextInt(6);
        }
        this.joueur3.setCouleur(caz[0][this.taille-1]);
        while(caz[this.taille-1][0]==caz[0][0] | caz[this.taille-1][0]==caz[this.taille-1][this.taille-1] | caz[this.taille-1][0]==caz[0][this.taille-1] | caz[this.taille-1][0]==6){
            caz[this.taille-1][0]=rand.nextInt(6);
        }
        this.joueur4.setCouleur(caz[this.taille-1][0]);
        //Il faut maintenant faire en sorte que chaque joueur n'aie qu'une seule case en debut de partie, donc que les cases
        // autour de sa case de depart ne soient pas de la meme couleur que celle-ci
        while(caz[0][1]==caz[0][0] | caz[0][1]==6){
            caz[0][1]=rand.nextInt(6);
        }
        while(caz[1][0]==caz[0][0] | caz[1][0]==6){
            caz[1][0]=rand.nextInt(6);
        }
        while(caz[this.taille-2][this.taille-1]==caz[this.taille-1][this.taille-1] | caz[this.taille-2][this.taille-1]==6){
            caz[this.taille-2][this.taille-1]=rand.nextInt(6);
        }
        while(caz[this.taille-1][this.taille-2]==caz[this.taille-1][this.taille-1] | caz[this.taille-1][this.taille-2]==6){
            caz[this.taille-1][this.taille-2]=rand.nextInt(6);
        }
        while(caz[this.taille-2][this.taille-2]==caz[this.taille-1][this.taille-1] | caz[this.taille-2][this.taille-2]==6){
            caz[this.taille-2][this.taille-2]=rand.nextInt(6);
        }
        while(caz[0][this.taille-2]==caz[0][this.taille-1] | caz[0][this.taille-2]==6){
            caz[0][this.taille-2]=rand.nextInt(6);
        }
        while(caz[1][this.taille-1]==caz[0][this.taille-1] | caz[1][this.taille-1]==6){
            caz[1][this.taille-1]=rand.nextInt(6);
        }
        while(caz[1][this.taille-2]==caz[0][this.taille-1] | caz[1][this.taille-2]==6){
            caz[1][this.taille-2]=rand.nextInt(6);
        }
        while(caz[this.taille-2][0]==caz[this.taille-1][0] | caz[this.taille-2][0]==6){
            caz[this.taille-2][0]=rand.nextInt(6);
        }
        while(caz[this.taille-1][1]==caz[this.taille-1][0] | caz[this.taille-1][1]==6){
            caz[this.taille-1][1]=rand.nextInt(6);
        }
        this.cases=caz;
    }
    
    
    public Plateau(int nbJoueurs,int typeTableau, int taille, boolean fini,boolean obstacle,int jouant,int[][]cases,int[][] casesControle,Joueur[] joueurs){
        this.veutEnregistrer=false;
        this.nbJoueur=nbJoueurs;
        this.taille=taille;
        this.fini=fini;
        this.obstacle=obstacle;
        this.jouant=jouant;
        this.cases=cases;
        this.casesControle=casesControle;
        this.joueur1=joueurs[0];
        this.joueur1.setNb(1);
        this.joueur2=joueurs[1];
        this.joueur2.setNb(2);
        if(nbJoueurs==3 | nbJoueurs==4){
            this.joueur3=joueurs[2];
            this.joueur3.setNb(3);
        }
        if(nbJoueurs==4){
            this.joueur3=joueurs[3];
            this.joueur4.setNb(4);
        }
    }
    
    // Methodes :
    
    // On definit quelques fonctions de bases permettant l'acces  certaines donnees du jeu ou leur definition/modification
    // Elles sont tres courtes et explicites.
    public boolean getFin(){
        return this.fini;
    }
    
    public void setFin(boolean fin){
        this.fini=fin;
    }
    
    public Joueur getJoueur1(){
        return joueur1;
    }
    
    public Joueur getJoueur2(){
        return joueur2;
    }
    
    public Joueur getJoueur3(){
        return joueur3;
    }
    
    public Joueur getJoueur4(){
        return joueur4;
    }
    
    /**
     * retourne le joueur en fonction de son numero
     * @return
     */
    public Joueur getJoueurv2(int numero){
        switch(numero){
            case 1:
                return this.joueur1;
            case 2:
                return this.joueur2;
            case 3:
                return this.joueur3;
            case 4:
                return this.joueur4;
            default :
                return null;
        }
    }
    
    public int getNbJoueur(){
        return this.nbJoueur;
    }
    
    public boolean getObstacle() {
        return this.obstacle;
    }
    
    public int[][] getCases(){
        return this.cases;
    }
    
    public int[][] getCasesControle(){
        return this.casesControle;
    }
    
    public boolean getVeutEnregistrer(){
        return this.veutEnregistrer;
    }
    
    public void setVeutEnregistrer(boolean a) {
        this.veutEnregistrer = a;
    }
    
    public int getTaille() {
        return this.taille;
    }
    
    public boolean getAJoue(){
        return this.aJoue;
    }
    
    public void setAJoue(boolean a) {
        this.aJoue = a;
    }
    
    public boolean getJouantIsIA(){
        return this.jouantIsIA;
    }
    
    public void setJouantIsIA(boolean a) {
        this.jouantIsIA = a;
    }
    
    /**
     * retourne l'int qui correspond au joueur en train de jouer (ex :  joueur1 donne 0 )
     * @return
     */
    public int getJouant(){
        return this.jouant;
    }
    
    /**
     * retourne le joueur en train de joue !!! attention valeur pour un tableau, commence a 0 !!!
     * @return
     */
    public Joueur getJouantv2(){
        switch(this.jouant){
            case 0:
                return this.joueur1;
            case 1:
                return this.joueur2;
            case 2:
                return this.joueur3;
            case 3:
                return this.joueur4;
            default :
                return null;
        }
    }
    
    public void setJouant(int a) {
        this.jouant = a;
    }
    
    public int getTypeTableau() {
        return this.typeTableau;
    }
    
    //On passe maintenant aux fonctions principales du fonctionnement du jeu :
    
    /*
    Affiche le tableau dans la console quand le jeu est lance en mode console
    Affiche dans la console l'etat du plateau a cases hexagonal (lignes impaires decalees vers la droite) a l'aide de lettres
    La couleur de chaque case est lue dans le tableau 'cases'
    On sait si une case est controlee ou non grace au tableau 'casesControle'
    La lettre affichee correspond a l'intitiale de la couleur, en minuscule si la case n'est pas controlee, en majuscule sinon  
    */
    public void affichageConsole(){
        System.out.println("");
        for(int i=0;i<this.taille;i++){
            if(i%2==1){
                System.out.print(" ");
            }
            for(int j=0;j<this.taille;j++){
                switch(cases[i][j]){
                    case 0:
                        if(casesControle[i][j]==0){
                            System.out.print("r  ");
                        }
                        else{
                            System.out.print("R  ");
                        }
                        break;
                    case 1:
                        if(casesControle[i][j]==0){
                            System.out.print("o  ");
                        }
                        else{
                            System.out.print("O  ");
                        }
                        break;
                    case 2:
                        if(casesControle[i][j]==0){
                            System.out.print("j  ");
                        }
                        else{
                            System.out.print("J  ");
                        }
                        break;
                    case 3:
                        if(casesControle[i][j]==0){
                            System.out.print("v  ");
                        }
                        else{
                            System.out.print("V  ");
                        }
                        break;
                    case 4:
                        if(casesControle[i][j]==0){
                            System.out.print("b  ");
                        }
                        else{
                            System.out.print("B  ");
                        }
                        break;
                    case 5:
                        if(casesControle[i][j]==0){
                            System.out.print("i  ");
                        }
                        else{
                            System.out.print("I  ");
                        }
                        break;
                    case 6:
                        System.out.print("x  ");
                        break;
                }
            }
            System.out.println("");
        }
    }
    
    
    /*
    Cette fonction, utile en debut de partie apres l'initialisation de la grille, verifie globalement si un joueur
    a ses cases adjascentes de la meme couleur que ses cases controlees.
    Dans ce cas la fonction remet a jour le tableau de controle du jeu afin d'ajouter les nouvelles conquetes du joueur aux anciennes
    Cette verification est affectuee pour tous les joueurs
    */
    public void verifControleGlobal(){
        verifControle(joueur1);
        verifControle(joueur2);
        if(nbJoueur==3 | nbJoueur==4){
            verifControle(joueur3);
        }
        if(nbJoueur==4){
            verifControle(joueur4);
        }
    }
    
    
    /*
    Cette fonction verifie si un joueur contrele les cases adjascentes aux siennes (dans le cas ou elles seraient de la meme 
    couleur que sa couleur choisie).
    Si c'est le cas le tableau casesControle est mis a  jour.
    On commence par attribuer un numero au joueur correspondant au X dans joueurX et a  ajouter sa case de depart a une pile
    Ensuite pour chaque case du tableau, si elle est controle par le joueur X, on l'ajoute a cette pile
    Ceci constitue la premiere phase d'initialisation de la fonction.
    On va ensuite faire appel a la 2e pertie de la fonction qui se rappellera elle meme jusqu'a  avoir detecte toutes 
    les nouvelles cases controlees par le joueur X
    */
    private void verifControle(Joueur joueurX){
        int nbJoueur=0;
        int[][] controle=new int[0][2];
        PileCoord pile=new PileCoord(controle);
        if(joueurX==joueur1){
            nbJoueur=1;
            int[] coord={0,0};
            pile.addVerif(coord);
        }
        if(joueurX==joueur2){
            nbJoueur=2;
            int[] coord={this.taille-1,this.taille-1};
            pile.addVerif(coord);
        }
        if(joueurX==joueur3){
            nbJoueur=3;
            int[] coord={0,this.taille-1};
            pile.addVerif(coord);
        }
        if(joueurX==joueur4){
            nbJoueur=4;
            int[] coord={this.taille-1,0};
            pile.addVerif(coord);
        }
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(casesControle[i][j]==nbJoueur){
                    int[] coord={i,j};
                    pile.addVerif(coord);
                }
            }
        }
        verifControle2(joueurX,nbJoueur,pile,0);
    }
    
    
    /*
    Cette 2e partie de la fonction lit successivement toute les valeurs de la pile de coordonnees qu'on lui fournit
    elle repere son avancement avec l'entier val qui s'increpmente a  chaque iteration de la fonction, on parcoure ainsi la pile
    Pour chaque coordonnee lue, la fonction regarde sa couleur et si ses cases adjascentes sont de la meme couleur
    si oui elle ajoute leur coordonnees a  la pile de coordonees, mais uniquement si elle ne s'y trouve pas deja , ainsi on ne 
    parcourera pas les mems cases en boucle
    On parcoure ainsi tout le groupe de cases adjascentes qui doivent etre controlees par le joueur 'joueurX'
    On met en meme temps a  jour le tableau 'casesControle' de cases controlees par cun joueur
    la fonction s'arrete quand elle arrive au bout de la pile, c'est a  dire qu'elle a parcouru l'ensemble de ses valeur sans 
    rencontrer de nouvelles cases adjascentes de meme couleur
    Cela signifie que tout le groue de cases adjascentes a ete decouvert et parcouru, le tableau de controle du 'joueurX' est a jour
    */
    private void verifControle2(Joueur joueurX,int nbJoueur, PileCoord pile, int val){
        int[] coord=pile.read(val);
        int i=coord[0];
        int j=coord[1];
        if(i>0){
            if(cases[i-1][j]==joueurX.getCouleur()){
                casesControle[i-1][j]=nbJoueur;
                int[] coor={i-1,j};
                pile.addVerif(coor);
                
            }
        }
        if(i<this.taille-1){
            if(cases[i+1][j]==joueurX.getCouleur()){
                casesControle[i+1][j]=nbJoueur;
                int[] coor={i+1,j};
                pile.addVerif(coor);
            }
        }
        if(j>0){
            if(cases[i][j-1]==joueurX.getCouleur()){
                casesControle[i][j-1]=nbJoueur;
                int[] coor={i,j-1};
                pile.addVerif(coor);
            }
        }
        if(j<this.taille-1){
            if(cases[i][j+1]==joueurX.getCouleur()){
                casesControle[i][j+1]=nbJoueur;
                int[] coor={i,j+1};
                pile.addVerif(coor);
            }
        }
        if(i%2==1){
            if(i>0 & j<this.taille-1){
                if(cases[i-1][j+1]==joueurX.getCouleur()){
                    casesControle[i-1][j+1]=nbJoueur;
                    int[] coor={i-1,j+1};
                    pile.addVerif(coor);
                }
            }
            if(i<this.taille-1 & j<this.taille-1){
                if(cases[i+1][j+1]==joueurX.getCouleur()){
                    casesControle[i+1][j+1]=nbJoueur;
                    int[] coor={i+1,j+1};
                    pile.addVerif(coor);
                }
            }
        }
        if(i%2==0){
            if(i>0 & j>0){
                if(cases[i-1][j-1]==joueurX.getCouleur()){
                    casesControle[i-1][j-1]=nbJoueur;
                    int[] coor={i-1,j-1};
                    pile.addVerif(coor);
                }
            }
            if(i<this.taille-1 & j>0){
                if(cases[i+1][j-1]==joueurX.getCouleur()){
                    casesControle[i+1][j-1]=nbJoueur;
                    int[] coor={i+1,j-1};
                    pile.addVerif(coor);
                }
            }
        }
        if(val<pile.getPileCoordLength()-1){
            verifControle2(joueurX,nbJoueur,pile,val+1);
        }
    }
    
    
    /**
     * le joueurX joue la couleur, changer la grille en consequence et verifier si on a fini
     * @param joueurX
     * @param couleur
     * @return
     */
    public void joue(Joueur joueurX, int couleur){
        this.changementCouleur(joueurX, couleur);
        this.verifControle(joueurX);
        this.regleAvancee();
        this.verifFin();
        
    }
    
    
    /**
     * determine la couleur parmi couleurJouables choisie par l'IA selon qu'elle IA est le joueurX
     * @param joueurX
     * @param couleursJouables
     * @return
     */
    private  int choixIA(Joueur joueurX, int[] couleursJouables){
        int choix=0;
        if(joueurX.isIA()==1){
            choix=this.choixCouleurIANiveau1(joueurX,couleursJouables);
        }
        if(joueurX.isIA()==2){
            choix=this.choixCouleurIANiveau2(joueurX,couleursJouables);
        }
        if(joueurX.isIA()==31){
            choix=this.choixCouleurIAN3v1(joueurX);
        }
        if(joueurX.isIA()==32){
            choix=this.choixCouleurIAN3v2(joueurX);
        }
        if(joueurX.isIA()==4){
            //il serait preferable d'enlever le this inutile
            choix=this.choixCouleurIANiveau4(joueurX,couleursJouables, this);
        }
        return choix;
    }
    
    
    /**
     * Permet de faire joue l'IA en mode Graphique
     * @param joueurX
     * @return
     */
    public boolean joueIAGraphique(Joueur joueurX, Enregistreur piFenetre){
        
        int[] couleursJouables= this.couleursJouables();
        if(joueurX.isIA()==0){
            System.out.println("non IA dans joueIAGraphique()");
        }
        this.joue(joueurX, this.choixIA(joueurX, couleursJouables)); // l'IA fait son choix puis joue
        return true; //pour dire que l'IA a joue
    }
    
    
    /*
    Cette fonction en combine plusieurs autres de maniere a  decrire toutes les etapes necessaire pour faire jouer un joueur dans la console:
    => l'analyse des couleur jouables
    => la demande au joueur de choisir parmis ces couleurs
    => l'affectation de cette couleur a  ses cases controlees
    => la verification de controle des cases adjascentes aux siennes, et la mise a  jour du tableau de controle
    => La verification permettant de savoir si le jeu et fini ou non
    => Et enfin l'affichage dans la console du plateau
    */
    public void joueConsole(Joueur joueurX){
        int[] couleursJouables=couleursJouables();
        int choix=0;
        if(joueurX.isIA()==0){
            choix=choixCouleurConsole(joueurX, couleursJouables);
        }else{
            choix = this.choixIA(joueurX, couleursJouables);
        }
        this.joue(joueurX, choix);
        this.affichageConsole();
    }
    
    
    /*
    Renvoie la liste des couleurs qu'un joueur peut jouer (sous forme de chiffres assicies aux couleurs)
    C'est a  dire toutes sauf celles deja  contolees par un joueur.
    On cree un tableau de 6 entiers valant 1
    Si la couleur i est indisponible, on met la ieme case du tableau a  0
    */
    public int[] couleursJouables(){
        int[] couleurs={1,1,1,1,1,1};
        couleurs[joueur1.getCouleur()]=0;
        couleurs[joueur2.getCouleur()]=0;
        if(this.nbJoueur==3){
            couleurs[joueur3.getCouleur()]=0;
        }
        if(this.nbJoueur==4){
            couleurs[joueur4.getCouleur()]=0;
            couleurs[joueur3.getCouleur()]=0;
        }
        //On cree maintenant un nouveau tableau avec uniquement les valeurs des couleurs possibles
        //On compte d'abord le nombre de valeurs
        int nb=0;
        for(int i=0;i<couleurs.length;i++){
            if(couleurs[i]==1){
                nb+=1;
            }
        }
        //On cree un tableau de la taille du nombre de valeurs a  retourner
        int[] resultat=new int[nb];
        //On rempli ensuite le tableau avec les chiffres correspondant aux couleurs jouables
        int val=0;
        for(int i=0;i<couleurs.length;i++){
            if(couleurs[i]==1){
                resultat[val]=i;
                val+=1;
            }
        }
        return resultat;
    }
    
    
    /*
    Cette fonction gere l'affichage graphique dans la console du texte demadant le choix de la nouvelle couleur d'un jouer
    On lui fournit le joueur qui joue ainsi que la liste des couleurs jouables
    Elle affiche uniquement les informations concernant ces couleurs
    Elle permet ensuite au joueur de choisir via la console
    Si la couleur choisie est correcte elle est retournee par la fonction
    Sinon la choix est redemande au joueur
    */
    public int choixCouleurConsole(Joueur joueurX, int[] couleurs){
        System.out.println(joueurX.getNom()+", choisissez la nouvelle couleur de vos cases parmis les couleurs disponibles (non deja  controlees).");
        System.out.print("Tapez: ");
        if(estDisponible(0,couleurs)){
            System.out.print("0 pour rouge");
        }
        if(estDisponible(1,couleurs)){
            System.out.print(", 1 pour orange");
        }
        if(estDisponible(2,couleurs)){
            System.out.print(", 2 pour jaune");
        }
        if(estDisponible(3,couleurs)){
            System.out.print(", 3 pour vert");
        }
        if(estDisponible(4,couleurs)){
            System.out.print(", 4 pour bleu");
        }
        if(estDisponible(5,couleurs)){
            System.out.print(", 5 pour indigo (violet)");
        }
        System.out.println(" :");
        Scanner scan = new Scanner(System.in);
        int a = scan.nextInt();
        boolean ok=false;
        while(!ok){
            if(estDisponible(a,couleurs)){
                ok=true;
            }
            else{
                System.out.println("La valeur entree n'est pas correcte, reessayez :");
                a= scan.nextInt();
            }
        }
        return a;
    }
    
    
    /*
    Verifie si une couleur est disponible dans une liste de couleurs (de chiffres associes a  des couleurs plus precisement)
    Concretement : verifie si un entier est present dans un tableau d'entiers.
    */
    public boolean estDisponible(int couleur,int[] couleurs){
        boolean ok=false;
        for(int i=0;i<couleurs.length;i++){
            if(couleurs[i]==couleur){
                ok=true;
            }
        }
        return ok;
    }
    
    
    /*
    Remet a  jour le tableau 'cases' pour changer toutes les cases controlees par un joueur en la nouvelle couleur
    qu'il a choisi (donnee en parametre sous forme de chiffre associe).
    */
    public void changementCouleur(Joueur joueurX,int nouvelleCouleur){
        joueurX.setCouleur(nouvelleCouleur);
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(casesControle[i][j]==joueurX.getNb()){
                    cases[i][j]=joueurX.getCouleur();
                }
            }
        }
    }
    
    
    /*
    Cette fonction verifie si le jeu est fini et retourne le vainqueur ainsi que le nombre de cases controlees par chaque joueur dans un tableau
    Le jeu fini dans deux cas:
    => si une personne controle plus de la moitie des cases (decompte du nombre de cases controlees par chaque joueur et comparaison a  la moitie des cases)
    => si toutes les cases sont controlees (decom^pte du nombre de cases non controlees, si il vaut 0 le jeu est fini)
    Le gagnant est celuit qui a le plus de cases controlees
    Le tableau retourne par la fonction est defini comme suit:
    => tableau[0]= numero du gagnant, ou 0 si le jeu n'est pas fini
    => tableau[1]= nombre de cases controlees par le joueur 1
    => tableau[2]= nombre de cases contolees par le joueur 2
    et ainsi de suite
    cette fonction peut etre a  la fois utilisee pour verifier si leu jeu est fini apres chaque tour ou pour le comptage de points final
    et la determination du vainqueur
    Si le jeu contient des obstacles, le nombre total de cases est reduit au nombre reel de cases disponibles. Les conditions sont donc =: jeu fini si nb Cases Libres=nb obstacles ou un joueur possede plus de taille*taille - nb obstacles  cases
    */    
    public int[] verifFin(){
        int nbObstacles=0;
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(this.cases[i][j]==6){
                    nbObstacles+=1;
                }
            }
        }
        int gagnant=0;
        int casesVides=0;
        int[] points=new int[nbJoueur]; // tableau[i] correspondra au nombre de points du joueur i+1
        for(int k=0;k<points.length;k++){
            points[k]=0;
        }
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(casesControle[i][j]==0){
                    casesVides+=1;
                }
                else{
                    points[casesControle[i][j]-1]+=1;
                }
            }
        }
        for(int l=0;l<points.length;l++){
            if(points[l]>((this.taille*this.taille)-nbObstacles)/2){
                gagnant=l+1;
                this.fini=true;
            }
        }
        if(casesVides==nbObstacles){
            this.fini=true;
            gagnant=1;
            for(int m=1;m<points.length;m++){
                if(points[m]>points[m-1]){
                    gagnant=m+1;
                }
            }
        }
        int[] resultat=new int[nbJoueur+1];
        resultat[0]=gagnant;
        for(int n=0;n<nbJoueur;n++){
            resultat[n+1]=points[n];
        }
        return resultat;
    }
    
    
    /*
    Cette fonction est dediee a  l'affichage dans la console en fin de partie :
    L'affichage du nombre de points (cases controlees) de chaque joueur en face de leur nom, ainsi qui l'affichage du vainqueur.
    */    
    public void annonceGagnantConsole(){
        System.out.println("La partie est terminee. Voici les resultats :");
        int[] resultats=verifFin();
        System.out.println(joueur1.getNom()+" controle "+resultats[1]+" cases.");
        System.out.println(joueur2.getNom()+" controle "+resultats[2]+" cases.");
        if(nbJoueur==3 | nbJoueur==4){
            System.out.println(joueur3.getNom()+" controle "+resultats[3]+" cases.");
        }
        if(nbJoueur==4){
            System.out.println(joueur4.getNom()+" controle "+resultats[4]+" cases.");
        }
        int gagnant=resultats[0];
        if(gagnant==1){
            System.out.println(joueur1.getNom()+" a gagne.");
        }
        if(gagnant==2){
            System.out.println(joueur2.getNom()+" a gagne.");
        }
        if(gagnant==3){
            System.out.println(joueur3.getNom()+" a gagne.");
        }
        if(gagnant==4){
            System.out.println(joueur4.getNom()+" a gagne.");
        }
    }
    
    
    /*
    Implemente l'IA de niveau 1 qui joue au hasard
    La fonction retourne une valeur au hasard dans le tableau des valeurs possibles
    */
    public int choixCouleurIANiveau1(Joueur joueurX, int[] couleurs){
        int longueur=couleurs.length;
        Random rand=new Random();
        int choix=rand.nextInt(longueur);
        return couleurs[choix];
    }
    
    
    /*
    Implemente l'IA de niveau 2 qui choisit la couleur lui permettant de capturer le plus de cases
    On analyse successivement pour chaque couleur le nombre de points gagnes, on choisit la couleur qui rapporte le plus de points
    */
    public int choixCouleurIANiveau2(Joueur joueurX, int[] couleurs){
        int[] pointsParCouleur=new int[couleurs.length];
        int max=0;
        for(int i=0;i<couleurs.length;i++){
            pointsParCouleur[i]=comptePointsHypothetiquesJoueurParCouleur(joueurX,couleurs[i]);
            if(i>0){
                if(pointsParCouleur[i]>pointsParCouleur[i-1]){
                    max=i;
                }
            }
        }
        return couleurs[max];
    }
    
    
    /*
    Implémente une première version de l'IA 3
    */
    public int choixCouleurIAN3v1 (Joueur joueurX){
        return joueurX.couleurIA3v1(this);
    }
    
    
    /*
    Implémente une seonde version de l'IA 3
    */
    public int choixCouleurIAN3v2 (Joueur joueurX){
        return joueurX.couleurIA3v2(this);
    }
    
    
    /*
    Implemente l'IA de niveau 4 qui est une version amelioree de l IA 2
    Elle compare toutes les couleurs jouables et joue la couleur lui permettant de gagner le plus de points
    Quand plusieurs couleurs lui peremttent de gagner le meme nombre de points elle choisit la couleur qui embete le plus un adversaire
    C est a dire la couleur qui fera perdre le plus de points a un joueur dans le tour a venir
    (Elle choisit le joueur qui a la plus grande difference entre le maximun et le minimum de points gagnes avec les couleurs disponibles)
    */
    public int choixCouleurIANiveau4(Joueur joueurX, int[] couleurs, Plateau grille){
        int[] pointsParCouleur=new int[couleurs.length]; // On cree un plateau de maniere a  avoir: pointsParCouleur[i] = nb ne point si on joue le couleur couleurs[i]
        // sachant que tableau 'couleurs', represente le tableau des valeurs jouables par le joueurX
        // On calcule ces valeurs et un determine le rang 'max' de la couleurs de 'couleurs' qui correspond a  la couleur gagnant le plus de points
        
        int max=0;
        for(int i=0;i<couleurs.length;i++){
            pointsParCouleur[i]=comptePointsHypothetiquesJoueurParCouleur(joueurX,couleurs[i]);
            if(i>0){
                if(pointsParCouleur[i]>pointsParCouleur[max]){
                    max=i;
                }
            }
        }
        
        System.out.println("Couleurs jouables:");
        for(int i=0;i<pointsParCouleur.length;i++){
            System.out.print(couleurs[i]+" : ");
            System.out.println(pointsParCouleur[i]+" pts");
        }
        System.out.println("Couleurs max: "+couleurs[max]+" avec "+pointsParCouleur[max]+" pts");
        
        // On cherche maintenant si il n'y a pas plusieurs couleurs qui ameneraient a  ce nombre de points
        // On compte donc le nombre de couleurs permettant de gagner ce maximum de points dans la variable c
        int c=0;
        for(int i=0;i<pointsParCouleur.length;i++){
            if(pointsParCouleur[i]==pointsParCouleur[max]){
                c+=1;
            }
        }
        // Si c'est le cas c'est qu'il y a au moins deux variables amenant au maximum de point (on a c>1)
        // Dans ce cas il va falloir choisir le couleur qui embete le plus l'adversaire parmis ces plusieurs couleurs optimales
        // On commence donc par les lister dans un tableau solutionEgales
        if(c>1){
            int [] solutionsEgales=new int[c]; // On cree un nouveau tableau acueillant les chiffres des couleurs amanant a  cet egal gain
            c=0;
            for(int i=0;i<pointsParCouleur.length;i++){
                if(pointsParCouleur[i]==pointsParCouleur[max]){
                    solutionsEgales[c]=couleurs[i];
                    c+=1;
                }
            }
            
            
            // On determine alors le numero du joueur suivant
            int nbJoueurActuel=joueurX.getNb();
            nbJoueurActuel+=1;
            if(nbJoueurActuel>grille.nbJoueur){
                nbJoueurActuel=1;
            }
            // On recupere le joueur correspondant au joueur suivant, afin de pouvoir appliquer la fonction comptePointsHypothetiquesJoueurParCouleur
            Joueur joueurSuivant=grille.getJoueurv2(nbJoueurActuel);
            // On cree maintenant un tableau contenant les points gagnes par cet adversaire en jouant cette couleur
            //  de maniere a  ce que points gagnes en jouant la couleur solutionsEgales[i]=pointsParCouleur2[i];
            int[] pointsParCouleur2=new int[solutionsEgales.length];
            // On cree aussi la variable qui contiendra le numero de la couleur faisant gagner le moins de points possibles a  l'adversaire
            int minSuivant=0;
            int maxSuivant=0;
            for(int j=0;j<pointsParCouleur2.length;j++){
                pointsParCouleur2[j]=comptePointsHypothetiquesJoueurParCouleur(joueurSuivant,solutionsEgales[j]);
                if(j>0){
                    if(pointsParCouleur2[j]<pointsParCouleur2[minSuivant]){
                        minSuivant=j;
                    }
                    if(pointsParCouleur2[j]>pointsParCouleur2[maxSuivant]){
                        maxSuivant=j;
                    }
                }
            }// On a onc le choix de couleur intermediaire dans le cas ou il y a 2 joueurs
            int solution=solutionsEgales[maxSuivant];
            
            // On refait la meme chose avec le 2e joueurs suivant (si il y a 3 joueurs)
            if(grille.getNbJoueur()==3){
                // On determine alors le numero du  2ejoueur suivant
                nbJoueurActuel+=1;
                if(nbJoueurActuel>grille.getNbJoueur()){
                    nbJoueurActuel=1;
                }
                // On recupere le joueur correspondant au joueur suivant, afin de pouvoir appliquer la fonction comptePointsHypothetiquesJoueurParCouleur
                Joueur joueurSuivant2=grille.getJoueurv2(nbJoueurActuel);
                // On cree maintenant un tableau contenant les points gagnes par ce 2e adversaire en jouant cette couleur
                //  de maniere a  ce que points gagnes en jouant la couleur solutionsEgales[i]=pointsParCouleur2[i];
                int[] pointsParCouleur3=new int[solutionsEgales.length];
                // On cree aussi la variable qui contiendra le numero de la couleur faisant gagner le moins de points possibles a  l'adversaire
                int minSuivant2=0;
                int maxSuivant2=0;
                for(int j=0;j<pointsParCouleur3.length;j++){
                    pointsParCouleur3[j]=comptePointsHypothetiquesJoueurParCouleur(joueurSuivant2,solutionsEgales[j]);
                    if(j>0){
                        if(pointsParCouleur3[j]<pointsParCouleur3[minSuivant2]){
                            minSuivant2=j;
                        }
                        if(pointsParCouleur3[j]>pointsParCouleur3[maxSuivant2]){
                            maxSuivant2=j;
                        }
                    }
                }// On regarde maintenant le second resultat intermediaure pour 3 joueurs
                // Si on empeche le 2e joueur suivant de gagner plus de points qu'au premier, alors on retourne la couleurs lui faisant gagner le moins de points
                if(maxSuivant2-minSuivant2>maxSuivant-minSuivant){
                    solution=solutionsEgales[maxSuivant2];
                }
            }
            
            
            
            // On refait encore la meme chose avec le 2e et 3e joueurs suivant si il y a 4 joueurs
            if(grille.getNbJoueur()==4){
                // On determine alors le numero du  2ejoueur suivant
                nbJoueurActuel+=1;
                if(nbJoueurActuel>grille.getNbJoueur()){
                    nbJoueurActuel=1;
                }
                // On recupere le joueur correspondant au joueur suivant, afin de pouvoir appliquer la fonction comptePointsHypothetiquesJoueurParCouleur
                Joueur joueurSuivant2=grille.getJoueurv2(nbJoueurActuel);
                // On cree maintenant un tableau contenant les points gagnes par ce 2e adversaire en jouant cette couleur
                //  de maniere a  ce que points gagnes en jouant la couleur solutionsEgales[i]=pointsParCouleur2[i];
                int[] pointsParCouleur3=new int[solutionsEgales.length];
                // On cree aussi la variable qui contiendra le numero de la couleur faisant gagner le moins de points possibles a  l'adversaire
                int minSuivant2=0;
                int maxSuivant2=0;
                for(int j=0;j<pointsParCouleur3.length;j++){
                    pointsParCouleur3[j]=comptePointsHypothetiquesJoueurParCouleur(joueurSuivant2,solutionsEgales[j]);
                    if(j>0){
                        if(pointsParCouleur3[j]<pointsParCouleur3[minSuivant2]){
                            minSuivant2=j;
                        }
                        if(pointsParCouleur3[j]>pointsParCouleur3[maxSuivant2]){
                            maxSuivant2=j;
                        }
                    }
                }
                // On determine alors le numero du  2ejoueur suivant
                nbJoueurActuel+=1;
                if(nbJoueurActuel>grille.getNbJoueur()){
                    nbJoueurActuel=1;
                }
                // On recupere le joueur correspondant au joueur suivant, afin de pouvoir appliquer la fonction comptePointsHypothetiquesJoueurParCouleur
                Joueur joueurSuivant3=grille.getJoueurv2(nbJoueurActuel);
                // On cree maintenant un tableau contenant les points gagnes par ce 3e adversaire en jouant cette couleur
                //  de maniere a  ce que points gagnes en jouant la couleur solutionsEgales[i]=pointsParCouleur2[i];
                int[] pointsParCouleur4=new int[solutionsEgales.length];
                // On cree aussi la variable qui contiendra le numero de la couleur faisant gagner le moins de points possibles a  l'adversaire
                int minSuivant3=0;
                int maxSuivant3=0;
                for(int j=0;j<pointsParCouleur4.length;j++){
                    pointsParCouleur4[j]=comptePointsHypothetiquesJoueurParCouleur(joueurSuivant3,solutionsEgales[j]);
                    if(j>0){
                        if(pointsParCouleur4[j]<pointsParCouleur4[minSuivant3]){
                            minSuivant3=j;
                        }
                        if(pointsParCouleur4[j]>pointsParCouleur4[minSuivant3]){
                            maxSuivant3=j;
                        }
                    }
                }// On regarde maintenant le second resultat intermediaure pour 3 joueurs
                // Si on empeche le 3e joueur suivant de gagner plus de points qu'au premier ou au 2e, alors on retourne la couleurs lui faisant gagner le moins de points
                if(maxSuivant3-minSuivant3>maxSuivant-minSuivant | maxSuivant-minSuivant3>maxSuivant2-minSuivant2){
                    solution=solutionsEgales[maxSuivant3];
                }
            }
            System.out.println("solutions Egales possibles");
            for(int i=0;i<solutionsEgales.length;i++){
                System.out.println(solutionsEgales[i]+" avec "+pointsParCouleur2[i]+" pts pour l'adversaire");
                
            }
            System.out.println("on choisit "+solutionsEgales[maxSuivant]+" avec "+pointsParCouleur2[maxSuivant]+" pts pour l'adversaire");
            return solution;
        }
        return couleurs[max];
    }
    
    
    
    /*
    Cette fonction compte le nombre de points qu'un joueur gagnerait en jouant une certaine couleur
    Elle compte le nombre de points actuels d'un joueur
    Puis elle cree un copie du plateau de jeu pour y faire jouer la couleur demandee au joueur demande
    (avec la verification de controle des cases et l'application de la regle avancee)
    Elle compte les point obtenus en fin de tour et renvoie la difference avec les points avant de jouer
    */
    public int comptePointsHypothetiquesJoueurParCouleur(Joueur joueurX,int couleur){
        Plateau copieGrille=new Plateau(this,couleur,joueurX);
        return copieGrille.comptePoints(joueurX)-this.comptePoints(joueurX);
    }
    
    
    /*
    compte le nombre de points d'un joueur et le retourne
    */
    private int comptePoints(Joueur joueurX){
        int c=0;
        for(int i=0;i<this.taille;i++){
            for(int j=0;j<this.taille;j++){
                if(this.casesControle[i][j]==joueurX.getNb()){
                    c+=1;
                }
            }
        }
        return c;
    }
    
     
    /*
    Permet de changer directement de couleur des cases libres entourees par les cases d un joueur et ne pouvant plus etre capturees que par lui
    Permet aussi de transformer en obstacle toutes les cases libres entourees par des obstacles, de maniere a ce qu il ny ait pas de
    cases non capturables
    */
    public void regleAvancee(){
        ArrayList<Integer>[] casesLibres = (ArrayList<Integer>[])new ArrayList[(this.taille+1)];
        boolean[][] dejaPassee = new boolean [this.taille][this.taille]; //tableau des cases deja visiter par les fct vers();
        boolean[] joueursRencontres = new boolean[this.nbJoueur]; // liste des joueursRencontres par un groupe
        
        //on va dresser l'ensemble des cases qui peuvent potentiellement forme un groupe entourable
        for(int i = 0; i<this.taille; i++){
            casesLibres[i] = new ArrayList<Integer>(this.taille + 1);
            for(int j = 0; j< this.taille; j++){
                if ( (this.casesControle[i][j] == 0) && (this.cases[i][j] != 6)){ //si la cases n'aapartient a personne et si ce n'est pas un obstacle alors elle est "libre"
                    casesLibres[i].add( (new Integer(j)) );
                }
            }
        }
        
        for (int i = 0; i<this.taille; i++){
            while(!casesLibres[i].isEmpty()){
                this.gererGroupe( i, casesLibres[i].get(0), casesLibres, dejaPassee, joueursRencontres);
            }
        }
    }
    
    
    /**
     * reinitialise les tableau joueursRencontres et dejaPasse à false.
     * @param dejaPassee
     * @param joueursRencontres
     */
    private static void reinitialisation(boolean[][] dejaPassee, boolean[] joueursRencontres){
        for (int i = 0; i < dejaPassee.length; i++){
            for(int j = 0; j < dejaPassee.length; j++){
                dejaPassee[i][j] = false;
            }
        }
        for (int i = 0; i < joueursRencontres.length; i++){
            joueursRencontres[i] = false;
        }
    }
    
    
    /**
     * Par d'une cases i,j; en determine par recurrence avec les fonctions vers() le groupe correspondants et les joueursRencontres le groupe
     * Puis les enleves des cases libres (a tester) et si necessaire recolore le groupe...
     * @param i
     * @param j
     * @param casesLibres
     * @param dejaPassee
     * @param joueursRencontres
     */
    private void gererGroupe( int i, Integer j, ArrayList<Integer>[] casesLibres, boolean[][] dejaPassee, boolean[] joueursRencontres){
        reinitialisation( dejaPassee, joueursRencontres);
        this.versDroite(i,j,dejaPassee, joueursRencontres);
        dejaPassee[i][j]=false;
        this.versGauche(i,j,dejaPassee, joueursRencontres);
        this.enleverGroupe(casesLibres, dejaPassee, joueursRencontres);
    }
    
    
    /**
     * a ce stade on trouve dans dejaPassee les cases du groupe (valeur true), et dans joueursRencontres les joueurs en contact avec le dit groupe
     * enlevons a cases libre le groupe vu qu'il a ete process et changeons la couleurs et les appartenances si necessaire.
     * @param casesLibres
     * @param dejaPassee
     * @param joueursRencontres
     */
    private void enleverGroupe ( ArrayList<Integer>[] casesLibres, boolean[][] dejaPassee, boolean[] joueursRencontres){
        int possession = 0; //in fine 0,1,2,3 ou 4
        int joueur = 0;
        for (int i = 0; i<joueursRencontres.length; i++){
            if (joueursRencontres[i]){
                joueur = i+1;
                possession++;
            }
        }
        //possession = 0, le groupe  est entoure d'obstacle transformons la en obstacle
        //possession = 1, le groupe est entoure d'un joueur (joueur) transformons la en ça couleur
        //possession >= 2, le groupe est entoure d'au moins deux joueurs laissons lui la paix
        Integer jBis = new Integer(0);
        if( possession == 0 ){
            for(int i = 0; i<this.taille; i++){
                for(int j = 0; j< this.taille; j++){
                    if(dejaPassee[i][j]){//i,j fait partie du groupe
                        this.cases[i][j] = 6; //chgmt de couleur
                        jBis = j;
                        casesLibres[i].remove(jBis);//on l'enleve des elements a scruter
                    }
                }
            }
        }
        else if ( possession == 1 ){
            for(int i = 0; i<this.taille; i++){
                for(int j = 0; j< this.taille; j++){
                    if(dejaPassee[i][j]){//i,j fait partie du groupe
                        this.cases[i][j] =  this.getJoueurv2(joueur).getCouleur(); //chgmt de couleur
                        this.casesControle[i][j] = joueur; //on redefinie les appartenance
                        jBis = j;
                        casesLibres[i].remove(jBis);//on l'enleve des elements a scruter
                    }
                }
            }
        }else{
            for(int i = 0; i<this.taille; i++){
                for(int j = 0; j< this.taille; j++){
                    if(dejaPassee[i][j]){//i,j fait partie du groupe
                        jBis = j;
                        casesLibres[i].remove(jBis);//on l'enleve des elements a scruter
                    }
                }
            }
        }
    }
    
    
    /**
     * renvoie  le numero du joueur a qui appartient la cases qu'on essaye d'acceder par recurrence (celle en a, b), 0 si libre, 6 si obstacle
     * @param a
     * @param b
     * @return
     */
    private int coteTeste(int a, Integer b){
        int coteTeste;
        if( (a<this.taille) && (b<this.taille) && (a>=0) && (b>=0) ){ //si on sort du tableau la cases est donc considerer comme un obstacle
            coteTeste = this.casesControle[a][b];
            if(this.cases[a][b] == 6){
                coteTeste = 6;
            }
        }
        else {
            coteTeste = 6;}
        return coteTeste;
        
    }
    
    
    /**
     * fonction renvoyant l'integer a mettre pour j pour alle en haut a gauche quand on se trouve a la ligne i (et oui... +1, -1 ça marche pas en quincquonce
     * @param i
     * @param j
     * @return
     */
    private static Integer hg (int i, int j){
        return j-((i+1)%2);
    }
    
    
    /**
     * fonction renvoyant l'integer a mettre pour j pour alle en haut a droite quand on se trouve a la ligne i (et oui... +1, -1 ça marche pas en quincquonce
     * @param i
     * @param j
     * @return
     */
    private static Integer hd (int i, int j){
        return j+(i%2);
    }
    
    
    /**
     * fonction renvoyant l'integer a mettre pour j pour alle en bas a gauche quand on se trouve a la ligne i (et oui... +1, -1 ça marche pas en quincquonce
     * @param i
     * @param j
     * @return
     */
    private static Integer bg (int i, int j){
        return j-((i+1)%2);
    }
    
    
    /**
     * fonction renvoyant l'integer a mettre pour j pour alle en bas a droite quand on se trouve a la ligne i (et oui... +1, -1 ça marche pas en quincquonce
     * @param i
     * @param j
     * @return
     */
    private static Integer bd (int i, int j){
        return j+(i%2);
    }
    
    
    /**
     * tres honnêtement même moi je suis pas sur de l'avoir compris celle-la, schema obligatoire. le but etant en la combinant avec versGauche() de passe par
     * toutes les cases d'un groupe par recurrence et de conserver tout les joueursRencontrees du meme coup. Celle-ci tente d'aller vers la droite comme son nom
     * l'indique un cas d'echec elle essaye de monte et descendre. Un petit ajout qu'en on ne peut pas alle vers le heut/bas a droite on ajoute une recurrence a gauche
     * vers la haut/bas gauche
     * @param i
     * @param j
     * @param dejaPassee
     * @param joueursRencontres
     */
    private void versDroite (int i, int j, boolean[][] dejaPassee, boolean[] joueursRencontres){
        if(dejaPassee[i][j] == false && (i>=0) && (j>=0) && (i<this.taille) && (j<this.taille) ){ // si on est out of bound inutile et si on est deja passe cela ne sert a rien (excepter lors des changement d'etages ou il faut partir des deux cotes sur la même case et la on repasse a false avant d'aller versGauche()
            dejaPassee[i][j] = true;
            int coteTeste = this.coteTeste(i, j+1);
            if (this.coteTeste(i, j+1) != 0){ //si l'on ne peut plus alle a droite
                if( (coteTeste>0) && coteTeste<=nbJoueur){ //si c'est la faute a  un joueur on l'ajoute dans le tableau des joueurs rencontres
                    joueursRencontres[coteTeste-1]=true;
                }
                //on essaye de descendre et de monte
                //pour la monte
                int a = i;
                int b = j;
                boolean reussi = false; // a t'on resussi a monter (ou descendre)
                if (i>0){		//si on peut monte
                    while( (b >= 0) && dejaPassee[a][b] && !reussi){ //tant qu'on a pas reussi a monte on va remonter en arriere toute les cases qui ont deja ete process (pour comprendre la il faut un schema)
                        coteTeste = this.coteTeste(a-1, hd(a,b)); // on regarde si la case en haut a droite marche
                        if(coteTeste == 0){ //le case en diagonale au dessus a droite est libre
                            reussi = true;
                            if(dejaPassee[a-1][hd(a,b)] == false){ //personne n'a encore monter on va le faire et partir vers la droite et la gauche
                                versDroite(a-1, hd(a,b),dejaPassee, joueursRencontres);
                                dejaPassee[a-1][hd(a,b)] = false;
                                versGauche(a-1, hd(a,b),dejaPassee, joueursRencontres);
                            }
                        }
                        else if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                            joueursRencontres[coteTeste-1]=true;
                        }
                        b--;
                    }
                }
                a=i; //on reinitialise la boucle
                b=j;
                reussi = false;
                if (i+1<this.taille){		//si on peut descendre
                    while( (b >= 0) && dejaPassee[a][b] && !reussi){ //tant qu'on a pas reussi a descendre on va remonter en arriere toute les cases qui ont deja ete process (pour comprendre la il faut un schema)
                        coteTeste = this.coteTeste(a+1, bd(a,b)); // on regarde si la case en bas a droite marche
                        if(coteTeste == 0){ //le case en diagonale en dessous a droite est libre
                            reussi = true;
                            if(dejaPassee[a+1][bd(a,b)] == false){ //personne n'a encore mdescendu on va le faire et partir vers la droite et la gauche
                                versDroite(a+1, bd(a,b),dejaPassee, joueursRencontres);
                                dejaPassee[a+1][bd(a,b)] = false;
                                versGauche(a+1, bd(a,b),dejaPassee, joueursRencontres);
                            }
                        }
                        else if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                            joueursRencontres[coteTeste-1]=true;
                        }
                        b--;
                    }
                }
            }
            else{ //on peut alle a droite
                if(j+1<this.taille){
                    versDroite(i, j+1,dejaPassee, joueursRencontres);
                }
            }
            //dans tout les cas, si la case en haut  ou bas a droite n'est pas libre il faut essayer d'alle en haut ou bas a gauche
            //testons la case en haut a droite
            coteTeste= this.coteTeste(i-1, hd(i,j));
            if(coteTeste != 0){ //si on ne peut pas on essaye d'alle vers en heut a gauche
                if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                    joueursRencontres[coteTeste-1]=true;
                }
                coteTeste = this.coteTeste(i-1, hg(i,j));
                if(coteTeste == 0){
                    if(dejaPassee[i-1][hg(i,j)] == false){ //personne n'a encore monter on va le faire et partir vers la droite et la gauche
                        versDroite(i-1, hg(i,j),dejaPassee, joueursRencontres);
                        dejaPassee[i-1][hg(i,j)] = false;
                        versGauche(i-1, hg(i,j),dejaPassee, joueursRencontres);
                    }
                }else{
                    if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                        joueursRencontres[coteTeste-1]=true;
                    }
                }
            }else{
                // ne rien faire
            }
            //testons la case en bas a droite
            coteTeste= this.coteTeste(i+1, bd(i,j));
            if(coteTeste != 0){ //si on ne peut pas on essaye d'alle vers en bas a gauche
                if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                    joueursRencontres[coteTeste-1]=true;
                }
                coteTeste = this.coteTeste(i+1, bg(i,j));
                if(coteTeste == 0 ){
                    if(dejaPassee[i+1][bg(i,j)] == false){ //personne n'a encore descendu on va le faire et partir vers la droite et la gauche
                        versDroite(i+1, bg(i,j),dejaPassee, joueursRencontres);
                        dejaPassee[i+1][bg(i,j)] = false;
                        versGauche(i+1, bg(i,j),dejaPassee, joueursRencontres);
                    }
                }else{
                    if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                        joueursRencontres[coteTeste-1]=true;
                    }
                }
            }else{
                // ne rien faire
            }
        }
        else{
            // ne rien faire (enfin je crois)
        }
    }
    
    
    /**
     * similaire vers droite mais inverser dans les deplacements
     * @param i
     * @param j
     * @param dejaPassee
     * @param joueursRencontres
     */
    private void versGauche (int i, int j, boolean[][] dejaPassee, boolean[] joueursRencontres){
        if(dejaPassee[i][j] == false && (i>=0) && (j>=0) && (i<this.taille) && (j<this.taille) ){ // si on est deja passe cela ne sert a rien (excepter lors des changement d'etages ou il faut partir des deux cotes sur la même case et la on repasse a false avant d'aller versGauche()
            dejaPassee[i][j] = true;
            int coteTeste = this.coteTeste(i, j-1);
            if (coteTeste != 0){ //si l'on ne peut plus alle a droite
                if( (coteTeste>0) && coteTeste<=nbJoueur){ //si c'est la faute a  un joueur on l'ajoute dans le tableau des joueurs rencontres
                    joueursRencontres[coteTeste-1]=true;
                }
                //on essaye de descendre et de monte
                //pour la monte
                int a = i;
                int b = j;
                boolean reussi = false; // a t'on resussi a monter (ou descendre)
                if (i>0){		//si on peut monte
                    while( (b < this.taille) && dejaPassee[a][b] && !reussi){ //tant qu'on a pas reussi a monte on va remonter en arriere toute les cases qui ont deja ete process (pour comprendre la il faut un schema)
                        coteTeste = this.coteTeste(a-1, hg(a,b)); // on regarde si la case en haut a  gauche marche
                        if(coteTeste == 0){ //le case en diagonale au dessus a  gauche est libre
                            reussi = true;
                            if(dejaPassee[a-1][hg(a,b)] == false){ //personne n'a encore monter on va le faire et partir vers la droite et la gauche
                                versDroite(a-1, hg(a,b),dejaPassee, joueursRencontres);
                                dejaPassee[a-1][hg(a,b)] = false;
                                versGauche(a-1, hg(a,b),dejaPassee, joueursRencontres);
                            }
                        }
                        else if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                            joueursRencontres[coteTeste-1]=true;
                        }
                        b++;
                    }
                }
                a=i; //on reinitialise la boucle
                b=j;
                reussi = false;
                if (i+1<this.taille){		//si on peut descendre
                    while( (b < this.taille) && dejaPassee[a][b] && !reussi){ //tant qu'on a pas reussi a descendre on va remonter en arriere toute les cases qui ont deja ete process (pour comprendre la il faut un schema)
                        coteTeste = this.coteTeste(a+1, bg(a,b)); // on regarde si la case en bas a gauche marche
                        if(coteTeste == 0){ //le case en diagonale en dessous a gauche est libre
                            reussi = true;
                            if(dejaPassee[a+1][bg(a,b)] == false){ //personne n'a encore mdescendu on va le faire et partir vers la droite et la gauche
                                versDroite(a+1, bg(a,b),dejaPassee, joueursRencontres);
                                dejaPassee[a+1][bg(a,b)] = false;
                                versGauche(a+1, bg(a,b),dejaPassee, joueursRencontres);
                            }
                        }
                        else if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                            joueursRencontres[coteTeste-1]=true;
                        }
                        b++;
                    }
                }
            }
            else{ //on peut alle a gauche
                if(j>0){
                    versGauche(i, j-1,dejaPassee, joueursRencontres);
                }
            }
            //dans tout les cas, si la case en haut  ou bas a  gauche n'est pas libre il faut essayer d'alle en haut ou bas a droite
            //testons la case en haut a  gauche
            coteTeste = this.coteTeste(i-1, hg(i,j));
            if(coteTeste != 0){ //si on ne peut pas on essaye d'alle vers en heut a droite
                if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                    joueursRencontres[coteTeste-1]=true;
                }
                coteTeste = this.coteTeste(i-1, hd(i,j));
                if(coteTeste == 0){
                    if(dejaPassee[i-1][hd(i,j)] == false){ //personne n'a encore monter on va le faire et partir vers la droite et la gauche
                        versDroite(i-1, hd(i,j),dejaPassee, joueursRencontres);
                        dejaPassee[i-1][hd(i,j)] = false;
                        versGauche(i-1, hd(i,j),dejaPassee, joueursRencontres);
                    }
                }else{
                    if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                        joueursRencontres[coteTeste-1]=true;
                    }
                }
            }else{
                // ne rien faire
            }
            //testons la case en bas a  gauche
            coteTeste= this.coteTeste(i+1, bg(i,j));
            if(coteTeste != 0){ //si on ne peut pas on essaye d'alle vers en bas a droite
                if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                    joueursRencontres[coteTeste-1]=true;
                }
                coteTeste= this.coteTeste(i+1, bd(i,j));
                if(coteTeste == 0 ){
                    if(dejaPassee[i+1][bd(i,j)] == false){ //personne n'a encore descendu on va le faire et partir vers la droite et la gauche
                        versDroite(i+1, bd(i,j),dejaPassee, joueursRencontres);
                        dejaPassee[i+1][bd(i,j)] = false;
                        versGauche(i+1, bd(i,j),dejaPassee, joueursRencontres);
                    }
                }else{
                    if(coteTeste<5){//si la cases est bloque par un joueur on le rajoute a la liste des joueursRencontres
                        joueursRencontres[coteTeste-1]=true;
                    }
                }
            }else{
                // ne rien faire
            }
        }
        else{
            //ne rien faire (enfin je crois)
        }
    }
    
    
    
    
    
    
    /*
    * obsolete, premiere version de regle avancee
    *
    public void regleAvancee(){
    // Cette fonction met en place la fonction avancee suivante: si un joueur entoure par ces cases controlees un groupe de cases
    // non controlees, il en prend le controle immediatement (ces cases ne pourront plus etre controlees que par lui de toute maniere).
    int ancienNum;
    boolean controleGroupe;
    int[][] tab=new int[0][0];
    PileCoord resultat=new PileCoord(tab);
    for(int i=0;i<this.taille;i++){ // Pour chaque case du tableau
    for(int j=0;j<this.taille;j++){
    if(casesControle[i][j]==0 & cases[i][j]!=6){// Si la case n'est pas contolee et n'est pas un obstacle
    int[][] groupeNonControle=detecterGroupeNonControle(i,j);// On cree le groupe des cases non controlees adjacentes
    controleGroupe=true; // Par defaut le controle du groupe est valide
    ancienNum=-1;
    for(int k=0;k<groupeNonControle.length;k++){// Pour chaque case de ce groupe
    int[] liste=numeroVoisinsGroupe(groupeNonControle[k][0],groupeNonControle[k][1]);// On trouve le liste du numero de ses voisins
    //System.out.println("longueur ="+liste.length);
    if(liste.length>1){ //si la longueur de cette liste est plus grande que 1, on a croise deux joueurs
    controleGroupe=false;// Donc aucun joueur ne contole le groupe
    //System.out.print(" liste >1");
    }
    else{
    if(liste.length==1){//Sinon si la longueur de la liste vaut 1
    if(ancienNum==-1){//Si on a pas rencontre de joueur avant
    ancienNum=liste[0]; // On memorise ce numero de joueur
    //System.out.println("definition ancienNum= "+ancienNum);
    }
    else{ // Si on a deja  rencontre un numero de joueur
    if(ancienNum!=liste[0]){ // si ce joueur est different du joueur deja  rencontre
    controleGroupe=false; // Le controle du groupe n'est pas valide
    //System.out.println("nouveauNum= "+liste[0]);
    }
    }
    }
    //Et si la liste avait une longueur de 0 (on a rencontre aucun joueur, c'est qu'on est au milieu du groupe,
    // Au bord du plateau, ou contre un obstacle, et on ne peut rien dire de plus sur le controle du group
    }
    }
    if(controleGroupe==true){// Finalement si un seul joueur a acces a  ce groupe, c'est qu'il est le seul a  entourer le groupe
    //System.out.println("controleOK");
    MAJCasesControle(groupeNonControle,ancienNum);// On met a  jour ses cases Controlees
    }
    else{
    //System.out.println("controleNOP");
    }
    }
    }// On rajoute le goupe au resultat
    }
    }
    
    
    public int[][] detecterGroupeNonControle(int i, int j){
    // Cette fonction detecte un groupe de cases non controlees a  partir d'un case dont les coordonnees sont fournies en parametres
    // elle retourne une liste de coordonnees des cases de ce groupe
    // elle ne prend pas en compte les obstacles
    int[][] controle={{i,j}};
    PileCoord pile=new PileCoord(controle);
    int[][] resultat=detecterGroupeNonControle2(pile,0);
    return resultat;
    }
    
    
    private int[][] detecterGroupeNonControle2(PileCoord pile, int val){
    // Cette 2e partie de la fonction lit successivement toute les valeurs de la pile de coordonnees qu'on lui fournit
    // elle repere son avancement avec l'entier val qui s'increpmente a  chaque iteration de la fonction, on parcoure ainsi la pile
    // Pour chaque coordonnee lue, la fonction regarde sa couleur et si ses cases adjascentes sont de la meme couleur
    // si oui elle ajoute leur coordonnees a  la pile de coordonees, mais uniquement si elle ne s'y trouve pas deja , ainsi on ne parcourera pas les mems cases en boucle
    // On parcoure ainsi tout le groupe de cases adjascentes qui doivent etre controlees par le joueur 'joueurX'
    // On met en meme temps a  jour le tableau 'casesControle' de cases controlees par cun joueur
    // la fonction s'arrete quand elle arrive au bout de la pile, c'est a  dire qu'elle a parcouru l'ensemble de ses valeur sans rencontrer de nouvelles cases adjascentes de meme couleur
    // Cela signifie que tout le groue de cases adjascentes a ete decouvert et parcouru, le tableau de controle du 'joueurX' est a  jour
    int[] coord=pile.read(val);
    int i=coord[0];
    int j=coord[1];
    if(i>0){
    if(casesControle[i-1][j]==0 & cases[i-1][j]!=6){
    int[] coor={i-1,j};
    pile.addVerif(coor);
    
    }
    }
    if(i<this.taille-1){
    if(casesControle[i+1][j]==0 & cases[i+1][j]!=6){
    int[] coor={i+1,j};
    pile.addVerif(coor);
    }
    }
    if(j>0){
    if(casesControle[i][j-1]==0 & cases[i][j-1]!=6){
    int[] coor={i,j-1};
    pile.addVerif(coor);
    }
    }
    if(j<this.taille-1){
    if(casesControle[i][j+1]==0 & cases[i][j+1]!=6){
    int[] coor={i,j+1};
    pile.addVerif(coor);
    }
    }
    if(i%2==1){
    if(i>0 & j<this.taille-1){
    if(casesControle[i-1][j+1]==0 & cases[i-1][j+1]!=6){
    int[] coor={i-1,j+1};
    pile.addVerif(coor);
    }
    }
    if(i<this.taille-1 & j<this.taille-1){
    if(casesControle[i+1][j+1]==0 & cases[i+1][j+1]!=6){
    int[] coor={i+1,j+1};
    pile.addVerif(coor);
    }
    }
    }
    if(i%2==0){
    if(i>0 & j>0){
    if(casesControle[i-1][j-1]==0 & cases[i-1][j-1]!=6){
    int[] coor={i-1,j-1};
    pile.addVerif(coor);
    }
    }
    if(i<this.taille-1 & j>0){
    if(casesControle[i+1][j-1]==0 & cases[i+1][j-1]!=6){
    int[] coor={i+1,j-1};
    pile.addVerif(coor);
    }
    }
    }
    if(val<pile.getPileCoordLength()-1){
    detecterGroupeNonControle2(pile,val+1);
    }
    return pile.getPileCoord();
    }
    
    
    public int[] numeroVoisinsGroupe(int i, int j){
    // Cette fonction retourne une liste des joueurs possedant une case adjacente a  la case dont les coordonnees sont fournies en parametres
    boolean[] joueurAdjacent=new boolean[this.nbJoueur];
    for(int k=0;k<this.nbJoueur;k++){ // on cree un tableau dont l'indice represente un joueur: joueurAdjacent[i] represente joueur i+1,
    joueurAdjacent[k]=false; // la valeur de joueurAdjacent[i] indique si cette couleur est autour de la case en question
    }
    if(i>0){
    if(casesControle[i-1][j]!=0){
    joueurAdjacent[casesControle[i-1][j]-1]=true;
    }
    }
    if(i<this.taille-1){
    if(casesControle[i+1][j]!=0){
    joueurAdjacent[casesControle[i+1][j]-1]=true;
    }
    }
    if(j>0){
    if(casesControle[i][j-1]!=0){
    joueurAdjacent[casesControle[i][j-1]-1]=true;
    }
    }
    if(j<this.taille-1){
    if(casesControle[i][j+1]!=0){
    joueurAdjacent[casesControle[i][j+1]-1]=true;
    }
    }
    if(i%2==1){
    if(i>0 & j<this.taille-1){
    if(casesControle[i-1][j+1]!=0){
    joueurAdjacent[casesControle[i-1][j+1]-1]=true;
    }
    }
    if(i<this.taille-1 & j<this.taille-1){
    if(casesControle[i+1][j+1]!=0){
    joueurAdjacent[casesControle[i+1][j+1]-1]=true;
    }
    }
    }
    if(i%2==0){
    if(i>0 & j>0){
    if(casesControle[i-1][j-1]!=0){
    joueurAdjacent[casesControle[i-1][j-1]-1]=true;
    }
    }
    if(i<this.taille-1 & j>0){
    if(casesControle[i+1][j-1]!=0){
    joueurAdjacent[casesControle[i+1][j-1]-1]=true;
    }
    }
    }
    int c=0; // On compte le nombre de valeurs a  retourner
    for(int k=0;k<this.nbJoueur;k++){
    if(joueurAdjacent[k]){
    c+=1;
    }
    }
    int[] resultat=new int[c];
    c=0;
    for(int k=0;k<this.nbJoueur;k++){
    if(joueurAdjacent[k]){
    resultat[c]=k+1;
    c+=1;
    }
    }
    return resultat;
    }
    
    public void MAJCasesControle(int[][] groupe, int numeroJoueur){
    //Cette fonction met a  joue la grille de controle pour indiquer que le groupe en ergument est maintenant possede par le joueur du numero fourni
    // On met aussi a  jour la couleur de ces cases.
    Joueur joueur=this.getJoueur1();
    if(numeroJoueur==2){
    joueur=this.getJoueur2();
    }
    if(numeroJoueur==3){
    joueur=this.getJoueur3();
    }
    if(numeroJoueur==4){
    joueur=this.getJoueur4();
    }
    for(int i=0;i<groupe.length;i++){
    casesControle[groupe[i][0]][groupe[i][1]]=numeroJoueur;
    cases[groupe[i][0]][groupe[i][1]]=joueur.getCouleur();
    }
    }
    */
}