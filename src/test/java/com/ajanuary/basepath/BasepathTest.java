package com.ajanuary.basepath;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class BasepathTest {
	
	@Test
	public void canCombine() throws SandboxException {
		File combined = Basepath.concat("/a/b/c", "d/ee/f.ff");
		assertEquals(new File("/a/b/c/d/ee/f.ff"), combined);
	}
	
	@Test
	public void canCombineWithLeadingSeparator() throws SandboxException {
		File combined = Basepath.concat("/a/b/c", "/d/ee/f.ff");
		assertEquals(new File("/a/b/c/d/ee/f.ff"), combined);
	}
	
	@Test
	public void canCombineWithTrailingSeparator() throws SandboxException {
		File combined = Basepath.concat("/a/b/c", "/d/ee/f.ff/");
		assertEquals(new File("/a/b/c/d/ee/f.ff/"), combined);
	}
	
	@Test
	public void canCombineRelativePathWithParentDirectoriesAsLongAsItDoesntEscapeBasePath() throws SandboxException {
		File combined1 = Basepath.concat("/a/b/c", "d/ee/../f.ff");
		assertEquals(new File("/a/b/c/d/ee/../f.ff"), combined1);
		
		File combined2 = Basepath.concat("/a/b/c", "d/..");
		assertEquals(new File("/a/b/c/d/.."), combined2);
	}
	
	@Test
	public void treatsNullBasePathFileAsNonExistant() throws SandboxException {
		File combined = Basepath.concat((File) null, "d/ee/f.ff");
		assertEquals(new File("d/ee/f.ff"), combined);
	}
	
	@Test
	public void treatsEmpoyBasePathStringAsEmpty() throws SandboxException {
		File combined = Basepath.concat("", "d/ee/f.ff");
		assertEquals(new File("/d/ee/f.ff"), combined);
	}
	
	@Test
	public void canHandleEmptyRelativePath() throws SandboxException {
		File combined = Basepath.concat("/a/b/c", "");
		assertEquals(new File("/a/b/c/"), combined);
	}
	
	@Test
	public void treatsPartsStartingWithPeriodsAsNormalParts() throws SandboxException {
		// If these were interpreted as parent directories it would throw a SandboxException
		
		File combined1 = Basepath.concat("/a/b/c", ".d");
		assertEquals(new File("/a/b/c/.d"), combined1);
		
		File combined2 = Basepath.concat("/a/b/c", ".dd");
		assertEquals(new File("/a/b/c/.dd"), combined2);
		
		File combined3 = Basepath.concat("/a/b/c", "..d/..");
		assertEquals(new File("/a/b/c/..d/.."), combined3);
	}
	
	@Test
	public void treatsPartsEndingWithPeriodsAsNormalParts() throws SandboxException {
		// If these were interpreted as parent directories it would throw a SandboxException
		
		File combined1 = Basepath.concat("/a/b/c", "d.");
		assertEquals(new File("/a/b/c/d."), combined1);
		
		File combined2 = Basepath.concat("/a/b/c", "d..");
		assertEquals(new File("/a/b/c/d.."), combined2);
		
		File combined3 = Basepath.concat("/a/b/c", "d../..");
		assertEquals(new File("/a/b/c/d../.."), combined3);
	}
	
	@Test
	public void canHandleSingleDotPathAtEnd() throws SandboxException {
		File combined = Basepath.concat("/a/b/c", "d/ee/.");
		assertEquals(new File("/a/b/c/d/ee/."), combined);
	}
	
	@Test(expected = SandboxException.class)
	public void cantCombineRelativePathThatEscapesBasePath() throws SandboxException {
		Basepath.concat("/a/b/c", "..");
	}
	
	@Test(expected = SandboxException.class)
	public void cantCombineRelativePathThatEscapesBasePathEvenIfItGoesDownAFewParts() throws SandboxException {
		Basepath.concat("/a/b/c", "d/ee/../../../f.ff");
	}
	
	@Test(expected = SandboxException.class)
	public void treatsSingleDotPathPartsAsNotChangingDepth() throws SandboxException {
		Basepath.concat("/a/b/c", "./..");
	}
	
	@Test(expected = SandboxException.class)
	public void treatsEmptyPathPartsAsNotChangingDepth() throws SandboxException {
		Basepath.concat("/a/b/c", "//..");
	}
	
}
