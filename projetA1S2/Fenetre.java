package projetA1S2;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Fenetre extends JFrame{
	
	
	public Fenetre(boolean setVisible){
		super();
		
		build(setVisible);//On initialise notre fenêtre
	}

	private void build(boolean setVisible){
		setTitle("Projet A1 S2 François ROBARD, Pierre-Philippe CORDIER"); //On donne un titre à l'application
		setSize(1000,700); //On donne une taille à notre fenêtre
		setLocationRelativeTo(null); //On centre la fenêtre sur l'écran
		//setResizable(false); //On interdit la redimensionnement de la fenêtre

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit à l'application de se fermer lors du clic sur la croix

		setVisible(setVisible);//On la rend visible
	}
	

	
}
