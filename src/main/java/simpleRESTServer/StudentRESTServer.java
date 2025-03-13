package simpleRESTServer;
import java.util.ArrayList;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
public class StudentRESTServer
{
	//archive
	public record ArchiveCourse(String request, 
			Boolean successful, 
			String message,
			ArrayList<Courses>data) {};
	public record Courses(String name,
			String description,
			String location) {};

	public record Course(String request,
			Boolean succesful,
			String message,
			CourseDetails data) {};
			
	public record CourseDetails(String season,
			int year,
			String dept,
			String num,
			String section,
			String name,
			String instructor,
			String meetingTime,
			String building,
			String roomNumber,
			String id) {};
	//course
	//course details, no array list, just one course data object
	//course data 
	
	//for all courses
		//loop through each location
	//get course data

	public static void main(String[] args)
	{
		
		RestClient client = RestClient.create();
		
		String uriBase = "http://localhost:9000/v1";
		/*
		RDesc desc = new RDesc("jm","Joanna McGill","");
		
		
		RResponseDesc resp = client.post()
		.uri(uriBase+"/jm")//archive/course
		.contentType(MediaType.APPLICATION_JSON)
		.body(desc)
		.retrieve()
		.body(RResponseDesc.class);
		*/
		ArchiveCourse response = client.get()
		.uri(uriBase +"/Archive/course")
		.retrieve()
		.body(ArchiveCourse.class);
		
		System.out.println(response.data.size());
		
		for(int i = 0; i < response.data.size();i++)
		{
			Course resp = client.get()
			.uri(response.data.get(i).location)
			.retrieve()
			.body(Course.class);
			System.out.println(resp.data.name);
			System.out.println(resp.data.instructor);
		}
		
/*
		String response = client.get()
		.uri(uriBase+"auth/mkb/"+pw)
		.retrieve()
		.body(String.class);
*/
/*		if(resp.get("successful").asBoolean())
			resp.get(data)
	*/	
		
		//System.out.println(resp);
		
		
		
		
		
		
		
	}

}
