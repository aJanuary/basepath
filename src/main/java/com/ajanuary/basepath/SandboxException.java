package com.ajanuary.basepath;

import java.io.IOException;

public class SandboxException extends IOException {
	private static final long serialVersionUID = -8777981435370502875L;
	
	public SandboxException(String relativePath) {
		super("Relative path \"" + relativePath + "\" escapes base path");
	}
	
}
