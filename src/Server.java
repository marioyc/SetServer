import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Server extends JFrame{
	private JTextArea txtMsg = new JTextArea();
    private JButton clear = new JButton("Clear");
	private ServerSocket server;
	private Socket s1,s2;
	int port;
	int value[] = new int[15];
	int N;
	int posDeck;
	boolean marked[] = new boolean[15];
	
	Server(int port){
		super("Set Server");
		
		this.port = port;

        setSize(500,450);
        
        add(txtMsg,BorderLayout.CENTER);
        add( new JScrollPane(txtMsg) );
        txtMsg.setEditable(false);
        
        clear.setSize(50, 50);
        add(clear,BorderLayout.SOUTH);
        clear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				txtMsg.setText("");
			}
        });
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	
	void launch(){
		server = Net.createServer(this.port);
		
		while(true){
			write("Wait connection");
			s1 = Net.acceptConnection(server);
			write("Client1!");
			s2 = Net.acceptConnection(server);
			write("Client2!");
			
			BufferedReader in1 = Net.connectionIn(s1);
			PrintWriter out1 = Net.connectionOut(s1);
			BufferedReader in2 = Net.connectionIn(s2);
			PrintWriter out2 = Net.connectionOut(s2);
			
			ArrayList<Integer> deck = new ArrayList<Integer>();//Cards.generateDeck();
			deck.add(102); deck.add(253); deck.add(223); deck.add(111); deck.add(254);
			deck.add(231); deck.add(171); deck.add(213); deck.add(221); deck.add(250);
			deck.add(109); deck.add(215); deck.add(183);
			
			deck.add(85); deck.add(118); deck.add(245); deck.add(103); deck.add(233);
			deck.add(117); deck.add(229); deck.add(95); deck.add(246); deck.add(105);
			deck.add(158); deck.add(91); deck.add(126);
			
			deck.add(122); deck.add(127); deck.add(237); deck.add(87); deck.add(153);
			deck.add(123); deck.add(191); deck.add(94); deck.add(166); deck.add(106);
			deck.add(189); deck.add(90); deck.add(151); deck.add(249);
			
			deck.add(121); deck.add(89); deck.add(187); deck.add(155); deck.add(181);
			deck.add(174); deck.add(86); deck.add(190); deck.add(110); deck.add(185);
			deck.add(235); deck.add(173); deck.add(165);
			
			deck.add(154); deck.add(251); deck.add(119); deck.add(222); deck.add(234);
			deck.add(239); deck.add(214); deck.add(175); deck.add(149); deck.add(230);
			deck.add(157); deck.add(219); deck.add(217);
			
			deck.add(182); deck.add(101); deck.add(255); deck.add(170); deck.add(150);
			deck.add(167); deck.add(218); deck.add(169); deck.add(247); deck.add(93);
			deck.add(159); deck.add(186); deck.add(238); deck.add(125); deck.add(107);
			N = 12;
			posDeck = 12;
			
			for(int i = 0;i < N;++i)
				value[i] = deck.get(i);
			test(value,N);
			String deckString = "";
			
			for(int x : deck){
				deckString = deckString + " " + x;
			}
			
			deckString = deckString.substring(1, deckString.length());
			
			//write(deckString);
			out1.println(deckString);
			out2.println(deckString);
			
			boolean end = false;
			
			int round = 0;
			
			while(!end){
				write("Round : " + (round + 1));
				++round;
				
				String move1 = "";
				String move2 = "";
				
				try {
					move1 = in1.readLine();
					move2 = in2.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				
				if(move1 == null || move2 == null){
					write("Received null");
					break;
				}
				
				write("J1 = " + move1);
				write("J2 = " + move2);
				
				int id1[] = new int[3]; long time1;
				int id2[] = new int[3]; long time2;
				
				String tokens1[] = move1.split(" ");
				String tokens2[] = move2.split(" ");
				
				for(int i = 0;i < 3;++i)
					id1[i] = Integer.parseInt(tokens1[i]);
				time1 = Long.parseLong(tokens1[3]);
				
				for(int i = 0;i < 3;++i)
					id2[i] = Integer.parseInt(tokens2[i]);
				time2 = Long.parseLong(tokens2[3]);
				
				boolean ok1 = false,ok2 = false;
				
				if(Cards.isSet(value[ id1[0] ], value[ id1[1] ], value[ id1[2] ]))
					ok1 = true;
				
				if(Cards.isSet(value[ id2[0] ], value[ id2[1] ], value[ id2[2] ]))
					ok2 = true;
				
				int score1 = 0,score2 = 0,c[] = new int[3];
				
				if(!ok1) score1 = -1;
				else if(time1 < time2 || !ok2){
					score1 = 1;
					for(int i = 0;i < 3;++i)
						c[i] = id1[i];
				}
				
				if(!ok2) score2 = -1;
				else if(time2 < time1 || !ok1){
					score2 = 1;
					for(int i = 0;i < 3;++i)
						c[i] = id2[i];
				}
				
				write(score1 + " " + score2);
				
				if(!ok1 && !ok2){
					out1.println("CONTINUE");
					out2.println("CONTINUE");
				}else{
					out1.println("ERASE " + c[0] + " " + c[1] + " " + c[2] + " " + score1 + " " + score2);
					out2.println("ERASE " + c[0] + " " + c[1] + " " + c[2] + " " + score2 + " " + score1);
					
					// update value
					
					for(int i = 0;i < N;++i)
						marked[i] = false;
					for(int i = 0;i < 3;++i)
						marked[ c[i] ] = true;
					
					N -= 3;
					
					if(N == 12)
						cleanCards(N + 3);
					
					while(81 - posDeck >= 3 && (N < 12 || (N == 12 && !Cards.test(value, N)))){
        				//System.out.println("N = " + N);
        				for(int k = 0;k < 3;++k){
        					value[N + k] = deck.get(posDeck); posDeck++;
            				//cards[N + k].setImageDrawable(new CardDrawable(value[N + k]));
        				}
        				
        				N += 3;
        			}
					
					if(!test(value,N)){
						end = true;
					}
					
					/*if((N == 9 && 81 - posDeck >= 3) || (N == 12 && !test(value,N))){
        				for(int k = 0;k < 3;++k){
        					value[ c[k] ] = deck.get(posDeck);
        					posDeck++;
        				}
        				
        				N += 3;
        				
        				if(!test(value,N) && 81 - posDeck >= 3){
        					for(int k = 0;k < 3;++k){
        						value[12 + k] = deck.get(posDeck);
        						posDeck++;
        					}
        					
        					N += 3;
        				}
        				
    					if(!test(value,N)){
    						end = true;
    					}
					}*/
				}
				
				write("-------------------");
				write("N = " + N);
				
				String auxValue = "";
            	
            	for(int i = 0;i < N;++i)
            		auxValue = auxValue + value[i] + " ";
            	
            	write(auxValue);
				
				write("-------------------");
			}
		}
	}
    
    void write(String s){
        txtMsg.append(s + "\n");
    }
    
    void cleanCards(int n){
		for(int i = 0,j = 0;i < n;++i){
			if(!marked[i]){
				value[j] = value[i];
				++j;
			}else marked[i] = false;
		}
    }
    
	boolean test(int val[], int n){
		for(int i = 0;i < n;++i)
			for(int j = i + 1;j < n;++j)
				for(int k = j + 1;k < n;++k)
					if(Cards.isSet(val[i], val[j], val[k])){
						write(i + " " + j + " " + k);
						return true;
					}
		write("Fin");
		return false;
	}
}
