# Route-Riter
 Route Riter is a set of tools for Microsoft Train Simulator created by Mike Simpson.

> *If you find a bug or find an improvement for the repository/project, please feel free to inquire/request a change. Your collaboration is greatly appreciated.

# Compiling
- If you are using Windows 10 there is a special procedure to install VB6. Use the [Visual Studio 6.0 Installer Wizard](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.27)/VS6InstallerSetup.exe). > How to proceed [(Video-tutorial)](https://www.youtube.com/watch?v=1tkTb6AYlAg)

- Install [Visual Basic 6.0](https://winworldpc.com/product/microsoft-visual-bas/60) or [Visual Studio 6.0](https://winworldpc.com/product/microsoft-visual-stu/60) with its corresponding [Service Pack 6](https://winworldpc.com/download/a4208baa-aaee-11eb-bc5b-0200008a0da4).

 - Install [MSDN Library](https://winworldpc.com/product/msdn/vs-60) (Optional) 
   MSDN Latest Version: 
   Disc1: https://archive.org/details/MSDN_Library_October_2001_Disc_1 
   Disc2: https://archive.org/details/MSDN_Library_October_2001_Disc_2 
   Disc3: https://archive.org/details/MSDN_Library_October_2001_Disc_3
 
 - Install [Code Advisor](https://www.microsoft.com/en-US/download/details.aspx?id=1222) (Optional)

 - Install [vb6mousewheelfix](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.27)/Others/vb6mousewheelfix.exe)

 - Install [Microsoft Visual Basic 6.0 Common Controls Update](https://www.microsoft.com/en-US/download/details.aspx?id=10019)

 - Install Source Dependancies from [./Source/Dependancies](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.27)/Source/Dependancies)

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
