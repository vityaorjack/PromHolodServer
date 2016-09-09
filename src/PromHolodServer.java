import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;




public class PromHolodServer {
	
	public static void main(String[] args) throws IOException{		
		MyFrame frame = new MyFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("ПромХолод Сервер");			
		frame.show();		
		
		
		ServerSocket s = new ServerSocket(8080);
	    System.out.println("Server Started");
	    try {
	    
	         while (true) {	            
	            Socket socket = s.accept();
	            try {new ServeOneJabber(socket);}
	            catch (IOException e) {socket.close();}
	         }
	    } //catch (ClassNotFoundException | SQLException	e1) {e1.printStackTrace();}
	    finally {System.out.println("Server Stoped");  s.close();
	  
	    }
	}
}
class MyFrame extends JFrame {
	public MyFrame(){
		setSize(1280,730);
		MyPanel panel = new MyPanel();
		Container pane = getContentPane();
		pane.add(panel);		
	}
}
class MyPanel extends JPanel {
	static String console="";
	public Image im;
	Font font=new Font("Arial", Font.BOLD, 20);	
	Colection colection=new Colection();	
	ArrayList<Rectangle> rectangles=new ArrayList<Rectangle>();
	
			MyPanel(){
				addMouseListener( new MyMouse());
				rectangles.add(new Rectangle(50, 600, 220, 35));
				rectangles.add(new Rectangle(300, 600, 220, 35));
				}			
			public void paintComponent(Graphics g){
				super.paintComponent(g);  repaint();
				setBackground(new Color(150,175,255));
				//g.drawImage(im,0,0,null);
				//консоль на экране
				g.setColor(Color.red);
				g.drawString(console, 10, 10);
				//рамка
				g.setColor(Color.cyan);
				for (int i = 0, t = 0; i < 9; i++,t+=100){				
					g.drawRect(800, 100+t, 300, 100);				
				}//текст в рамке
				g.setFont(font);
				g.setColor(Color.black);
				for(int i = 0, ot = 0; i < colection.mainGroup.size(); i++,ot+=100){
					g.drawString(colection.mainGroup.get(i), 810, 140+ot);					
				}//меню				
				g.drawRoundRect(500, 50, 220, 35, 25,25);
				g.drawString("В главное меню", 510, 75);
				g.drawString("Серверня часть", 10, 50);
				//память
				g.drawRoundRect(50, 600, 220, 35, 25,25);
				g.drawString("Загрузить", 70, 625);
				g.drawRoundRect(300, 600, 220, 35, 25,25);
				g.drawString("Сохранить", 310, 625);				
			}			

			public class MyMouse extends MouseAdapter  {				
				public void mousePressed(MouseEvent event){
					for(int i=0;i<rectangles.size();i++){
						console="нажато-"+(i);
						if(rectangles.get(i).contains(event.getPoint())){										
							try {
								switch(i){							
									case 0:{colection.load("mainGroup");break;}
									case 1:{colection.save("mainGroup");break;}
									
								}
							} catch (ClassNotFoundException | IOException e) {e.printStackTrace();}
							break;
						}	
					}
				}							
				
			}			
}
class ServeOneJabber extends Thread {
	   private Socket socket;
	   private BufferedReader in;	  
	   Colection colection = new Colection();   
	   boolean text;
	   FileOutputStream fos;
	   ObjectInputStream oin;
	   ObjectOutputStream oos;	  
	   
	   public ServeOneJabber(Socket s) throws IOException {		  
	      socket = s;
	      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	      start();
	   }	   
	   public void run() {
		   
		   while(true){		  
			   try {
				   oin = new ObjectInputStream(socket.getInputStream());
					
				   
				   oos = new ObjectOutputStream(socket.getOutputStream());	        	 	
				   oos.writeObject(colection.change((Aparat) oin.readObject()));				  
				   oos.flush();				
					//oos.close();	        
			   } catch (IOException | ClassNotFoundException e) {e.printStackTrace(); stop();}
		   }
	      /*finally {
	         try {System.out.println("Server Stoped !");   	 socket.close();
	         }catch (IOException e) {System.err.println("Socket not closed");}
	      }*/		 
	 }
}
class Colection {	
	ObjectOutputStream oos;	
	ObjectInputStream oin; 
	
	ArrayList<String> mainGroup=new ArrayList<String>();
	ArrayList<ArrayList<String>> mainStr=new ArrayList<ArrayList<String>>();
	ArrayList<Aparat> aparat=new ArrayList<Aparat>();	
	
	int condition;
	Colection() {		
		for(int i=0;i<3;i++){mainStr.add(new ArrayList<String>());}		
		//mainGroup.add("Компресоры");mainGroup.add("Вентиляторы");mainGroup.add("Трубы");  	
		mainStr.get(0).add("Филипс 2000");mainStr.get(0).add("Панасоник ад800");mainStr.get(0).add("Ссср Матор, цена 2 руб.");
		mainStr.get(1).add("Фюджи рн52");mainStr.get(1).add("Харнит 100");mainStr.get(1).add("Супер спирит 3000");mainStr.get(1).add("Ветряк");
		mainStr.get(2).add(" Чавунные д180");mainStr.get(2).add("Мендыне д36");mainStr.get(2).add("Сталь д20");mainStr.get(2).add("Алюминий д12");
	}
	Object change(Aparat aparat){		
		try {
			if(condition>0){
				if(aparat.integer>0){ load(aparat.integer);  return aparat;}}
				if(aparat.integer<8)condition=aparat.integer;
		
				if(aparat.integer<8){	load("mainStr");  return mainStr.get(aparat.integer);  }		
		
				switch(aparat.integer){
				case 8:load("mainGroup");	return mainGroup;  
				case 9:load("mainGroup");	return mainGroup;
				}
		} catch (ClassNotFoundException | IOException e) {e.printStackTrace();}
				return mainGroup;		
	}	
	void load(String memory) throws IOException, ClassNotFoundException{
		oin = new ObjectInputStream(new FileInputStream("Baza/"+memory+".txt"));
		ArrayList arrayList=(ArrayList) oin.readObject();
		if(memory=="mainGroup")mainGroup=arrayList;
		else if(memory=="mainStr") mainStr=arrayList;
		MyPanel.console="load";
	}
	void load(int memory) throws IOException, ClassNotFoundException{
		oin = new ObjectInputStream(new FileInputStream("Baza/"+memory+".txt"));
		ArrayList arrayList=(ArrayList) oin.readObject();
		aparat=arrayList;
	}
	void save(String memory) throws FileNotFoundException, IOException{	
		oos = new ObjectOutputStream(new FileOutputStream("Baza/"+memory+".txt"));
		if(memory=="mainGroup")oos.writeObject(mainGroup);
		else if(memory=="mainStr")oos.writeObject(mainStr);
		oos.flush();
		oos.close();
	}
	void save(int memory) throws FileNotFoundException, IOException{	
		oos = new ObjectOutputStream(new FileOutputStream("Baza/"+memory+".txt"));
		oos.writeObject(aparat);
		oos.flush();
		oos.close();
	}
}
class Aparat implements Serializable{
	int integer;
	String srt;	
}