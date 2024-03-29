/**
 *    Gestion des noms (noms de famille + prenom) des personnes.
 *    <p>
 *    La classe NomPersonne permet de gerer les noms et tient
 *    en particulier compte des prefixes des noms ('de', 'von', 'van')
 *    dans le tri.
 */ 
public class NomPersonne implements Comparable<NomPersonne>{
    private String _nom;
    private String _prenom;
    private int    _debutComp;

    /**
     *    Cr&eacute; d'un nouveau NomPersonne. Attention, le prenom
     *    est passe en deuxieme.
     *
     *    @param nom Nom de famille ou nom d'artiste
     *    @param prenom Prenom (peut etre "null")
     */
    public NomPersonne(String nom, String prenom) {
        _nom = nom;
        _prenom = prenom;
        _debutComp = 0;
        // On regarde quel est le premier caract�re en majuscules
        // pour trier 'von Stroheim' avec les S, 'de la Huerta'
        // avec les H et 'de Fun�s' avec les F.
        // 'De Niro' sera en revanche � D.
        while ((_debutComp < _nom.length())
               && (_nom.charAt(_debutComp)
                   == Character.toLowerCase(_nom.charAt(_debutComp)))) {
           _debutComp++;
        }
    }

    /**   Comparateur qui tient compte des prefixes de noms.
     *    
     *    @param autre NomPersonne qui est compare a l'objet courant
     *    @return un entier inferieur, egal ou superieur a zero suivant le cas
     */
    @Override
    public int compareTo(NomPersonne autre) {
        if (autre == null) {
          return 1;
        }
        int cmp = this._nom.substring(this._debutComp)
                      .compareTo(autre._nom.substring(autre._debutComp));
        if (cmp == 0) {
          if (this._prenom == null) {
            if (autre._prenom == null) {
              return 0;
            }
            return -1;
          }
          if (autre._prenom == null) {
            return 1;
          }
          return this._prenom.compareTo(autre._prenom);
        } else {
          return cmp;
        }
    }
    
    /**
     *   Retourne un nom affichable.
     *   <p>
     *   S'il y a une mention telle que (Jr.) qui dans la base est dans
     *   la colonne du prenom, elle est reportee a; 
     *   la fin.
     *
     *   @return La combinaison du prenom et du nom, dans cet ordre.
     */
    @Override
    public String toString() {
        int pos = -1;

        if (this._prenom != null) {
          // Les mentions entre parenth�ses seront renvoy�es
          // � la fin.
          pos = this._prenom.indexOf('(');
        } else {
          return this._nom;
        }
        if (pos == -1) {
          return this._prenom + " " + this._nom;
        } else {
          return this._prenom.substring(0, pos-1).trim() 
                 + " " + this._nom
                 + " " + this._prenom.substring(pos).trim();
        }
    }
}