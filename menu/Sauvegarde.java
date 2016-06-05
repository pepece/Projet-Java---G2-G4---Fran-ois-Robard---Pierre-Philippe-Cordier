package menu;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Sauvegarde {
    //suivent les fonctions de sauvegarde et de chargement de partie sauvegardée
    
    /*
    Cette fonction utilise le filedialog pour ouvrir une fenetre Windows qui permet de parcourir les dossiers afin de
    choisir l'emplacement de la sauvegarde ainsi que le nom du fichier créé
    Si il n'y a pas d'erreur elle fait ensuite appel à la fonction EcrireSauvegarde qui ecrit les infomations de sauvegarde
    dans un fichier txt
    */
    public static void sauvegarde(Plateau grille,PieceImage PI){
        grille.setVeutEnregistrer(false);
        FileDialog fd = new FileDialog(PI.getFenetre(), "Nommez un fichier txt pour la sauvegarde :", FileDialog.SAVE);
        fd.setDirectory("C:\\");
        fd.setFile("JeuDes6Couleurs.txt");
        fd.setVisible(true);
        fd.setAlwaysOnTop(true);
        String filename = fd.getFile();
        if(filename == null){
            System.out.print("Vous avez annulé la sauvegarde.");
            
        }
        else{
            ecrireSauvegarde(grille,PI,filename,fd.getDirectory());
        }
    }
    
    /*
    Cette fonction écrit dans le document txt du nom et de l'emplacement donnee par le fonction sauvegard, l'ensemble des informations 
    necessaires a la re-creation d'une partie grace a un bufferwriter
    */
    private static void ecrireSauvegarde(Plateau grille,PieceImage PI,String filename,String cheminAcces){
        try {
            FileWriter fw = new FileWriter ( cheminAcces+filename ) ;
            BufferedWriter bw = new BufferedWriter ( fw ) ;
            PrintWriter pw = new PrintWriter ( bw ) ;
            //Ecriture d'un code de vérification, qui permettra de vérifier qu'un fichier texte ouvert
            pw.print("F5j3h3p-V[^F5lZ");
            bw.newLine();
            //Ecriture des informations concernant la piece image
            pw.print(PI.getRayon()+","+PI.getXdepart()+","+PI.getYdepart());
            bw.newLine();
            //Ecritre des informations concernant le Plateau (sauf les tableaux qui seront écrits plus tard, afin de limiter au maximum le nombre de lignes à numéro variable)
            pw.print(grille.getNbJoueur()+","+grille.getTypeTableau()+","+grille.getTaille()+","+grille.getFin()+","+grille.getObstacle()+","+grille.getJouant());
            bw.newLine();
            //Ecriture des informations concernant les joueurs
            //Joueur1
            pw.print(grille.getJoueur1().getNom()+","+grille.getJoueur1().getCouleur()+","+grille.getJoueur1().isIA());
            bw.newLine();
            //Joueur2
            pw.print(grille.getJoueur2().getNom()+","+grille.getJoueur2().getCouleur()+","+grille.getJoueur2().isIA());
            bw.newLine();
            //Joueur3
            if(grille.getNbJoueur()==3 | grille.getNbJoueur()==4){
                pw.print(grille.getJoueur3().getNom()+","+grille.getJoueur3().getCouleur()+","+grille.getJoueur3().isIA());
                bw.newLine();
            }
            //Joueur4
            if(grille.getNbJoueur()==4){
                pw.print(grille.getJoueur4().getNom()+","+grille.getJoueur4().getCouleur()+","+grille.getJoueur4().isIA());
                bw.newLine();
            }
            int[][] tab=grille.getCases();
            for(int i=0;i<tab.length;i++){
                for(int j=0;j<tab[0].length;j++){
                    pw.print(tab[i][j]);
                    if(j<tab[0].length-1){
                        pw.print(",");
                    }
                }
                bw.newLine();
            }
            tab=grille.getCasesControle();
            for(int i=0;i<tab.length;i++){
                for(int j=0;j<tab[0].length;j++){
                    pw.print(tab[i][j]);
                    if(j<tab[0].length-1){
                        pw.print(",");
                    }
                }
                bw.newLine();
            }
            pw. close ( ) ;
            System.out.println("La partie a bien ete sauvegardee.");
        }
        catch ( IOException e ) {
            System.out.println ( " Problème à l'écriture du fichier " ) ;
            System.exit(0);
        }
    }
    
    
    /*
    Cette fonction utilise le filedialog pour ouvrir une fenetre Windows qui permet de parcourir les dossiers afin de
    choisir le fichier de sauvegarde à ouvrir
    Si il n'y a pas d'erreur elle lance ensuite la fonction lecture fichier qui lit les informations depuis le fichier
    de sauvegarde et ouvre la partie correspondante
    */
    public static void chargementSauvegarde(Enregistreur piFenetre){
        FileDialog fd = new FileDialog(piFenetre.getFenetre(), "Choisissez un fichier txt de sauvegarde Ã  charger :", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setFile("*.txt");
        fd.setVisible(true);
        fd.setAlwaysOnTop(true);
        String filename = fd.getFile();
        if(filename == null){
            System.out.print("Vous avez annulÃ© la sÃ©lection.");
            
        }
        else{
            lectureFichier(filename,fd.getDirectory(), piFenetre);
        }
    }
    
    private static void lectureFichier(String filename,String cheminAcces, Enregistreur piFenetre){
        try{
            InputStream ips=new FileInputStream(cheminAcces+filename); // Ouverture du fichier
            InputStreamReader ipsr=new InputStreamReader(ips); // Overture du mode lecture
            BufferedReader br=new BufferedReader(ipsr); // Onverture du buffered reader
            String ligne;
            // Test: le fichier a-t-il bien été générée par le jeu, c.a.d, y a ti'il ne sorte de code en première ligne ?
            ligne=br.readLine();
            if(ligne.equals("F5j3h3p-V[^F5lZ")){
                // Lecture de la premiere ligne concernant la PieceImage
                ligne=br.readLine();
                String[] decompose = ligne.split(","); //Décomposition de la ligne en tableau en considérant la virgule comme séparateur
                int rayon=Integer.parseInt(decompose[0]);
                int Xdepart=Integer.parseInt(decompose[1]);
                int Ydepart=Integer.parseInt(decompose[2]);
                
                //Lecture de la deuxième ligne (contenant les attributs du Plateau à l'exeption de tableaux)
                ligne=br.readLine();
                decompose = ligne.split(","); //Décomposition de la ligne en tableau en considérant la virgule comme séparateur
                int nbJoueurs=Integer.parseInt(decompose[0]);
                int typeTableau=Integer.parseInt(decompose[1]);
                int taille=Integer.parseInt(decompose[2]);
                boolean fini=Boolean.parseBoolean(decompose[3]);
                boolean obstacle=Boolean.parseBoolean(decompose[4]);
                int jouant=Integer.parseInt(decompose[5]);
                
                //Lecture des 2 à 4 lignes suivantes concernant le joueurs
                // Création d' une liste de joueurs qui sera utilisée pour permettre d'avoir uniquementun constructeur de plateau pour la reprise de partie sauvegardée
                Joueur[] joueurs=new Joueur[nbJoueurs];
                //Joueur 1
                ligne=br.readLine();
                decompose = ligne.split(","); //Décomposition de la ligne en tableau en considérant la virgule comme séparateur
                Joueur A=new Joueur(decompose[0],1);
                A.setCouleur(Integer.parseInt(decompose[1]));
                A.setIsIA(Integer.parseInt(decompose[2]));
                joueurs[0]=A;
                //Joueur 2
                ligne=br.readLine();
                decompose = ligne.split(","); //Décomposition de la ligne en tableau en considÃ©rant la virgule comme séparateur
                Joueur B=new Joueur(decompose[0],2);
                B.setCouleur(Integer.parseInt(decompose[1]));
                B.setIsIA(Integer.parseInt(decompose[2]));
                joueurs[1]=B;
                //Joueur 3
                if(nbJoueurs==3 | nbJoueurs==4){
                    ligne=br.readLine();
                    decompose = ligne.split(","); //Décomposition de la ligne en tableau en considÃ©rant la virgule comme séparateur
                    Joueur C=new Joueur(decompose[0],3);
                    C.setCouleur(Integer.parseInt(decompose[1]));
                    C.setIsIA(Integer.parseInt(decompose[2]));
                    joueurs[2]=C;
                }
                //Joueur 3
                if(nbJoueurs==4){
                    ligne=br.readLine();
                    decompose = ligne.split(","); //Décomposition de la ligne en tableau en considérant la virgule comme séparateur
                    Joueur D=new Joueur(decompose[0],4);
                    D.setCouleur(Integer.parseInt(decompose[1]));
                    D.setIsIA(Integer.parseInt(decompose[2]));
                    joueurs[3]=D;
                }
                
                //Lecture des deux tableaux
                int[][] cases=new int[taille][taille];
                for(int i=0;i<taille;i++){
                    ligne=br.readLine();
                    decompose = ligne.split(",");
                    for(int j=0;j<taille;j++){
                        cases[i][j]=Integer.parseInt(decompose[j]);
                    }
                }
                int[][] casesControle=new int[taille][taille];
                for(int i=0;i<taille;i++){
                    ligne=br.readLine();
                    decompose = ligne.split(",");
                    for(int j=0;j<taille;j++){
                        casesControle[i][j]=Integer.parseInt(decompose[j]);
                    }
                }
                br.close();
                
                // Création des éléments nécessaires à la partie
                Plateau grille=new Plateau(nbJoueurs,typeTableau,taille,fini,obstacle,jouant,cases,casesControle,joueurs);
                Menu.iniSwing(rayon,Xdepart,Ydepart,grille,piFenetre);
                piFenetre.getFenetre().revalidate();
                piFenetre.getFenetre().repaint();
                grille.verifControleGlobal();
                grille.affichageConsole();
                //deroulementPartieConsole(grille);
                Menu.deroulementPartieGraphique(grille, piFenetre,jouant);
                grille.annonceGagnantConsole();
            }
            else{
                System.out.println("Le fichier txt choisi n'est pas valable");
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
}