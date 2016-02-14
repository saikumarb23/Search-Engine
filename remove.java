public static String Remove(String s)
	{
	Vector<String> vec= new Vector<String>();
	vec.add(s);
	s=" ";
	for(int i=0;i<vec.size();i++)
	{
	s=vec.get(i).replaceAll("my|name|is| a|an|of|the|or|of|for|in|as|at","").trim();
	}
	return s;
	}
