package gui;

/**
 * @author Ricardas Risys
 */
public interface UpdateSubject {
	public void registerObserver(UpdateObserver o);

	public void removeObserver(UpdateObserver o);

	public void notifyObservers();
}
