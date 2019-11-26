# stringart [![Build Status](https://travis-ci.org/jblezoray/stringart.svg?branch=master)](https://travis-ci.org/jblezoray/stringart) [![Coverage Status](https://codecov.io/gh/jblezoray/stringart/branch/master/graph/badge.svg)](https://codecov.io/gh/jblezoray/stringart)




**Work in progress.**


This project generates string art versions of any image.

Feed it an image, and it will re-create it with a single string connected to nails.


## Usage

Install maven >=3.6.2 and a java jdk >=11.  Compile with:

```
$ mvn package
```

This generates a standalone command line application in `cli/target/stringart.jar`:

```
$ java -jar cli/target/stringart.jar --help
Usage:
    java -jar <jar_file> [flags] [options] <goal image>

Arguments:
   <goal image>          The goal image.

Flags:
   -help, -h             print help
   -quiet, -q            Quiet mode
   -disableEdgeWay       disables the rendering of the way the string goes around edges. Enables a 
                         ~4x faster rendering, but is less precise.

Options:
   -importanceImage, -i  An importance image, that ponderates the pixels of the goal image
   -output, -o           Output file for rendering the graphical result
   -diff, -d             Output file for a pixel to pixel difference between the rendering and the 
                         goal image
   -stringPath, -s       Output file for saving the string path
```

To run it on the sample files included in the project, use this command.  It will run for 1 or 2 hours to generate the final result.

```
$ java -jar cli/target/stringart.jar \
	samples/einstein/goal_image.png \
	-i samples/einstein/importance_image.png
```
 


## Related works

This project has had many inspiration sources:

The original idea came from Greek artist [Petros Vrellis](http://artof01.com/vrellis/works/knit.html) in 2016. 

Vrellis idea was latter refined in 2018 by Michael Birsak et al. They proposed a "method for the automatic computation and digital fabrication of artistic string images" \[1\] ([Matlab sources](https://github.com/Exception1984/StringArt)). When I began this project, I tried to implement an alternative implementation based on [Genetic algorithms](https://github.com/jblezoray/GeneticAlgo), but I ultimately failed and had to retreat to the amazing Birsak proposition.

\[1\] Michael Birsak et al. "String Art: Towards Computational Fabrication of String Images", Computer Graphics Forum (Proc. EUROGRAPHICS 2018), 37(2), April 2018. 


Many other implentations are available on the internet:
* [Knit](https://github.com/MaloDrougard/knit), in C++, as part of a master thesis project;
* [Strixel](https://github.com/wose/Strixel), in C++ with a GUI;
* [Knitter](https://github.com/christiansiegel/knitter), in Processing, by Christian Siegel;
* [ThreadTone](http://www.thevelop.nl/blog/2016-12-25/ThreadTone/), in Python;
* [Automate the art](https://hackaday.io/project/13047-automate-the-art), also in Processing, is a project that appears on hackaday.io;
* [A string art font](http://erikdemaine.org/fonts/stringart/) by Erik Demaine;
* [Mid-air Laser Image Display](https://hackaday.io/project/12889-mid-air-laser-image-display) is a somehow similar project that appears on hackaday, but it relies on lasers instead of strings.


## TODO list

* a GUI.
* CLI: an option for setting the shape.
* CLI: show default values in the help.
* CLI: add a possibility to use a text string path file as a start point.
* CORE: before the upscaling step, run a genetic algo for avoiding local optimums. (see [1](http://dr.library.brocku.ca/handle/10464/13709), [2](http://www.cosc.brocku.ca/~bross/JNetic/))
* CORE: use GPUs if any.



