%clear; %clear workspace variables
%close all; %close all figures

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Options:
%Adjust numbers or other variables below to change visualization appearance.
%Save the file after editing, and run the script.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

alphaRad = 400;             %alphashape radius. Try half pixel distance of one typical countour size
alphaColor = 'black';       %alphashape color
alphaTransparency = 0.2;    %alphashape transparency. From 0 to 1.
xy_scale = 1;               %try to change this is Z-level scaling is wrong. From 0 to infinity.
showGrid = true;			%Show/hide figure grid

objectShape = 'o';          %'+' , '*' , '.' , 'x', 'o' etc... see https://se.mathworks.com/help/matlab/ref/scatter.html
showOutlines = true;       	%Show/hide objects outline NB! Must be true for certain 'objectShape' types
outlineColor = 'black';     %Objects outline color
objectSize = 50;            %Objects size


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Script start:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

outlineFile = 'outline_coordinates.csv';
colorFile = 'colors.txt';
cellFile = 'cell_coordinates.csv';

%Read and process color defenition file
f=fopen(colorFile,'r');
C=textscan(f,'color%d=[%d, %d, %d]');
fclose(f);
RGB=[C{2},C{3},C{4}];
C=C{1};

%Read and process cell/object/scatterplot file
a=dlmread(cellFile);
x=a(:,1) * xy_scale; %X scaling
y=a(:,2) * xy_scale; %Y scaling
z=a(:,3);
col=a(:,4);

%Assign unique colors to each colocalization category
rgb=[];
uniqueCategories = unique(col);
for i=1:length(col)
    index = find(col(i)==uniqueCategories);
    rgb=[rgb; RGB(index,:)];
end
for i=1:length(uniqueCategories)
    rgbValue = mat2str(RGB(i,:));
    category = int2str(uniqueCategories(i));
    s = strcat('Cell category_', category, ' --> color', int2str(i), '=', rgbValue, ' according to colors.txt');
    if i==1
        msg={s};
    else
        msg=[msg;{s}];
    end
end
msgbox(msg,'Info regarding color codes','help','replace');


%Make the figure/plog
clear fig;
hold('on');

%Make the objects scatterplot
for i=1:size(x)
    xTmp = x(i);
    yTmp = y(i);
    zTmp = z(i);
    rgbTmp = rgb(i,:);
    rgbTmp = rdivide(rgbTmp, 255); %make rgb values between 0 and 1;
    rgbTmp = double(rgbTmp);
    if showOutlines
        point = scatter3(xTmp,yTmp,zTmp,objectSize, 'MarkerFaceColor',  rgbTmp, 'Marker', objectShape , 'MarkerEdgeColor', outlineColor, 'LineWidth', 0.5);
    else
        scatter3(xTmp,yTmp,zTmp,objectSize, 'MarkerFaceColor', rgbTmp , 'Marker', objectShape, 'MarkerEdgeColor', 'none');
    end
end

%Alphashape figure:
if verLessThan('matlab','8.4')
    % -- Code to run in MATLAB R2014a and earlier here --
    msgbox('Sorry, MATLAB R2014b or later required to display alphashapes');
    disp('Sorry, MATLAB R2014b or later required to display alphashapes');
else
    try
        % -- Code to run in MATLAB R2014b and later here --
        shp=dlmread(outlineFile);
        shp(:,1)=shp(:,1) * xy_scale;
        shp(:,2)=shp(:,2) * xy_scale;
        shp = alphaShape(shp, alphaRad);
        h=plot(shp, 'FaceColor', alphaColor,'FaceAlpha',alphaTransparency, 'Linestyle', 'none',...
        'EdgeColor', alphaColor, 'EdgeAlpha', 0.2, 'EdgeLighting', 'gouraud', 'FaceLighting', 'gouraud');

        %Add light from multiple directions
        light('Position',[-1 0 0],'Style','infinite', 'Visible', 'on')
        light('Position',[0 -1 0],'Style','infinite', 'Visible', 'on')
        light('Position',[0 0 -1],'Style','infinite', 'Visible', 'on')
        light('Position',[1 0 -1],'Style','infinite', 'Visible', 'on')
        light('Position',[0 1 -1],'Style','infinite', 'Visible', 'on')
        light('Position',[0 0 1],'Style','infinite', 'Visible', 'on')
    catch exception
        disp('Could not display alphashape...');
    end
end


if showGrid
    grid on;
else
    grid off;
end

axis('tight');
axis equal

%Code below can be used to get/set viewing angle, and print figure to high
%quality file

% [az,el] = view    <-to get current view data
% view(az,el)        <-to set current view data

% print('-dtiff','-r1500','output')
disp('Done!')

