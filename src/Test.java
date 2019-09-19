import java.util.Scanner;

public class Test {
 
    public static void main(String[] args) {
    	RechercheFilm recherche = new RechercheFilm("bdfilm.sqlite");
    	Scanner scan = new Scanner(System.in);
    	String demande = scan.nextLine();
    	scan.close();
    	recherche.retrouve(demande);
        
        
    }
 
}