import java.util.*;
import java.sql.*;

/**
 * Recherche la liste des films correspondant a la demande de l'utilisateur
 * @author Laetitia
 *
 */
public class RechercheFilm {
	
	String nomFicherSQLite;
	BDDConnexion bdd;

	/**
	 * Constructeur
	 * @param nomFicherSQLite nom de la base de donnees
	 */
	public RechercheFilm(String nomFicherSQLite) {
		this.nomFicherSQLite = nomFicherSQLite;
		bdd = new BDDConnexion(this.nomFicherSQLite);
		bdd.connect();
	}
	
	/**
	 * 
	 * Ferme la base de donnees
	 */
	public void fermeBase() {
		bdd.close();
	}
	
	
	
	
	
	/**
	 * Segmente la requete de l'utilisateur
	 * Transforme la requete en requete sql
	 * Range les resultats de la requete sql dans un ArrayList
	 * Affiche les resultats de la recherche sous forme d'objet json
	 * 
	 * @param requete de l'utlisateur
	 * @return sous forme de liste ce que l'utilisateur a demande
	 */
	public String retrouve(String requete) {
		
		StringTokenizer st = new StringTokenizer(requete);
		String tab[] = new String[100];
		for(int p=0; p<tab.length-1; p++) {
			tab[p] = "";
		}
		
		int i = 0;
		
		//la requete de l'utilisateur est segmentee
		while (st.hasMoreElements()) {
			tab[i] = st.nextElement().toString();
			//System.out.println(tab[i]);
			i++;
		}
		
		FormatDemande infos = formatDemande(requete, tab);
		
		String sql = RequeteSQL(infos, tab);
		//System.out.println(sql);
		ArrayList<InfoFilm> list = getResults(sql);
		
		System.out.println(convertToJSON(list));
		return convertToJSON(list);
	}
	
	
	
	
	/**
	 * Initialise les tableaux qui contiendront les informations de la requete utilisateur
	 * Execute une analyse de la requete
	 *  
	 * @param requete de l'utilisateur
	 * @return un objet infofilm incomplet qui correspond a la recherche
	 */
	FormatDemande formatDemande(String requete, String tab[]) {

		String titres[] = new String[15];
		for(int p=0; p<titres.length-1; p++) {
			titres[p] = "";
		}
		
		String realisateurs[][] = new String[15][15];
		for(int p=0; p<realisateurs.length-1; p++) {
			for(int q=0; q<realisateurs.length-1; q++) {
				realisateurs[p][q] = "";
			}
		}
		
		String acteurs[][] = new String[15][15];
		for(int p=0; p<acteurs.length-1; p++) {
			for(int q=0; q<acteurs.length-1; q++) {
				acteurs[p][q] = "";
			}
		}
		
		String pays[] = new String [15];
		for(int p=0; p<pays.length-1; p++) {
			pays[p] = "";
		}
		
		int annee[] = new int[15];
		for(int p=0; p<annee.length-1; p++) {
			annee[p] = 0;
		}
		
		int avant[][] = new int[15][15];
		for(int p=0; p<avant.length-1; p++) {
			for(int q=0; q<avant.length-1; q++) {
				avant[p][q] = 0;
			}
		}
		
		int apres[][] = new int[15][15];
		for(int p=0; p<apres.length-1; p++) {
			for(int q=0; q<apres.length-1; q++) {
				apres[p][q] = 0;
			}
		}
		
		String erreur = "";
		
		FormatDemande demande = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
		
		//analyser tab[] pour compléter FormatDemande
		analyse(tab, 0, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
		
		return demande;
	}
		
	
	
	/**
	 * Analyse la synthaxe de la requete utilisateur
	 * 
	 * 
	 * @param tab : contient la requete utilisateur
	 * @param indice : "curseur" qui avant tout le long de la requete
	 * @param demande : objet qui contient toute les informations de la requete
	 * @param titres : tableau qui contient la liste des titre
	 * @param realisateurs : tableau a deux dimensions qui contient la liste des realisateurs
	 * @param acteurs : tableau a deux dimensions qui contient la liste des acteurs
	 * @param pays : tableau qui contient la liste des pays
	 * @param annee : tableau qui contient la liste des annees de sortie
	 * @param avant : tableau a deux dimensions qui contient la liste des annees de sorties anterieurs
	 * @param apres : tableau a deux dimensions qui contient la liste des annees de sorties ulterieures
	 * @param erreur : String qui contient en objet json une erreur
	 */
	void analyse(String tab[], int indice, FormatDemande demande, String titres[], String realisateurs[][], String acteurs[][], String pays[], int annee[], int avant[][], int apres[][], String erreur) {
			
			
		if(tab[indice].equalsIgnoreCase("TITRE")) {
			
			
			int k=indice+1;
			int o=0;
			int unique = 0;
			
			for(int i=0; i<tab.length-1; i++) {
				if(tab[i].equalsIgnoreCase("TITRE")) {
					unique++;
				}
			}		

			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"titre\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}
				System.out.println("Vous avez rentrer un(des) titre(s)");
				
			while(tab[k] != null) {
						
				if(tab[k].endsWith(",")) {
					
					if(unique>=2) { 
						erreur = "Un film n'a pas plusieurs titres. Veuillez utiliser l'operateur 'ou'.";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);	
					}
					
					if(tab[k+1].equalsIgnoreCase("DE") 
						|| tab[k+1].equalsIgnoreCase("AVEC") 
						|| tab[k+1].equalsIgnoreCase("PAYS") 
						|| tab[k+1].equalsIgnoreCase("EN") 
						|| tab[k+1].equalsIgnoreCase("AVANT") 
						|| tab[k+1].equalsIgnoreCase("APRES") 
						|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						
						titres[o] += tab[k].substring(0, tab[k].length()-1);
						
						/*for(int i=0; i<titres.length-1; i++) {
							System.out.println(titres[i]);
						}*/
							
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("TITRE")) {
						erreur = "Un film n'a pas plusieurs titres. Veuillez utiliser l'operateur 'ou'.";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					titres[o] += tab[k]+" ";
					k++;
				} else if(tab[k].equalsIgnoreCase("ou")) {
					o++;
					if(tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
							
							/*for(int i=0; i<titres.length-1; i++) {
								System.out.println(titres[i]);
							}*/
								
							analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
							break;
					} else if(tab[k+1].equalsIgnoreCase("TITRE")) {
						k+= 2;
						unique -=1;
					} else {
						k++;	
					}
				} else {
					
					titres[o] += tab[k]+" ";
					k++;
				}	
			}
			
			/*for(int i=0; i<titres.length-1; i++) {
				System.out.println(titres[i]);
			}*/
			
		} else if(tab[indice].equalsIgnoreCase("DE")) {
			
			
			System.out.println("vous avez rentrer un(des) realisateur(s)");
			
			int k = indice+1;
			int o = 0;
			int p = 0;

			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"de\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}
						
			while(tab[k] != null && tab[k] != "") {
				
				if(tab[k].endsWith(",")) {
					
					realisateurs[o][p] += tab[k].substring(0, tab[k].length()-1);
					o++;
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
						|| tab[k+1].equalsIgnoreCase("AVEC") 
						|| tab[k+1].equalsIgnoreCase("PAYS") 
						|| tab[k+1].equalsIgnoreCase("EN") 
						|| tab[k+1].equalsIgnoreCase("AVANT") 
						|| tab[k+1].equalsIgnoreCase("APRES") 
						|| tab[k+1].equalsIgnoreCase("APRÈS")) {
							
						/*for(int i=0; i<realisateurs.length-1; i++) {
							for(int l=0; l<realisateurs.length-1; l++) {
								System.out.print(realisateurs[i][l]+" ");
							}
							System.out.print("\n");
						}*/
						
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("DE")) {
						k++;
					}
			
				} else if(tab[k].equalsIgnoreCase("ou")) {
					
					p++;
					
					if(tab[k+1].equalsIgnoreCase("TITRE")  
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
								
							/*for(int i=0; i<realisateurs.length-1; i++) {
								for(int l=0; l<realisateurs.length-1; l++) {
									System.out.print(realisateurs[i][l]+" ");
								}
								System.out.print("\n");
							}*/	
							
							analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
							break;
						} else if(tab[k+1].equalsIgnoreCase("DE")) {
							k++;
						}
				
				}  else {
					realisateurs[o][p] += tab[k]+" ";
				}
				k++;
			}
			
			/*for(int i=0; i<realisateurs.length-1; i++) {
				for(int l=0; l<realisateurs.length-1; l++) {
					System.out.print(realisateurs[i][l]+" ");
				}
				System.out.print("\n");
			}*/
			
		
		} else if(tab[indice].equalsIgnoreCase("AVEC")) {
		
			
			
			System.out.println("vous avez rentrer un(des) acteur(s)");
			
			int k = indice+1;
			int o = 0;
			int p = 0;
			
			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"avec\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}
						
			while(tab[k] != null && tab[k] != "") {
				
				if(tab[k].startsWith("mc")) { 
					tab[k].replaceAll("(.*)mc(.*)", "(.*)Mac(.*)");
				}
				
				if(tab[k].endsWith(",")) {
						
					acteurs[o][p] += tab[k].substring(0, tab[k].length()-1);
					o++;
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
						|| tab[k+1].equalsIgnoreCase("DE") 
						|| tab[k+1].equalsIgnoreCase("PAYS") 
						|| tab[k+1].equalsIgnoreCase("EN") 
						|| tab[k+1].equalsIgnoreCase("AVANT") 
						|| tab[k+1].equalsIgnoreCase("APRES") 
						|| tab[k+1].equalsIgnoreCase("APRÈS")) {
							
						//for(int i=0; i<acteurs.length-1; i++) {
						//	for(int l=0; l<acteurs.length-1; l++) {
						//		System.out.print(acteurs[i][l]+" ");
						//	}
						//	System.out.print("\n");
						//}	
						
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("AVEC")) {
						k++;
					}
			
				} else if(tab[k].equalsIgnoreCase("ou")) {
					
					p++;
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
								
							/*for(int i=0; i<realisateurs.length-1; i++) {
								for(int l=0; l<realisateurs.length-1; l++) {
									System.out.print(realisateurs[i][l]+" ");
								}
								System.out.print("\n");
							}*/	
							
							analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
							break;
					} else if(tab[k+1].equalsIgnoreCase("AVEC")) {
						k++;
					}
				
				} else {
				
					acteurs[o][p] += tab[k]+" ";
				}
				k++;
			}
			
