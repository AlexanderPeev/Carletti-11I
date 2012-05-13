package gui;

/**
 * Observer pattern (modified version) - observer interface. The observable
 * interface does not exist, due to the fact that the observable is instead a
 * type with static methods, so all the observers are coupled to that particular
 * type.
 * 
 * @author Alexander Peev
 * 
 */
public interface TraySelectionChangedListener {
	public void onTraySelectionChanged();
}
