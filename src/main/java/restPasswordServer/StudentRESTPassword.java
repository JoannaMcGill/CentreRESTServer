package restPasswordServer;

import org.springframework.web.client.RestClient;

public class StudentRESTPassword
{

	public static void main(String[] args)
	{
		RestClient client = RestClient.create();
		
		String uriBase = "http://localhost:8080/";
		
		
		String pw = client.get()
		.uri(uriBase+"request/mkb")
		.retrieve()
		.body(String.class);

		String response = client.get()
		.uri(uriBase+"auth/mkb/"+pw)
		.retrieve()
		.body(String.class);

		System.out.println(response);
		
		
	}

}
