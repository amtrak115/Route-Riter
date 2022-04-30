VERSION 5.00
Begin VB.Form frmAbout 
   BackColor       =   &H80000005&
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "About MyApp"
   ClientHeight    =   5490
   ClientLeft      =   2340
   ClientTop       =   1935
   ClientWidth     =   6225
   ClipControls    =   0   'False
   BeginProperty Font 
      Name            =   "MS Sans Serif"
      Size            =   8.25
      Charset         =   0
      Weight          =   700
      Underline       =   0   'False
      Italic          =   0   'False
      Strikethrough   =   0   'False
   EndProperty
   Icon            =   "frmAbout.frx":0000
   LinkTopic       =   "Form2"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   3789.296
   ScaleMode       =   0  'User
   ScaleWidth      =   5845.598
   ShowInTaskbar   =   0   'False
   StartUpPosition =   2  'CenterScreen
   Begin VB.PictureBox picIcon 
      AutoSize        =   -1  'True
      ClipControls    =   0   'False
      BeginProperty Font 
         Name            =   "MS Sans Serif"
         Size            =   8.25
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   1140
      Left            =   240
      Picture         =   "frmAbout.frx":406A
      ScaleHeight     =   758.52
      ScaleMode       =   0  'User
      ScaleWidth      =   758.52
      TabIndex        =   0
      Top             =   240
      Width           =   1140
   End
   Begin VB.CommandButton cmdOK 
      Cancel          =   -1  'True
      Caption         =   "OK"
      Default         =   -1  'True
      Height          =   345
      Left            =   5160
      MaskColor       =   &H00404000&
      Style           =   1  'Graphical
      TabIndex        =   1
      Top             =   3720
      UseMaskColor    =   -1  'True
      Width           =   900
   End
   Begin VB.Line Line1 
      BorderColor     =   &H00404000&
      Index           =   2
      X1              =   0
      X2              =   5662.483
      Y1              =   0
      Y2              =   0
   End
   Begin VB.Label Label2 
      BackStyle       =   0  'Transparent
      Caption         =   "Updated by Jeffrey Kraus-Yao, Madison, Wisconsin"
      BeginProperty Font 
         Name            =   "MS Sans Serif"
         Size            =   8.25
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   255
      Left            =   1560
      TabIndex        =   7
      Top             =   1680
      Width           =   3735
   End
   Begin VB.Label Label1 
      BackStyle       =   0  'Transparent
      Caption         =   "Created by Mike Simpson, Penrith, NSW, Australia"
      BeginProperty Font 
         Name            =   "MS Sans Serif"
         Size            =   8.25
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   255
      Left            =   1560
      TabIndex        =   6
      Top             =   1440
      Width           =   4095
   End
   Begin VB.Line Line1 
      BorderColor     =   &H00404000&
      Index           =   1
      X1              =   0
      X2              =   5746.998
      Y1              =   1687.583
      Y2              =   1687.583
   End
   Begin VB.Label lblDescription 
      BackStyle       =   0  'Transparent
      Caption         =   "A suite of utilities for Microsoft Train Simulator"
      BeginProperty Font 
         Name            =   "MS Sans Serif"
         Size            =   8.25
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      ForeColor       =   &H00000000&
      Height          =   195
      Left            =   1560
      TabIndex        =   2
      Top             =   1200
      Width           =   3960
   End
   Begin VB.Label lblTitle 
      BackStyle       =   0  'Transparent
      Caption         =   "Application Title"
      BeginProperty Font 
         Name            =   "MS Serif"
         Size            =   13.5
         Charset         =   0
         Weight          =   700
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      ForeColor       =   &H80000007&
      Height          =   480
      Left            =   1560
      TabIndex        =   4
      Top             =   240
      Width           =   4485
   End
   Begin VB.Label lblVersion 
      BackStyle       =   0  'Transparent
      Caption         =   "Version"
      BeginProperty Font 
         Name            =   "MS Sans Serif"
         Size            =   9.75
         Charset         =   0
         Weight          =   700
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      ForeColor       =   &H80000008&
      Height          =   225
      Left            =   1560
      TabIndex        =   5
      Top             =   840
      Width           =   4125
   End
   Begin VB.Label lblDisclaimer 
      BackStyle       =   0  'Transparent
      Caption         =   "Copyright 2002-2013 © Mike Simpson"
      BeginProperty Font 
         Name            =   "MS Sans Serif"
         Size            =   8.25
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      ForeColor       =   &H80000008&
      Height          =   795
      Left            =   240
      TabIndex        =   3
      Top             =   2760
      Width           =   4485
      WordWrap        =   -1  'True
   End
End
Attribute VB_Name = "frmAbout"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Public registered As Boolean

Private Sub cmdOK_Click()
    frmUtils.Show
    Unload Me
End Sub

Private Sub Form_Load()
    Me.Caption = "About " & App.Title
    lblTitle.Caption = App.Title
    lblVersion.Caption = "Version " & App.Major & "." & App.Minor & "." & App.Revision
    lblDisclaimer.Caption = "Copyright © 2002-2013 Mike Simpson" & vbCrLf
    lblDisclaimer.Caption = lblDisclaimer.Caption & vbCrLf & "Copyright © 2015 Digital Rails Corporation"
    If Command <> vbNullString Then cmdOK.value = True
End Sub


