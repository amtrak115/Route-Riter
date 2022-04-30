@Echo OFF
Rem Initialization of the TSUtil-Installation
REM Set TSutil-Pfad and path of Train-Simulator
Rem
if X%1==X goto .path
REM Copy path to current TSUtil-Installion to environment-variable 'TSUtil'
Java -cp . CpyReg "*" %1 "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" TSUtil
if %ERRORLEVEL%==0 goto .path
Echo Program 'CpyReg'(1) ended with error-marker! (%errorlevel%)
pause
:.path
REM Copy path to current Trainsimulator-directory to environment-variable 'TrSimPath'
Java -cp . CpyReg "HKLM\SOFTWARE\Microsoft\Microsoft Games\Train Simulator\1.0" Path^
 "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" TrSimPath
if %ERRORLEVEL%==0 goto .ende
Echo Program 'CpyReg'(2) ended with error-marker! (%errorlevel%)
pause
:.ende
