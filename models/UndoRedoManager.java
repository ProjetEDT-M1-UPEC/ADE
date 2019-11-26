package models;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * une classe représente un tableau en mode LIFO (dernier entré, premier sorti)
 */
public class UndoRedoManager<T> {

	private int indexOfNextAdd;
	private int limit;
	private Stack<T> lifo;

	public UndoRedoManager(int limit) {
		this.limit = limit;
		this.indexOfNextAdd = 0;
		lifo = new Stack<T>();
	}

	/**
	 * teste si on peut ajouter des objets
	 * 
	 * @return Renvoie un simple boolean vérifiant le test
	 */
	public Boolean canRedo() {
		return indexOfNextAdd < limit;
	}

	/**
	 * tese si on peut lire des objets
	 * 
	 * @return Renvoie un simple boolean vérifiant le test
	 */
	public Boolean canUndo() {
		return indexOfNextAdd > 0;
	}

	/**
	 * vider le tabeau
	 */
	public void clear() {
		lifo.clear();
		indexOfNextAdd = 0;
	}

	/**
	 * lire l'objet de la tête
	 * @return Renvoie cet objet
	 */
	public T undo() throws EmptyStackException {
		if (canUndo())
			indexOfNextAdd--;
		return lifo.pop();
	}

	/**
	 * écrire l'objet en param
	 * @param e Un objet que l'on va ajouter
	 */
	public void Redo(T e) {
		if (!canRedo()) {
			lifo.remove(0);
			indexOfNextAdd--;
		}
		indexOfNextAdd++;
		lifo.add(e);
	}
}
