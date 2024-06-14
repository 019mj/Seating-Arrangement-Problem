
public class Solution {
	private int[] arrangement;
	private double cost;

	public Solution(int[] optimalArragngement, double optimalCost) {
		arrangement = optimalArragngement;
		cost = optimalCost;
	}
	
	public int[] getArrangement() {
		return arrangement;
	}
	public void setArrangement(int[] arrangement) {
		this.arrangement = arrangement;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
}
