// Copyright (C) 2008 Universitaet Karlsruhe, Germany
//
// This source is protected by the GNU General Public License. 
//

package de.uka.iti.lights;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.IOException;

/**
 * <p>
 * This is a sample solution to the "Light up" assignment.
 * </p>
 * 
 * Please note:
 * <ul>
 * <li>We use a shadow index {@link #alwaysFalse} beyond the indices of the
 * board to create clauses at the edge. </li>
 * <li>This solution allows to find all solutions to a board if there is more
 * than one. To do so, it encodes the currently found solution in a single clause
 * which requires that at the next run at least one light must be placed
 * differently.</li>
 * </ul>
 * 
 * @author Mattias Ulbrich
 * @version 2008.1c
 */
public class Solution {

	/**
	 * The cnf clauses storage before it is written to the file.
	 */
	private ISolver solver = SolverFactory.newDefault();

	/**
	 * The Lights object we use.
	 */
	private final Lights lights;

	/**
	 * The dimension, taken from {@link #lights}.
	 */
	private final int dimension;

	/**
	 * The index <i>X</i> of a variable which is beyond the indices of the
	 * {@link #dimension}xdimension variables of the board. It is guaranteed to
	 * be false (via a <code>"-<i>X</i> 0"</code> clause) and will be used
	 * as placeholder for the constraints at the edge of the board.
	 */
	private final int alwaysFalse;

	/**
	 * Instantiates a new sample solution.
	 *
	 * @param lights
	 *            the {@link Lights} object which contains the board with all
	 *            walls set up (but no lights yet)
	 */
	public Solution(Lights lights) {
		this.lights = lights;
		this.dimension = lights.getDimension();

		// the index beyond the boards ensured to be false
		alwaysFalse = dimension * dimension + 1;
	}

	/**
	 * Find a solution for the board {@link #lights}.
	 * 
	 * @return <code>true</code> iff there is a solution to this board.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred somewhere.
	 * @throws InterruptedException
	 *             can occur if minisat is interrupted during its run
	 * @see #newSolution()
	 */
	public boolean solve() throws IOException, InterruptedException, ContradictionException, TimeoutException {

		// Add a clause that alwaysFalse is false

		// 1. Make clauses that make sure everything is properly lit

		// 2. light constraints

		// 3. Interpret results
		return newSolution();
	}

	/**
	 * Find a solution for the given set of clauses.
	 * 
	 * Call this method only if {@link #solve()} has already been called!
	 * Otherwise the result is arbitrary.
	 * 
	 * Every call to newSolution sets a solution on the board that has not yet
	 * been set or returns <code>false</code> if there are no more solutions.
	 * 
	 * Every call to newSolution makes a system call to minisat. The set of
	 * clauses and the counter are changed to ensure the next solution is
	 * different.
	 * 
	 * @return <code>true</code> iff there is a solution to the board which is
	 *         distinct from the solutions found previously.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred somewhere.
	 * @throws InterruptedException
	 *             can occur if minisat is interrupted during its run
	 * @see #solve()
	 */
	public boolean newSolution() throws IOException, InterruptedException, TimeoutException, ContradictionException {

		if(!solver.isSatisfiable()) {
			return false;
		}

		lights.removeAllLights();
		// set all lights from the model available via solver.model() or solver.model(int)

		// add clause to avoid same model again

		return true;
	}
	
	public static void main(String[] args) throws Exception {

		try {
			Lights lights = new Lights(args[0]);
			lights.toConsole();
			lights.showWindow();

			Solution solver = new Solution(lights);
			if(solver.solve()) {
				do {
					lights.showWindow();
					lights.toConsole();
					System.out.println("Solution: " + lights.toString());
				} while (solver.newSolution());
			} else {
				System.out.println("Sorry, there is no solution.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
