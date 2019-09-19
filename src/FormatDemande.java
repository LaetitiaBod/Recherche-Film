
public class FormatDemande {
	
	private String titres[] = new String[15];
	private String realisateurs[][] = new String[15][15]; 
	private String acteurs[][] = new String[15][15];
	private String pays[] = new String [15];
	private int annee[] = new int[15];
	private int avant[][] = new int[15][15];
	private int apres[][] = new int[15][15];
	private String erreur = new String();
	
	public FormatDemande(String titres[], String realisateurs[][], String acteurs[][], String pays[], int annee[], int avant[][], int apres[][], String erreur) {
		this.titres = titres;
		this.realisateurs = realisateurs;
		this.acteurs = acteurs;
		this.pays = pays;
		this.annee = annee;
		this.avant = avant;
		this.apres = apres;	
		this.erreur = erreur;
	}
	
	public String[] getTitres() {	return titres;	}
	
	public void setTitres(String titres[]) {	this.titres = titres;}
	
	public String[][] getRealisateurs() {	return realisateurs;}
	
	public void setRealisateurs(String realisateurs[][]) {	this.realisateurs = realisateurs;}
	
	public String[][] getActeurs() {	return acteurs;	}
	
	public void setActeurs(String acteurs[][]) {	this.acteurs = acteurs; }
	
	public String[] getPays() {	return pays;}
	
	public void setPays(String pays[]) {	this.pays = pays;}
	
	public int[] getAnnee() {	return annee;}
	
	public void setAnnee(int annee[]) {	this.annee = annee;	}
	
	public int[][] getAvant() {	return avant;}
	
	public void setAvant(int avant[][]) {	this.avant = avant;	}
	
	public int[][] getApres() {	return apres;}
	
	public void setApres(int apres[][]) {	this.apres = apres;	}
	
	public String getErreur() { return erreur; }
	
	public void setErreur(String erreur) { this.erreur = erreur; }
}
