import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

public class linkStateRouting {
static int num_routers;
static int distanceArray[];//Array to hols minimum distance form source to each destination
static int[][] matrix;// cost adjacency matrix for network represented as a graph
static int[] pathArray;//Array to hold optimal path
static int sourceNode=-1;
static int destination=-1;
static boolean[] shortpathVertices; //Array to indicate visited node in dijkstra's algorithm
static int removeRouter=-1;
static ArrayList <Integer> removedRouters = new ArrayList<Integer>();
     /**
	 * @param args
	 * @throws FileNotFoundException
	 * creates network topology 
	 */
public void createTopology() throws FileNotFoundException{
	try{
	String token1 = "";
	System.out.print("Please enter the path of input file alongwith file name: ");
	Scanner sc=new Scanner(System.in);
	String path=sc.next();
    Scanner inFile1 = new Scanner(new File(path));
    ArrayList<String> temps = new ArrayList<String>();
    while (inFile1.hasNext()) {
      token1 = inFile1.next();
      temps.add(token1);
    }
    inFile1.close();
    String[] tempsArray = temps.toArray(new String[0]);
    int[] numbers = new int[tempsArray.length];
    for(int i = 0;i < tempsArray.length;i++)
    {
       numbers[i] = Integer.parseInt(tempsArray[i]);
    }
    int array_length=numbers.length;

    num_routers=(int) Math.sqrt(array_length);
    distanceArray=new int[num_routers];
	pathArray=new int[num_routers];
    matrix=new int[num_routers][num_routers];
    System.out.println("\nReview original topology matrix:\n");
    for(int m=0;m<num_routers;m++){
    	for(int n=0;n<num_routers;n++){
    		matrix[m][n]=numbers[(m*num_routers+n)];
    		System.out.print(matrix[m][n] + " ");
    	}
    	System.out.println(" ");
    }
	}
	catch(FileNotFoundException filenotfound){
		System.out.println("Please ensure the path file is correct. Also make sure there are no spaces in path");
	}
}

/**
 * dijkstrasAlgo calculates shortest path and min distance from 'sourceNode' to all other running routers
 */
	public static void dijkstrasAlgo(){
		
		int minVertice=-1; 
		int minValue=Integer.MAX_VALUE;
		shortpathVertices=new boolean[num_routers]; // variable to check if vertex is added in path or not
		for(int i=0;i<num_routers;i++){
			distanceArray[i]=Integer.MAX_VALUE;//cost initially set to infinite for all vertices
			shortpathVertices[i]=false;// All routers marked unvisited initially
			pathArray[i] = -2; // initialising Path array
		}
		pathArray[sourceNode]=-1;      
		distanceArray[sourceNode]=0;   //set to zero so that source node is selected due to minimum value
		for(int j=0;j<num_routers;j++){
			minValue=Integer.MAX_VALUE;
			 for (int v = 0; v < num_routers; v++)
				 if(!removedRouters.contains(v))
				 {
					 //choose router v such that it is not visited and distanceArray[v] is minimum
					 //Exclude routers listed in the removedRouters so that shortest path will not be calculated
		            if (shortpathVertices[v] == false && distanceArray[v] <= minValue)
		            {
		                minValue = distanceArray[v];
		                minVertice = v;
		            }
				 }
		        int u=minVertice;
		        shortpathVertices[u]=true;  // mark the current router with minimum distance as visited
		        for (int v = 0; v < num_routers; v++)
		        {
	                // Update distanceArray[v] only if is not in shortPathVertices, there is an
	                // edge from u to v, and total weight of path from sourceNode to
	                // v through u is smaller than current value of distanceArray[v]
		        	//Exclude routers listed in the removedRouters so that shortest path will not be calculated
		        	//PathArray[v] holds u if such that distance from v to u is less than current distance from v to u
		        	//for path from i to j PathArray is traced backwards by considering path[j] as previous router to j
		        	if(!removedRouters.contains(v))
		        	{
	                if (!shortpathVertices[v] && matrix[u][v]!=0 &&
	                        distanceArray[u] != Integer.MAX_VALUE &&
	                        distanceArray[u]+matrix[u][v] < distanceArray[v]&&matrix[u][v]!=-1){
	                    distanceArray[v] = distanceArray[u] + matrix[u][v];
	                    pathArray[v]=u;
	                }
		        	}
	        }
		    }
	}
	/**
	 * Prints forward connection table for SourceNode
	 * Excludes routers those are down
	 */
	public static void printConnectionTable()
	{
		int j,initialDestination;
		ArrayList intList=null;
		System.out.println();
		System.out.println("\n\nConnection table for router " + sourceNode);
		System.out.println("Destination   Interface");
		System.out.print("=======================");
		for(j=0;j< num_routers;j++){
			//trace shortest path from sourceNode to j'th router
			//print router next to sourceNode as interface
			//if j'th router is down or is same as source then no interface is shown
			//intList holds path from source to destination 
			if(sourceNode != j && !removedRouters.contains(j))
			{
				initialDestination=j;
				intList = new ArrayList();
				if(pathArray[initialDestination] == -2)
				{
					System.out.printf("\n% 5d            -",j);
				}
				else
				{	////tracing path backwards through pathArray
					while(pathArray[initialDestination]!=-1){
					intList.add(pathArray[initialDestination]);
					initialDestination=pathArray[initialDestination];
				}
				Collections.reverse(intList);
				if(intList.size() == 1) 
				{	//if j'th router is immediately next to the source
					//print j as the interface to j'th router
					intList.add(j);
					System.out.printf("\n% 5d            %d",j,j);
				}
				else  
					//if the path has more than 2 routers 
					//print router next to source(intList[1]) as the interface to router 'j'
					System.out.printf("\n% 5d            %d",j,intList.get(1));
				}
			}
			else
			{
				System.out.printf("\n% 5d            -",j);
			}
		}
	}
	
