# Microsoft Developer Studio Project File - Name="complex" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=complex - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "complex.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "complex.mak" CFG="complex - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "complex - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "complex - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "complex - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /MD /W3 /WX /GR /GX /O2 /I "../../include" /I "." /I "../../../include" /I "dummyinclude" /D "NDEBUG" /D "_CONSOLE" /FD /c
# SUBTRACT CPP /Fr /YX
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 /nologo /subsystem:console /machine:I386 /out:"client.exe" /libpath:"../../../lib"
# SUBTRACT LINK32 /debug /nodefaultlib

!ELSEIF  "$(CFG)" == "complex - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD CPP /nologo /MDd /W3 /WX /Gm /GR /GX /Zi /Od /I "." /I "../../../include" /I "../../include" /I "dummyinclude" /D "_DEBUG" /D "_CONSOLE" /FD /GZ /c
# SUBTRACT CPP /Fr /YX
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 /nologo /subsystem:console /debug /machine:I386 /out:"client.exe" /pdbtype:sept /libpath:"../../../lib"
# SUBTRACT LINK32 /nodefaultlib

!ENDIF 

# Begin Target

# Name "complex - Win32 Release"
# Name "complex - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\Client.cpp
# End Source File
# Begin Source File

SOURCE=.\Complex.cpp
# End Source File
# Begin Source File

SOURCE=.\ComplexDict.cpp
# End Source File
# Begin Source File

SOURCE=.\Grammar.cpp
# End Source File
# Begin Source File

SOURCE=.\Parser.cpp
# End Source File
# Begin Source File

SOURCE=.\Scanner.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\Grammar.h
# End Source File
# Begin Source File

SOURCE=.\NodeI.h
# End Source File
# Begin Source File

SOURCE=.\Parser.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# Begin Source File

SOURCE=.\Complex.ice

!IF  "$(CFG)" == "complex - Win32 Release"

USERDEP__COMPL="..\..\..\bin\slice2freeze.exe"	"..\..\..\bin\slice2cpp.exe"	"..\..\..\lib\slice.lib"	
# Begin Custom Build
InputPath=.\Complex.ice

BuildCmds= \
	..\..\..\bin\slice2cpp.exe Complex.ice \
	..\..\..\bin\slice2freeze.exe --dict Complex::ComplexDict,Complex::Key,Complex::Node ComplexDict Complex.ice \
	

"Complex.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)

"Complex.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)

"ComplexDict.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)

"ComplexDict.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)
# End Custom Build

!ELSEIF  "$(CFG)" == "complex - Win32 Debug"

USERDEP__COMPL="..\..\..\bin\slice2freeze.exe"	"..\..\..\bin\slice2cpp.exe"	"..\..\..\lib\sliced.lib"	
# Begin Custom Build
InputPath=.\Complex.ice

BuildCmds= \
	..\..\..\bin\slice2cpp.exe Complex.ice \
	..\..\..\bin\slice2freeze.exe --dict Complex::ComplexDict,Complex::Key,Complex::Node ComplexDict Complex.ice \
	

"Complex.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)

"Complex.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)

"ComplexDict.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)

"ComplexDict.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
   $(BuildCmds)
# End Custom Build

!ENDIF 

# End Source File
#xxx## Begin Source File
#xxx#
#xxx#SOURCE=.\Grammar.y
#xxx#
#xxx#!IF  "$(CFG)" == "complex - Win32 Release"
#xxx#
#xxx## Begin Custom Build
#xxx#InputPath=.\Grammar.y
#xxx#
#xxx#BuildCmds= \
#xxx#	bison -dvt Grammar.y \
#xxx#	move Grammar.tab.c Grammar.cpp \
#xxx#	move Grammar.tab.h Grammar.h \
#xxx#	
#xxx#
#xxx#"Grammar.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
#xxx#   $(BuildCmds)
#xxx#
#xxx#"Grammar.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
#xxx#   $(BuildCmds)
#xxx## End Custom Build
#xxx#
#xxx#!ELSEIF  "$(CFG)" == "complex - Win32 Debug"
#xxx#
#xxx## Begin Custom Build
#xxx#InputPath=.\Grammar.y
#xxx#
#xxx#BuildCmds= \
#xxx#	bison -dvt Grammar.y \
#xxx#	move Grammar.tab.c Grammar.cpp \
#xxx#	move Grammar.tab.h Grammar.h \
#xxx#	
#xxx#
#xxx#"Grammar.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
#xxx#   $(BuildCmds)
#xxx#
#xxx#"Grammar.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
#xxx#   $(BuildCmds)
#xxx## End Custom Build
#xxx#
#xxx#!ENDIF 
#xxx#
#xxx## End Source File
#xxx## Begin Source File
#xxx#
#xxx#SOURCE=.\Scanner.l
#xxx#
#xxx#!IF  "$(CFG)" == "complex - Win32 Release"
#xxx#
#xxx## Begin Custom Build
#xxx#InputPath=.\Scanner.l
#xxx#
#xxx#"Scanner.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
#xxx#	flex Scanner.l 
#xxx#	echo #include "IceUtil/Config.h" > Scanner.cpp 
#xxx#	type lex.yy.c >> Scanner.cpp 
#xxx#	
#xxx## End Custom Build
#xxx#
#xxx#!ELSEIF  "$(CFG)" == "complex - Win32 Debug"
#xxx#
#xxx## Begin Custom Build
#xxx#InputPath=.\Scanner.l
#xxx#
#xxx#"Scanner.cpp" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
#xxx#	flex Scanner.l 
#xxx#	echo #include "IceUtil/Config.h" > Scanner.cpp 
#xxx#	type lex.yy.c >> Scanner.cpp 
#xxx#	
#xxx## End Custom Build
#xxx#
#xxx#!ENDIF 
#xxx#
#xxx## End Source File
# End Group
# End Target
# End Project
