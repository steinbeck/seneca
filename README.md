# Seneca
## Introduction
Seneca is a software for computer-assisted structure elucidation based on a stochastic structure generator. Its basic function is documented in 
[1, 2].
The software is licensed under GPL 2.0.
This github repository hosts the latest version of Seneca based on the latest version of the Chemistry Development Kit (CDK).

IMPORTANT:
Seneca in its current state is, and for the last 20 years was, what Barend Mons calls 'professorware' :)
It is here for historical reasons and to document our work from 20 years ago. 
We acknowledge that it is for the brave early adopter, but we are working hard to get to stage where we can give a nice GUI app to true experimental chemists to load some NMR fresh from the spectrometer and start the CASE from there. 


## Installation
In preparation for using Seneca, goto https://github.com/steinbeck/casekit and follow the instructions to download. 
Once done, also do a 
```
mvn install
```

To install Seneca, get the latest version from this Git repository by using the green "Clone or download" button or do

```
git clone https://github.com/steinbeck/seneca.git
```
on your command line.
```
mvn package
```
should get you an executable jar file in the 'target' directory, which you can then run by 
```
java -jar seneca.jar (use the real filename with all dependencies included)
```
## Using Seneca
For now, start Seneca as mentioned above. Goto File->Open->SenecaDataset, navigate to your source directory and there to target->test-classes->data. 
Load the file Eurabidiol.sml. 
You can then inspect the various aspects of the data tree in the left-most window of Seneca, or move directly to Structure Generation->Simulation.
In this window, you should see one local structure generator running, and the option "Evolutionary Algorithm" selected. Press the button "Start Structure Generation" in the lower part of the window to get the structure elucidated. Please note that due to the stochastic nature of the process, the CASE does not end with the perfect solution in every case. For this reason, you can start a handful (instead of only one) local servers, select them all and then press "Start Structure Generation". 

## Contributors

Seneca was conceived by Christoph Steinbeck. He also wrote the first version documented in [1]. Yongquan Han wrote the evolutionary algorithm engine documented in [2]. Kalai Jayaseelan migrated Senenca to a modern day version of the CDK, implemented the adaptive simulated annealing engine and renovated the evolutionary engine. Seneca was hosted on Sourceforge until March 2018. In order to avoid confusion, we removed the sourceforge repository. A copy of the SF subversion repository is available on request.  

## References:
[1] Han, Y. Q., and Steinbeck, C. (2004) Evolutionary-algorithm-based strategy for computer-assisted structure elucidation. Journal of Chemical Information & Computer Sciences 44, 489–498.5

[2] Steinbeck, C. (2001) SENECA: A platform-independent, distributed, and parallel system for computer-assisted structure elucidation in organic chemistry. Journal of Chemical Information & Computer Sciences 41, 1500–1507.
