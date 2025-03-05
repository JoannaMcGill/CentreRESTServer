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
	
	String uriBase = "http://localhost:9000/v1";
	RDesc teamDesc = new RDesc("MKB","Michael's Team",uriBase+"/MKB");
	RDesc addDesc = new RDesc("address","Personal Address",teamDesc.location()+"/address");
	RDesc todoDesc = new RDesc("todo","Todo List Items",teamDesc.location()+"/todo");
	
	RDesc A = new RDesc("A","A Team",uriBase+"/A");
	RDesc B = new RDesc("B","B Team",uriBase+"/B");
	RDesc C = new RDesc("C","C Team",uriBase+"/C");
	
	RDesc x = new RDesc("x","x Class",uriBase+"/MKB/x");
	RDesc y = new RDesc("y","y Class",uriBase+"/MKB/y");
	RDesc z = new RDesc("z","z Class",uriBase+"/MKB/z");
	
	
	RDesc t1 = new RDesc("t1","todo",uriBase+"/MKB/todo/t1");
	RDesc t2 = new RDesc("t2","todo",uriBase+"/MKB/todo/t2");
	RDesc t3 = new RDesc("t3","todo",uriBase+"/MKB/todo/t3");
	
	
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
	
	private void assertRReponseShort(RResponseShort desc,String request, boolean successful, String message)
	{
		assertEquals(request,desc.request());
		assertEquals(successful,desc.successful());
		assertEquals(message,desc.message());
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
	
	
	
	private void assertReadObject(String message,RDesc desc,Object data)
	{

		RResponseTodo res = client.get()
				.uri(desc.location())
				.retrieve()
				.body(RResponseTodo.class);
		
		
		assertEquals(desc.location(),res.request());
		assertTrue(res.successful());
		assertEquals(message,res.message());
		
		assertEquals(data,res.data());
	
	}
	
	private void assertCreateObject(String message,RDesc desc,Object data)
	{
		RResponseDesc createDesc = client.post()
				.uri(desc.location())
				.contentType(MediaType.APPLICATION_JSON)
				.body(data)
				.retrieve()
				.body(RResponseDesc.class);
		
		assertRReponseDesc(createDesc,desc.location(),true,message,desc);
		
			
		
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
	
	private void assertDeletePath(RDesc desc,String message,boolean success)
	{
		RResponseShort res = client.delete()
		.uri(desc.location())
		.retrieve()
		.body(RResponseShort.class);
		
		assertRReponseShort(res,desc.location(),success,message);
		
		
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
		
		//test bad delete
		assertDeletePath(C,"Team C does not exist",false);

		//Test bad put not present
		RDesc ABad = new RDesc("B","A Team 2",uriBase+"/A");

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
		
		//test good delete
		assertDeletePath(C,"Team C has been removed",true);
		data.remove(A);
		//tests remove Team
		assertReadArray("Here are all of the Teams",uriBase,data);
	}

	
	@Test
	void testGetClasses()
	{
		
	
		//Test Create Classes
		assertCreate("Class "+x.name()+" successfully created",x);
		assertCreate("Class "+y.name()+" successfully created",y);
		
		ArrayList<RDesc> data = new ArrayList<RDesc>();
		data.add(todoDesc);
		data.add(addDesc);
		data.add(x);
		data.add(y);
		
	
		//Test get all Classes
		assertReadArray(teamDesc.description(),teamDesc.location(),data);
		
		//test bad delete
		assertDeletePath(z,"Class z does not exist",false);

		//Test bad put not present

		RResponseShort putDesc = client.put()
				.uri(z.location())
				.contentType(MediaType.APPLICATION_JSON)
				.body(z)
				.retrieve()
				.body(RResponseShort.class);
			
			
		assertFalse(putDesc.successful);
		assertEquals("Class z does not exist", putDesc.message);
		
		
		//Test bad put
		RDesc xBad = new RDesc("y","x Class 2",uriBase+"/MKB/x");

		putDesc = client.put()
			.uri(x.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(xBad)
			.retrieve()
			.body(RResponseShort.class);
		
		
		assertFalse(putDesc.successful);
		assertEquals("Class y already exists", putDesc.message);
		
		
		//Test good description change only put
		RDesc xGood = new RDesc("x","x Team 2",uriBase+"/MKB/x");

		RResponseDesc goodPut1 = client.put()
			.uri(x.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(xGood)
			.retrieve()
			.body(RResponseDesc.class);
		
		assertRReponseDesc(goodPut1,x.location(), true, "Class x has been updated",xGood);

		//test good name and description change on put
		RResponseDesc goodPut2 = client.put()
			.uri(x.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(z)
			.retrieve()
			.body(RResponseDesc.class);
		
		assertRReponseDesc(goodPut2,x.location(), true, "Class x has been updated",z);
		
		//test good delete
		assertDeletePath(z,"Class z has been removed",true);
		data.remove(x);
		//tests remove Team
		assertReadArray(teamDesc.description(),teamDesc.location(),data);
	}
	
	
	@Test
	void testGetObjects()
	{
		//This test doesn't investigate how the objects are stored, could be accidental test, need another.
	
		//Test Create Classes
		assertCreate("Object "+t1.name()+" successfully created",t1);
		assertCreate("Object "+t2.name()+" successfully created",t2);
		
		ArrayList<RDesc> data = new ArrayList<RDesc>();
		data.add(t1);
		data.add(t2);
		
	
		//Test get all Objects
		assertReadArray(todoDesc.description(),todoDesc.location(),data);
		
		//test bad delete
		assertDeletePath(t3,"Object t3 does not exist",false);

		//Test bad put not present

		RResponseShort putDesc = client.put()
				.uri(t3.location())
				.contentType(MediaType.APPLICATION_JSON)
				.body(t3)
				.retrieve()
				.body(RResponseShort.class);
			
			
		assertFalse(putDesc.successful);
		assertEquals("Object t3 does not exist", putDesc.message);
		
		
		//Test bad put
		RDesc t1Bad = new RDesc("t1","t1 Object 2",uriBase+"/MKB/barbells/t1");

		putDesc = client.put()
			.uri(t1Bad.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(t1Bad)
			.retrieve()
			.body(RResponseShort.class);
		
		
		assertFalse(putDesc.successful);
		assertEquals("Class barbells does not exist", putDesc.message);
		
		

		//test good name and description change on put
		RResponseShort goodPut2 = client.put()
			.uri(t1.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(t3)
			.retrieve()
			.body(RResponseShort.class);
		
		assertRReponseShort(goodPut2,t1.location(), true, "Object t1 has been updated");
		
		//test good delete
		assertDeletePath(t1,"Object t1 has been removed",true);
		data.remove(t1);
		//tests remove Team
		assertReadArray(todoDesc.description(),todoDesc.location(),data);
	}

	@Test
	void testGetObjectsReal()
	{

		
		Todo t1Data = new Todo("sleep",10);
		Todo t2Data = new Todo("eat",7);
		Todo t3Data = new Todo("crow",5);
		
		//Test Create Classes
		assertCreateObject("Object "+t1.name()+" successfully created",t1,t1Data);
		assertCreateObject("Object "+t2.name()+" successfully created",t2,t2Data);
		
		assertReadObject(todoDesc.description(),t1,t1Data);
		assertReadObject(todoDesc.description(),t2,t2Data);
		
		ArrayList<RDesc> data = new ArrayList<RDesc>();
		data.add(t1);
		data.add(t2);
		
	
		//Test get all Objects
		assertReadArray(todoDesc.description(),todoDesc.location(),data);
		
			

		//test good name and description change on put
		RResponseShort goodPut2 = client.put()
			.uri(t1.location())
			.contentType(MediaType.APPLICATION_JSON)
			.body(t3Data)
			.retrieve()
			.body(RResponseShort.class);
		
		assertRReponseShort(goodPut2,t1.location(), true, "Object t1 has been updated");
		assertReadObject(todoDesc.description(),t1,t3Data);
		
		
		//test good delete
		assertDeletePath(t1,"Object t1 has been removed",true);
		data.remove(t1);
		//tests remove Team
		assertReadArray(todoDesc.description(),todoDesc.location(),data);
	}

}
