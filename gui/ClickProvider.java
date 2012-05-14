package gui;

import java.util.Iterator;
import java.util.Set;

/**
 * Observer pattern for click events which are not compatible with the standard
 * ActionListener. This is the Observable interface.
 * 
 * @author Alexander Peev
 * 
 */
public interface ClickProvider {
	public void addClickListener(ClickListener listener);

	public void removeClickListener(ClickListener listener);

	public Set<ClickListener> getClickListeners();

	public Iterator<ClickListener> getClickListenersIterator();

	public int getClickListenersTotal();

	public void fireClick();
}
