import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
	
	public static ArrayList<Master_Node> master_nodes;
	public static ArrayList<Miner_Node> miner_nodes; 
	
	static int num_master_nodes = 200;
	static int num_miner_nodes = 1000;
	static int tot_jobs_done = 0;
	static long prev_time = System.currentTimeMillis();
	static int prev_blockchain_length = 0;
	static double txs;
	static int desired_txs = 5;
	static int blocks_dumped = 0;

	
	private static ArrayList<String> record = new ArrayList<String>();
	
	public static void main(String[] args) {
		try{
			run_sim();
		}finally{
			for(Master_Node m: master_nodes){
				m.stop();
			}for(Miner_Node m: miner_nodes){
				m.stop();
			}
			record_to_file();
		}
		
		}
		
	
	public static void run_sim(){
		master_nodes = new ArrayList<Master_Node>();
		miner_nodes = new ArrayList<Miner_Node>();
		
		
		for(int i = 0; i < num_master_nodes; i++){
			master_nodes.add(new Master_Node(i));
		}
		
		for(int i = 0; i < num_miner_nodes; i++){
			miner_nodes.add(new Miner_Node(i+num_master_nodes));
		}
		
		for(Master_Node m: master_nodes){
			
			try {
				Thread.sleep((int)(Math.random()*10000/num_master_nodes));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.start();
		}
		for(Miner_Node m: miner_nodes){
			
			try {
				Thread.sleep((int)(Math.random()*20000/num_miner_nodes));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.start();
		}
		System.out.println("Nodes Created");
		long start_time = System.currentTimeMillis();
		while(System.currentTimeMillis()-start_time < 600000){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int chain_size = 0;
			int difficulty = 0;
			for(Master_Node m: Main.master_nodes){
				if(m.blockchain.length() > chain_size){
					chain_size = m.blockchain.length();
					difficulty = m.blockchain.get(chain_size-1).current_difficulty;
				}
			}
			
			long time = System.currentTimeMillis();
			long dt = time - prev_time;

			if(dt > 1000){
				int tx = chain_size - prev_blockchain_length;
				txs = (((double)tx)/((double)(dt/1000)));
				prev_blockchain_length = chain_size;
				prev_time = time;
			}
			
			if(tot_jobs_done > 0){
			
			int percentage = (100*chain_size)/tot_jobs_done;
			Main.record.add((System.currentTimeMillis() - start_time)/1000 + " Transactions per second: " + txs + " Chain size is: " + chain_size + " Total Jobs Done is: " + tot_jobs_done);
			
			System.out.println("");
			System.out.println("Transactions per Second is: " + txs + " at difficulty: " + difficulty);
			System.out.println("");
			System.out.println("Percentage of blocks added is: " + percentage + "%");
			System.out.println("");
			System.out.println("Length of chain is: " + chain_size);
			System.out.println("");
			System.out.println("Number of nodes to be added: " + master_nodes.get(0).to_add.size());
			System.out.println("");
			System.out.println("Length of chains are: " + master_nodes.get(0).blockchain.length() + " and " + master_nodes.get(1).blockchain.length());
			}
		}
	}
	
	public static void record_to_file(){
		
		PrintWriter out;
		try {
			File file = new File("log.txt");
			file.delete();
			out = new PrintWriter(new FileWriter("log.txt"));
			for(String s: record){
				out.println(s);
			}
			out.close();
		} catch (IOException e) {e.printStackTrace();} 
		
		
	}

}
