import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SearchAlgorithms {

	static double[][] dislikeMatrix;
	static int size;
	static final Random RAND = new Random();

	static public Solution hillClimbing(double[][] dislikeMatrix, int num_restarts) {
		SearchAlgorithms.dislikeMatrix = dislikeMatrix;
		SearchAlgorithms.size = dislikeMatrix.length;

		return hillClimbing(num_restarts);
	}

	static private Solution hillClimbing(int num_restarts) {
		int[] optimalArrangement = new int[size];
		double optimalCost = Integer.MAX_VALUE;

		for (int k = 0; k < num_restarts; k++) {

			int[] currArrangement = generateRandomSeating();
			double currCost = calculateCost(currArrangement);

			boolean localMinima = true;

			while (localMinima) {
				localMinima = false;
				for (int i = 0; i < size; i++)
					for (int j = i + 1; j < size; j++) {
						int[] neigbourArrangement = swap(currArrangement, i, j);
						double neigbourCost = calculateCost(neigbourArrangement);

						if (neigbourCost < currCost) {
							currArrangement = neigbourArrangement;
							currCost = neigbourCost;
							localMinima = true;
						}
					}

			}

			if (currCost < optimalCost) {
				optimalArrangement = currArrangement;
				optimalCost = currCost;
			}
		}

		return new Solution(optimalArrangement, optimalCost);

	}

	static public Solution geneticAlgorithm(double[][] dislikeMatrix, int population_size, int num_generations,
			double mutation_rate) {
		SearchAlgorithms.dislikeMatrix = dislikeMatrix;
		SearchAlgorithms.size = dislikeMatrix.length;

		return geneticAlgorithm(population_size, num_generations, mutation_rate);
	}

	static private Solution geneticAlgorithm(int population_size, int num_generations, double mutation_rate) {
	    List<int[]> population = new ArrayList<>();
	    for (int i = 0; i < population_size; i++) {
	        population.add(generateRandomSeating());
	    }

	    for (int generation = 0; generation < num_generations; generation++) {

	    	population.sort(Comparator.comparingDouble(SearchAlgorithms::calculateCost));

	        List<int[]> topIndividuals = new ArrayList<>();
	        for (int i = 0; i < 60; i++) {
	            topIndividuals.add(population.get(i));
	        }

	        List<int[]> newPopulation = new ArrayList<>();
	        while (newPopulation.size() < population_size) {
	            int index = (int) (Math.random() * 60);
	            int[] individual = topIndividuals.get(index);
	            newPopulation.add(individual);
	        }

	        List<int[]> offspringPopulation = new ArrayList<>();
	        for (int i = 0; i < newPopulation.size(); i += 2) {
	            int[] parent1 = newPopulation.get(i);
	            int[] parent2 = newPopulation.get((i + 1) % newPopulation.size());

	            int[] child1 = new int[parent1.length];
	            int[] child2 = new int[parent1.length];
	            Set<Integer> used1 = new HashSet<>();
	            Set<Integer> used2 = new HashSet<>();

	            int crossoverPoint = (int) (Math.random() * parent1.length);

	            for (int j = 0; j < crossoverPoint; j++) {
	                child1[j] = parent1[j];
	                used1.add(parent1[j]);
	                child2[j] = parent2[j];
	                used2.add(parent2[j]);
	            }

	            int currentIndex1 = crossoverPoint;
	            int currentIndex2 = crossoverPoint;
	            for (int j = 0; j < parent2.length; j++) {
	                if (!used1.contains(parent2[j])) {
	                    child1[currentIndex1++] = parent2[j];
	                }
	                if (!used2.contains(parent1[j])) {
	                    child2[currentIndex2++] = parent1[j];
	                }
	            }

	            if (Math.random() < mutation_rate) {
	                int index1Mut = (int) (RAND.nextDouble() * child1.length);
	                int index2Mut = (int) (RAND.nextDouble() * child1.length);
	                int temp = child1[index1Mut];
	                child1[index1Mut] = child1[index2Mut];
	                child1[index2Mut] = temp;

	                index1Mut = (int) (RAND.nextDouble() * child2.length);
	                index2Mut = (int) (RAND.nextDouble() * child2.length);
	                temp = child2[index1Mut];
	                child2[index1Mut] = child2[index2Mut];
	                child2[index2Mut] = temp;
	            }


	            offspringPopulation.add(child1);
	            offspringPopulation.add(child2);
	        }


	        population = offspringPopulation;
	    }

	    int[] optimalArrangement = new int[population.get(0).length];
	    double optimalCost = Double.MAX_VALUE;

	    for (int[] currArrangement : population) {
	        double currCost = calculateCost(currArrangement);
	         
	        if (currCost < optimalCost) {
	            optimalCost = currCost;
	            optimalArrangement = currArrangement;
	        }
	    }

	    return new Solution(optimalArrangement, optimalCost);
	}

	static public Solution simulatedAnnealing(double[][] dislikeMatrix, double initial_temperature, double cooling_rate,
			int num_iterations) {
		SearchAlgorithms.dislikeMatrix = dislikeMatrix;
		SearchAlgorithms.size = dislikeMatrix.length;

		return simulatedAnnealing(initial_temperature, cooling_rate, num_iterations);
	}

	static private Solution simulatedAnnealing(double initial_temperature, double cooling_rate, int num_iterations) {
		int[] currentArrangement = generateRandomSeating();
		int[] bestArrangement = currentArrangement.clone();

		double bestCost = calculateCost(currentArrangement);
		double temperature = initial_temperature;

		for (int i = 0; i < num_iterations; i++) {
			int[] newArrangement = generateNeighbor(currentArrangement);
			double currentCost = calculateCost(currentArrangement);
			double newCost = calculateCost(newArrangement);
			double acceptanceProbability = acceptanceProbability(currentCost, newCost, temperature);

			if (acceptanceProbability > RAND.nextDouble()) {
				currentArrangement = newArrangement;
				if (newCost < bestCost) {
					bestArrangement = newArrangement;
					bestCost = newCost;
				}
			}

			temperature *= cooling_rate;
		}

		return new Solution(bestArrangement, bestCost);

	}

	static private int[] generateRandomSeating() {
		int[] arrangement = new int[size];
		for (int i = 0; i < size; i++)
			arrangement[i] = i;

		for (int i = size - 1; i > 0; i--) {
			int randomIndex = RAND.nextInt(i + 1);
			int temp = arrangement[randomIndex];
			arrangement[randomIndex] = arrangement[i];
			arrangement[i] = temp;
		}
		
		return arrangement;

	}

	static private double calculateCost(int[] arrangement) {
		double cost = 0;
		for (int i = 0; i < size; i++) {
			int nextPerson = (i + 1) % size;
			cost += dislikeMatrix[arrangement[i]][arrangement[nextPerson]];
		}

		return cost;
	}

	private static int[] swap(int[] arrangement, int i, int j) {
		int[] newArrangement = arrangement.clone();
		int temp = newArrangement[i];
		newArrangement[i] = newArrangement[j];
		newArrangement[j] = temp;
		return newArrangement;
	}

	private static int[] generateNeighbor(int[] arrangement) {
		int n = arrangement.length;
		int[] newArrangement = arrangement.clone();
		int i = RAND.nextInt(n);
		int j = RAND.nextInt(n);
		int temp = newArrangement[i];
		newArrangement[i] = newArrangement[j];
		newArrangement[j] = temp;
		return newArrangement;
	}

	private static double acceptanceProbability(double currentCost, double newCost, double temperature) {
		if (newCost < currentCost) {
			return 1.0;
		}
		return Math.exp((currentCost - newCost) / temperature);
	}
}