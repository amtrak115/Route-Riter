echo off
if exist ..\..\train.exe goto start 
goto error

:start
call xcopy ..\..\TEMPLATE\Envfiles\*.* .\Envfiles\*.* /y
call xcopy ..\..\TEMPLATE\Envfiles\Textures\*.* .\Envfiles\Textures\*.* /y
call xcopy ..\..\TEMPLATE\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\..\TEMPLATE\Sound\*.* .\Sound\*.* /y
call xcopy ..\..\TEMPLATE\Textures\*.* .\Textures\*.* /s /y
call xcopy ..\..\TEMPLATE\Terrtex\*.* .\Terrtex\*.* /s /y

rem edit here down, first 3 lines in each group
rem europe1**************************************************

call xcopy ..\europe1\Envfiles\*.* .\Envfiles\*.* /s /y
call xcopy ..\europe1\Sound\*.* .\Sound\*.* /y 
call xcopy ..\europe1\TerrTex\*.* .\Terrtex\*.* /s /y

call xcopy ..\europe1\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\europe1\Textures\*.* .\Textures\*.* /s /y

rem europe2*************************************************

call xcopy ..\europe2\Envfiles\*.* .\Envfiles\*.* /s /y
call xcopy ..\europe2\Sound\*.* .\Sound\*.* /y
call xcopy ..\europe2\TerrTex\*.* .\Terrtex\*.* /s /y

call xcopy ..\europe2\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\europe2\Textures\*.* .\Textures\*.* /s /y

rem japan1*************************************************

call xcopy ..\japan1\Envfiles\*.* .\Envfiles\*.* /s /y
call xcopy ..\japan1\Sound\*.* .\Sound\*.* /y
call xcopy ..\japan1\TerrTex\*.* .\Terrtex\*.* /s /y

call xcopy ..\japan1\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\japan1\Textures\*.* .\Textures\*.* /s /y

rem japan2*************************************************

call xcopy ..\japan2\Envfiles\*.* .\Envfiles\*.* /s /y
call xcopy ..\japan2\Sound\*.* .\Sound\*.* /y
call xcopy ..\japan2\TerrTex\*.* .\Terrtex\*.* /s /y

call xcopy ..\japan2\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\japan2\Textures\*.* .\Textures\*.* /s /y

rem usa1*************************************************

call xcopy ..\usa1\Envfiles\*.* .\Envfiles\*.* /s /y
call xcopy ..\usa1\Sound\*.* .\Sound\*.* /y
call xcopy ..\usa1\TerrTex\*.* .\Terrtex\*.* /s /y

call xcopy ..\usa1\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\usa1\Textures\*.* .\Textures\*.* /s /y

rem usa2*************************************************

call xcopy ..\usa2\Envfiles\*.* .\Envfiles\*.* /s /y
call xcopy ..\usa2\Sound\*.* .\Sound\*.* /y
call xcopy ..\usa2\TerrTex\*.* .\Terrtex\*.* /s /y

call xcopy ..\usa2\Shapes\*.* .\Shapes\*.* /y
call xcopy ..\usa2\Textures\*.* .\Textures\*.* /s /y



rem last shape and textures copied above
rem *****************************************************
rem Bottom of editable entries. The following lines are to make
rem the missing objects work.

call xcopy .\stuffed\root\*.* .\*.* /y
call xcopy .\stuffed\*.* .\Shapes\*.* /y
call xcopy ..\..\global\shapes\us1deer.* .\Shapes\*.* /y
call xcopy ..\..\global\shapes\yardmarker.* .\Shapes\*.* /y
call xcopy ..\..\global\shapes\workman.* .\Shapes\*.* /y
call xcopy ..\..\global\shapes\platform.* .\Shapes\*.* /y
call xcopy ..\..\global\shapes\marker*.* .\Shapes\*.* /y
call xcopy ..\..\global\textures\*.* .\textures\*.* /s /y

goto success

:error 
echo  Your zip extractor unzipped this file into the wrong directory.
echo *
echo 1. Close this window.
echo *
echo 2. Open windows explorer
echo *
echo 3. Click tools, find, files or folders named My_Route
echo *
echo 4. Single click on the folder named My_Route, click on copy
echo *
echo 5. Scroll down the left window to find where your 
echo *  \Train Simulator\Routes\ folder is
echo *
echo 6. Paste the My_Route folder into the \Routes folder.
echo *
echo 7. Click on My_Route 
echo *
echo 8. double click on Ezstuff4.bat
goto end




:success
echo *
echo *
echo *
echo *
echo ALL FILES COPIED, Click on X in upper right corner of window to exit.
echo *
echo *
echo *
echo *
echo *
echo *
echo *
echo Original file by Jim "Sniper" Ward.
echo *
echo *
echo From the days of DOS and dinosaurs.
echo *
echo *
:end
echo Click on the X in the upper right to exit.

