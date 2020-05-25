package dv;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class RouterNode {
  //ID variable to identify each router
  int RouterID;
  //Bool that indicates if an update was made to a router's distance vector
  boolean update = false;
  //List of directly connected links
  ArrayList<Link> myLinks = new ArrayList<>();
  //List of vectors of directly connected routers
  ArrayList<Link> neighborLinks = new ArrayList<>();
  //List to hold distance vector for this router
  ArrayList<Link> myVector = new ArrayList<>();
  //List that holds textareas to allow updating
  ArrayList<JTextArea> routerWindows = new ArrayList<>();
  //List of router window jframes used to auto close windows when a new file
  //is entered by the user
  ArrayList<JFrame> routerFrames = new ArrayList<>();
  //Creating instance of DistanceVector class to get router and link lists
  DistanceVector DV = new DistanceVector();
  ArrayList<Integer> routers = DV.getRouters();
  ArrayList<Link> tempList = DV.getLinks();
  //Setter function for router node id
  public void setID (RouterNode rn, Integer i){ rn.RouterID = i;}
  //Function to set direct links for each router
  public void setLinks (RouterNode rn) {
    for(Link link : tempList ) {
      if(link.source == rn.RouterID || link.dest == rn.RouterID) {
        Link temp = new Link();
        if(link.source == rn.RouterID){
          temp.dest = link.dest;
          temp.source = link.source; }
        else{
          temp.dest = link.source;
          temp.source = link.dest; }
        temp.cost = link.cost;
        myLinks.add(temp);
        myVector.add(temp);
      }
    }
    //Initializing all neighbor links to 16 to start to start the process
    for(Link link : myLinks){
      for(Integer i : routers){
        Link temp = new Link();
        temp.source = link.dest;
        temp.dest = i;
        temp.cost = 16;
        neighborLinks.add(temp);
      }
    }
  }
  //Function to set distance vector for this router
  public void setMyVector(RouterNode rn){
    ArrayList<Integer> temp = new ArrayList<>();
    for(Integer i:routers)if(i!=rn.RouterID)temp.add(i);
    for(Link link:myLinks){
      if(temp.contains(link.dest)==true)temp.remove(temp.indexOf(link.dest));
    }
    for(Integer i:temp){
      Link tempLink = new Link();
      tempLink.source = rn.RouterID;
      tempLink.dest = i;
      tempLink.cost = 16;
      myVector.add(tempLink);
    }
    Link l = new Link();
    l.source = rn.RouterID;
    l.dest = rn.RouterID;
    l.cost = 0;
    myVector.add(l);
  }
  //Function to store instance of router windows for update
  public void addToWindowList(JTextArea ta){
    routerWindows.add(ta);
  }
  //Function to create windows for each router
  public void createRouterWindow(RouterNode rn) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame f = new JFrame("Data Window for Router " + rn.RouterID );
    routerFrames.add(f);
    f.setResizable(true);
    JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setFont(new Font("monospaced", Font.PLAIN,12));
    textArea.setBounds(0,0,400,300);
    String temp = initRouterWindow(rn);
    textArea.append(temp);
    JScrollPane scroll = new JScrollPane(textArea);
    f.getContentPane().add(scroll);
    addToWindowList(textArea);
    f.setSize(400,300);
    f.setVisible(true);
  }
  //Function to initialize each router window with the default strings
  public String initRouterWindow(RouterNode rn){
    String temp = "Current data for router " + rn.RouterID + "\n\n" +
      "Neighboring Routers Distance Vectors:\n\n"+
      "Destination |"+printAllRouters()+"\n"+printNeighborVectors()+"\n\n\n"+
      "Distance Vector for This Router:\n\n"+"Destination |"+printAllRouters()+
      "\n"+printMyVector(rn);
    return temp;
  }
  //Function that prints all routers to text area
  public String printAllRouters(){
    String temp = "";
    String temp2 = "\n-------";
    for (Integer i : routers){
      String temp3 = "\t"+i;
      temp = temp.concat(temp3);
      temp2 = temp2.concat("---------");
    }
    temp = temp.concat(temp2);
    return temp;
  }
  //Function that prints the vectors of connected neighbors
  public String printNeighborVectors(){
    String temp = "";
    for(Integer i : routers){
      for(Link link : myLinks){
        if(link.dest == i){
          String temp2 = "";
          temp2=temp2.concat("Router "+link.dest+"    |");
          for(Link nlink : neighborLinks){
            if(nlink.source == i)temp2 = temp2.concat("\t"+nlink.cost);
          }
          temp2 = temp2.concat("\n");
          temp = temp.concat(temp2);
        }
      }
    }
    return temp;
  }
  //Function that prints the vector of this router
  public String printMyVector(RouterNode rn){
    String temp="   Cost     |";
    for(Integer i:routers){
      for(Link l:myVector){
        if(i==l.dest)temp=temp.concat("\t"+l.cost);
      }
    }
    return temp;
  }
  //This is the function used by the program to exchange distance vectors
  //between connected routers
  public void exchangeData(RouterNode rn, ArrayList<RouterNode> list){
    for(Link link:rn.myLinks){
      for(RouterNode rNode: list){
        if(rNode.RouterID==link.dest){
          for(Link nLink:rNode.myVector){
            for(Link mLink:rn.neighborLinks){
              if(nLink.source==mLink.source && nLink.dest==mLink.dest)
                mLink.cost = nLink.cost;
            }
          }
        }
      }
    }
  }
  //This function runs the Bellman-Ford algorithm that updates vectors when
  //shorter paths are found
  public boolean updateRouterVectors(RouterNode rn){
    boolean update = false;
    for(Link mLink:rn.myVector){
      for(Link nLink:rn.neighborLinks){
        if(mLink.dest==nLink.dest&&mLink.dest!=rn.RouterID&&nLink.source!=nLink.dest){
          int temp = getCost(rn.myVector, nLink.source) + nLink.cost;
          if(temp < mLink.cost){
            mLink.cost = temp;
            update = true;
          }
        }
      }
    }
    return update;
  }
  //This function returns the cost of a specific link to help run the
  //Bellman-Ford algorithm
  public Integer getCost(ArrayList<Link> list, Integer dest){
    int temp = 16;
    for(Link link:list){
      if(link.dest==dest)temp = link.cost;
    }
    return temp;
  }
  //This function prints the updated data
  public void printUpdatedData(RouterNode rn){
    String temp = initRouterWindow(rn);
    routerWindows.get(0).setText("");
    routerWindows.get(0).append(temp);
  }
  //This function closes any open router windows when the user selects a new
  //file to be read
  public void closeRouterWindows(){
    routerFrames.get(0).setVisible(false);
  }

}
