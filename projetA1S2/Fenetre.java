package projetA1S2;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Fenetre extends JFrame{
	
	
	public Fenetre(boolean setVisible){
		super();
		
		build(setVisible);//On initialise notre fen�tre
	}

	private void build(boolean setVisible){
		setTitle("Projet A1 S2 Fran�ois ROBARD, Pierre-Philippe CORDIER"); //On donne un titre � l'application
		setSize(1000,700); //On donne une taille � notre fen�tre
		setLocationRelativeTo(null); //On centre la fen�tre sur l'�cran
		//setResizable(false); //On interdit la redimensionnement de la fen�tre

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit � l'application de se fermer lors du clic sur la croix

		setVisible(setVisible);//On la rend visible
	}
	

	
}
