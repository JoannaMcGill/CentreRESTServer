package simpleRESTServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class CentreRESTServer
{
	
	
	
	
	
	public static void main(String[] args)
	{
		SpringApplication.run(CentreRESTServer.class, args);
	}
	
	HashMap<String,RTeam> teams = new HashMap<>();
	
	
	
	
	public CentreRESTServer()
	{
		String uriBase = "http://localhost:9000/v1/";
		String teamName = "Archive";
		RTeam team = new RTeam(teamName,"Archived Centre Catalog",uriBase+teamName);
		
		teams.put(teamName, team);
		uploadCourses(team);
	}
	
	
	public record RawMeeting(String instructor,String day, String startTime, String endTime,
			String building, String description, String roomNumber,
			int startHour,int startMin, int endHour,int endMin, double contact,boolean isDup
			) {}
	
	
	
	public record RawCourse(String season, int year, String Ayear,
			String dept, String num,String section,String name,
			double credits,int registered,int max,RawMeeting [] meetings,
			double contact,boolean isLab, String code,double teach_credit,double studentDensityTC			
			) {}
	
	public record RefCourse(String season, int year, 
			String dept, String num,String section,String name,
			String instructor,String meetingTime,
			String building, String roomNumber
			) {}
	
	
	public record Room (String building, String roomNumber) {}
	public HashMap<Room,Integer> roomDict = new HashMap();
	
	public void uploadCourses(RTeam team)
	{
		String courseName = "course";
		String courseURI = team.getURI()+"/"+courseName;
		
		team.createClass(courseURI,courseName, "All of the Courses");
				
		InputStream is = this.getClass().getResourceAsStream("catalog_joined.json");
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<RawCourse> rawCourses = null;
		try
		{
			rawCourses = 
					mapper.readValue(is,new TypeReference<ArrayList<RawCourse>>() {});
		} catch (StreamReadException e)
		{
			e.printStackTrace();
		} catch (DatabindException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
			
		for(RawCourse c:rawCourses)
		{
			addCourse(team,courseName, c);
		}
		
			
			
	}
	
	
	
	
	
	public RefCourse refineCourse(RawCourse raw)
	{
		if(raw.meetings.length!=1)
		{
			return null;
		}

		RawMeeting meet= raw.meetings()[0];
		String meetTime = meet.day()+" "+meet.startTime()+"-"+meet.endTime();
		
		Room r = new Room(meet.building(),meet.roomNumber());

		roomDict.put(r,roomDict.getOrDefault(r, 0)+1);
	
		
		return new RefCourse(raw.season(), raw.year(), 
				raw.dept(), raw.num(),raw.section(),raw.name(),
				meet.instructor(),meetTime,
				meet.building(), meet.roomNumber()
				);	

	}
	
	private String genCourseName(RefCourse c)
	{
		return c.dept()+c.num+c.section()+"-"+c.year()+c.season();
	}

	private void addCourse(RTeam team, String className, RawCourse c)
	{
		RefCourse refined = refineCourse(c);
		if(refined == null) { return; }
		
		String name = genCourseName(refined);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.valueToTree(refined);
		
		team.createObject(team.uri+"/"+className+"/"+name, name, className, node);
		
	}





	@GetMapping("/")
	public String hello(HttpServletRequest request)
	{
		
		
		return """	
<html>
<head>
<style>
table, td, th {
  border: 1px solid;
}

table {
  width: 100%;
  border-collapse: collapse;
}
</style>
</head>
<body>
<h1>Simple REST Database</h1>
<p>This is a REST server for retrieving and storing JSON Objects.  You can create different categories for the Objects you place in the Server.
The highest level is the "team".  Teams should contain an entire project.  The next level of organization is the "class"  
The class levels should be used to store objects that share a class.  When you create a team or class you must include a RDesc to provide a name and description.
The final level is the Object.  Use a unique id in the URI to describe the object and pass the JSON representation in the body of your post request.





Here are the following links to services provide on the server.</p>
<table>
<tr><th>Path</th><th>Purpose</th><th>Command</th><th>Description</th></tr>
<tr><td>/v1</td><td>READ</td><td>Get<td>List all Teams</td></tr>
<tr><td>/v1/:team</td><td>Create</td><td>Post<td>Create a new team, Pass a RDesc in the body</td></tr>
<tr><td></td><td>Read</td><td>Get<td>Retrieve all the Classes in the Team</td></tr>
<tr><td></td><td>Update</td><td>Put<td>Update the name/description of the Team</td></tr>
<tr><td></td><td>Delete</td><td>Delete<td>Deletes the Team as well as all the classes and objects contained in that team</td></tr>
<tr><td>/v1/:team/:class</td><td>Create</td><td>Post<td>Create a new class in this team, Pass a RDesc in the body</td></tr>
<tr><td></td><td>Read</td><td>Get<td>Retrieve all Descriptions of the Objects in this Class</td></tr>
<tr><td></td><td>Update</td><td>Put<td>Update the name/description of the Class</td></tr>
<tr><td></td><td>Delete</td><td>Delete<td>Deletes the Class as well as all the objects contained in that class</td></tr>
<tr><td>/v1/:team/:class/:object</td><td>Create</td><td>Post<td>Create a new object in this class, Pass the object you want to store as JSON in the body</td></tr>
<tr><td></td><td>Read</td><td>Get<td>Retrieve the JSON object stored in the Server</td></tr>
<tr><td></td><td>Update</td><td>Put<td>Update the JSON object for this object</td></tr>
<tr><td></td><td>Delete</td><td>Delete<td>Deletes the Object</td></tr>
</table>
</html>""";
	}
	
	

	
	private String getURI(HttpServletRequest request)
	{
		String query =  request.getQueryString();
		if(query == null)
		{
			return request.getRequestURL().toString();
		}
		else
		{
			return request.getRequestURL().append(request.getQueryString()).toString();
		}
	}

	
	@GetMapping("/v1")
	public RResponse getTeams(HttpServletRequest req)
	{
		
		String request = getURI(req);
		ArrayList<RDesc> descs=new ArrayList<RDesc>();
		
		for(RTeam team:teams.values())
		{
			descs.add(team.getRDesc());
		}
		
		return new RResponse(request,true,"Here are all of the Teams",descs);
		
	}
	
	
	/* CRUD for Teams */
	
	
	@PostMapping("/v1/{teamname}")
	public RResponse createTeam(HttpServletRequest req,
			@PathVariable String teamname,
			@RequestBody RDesc desc)
	{
		String request = getURI(req);

		
		if(teams.containsKey(teamname))
		{
			return new RResponse(request,false,"Team "+teamname+" already exists");
		}
		
		RTeam team = new RTeam(teamname,desc.description(),request);
		
		teams.put(teamname, team);
		return new RResponse(request,true,"Team "+teamname+" successfully created",
				team.getRDesc()
				);
		
	}
	
	@GetMapping("/v1/{teamname}")
	public RResponse readTeam(HttpServletRequest req,
			@PathVariable String teamname)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		

		ArrayList<RDesc> descs = team.getClassDescriptions();
		
		
		return new RResponse(request,true,team.description,descs);
	}

	@PutMapping("/v1/{teamname}")
	public RResponse updateTeam(HttpServletRequest req,
			@PathVariable String teamname,
			@RequestBody RDesc desc)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exist");
		}

		if(!teamname.equals(desc.name()) && teams.containsKey(desc.name()))
		{
			return new RResponse(request,false,"Team "+desc.name()+" already exists");
		}
		
		if(!teamname.equals(desc.name()))
		{
			teams.remove(teamname);
			teams.put(desc.name(), team);
			
			team.uri = team.uri.replaceFirst(team.name+"$", desc.name()); //("/v1/"+team.name;
			team.name = desc.name();
		}
		
		team.description = desc.description();
		
		return new RResponse(request,true,"Team "+teamname+" has been updated",team.getRDesc());
	}
	
	
	@DeleteMapping("/v1/{teamname}")
	public RResponse updateTeam(HttpServletRequest req,
			@PathVariable String teamname)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exist");
		}

		teams.remove(teamname);
		return new RResponse(request,true,"Team "+teamname+" has been removed");
	}
	
	/* Crud for Classes */
	
	
	@PostMapping("/v1/{teamname}/{classname}")
	public RResponse createClass(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname,
			@RequestBody RDesc desc)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.createClass(request,classname,desc.description());
		
	}
	
	@GetMapping("/v1/{teamname}/{classname}")
	public RResponse readClass(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.readClass(request,classname);
	}
	
	@PutMapping("/v1/{teamname}/{classname}")
	public RResponse updateClass(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname,
			@RequestBody RDesc desc)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
	
		return team.updateClass(request,classname,desc.name(),desc.description());
		
	}
	
	@DeleteMapping("/v1/{teamname}/{classname}")
	public RResponse deleteClass(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.deleteClass(request,classname);
	}
	
	
	/* CRUD for Objects */
	
	
	@PostMapping("/v1/{teamname}/{classname}/{objname}")
	public RResponse createObject(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname,
			@PathVariable String objname,
			@RequestBody JsonNode data)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.createObject(request,objname,classname,data);
		
	}
	
	@GetMapping("/v1/{teamname}/{classname}/{objname}")
	public RResponse readObject(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname,
			@PathVariable String objname)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.readObject(request,classname,objname);
	}
	
	@PutMapping("/v1/{teamname}/{classname}/{objname}")
	public RResponse updateClass(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname,
			@PathVariable String objname,
			@RequestBody JsonNode data)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.updateObject(request,classname,objname,data);
		
	}
	
	@DeleteMapping("/v1/{teamname}/{classname}/{objname}")
	public RResponse deleteObject(HttpServletRequest req,
			@PathVariable String teamname,
			@PathVariable String classname,
			@PathVariable String objname)
	{
		String request = getURI(req);

		RTeam team = teams.get(teamname);
		
		if(team == null)
		{
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}
		
		
		return team.deleteObject(request,classname,objname);
	}
	
	
	
}
