package simpleRESTServer;

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

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class SimpleRESTServer
{
	
	public static void main(String[] args)
	{
		SpringApplication.run(SimpleRESTServer.class, args);
	}

	@GetMapping("/")
	public String hello(HttpServletRequest request)
	{
		
		
		return getURI(request);
				/*"""	
<html>
<body>
<h1>Password server</h1>
<p>This is a REST server for retrieving and storing passwords.  Here are the following links to services provide on the server.</p>
<ol>
<li><a href="/request">/request"</a> a JSON list of all the people that have requested passwords.
<li><a href="/auth">/auth"</a> a JSON list of all the people that have successfully submitted their password
<li><a href="/request/janedoe">/request/:username"</a> A request for a password for the username provided.
<li><a href="/auth/janedoe/4132">/request/:username/:password"</a> Attempts to authenticate the password for the username provided.
</ol>
</html>""";*/
	}
	
	
	HashMap<String,RTeam> teams = new HashMap<>();
	
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
			return new RResponse(request,false,"Team "+teamname+" does not exists");
		}

		team.name = desc.name();
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
			return new RResponse(request,false,"Team "+teamname+" does not exists");
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
	
	
	
	/*
	private String getPassword(String user)
	{
		return "" + Math.abs(user.hashCode()) % 10000;
	}
	
	
	@GetMapping("/request")
	public String getRequesters()
	{
		return "Requesters: "+requesters.toString();
	}
	
	@GetMapping("/request/{username}")
	public String requestPassword(@PathVariable String username)
	{
		
		requesters.add(username);
		
		return getPassword(username);
	}

	@GetMapping("/auth")
	public String getAuths()
	{
		return "Authorized: "+auths.toString();
	}

	@GetMapping("/auth/{username}/{password}")
	public String checkPassword(@PathVariable String username,@PathVariable String password)
	{
		String pw = getPassword(username);
		
		if(pw.equals(password))
		{
			auths.add(username);
			return "Authentication Successful";
		}
		else
		{
			return "Authentication Failed";
		}
	}
	
	*/
}
