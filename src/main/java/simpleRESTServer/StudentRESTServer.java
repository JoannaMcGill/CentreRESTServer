package simpleRESTServer;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
public class StudentRESTServer
{

	public static void main(String[] args)
	{
		
		RestClient client = RestClient.create();
		
		String uriBase = "http://localhost:8080/v1";
		
		RDesc desc = new RDesc("mkb","Michael K Bradshaw","");
		
		
		RResponseDesc resp = client.post()
		.uri(uriBase+"/mkb")
		.contentType(MediaType.APPLICATION_JSON)
		.body(desc)
		.retrieve()
		.body(RResponseDesc.class);
/*
		String response = client.get()
		.uri(uriBase+"auth/mkb/"+pw)
		.retrieve()
		.body(String.class);
*/
/*		if(resp.get("successful").asBoolean())
			resp.get(data)
	*/	
		
		System.out.println(resp);
		
		
		
		
		
		
		
	}

}
