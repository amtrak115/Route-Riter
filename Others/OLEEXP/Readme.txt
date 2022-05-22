https://www.vbforums.com/showthread.php?786079-VB6-Modern-Shell-Interface-Type-Library-oleexp-tlb#dloleexp

# About Project

oleexp.tlb is a type library containing a vast collection of Windows shell interfaces and interfaces for a number of related features, forking an older project in order to bring in the expansive set of new interfaces introduced in Windows Vista, and including the smaller number introduced in Windows 7 through 10. It also includes virtually all structures, types, and enums used by these interfaces, and a limited set of related APIs. While some of the interfaces may be present in system libraries, these, and the original versions for everything from the SDK, often use variable types that are incompatible with VB5/6; all interfaces have been reviewed and modified to use variable types that ensure compatibility with VB5 and VB6.

# History
oleexp is based on Edanmo's olelib, forked and expanded. It's a very strong foundation for any desktop application.
Back in the day, E. Morcillo released the very comprehensive interface library olelib.tlb (Edanmo's OLE interfaces & functions). It contained a massive number of interfaces, enums, structs, etc. But after a point it was no longer updated and thus doesn't have any interfaces from Windows Vista or Windows 7. So I set out to bring these interfaces to VB, and quickly realized that so much would have to be duplicated and would then be conflicting, that the only sensible way to approach this would be to fork and expand olelib, particularly since anyone using oleexp would almost certainly be using olelib.

If an interface isn't included that you would like to use, or you find a bug, let me know in this thread, or through a private message or e-mail (fafalone at gmail).

This project is completely free to use and modify as you see fit, for any purpose, including commercial. All that's requested is an acknowledgement and if you're distributing a modified version publicly, to change the project GUID to avoid conflicts, found in oleexp.odl.

# Requirements and Installation / Setup

## Requirements
oleexp can be used with any version of Windows. If you attempt to call an interface/API that is not present on the current OS version, an error will occur, but the presence of those definitions in the library do not effect the usage of any other interfaces/APIs.
No other files are required. You do not need to add oleexpimp or mimelib unless you specifically want to use them. All of the addon modules are optional, but mIID is strongly recommended; it saves a ton of time by allowing direct IID_ / FOLDERID_ / etc usage without having to convert a string to a GUID.

## Installing oleexp
oleexp.tlb (and oleexmpimp.tlb/mimelib.tlb if you're using them) should be placed in a permanent, common folder all your projects can access-- typically SysWow64 (or System32 on a 32-bit Windows install). You can register it manually, but I've never had trouble with the registration VB6 performs when you add it for the first time.
IMPORTANT: oleexp is a common file, and you should only have one copy installed. Do not place multiple copies in the folders of projects that use it. Also, it's a direct replacement for olelib; a project should not have both.

The add-on modules are updated too, so those and any other files you're keeping, should go in a common directory for your VB projects, not the system folder. E.g. all the sample projects point at ..\tl_ole by default, so if you keep your projects in C:\vb\Prj1 Prj2 etc, a good spot would be C:\vb\tl_ole.

## Setting up oleexp in your project
oleexp.tlb is added via Project->References. As with any typelib, a sample project may reference a different location, if so, update the location if the References window says it's 'MISSING:'
NOTE: Like all TLBs, oleexp is a dependency only for the IDE. Once your project is compiled, the TLB is no longer used. It does not need to be present on end user machines. See the 'File Size' section in post #2 for additional details.

## Upgrading from olelib
If you're upgrading an existing project that uses the original olelib, the vast majority of those interfaces have not been modified, but a few have had minor changes to use different variable types. Explicit declares can be changed via Replace; just replace "As olelib.vartype" (or oleexp3.vartype if upgrading from oleexp 3.x) with "As oleexp.vartype". If you get an error or something has stopped working, check the variable type and if it's ByVal or ByRef.
