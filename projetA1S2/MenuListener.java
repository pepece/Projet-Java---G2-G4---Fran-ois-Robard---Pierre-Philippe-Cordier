package projetA1S2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


/**
 * a priori inutile dans la version sans menu permet de gérer les inputs
 * 
 * de toutes manière incomplet ici, voir MenuListener du projet menu
 */
public class MenuListener extends JPanel implements ActionListener{

	JButton[] boutonMenu;
	Enregistreur piFenetre;
	Fenetre menu;

	public MenuListener(Fenetre menu, Enregistreur piFenetre){
		boutonMenu = new JButton[4];
		this.piFenetre = piFenetre;
		this.menu =menu;

	}

	public JButton[] getBoutonMenu(){
		return this.boutonMenu;
	}

	public void actionPerformed(ActionEvent e){
		// Check if the source of the event was the button
		if(e.getSource() == boutonMenu[0]){//np
			// Main.menu2(menu, piFenetre);
			// Jeu graphique
			Plateau grille2 = Main.initialisationJeuConsole();
			Main.iniSwing( 25, 50, 50 , grille2, piFenetre);
			piFenetre.getFenetre().getContentPane().revalidate();
			piFenetre.getFenetre().getContentPane().repaint();
			grille2.verifControleGlobal();
			grille2.affichageConsole();
			// grille2.annonceGagnantConsole();
		}
		else if (e.getSource() == boutonMenu[1]){ //charger

			piFenetre.getFenetre().getContentPane().removeAll();//ou remove(JComponent)
			Sauvegarde.chargementSauvegarde(piFenetre);
			piFenetre.getFenetre().getContentPane().revalidate();
			piFenetre.getFenetre().getContentPane().repaint();

		}
	}
}
