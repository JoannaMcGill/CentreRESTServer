package restPasswordServer;

import java.util.HashSet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RESTPasswordServer
{

	public static void main(String[] args)
	{
		SpringApplication.run(RESTPasswordServer.class, args);
	}

	@GetMapping("/")
	public String hello()
	{
		return """	
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
</html>""";
	}
	
	
	HashSet<String> requesters = new HashSet<>();
	HashSet<String> auths = new HashSet<>();
	
	
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
	
	

}
