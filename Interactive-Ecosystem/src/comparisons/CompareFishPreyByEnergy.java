package comparisons;

import java.util.Comparator;

import fish.Fish;

public class CompareFishPreyByEnergy implements Comparator<Fish> {

	public CompareFishPreyByEnergy(){
		
	}

	@Override
	public int compare(Fish fishOne, Fish fishTwo) {
		// TODO Auto-generated method stub
		return -(fishOne.getEnergy() - fishTwo.getEnergy());
	}
	
}
