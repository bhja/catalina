Capturing the KILL -9 is not possible since that is an immediate termination. 
SIGTERM can be captured as regular shutdown but not the forceful one. 
Added logic to implement the one that is most close to what might meet your needs
When kill -9 is issued developer knows what is being done hence can use a script to run this and log the 
steps in the shell script and audit trail.