			//for(int i=0; i<acteurs.length-1; i++) {
			//	for(int l=0; l<acteurs.length-1; l++) {
			//		System.out.print(acteurs[i][l]+" ");
			//	}
			//	System.out.print("\n");
			//}
		
		
			//pas d'operateur 'et' car un film ne vient pas de deux pays differents 
			
		} else if(tab[indice].equalsIgnoreCase("PAYS")) {
			
			int k=indice+1;
			int o=0;
			int unique = 0;
			
			for(int i=0; i<tab.length-1; i++) {
				if(tab[i].equalsIgnoreCase("PAYS")) {
					unique++;
				}
			}		
			
			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"pays\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}
			
			System.out.println("Vous avez rentrer un(des) pays(s)");
				
			while(tab[k] != null && tab[k] != "") {
						
				if(tab[k].endsWith(",")) {
					if(unique >=2) {
						erreur = "Un film n'a pas plusieurs pays. Veuillez utiliser l'operateur 'ou'.";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
						|| tab[k+1].equalsIgnoreCase("DE") 
						|| tab[k+1].equalsIgnoreCase("AVEC") 
						|| tab[k+1].equalsIgnoreCase("EN") 
						|| tab[k+1].equalsIgnoreCase("AVANT") 
						|| tab[k+1].equalsIgnoreCase("APRES") 
						|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						
						pays[o] +=  tab[k].substring(0, tab[k].length()-1);
							
						/*for(int i=0; i<pays.length-1; i++) {
							System.out.println(pays[i]);
						}*/
							
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} 
					
					
				} else if(tab[k].equalsIgnoreCase("ou")) {
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC")  
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
								
						/*for(int i=0; i<pays.length-1; i++) {
							System.out.println(pays[i]);
						}*/
							
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("PAYS")) {
						k+=2;
						unique -=1;
					} else {
						k++;
					}
				} else {
					
					pays[o] += tab[k]+" ";
					k++;
					o++;
				}	
			}
			
			/*for(int i=0; i<pays.length-1; i++) {
				System.out.println(pays[i]);
			}*/
			
			
		//pas d'operateur 'et' car un film n'a pas deux annees de sorties
		} else if(tab[indice].equalsIgnoreCase("EN")) {
			
			
			int k = indice+1;
			int o = 0;
			int unique = 0;
			
			for(int i=0; i<tab.length-1; i++) {
				if(tab[i].equalsIgnoreCase("EN")) {
					unique++;
				}
			}
			
			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"en\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}

			System.out.println("Vous avez rentrer une(des) annee(s) de sortie");
			
			while(tab[k] != null && tab[k] != "") {
				
				
				if(tab[k].endsWith(",")) {
					if(unique>=2) { 
						erreur = "Un film n'a pas plusieurs annees de sorties. Veuillez utiliser l'operateur 'ou'.";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);	
					}
					try {
						annee[o] = Integer.parseInt(tab[k].substring(0, tab[k].length()-1));
					} catch (NumberFormatException e) {
						erreur = tab[k]+" n'est pas un entier";
						o--;
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
						|| tab[k+1].equalsIgnoreCase("DE") 
						|| tab[k+1].equalsIgnoreCase("AVEC") 
						|| tab[k+1].equalsIgnoreCase("PAYS")
						|| tab[k+1].equalsIgnoreCase("AVANT") 
						|| tab[k+1].equalsIgnoreCase("APRES") 
						|| tab[k+1].equalsIgnoreCase("APRÈS")) {
							
						/*for(int i=0; i<annee.length-1; i++) {
							System.out.println(annee[i]);
						}*/
							
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("EN")) {
						erreur = "Un film n'a pas plusieurs annees de sorties. Veuillez utiliser l'operateur 'ou'.";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);	
					} else {
						erreur = "Un film n'a pas plusieurs annees de sorties. Veuillez utiliser l'operateur 'ou'.";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);	
					}
				
				} else if(tab[k].equalsIgnoreCase("ou")) {
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
								
						/*for(int i=0; i<annee.length-1; i++) {
							System.out.println(annee[i]);
						}*/	
							
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("EN")) {
						k+=2;	
						unique -=1;
					} else {
						k++;
					}
				
				} else if(tab[k] != "") {
					
					try {
						annee[o] = Integer.parseInt(tab[k]);
					} catch (NumberFormatException e) {
						erreur = tab[k]+" n'est pas un entier";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					k++;
					o++;
				}
			}

			/*for(int i=0; i<annee.length-1; i++) {
				System.out.println(annee[i]);
			}*/
				
		
		} else if(tab[indice].equalsIgnoreCase("AVANT")) {
			
			
			System.out.println("vous avez rentrer une(des) annee(s) maximum");
			
			
			int k = indice+1;
			int o = 0;
			int p = 0;
			
			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"avant\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}
						
			while(tab[k] != null && tab[k] != "") {

				if(tab[k].endsWith(",")) {
					
					try {
						avant[o][p] = Integer.parseInt(tab[k].substring(0, tab[k].length()-1));
						
					} catch (NumberFormatException e) {
						erreur = tab[k]+" n'est pas un entier";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					o++;
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
						|| tab[k+1].equalsIgnoreCase("DE") 
						|| tab[k+1].equalsIgnoreCase("AVEC") 
						|| tab[k+1].equalsIgnoreCase("PAYS") 
						|| tab[k+1].equalsIgnoreCase("EN")
						|| tab[k+1].equalsIgnoreCase("APRES") 
						|| tab[k+1].equalsIgnoreCase("APRÈS")) {
							
						/*for(int i=0; i<avant.length-1; i++) {
							for(int l=0; l<avant.length-1; l++) {
								System.out.print(avant[i][l]+" ");
							}
							System.out.print("\n");
						}*/	
						
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("AVANT")) {
						k+=2;
					}
			
				} else if(tab[k].equalsIgnoreCase("ou")) {
					
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
								
							/*for(int i=0; i<avant.length-1; i++) {
								for(int l=0; l<avant.length-1; l++) {
									System.out.print(avant[i][l]+" ");
								}
								System.out.print("\n");
							}*/
							
							analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
							break;
					} else if(tab[k+1].equalsIgnoreCase("AVANT")) {
						
						k+=2;
					}
				
				} else if(tab[k] != ""){
					try {
						avant[o][p] = Integer.parseInt(tab[k]);
					} catch (NumberFormatException e) {
						erreur = tab[k]+" n'est pas un entier";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					p++;
				}
				k++;
			}
			
			/*for(int i=0; i<avant.length-1; i++) {
				for(int l=0; l<avant.length-1; l++) {
					System.out.print(avant[i][l]+" ");
				}
				System.out.print("\n");
			}*/
		
		} else if(tab[indice].equalsIgnoreCase("APRES") || tab[indice].equalsIgnoreCase("APRÈS")) {
			
			System.out.println("vous avez rentrer une(des) annee(s) minimum");
			
			
			int k = indice+1;
			int o = 0;
			int p = 0;
			
			if(tab[k].equals("") || tab[k].equals(null)) {
				erreur = "Aucune valeure entree apres le mot-cle \"apres\"";
				FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
				String sql = RequeteSQL(infos, tab);
				System.out.println(sql);
				System.exit(0);
			}
						
			while(tab[k] != null && tab[k] != "") {

				if(tab[k].endsWith(",")) {
					
					try {
						apres[o][p] = Integer.parseInt(tab[k].substring(0, tab[k].length()-1));
						
					} catch (NumberFormatException e) {
						erreur = tab[k]+" n'est pas un entier";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					o++;
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
						|| tab[k+1].equalsIgnoreCase("DE") 
						|| tab[k+1].equalsIgnoreCase("AVEC") 
						|| tab[k+1].equalsIgnoreCase("PAYS") 
						|| tab[k+1].equalsIgnoreCase("EN")
						|| tab[k+1].equalsIgnoreCase("AVANT")) {
							
						/*for(int i=0; i<apres.length-1; i++) {
							for(int l=0; l<apres.length-1; l++) {
								System.out.print(apres[i][l]+" ");
							}
							System.out.print("\n");
						}*/	
						
						analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						break;
					} else if(tab[k+1].equalsIgnoreCase("APRES") || tab[k+1].equalsIgnoreCase("APRÈS")) {
						k+=2;
					}
			
				} else if(tab[k].equalsIgnoreCase("ou")) {
					
					
					if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN")  
							|| tab[k+1].equalsIgnoreCase("AVANT")) {
								
							/*for(int i=0; i<apres.length-1; i++) {
								for(int l=0; l<apres.length-1; l++) {
									System.out.print(apres[i][l]+" ");
								}
								System.out.print("\n");
							}*/
							
							analyse(tab, k+1, demande, titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
							break;
					} else if(tab[k+1].equalsIgnoreCase("APRÈS") || tab[k+1].equalsIgnoreCase("APRES")) {
						
						k+=2;
					}
				
				} else if(tab[k] != ""){
					try {
						apres[o][p] = Integer.parseInt(tab[k]);
					} catch (NumberFormatException e) {
						erreur = tab[k]+" n'est pas un entier";
						FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
						String sql = RequeteSQL(infos, tab);
						System.out.println(sql);
						System.exit(0);
					}
					p++;
				}
				k++;
			}
			
			/*for(int i=0; i<apres.length-1; i++) {
				for(int l=0; l<apres.length-1; l++) {
					System.out.print(apres[i][l]+" ");
				}
				System.out.print("\n");
			}*/
				
		} else {
			erreur = "Synthaxe incorrecte : "+tab[indice]+" n'est pas un mot cle\n";
			FormatDemande infos = new FormatDemande(titres, realisateurs, acteurs, pays, annee, avant, apres, erreur);
			String sql = RequeteSQL(infos, tab);
			System.out.println(sql);
			System.exit(0);
		}
	}
	
	
	/**
	 * Cree la partie de la requete sql concernant les titres
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String titres(FormatDemande infos, String tab[], String sql) {
		String titres[] = infos.getTitres();
		
		
		if(titres[0] != null && titres[0] != "") {
			sql += "(";
			for(int i=0; i<titres.length-1; i++) {
				if(titres[i] != null && titres[i] != "") {				
					sql += "(f.id_film IN (SELECT id_film from recherche_titre rt WHERE rt.titre LIKE \"%"+titres[i].replaceFirst(".",(titres[i].charAt(0)+"").toUpperCase()).trim().replaceAll(" ", "%")+"%\")) OR ";	
				}
			}
			sql = sql.substring(0, sql.length()-4);
			sql += ")";
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("TITRE")) {
					
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}			
		}
		return sql;
	}
	
	/**
	 * Cree la partie de la requete sql concernant les realisateurs
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String realisateurs(FormatDemande infos, String tab[], String sql) {

		String realisateurs[][] = infos.getRealisateurs();
		
		if(realisateurs[0][0] != null && realisateurs[0][0] != "") {
			
			int i=0;
			int j=0;
				
			
			do {
		
				sql += "((f.id_film IN (SELECT id_film FROM personnes NATURAL JOIN generique WHERE (prenom_sans_accent || ' ' || nom_sans_accent LIKE \"%"+realisateurs[i][j].replaceAll(" ", "%")+"%\" OR nom_sans_accent || ' ' || prenom_sans_accent LIKE \"%"+realisateurs[i][j].replaceAll(" ", "%")+"%\" 	OR nom_sans_accent LIKE \"%"+realisateurs[i][j].replaceAll(" ", "%")+"%\") 	AND role = 'R'))"; //debut du OU
				while(!realisateurs[i][j+1].equals("") && !realisateurs[i][j+1].equals(null)) {
					sql += " OR  (f.id_film IN (SELECT id_film FROM personnes NATURAL JOIN generique WHERE (prenom_sans_accent || ' ' || nom_sans_accent LIKE \"%"+realisateurs[i][j+1].replaceAll(" ", "%")+"%\" OR nom_sans_accent || ' ' || prenom_sans_accent LIKE \"%"+realisateurs[i][j+1].replaceAll(" ", "%")+"%\"	OR nom_sans_accent LIKE \"%"+realisateurs[i][j+1].replaceAll(" ", "%")+"%\")	AND role = 'R'))";
					j++;
				}
				sql += ")"; //fin du OU
			
				//debut du AND
				if(!realisateurs[i+1][j].equals("") && !realisateurs[i+1][j].equals(null)) {
					sql += " AND ";
				}
				//fin du AND
			
				i++;
			
			} while(!realisateurs[i][j].equals("") && !realisateurs[i][j].equals(null));
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("DE")) {
					
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}	
		}
		return sql;
	}

	/**
	 * Cree la partie de la requete sql concernant les acteurs
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String acteurs(FormatDemande infos, String tab[], String sql) {

		String acteurs[][] = infos.getActeurs();
		
		if(acteurs[0][0] != null && acteurs[0][0] != "") {
			
			int i=0;
			int j=0;
				
				
			do {
					
				sql += "((f.id_film IN (SELECT id_film FROM personnes NATURAL JOIN generique WHERE (prenom_sans_accent || ' ' || nom_sans_accent LIKE \"%"+acteurs[i][j].replaceAll(" ", "%")+"%\" OR nom_sans_accent || ' ' || prenom_sans_accent LIKE \"%"+acteurs[i][j].replaceAll(" ", "%")+"%\" 	OR nom_sans_accent LIKE \"%"+acteurs[i][j].replaceAll(" ", "%")+"%\") 	AND role = 'A'))"; //debut du OU
				while(!acteurs[i][j+1].equals("") && !acteurs[i][j+1].equals(null)) {
					sql += " OR  (f.id_film IN (SELECT id_film FROM personnes NATURAL JOIN generique WHERE (prenom_sans_accent || ' ' || nom_sans_accent LIKE \"%"+acteurs[i][j+1].replaceAll(" ", "%")+"%\" OR nom_sans_accent || ' ' || prenom_sans_accent LIKE \"%"+acteurs[i][j+1].replaceAll(" ", "%")+"%\"	OR nom_sans_accent LIKE \"%"+acteurs[i][j+1].replaceAll(" ", "%")+"%\")	AND role = 'A'))";
					j++;
				}
				sql += ")"; //fin du OU
					
				//debut du AND
				if(!acteurs[i+1][j].equals("") && !acteurs[i+1][j].equals(null)) {
					sql += " AND ";
				}
				//fin du AND
					
				i++;
					
			} while(!acteurs[i][j].equals("") && !acteurs[i][j].equals(null));
			
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("AVEC")) {
					
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}
		}
		return sql;
	}
	
	/**
	 * Cree la partie de la requete sql concernant les pays
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String pays(FormatDemande infos, String tab[], String sql) {
		
		String pays[] = infos.getPays();
		
		if(pays[0] != null && pays[0] != "") {
			sql += "(";
			for(int i=0; i<pays.length-1; i++) {
				if(pays[i] != null && pays[i] != "") {				
					sql += "(py.nom like \"%"+pays[i].replaceFirst(".",(pays[i].charAt(0)+"").toUpperCase()).trim()+"%\" OR py.code like \"%"+pays[i].trim()+"%\") OR ";	
				}
			}
			sql = sql.substring(0, sql.length()-4);
			sql += ")";
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("PAYS")) {
					
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}
		}
		return sql;
	}

	/**
	 * Cree la partie de la requete sql concernant les annees de sorties
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String annee(FormatDemande infos, String tab[], String sql) {
	
		int annee[] = infos.getAnnee();
		
		if(annee[0] != 0) {
			sql += "(";
			for(int i=0; i<annee.length-1; i++) {
				if(annee[i] != 0) {				
					sql += "annee == "+annee[i]+" OR ";	
				}
			}
			sql = sql.substring(0, sql.length()-4);
			sql += ")";
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("EN")) {
					
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}
		}
		return sql;
	}

	/**
	 * Cree la partie de la requete sql concernant les annees de sorties anterieures
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String avant(FormatDemande infos, String tab[], String sql) {
		
		int avant[][] = infos.getAvant();

		if(avant[0][0] != 0) {
			
			int i=0;
			int j=0;
									
			do {
					
				sql += "(annee < "+avant[i][j]; //debut du OU
				while(avant[i][j+1] != 0) {
					sql += " OR annee < "+avant[i][j+1];
					j++;
				}
				sql += ")"; //fin du OU
					
				//debut du AND
				if(avant[i+1][j] != 0) {
					sql += " AND ";
				}
				//fin du AND
					
				i++;
					
			} while(avant[i][j] != 0);
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("AVANT")) {
					
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVEC") 
							|| tab[k+1].equalsIgnoreCase("APRES") 
							|| tab[k+1].equalsIgnoreCase("APRÈS")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}
		}
		return sql;
	}

	/**
	 * Cree la partie de la requete sql concernant les annees de sorties ulterieures
	 * 
	 * @param infos : contient les informations analysees de la requete utilisateur
	 * @param tab : contient la requete utilisateur
	 * @param sql : contient le debut de la requete sql
	 * @return la requete sql completee
	 */
	String apres(FormatDemande infos, String tab[], String sql) {
		
		int apres[][] = infos.getApres();
		
		if(apres[0][0] != 0) {
			
			int i=0;
			int j=0;
			
			do {
				
				sql += "(annee > "+apres[i][j]; //debut du OU
				while(apres[i][j+1] != 0) {
					sql += " OR annee > "+apres[i][j+1];
					j++;
					
				}
				sql += ")"; //fin du OU
					
				//debut du AND
				if(apres[i+1][j] != 0) {
					sql += " AND ";
				}
				//fin du AND
					
				i++;
					
			} while(apres[i][j] != 0);
			
			int indice = 0;
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("APRES") || tab[k].equalsIgnoreCase("APRÈS")) {	
					do {
					while(!tab[k].trim().endsWith(",") && !tab[k].trim().equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						k++;
					}
					
					if(tab[k].endsWith(",") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("AVEC")) {
						sql += ") AND (";
						indice = 1;
						}
					} else if(tab[k].equalsIgnoreCase("OU") && !tab[k].equals("") && !tab[k].equals(null)) {
						if(tab[k+1].equalsIgnoreCase("TITRE") 
							|| tab[k+1].equalsIgnoreCase("DE") 
							|| tab[k+1].equalsIgnoreCase("PAYS") 
							|| tab[k+1].equalsIgnoreCase("EN") 
							|| tab[k+1].equalsIgnoreCase("AVANT") 
							|| tab[k+1].equalsIgnoreCase("AVEC")) {
						sql += " OR ";
						indice = 1;
						}
					}
					k++;
					} while(!tab[k].equals("") && !tab[k].equals(null) && indice==0);
				}	
			}
		}
		return sql;
	}
	
	
	/**
	 * transforme un objet FormatDemande qui contient la requete utilisateur analysee en requete sql
	 * @param infos: contient les informations analysees de la requete utilisateur
	 * @return la requete sql completee
	 */
	String RequeteSQL(FormatDemande infos, String tab[]) {
		
		String sql = "select	f.titre, prenom, p.nom, role, annee, duree, py.nom, a_t.titre, f.id_film\r\n" + 
				"from films f natural join generique g natural join personnes p left join autres_titres a_t on f.id_film = a_t.id_film left join pays py on f.pays = py.code\r\n"
				+ "where ";
	
		
		if (infos.getErreur().equals("")) {
					
			sql += "(("; //debut du where
			
			
			//String titres[] = infos.getTitres();
			//String realisateurs[][] = infos.getRealisateurs();
			//String acteurs[][] = infos.getActeurs();
			//String pays[] = infos.getPays();
			//int annee[] = infos.getAnnee();
			//int avant[][] = infos.getAvant();
			//int apres[][] = infos.getApres();
			
			//installer des priorites
			
			int[] t = new int[10];
			int[] r = new int[10];
			int[] ac = new int[10];
			int[] p = new int[10];
			int[] an = new int[10];
			int[] av = new int[10];
			int[] ap = new int[10];
			int a = 0;
			int b = 0;
			int c = 0;
			int d = 0;
			int e = 0;
			int f = 0;
			int g = 0;
			
			
			for(int k=0; k<tab.length-1; k++) {
				if(tab[k].equalsIgnoreCase("TITRE")) {
					t[a] = k+1;
					a++;
				}
				if(tab[k].equalsIgnoreCase("DE")) {
					r[b] = k+1;
					b++;
				}
				if(tab[k].equalsIgnoreCase("AVEC")) {
					ac[c] = k+1;
					c++;
				}
				if(tab[k].equalsIgnoreCase("PAYS")) {
					p[d] = k+1;
					d++;
				}
				if(tab[k].equalsIgnoreCase("EN")) {
					an[e] = k+1;
					e++;
				}
				if(tab[k].equalsIgnoreCase("AVANT")) {
					av[f] = k+1;
					f++;
				}
				if(tab[k].equalsIgnoreCase("APRES") || tab[k].equalsIgnoreCase("APRÈS")) {
					ap[g] = k+1;
					g++;
				}
			}
			
		a=0;
		b=0;
		c=0;

		for(int cpt=1; cpt<40; cpt++) {	
			for(int k=0; k<t.length; k++) {
				if(t[k]==cpt) {	
					if(a==0) {
						sql = titres(infos, tab, sql);
					}
					a=1;
				} else if(r[k]==cpt) {
					sql = realisateurs(infos, tab, sql);
				} else if(ac[k]==cpt) {
					sql = acteurs(infos, tab, sql);
				} else if(p[k]==cpt) {
					if(b==0) {
						sql = pays(infos, tab, sql);
					}
					b=1;
				} else if(an[k]==cpt) {
					if(c==0) {
						sql = annee(infos, tab, sql);
					}
					c=1;
				} else if(av[k]==cpt) {
					sql = avant(infos, tab, sql);
				} else if(ap[k]==cpt) {
					sql = apres(infos, tab, sql);
				}
			}
		}
		
			sql += "))\nORDER BY annee DESC, f.titre"; //fin du where
		
			return sql;
		
		} else {
			return "{\"erreur\":\""+infos.getErreur()+"\"}";
		}
	}
	
	/**
	 * /!\ Methode non implementee
	 * recupere les resultats de la requete sql
	 * les range dans un ArrayList qui est retourne
	 * 
	 * @param sql Requete sql complete
	 * @return list qui contient le resultat de la recherche
	 */
	ArrayList<InfoFilm> getResults(String sql) {
		
		ArrayList<InfoFilm> InfoFilmsList = new ArrayList<>();
		String                 _titre;
	    ArrayList<NomPersonne> _realisateurs = new ArrayList<>();
	    ArrayList<NomPersonne> _acteurs = new ArrayList<>();
	    String                 _pays;
	    int                    _annee;
	    int                    _duree;
	    ArrayList<String>      _autres_titres = new ArrayList<>();
	    
	    PreparedStatement ps;
        ResultSet rs;
        ArrayList<ArrayList<String>> listeBDD = new ArrayList<>();
        
        try {
            ps = bdd.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
            	do {
            		ArrayList<String> ls = new ArrayList<>();
                    for (int i=1; i<=9; i++) {
                    	ls.add(rs.getString(i));
                    }
                    
                    listeBDD.add(ls);
                   
            	}while(rs.next());
            	rs.close();
            }
        }
        catch (SQLException e) { 
        	e.printStackTrace(); 
        }
        
        for (int i = 0; i < listeBDD.size(); i++) {
        	
        	_titre = listeBDD.get(i).get(0);
        	
            if (listeBDD.get(i).get(3).equals("A")) {
                String prenomA = "";
                String nomA = listeBDD.get(i).get(2);
                if(listeBDD.get(i).get(1) != null) {
                	prenomA = listeBDD.get(i).get(1);
                } else {
                	prenomA = "";
                }
                int cpt = 0;
                NomPersonne newA = new NomPersonne(nomA, prenomA);
                for(NomPersonne token : _acteurs) {
                	if(newA.toString().equals(token.toString())) {
                		cpt++;
                	}
                }
                if(cpt == 0) _acteurs.add(newA);
            }
            else if (listeBDD.get(i).get(3).equals("R")) {
                String prenomR = "";
                String nomR = listeBDD.get(i).get(2);
                if(listeBDD.get(i).get(1) != null) {
                	prenomR = listeBDD.get(i).get(1);
                } else {
                	prenomR = "";
                }
                int cpt = 0;
                NomPersonne newR = new NomPersonne(nomR, prenomR);
                for(NomPersonne token : _realisateurs) {
                	if(newR.toString().equals(token.toString())) {
                		cpt++;
                	}
                }
                if(cpt == 0) _realisateurs.add(newR);
            }

            
            if(listeBDD.get(i).get(4) != null) {
            	_annee = Integer.valueOf(listeBDD.get(i).get(4));
            } else {
            	_annee = 0;
            }
            if(listeBDD.get(i).get(5) != null) {
            	_duree = Integer.valueOf(listeBDD.get(i).get(5));
            } else {
            	_duree = 0;
            }
            if(listeBDD.get(i).get(6) != null) {
            	_pays = listeBDD.get(i).get(6);
            } else {
            	_pays = "";
            }

            if (listeBDD.get(i).get(7) != null) {  
            	String a_t = listeBDD.get(i).get(7);            	
            	int cpt = 0;
                
                for(String token : _autres_titres) {
                	if(a_t.equals(token)) {
                		cpt++;
                	}
                }
                if(cpt == 0) _autres_titres.add(a_t);
            }

            
            		
            if (i == listeBDD.size()-1 || !Integer.valueOf(listeBDD.get(i).get(8)).equals(Integer.valueOf(listeBDD.get(i+1).get(8)))) {
            	InfoFilmsList.add(new InfoFilm(_titre, _realisateurs, _acteurs, _pays, _annee, _duree, _autres_titres));
                _acteurs = new ArrayList<>();
                _realisateurs = new ArrayList<>();
                _autres_titres = new ArrayList<>();
            }
        }
        
        fermeBase();
        return InfoFilmsList;
	
	}
	
	public String convertToJSON(ArrayList<InfoFilm> list) {
        StringBuilder result = new StringBuilder();
        int n;
        if (list.size() >= 100) {
            n = 100;
            result.append("{\"resultat\":[");
            for (int i = 0; i < n; i++) {
                if (i > 0) result.append(",\n");
                result.append(list.get(i).toString());
            }
            result.append("],\n\"info\":\"Resultat limite a 100 films\"}");
        }
        else {
            n = list.size();
            result.append("{\"resultat\":[");
            for (int i = 0; i < n; i++) {
                if (i > 0) result.append(",\n");
                result.append(list.get(i).toString());
            }
            result.append("]}");
        }
        
        return result.toString();
    }

}
