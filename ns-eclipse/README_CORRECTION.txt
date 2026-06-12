Projet corrigé pour Eclipse

Corrections effectuées :
- suppression de module-info.java pour éviter les erreurs de module du type "javax.swing is not accessible" ;
- configuration Eclipse repassée en JavaSE-17 ;
- retrait de la référence au fichier lib/mysql-connector-j.jar absent du zip ;
- conservation du code source Java original.

Pour lancer :
1. Eclipse > File > Import > Existing Projects into Workspace.
2. Sélectionner le dossier ns-eclipse.
3. Project > Clean.
4. Lancer controleur.NeigeEtSoleil.

Si Eclipse demande un JRE/JDK :
Window > Preferences > Java > Installed JREs > sélectionner un JDK 17 ou 21.
