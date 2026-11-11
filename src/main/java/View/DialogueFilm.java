package View;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DialogueFilm extends JOptionPane{
    public DialogueFilm(){
    }
    public void filmIntrouvable(){
        showConfirmDialog(new JPanel(),"Film Introuvble");
    }
}
