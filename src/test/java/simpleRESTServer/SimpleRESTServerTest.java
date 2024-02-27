package simpleRESTServer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

class SimpleRESTServerTest
{

	
	RestClient client = RestClient.create();
	
	String uriBase = "http://localhost:8080/v1";
	RDesc teamDesc = new RDesc("MKB","Michael's Team",uriBase+"/MKB");
	RDesc addDesc = new RDesc("address","Personal Address",teamDesc.location()+"/address");
	RDesc todoDesc = new RDesc("todo","Todo List Items",teamDesc.location()+"/todo");
	
	RDesc A = new RDesc("A","A Team",uriBase+"/A");
	RDesc B = new RDesc("B","B Team",uriBase+"/B");
	RDesc C = new RDesc("C","C Team",uriBase+"/C");
	
	public record Address(String Street,String State,String zip ) {}
	public record Todo(String item,int priority) {}
	
	public record RResponseAddress(String request, boolean successful, String message, Address data)
	{}
	
	public record RResponseTodo(String request, boolean successful, String message, Todo data)
	{}
	public record RResponseArray(String request, boolean successful, String message, ArrayList<RDesc> data)
	{}
	
	public record RResponseShort(String request, boolean successful, String message, String data)
	{}
	
	private void assertRReponseDesc(RResponseDesc desc,String request, boolean successful, String message,RDesc data )
	{
		assertEquals(request,desc.request());
		assertEquals(successful,desc.successful());
		assertEquals(message,desc.message());
		assertEquals(data.name(),desc.data().name());
		assertEquals(data.description(),desc.data().description());
		assertEquals(data.location(),desc.data().location());
	}
	
	private void assertRReponseArray(RResponseArray desc,String request, boolean successful, String message,ArrayList<RDesc> data )
	{
		assertEquals(request,desc.request());
		assertEquals(successful,desc.successful());
		assertEquals(message,desc.message());
		
		assertTrue(data.size() == desc.data.size() && data.containsAll(desc.data) && desc.data.containsAll(data));
	}
	
	
	
	
	private void assertCreate(String message,RDesc data)
	{
		RResponseDesc createDesc = client.post()
				.uri(data.location())
				.contentType(MediaType.APPLICATION_JSON)
				.body(data)
				.retrieve()
				.body(RResponseDesc.class);
		
		assertRReponseDesc(createDesc,data.location(),true,message,data);
	}
	
	
	private void assertReadArray(String message,String request,ArrayList<RDesc> array)
	{
		RResponseArray readArray = client.get()
				.uri(request)
				.retrieve()
				.body(RResponseArray.class);
	
		assertRReponseArray(readArray,request, true, message, array );
	}
	
	
	private void deletePath(RDesc desc)
	{
		client.delete()
		.uri(desc.location())
		.retrieve();
	}
	
	private void assertDeletePath(RDesc desc,boolean succcess)
	{
		RResponseShort res = client.delete()
		.uri(desc.location())
		.retrieve()
		.body(RResponseShort.class);
		
		
		
	}

	
	
	
	@BeforeEach
	void setUp() throws Exception
	{
		deletePath(teamDesc);
		deletePath(A);
		deletePath(B);
		deletePath(C);
		

		assertCreate("Team "+teamDesc.name()+" successfully created",teamDesc);
		assertCreate("Class "+addDesc.name()+" successfully created",addDesc);
		assertCreate("Class "+todoDesc.name()+" successfully created",todoDesc);
		
	}

	
	@Test
	void testGetTeams()
	{
		
	
		//Test Create Team
		assertCreate("Team "+A.name()+" successfully created",A);
		assertCreate("Team "+B.name()+" successfully created",B);
		
		/*RResponseArray teams = client.get()
				.uri(uriBase)
				.retrieve()
				.body(RResponseArray.class);
	*/	
		ArrayList<RDesc> data = new ArrayList<RDesc>();
		data.add(A);
		data.add(B);
		data.add(teamDesc);
	
		//Test get all Teams
		assertReadArray("Here are all of the Teams",uriBase,data);
		

		RDesc ABad = new RDesc("B","A Team 2",uriBase+"/A");

		//Test bad put not present
		RResponseShort putDesc = client.put()
				.uri(C.location())
				.contentType(MediaType.APPLICATION_JSON)
				.body(C)
				.retrieve()
				.body(RResponseShort.class);
			
			
			assertFalse(putDesc.successful);
			assertEquals("Team C does not exist", putDesc.message);
		
		
		//Test bad put
		putDesc = client.put()
			.uri(A.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(ABad)
			.retrieve()
			.body(RResponseShort.class);
		
		
		assertFalse(putDesc.successful);
		assertEquals("Team B already exists", putDesc.message);
		
		
		//Test good description change only put
		RDesc AGood = new RDesc("A","A Team 2",uriBase+"/A");

		RResponseDesc goodPut1 = client.put()
			.uri(A.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(AGood)
			.retrieve()
			.body(RResponseDesc.class);
		
		assertRReponseDesc(goodPut1,A.location(), true, "Team A has been updated",AGood);

		//test good name and description change on put
		RResponseDesc goodPut2 = client.put()
			.uri(A.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(C)
			.retrieve()
			.body(RResponseDesc.class);
		
		assertRReponseDesc(goodPut2,A.location(), true, "Team A has been updated",C);
		
		deletePath(C);
		data.remove(A);
		//tests remove Team
		assertReadArray("Here are all of the Teams",uriBase,data);
	}


	@Test
	void testReadTeam()
	{
		fail("Not yet implemented");
	}

	@Test
	void testUpdateTeamHttpServletRequestStringRDesc()
	{
		fail("Not yet implemented");
	}

	@Test
	void testUpdateTeamHttpServletRequestString()
	{
		fail("Not yet implemented");
	}

	@Test
	void testCreateClass()
	{
		fail("Not yet implemented");
	}

	@Test
	void testReadClass()
	{
		fail("Not yet implemented");
	}

	@Test
	void testUpdateClassHttpServletRequestStringStringRDesc()
	{
		fail("Not yet implemented");
	}

	@Test
	void testDeleteClass()
	{
		fail("Not yet implemented");
	}

	@Test
	void testCreateObject()
	{
		fail("Not yet implemented");
	}

	@Test
	void testReadObject()
	{
		fail("Not yet implemented");
	}

	@Test
	void testUpdateClassHttpServletRequestStringStringStringJsonNode()
	{
		fail("Not yet implemented");
	}

	@Test
	void testDeleteObject()
	{
		fail("Not yet implemented");
	}

}
