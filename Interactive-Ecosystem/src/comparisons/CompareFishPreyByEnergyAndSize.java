package comparisons;

import java.util.Comparator;

import fish.Fish;

public class CompareFishPreyByEnergyAndSize implements Comparator<Fish> {
	public CompareFishPreyByEnergyAndSize(){
		
	}

	@Override
	public int compare(Fish fishOne, Fish fishTwo) {
		// TODO Auto-generated method stub
		return -((fishOne.getEnergy() + fishOne.getSize()) - ((fishTwo.getEnergy() + fishTwo.getSize()) ));
	}
	
	
}
