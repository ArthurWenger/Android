import Integer.parseInt;

private void AddHighscores(ArraySet<String> newScore){//newScore = ["nom", "score", "longitude", "lattitude"]
	String rank = "0";// Au départ, on ne connaît pas son rang.
	int score = parseInt(newScore.valueAt(1));
	defaultSet = new ArraySet();	defaultSet.add("");	defaultSet.add("0");
	SharedPreferences highscore = getSharedPreferences("highscores", MODE_PRIVATE);//"highscores" est le nom de fichier, MODE_PRIVATE est le mode d'opération.
	SharedPreferencesEditor editor = highscore.edit();//On créé un éditeur.
	String i = "20";
	while (rank == "0"){//Boucle qui permet de trouver le rang.
		try{
			if (score > (parseInt((highscore.getStringSet(i, defaultSet)).valueAt(1)))){//Si le nouveau score est supérieur au score courant
				if (i == "1"){//Sil est supérieur au meilleur score, il est premier
					rank = "1";
					break;
				}
				if (score < (parseInt(highscore.getStringSet(Integer.toString(parseInt(i)-1), defaultSet)[1]))//Et si son score est inférieur au score suivant
					rank = i;//Son rang est le rang courant.
				else//Sinon, son score est supérieur au score suivant
					i = Integer.toString(parseInt(i)-1);//Donc on incrémente
			}
			else//Sinon, son score n'est pas supérieur au score courant, cas possible uniquement à 20
				return;//Donc on tue le programme.
		} catch (ClassCastException e){//Si on a réussi à trouver des valeurs dans highscores qui n'étaient pas des Set<String>
			e.printStackTrace();
		}
	}
	while ((parseInt(rank) <= 20) && (newScore != null)){//Boucle de réorganisation des scores
		ArraySet<String> currentScore = highscore.getStringSet(rank, null);//On garde le score au rang courant
		editor.putStringSet(rank, newScore);//On met le nouveau score à ce rang
		newScore = currentScore;//Le prochain score à enregistrer sera le score courant
		rank = Integer.toString(parseInt(rank)+1);//et on incrémente le rang
	}
	editor.apply()//On commit les nouvelles valeurs.
}