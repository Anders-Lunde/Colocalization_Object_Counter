# Colocalization Object Counter
## An ImageJ plugin for cell-by-cell semi-automatic object based colocalization analysis
![alt text](https://github.com/Anders-Lunde/Colocalization_Object_Counter/blob/master/Plugin%20overview.png "Plugin overview")

## NB! This is the repository for Plugin 2 in the above image. Plugin 1 has its [own repository here](https://github.com/Anders-Lunde/Colocalization_Image_Creator).
## NB 2! Both plugins 1 + 2, including the Excel and Matlab files are automatically installed by adding our update site (see Installation - option 1).

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
3. Add https://sites.imagej.net/ObjectColocalizationPlugins/ as the URL. 
4. Click Close>Apply changes.

### Option 2):
Download the most recent .jar files from urls below, and put in your ImageJ/FIJI /plugins/ folder:

[Link to plugin 1 JAR file](https://github.com/Anders-Lunde/Colocalization_Image_Creator/tree/master/jar-file) (Click on the .jar file, and click "download" top right).

[Link to plugin 2 JAR file](https://github.com/Anders-Lunde/Colocalization_Object_Counter/tree/master/jar-file) (Click on the .jar file, and click "download" top right).


## Full description of the plugin can be found in the original publication:
Currently in review in [Scientific Reports] (https://www.nature.com/srep/) (Lunde&Glover 2020).



### This ImageJ plugin enables semi-automatic identification and quantification of image objects, with the opportunity to employ varying degrees of automation as desired. The  colocalization Object Counter is optimized for annotating and keeping track of each object’s colocalization category, that is, which fluorescent label(s) are associated with each object. The plugin also contains basic tools for subsequent 3D reconstruction of object and tissue contour data. 

![alt text](https://github.com/Anders-Lunde/Colocalization_Object_Counter/blob/master/Colocalization%20Object%20Counter%20menu.png "Colocalization Object Counter menu")

## Introduction:

Tools and methods that employ fully automatic colocalization analysis have the benefit of providing reproducibility and speed, albeit sometimes at the cost of accuracy. A major reason for lowered accuracy is that full automation of segmentation and object identification is a notoriously difficult problem to solve, especially for complex objects such as neurons 15,16. To address the need for a high-throughput OBCA workflow that does not rely exclusively on fallible automated algorithms and that leverages human visual processing capacity, we have developed a set of tools for semi-automatic OBCA that combines automation for speed with visual/manual verification for accuracy. The tools we present use image binarization and other operations to extract and visualize meaningful colocalization signals, but ultimate quantification is based on a centroid-like approach in which objects are defined by a single point. We emphasize the utility of visual verification and correction of automatic centroid placement, without the need to perform time consuming corrections to object delineations for quantification.

A schematic of the tool-chain workflow is shown in Figure 1. The entry point to the workflow consists of a plugin (the Colocalization Image Creator) for the popular free and open source image analysis software ImageJ 17. The Colocalization Image Creator enables flexible processing of image data into a visual format that is better suited to high-throughput semi-automatic OBCA. It can produce processed binary and grayscale signal outputs, visualize signal overlap across channels, and can produce a special Z-projection where 3D information is condensed onto a 2D plane for easy visualization of 3D colocalization data, in a way that minimizes Z-projection artifacts (examples of such artifacts are shown in Figure 2A). Additionally, signal overlap processing enables restricting visualization to labeled cellular sub-compartments, for example cell nuclei, which can improve object segmentation. This can also minimize artifacts that arise from partially transected objects (example in Figure 2B). 
A second ImageJ plugin (the Colocalization Object Counter) enables OBCA quantification in any type of image. It uses local maxima algorithms to automatically define objects as single points, which can be edited and verified visually in a semi-automatic manner. The Colocalization Object Counter enables annotating which label(s) are associated with each object (object colocalization category). The plugin also contains basic tools for subsequent 3D reconstruction of object and tissue contour data.

A third tool, in the form of a Microsoft Excel macro file, enables data import overview, revision, statistical analysis, and export. Exported data can be imported by a fourth tool, a Matlab script that enables interactive 3D visualization of identified objects with their colocalization categories indicated within visible tissue contours.

Although we have designed and optimized this tool set for semi-automatic OBCA of multi-fluorescence imaging data from the developing central nervous system, the workflow is adaptable to other types of imaging data as well. The tools complement each other, but can also be used individually. Below, we describe the function of each tool, provide some examples of usage, and provide an assessment of robustness across individual operators. We compare our platform head-to-head with similar platforms, and we evaluate specific limitations associated with our platform. In the Supplementary Information we provide specific details on the operation and user interfaces of the tool set.


## Purpose, input requirements, operation and output:

The Colocalization Object Counter plugin is used for the quantification part of our OBCA pipeline, and also comes with a set of tools for subsequent 3D reconstruction of data. It enables semi-automatic identification, quantification and XYZ-coordinate designation of image objects, and comes with a set of effective and simple tools for assigning specific colocalization categories for each object (associating specific cellular markers or labels with objects). For automatic object detection, it uses local maxima algorithms to place ImageJ multipoints (XYZ-coordinate points) to define objects, which can be edited and verified visually in a semi-automatic manner. The user can choose between having multipoints assigned and displayed only on specific Z-images, or all Z-images , depending for example on whether a complete Z-stack is analyzed in detail, or if only a Z-projection of it is analyzed. The highest accuracy and speed are normally achieved when automatic detection is followed by manual verification and adjustment.  

Any image readable by ImageJ can be used with the plugin, including those generated with the Colocalization Image Creator plugin.  The plugin supports assignment of up to eight different categories per object, allowing for 28=256 possible colocalization combinations. See the Supplementary Information section online entitled “ImageJ plugin 2” for specific details on the plugin operation and user interfaces.

Following assignment of objects and categories, data can be saved as files (comma separated value format, CSV) that include the XYZ-coordinates and the colocalization categories of each object, the image filename and additional image metadata. Colocalization categories are specified by a string of digits; for example, “358” designates an object that is positive for categories 3, 5 and 8. In a typical experiment the category “1” would be used to designate all objects of interest, and additional category designations would be used to annotate the presence of additional markers (such as fluorescent labels) within the objects. This of course requires a generic marker for the objects of interest, such as a general cellular or nuclear marker. To help the user organize the data, all the output files are saved automatically in the same folder from which the image was loaded, in a subfolder called “Counts”. It is therefore highly recommended to store all images from the same experimental series in a single folder. Additional output files are generated in the same “Counts” folder after using the 3D serial reconstruction tools included with the Colocalization Object Counter plugin, which includes a tool for assigning the image origin coordinates and north direction, and a tool for drawing tissue contours (see below).

Together with the object count data, these can be imported and analyzed with the Excel macro file (see below), and subsequently visualized in 3D with the Matlab script (see below), both of which are automatically supplied and placed by the plugin in the appropriate output folders. To facilitate logging of operator interventions for later review, all operations involving object counting are automatically logged in a file named “Counting_log.txt” in the “Counts” folder.


