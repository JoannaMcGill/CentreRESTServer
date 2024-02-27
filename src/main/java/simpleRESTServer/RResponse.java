package simpleRESTServer;

public class RResponse
{
	
	//@JsonIgnore
	//private static ObjectMapper mapper = new ObjectMapper();
	
	public String request;
	public boolean successful;
	public String message;
	public Object data;
	/**
	 * @param request
	 * @param successful
	 * @param message
	 * @param data
	 */
	public RResponse(String request, boolean successful, String message, Object data)
	{
		this.request = request;
		this.successful = successful;
		this.message = message;
		this.data = data;
	}
	
	

	public RResponse(String request, boolean successful, String message)
	{
		this.request = request;
		this.successful = successful;
		this.message = message;
		this.data = "";
	}
	
	
	
	
	
}
