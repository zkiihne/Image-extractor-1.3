# Image-extractor-1.3
Project report: Image Extraction Summer project
Zachary Kiihne
8/11/2016

The project: 

Older scans of Astrophysics journals present a problem for ADS and parsing in general. These files are in the Tagged Image File Format(TIFF for short), which unlike modern day PDF’s are not searchable. As a result they cannot be parsed as easily and cannot have the images simply taken out. The point of this project was to solve this issue, enabling the images to be separated from these older journal scans.

What the solution was:

The actual code involve using Java to first parse through an XML metadata file created by the Optical Character Recognition software from Luratech. This file is used to get the coordinates of each image within the TIFF files. Then by matching these coordinates with the TIFF files new images are created. These images are then saved along with metadata containing things such as their height and width, along with some other metadata. All of this can be run from the command line. As for commands that are available via the command line, you can test to make sure certain addresses exist, you can run the cut out program on anywhere from an entire journal to a single bibcode. You can also then classify previously generated thumbnails through the command line. 

Problems encountered:

Along the way to finishing the program I encountered several problems and issues most of which were overcome. When sorting the images the java method to list files in a directory did not return the list in a sensible order. To fix this I implemented a quicksort algorithm. Another issue was minimizing the amount of time the program took. Each TIFF file in a journal or volume is run in a separate thread, with a max of 5 threads running at anyone time. This way not too many threads are running and impeding each other, while multiple processes are happening at the same time. To speed up the process of sorting I had the program only look at every one out of 25 pixels in its classification algorithm. Finally the cut outs dictated by the OCR program are sometimes inaccurate. To correct for this I did two things. First I increased the size of the cut outs, to capture things that were cut out of the image. Then I got rid of all the images under a certain size. Images under this size(360,000 pixels) were all errors, usually pieces of graphs and equations. This did not solve the underlying issue of incorrect coordinates though. For this reason I am going to explore OpenCV in these coming weeks to try and create my own method of getting coordinates of images. 




Instructions

General: 
In order to locate the root files, where the journals are stored(and the volumes and bibcodes within them) you need to edit the config file. The default currently is /home/zkiihne/config.txt* and is on line 56 of runner.java. If you want to use a different config file use the -config command. This will only work for the command that follows it, it does not permanently change the address. 

The code has only been tested on the adszee server, and while I am fairly certain that the file system is the same I would still like to confirm. For example this should be the address of volume 0111 of the journal AJ:
/proj/adszee/zkiihne/data/images/seri/AJ.../0111/

*I can change this to something you know works

Commands:

-test/-t:
You should run this command before running any other command . Include a journal name followed by a volume number. This command will ensure that everything is where it is expected to be. If you do not know a volume number for the journal put 0000 instead and ignore it when it says there are issues with the volume.
Example: -t AJ… 0111

-config/-c:
This command goes before any of the other six. It will set the config file to whatever you specify when it runs that command. It does not change it permanently. The default address is /home/zkiihne/config.txt. The config file should look like this:
OCR_ROOT =/proj/adszee/zkiihne/data/ocr/seri/
IMAGES_ROOT =/proj/adszee/zkiihne/data/images/seri/
METADATA_ROOT =/proj/adszee/zkiihne/data/metadata/seri/

Example: -c /home/jappleseed/othertext.txt -t AJ… 0111

-sort/-s:
Sort is intended to be run only after you have run the main part of the program(one of the three commands below this one) on a volume. It will sort the cut outs into three folders, in the directory of the volume, Graphs, Pictures and In Between
Example: -s AJ… 0111

-journal/-j:
Processes an entire journal, cutting out the images and saving these cut outs and their metadata in the sub directory’s of the one containing the pictures.
Example: -j AJ...

-volume/-v:
Processes an entire volume, cutting out the images and saving these cut outs and their metadata in the sub directory’s of the one containing the pictures. Must be preceded by a -journal command
Example: -j AJ… -v 0111

-bibcode/-b:
Processes a specific bibcode, cutting out the images and saving these cut outs and their metadata in the sub directory’s of the one containing the pictures.
Example: -b 1996AJ....111..109S

-help/-h:
This command simply prints out the help menu with all the commands.
Example: -h

Running the code in a new environment:

The data for the images files needs to be formated in the following way:

ROOT/Journal/Volume/600/example.tiff
For the XML files it should be like this:
ROOT/Journal/Volume/example.xml
For the DAT files it should be like this:
ROOT/Journal/example.dat

You have to change the config file to change the root. See more about this under the -config/-c section. To permanently change the address you need to change the code.

