import javafx.event.Event;
import javafx.event.EventHandler;

public abstract class CustomEventHandler implements EventHandler<Event> {

	private int index;
	public boolean isFirstClick;
	public boolean isSecondClick;
	
	public CustomEventHandler(int index, boolean isFirstClick, boolean isSecondClick) {
        this.index = index;
        this.isFirstClick = isFirstClick;
        this.isSecondClick = isSecondClick;
    }

    public int getIndex() {
        return this.index;
    }
	
}