	/**
	 * ConnectionTableAll prints forward connection table for all running nodes  
	 */
	public void connectionTableAll(){
		int i;
		  //calculating shortest paths by taking each node as source 
		for(i=0;i<num_routers;i++)
		{
			if(!removedRouters.contains(i))  // Check if the router i is working
			{
			sourceNode = i;
			dijkstrasAlgo();    //calculate shortest path from i'th node
			printConnectionTable(); //print connection table
			}
		}
		sourceNode = -1;
		}
	/**
	 * srcDestinationPath inputs a destination router and finds shortest path from
	 * sourceNode to destination router
	 * Indicates if shortest path doesn't exist because a intermediate router is down
	 */
	public void srcDestinationPath(){
		Scanner s = new Scanner(System.in);
		int initialDestination;
		System.out.println("Enter the destination router: ");
		destination = s.nextInt();
		// check if distance for destination is not altered from it's initial value
		//indicating that shortest path to the destination doesn't exist
		if(distanceArray[destination] == Integer.MAX_VALUE)  
		{
			System.out.println("\npath from "+sourceNode+ " to "+destination+" doesn't exist as intermidiate router(s) are down");
		}
		else
		{
			initialDestination=destination;
			ArrayList intList = new ArrayList();
			//track path from destination to source
			while(pathArray[initialDestination]!=-1)
			{
				intList.add(pathArray[initialDestination]);
				initialDestination=pathArray[initialDestination];
			}
			//reverse the path
			Collections.reverse(intList); 
			System.out.print("The shortest path from router " + sourceNode+ " to router " + destination + " is: ");
			for(int m=0;m<intList.size();m++){
				System.out.print(intList.get(m)+"-");
			}
			System.out.print(destination);
			System.out.println("\n \nThe total cost " + "till destination router "+destination+ " is: " + distanceArray[destination] + "\n");
		}
	}
	/**
	 * removeRouter updates removesRouters list which indicates routers those are down
	 * If source/destination is chosen then it asks for new source/destination
	 */
	public void removeRouter(){
		int removedRouter,newSourceRouter,newDestinationRouter,initialDestination;
		ArrayList intList = null;
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the router number to be removed: ");
		removedRouter=sc.nextInt();
		//check if selected router is source
		if(removedRouter == sourceNode) 
		{
			System.out.println("Source node removed. Please select a new source node another:");
			newSourceRouter = sc.nextInt();
			removedRouters.add(removedRouter);
			if(removedRouters.contains(newSourceRouter))
			{
				System.out.println("Selected router is down.");
			}
			else
			{
				removedRouters.add(removedRouter);
				sourceNode=newSourceRouter;
				dijkstrasAlgo();
				printConnectionTable();
				//ArrayList intList=null;
				
				intList=new ArrayList();
				initialDestination = destination;
				while(pathArray[initialDestination]!=-1){
					intList.add(pathArray[initialDestination]);
					initialDestination=pathArray[initialDestination];
				}
				Collections.reverse(intList);
				System.out.print("\nThe updated path from source " + sourceNode + " is: ");
				for(int m=0;m<intList.size();m++)
					System.out.print(intList.get(m)+"-");  
				System.out.println(destination);
				System.out.println("The updated cost for shortest path is:" + distanceArray[destination]);
			}
			
		}
		//check if selected router is destination router
		else if(removedRouter == destination) 
		{
			System.out.println("Destination node removed. Please select a new destination node:");
			newDestinationRouter = sc.nextInt();
			removedRouters.add(removedRouter);
			if(removedRouters.contains(newDestinationRouter))
			{
				System.out.println("Selected router is down.");
			}
			else
			{
				removedRouters.add(removedRouter);
				intList=new ArrayList();
				destination=newDestinationRouter;
				dijkstrasAlgo();
				printConnectionTable();
				initialDestination = destination;
				while(pathArray[initialDestination]!=-1){
					intList.add(pathArray[initialDestination]);
					initialDestination=pathArray[initialDestination];
				}
				Collections.reverse(intList);
				System.out.print("\nThe updated path from source " + sourceNode + " is: ");
				for(int m=0;m<intList.size();m++)
					System.out.print(intList.get(m)+"-");  
				System.out.println(destination);
				System.out.println("The updated cost for shortest path is:" + distanceArray[destination]);
			}
		}
		else
		{
			if(!removedRouters.contains(removedRouter))
			{
				removedRouters.add(removedRouter);
			}
			dijkstrasAlgo();    //update shortest path from source router
			//printConnectionTable();//printing updated connection table for source router
			
			int j;
			
			initialDestination = destination;
			//check if shortest path exist from source to destination
			//Shortest path may alter or broken after removing a router from graph
			if(distanceArray[destination] == Integer.MAX_VALUE)
				System.out.println("\npath from "+sourceNode+ " to "+destination+" doesn't exist as intermidiate router(s) are down");
			else
			{
				System.out.println("\n\nConnection table for router " + sourceNode);
				System.out.println("Destination   Interface");
				System.out.print("=======================");
				for(j=0;j< num_routers;j++){
					//trace shortest path from sourceNode to j'th router
					//print router next to sourceNode as interface
					//if j'th router is down or is same as source then no interface is shown
					//intList holds path from source to destination 
					if(sourceNode != j && !removedRouters.contains(j))
					{
						initialDestination=j;
						intList = new ArrayList();
						if(pathArray[initialDestination] == -2)
						{
							System.out.printf("\n% 5d            -",j);
						}
						else
						{	////tracing path backwards through pathArray
							while(pathArray[initialDestination]!=-1){
							intList.add(pathArray[initialDestination]);
							initialDestination=pathArray[initialDestination];
							}
						Collections.reverse(intList);
						if(intList.size() == 1) 
						{	//if j'th router is immediately next to the source
							//print j as the interface to j'th router
							intList.add(j);
							System.out.printf("\n% 5d            %d",j,j);
						}
						else  
							//if the path has more than 2 routers 
							//print router next to source(intList[1]) as the interface to router 'j'
							System.out.printf("\n% 5d            %d",j,intList.get(1));
						}
					}
					else
					{
						System.out.printf("\n% 5d            -",j);
					}
				}
				intList = new ArrayList();
				initialDestination = destination;
				while(pathArray[initialDestination]!=-1){
					intList.add(pathArray[initialDestination]);
					initialDestination=pathArray[initialDestination];
				}
				Collections.reverse(intList);
				System.out.print("\nThe updated path from source " + sourceNode + " is: ");
				for(int m=0;m<intList.size();m++)
					System.out.print(intList.get(m)+"-");  
				System.out.println(destination);
				System.out.println("The updated cost for shortest path is:" + distanceArray[destination]);
			}
		}
	}
	
