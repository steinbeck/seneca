# seneca
## Introduction
Seneca is a software for computer-assisted structure elucidation based on a stochastic structure generator. Its basic function is documented in 
[1, 2].
The software is licensed under GPL 2.0.
This github repository hosts the latest version of Seneca based on the latest version of the Chemistry Development Kit (CDK).

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
should get you an executable jar file which you can then run by 
```
java -jar seneca.jar
```


## Contributors

Seneca was conceived by Christoph Steinbeck. He also wrote the first version documented in [1]. Yongquan Han wrote the evolutionary algorithm engine documented in [2]. Kalai Jayaseelan migrated Senenca to a modern day version of the CDK, implemented the adaptive simulated annealing engine and renovated the evolutionary engine. Seneca was hosted on Sourceforge until March 2018. In order to avoid confusion, we removed the sourceforge repository. A copy of the SF subversion repository is available on request.  

## References:
[1] Han, Y. Q., and Steinbeck, C. (2004) Evolutionary-algorithm-based strategy for computer-assisted structure elucidation. Journal of Chemical Information & Computer Sciences 44, 489–498.5

[2] Steinbeck, C. (2001) SENECA: A platform-independent, distributed, and parallel system for computer-assisted structure elucidation in organic chemistry. Journal of Chemical Information & Computer Sciences 41, 1500–1507.
