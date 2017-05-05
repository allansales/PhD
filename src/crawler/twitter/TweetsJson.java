package crawler.twitter;


public class TweetsJson {

	private String items_html;
	private String scroll_cursor;
    private boolean is_scrolling_request;
    private boolean is_refresh_request;
    private String refresh_cursor;
    public String getItems_html() {
		return items_html;
	}
	public void setItems_html(String items_html) {
		this.items_html = items_html;
	}
	public String getScroll_cursor() {
		return scroll_cursor;
	}
	public void setScroll_cursor(String scroll_cursor) {
		this.scroll_cursor = scroll_cursor;
	}
	public boolean isIs_scrolling_request() {
		return is_scrolling_request;
	}
	public void setIs_scrolling_request(boolean is_scrolling_request) {
		this.is_scrolling_request = is_scrolling_request;
	}
	public boolean isIs_refresh_request() {
		return is_refresh_request;
	}
	public void setIs_refresh_request(boolean is_refresh_request) {
		this.is_refresh_request = is_refresh_request;
	}
	public boolean isHas_more_items() {
		return has_more_items;
	}
	public void setHas_more_items(boolean has_more_items) {
		this.has_more_items = has_more_items;
	}
	public String getRefresh_cursor() {
		return refresh_cursor;
	}
	public void setRefresh_cursor(String refresh_cursor) {
		this.refresh_cursor = refresh_cursor;
	}
	private boolean has_more_items;
}
