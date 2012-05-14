package gui;

/**
 * Observer pattern for click events which are not compatible with the standard
 * ActionListener. This is the Observer interface, using pull.
 * 
 * @author Alexander Peev
 * 
 */
public interface ClickListener {
	public void onClick(ClickProvider provider);
}
