package projetA1S2;
/*
Cette classe implémente une pile de coordonnées,
c'est à  dire une pile constituée d'une liste contenant elle même des liste de deux valeurs correspondant à  des coordonnées
Elle permettra dans le code de stocker des listes de coordonnÃ©es pour diverses applications sans avoir à  se préoccuper
de la taille du tableau les contenant
*/

public class PileCoord {
    private int[][] pile_coord;
    
//Constructeur
    
    public PileCoord(int[][] pile_coord){
        this.pile_coord=pile_coord;
    }
    
    

//Definition des méthodes
    
    
    /*
    Permet d'allonger la pile d'une valeur et de rajouter la valeur en paramètre à  la fin de la pile
    */
    public void add(int[] coord){
        int L=this.pile_coord.length;
        int[][] tab=new int[L+1][2];
        for(int i=0;i<L;i++){
            tab[i]=this.pile_coord[i];
        }
        tab[L]=coord;
        this.pile_coord=tab;
    }
    
    
    /*
    Retourne le premier élément de la pile et raccourci la pile d'une valeur en enlevant sa première case
    */
    public int[] take(){
        int[] val=this.pile_coord[0];
        int L=this.pile_coord.length;
        int[][] tab=new int[L-1][2];
        for(int i=0;i<L-1;i++){
            tab[i]=this.pile_coord[i+1];
        }
        this.pile_coord=tab;
        return val;
    }
    
    
    /*
    Permet de lire une valeur dans une pile à  partir de sa coordonnée
    */
    public int[] read(int val){
        return this.pile_coord[val];
    }
    
    
    /*
    Permet d'afficher la pile dand la console (utile pour les tests de fonctions)
    */
    public void afficher_pile(){
        for(int i=0;i<this.pile_coord.length;i++){
            System.out.print(this.pile_coord[i][0]+","+this.pile_coord[i][1] + "  ;  " );
        }
    }
    
    
    /*
    Permet l'ajout d'une case supplémentaire à  une pile en y plaçant le valeur fournie en paramètre
    mais seulement si cette valeur ne se trouve pas déjà  dans la liste.
    */
    public void addVerif(int[] coord){
        boolean ok=true;
        for(int i=0;i<this.pile_coord.length;i++){
            if(coord[0]==this.pile_coord[i][0] & coord[1]==this.pile_coord[i][1]){
                ok=false;
            }
        }
        if(ok){
            this.add(coord);
        }
    }
    
    
    /*
    Fonction qui permet d'obtenir la taille d'une pile de coordonnées
    */
    public int getPileCoordLength(){
        return this.pile_coord.length;
    }
    
    
    /*
    retourne le tableau qui constitue une pile (afin de pouvoir l'analyser dans une fonction par exemple)
    */
    public int[][] getPileCoord(){
        return this.pile_coord;
    }
    
}
