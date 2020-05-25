package dv;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DistanceVector {
  //This list holds all links from provided file
  public static ArrayList<Link> links = new ArrayList<>();
  //This is a list of the routers
  public static ArrayList<Integer> routers = new ArrayList<>();
  //This array stores the router nodes
  public static ArrayList<RouterNode> routerNodes = new ArrayList<>();
  //Variable to keep track of value of time
  int time=0;
  //Function that returns the list of links from text file
  public ArrayList<Link> getLinks(){
    return links;
  }
  //Function to get list of routers from the file
  public static ArrayList<Integer> getRouters() {
    return routers;
  }
  //Function creates main simulation window
  public void createSimWindow(){
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame f = new JFrame("Distance Vector Simulation Window");
    f.setResizable(true);
    JPanel commandPanel = new JPanel();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    commandPanel.setBounds(0,0,150,300);
    commandPanel.setLayout(null);
    commandPanel.setBackground(Color.gray);
    String initSimWindow = "This is the main simulation window. \nIt will"+
      " display necessary data. The\nOpen File button will allow the user\n to "+
      "enter a file name to read. The\nstep button will step through the\ndistance"+
      " vector algorithm while\nupdating the router windows.";
    JTextArea textArea = new JTextArea(initSimWindow);
    textArea.setBounds(150,0,350,300);
    textArea.setFont(new Font("monospaced", Font.PLAIN,12));
    JButton fButton = new JButton("Open File");
    fButton.setBounds(30,50,110,30);
    //This is the action listener for the button to take the file name from
    //the user
    fButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        Object result = JOptionPane.showInputDialog(f, "Enter file name:");
        File file = new File(result.toString());
        //this code block handles opening and reading the text file, creates a
        //list of links and a list of routers
        try{
          for(RouterNode rn:routerNodes)rn.closeRouterWindows();
          links.clear();
          routers.clear();
          time=0;
          Scanner lineScanner = new Scanner(file);
          while(lineScanner.hasNextLine()){
            Link temp = new Link();
            Scanner scan = new Scanner(lineScanner.nextLine());
            scan.useDelimiter(" ");
            temp.source = Integer.parseInt(scan.next());
            temp.dest = Integer.parseInt(scan.next());
            temp.cost = Integer.parseInt(scan.next());
            links.add(temp);
            if(routers.contains(temp.source) == false) routers.add(temp.source);
            if(routers.contains(temp.dest) == false) routers.add(temp.dest);
          }
          Collections.sort(routers);
          //Creating a router node from the list of routers from the text file
          for(Integer i : routers) {
            RouterNode temp = new RouterNode();
            temp.setID(temp, i);
            temp.setLinks(temp);
            temp.setMyVector(temp);
            temp.createRouterWindow(temp);
            routerNodes.add(temp);
          }
          //This string lets the user know that the router windows are displaying
          //data at time 0
          String routerData = "This file has a total of "+routers.size()+" routers.\n"+
            "The router windows are currently\ndisplaying data at time t = 0";
          textArea.setText("");
          textArea.setText(routerData);
        }
        //Catch exception if file not found and display message to user
        catch(FileNotFoundException ex){
          if (file.exists() == false) JOptionPane.showMessageDialog(f, "Error: "
              + "File Not Found");
        }
      }    });
    //This block creates the button to go through the algorithm step by step
    JButton stepButton = new JButton("Step");
    stepButton.setBounds(30,90,110,30);
    stepButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        ArrayList<Boolean> boolList = new ArrayList<>();
        time=time+1;
        String routerData=getUpdateString(time);
        for(RouterNode rn: routerNodes)rn.exchangeData(rn, routerNodes);
        for(RouterNode rn: routerNodes){
          boolean uBool = rn.updateRouterVectors(rn);
          boolList.add(uBool);
        }
        for(RouterNode rn: routerNodes)rn.printUpdatedData(rn);
        textArea.setText("");
        textArea.setText(routerData);
        String notStable = "\n\nThere are updates still pending.";
        String stable = "\n\nThe network is now stable.";
        if(boolList.contains(true)==true)textArea.append(notStable);
        else textArea.append(stable);
      }    });
      //This block creates the run button that will run the algorithm from start
      //to end without stopping, the time taken in ms is displayed
      JButton runButton = new JButton("Run");
      runButton.setBounds(30,130,110,30);
      runButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          ArrayList<Boolean> boolList = new ArrayList<>();
          long start = System.currentTimeMillis();
          do{
            time = time+1;
            boolList.clear();
            for(RouterNode rn:routerNodes){
              rn.exchangeData(rn,routerNodes);
              boolean uBool = rn.updateRouterVectors(rn);
              boolList.add(uBool);
            }
          }while(boolList.contains(true)==true);
          long end = System.currentTimeMillis();
          for(RouterNode rn:routerNodes)rn.printUpdatedData(rn);
          textArea.setText("");
          String routerData = getUpdateString(time);
          textArea.setText(routerData);
          String stable = "\n\nThe network is now stable.";
          textArea.append(stable);
          String tMeasured = "\n\nThe time taken by the algorithm is:\n"+
            " "+(end-start)+" ms";
          textArea.append(tMeasured);
        }    });
    commandPanel.add(fButton);
    commandPanel.add(stepButton);
    commandPanel.add(runButton);
    f.add(commandPanel, BorderLayout.WEST);
    f.add(textArea);
    f.setSize(400,300);
    f.setLayout(null);
    f.setVisible(true);
  }
  //This function returns the string to display the number of routers and the
  //current value of the time variable 
  public String getUpdateString(int time){
    String temp = "This file has a total of "+routers.size()+" routers.\n"+
      "The router windows are currently\ndisplaying data at time t = "+time;
    return temp;
  }
}
