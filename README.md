# Route-Riter
 Route Riter is a set of tools for Microsoft Train Simulator created by Mike Simpson.

> *If you find a bug or find an improvement for the repository/project, please feel free to inquire/request a change. Your collaboration is greatly appreciated.

> *There are Forms and Modules that are unused if you check the folder containing the source, but due to my limited knowledge in MSTS I could not decipher if these are needed or obsolete. Please, if you know something about it I hope for your collaboration.

# Compiling
- Download [Visual Basic 6.0](https://winworldpc.com/product/microsoft-visual-bas/60) or [Visual Studio 6.0](https://winworldpc.com/product/microsoft-visual-stu/60) with its corresponding [Service Pack 6](https://winworldpc.com/download/a4208baa-aaee-11eb-bc5b-0200008a0da4).

 - Download [MSDN Library](https://winworldpc.com/product/msdn/vs-60) (Optional)
   
 - Install VB6 and MSDN Library. If you are using Windows 10 there is a special procedure to install VB6. Use the [Visual Studio 6.0 Installer Wizard](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.27)/VS6InstallerSetup.exe). > How to proceed [(Video-tutorial)](https://www.youtube.com/watch?v=1tkTb6AYlAg)
 - After downloading and install the MSDN library you can install the following update for it:
- **MSDN Latest Version Update**: 
   - **Disc1**: https://archive.org/details/MSDN_Library_October_2001_Disc_1 
   - **Disc2**: https://archive.org/details/MSDN_Library_October_2001_Disc_2 
   - **Disc3**: https://archive.org/details/MSDN_Library_October_2001_Disc_3
 - Install Service Pack 6
 
 - Install [Code Advisor](https://www.microsoft.com/en-US/download/details.aspx?id=1222) (Optional)

 - Install [vb6mousewheelfix](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.27)/Others/vb6mousewheelfix.exe)

 - Install [Microsoft Visual Basic 6.0 Common Controls Update](https://www.microsoft.com/en-US/download/details.aspx?id=10019)

 - Install C1 Controls. If you have problems loading the vsflex8l.ocx vsprint.ocx and c1sizer.ocx controls in visual basic try copying and registering them in both system directories: /system32 and /SysWow64 manually with [cabs files](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.27)/Source/Dependancies/C1%20ActiveX%20Controls) or with [.ocx files](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.27)/Source/Dependancies/ActiveX%20Controls%20and%20DLL/VSFLEX8) . Or better make an installation from the [installers (New Versions).](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.27)/Source/Dependancies/C1%20ActiveX%20Controls/C1ActiveX%20Installers) or [installers (Old Versions).](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.27)/Source/Dependancies/C1%20ActiveX%20Controls).

 - Run Visual Basic 6.0, choose the option to load existing project and choose Route_Riter7.vbp to load the project.
 
 - Make the necessary changes and run with F5 or ctrl + F5 to run with a complete compilation.

 - From Project>Properties>Compile you can choose the compilation mode.

 - From the file menu you can generate an .exe

 - Drop the compiled executable to the Release folder

# After compiling the project 
 - Extract all compressed files from [./Release/Dependancies/Extract...](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.27)/Release/Dependancies/Extract%20to%20release%20folder) directory to the root of the Release folder. 
 Install [mwgfxdll.exe](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.27)/Release/Dependancies/mwgfxdll%20INSTALLER.7z).

# Development plans
 - Improve the current code following the original direction.

# Future development plans
- Prepare the Visual Basic 6.0 code for migration to Visual Basic .NET.

- Separate application and data tiers into a DLL separate from presentation.

- Change the user interface to an inductive user interface, https://msdn.microsoft.com/en-us/library/ms997506.aspx

- Fully parse the Microsoft Train Simulator files by adapting an XML parser, http://pugixml.org/

# Dependancies:
- ComponentOne
- VB6

# Credits:
© Mike Simpson
virtualtrains@tpg.com

© Jeffrey Kraus-Yao
http://www.digital-rails.com
support@digital-rails.com

© Carl-Heinz Rave
TsUtils
http://www.carloshr.de
mail@carloshr.de

© Scott Miller
AceIt
aceit@ameritech.net

© Martin Wright
TGATools2A
http://www.mwgfx.co.uk/index.htm
martin@mwgfx.co.uk

© Paul Gausden
Shape Viewer
https://decapod99.wordpress.com/

© Edward Grubb
PicFormat32
https://github.com/Planet-Source-Code/edward-grubb-ed0-picformat32__1-13267

© Franky Braem
SAWZipNG
http://www.codeproject.com/Articles/875/SAWZip-zip-file-manipulation-control

© Jean-loup Gailly
zlib.dll
http://www.zlib.net/

© ComponentOne
c1sizer.ocx, vsflex8l.ocx and vsprint8.ocx
http://www.componentone.com/

© ClickTeam
ClickTeam Installer Pro
http://www.clickteam.com/

© Uwe Herklotz
UHARC.EXE
Uwe.Herklotz@gmx.de

© Jerry Sulivan
tester
jhsulliv@comcast.net

© Giorgio Brausi
VS6Installer 
http://nuke.vbcorner.net/VS6Installer/tabid/125/language/en-US/Default.aspx

# License:

GNU GPLv3

# Disclaimer
The Route-Riter source code and all software in this repository is provided for educational purposes ONLY. This repository is not affiliated with or endorsed by their respective copyright holders.
