package me.botsko.prism.actionlibs;

import java.util.ArrayList;
import java.util.List;

import me.botsko.prism.Prism;
import me.botsko.prism.actions.Handler;

public class RecordingTask implements Runnable {

    /**
	 * 
	 */
    private final Prism plugin;

    /**
     * 
     * @param plugin
     */
    public RecordingTask(Prism plugin) {
        this.plugin = plugin;
    }

    /**
	 * 
	 */
    public void save() {
        if( !RecordingQueue.getQueue().isEmpty() ) {
            insertActionsIntoDatabase();
        }
    }

    /**
     * 
     * @throws SQLException
     */
    public void insertActionsIntoDatabase() {
    	
    	if( !RecordingQueue.getQueue().isEmpty() ) {
    		
    		Prism.debug( "Beginning batch insert from queue. " + System.currentTimeMillis() );
    		
    		List<Handler> actions = new ArrayList<Handler>();
    		
    		while( !RecordingQueue.getQueue().isEmpty() ){
    			final Handler action = RecordingQueue.getQueue().poll();
    			if( action == null ) continue;
    			actions.add(action);
    		}
    		
    		Prism.getStorageAdapter().create(actions);
    		
    	}
    }

   

    /**
	 * 
	 */
    @Override
    public void run() {
//        if( RecordingManager.failedDbConnectionCount > 5 ) {
//            plugin.rebuildPool(); // force rebuild pool after several failures
//        }
        save();
        scheduleNextRecording();
    }

    /**
     * 
     * @return
     */
    protected int getTickDelayForNextBatch() {

        // If we have too many rejected connections, increase the schedule
        if( RecordingManager.failedDbConnectionCount > plugin.getConfig().getInt(
                "prism.database.max-failures-before-wait" ) ) { return RecordingManager.failedDbConnectionCount * 20; }

        int recorder_tick_delay = plugin.getConfig().getInt( "prism.queue-empty-tick-delay" );
        if( recorder_tick_delay < 1 ) {
            recorder_tick_delay = 3;
        }
        return recorder_tick_delay;
    }

    /**
	 * 
	 */
    protected void scheduleNextRecording() {
        if( !plugin.isEnabled() ) {
            Prism.log( "Can't schedule new recording tasks as plugin is now disabled. If you're shutting down the server, ignore me." );
            return;
        }
        plugin.recordingTask = plugin.getServer().getScheduler()
                .runTaskLaterAsynchronously( plugin, new RecordingTask( plugin ), getTickDelayForNextBatch() );
    }
}
