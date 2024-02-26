package simpleRESTServer;

import java.util.ArrayList;
import java.util.HashMap;

public class RClass
{
	String name;
	String description;
	RTeam parent;
	HashMap<String,RObject> objects = new HashMap<>();
	
	
	
	
	
	/**
	 * @param name
	 * @param description
	 * @param parent
	 */
	public RClass(String name, String description, RTeam parent)
	{
		super();
		this.name = name;
		this.description = description;
		this.parent = parent;
	}

	public String getURI()
	{
		return parent.getURI()+"/"+name;
	}
	
	public RDesc getRDesc()
	{
		return new RDesc(name,description,getURI());
	}

	public ArrayList<RDesc> getDescs()
	{
		ArrayList<RDesc> descs = new ArrayList<>();
		
		for(RObject rObject:objects.values())
		{
			descs.add(rObject.getRDesc());
		}
		
		return descs;
	}
	
	
	
	

}
