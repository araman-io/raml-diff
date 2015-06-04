## The Problem
RAML specifications evolve over time. However except for manually comparing specifications there is no good way to figure out what changed between 2 versions of the RAML file. A simple file diff is also not an efficient strategy since it only compare the file structure and not semantically. 

## The Solution
This application makes it easier to find differences and prints them out. When provided 2 versions of the same file; it does a structured comparison. 

## Semantical Differences
The application can figure out the following changes
* New actions
* Removed actions
* Actions with updated query paramaters
* Actions with new responses associated 
* Actions where the response schema has changed
* Actions with updated traits

Let us know if you have any suggestions.
