# Route-Riter
 Route Riter is a set of tools for Microsoft Train Simulator created by Mike Simpson.

> *If you find a bug or find an improvement for the repository/project, please feel free to inquire/request a change. Your collaboration is greatly appreciated.

> *There are Forms and Modules that are unused if you check the folder containing the source, but due to my limited knowledge in MSTS I could not decipher if these are needed or obsolete. Please, if you know something about it I hope for your collaboration.

## *This repository is under reconstruction.*

# Pre-requisites:
- Download [Visual Basic 6.0](https://winworldpc.com/product/microsoft-visual-bas/60) or [Visual Studio 6.0](https://winworldpc.com/product/microsoft-visual-stu/60)

 - Download [MSDN Library](https://winworldpc.com/product/msdn/vs-60) (Optional)
   
    ***I assume you are running Windows 10 64 bit***
 
- Install Visual Studio 6 and MSDN Library.

*There is a special procedure to install Visual Studio 6 in Windows 10. Use the* [Visual Studio 6 Installer Wizard v5.0](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.26)/VS6InstallerSetup.exe)

**How to proceed**
- [(Video-tutorial)](https://www.youtube.com/watch?v=1tkTb6AYlAg)
- [Manual installation tutorial](https://www.codeproject.com/Articles/1191047/Install-Visual-Studio-on-Windows)

**After downloading and install the MSDN library you can install the following update for it:
- **MSDN Latest Version Update**: 
   - **Disc1**: https://archive.org/details/MSDN_Library_October_2001_Disc_1 
   - **Disc2**: https://archive.org/details/MSDN_Library_October_2001_Disc_2 
   - **Disc3**: https://archive.org/details/MSDN_Library_October_2001_Disc_3
 - Install [Service Pack 6](https://web.archive.org/web/20120707200906/http://download.microsoft.com/download/1/9/f/19fe4660-5792-4683-99e0-8d48c22eed74/Vs6sp6.exe)
 
 - Install [Microsoft Visual Basic 6.0 Service Pack 6 Cumulative Update](https://www.microsoft.com/en-us/download/details.aspx?id=7030)
 
 - Install [Microsoft Visual Basic 6.0 Service Pack 6 Security Rollup Update](https://www.microsoft.com/en-us/download/details.aspx?id=50722)
   
 - Install C1 Controls manually with [cabs files](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.26)/Source/Dependancies/ComponentOne%20Installers) or with [.ocx files](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.26)/Source/Dependancies/ComponentOne%20.ocx), or better make an installation from the [installers.](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.26)/Source/Dependancies/ComponentOne%20Installers)
 
If you have problems loading the vsflex8l.ocx vsprint.ocx and c1sizer.ocx controls in visual basic try copying and registering them in both system directories: /system32 and /SysWOW64 

## Recommended/Interest addins/tools for VB6 IDE:

 - [Code Advisor](https://www.microsoft.com/en-US/download/details.aspx?id=1222) (Optional)

 - [Visual Basic 6 Mouse Wheel Fix](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.27)/Others/vb6mousewheelfix.exe)

 - [MZ-Tools 8.0](https://www.mztools.com/v8/mztools8.aspx) (Recommended)

 - [CodeSMART](https://www.axtools.com/products-codesmart-vb6.php) (Recommended)

 - [Codejock Suite Pro for ActiveX](https://codejock.com/products/suitepro/?2yn6s14z=p1z) (Recommended)

 - [ModernVB](https://github.com/VykosX/ModernVB) (Recommended)

 - [OLEEXP : Modern Shell Interfaces](https://www.vbforums.com/showthread.php?786079-VB6-Modern-Shell-Interface-Type-Library-oleexp-tlb)

# Run compiling
 - Run Visual Basic 6.0, choose the option to load existing project and choose Route_Riter7.vbp to load the project.
 
 - Make the necessary changes and run with F5 or ctrl + F5 to run with a complete compilation.

 - From Project>Properties>Compile you can choose the compilation mode.

 - From the file menu you can generate an .exe

 - Drop the compiled executable to the Release folder

# After compiling the project 
 - Extract all compressed files from [./Release/Dependancies](https://github.com/FrannDzs/Route-Riter/tree/main-(7.6.26)/Release/Dependancies) directory to the root of the Release folder. 
 Install [mwgfxdll.exe.](https://github.com/FrannDzs/Route-Riter/blob/main-(7.6.26)/Release/mwgfxdll.exe)

# Development plans
 - Improve the current code following the original direction.

# Future development plans
- Prepare the Visual Basic 6.0 code for migration to Visual Basic .NET.

- Separate application and data tiers into a DLL separate from presentation.

- Change the user interface to an inductive user interface, https://msdn.microsoft.com/en-us/library/ms997506.aspx

- Fully parse the Microsoft Train Simulator files by adapting an XML parser, http://pugixml.org/

# Credits:
© Mike Simpson
virtualtrains@tpg.com

© Jeffrey Kraus
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

© UPX Packer
upx.exe
https://upx.github.io/

© Okrasa Ghia
FCalc
okrasaghia@yahoo.com

And much more.

# License:

GNU GPLv3

# Disclaimer
The Route-Riter source code and all software in this repository is provided for educational purposes ONLY. This repository is not affiliated with or endorsed by their respective copyright holders.
