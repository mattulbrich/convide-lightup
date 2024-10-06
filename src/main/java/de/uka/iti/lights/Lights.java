// Copyright (C) 2008 Universitaet Karlsruhe, Germany
//
// This source is protected by the GNU General Public License. 
//

package de.uka.iti.lights;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This class can be used to solve "Light Up" puzzles.
 * 
 * <p>
 * It is meant as a framework for the programming assignment "Light Up"
 * available at the <a
 * href="http://i12www.ira.uka.de/~beckert/Lehre/Formale-Systeme"
 * target="_parent">homepage of the lecture</a>.
 * </p>
 * 
 * <p style="color:red">
 * Please consider checking for updates every now and then since there might
 * still be errors in these classes.
 * </p>
 * 
 * <h2>Features</h2>
 * <ul>
 * <li>"Lights" boards can be created from description strings.
 * <li>Description strings can be created from boards.
 * <li>You can query and ...
 * <li>...set single fields of the boards.
 * <li>You can visualise the board either on the console ({@link #toConsole()})
 * ...
 * <li>... or as a dialog window ({@link #showWindow()}).
 * </ul>
 * 
 * @author Mattias Ulbrich
 * @version 2008.1c
 */

public class Lights {

	// the height and width of the square board
	private final int dimension;

	// the actual board
	private final Object[] field;

	/** this immutable object stands for a light */
	public static final Object LIGHT = "L";

	/** this immutable object stands for an unconstrained block */
	public static final Object BLOCK = "B";

	/**
	 * convert a board description into a Lights object. The description must be
	 * well formulated otherwise an exception will be raised.
	 * 
	 * <p>
	 * The string must begin with a positive number - the dimension of the board
	 * followed by a colon. The following characters describe the board: <table
	 * border="1">
	 * <tr>
	 * <td>'a'-'z'</td>
	 * <td>1-26 empty fields in succession.</td>
	 * </tr>
	 * <tr>
	 * <td>'0'-'4'</td>
	 * <td>a constrained block </td>
	 * </tr>
	 * <tr>
	 * <td>'L' </td>
	 * <td>a light</td>
	 * </tr>
	 * <tr>
	 * <td>'B' </td>
	 * <td>a block</td>
	 * </tr>
	 * <tr>
	 * <td>' ', '\t', '\n', '\r'</td>
	 * <td>spaces are ignored</td>
	 * </tr>
	 * </table>
	 * 
	 * @see #toString()
	 * @param desc
	 *            a board description as string
	 * @throws LightsFormatException
	 *             if the string is not formated according to the
	 *             specifications.
	 */
	public Lights(String desc) throws LightsFormatException {
		String[] parts = desc.split(":");

		if (parts.length != 2)
			throw new LightsFormatException("Description needs a ':'");

		try {
			dimension = Integer.parseInt(parts[0]);
		} catch (NumberFormatException e) {
			throw new LightsFormatException("Dimension must be a number", e);
		}

		if (dimension < 0 || dimension > 80)
			throw new LightsFormatException(
					"Dimension must be between 0 and 80");

		int d_square = dimension * dimension;
		field = new Object[d_square];
		int pos = 0;

		for (int i = 0; i < parts[1].length(); i++) {
			if (pos >= d_square)
				throw new LightsFormatException("Out of bounds!");

			char c = parts[1].charAt(i);
			if (c >= 'a' && c <= 'z') {
				pos += (c - 'a' + 1);
			} else if (c == 'L') {
				setLinear(pos, LIGHT);
				pos++;
			} else if (c == 'B') {
				setLinear(pos, BLOCK);
				pos++;
			} else if (c >= '0' && c <= '4') {
				setLinear(pos, Integer.valueOf(c - '0'));
				pos++;
			} else if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
				// do nothing
			} else
				throw new LightsFormatException("Unrecognized character: " + c
						+ " at position " + i);
		}
	}

	/**
	 * create a new empty Lights-board.
	 * 
	 * @param dimension
	 *            the height and width of the board, positive.
	 * @throws IllegalArgumentException
	 *             if dimension is not positive.
	 */
	public Lights(int dimension) {
		if (dimension <= 0)
			throw new IllegalArgumentException("Dimension non-positive: "
					+ dimension);
		this.dimension = dimension;
		this.field = new Object[dimension * dimension];
	}

	/**
	 * generate a describing String from this board.
	 * 
	 * see {@link #Lights(String)} for the format of the string.
	 * 
	 * @return a string completely describing the board
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(dimension).append(':');
		int acc = 0;
		for (int i = 0; i < field.length; i++) {
			Object content = getLinear(i);
			if (content == null) {
				acc++;
				if (acc == 26) {
					sb.append('z');
					acc = 0;
				}
			} else {
				if (acc > 0) {
					sb.append((char) ('a' + acc - 1));
					acc = 0;
				}
				sb.append(content);
			}
		}
		if (acc > 0)
			sb.append((char) ('a' + acc - 1));
		return sb.toString();
	}

	// access the field in a transparent way
	private Object getLinear(int pos) {
		return field[pos];
	}

	// access the field in a transparent way
	private void setLinear(int pos, Object o) {
		field[pos] = o;
	}

	/**
	 * return the (immutable) object that is used to describe a field on the
	 * board.
	 * 
	 * The returned object can either be
	 * <ul>
	 * <li>null if it is empty
	 * <li>LIGHT if it is a light
	 * <li>BLOCK if it is an unconstrained block
	 * <li>an instance of Integer with value 0 .. 4 if it is a contrained block
	 * </ul>
	 * 
	 * @see #LIGHT
	 * @see #BLOCK
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return see above.
	 */
	public Object getField(int row, int col) {
		if (row < 0 || row >= dimension || col < 0 || col >= dimension)
			throw new IndexOutOfBoundsException("getField: " + row + ", " + col);
		return field[row * dimension + col];
	}

	// set the value at a field w/o further check.
	private void setField(int row, int col, Object obj) {
		if (row < 0 || row >= dimension || col < 0 || col >= dimension)
			throw new IndexOutOfBoundsException("setField: " + row + ", " + col);
		field[row * dimension + col] = obj;
	}

	/**
	 * get the width/height of this board.
	 * 
	 * @return a positive integer
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * check if a field on the board is empty, i.e. neither a light nor a block
	 * is placed on it.
	 * 
	 * @see #clear(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return true iff the field is indeed empty
	 */
	public boolean isEmpty(int row, int col) {
		return getField(row, col) == null;
	}

	/**
	 * check if a field on the board has been set to a light.
	 * 
	 * @see #setLight(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return true iff the field is indeed a light.
	 */
	public boolean isLight(int row, int col) {
		return getField(row, col) == LIGHT;
	}

	/**
	 * check if a field on the board has been set to a block. This can be either
	 * a constrained or an unconstrained block.
	 * 
	 * @see #setBlock(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return true iff the field is indeed a block.
	 */
	public boolean isBlock(int row, int col) {
		return getField(row, col) == BLOCK || isConstrainedBlock(row, col);
	}

	/**
	 * check if a field on the board has been set to a constrained block. This
	 * returns false for unconstrained blocks
	 * 
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return true iff the field is indeed a constrained block.
	 */
	public boolean isConstrainedBlock(int row, int col) {
		return getField(row, col) instanceof Integer;
	}

	/**
	 * Get the value of a constraint of a field.
	 * 
	 * This value is between 0 and 4. Please make sure the indexed field
	 * contains indeed a constrained block. Otherwise a
	 * {@link IllegalArgumentException} will be thrown.
	 * 
	 * @see #setBlockConstraint(int, int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @throws IllegalArgumentException
	 *             if the field is not a constrained block
	 * @return the value of the constraint of the indexed field (0..4)
	 */
	public int getBlockConstraint(int row, int col) {
		if (getField(row, col) instanceof Integer) {
			Integer val = (Integer) getField(row, col);
			return val.intValue();
		} else {
			throw new IllegalArgumentException("not a contrained block: " + row
					+ ", " + col);
		}
	}

	/**
	 * Get the number of neighbouring fields that contain a light.
	 * 
	 * In a valid solution this value needs to be the same as the result of
	 * {@link #getBlockConstraint(int, int)}(row,col) if this does not yield an
	 * exception.
	 * 
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return the number of fields which are neighbours (horizontally or
	 *         vertically) on which a light is placed
	 */
	public int getLitNeighbours(int row, int col) {
		int count = 0;
		
		if(row > 0 && isLight(row-1, col))
			count ++;
		
		if(row < getDimension()-1 && isLight(row+1, col))
			count ++;
		
		if(col > 0 && isLight(row, col-1))
			count ++;
		
		if(col < getDimension()-1 && isLight(row, col+1))
			count ++;

		
		return count;
	}


	/**
	 * check if a field in the board is lit a by a light placed on the board.
	 * This can be on the same field or on a field horizontally or vertically
	 * visible from this field. Light does not go through blocks.
	 * 
	 * @see #isLight(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @return true iff the indexed field is lit by a light placed somewhere on
	 *         the board.
	 */
	public boolean isLit(int row, int col) {
		// top
		for (int k = row; k >= 0; k--) {
			if (isBlock(k, col))
				break;
			if (isLight(k, col))
				return true;
		}

		// bottom
		for (int k = row; k < dimension; k++) {
			if (isBlock(k, col))
				break;
			if (isLight(k, col))
				return true;
		}

		// left
		for (int k = col; k >= 0; k--) {
			if (isBlock(row, k))
				break;
			if (isLight(row, k))
				return true;
		}

		// right
		for (int k = col; k < dimension; k++) {
			if (isBlock(row, k))
				break;
			if (isLight(row, k))
				return true;
		}

		return false;
	}

	/**
	 * set a field on the board to empty.
	 * 
	 * @see #setLight(int, int)
	 * @see #setBlock(int, int)
	 * @see #setBlockConstraint(int, int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 */
	public void clear(int row, int col) {
		setField(row, col, null);
	}

	/**
	 * set a field on the board to "Light". This can only be done if the field
	 * is empty or has been set to "Light" previously. 
	 * This restriction is a precaution to not accidently change the
	 * structure of a field.
	 * 
	 * @see #isLight(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @throws IllegalArgumentException
	 *             if the indexed field is neither empty nor a light.
	 */
	public void setLight(int row, int col) {
		Object f = getField(row, col);
		if (f != null && f != LIGHT)
			throw new IllegalArgumentException("The field " + row + ", " + col
					+ " is already occupied by " + f);
		setField(row, col, LIGHT);
	}
	
	/**
	 * set a field on the board to "Empty". This can only be done if the field
	 * is empty or has been set to "Light" previously. 
	 * This restriction is a precaution to not accidently change the
	 * structure of a field.
	 * 
	 * @see #isLight(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @throws IllegalArgumentException
	 *             if the indexed field is neither empty nor a light.
	 */
	public void setEmpty(int row, int col) {
		Object f = getField(row, col);
		if (f != null && f != LIGHT)
			throw new IllegalArgumentException("The field " + row + ", " + col
					+ " is already occupied by " + f);
		setField(row, col, null);
	}

	/**
	 * Removes all lights from the board.
	 *
	 * All fields that are set to LIGHT will be reset to empty fields.
	 * @see #setEmpty(int, int)
	 */
	public void removeAllLights() {
		for (int i = 0; i < field.length; i++) {
			if (field[i] == LIGHT)
				field[i] = null;
		}
	}


	/**
	 * set a field on the board to an unconstrained block. This can only be done
	 * if the field is empty or has perviously been set to an unconstrained block. 
	 * This restriction is a precaution to not accidently
	 * change the structure of a field.
	 * 
	 * @see #isBlock(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @throws IllegalArgumentException
	 *             if the indexed field is neither empty nor an unconstrained
	 *             block.
	 */
	public void setBlock(int row, int col) {
		Object f = getField(row, col);
		if (f != null && f != BLOCK)
			throw new IllegalArgumentException("The field " + row + ", " + col
					+ " is already occupied by " + f);
		setField(row, col, BLOCK);
	}

	/**
	 * set a field on the board to a constrained block. This can only be done if
	 * the field is empty or has previously been set to a constrained block.
	 * This restriction is a precaution to not accidently
	 * change the structure of a field. Please note that you can change the
         * value of the constraint without exception.
	 *
	 * @see #getBlockConstraint(int, int)
	 * @see #isConstrainedBlock(int, int)
	 * @param row
	 *            the index of the row of the field (0 .. dimension-1)
	 * @param col
	 *            the index of the column of the field (0 .. dimension-1)
	 * @param constraint
	 *            the value to set to the field (0..4)
	 * @throws IndexOutOfBoundsException
	 *             if row or col are not within the bounds.
	 * @throws IllegalArgumentException
	 *             if the indexed field is neither empty nor a constrained block
	 *             or if the constraint is not within the valid range.
	 */
	public void setBlockConstraint(int row, int col, int constraint) {
		if (constraint < 0 || constraint > 4)
			throw new IllegalArgumentException("constraint not valid: "
					+ constraint);
		Object f = getField(row, col);
		if (f != null && !(f instanceof Integer))
			throw new IllegalArgumentException("The field " + row + ", " + col
					+ " is already occupied by " + f);
		setField(row, col, Integer.valueOf(constraint));
	}


	/**
	 * open a modal dialog window that displays the current field. The thread
	 * continues after the window has been closed.
	 * 
	 * <b>Pleas note:</b> You need to terminate your program via
	 * {@link System#exit(int)} if you use this method. This is due to some AWT
	 * thread.
	 * 
	 * @see JDialog
	 * @see LightsComponent
	 */
	public void showWindow() {
		JDialog dlg = new JDialog(new JFrame(), "Lights!", true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.getContentPane().add(new LightsComponent(this));
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * print the board to the console, i.e. {@link System#out} each field is
	 * represented by a character with the following meanings:
	 * <ul>
	 * <li> ' ' (space) for an empty field
	 * <li> 'B' for an unconstrained block
	 * <li> '0'-'4' for a constrained block
	 * <li> 'L' for a light
	 * </ul>
	 */
	public void toConsole() {
		for (int row = 0; row < dimension; row++) {
			for (int col = 0; col < dimension; col++) {
				Object o = getField(row, col);
				System.out.print(o == null ? " " : o);
			}
			System.out.println();
		}
	}

	/**
	 * create a new object of this class which gets a copy the fields array. The
	 * entries in the array are not deep copied since they are immutable.
	 * 
	 * @return a new (identical) Lights object
	 */
	public Lights clone() {
		Lights ret = new Lights(dimension);
		for (int i = 0; i < field.length; i++) {
			ret.field[i] = field[i];
		}
		return ret;
	}

	/**
	 * compare this Lights board to another object.
	 * 
	 * They are considered equal if and only if obj is an instance of Lights and
	 * the field has the same dimension and contains the same object in the same
	 * order.
	 * 
	 * @param obj
	 *            an arbitrary object or null
	 * @return true iff the board is equal to the board described by obj
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Lights) {
			Lights l2 = (Lights) obj;
			if (l2.dimension != dimension)
				return false;

			// we always use the same objects, so comparison with == suffices
			// instead of equals().
			for (int i = 0; i < field.length; i++) {
				if (field[i] != l2.field[i])
					return false;
			}
			return true;
		} else {
			return false;
		}
	}

}
