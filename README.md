# Colocalization Object Counter
## An ImageJ plugin for cell-by-cell semi-automatic object based colocalization analysis
![alt text](https://github.com/Anders-Lunde/Colocalization_Object_Counter/blob/master/Plugin%20overview.png "Plugin overview")

## NB! This is the repository for Plugin 2 in the above image. Plugin 1 has its [own repository here](https://github.com/Anders-Lunde/Colocalization_Image_Creator).
## NB 2! Both plugins with the Excel and Matlab files are automatically installed by adding our update site (see Installation - option 1).

In this repository you can find:
1. the plugin .jar file
2. the plugin source code
3. *the Microsoft Excel macro file (in folder: "Excel data organizer macro file")
4. *the Matlab script. (in folder: "Matlab 3d visualization files")

*NB! The items 3. and 4. above are automatically installed when using the ImageJ plugin. There is no need to download them individually.

## Installing the plugin:
### Option 1 - Recommended!: 
1. Requires FIJI installation: https://imagej.net/Fiji/Downloads.
2. Open FIJI and click Help>Update>Manage Update Sites>Add update site
3. Add “http://sites.imagej.net/ObjectColocalizationPlugins/” as the URL. 

### Option 2):
Download the most recent .jar files from urls below, and put in your ImageJ/FIJI /plugins/ folder:

[Link to plugin 1 JAR file](https://github.com/Anders-Lunde/Colocalization_Image_Creator/tree/master/jar-file) (Click on the .jar file, and click "download" top right).

[Link to plugin 2 JAR file](https://github.com/Anders-Lunde/Colocalization_Object_Counter/tree/master/jar-file) (Click on the .jar file, and click "download" top right).


## Full description of the plugin can be found in the original publication:

### This ImageJ plugin enables semi-automatic identification and quantification of image objects, with the opportunity to employ varying degrees of automation as desired. The  colocalization Object Counter is optimized for annotating and keeping track of each object’s colocalization category, that is, which fluorescent label(s) are associated with each object. The plugin also contains basic tools for subsequent 3D reconstruction of object and tissue contour data. 

![alt text](https://github.com/Anders-Lunde/Colocalization_Object_Counter/blob/master/Colocalization%20Object%20Counter%20menu.png "Colocalization Object Counter menu")

## Purpose, input requirements, operation and output:

The Colocalization Object Counter plugin enables semi-automatic identification, quantification and
XYZ-coordinate designation of image objects, and comes with a set of effective and simple tools for
assigning specific colocalization categories for each object.
Any image readable by ImageJ can be used with the plugin, including those generated with the
Colocalization Image Creator plugin. Objects can be defined manually by marking objects with the
ImageJ “multipoint” tool, or automatically by using the built-in “automatic detection” tools. The
highest accuracy and speed are normally achieved when automatic detection is followed by manual
verification and adjustment. The plugin supports assignment of up to eight different categories per
object, allowing for 28
=256 possible colocalization combinations. See the Supplementary
Information section online entitled “ImageJ plugin 2” for details on the plugin operation and user
interfaces.
Following assignment of objects and categories, data can be saved as files (comma separated value
format, CSV) that include the XYZ-coordinates and the colocalization categories of each object, the
image filename and additional image metadata. Colocalization categories are specified by a string of
digits; for example, “358” designates an object that is positive for categories 3, 5 and 8. In a typical
experiment the category “1” would be used to designate all objects of interest, and additional 
15
category designations would be used to annotate the presence of additional markers (such as
fluorescent labels) within the objects. This of course requires a generic marker for the objects of
interest, such as a cell-specific or nuclear marker. To help the user organize the data, all the output
files are saved automatically in the same folder from which the image was loaded, in a subfolder
called “Counts”. It is therefore highly recommended to store all images from the same experimental
series in a single folder. Additional output files are generated in the same “Counts” folder after using
the 3D serial reconstruction tools included with the Colocalization Object Counter plugin, which
includes a tool for assigning the image origin coordinates and north direction, and a tool for drawing
tissue contours (see below). Together with the object count data, these can be imported and
analyzed with the associated Excel macro file (see below), and subsequently visualized in 3D with
the associated Matlab script (see below). To facilitate logging of operator interventions for later
review, all operations involving object counting are automatically logged in a file named
“Counting_log.txt” in the “Counts” folder. 

