package comparisons;

import java.util.Comparator;

import fish.Fish;

public class CompareFishPreyBySize implements Comparator<Fish>{
	
	public CompareFishPreyBySize(){
		
	}
	
	
	@Override
	public int compare(Fish fishOne, Fish fishTwo) {
		// TODO Auto-generated method stub
		return -(fishOne.getSize() - fishTwo.getSize());
	}

}
