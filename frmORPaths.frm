VERSION 5.00
Begin VB.Form frmORPaths 
   Caption         =   "Open Rails Paths"
   ClientHeight    =   3090
   ClientLeft      =   60
   ClientTop       =   450
   ClientWidth     =   5880
   BeginProperty Font 
      Name            =   "Tahoma"
      Size            =   8.25
      Charset         =   0
      Weight          =   400
      Underline       =   0   'False
      Italic          =   0   'False
      Strikethrough   =   0   'False
   EndProperty
   LinkTopic       =   "Form1"
   ScaleHeight     =   3090
   ScaleWidth      =   5880
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  'Windows Default
   Begin VB.CommandButton cmdCancel 
      Caption         =   "Cancel"
      Height          =   360
      Left            =   3720
      TabIndex        =   2
      Top             =   2640
      Width           =   990
   End
   Begin VB.CommandButton cmdSelect 
      Caption         =   "Select"
      Enabled         =   0   'False
      Height          =   360
      Left            =   1320
      TabIndex        =   1
      Top             =   2640
      Width           =   990
   End
   Begin VB.ListBox listPaths 
      Height          =   2400
      Left            =   120
      TabIndex        =   0
      Top             =   120
      Width           =   5655
   End
End
Attribute VB_Name = "frmORPaths"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Type FILETIME
    intLow As Long
    intHigh As Long
End Type

Const REG_DWORD = 4
Const REG_SZ = 1
Const HKEY_CURRENT_USER = &H80000001
Const ERROR_SUCCESS = 0&
Const STANDARD_RIGHTS_READ = &H20000
Const KEY_QUERY_VALUE = &H1
Const KEY_ENUMERATE_SUB_KEYS = &H8
Const KEY_NOTIFY = &H10
Const KEY_READ = ((STANDARD_RIGHTS_READ Or _
                  KEY_QUERY_VALUE Or _
                  KEY_ENUMERATE_SUB_KEYS Or _
                  KEY_NOTIFY) And _
                  (Not SYNCHRONIZE))

Private Declare Function RegOpenKeyEx Lib "advapi32.dll" _
    Alias "RegOpenKeyExA" _
    (ByVal hKey As Long, _
    ByVal lpSubKey As String, _
    ByVal ulOptions As Long, _
    ByVal samDesired As Long, phkResult As Long) As Long
    
Private Declare Function RegEnumKeyEx Lib "advapi32.dll" _
    Alias "RegEnumKeyExA" _
    (ByVal hKey As Long, _
    ByVal dwIndex As Long, _
    ByVal lpName As String, _
    lpcbName As Long, _
    ByVal lpReserved As Long, _
    ByVal lpClass As String, _
    lpcbClass As Long, _
    lpftLastWriteTime As FILETIME) As Long
    
Private Declare Function RegEnumValue Lib "advapi32.dll" _
    Alias "RegEnumValueA" _
    (ByVal hKey As Long, _
    ByVal dwIndex As Long, _
    ByVal lpValueName As String, _
    lpcbValueName As Long, _
    ByVal lpReserved As Long, _
    lpType As Long, _
    lpData As Any, _
    lpcbData As Long) As Long
                   
Private Declare Function RegCloseKey Lib "advapi32.dll" _
    (ByVal hKey As Long) As Long

Private Sub cmdCancel_Click()
    Unload Me
End Sub

Private Sub cmdSelect_Click()
    MSTSPath = listPaths.List(listPaths.ListIndex)
    Unload Me
End Sub

Private Sub Form_Load()
    Dim sORPath As String
    Dim lngResult As Long
    Dim lngKeyHandle As Long
    Dim lngCurIdx As Long
    Dim strValue As String
    Dim lngValueLen As Long
    Dim strData As String
    Dim lngDataLen As Long
    
    sORPath = "Software\OpenRails\ORTS\Folders"
    lngResult = RegOpenKeyEx(HKEY_CURRENT_USER, sORPath, 0&, KEY_READ, lngKeyHandle)
    If lngResult <> ERROR_SUCCESS Then
        cmdSelect.Enabled = False
    Else
        cmdSelect.Enabled = True
        lngCurIdx = 0
        Do
            lngValueLen = 2000
            strValue = String(lngValueLen, 0)
            lngDataLen = 2000
            strData = String(lngDataLen, 0)
            lngResult = RegEnumValue(lngKeyHandle, _
                lngCurIdx, _
                ByVal strValue, _
                lngValueLen, _
                0&, _
                REG_SZ, _
                ByVal strData, _
                lngDataLen)
            listPaths.AddItem (Trim$(strData))
            lngCurIdx = lngCurIdx + 1
        Loop While lngResult = ERROR_SUCCESS
    End If
    lngResult = RegCloseKey(lngKeyHandle)
    'listPaths.AddItem ("C:\Program Files\Microsoft Games\Train Simulator")
End Sub

Private Sub listPaths_Click()
    cmdSelect.Enabled = True
End Sub
