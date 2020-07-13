function varargout = Colocalization_Visualization_3D_GUI(varargin)
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @Colocalization_Visualization_3D_GUI_OpeningFcn, ...
                   'gui_OutputFcn',  @Colocalization_Visualization_3D_GUI_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end

function Colocalization_Visualization_3D_GUI_OpeningFcn(hObject, eventdata, handles, varargin)

handles.output = hObject;
handles.ax=-1;

guidata(hObject, handles);

function varargout = Colocalization_Visualization_3D_GUI_OutputFcn(hObject, eventdata, handles) 
varargout{1} = handles.output;

function showGrid_Callback(hObject, eventdata, handles)
if isgraphics(handles.ax)
    if hObject.Value
        set(handles.ax,'XGrid','on');
        set(handles.ax,'YGrid','on');
        set(handles.ax,'ZGrid','on');
    else
        set(handles.ax,'XGrid','off');
        set(handles.ax,'YGrid','off');
        set(handles.ax,'ZGrid','off');
    end
end
guidata(hObject,handles);

function outColor_Callback(hObject, eventdata, handles)
col=uisetcolor('RGB');
hObject.BackgroundColor=col;
guidata(hObject,handles);

function outColor_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function outAlpha_Callback(hObject, eventdata, handles)

function outAlpha_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function trans_Callback(hObject, eventdata, handles)
val=str2double(hObject.String);
if isnan(val)
    val=handles.slider.Value;
elseif val>handles.slider.Max
    val=handles.slider.Max;
elseif val<handles.slider.Min
    val=handles.slider.Min;
end
handles.trans.String=num2str(val);
handles.slider.Value=val;

guidata(hObject, handles);

function trans_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function slider_Callback(hObject, eventdata, handles)
handles.trans.String=num2str(hObject.Value);
guidata(hObject,handles);

function slider_CreateFcn(hObject, eventdata, handles)
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end

function scatSize_Callback(hObject, eventdata, handles)

function scatSize_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function scatShape_Callback(hObject, eventdata, handles)

function scatShape_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function outline_Callback(hObject, eventdata, handles)
if hObject.Value
    handles.outlineColor.Enable='on';
else
    handles.outlineColor.Enable='off';
end
guidata(hObject,handles);

function outlineColor_Callback(hObject, eventdata, handles)
col=uisetcolor('RGB');
hObject.BackgroundColor=col;
guidata(hObject,handles);

function outlineColor_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function colorFile_Callback(hObject, eventdata, handles)

function colorFile_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function colorFileBrowse_Callback(hObject, eventdata, handles)
[pp,~,~]=fileparts(handles.colorFile.String);
[a,b]=uigetfile(fullfile(pp,'*.txt'),'Select the color definition file');
if a~=0
    handles.colorFile.String=fullfile(b,a);
end
guidata(hObject,handles);

function viewButton_Callback(hObject, eventdata, handles)
xy_scale=1.0;
[handles.shape,handles.ax]=drawShape(handles,xy_scale);
f=fopen(handles.colorFile.String,'r');
C=textscan(f,'color%d=[%d, %d, %d]');
fclose(f);
RGB=[C{2},C{3},C{4}];
C=C{1};
a=dlmread(handles.cellFile.String);
x=a(:,1)*xy_scale;
y=a(:,2)*xy_scale;
z=a(:,3);
col=a(:,4);

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
mm=handles.scatShape.String{handles.scatShape.Value};
marker=mm(end-1);

hold(handles.ax,'on');
if handles.outline.Value
    scatter3(handles.ax,x,y,z,str2double(handles.scatSize.String), rgb , marker , 'filled', 'MarkerEdgeColor', handles.outlineColor.BackgroundColor, 'LineWidth', 0.5);
else
    scatter3(handles.ax,x,y,z,str2double(handles.scatSize.String), rgb , marker , 'filled', 'LineWidth', 0.5);
end
hold(handles.ax,'off');
axis(handles.ax,'tight');
guidata(hObject,handles);
showGrid_Callback(handles.showGrid,eventdata,handles);

function [h,ax]=drawShape(handles,xy_scale)
    alphaRad=str2double(handles.outAlpha.String);
    if ~isgraphics(handles.ax)
        figure(1);cla;
        ax=gca;
    else
        axes(handles.ax);cla;
        ax=handles.ax;
    end
    try
        shp=dlmread(handles.outlineFile.String);
        shp(:,1)=shp(:,1)*xy_scale;
        shp(:,2)=shp(:,2)*xy_scale;
        SHP = alphaShape(shp, alphaRad);
        h=plot(SHP, 'FaceColor', handles.outColor.BackgroundColor ,'FaceAlpha',1-handles.slider.Value, 'Linestyle', 'none',...
            'EdgeColor', handles.outColor.BackgroundColor, 'EdgeAlpha', 0.2, 'EdgeLighting', 'gouraud', 'FaceLighting', 'gouraud');
    catch exception
        disp('Could not display alphashape...');
        h = 0;
    end
    light('Position',[-1 0 0],'Style','infinite', 'Visible', 'on')
    light('Position',[0 -1 0],'Style','infinite', 'Visible', 'on')
    light('Position',[0 0 -1],'Style','infinite', 'Visible', 'on')
    light('Position',[1 0 -1],'Style','infinite', 'Visible', 'on')
    light('Position',[0 1 -1],'Style','infinite', 'Visible', 'on')
    light('Position',[0 0 1],'Style','infinite', 'Visible', 'on')


function cellFile_Callback(hObject, eventdata, handles)

function cellFile_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function cellFileBrowse_Callback(hObject, eventdata, handles)
[pp,~,~]=fileparts(handles.cellFile.String);
[a,b]=uigetfile(fullfile(pp,'*.csv'),'Select the cell coordinates file');
if a~=0
    handles.cellFile.String=fullfile(b,a);
end
guidata(hObject,handles);

function outlineFile_Callback(hObject, eventdata, handles)


function outlineFile_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function outlineFileBrowse_Callback(hObject, eventdata, handles)
[pp,~,~]=fileparts(handles.outlineFile.String);
[a,b]=uigetfile(fullfile(pp,'*.csv'),'Select the outer structure coordinates file');
if a~=0
    handles.outlineFile.String=fullfile(b,a);
end
guidata(hObject,handles);

function figure1_CloseRequestFcn(hObject, eventdata, handles)
delete(hObject);
close all;
