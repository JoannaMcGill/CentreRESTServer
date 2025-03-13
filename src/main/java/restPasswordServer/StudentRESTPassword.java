package restPasswordServer;

import org.springframework.web.client.RestClient;

public class StudentRESTPassword
{

	public static void main(String[] args)
	{
		RestClient client = RestClient.create();
		
		String uriBase = "http://localhost:9000/";
		
		
		String pw = client.get()
		.uri(uriBase+"request/jane")
		.retrieve()
		.body(String.class);

		String response = client.get()
		.uri(uriBase+"auth/jane/"+pw)
		.retrieve()
		.body(String.class);

		System.out.println(response);
		
		
	}

}
