package projetA1S2;

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
    //suivent les fonctions de sauvegarde et de chargement de partie sauvegard�e
    
    /*
    cette fonction utilise le filedialog pour ouvrir une fen�tre Windows qui permet de parcourir les dossiers afin de
    choisir l'emplacement de la sauvegarde ainsi que le nom du fichier cr��
    Elle fait ensuite appel, si il n'y a pas d'erreur, � la fonction ecrireSauvegarde qui comme son nom l'indique �crit la
    sauvegarde.
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
    Cette fonction �crit dans le document txt du nom et de l'emplacement donn�e par le fonction sauvegarde,
    l'ensemble des informations n�cessaires � la re-cr�ation d'une partie gr�ce � un bufferwriter
    */
    private static void ecrireSauvegarde(Plateau grille,PieceImage PI,String filename,String cheminAcces){
        try {
            FileWriter fw = new FileWriter ( cheminAcces+filename ) ;
            BufferedWriter bw = new BufferedWriter ( fw ) ;
            PrintWriter pw = new PrintWriter ( bw ) ;
            //Ecriture d'un code de v�rification, qui permettra de v�rifier qu'un fichier texte ouvert
            pw.print("F5j3h3p-V[^F5lZ");
            bw.newLine();
            //Ecriture des informations concernant la pi�ce image
            pw.print(PI.getRayon()+","+PI.getXdepart()+","+PI.getYdepart());
            bw.newLine();
            //Ecritre des informations concernant le Plateau (sauf les tableaux qui seront �crits plus tard, afin de limiter au maximum le nombre de lignes � num�ro variable)
            pw.print(grille.getNbJoueur()+","+grille.getTypeTableau()+","+grille.getTaille()+","+grille.getFin()+","+grille.getObstacle()+","+grille.getJouant());
            bw.newLine();
            //Ecriture des informations concernant les joueurs
            //Joueur1
            pw.print(grille.getJoueur1().getNom()+","+grille.getJoueur1().getCouleur()+","+grille.getJoueur1().isIA());
            bw.newLine();
            //Joueur2
            pw.print(grille.getJoueur2().getNom()+","+grille.getJoueur2().getCouleur()+","+grille.getJoueur2().isIA());
            bw.newLine();
            //Joueur3 (si il est pr�sent)
            if(grille.getNbJoueur()==3 | grille.getNbJoueur()==4){
                pw.print(grille.getJoueur3().getNom()+","+grille.getJoueur3().getCouleur()+","+grille.getJoueur3().isIA());
                bw.newLine();
            }
            //Joueur4 (si il est pr�sent)
            if(grille.getNbJoueur()==4){
                pw.print(grille.getJoueur4().getNom()+","+grille.getJoueur4().getCouleur()+","+grille.getJoueur4().isIA());
                bw.newLine();
            }
            // Ecriture du premier tableau contenant les couleurs
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
            // Ecriture du second tableau contenant les possessions de chaque joueur
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
            System.out.println("La partie a bien �t� suvegardee.");
        }
        catch ( IOException e ) {
            System.out.println ( " Probl�me � l'�criture du fichier " ) ;
            System.exit(0);
        }
    }
    
    
    /*
    Cette fonction utilise le filedialog pour ouvrir une fen�tre Windows qui permet de parcourir les dossiers afin de
    choisir le fichier de sauvegarde � ouvrir
    Si il n'y a pas d'erreur elle lance la foncton LectureFichier qui lit les informations sur la partie dans le fichier de
    sauvegarde et ouvre une partie en utilisant ces informations
    */
    public static void chargementSauvegarde(Enregistreur piFenetre){
        FileDialog fd = new FileDialog(piFenetre.getFenetre(), "Choisissez un fichier txt de sauvegarde à charger :", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setFile("*.txt");
        fd.setVisible(true);
        fd.setAlwaysOnTop(true);
        String filename = fd.getFile();
        if(filename == null){
            System.out.print("Vous avez annul� la s�lection.");
            
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
            // Test: le fichier a-t-il bien �t� g�n�r�e par le jeu, c.a.d, y a ti'il ne sorte de code en premi�re ligne ?
            ligne=br.readLine();
            if(ligne.equals("F5j3h3p-V[^F5lZ")){
                // Lecture de la premi�re ligne concernant la Pi�ceImage
                ligne=br.readLine();
                String[] decompose = ligne.split(","); //D�composition de la ligne en tableau en consid�rant la virgule comme s�parateur
                int rayon=Integer.parseInt(decompose[0]);
                int Xdepart=Integer.parseInt(decompose[1]);
                int Ydepart=Integer.parseInt(decompose[2]);
                
                //Lecture de la deuxi�me ligne (contenant les attributs du Plateau � l'exeption de tableaux)
                ligne=br.readLine();
                decompose = ligne.split(","); //D�composition de la ligne en tableau en consid�rant la virgule comme s�parateur
                int nbJoueurs=Integer.parseInt(decompose[0]);
                int typeTableau=Integer.parseInt(decompose[1]);
                int taille=Integer.parseInt(decompose[2]);
                boolean fini=Boolean.parseBoolean(decompose[3]);
                boolean obstacle=Boolean.parseBoolean(decompose[4]);
                int jouant=Integer.parseInt(decompose[5]);
                
                //Lecture des 2 � 4 lignes suivantes concernant le joueurs
                // Création d' une liste de joueurs qui sera utilis�e pour permettre d'avoir uniquementun constructeur de plateau pour la reprise de partie sauvegard�e
                Joueur[] joueurs=new Joueur[nbJoueurs];
                //Joueur 1
                ligne=br.readLine();
                decompose = ligne.split(","); //D�composition de la ligne en tableau en consid�rant la virgule comme s�parateur
                Joueur A=new Joueur(decompose[0],1);
                A.setCouleur(Integer.parseInt(decompose[1]));
                A.setIsIA(Integer.parseInt(decompose[2]));
                joueurs[0]=A;
                //Joueur 2
                ligne=br.readLine();
                decompose = ligne.split(","); //D�composition de la ligne en tableau en consid�rant la virgule comme s�parateur
                Joueur B=new Joueur(decompose[0],2);
                B.setCouleur(Integer.parseInt(decompose[1]));
                B.setIsIA(Integer.parseInt(decompose[2]));
                joueurs[1]=B;
                //Joueur 3
                if(nbJoueurs==3 | nbJoueurs==4){
                    ligne=br.readLine();
                    decompose = ligne.split(","); //D�composition de la ligne en tableau en consid�rant la virgule comme s�parateur
                    Joueur C=new Joueur(decompose[0],3);
                    C.setCouleur(Integer.parseInt(decompose[1]));
                    C.setIsIA(Integer.parseInt(decompose[2]));
                    joueurs[2]=C;
                }
                //Joueur 3
                if(nbJoueurs==4){
                    ligne=br.readLine();
                    decompose = ligne.split(","); //D�composition de la ligne en tableau en consid�rant la virgule comme s�parateur
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
                
                // Cr�ation des �l�ments n�cessaires � la partie
                Plateau grille=new Plateau(nbJoueurs,typeTableau,taille,fini,obstacle,jouant,cases,casesControle,joueurs);
                Main.iniSwing(rayon,Xdepart,Ydepart,grille,piFenetre);
                piFenetre.getFenetre().revalidate();
                piFenetre.getFenetre().repaint();
                grille.verifControleGlobal();
                grille.affichageConsole();
                //deroulementPartieConsole(grille);
                Main.deroulementPartieGraphique(grille, piFenetre,jouant);
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