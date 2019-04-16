# NCSA_Parallel_herd
NCSA project utilizing the parallel programming skills to improve performance.





## Week March 20th
- Receive the formula of CowVal formula and calculate the value
- Compare the result with the Matlab Code
- Read the paper and get the ME 305 formula

## Week April 1th
- Apply concurrent programming on the model
- Improve the running efficiency of the model
- Log into HTC
- Create ME 305 function
- Create a new column cumulate the value of the milk production

## Week April 10th
- Change the sorting order of the herd(line:490) [9->10]
- Rank the Cow by the value of herd (The value of NPV go up by approximately 300-400)
- Try to run it on HTC
  - Finish the first version of the sub and create simplified submission 
  - Future and more work, integrate Beta into the script file and make it easier to use
- Fix the bug of Exponential Function
- Introduce Beta


## A simple guide to the HTC_Condor commands
  - Useful commands
    - ``` condor_submit herd.sub``` (target script file) [submit the job to the cluster and return the clustervalue]
    - ``` condor_q```  [check the current status of the job]
    - ``` condor_rm 9239``` (campus cluster number) [remove the job by number]
 
## A simple guide to Condor script file
``` 
`####################
  #
  # Running a mutiple threads version of herd simulation by Professor Rebecca Smith
  # Execute the main class named herdRun
  #
  ####################

  universe       = java
  executable     = herdRun.class
  transfer_input_files = herdHelperFunc$1.class, herdParameter.class,herdHelperFunc.class, threadHerd.class
  arguments      = herdRun
  output         = herdRun.output
  error          = herdRun.error
  queue
 ```
 - Error file is stored in herdRun.error and output is stored in herdRun.output(open with cat)
 - Transfer_input_files is the sub-java classes that need to be specified for HTC to run
 - Executable is the java classes with main
 
 

