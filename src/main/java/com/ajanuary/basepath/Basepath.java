package com.ajanuary.basepath;

import java.io.File;

/**
 * Utility class for combining base paths with relative paths.
 * <p>
 * When concatenating a relative path to a base path, it is often a security requirement that
 * the relative path never escapes outside of the base path, otherwise they can explore parts
 * of the filesystem that should not be able to access.
 * <p>
 * Even with filesystem level access controls and checking that the result is inside the base
 * path, a carefully constructed relative path can be used to test for the existence of a
 * particular directory. For example, if 
 * {@code ../some/sensitive/dir/../../../base/some/innocent/location} does not give a
 * "path does not exist" error, an attacker knows that the {@code some/sensitive/dir} directory
 * structure exists.
 * 
 * @author ajanuary
 *
 */
public final class Basepath {
	
	private Basepath() {
		/* Prevent construction of util class */
	}
	
	/**
	 * Creates a new {@code File} instance from a base path file and a relative path string.
	 * <p>
	 * If the relative path ever escapes the base path by using too many {@code ..} parent
	 * references a {@link SandboxException} is thrown.
	 * 
	 * @param base  the file that forms the base of the concatenated file
	 * @param relativePath  the relative path to be concatenated with the base path
	 * @return  a file with {@code relativePath} concatenated with {@code base}
	 * @throws SandboxException if {@code relativePath} escapes {@code base}
	 * @see {@link #escapesBase(String)}
	 */
	public static File concat(File base, String relativePath) throws SandboxException {
		if (escapesBase(relativePath)) {
			throw new SandboxException(relativePath);
		}
		
		return new File(base, relativePath);
	}
	
	/**
	 * Creates a new {@code File} instance from a base path and a relative path string.
	 * <p>
	 * If the relative path ever escapes the base path by using too many {@code ..} parent
	 * references a {@link SandboxException} is thrown.
	 * 
	 * @param base  the path that forms the base of the concatenated file
	 * @param relativePath  the relative path to be concatenated with the base path
	 * @return  a file with {@code relativePath} concatenated with {@code base}
	 * @throws SandboxException if {@code relativePath} escapes {@code base}
	 * @see {@link #escapesBase(String)}
	 */
	public static File concat(String base, String relativePath) throws SandboxException {
		return concat(new File(base), relativePath);
	}

	/**
	 * Returns whether a relative path would escape the base path when concatenated with it.
	 * <p>
	 * If the number of {@code ..} parent reference parts exceeds the number of non-empty,
	 * non-parent reference parts that precedes them, the relative path is considered to escape
	 * the base.
	 * <p>
	 * <table>
	 * <tr><th>relativePath</th><th>Result</th></tr>
	 * <tr><td>a/b/c</td><td>{@code false}</td></tr>
	 * <tr><td>/a/b/c</td><td>{@code false}</td></tr>
	 * <tr><td>a/b/../c</td><td>{@code false}</td></tr>
	 * <tr><td>a//./b/c</td><td>{@code false}</td></tr>
	 * <tr><td>..</td><td>{@code true}</td></tr>
	 * <tr><td>a/b/../../../c</td><td>{@code true}</td></tr>
	 * <tr><td>.//..</td><td>{@code true}</td></tr>
	 * </table>
	 * 
	 * @param relativePath  path to test
	 * @return {@code true} if {@code relativePath} would escape the base path, else {@code false}
	 */
	public static boolean escapesBase(String relativePath) {
		// Need to normalize the path so that the non-system platform separators are converted into File.separator.
		String normalizedPath = new File(relativePath).getPath();
		
		int depth = 0;
		int startOfPart = 0;
		
		while (startOfPart < normalizedPath.length()) {
			int endOfPart = normalizedPath.indexOf(File.separator, startOfPart);
			if (endOfPart == -1) {
				endOfPart = normalizedPath.length();
			}
			
			int partLen = endOfPart - startOfPart;
			if (partLen == 2 && normalizedPath.charAt(startOfPart) == '.' && normalizedPath.charAt(startOfPart + 1) == '.') {
				if (depth == 0) {
					return true;
				}
				
				depth -= 1;
			} else if (partLen > 0 && !(partLen == 1 && normalizedPath.charAt(startOfPart) == '.')) {
				depth += 1;
			}
			
			startOfPart = endOfPart + 1;
		}
		
		return false;
	}

}
