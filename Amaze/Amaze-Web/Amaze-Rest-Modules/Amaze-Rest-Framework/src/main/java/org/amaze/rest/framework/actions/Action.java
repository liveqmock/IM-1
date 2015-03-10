package org.amaze.rest.framework.actions;

import java.util.List;

import org.amaze.rest.framework.detail.Detail;
import org.amaze.rest.framework.screen.Screen;

public interface Action extends Screen
{
	public String action( List<Integer> actionIds );
	
	public void setDetail( Detail detail);
	
}
