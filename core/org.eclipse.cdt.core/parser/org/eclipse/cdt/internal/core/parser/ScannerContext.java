/*******************************************************************************
 * Copyright (c) 2001 Rational Software Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     Rational Software - initial implementation
 ******************************************************************************/
package org.eclipse.cdt.internal.core.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

import org.eclipse.cdt.core.parser.ast.IASTInclusion;

public class ScannerContext implements IScannerContext
{
	private Reader reader;
	private String filename;
    private int macroOffset = -1;
    private int macroLength = -1;
	private int line = 1;
	private int offset;
	private Stack undo = new Stack(); 
	private int kind; 
				
	public ScannerContext(){}
    
    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IScannerContext#initialize(Reader, String, int, IASTInclusion, int, int, int)
     */
	public IScannerContext initialize(Reader r, String f, int k, IASTInclusion i, int mO, int mL, int l)
	{
		reader = r;
		filename = f;
		offset = 0;
		kind = k; 
		inc = i;
        macroOffset = mO;
        macroLength = mL;
        line = l;
		return this;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IScannerContext#initialize(Reader, String, int, IASTInclusion)
     */
    public IScannerContext initialize(Reader r, String f, int k, IASTInclusion i)
    {
        return initialize(r, f, k, i, -1, -1, 1);
    }
		
	public int read() throws IOException {
		++offset;
		int c = reader.read();
		if ((char)c == '\n') line++;
		return c;
	}
	
	/**
	 * Returns the filename.
	 * @return String
	 */
	public final String getFilename()
	{
		return filename;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IScannerContext#getExtension()
     */
    public final int getMacroOffset() 
    {
        return macroOffset;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IScannerContext#getMacroLength()
     */
    public final int getMacroLength() 
    {
        return macroLength;
    }

	/* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IScannerContext#getOffset()
     */
	public final int getOffset()
	{
        // All the tokens generated by the macro expansion 
        // will have dimensions (offset and length) equal to the expanding symbol.
		return (macroOffset < 0) ? offset : macroOffset;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IScannerContext#getRelativeOffset()
     */
    public final int getRelativeOffset()
    {
        return offset;
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.parser.IScannerContext#getLine()
	 */
	public final int getLine()
	{
		return line;
	}

	/**
	 * Returns the reader.
	 * @return Reader
	 */
	public final Reader getReader()
	{
		return reader;
	}

	public final int undoStackSize()
	{
		return undo.size();
	}

	/**
	 * Returns the undo.
	 * @return int
	 */
	public final int popUndo()
	{
		int c = ((Integer)undo.pop()).intValue();
		if ((char)c == '\n') line++;
		return c;
	}

	/**
	 * Sets the undo.
	 * @param undo The undo to set
	 */
	public void pushUndo(int undo)
	{
		if ((char)undo == '\n') line--;
		this.undo.push( new Integer( undo )); 
	}


	/**
	 * Returns the kind.
	 * @return int
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * Sets the kind.
	 * @param kind The kind to set
	 */
	public void setKind(int kind) {
		this.kind = kind;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.parser.IScannerContext#getExtension()
	 */
	public IASTInclusion getExtension() {
		return inc;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.parser.IScannerContext#setExtension(org.eclipse.cdt.core.parser.ast.IASTInclusion)
	 */
	public void setExtension(IASTInclusion ext) {
		inc = ext;
	}
	
	private IASTInclusion inc = null;


}
