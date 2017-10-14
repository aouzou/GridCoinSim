import java.util.ArrayList;

public class Blockchain {
	
	private ArrayList<Block> chain;
	public int length = 0;
	
	public Blockchain(){
		chain = new ArrayList<Block>();
	}
	
	public Blockchain(ArrayList<Block> chain){
		this.chain = chain;
		this.length = chain.size();
	}
	
	public Blockchain clone(){
		return new Blockchain((ArrayList<Block>) chain.clone());
		
	}
	
	//when a job is done NOT when it is offered, it is added to the block chain. The job is officially over and payment is given. 
	public void add(Job job, Master_Node master, Miner_Node miner){
		chain.add(new Block(job, master, miner));
		length++;
	}
	
	
	
	public void report(int id){
		System.out.println("Blockchain length on Node: " + id + " is: " + chain.size());
	}
	/*
	public void report(int id){
		System.out.println("");
		System.out.println("==================================");
		for(Block b: chain){
			if(b != null) System.out.println("Block Master is: " + b.master_id);
		}
	}
	*/
	
	public int length(){
		return length;
	}
	
	
}
