# basepath

A library for combining base paths with relative paths while checking that the relative path never escapes the base path.

When concatenating a relative path to a base path, it is often a security requirement that the relative path never escapes outside of the base path, otherwise they can explore parts of the filesystem that should not be able to access.

Even with filesystem level access controls and checking that the result is inside the base path, a carefully constructed relative path can be used to test for the existence of a particular directory. For example, if `../some/sensitive/dir/../../../base/some/innocent/location` does not give a "path does not exist" error, an attacker knows that the `some/sensitive/dir` structure exists.

## Usage

```java
File base = configuration.getBasePath();
System.out.print("File path: ");
String relative = System.console.readline();

try {
  File path = Basepath.concat(base, relative);
  ...
} catch (SandboxException e) {
  System.err.println(e.getMessage());
}
```
