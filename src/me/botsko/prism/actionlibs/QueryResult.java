package me.botsko.prism.actionlibs;

import java.util.ArrayList;
import java.util.List;

import me.botsko.prism.actions.Action;

public class QueryResult {
	
	/**
	 * 
	 */
	protected List<Action> actionResults = new ArrayList<Action>();
	
	/**
	 * 
	 */
	protected long queryTime;
	
	/**
	 * 
	 */
	protected int total_results;
	
	/**
	 * 
	 */
	protected int per_page = 5;
	
	/**
	 * 
	 */
	protected int total_pages = 0;
	
	/**
	 * 
	 */
	protected int page = 1;
	
	
	/**
	 * 
	 * @param actions
	 */
	public QueryResult( List<Action> actions ){
		
		this.actionResults = actions;

		java.util.Date date = new java.util.Date();
		this.queryTime = date.getTime();
		
		// set counts
		total_results = actionResults.size();
		total_pages = (int) Math.ceil( ((double)total_results / (double)per_page) );
		
	}


	/**
	 * @return the actionResults
	 */
	public List<Action> getActionResults() {
		return actionResults;
	}
	
	
	/**
	 * @return the actionResults
	 */
	public List<Action> getPaginatedActionResults() {
		int limit = (page * per_page);
		int offset = (limit - per_page);
		return actionResults.subList(offset, limit);
	}
	

	/**
	 * @return the total_results
	 */
	public int getTotal_results() {
		return total_results;
	}


	/**
	 * @return the queryTime
	 */
	public long getQueryTime() {
		return queryTime;
	}


	/**
	 * @return the per_page
	 */
	public int getPer_page() {
		return per_page;
	}


	/**
	 * @param per_page the per_page to set
	 */
	public void setPer_page(int per_page) {
		this.per_page = per_page;
	}


	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}


	/**
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}


	/**
	 * @return the total_pages
	 */
	public int getTotal_pages() {
		return total_pages;
	}
}