	public void exit(){
		System.out.println("CS542 Project successful!!! Goodbye :)");
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
	
		int arr[]=null;
	    int menuChoice=0;	
	    linkStateRouting ip = new linkStateRouting();
		String mainMenu = ("\nLink State Routing Simulator: \n" 
					+ "(1) Create Network Topology.\n" 
					+ "(2) Build Connection Table.\n"
					+ "(3) Shortest Path to Destination Router.\n"
					+ "(4) Remove router.\n"
					+ "(5) Exit");
		Scanner sc = new Scanner(System.in);
	    try
	    {
	    	do{
	    		System.out.println(mainMenu);
				System.out.println("Command:");
				menuChoice = sc.nextInt();
				if(menuChoice!=5) //menuChoice > 0 && menuChoice < 4
				{
					if(menuChoice<1 || menuChoice>5){
						System.out.println("\n Invalid choice!! Please enter choice betweeen 1 and 5. \n");
					}
					if(menuChoice==1)
					{	//create topology and print connection table for all nodes
						ip.createTopology();
						ip.connectionTableAll();
					}
					if(menuChoice==2)
					{	//print connection table for specific router
						//If the selected router is down then it is not considered as a source 
						System.out.println("Enter the source router: ");
						sourceNode = sc.nextInt();
						if(removedRouters.contains(sourceNode)){
							System.out.println("\n Router is down. Please select another router\n");
						}
						else
						{
							dijkstrasAlgo();
							printConnectionTable();
						}
					}
					if(menuChoice==3)
					{	//Input source if not specified
						if(sourceNode == -1)
						{
							System.out.println("enter source node please");
							sourceNode = sc.nextInt(); 
						}
						dijkstrasAlgo();
						ip.srcDestinationPath(); //accpets destination and prints shortest path
					}
					if(menuChoice==4)
					{	//input source if not specified
						if(sourceNode == -1)
						{
							System.out.println("Source node not selected yet.Enter source node please:");
							sourceNode = sc.nextInt(); 
						}
						//input destination if not specified
						if(destination == -1)
						{
							System.out.println("Destination node not selected yet.Enter destination node please:");
							destination = sc.nextInt();
						}
						//accepts a router to be removed/turned down
						ip.removeRouter(); 
					}
					if(menuChoice>5){
						System.out.println("Invalid input!! Please try again and enter menu choice from 1 to 5");
					}
				}
				if(menuChoice==5)
				{
					ip.exit();
				}
						
			} while(menuChoice!=5);	
					 
	      }
	    catch (InputMismatchException inputMismatch)
	    {
	          System.out.println("Wrong Input Format. Enter menu choice only from number 1 to 5");
	    }
	}

}
