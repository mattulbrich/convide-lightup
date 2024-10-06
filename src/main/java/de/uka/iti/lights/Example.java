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
import java.util.Arrays;

/**
 * <p>This is a general example how to use {@link SAT4J}.</p>
 *
 * <p>Please consult the source code ... </p>
 *
 * @author Mattias Ulbrich
 * @version originally 2008.1
 */

public class Example {

    public static void main(String[] args) throws TimeoutException, ContradictionException {

        ISolver solver = SolverFactory.newDefault();

        // add a clause (a OR b)
        VecInt clause = new VecInt(new int[] {1, 2});
        solver.addClause(clause);

        // (a OR ¬b)
        clause = new VecInt();
        clause.push(1).push(-2);
        solver.addClause(clause);

        boolean sat = solver.isSatisfiable();
        System.out.println(sat ? "SAT" : "UNSAT");
        System.out.println(Arrays.toString(solver.model()));
        System.out.println("Var 2: " + solver.model(2));

        solver.addClause(new VecInt().push(-1));
        sat = solver.isSatisfiable();
        System.out.println(sat ? "SAT" : "UNSAT");
	}

}
