Attribute VB_Name = "Module4"
'### Declarations - place this part in the Declarations section of a code file (at the very top of the code)
Public Enum eSpecialFolders
  SpecialFolder_AppData = &H1A        'for the current Windows user, on any computer on the network [Windows 98 or later]
  SpecialFolder_CommonAppData = &H23  'for all Windows users on this computer [Windows 2000 or later]
  SpecialFolder_LocalAppData = &H1C   'for the current Windows user, on this computer only [Windows 2000 or later]
  SpecialFolder_Documents = &H5       'the Documents folder for the current Windows user
End Enum

  '### The routine - place this part before/after any other rotuine
Public Function SpecialFolder(pFolder As eSpecialFolders) As String
'Returns the path to the specified special folder (AppData etc)

Dim objShell  As Object
Dim objFolder As Object

  Set objShell = CreateObject("Shell.Application")
  Set objFolder = objShell.namespace(CLng(pFolder))

  If (Not objFolder Is Nothing) Then SpecialFolder = objFolder.Self.Path

  Set objFolder = Nothing
  Set objShell = Nothing

  If SpecialFolder = "" Then Err.Raise 513, "SpecialFolder", "The folder path could not be detected"

End Function
