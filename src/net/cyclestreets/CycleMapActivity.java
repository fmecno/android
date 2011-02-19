package net.cyclestreets;

import net.cyclestreets.CycleStreetsConstants;
import net.cyclestreets.R;
import net.cyclestreets.views.CycleMapView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;

import uk.org.invisibility.cycloid.GeoIntent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

 public class CycleMapActivity extends Activity 
 {
	private CycleMapView map_; 
	
    @Override
    public void onCreate(Bundle saved)
    {
        super.onCreate(saved);

		map_ = new CycleMapView(this, "route");

        final RelativeLayout rl = new RelativeLayout(this);
        rl.addView(map_, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setContentView(rl);
    } // onCreate
    
    protected CycleMapView mapView() { return map_; }
    protected Overlay overlayPushBottom(final Overlay overlay) { return map_.overlayPushBottom(overlay); }
    protected Overlay overlayPushTop(final Overlay overlay) { return map_.overlayPushTop(overlay); }
    
    protected void findPlace() { launchFindDialog(); }

    @Override
    protected void onPause()
    {
    	map_.onPause();
        super.onPause();
    } // onPause

    @Override
    protected void onResume()
    {
    	super.onResume();
    	map_.onResume();
    } // onResume

    @Override
	public boolean onCreateOptionsMenu(final Menu menu)
    {
    	map_.onCreateOptionsMenu(menu);
    	menu.add(0, R.string.ic_menu_findplace, Menu.NONE, R.string.ic_menu_findplace).setIcon(R.drawable.ic_menu_search);
    	return true;
	} // onCreateOptionsMenu
    	
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item)
	{
		if(map_.onMenuItemSelected(featureId, item))
			return true;
		
		if(item.getItemId() == R.string.ic_menu_findplace)
		{
			launchFindDialog();
			return true;
		} // if ...
		
		return false;
	} // onMenuItemSelected
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != RESULT_OK)
			return;
		
		if(requestCode != R.string.ic_menu_findplace)
			return;
		
		final GeoPoint place = new GeoPoint(data.getIntExtra(CycleStreetsConstants.EXTRA_PLACE_FROM_LAT, 0),
				data.getIntExtra(CycleStreetsConstants.EXTRA_PLACE_FROM_LONG, 0));
		// we're in the wrong thread, so pop this away for later and force a refresh
		map_.centreOn(place);
	} // onActivityResult
    
	private void launchFindDialog()
	{
		final Intent intent = new Intent(this, FindPlaceActivity.class);
    	GeoIntent.setBoundingBoxInExtras(intent, map_.getBoundingBox());
    	startActivityForResult(intent, R.string.ic_menu_findplace);
	} // launchFindDialog
	
   @Override
   public boolean onTrackballEvent(MotionEvent event)
   {
       return map_.onTrackballEvent(event);
   } // onTrackballEvent
  
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
       if (event.getAction() == MotionEvent.ACTION_MOVE)
           map_.disableFollowLocation();
       return super.onTouchEvent(event);
   } // onTouchEvent   
} // class MapActivity