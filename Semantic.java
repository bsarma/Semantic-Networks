

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Semantic {
	public static void main(String[] args) throws Exception 
	{
		String str1, str2, str3, str4, str5, str6, str7, str8, str9;

		BufferedReader in1 = new BufferedReader(new FileReader("flashlight.txt")); // read input file

		List<String> IsAFact1 = new ArrayList<String>(); //list to store values
		List<String> IsAFact2 = new ArrayList<String>(); //list to store value type
		List<String> ConnectedFact1 = new ArrayList<String>(); //list to store connected facts' first value
		List<String> ConnectedFact2 = new ArrayList<String>(); //list to store connected facts' second value
		List<Double> Activation = new ArrayList<Double>(); //list to store activation level corresponding to each value
		List<Integer> Index = new ArrayList<Integer>(); //list to store indices returned for all matching value types for each value  
		List<String> Index1 = new ArrayList<String>(); //list to store all matching values which are connected
		List<String> Index2 = new ArrayList<String>(); //list to store all matching values which are connected

		while ((str1 = in1.readLine()) != null)
		{ 
			String s1 = str1.toUpperCase(); //convert to uppercase for happiness.txt
		
			if(s1.startsWith("(FACT")) //parse facts
			{
				String[] temp = s1.split("\\s+");
				String temp3 = temp[3].replaceAll("\\)\\)", "");

				if(temp[1].equals("(IS-A")) // fact Is-A type
				{
					IsAFact1.add(temp[2]);
					IsAFact2.add(temp3);
					Activation.add((double) 100); //initialize activation levels of all values to 100
				}
				if(temp[1].equals("(CONNECTED")) //fact connected type
				{
					ConnectedFact1.add(temp[2]); 
					ConnectedFact2.add(temp3);
				}
			}
		} 
		

		BufferedReader in2 = new BufferedReader(new FileReader("flashlight.txt")); // read input file
		while ((str2 = in2.readLine()) != null)
		{
			String s2 = str2.toUpperCase(); //convert to uppercase for happiness.txt
			
			if(s2.startsWith("(QUERY")) //parse queries
			{
				double updated_activation;
				String[] temp = s2.split("\\s+");
				if(temp[1].equals("(CONNECTED")) //parse "connected" queries
				{
					str3 = temp[2];
					int p = IsAFact1.indexOf(str3);
					updated_activation = (double)(Activation.get(p) + (0.50 * Activation.get(p))); //update activation level of queried node
					Activation.set(p, updated_activation);

					str4 = temp[3].replaceAll("\\)\\)", "");
					int q = IsAFact1.indexOf(str4);
					updated_activation = (double)(Activation.get(q) + (0.50 * Activation.get(q))); 
					Activation.set(q, updated_activation);

					String result = ConnectedCheck(str3, str4, ConnectedFact1, ConnectedFact2); //call function ConnectedCheck to retrieve connected value for query
					System.out.println(result);
				}
				updated_activation = 0;

				if(temp[1].equals("(IS-A")) //parse "Is-A" queries
				{
					str5 = temp[2];
					int r = IsAFact1.indexOf(str5);
					updated_activation = (double)(Activation.get(r) + (0.50 * Activation.get(r)));
					Activation.set(r, updated_activation);

					str6 = temp[3].replaceAll("\\)\\)", "");
					String result = IsACheck(str5, str6, IsAFact1, IsAFact2); //call function IsACheck to retrieve value type for query
					System.out.println(result);
				}
				updated_activation = 0;

				if(temp[1].equals("VALUE") && temp[2].equals("(IS-A")) //parse queries with value and Is-A
				{
					List<Double> ActivationTest = new ArrayList<Double>();
					List<Double> AT = new ArrayList<Double>();

					str7 = temp[4].replaceAll("\\)\\)", "");
					Index = SearchValuesIsA(str7, IsAFact1, IsAFact2); 
					
					if(Index.get(0) == -1)
						System.out.println("None");
					else
					{
						for(int j = 0; j < Index.size(); j++)
						{
							updated_activation = (double)(Activation.get(Index.get(j)) + (0.25 * Activation.get(Index.get(j)))); //update activation levels of neighboring nodes
							Activation.set(Index.get(j), updated_activation);
							ActivationTest.add(Activation.get(Index.get(j)));
						}
						AT = ActivationTest;
						Collections.sort(ActivationTest); // sort by activation levels of nodes  
						double max = ActivationTest.get(ActivationTest.size()-1); //index for node with maximum activation level
						System.out.println(IsAFact1.get(Index.get(AT.indexOf(max))));
						
						updated_activation = (double)(Activation.get(Index.get(AT.indexOf(max))) + (0.20 * Activation.get(Index.get(AT.indexOf(max)))));
						Activation.set(Index.get(AT.indexOf(max)), updated_activation);
					}
				}
				Index.clear();
				updated_activation = 0;


				if(temp[1].equals("VALUE") && temp[2].equals("(AND") && (temp.length == 9))
				{
					List<String> test = new ArrayList<String>();
					List<Double> ActivationTest = new ArrayList<Double>();
					List<String> AT = new ArrayList<String>();
					List<String> test1 = new ArrayList<String>();

					if(temp[3].equals("(IS-A"))
					{
						str8 = temp[5].replaceAll("\\)", "");
						Index = SearchValuesIsA(str8, IsAFact1, IsAFact2);
						
						if(Index.get(0) != -1)
						{
							for(int j = 0; j < Index.size(); j++)
							{
								updated_activation = (double)(Activation.get(Index.get(j)) + (0.25 * Activation.get(Index.get(j)))); //update activation level for neighboring nodes
								Activation.set(Index.get(j), updated_activation);
								test.add(IsAFact1.get(Index.get(j)));
							}
						}
					}
					updated_activation = 0;
					
					if(temp[6].equals("(CONNECTED"))
					{
						str9 = temp[8].replaceAll("\\)+", "");
						if(IsAFact1.contains(str9))
						{
							updated_activation = (double)(Activation.get(IsAFact1.indexOf(str9)) + (0.25 * Activation.get(IsAFact1.indexOf(str9))));
							Activation.set(IsAFact1.indexOf(str9), updated_activation);
							Index1 = SearchValuesConnectedFact(str9, ConnectedFact1, ConnectedFact2, Activation);
						}
					}

					if((Index.get(0) == -1) || (Index1.get(0).equals("-1")))
						System.out.println("None");
					else
					{
						for(int j = 0; j < Index1.size(); j++)
						{
							if(test.contains(Index1.get(j)))
							{
								int r = IsAFact1.indexOf(Index1.get(j));
								ActivationTest.add(Activation.get(r));
								AT.add((Activation.get(r)).toString());
								test1.add(Index1.get(j));
							}
						}
						
						if(ActivationTest.size() == 0)
						{
							System.out.println("None");
						}
						else
						{
							Collections.sort(ActivationTest); 
							double max = ActivationTest.get(ActivationTest.size()-1);
							String t = String.valueOf(max);
							int l = AT.indexOf(t);
							System.out.println(test1.get(l));
						}
					}
				}
				Index.clear();
				Index1.clear();
				updated_activation = 0;
				
				if(temp[1].equals("VALUE") && temp[2].equals("(AND") && (temp.length > 9))
				{
					List<String> test = new ArrayList<String>();
					List<Double> ActivationTest = new ArrayList<Double>();
					List<Double> AT = new ArrayList<Double>();

					str8 = temp[5].replaceAll("\\)", "");
					Index = SearchValuesIsA(str8, IsAFact1, IsAFact2);

					if(Index.get(0) != -1)
					{
						for(int j = 0; j < Index.size(); j++)
						{
							updated_activation = (double)(Activation.get(Index.get(j)) + (0.25 * Activation.get(Index.get(j))));
							Activation.set(Index.get(j), updated_activation);
							test.add(IsAFact1.get(Index.get(j)));
						}
					}
					updated_activation = 0;
					
					str9 = temp[8].replaceAll("\\)", ""); 
					
					if(IsAFact1.contains(str9))
					{
						updated_activation = (double)(Activation.get(IsAFact1.indexOf(str9)) + (0.50 * Activation.get(IsAFact1.indexOf(str9))));
						Activation.set(IsAFact1.indexOf(str9), updated_activation);
					}
					Index1 = SearchValuesConnectedFact(str9, ConnectedFact1, ConnectedFact2, Activation);
					

					String str10 = temp[11].replaceAll("\\)+", "");
					if(IsAFact1.contains(str10))
					{
						updated_activation = (double)(Activation.get(IsAFact1.indexOf(str10)) + (0.50 * Activation.get(IsAFact1.indexOf(str10))));
						Activation.set(IsAFact1.indexOf(str10), updated_activation);
					}
					Index2 = SearchValuesConnectedFact(str10, ConnectedFact1, ConnectedFact2, Activation);

					if((Index.get(0) == -1) || (Index1.get(0).equals("-1")) || (Index2.get(0).equals("-1")))
						System.out.println("None");
					else
					{
						for(int j = 0; j < Index1.size(); j++)
						{
							if(test.contains(Index1.get(j)) && Index2.contains(Index1.get(j)))
							{
								ActivationTest.add(Activation.get(IsAFact1.indexOf(Index1.get(j))));
							}
						}

						if(ActivationTest.size() == 0)
						{
							System.out.println("None");
						}
						else
						{
							AT = ActivationTest;
							Collections.sort(ActivationTest);  
							double max = ActivationTest.get(ActivationTest.size()-1);
							System.out.println(test.get(AT.indexOf(max)));
						}
					}
				}
			}
		}
		
		
	}

	/*function to calculate result for (query (connected node1 node2))
	input - string from query, lists with connected values information
	output - yes or no depending on whether values are connected*/
	public static String ConnectedCheck(String str3, String str4, List<String> ConnectedFact1, List<String> ConnectedFact2)
	{
		String answer = null;
		for(int i = 0; i < ConnectedFact1.size(); i++)
		{
			if(str3.equals(ConnectedFact1.get(i)) && str4.equals(ConnectedFact2.get(i)))
			{
				answer = "Yes";
				break;
			}
			else
				answer = "No";

			if (str3.equals(ConnectedFact2.get(i)) && str4.equals(ConnectedFact1.get(i)))
			{
				answer = "Yes";
				break;
			}
			else
				answer = "No";	
		}
		return answer;
	}

	/*function to calculate result for (query (is-a node type))
	input - string from query, lists with fact Is-A information
	output - yes or no depending on whether values and their corresponding types match*/
	public static String IsACheck(String str5, String str6, List<String> IsAFact1, List<String> IsAFact2)
	{
		String answer = null;
		for(int i = 0; i < IsAFact1.size(); i++)
		{
			if(str5.equals(IsAFact1.get(i)) && str6.equals(IsAFact2.get(i)))
			{
				answer = "Yes";
				break;
			}
			else
				answer = "No";
		}
		return answer;
	}

	/*function to search for values in IsA array
	input - string from query, lists with fact Is-A information
	output - lists of value types that match with the given value*/
	public static List<Integer> SearchValuesIsA(String str7, List<String> IsAFact1, List<String> IsAFact2)
	{
		List<Integer> Index = new ArrayList<Integer>();
		for(int i = 0; i < IsAFact2.size(); i++)
		{
			if(str7.equals(IsAFact2.get(i)))
			{
				//System.out.println(str7 + "----" + IsAFact1.indexOf(IsAFact1.get(i)));
				Index.add(IsAFact1.indexOf(IsAFact1.get(i)));
			}
		}
		if(Index.size() == 0)
			Index.add(-1);
		return Index;
	}

	/*function to search for values in ConnectedFact array
	input - string from query, lists with fact connected fact information
	output - lists of connected facts that match with the given value*/
	public static List<String> SearchValuesConnectedFact(String str9, List<String> ConnectedFact1, List<String> ConnectedFact2, List<Double> Activation)
	{
		List<String> I = new ArrayList<String>();
		
		for(int i = 0; i < ConnectedFact2.size(); i++)
		{
			if(str9.equals(ConnectedFact1.get(i)))
			{
				I.add(ConnectedFact2.get(i));
				//double updated_activation = (double)(Activation.get(i) + (0.50 * Activation.get(i)));
				//Activation.set(i, updated_activation);
			}
			if(str9.equals(ConnectedFact2.get(i)))
			{
				I.add(ConnectedFact1.get(i));
				//double updated_activation = (double)(Activation.get(i) + (0.50 * Activation.get(i)));
				//Activation.set(i, updated_activation);
			}
		}
		if(I.size() == 0)
			I.add("-1");
		return I;
	}

}
