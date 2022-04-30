/*======================================================================
 * Class TSFile -- Compress, decompress, reduce or expand of a file
 *  Reduce     - change normal format to tokenized version
 *  Expand     - change tokenized format to normal version
 *  Compress   - use zlib-algorithm to compress tokenized format
 *  Uncompress - uncompress data to tokenized format
 *   Uncompress/Expand is done step by step while reading data
 *   Reduce is done step by step while writing data
 *     (Write data to file is executed before file is closed)
 *   Compress is executed before file is closed
 * ##TSFile (Eyecatcher)
 =====================================================================*/
class TSFile { /* Decompress,Compress, Expand or Reduce of files */
 /*-------------------------------------------------------------------*/
  private static final DecimalFormatSymbols fFS =  /* Format-object */
    new DecimalFormatSymbols(Locale.US); /* for float-Formats */
  private static final DecimalFormat[] fF = { /* Format-Objects */
    new DecimalFormat("####0.#;-####0.#"              ,fFS),
    new DecimalFormat("####0.##;-####0.##"            ,fFS),
    new DecimalFormat("####0.###;-####0.###"          ,fFS),
    new DecimalFormat("####0.####;-####0.####"        ,fFS),
    new DecimalFormat("####0.#####;-####0.#####"      ,fFS),
    new DecimalFormat("####0.######;-####0.######"    ,fFS),
    new DecimalFormat("####0.#######;-####0.#######"  ,fFS),
    new DecimalFormat("####0.########;-####0.########",fFS)};
 /*-------------------------------------------------------------------*/
  protected static final int    K_none          = 0;  /* Token-Kng. */
  protected static final int    K_uint          = 1;  /* Token-Kng. */
  protected static final int    K_sint          = 2;  /* Token-Kng. */
  protected static final int    K_hex           = 3;  /* Token-Kng. */
  protected static final int    K_float         = 4;  /* Token-Kng. */
  /* ATTENTION: 1..4 has to stay in this manner (UINTUINT) */
  protected static final int    K_uint4float    = 5;  /* Token-Kng. */
  protected static final int    K_sintfloat     = 6;  /* Token-Kng. */
  protected static final int    K_string        = 7;  /* Token-Kng. */
  protected static final int    K_uintfloat     = 9;  /* Token-Kng. */
  protected static final int    K_hexuint       = 10; /* Token-Kng. */
  protected static final int    K_tokuintfloat  = 11; /* Token-Kng. */
  protected static final int    K_uintuint      = 12; /* Token-Kng. */
  protected static final int    K_uintfloathex  = 13; /* Token-Kng. */
  protected static final int    K_hexfloatuint  = 14; /* Token-Kng. */
  protected static final int    K_uintfloat6    = 15; /* Token-Kng. */
  protected static final int    K_mixed1        = 16; /* Token-Kng. */
  protected static final int    K_uintplus      = 17; /* Token-Kng. */
  protected static final int    K_uintplusfloat = 18; /* Token-Kng. */
  protected static final int    K_mixed3        = 19; /* Token-Kng. */
  protected static final int    K_mixed4        = 20; /* Token-Kng. */
  protected static final int    K_mixed2        = 22; /* Token-Kng. */
  protected static final int    K_tokfloat      = 23; /* Token-Kng. */
  protected static final int    K_stringuint    = 24; /* Token-Kng. */
  protected static final int    K_hexfloat      = 25; /* Token-Kng. */
  protected static final int    K_buffer        = 26; /* Token-Kng. */
  protected static final int    K_floats        = 27; /* Token-Kng. */
  protected static final int    K_bLen = 4096; /* buffer-length */
  protected static final int    K_bInc = 64;   /* Inc-level */
 /*-------------------------------------------------------------------*/
  protected static TSFleTok     tok;    /* static temporary */
 /*-------------------------------------------------------------------*/
  private File                  Fle;    /* current used file-def. */
  private int                   cMde;   /* last used I/O-Mode (0..3) */
 /*-------------------------------------------------------------------*/
  /* Variables, which must be available independend from methods: */
  private BufferedReader        Rdr;    /* Reader uncompressed */
  private BufferedInputStream   IS;     /* Reader compressed */
  private BufferedWriter        Wtr;    /* Writer uncompressed */
  private FileOutputStream      OS;     /* Writer compressed */
  private int                   mode;   /* Accessmode */
  private byte[]                ibuf;   /* global temp-buffer */
  private String                FNme;   /* Filename (fully-q) */
  private String                EMsg;   /* Errormessage or 'null' */
  private int                   Ix,Ox;  /* Bufferpointer */
  private String[]              DtaL;   /* Linebuffer */
  private int                   Lvl;    /* Iterationslevel */
  private int                   cCmd;   /* curr. Tokencommand */
  private int[]                 CmdT;   /* Memory Tokencommands */
  private int                   Pos;    /* Characterposition (syn) */
  private int                   mPos;   /* Characterposition (end) */
  private int[]                 zPos;   /* Targetpositions of levels */
  private TSFleTok              cTok;   /* current Tokendefinition */
  private TSFleTok[]            TokT;   /* Memory of Tokendefinitions */
  private int[]                 nLFT;   /* Memory of newLine's */
  private byte[]                cB;     /* Output-Buffer for reduce */
  private int                   w1,w2;  /* temp. values (reduce) */
  private boolean               nLF1,nLF2; /* current newLine-status */
 /*=====================================================================
  * Constructor: (1) Create Default-Entry
  ====================================================================*/
  protected TSFile() /* Create File-control (Dummy) */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    /* Initialize objects */
    Rdr  = null; /* Reader-object is (still) not used */
    IS   = null; /* Reader-object is (still) not used */
    Wtr  = null; /* Writer-object is (still) not used */
    OS   = null; /* Writer-object is (still) not used */
    mode = 0;    /* Init-Accessmode (File is closed) */
    EMsg = null; /* no error occurred */
    /* allocate variables */
    ibuf = new byte[16]; /* allocate buffer for Compares, I/O, ...*/
    DtaL = null; /* line buffer is still NOT allocated */
    Ix = Ox = 0; /* initialize Bufferpointer */
    Pos  = mPos = -1; /* no input available */
    zPos = new int[TSUtil.Tab.length()]; /* create Iterationsbuffer */
    Lvl  = -1;   /* no active iteration */
    cCmd = -1;   /* Meaning: load first token */
    CmdT = new int[TSUtil.Tab.length()]; /* create Iterations-buffer */
    cTok = null;  /* Tokendefinition is still not used */
    TokT = new TSFleTok[TSUtil.Tab.length()]; /* Iterations-memory */
    nLFT = new int[TSUtil.Tab.length()]; /* Iterations-memory */
    cB   = null; /* buffer is still not allocated */
    w1 = w2 = 0; /* init */
    cMde = -1; /* File was not opened till now */
  }

 /*=====================================================================
  * Constructor: (2) Create Standard-Entry
  ====================================================================*/
  protected TSFile( /* Create File-control */
   File dat, /* File-Definition */
   int  bl)  /* line-count of line-buffer */
   {
    this(); /* set Default-Values (--- Restriction ---) */
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    Fle = dat; /* store File-Definition */
    FNme = Fle.getAbsolutePath(); /* store fully qualified Filename */
    DtaL = new String[bl]; /* create Line-buffer */
  }

 /*=====================================================================
  * Method "addPool"
  * Add Dataline(String) to Input-/Output-Pool
  ====================================================================*/
  private boolean addPool( /* add line to pool */
   String zle) /* line to be added to pool */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(zle.endsWith(" ")) zle = zle.substring(0,zle.length()-1);
    DtaL[Ix++] = zle; /* store string and increment pointer */
    if(Ix==DtaL.length) Ix = 0; /* Wrap-around */
    if(Ix!=Ox) return(false); /* no overflow -- OK */
    EMsg = TSLogg.getMsg("TSFle101",Ix); /* create error-message */
    return(true); /* Overflow in I/O-Pool -- signal error */
  }

 /*=====================================================================
  * Method "chkFKng" (Dummy for extended classes)
  ====================================================================*/
  protected String chkFKng(String s,boolean t) { return(s); }

 /*=====================================================================
  * Method "close"
  * Close reader/writer again
  * Method returns error-message or 'null'
  ====================================================================*/
  protected String close() /* Close Reader/Writer again */
   {
   /*-----------------------------------------------------------------*/
    BufferedOutputStream W; /* Writer for data */
    String               is;
    byte[]               oBuf;
    int                  i;
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    if(Fle==null) return(TSLogg.getKey("TSFleC01")); /* no active file */

    switch(mode) { /* close file according to current mode */
      case(-1): /* uncompressed input is active */
        try { Rdr.close(); /* close input-file */
        } catch (IOException e) { System.err.println(e.toString()); }
        break;
      case(-2): /* compressed input is active */
        try { IS.close(); /* close input-file */
        } catch (IOException e) { System.err.println(e.toString()); }
        break;
      case( 0): break; /* ignore command if already close */
      case( 1): /* uncompressed/unreduced output is active (UT) */
      case( 3): /* compressed/unreduced output is active (CT) */
        try { Wtr.close(); /* close output-file */
        } catch (IOException e) { System.err.println(e.toString()); }
        break;
      case( 2): /* compressed/reduced output is active (CB) */
      case( 4): /* reduced output is active (UB) */

// TSUtil.lg.Log("Msg010","("+Lvl+")-->"+"buffer='"+(new String(cB,0,Pos))+"'");

        oBuf = "@@@@@@@@".getBytes(); /* set second part of file-ID */
        i = Pos; /* save length of data ('Pos' may be invalidated) */

        if(mode>2) { /* output is ONLY 'reduced' */
          try {OS.write(oBuf); /* write file-identifier */
          } catch (IOException e) { /* output-error occurred */
            System.err.println(e.toString());
            try {OS.close();} catch (IOException E) { }
            mode = 0; /* mark file as closed (mode) */
            return(TSLogg.getKey("TSFleC02")); /* return error-message */
          } /* Error encountered */
          W = new BufferedOutputStream(OS);
            /* use buffered output and DO NOT compress */
        } else { /* output must be compressed, too */
          putInt(Pos+16,oBuf,0,4); /* set length of whole data */
            /* 'Pos' is now incremented and therefore 'invalid' */
          try {OS.write(oBuf); /* write file-identifier */
          } catch (IOException e) { /* output-error occurred */
            System.err.println(e.toString());
            try {OS.close();} catch (IOException E) { }
            mode = 0; /* mark file as closed (mode) */
            return(TSLogg.getKey("TSFleC03")); /* return error-message */
          } /* Error encountered */
          W = new BufferedOutputStream(new DeflaterOutputStream(OS));
            /* use buffered output and compress implicitely */
        } /* compressed output is requested */

        mode = 0; /* mark file as closed (mode) */
        is = chkFKng(null,true); /* get current file-identifier */
        if(is==null) { /* file-ID could not provided */
          try {W.close();} catch (IOException e) { }
          return(TSLogg.getKey("TSFleC04")); } /* return error-message */
        is += "\r\n"; /* add CRLF */
        try {W.write(is.getBytes()); /* write file-identifier */
        } catch (IOException e) { /* output-error occurred */
          System.err.println(e.toString());
          try {W.close();} catch (IOException E) { }
          return(TSLogg.getMsg("TSFleC05",is)); /* return error-message */
        } /* Error encountered */
        try {W.write(cB,0,i); /* write all Data to file */
        } catch (IOException e) { /* output-error occurred */
          System.err.println(e.toString());
          try {W.close();} catch (IOException E) { }
          return(TSLogg.getKey("TSFleC06")); /* return error-message */
        } /* Error encountered */

        try { W.close(); /* close output-file */
        } catch (IOException e) { System.err.println(e.toString()); }

        if(Lvl<=0) break; /* File is fully created */
        return(TSLogg.getMsg("TSFleC07",Lvl)); /* return error-message */
      default: /* invalid mode -- provide error-message */
        return(TSLogg.getMsg("TSFleC11",mode)); /* return error-message */
    } /* close of file is executed */
    mode = 0; /* mark file as closed (mode) */
    return(null); /* return without error */
  }

 /*=====================================================================
  * Method "Expand"
  * process token-definitions and create lines of data
  ====================================================================*/
  private boolean Expand() /* build a expanded line (min '1') */
   {
   /*-----------------------------------------------------------------*/
    int      i;      /* work */
    long     j;      /* work */
    String   is,js;  /* work */
    boolean  noCRLF; /* no change of line (newline-status) */
   /*=================================================================*/
    is = DtaL[Ix]; /* get current line */
    LL:do { /* do until one line is fully provided */

      if(Pos>=mPos) cCmd = Math.max(cCmd,0); /* End of 'embedded' Token */

      if(cCmd<0) { /* new token is needed */
        if(is.length()==0) is = TSUtil.Tab.substring(0,Lvl);
        zPos[Lvl] = mPos;   /* save buffer-end of current token */
        TokT[Lvl] = cTok;   /* save current token-definition */
        nLFT[Lvl] = nLF1 ? 2 : 1; /* save newLine-status for 'End of Token' */
        if(nLF2) nLFT[Lvl] *= 3; /* save 'designated' newLine-status */
        Lvl += 1; /* process new token-definition */
          /* Start of a new token is a new iteration-level */
        if(Lvl>=zPos.length) { /* too less iteration-memory */
          EMsg = TSLogg.getMsg("TSFleE01",zPos.length); /* create error-msg */
          return(true); /* set 'end of data' and mark error */
        } /* Systemerror found */
        nLF1 = nLF2; /* activate 'designated' newLine-status */
        nLF2 = false; /* reset new 'designated' newLine-status */
        i = (int)getInt(4); /* get number of new token */
        if(EMsg!=null) return(true); /* error found -- break */
        cTok = getTok(i); /* get current token-definition */
        if(cTok==null) { /* Token is NOT defined in table */

// TSUtil.lg.Msg("Msg010",TSUtil.Tab.substring(0,Lvl)+"("+Lvl+")-->"+
//   "Pos="+Pos+", zPos="+mPos+", nLF1="+nLF1+", nLF2="+nLF2+", is='"+is+"'");

          EMsg = TSLogg.getMsg("TSFleE02",i); /* create error-message */
          return(true); /* set 'end of data' and mark error */
        } /* Systemerror found */
        is += cTok.getName() + " "; /* add name of current token */
        mPos = (int)getInt(4); /* get length of token ('Pos' is incremented) */
        if(EMsg!=null) return(true); /* error found -- break */
        mPos += Pos; /* compute pointer to buffer-end of current token */
        try {i = IS.read(); /* get optional label-length */
        } catch (IOException e) { /* read-error occurred */
          System.err.println(e.toString());
          EMsg = TSLogg.getKey("TSFleE03"); /* create error-message */
          return(true); /* set 'end of data' and mark error */
        } /* read-error found */
        Pos += 1; /* adjust pointer due to length read */
        if(i>0) { /* Label defined -- process it */
          js = getString(i); /* read label */
          if(js==null) return(true); /* error found -- break */
          is += js + " "; /* and add it to current line */
        } /* Label is processed */
        is += "( "; /* Processing of token starts here */
        cCmd = cTok.getTyp(); /* activate current token-command (type) */
      } /* new token is available now */

      noCRLF = false; /* set default 'current newLine-status */

// TSUtil.lg.Msg("Msg010",TSUtil.Tab.substring(0,Lvl)+"("+Lvl+")-->"+
//   "Pos="+Pos+", zPos="+mPos+", noCRLF="+noCRLF+", nLF1="+nLF1+", nLF2="+nLF2+", TTyp="+
//   cTok.getKey().toString()+", TNme='"+cTok.getName()+"', Act="+cCmd);

      /* process token */
      i = cCmd; /* save for switch-command */
      CmdT[Lvl] = cCmd = -1; /* Default: Token is COMPLETELY processed */
      switch(i) { /* process token due to current token-command (type) */
        case(K_none): break; /* no Action */
//          EMsg = TSLogg.getMsg("TSFleE04",i); /* create error-message */
//          return(true); /* set 'end of data' and mark error */
        case(K_uint):  /* -1- n x unsigned Integer value */
          if((cTok.getVal2()%3)==0) if((mPos-Pos)==0) break; /* no optional value */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if((cTok.getVal2()%2)==0) nLF1 = true; /* no newLine for current object */
          break; /* current token-command is processed */
        case(K_sint):  /* -2- n x signed Integer value */
          js = getInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_hex):  /* -3- Integer in Hex-Format (bit-array) */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          if(cTok.isEmbed()) i = cTok.getVal1(); /* number is fixed */
          js = getHex$(i); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_float):  /* -4- one ore more Float-Values */
          js = getFloat$(cTok.getVal1(),cTok.getVal2()); /* extract all values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_uint4float): /* -5-  n x UInt and x groups of 4 Floats */
          j = getInt(4); /* get next value */
          if(EMsg!=null) return(true); /* error found -- break */
          is += j + " "; /* convert number and add it to line */
          if((i=cTok.getVal1())>1) { /* more UInt-Numbers are available */
            js = getUInt$(i-1); /* extract all requested values */
            if(EMsg!=null) return(true); /* error found -- break */
            is += js; /* add value(s) to current line */
          } /* extract UIT-values and add to line */
          i = (int)(j * 4); /* compute number of following Float-values */
          js = getFloat$(i,cTok.getVal2()); /* extract all values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_sintfloat): /* -6-  n x SInt followed by n x Float */
          js = getInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getFloat$(cTok.getVal2(),6); /* extract all values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_string):  /* -7- variable Length unicode-string */
          js = getString$(cTok.getVal1(),false); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_uintfloat): /* -9-  n x UInt followd by n x Float(3) */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getFloat$(cTok.getVal2(),3); /* extract all values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_hexuint): /* -10-  n x Hex follwed by n x UInt */
          js = getHex$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(cTok.getVal2()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_tokuintfloat): /* -11- Token + UInt's + Floats */
          CmdT[Lvl] = 111; /* set continuation after 'embedded' token */
          nLF2 = true; /* no CRLF for NEXT token requested */
          break; /* current token-command is processed */
        case(111): /* Continuation of -11- Token + UInt's + Floats */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getFloat$(cTok.getVal2(),3); /* extract all values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          break; /* current token-command is processed */
        case(K_uintuint):  /* -12- */
          i = (int)getInt(4); /* get next value */
          if(EMsg!=null) return(true); /* error found -- break */
          is += i + " "; /* convert number and add it to line */
          i *= cTok.getVal1(); /* compute number of following values */
          switch(cTok.getVal2()) { /* select type and convert value */
            case(K_float): /* Float-Werte */
              js = getFloat$(i,3); /* extract all requested values */
              break; /* current token-command is processed */
            case(K_uint): /* unsigned Integer */
              js = getUInt$(i); /* extract all requested values */
              break; /* current token-command is processed */
            case(K_sint): /* signed Integer */
              js = getInt$(i); /* extract all requested values */
              break; /* current token-command is processed */
            case(K_hex): /* Integer in Hex-Darstellung */
              js = getHex$(i); /* extract all requested values */
              break; /* current token-command is processed */
            default: /* unknown value-type requested */
              EMsg = TSLogg.getMsg("TSFleE05",cTok.getName(),cTok.getVal2(),0);
              return(true); /* set 'end of data' and mark error */
          } /* convert due to requested type */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_uintfloathex):  /* -13- */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getFloat$(cTok.getVal2(),3); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_hexfloatuint): /* -14- n x Hex + n x Float + UInt */
          js = getHex$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getFloat$(cTok.getVal2(),3); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(1); /* extract requested value */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_uintfloat6): /* -15- n x UInt followd by n x Float(6) */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getFloat$(cTok.getVal2(),6); /* extract all values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_mixed1):  /* -16- */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getInt$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          if(i==0) break; /* no value furthermore */
          js = getInt$(i); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_uintplus):  /* -17- */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          js = getUInt$(i); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_uintplusfloat):  /* -18- */
          js = getUInt$(cTok.getVal1()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          js = getFloat$(i,cTok.getVal2()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_mixed3):  /* -19- */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getInt$(2); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getHex$(2); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          CmdT[Lvl] = 119; /* set continuation after 'embedded' token */
          break; /* current token-command is processed */
        case(119): /* Continuation of -19- */
          i = mPos - Pos; /* compute rest of current token */
          if(i>4) { /* annother value following? */
            CmdT[Lvl] = 219; /* set continuation after 'embedded' token */
            noCRLF = true; /* use same line for continuation */
            break; /* current token-command is processed */
          } /* token-value is processed */
        case(219): /* Continuation of -19- */
          nLF2 = true; /* no CRLF for NEXT token requested */
          i = mPos - Pos; /* compute rest of current token */
          if(i>4) { /* annother value following? */
            CmdT[Lvl] = 319; /* set continuation after 'embedded' token */
            noCRLF = true; /* use same line for continuation */
            break; /* current token-command is processed */
          } /* token-value is processed */
        case(319): /* Continuation of -19- */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          js = getUInt$(i); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          break; /* current token-command is processed */
        case(K_mixed4):  /* -20- */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(2); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getHex$(2); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          CmdT[Lvl] = 120; /* set continuation after 'embedded' token */
          nLF2 = true; /* no CRLF for NEXT token requested */
          break; /* current token-command is processed */
        case(120): /* Continuation of -20- */
          i = mPos - Pos; /* compute rest of current token */
          if(i>4) { /* annother value following? */
            CmdT[Lvl] = 220; /* set continuation after 'embedded' token */
            noCRLF = true; /* use same line for continuation */
            nLF2 = true; /* no CRLF for NEXT token requested */
            break; /* current token-command is processed */
          } /* token-value is processed */
        case(220): /* Continuation of -20- */
          i = mPos - Pos; /* compute rest of current token */
          if(i>4) { /* annother value following? */
            CmdT[Lvl] = 320; /* set continuation after 'embedded' token */
            noCRLF = true; /* use same line for continuation */
            nLF2 = true; /* no CRLF for NEXT token requested */
            break; /* current token-command is processed */
          } /* token-value is processed */
        case(320): /* Continuation of -20- */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          js = getFloat$(i,3); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          break; /* current token-command is processed */
        case(K_mixed2):  /* -22- */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          CmdT[Lvl] = 122; /* set continuation after 'embedded' token */
          nLF2 = true; /* no CRLF for NEXT token requested */
          break; /* current token-command is processed */
        case(122): /* Continuation of -22- */
          js = getFloat$(1,3); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getInt$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          if(i==0) break; /* no value furthermore */
          js = getUInt$(i); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          break; /* current token-command is processed */
        case(K_tokfloat):  /* -23- Token + one float-Wert */
          CmdT[Lvl] = 123; /* set continuation after 'embedded' token */
          nLF2 = true; /* no CRLF for NEXT token requested */
          break; /* current token-command is processed */
        case(123): /* Continuation of -23- Token + one float-Wert */
          js = getFloat$(cTok.getVal1(),cTok.getVal2()); /* extract annother value(s) */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          break; /* current token-command is processed */
        case(K_stringuint): /* -24- quote-surrounded string and uint */
          js = getString$(cTok.getVal1(),true); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          js = getUInt$(cTok.getVal2()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_hexfloat): /* -25- hex-value and floats */
          js = getHex$(1); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          js = getFloat$(i,cTok.getVal2()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        case(K_buffer): /* -26- */
          w1 = mPos - Pos; /* compute length of arguments until end */
          i = w1 % 64; /* compute remaining number of values */
          is += i + " "; /* add value to line */
          js = getChar$(i); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          w1 /= 64; /* compute number of lines */
          cCmd = 126; /* set continuation with next call */
          if(w1>0) break LL; /* save data and split command-processing */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          cCmd = -1; /* Now: Token is COMPLETELY processed */
          break; /* current token-command is processed */
        case(126): /* Continuation of -26-  */
          /* value w1 is preserved from command 26 */
          is = TSUtil.Tab.substring(0,Lvl); /* init line for output */
          js = getHex$(8); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = TSUtil.Tab.substring(0,Lvl); /* init line for output */
          js = getHex$(8); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          cCmd = 126; /* set continuation with next call */
          if(--w1>0) break LL; /* save data and split command-processing */
          if(addPool(is)) return(true); /* Overflow -- signal error */
          is = ""; /* reset current line after writing */
          cCmd = -1; /* Now: Token is COMPLETELY processed */
          break; /* current token-command is processed */
        case(K_floats): /* -27- Float-values until end of parm reached */
          i = (mPos - Pos) / 4; /* compute remaining number of values */
          js = getFloat$(i,cTok.getVal2()); /* extract all requested values */
          if(EMsg!=null) return(true); /* error found -- break */
          is += js; /* add value(s) to current line */
          break; /* current token-command is processed */
        default: /* invalid token-command */
          EMsg = TSLogg.getMsg("TSFleE09",cTok.getName(),i,0); /* error-msg */
          return(true); /* set 'end of data' and mark error */
      } /* process all token-definition due to current command */

// TSUtil.lg.Msg("Msg010",TSUtil.Tab.substring(0,Lvl)+"("+Lvl+")-->"+
//   "noCRLF="+noCRLF+", nLF1="+nLF1+", nLF2="+nLF2+", Act="+cCmd);

      if(Pos>=mPos) { /* end of buffer of current token reached? */
        /* 'embedded' is ignored in this casee
         * The current command is ignored, because it must be processed
         * completely
         */
        if(cCmd>=0) { /* Continuation must not be requested */
          EMsg = TSLogg.getMsg("TSFleE11",cTok.getName(),cCmd,0); /* error-message */
          return(true); /* set 'end of data' and mark error */
        } /* invalid token-command */
        noCRLF = nLF1; /* last newLine-Status 'End of Token' is used */
        Lvl -= 1; /* deactivate current iteration-level, Parent-token gets aktive  */
        if(Lvl<0) return(false); /* EOF -- no further action */
        mPos = zPos[Lvl]; /* new buffer-end-position of current token */
        cCmd = CmdT[Lvl]; /* activate next/last token-command */
        cTok = TokT[Lvl]; /* activate next/last token-definition */
        nLF1 = (nLFT[Lvl]%2)==0; /* activate last newLine-status 'End of Token' */
        nLF2 = (nLFT[Lvl]%3)==0; /* activate last 'designated' newLine-status */

        if(is.length()==0) is = TSUtil.Tab.substring(0,Lvl);
        is += ") "; /* mark end of token */

        if(noCRLF) continue; /* process next token without CRLF */

        if(addPool(is)) return(true); /* Overflow -- signal error */
        DtaL[Ix] = ""; /* reset current line after writing */
        return(false); /* OK -- return without error */
      } /* token is fully processed */

      if(!cTok.isEmbed()) { /* embedded tokens must be allowed */
        EMsg = TSLogg.getMsg("TSFleE12",cTok.getName(),Pos,mPos); /* error-message */
        return(true); /* set 'end of data' and mark error */
      } /* Systemerror found! */
    } while(noCRLF); /* -LL- loop until at least ONE line is provided */

    if(addPool(is)) return(true); /* Overflow -- signal error */
    DtaL[Ix] = ""; /* reset current line after writing */
    return(false); /* OK -- return without error */
  }

 /*=====================================================================
  * Method "getChar$"
  * Get n x byte-value from file
  ====================================================================*/
  private String getChar$( /* read Byte-values and convert them */
   int rep) /* number of values */
   {
   /*-----------------------------------------------------------------*/
    int    i,j; /* temp. value(s) */
    String erg; /* result */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    erg = ""; /* initialize result */
    for(i=0;i<rep;i+=1) { /* add all requested value(s) */
      try {j = IS.read(); /* read 1 byte */
      } catch (IOException e) { /* read-error occurred */
        System.err.println(e.toString());
        EMsg = TSLogg.getMsg("TSFleM11",Pos); /* error-message */
        return(null); /* value is NOT available -- return */
      } /* Exception while reading */
      Pos += 1; /* increment current position */
      j &= 255; /* mask Byte */
      erg += j + " "; /* add value(s) to current line */
    } /* process all values */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "getData"
  * Read next line 
  * Next line is provided from file or buffer
  * If buffer is empty is is filled with decompressed data of file
  * if an error is found: EMsg is set and 'null' is returned
  ====================================================================*/
  protected String getData() /* read new line of data */
   {
   /*-----------------------------------------------------------------*/
    String is; /* Work-buffer */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    if(Fle==null) { /* no active file definition */
      EMsg = TSLogg.getKey("TSFleD01"); /* set error-message */
      return(null); } /* return and signal error */
    is = Fle.getAbsolutePath(); /* get complete file-definition */
    if(mode>=0) { /* file is opened for output or close */
      EMsg = TSLogg.getMsg("TSFleD02",is,mode,0); /* set error-message */
      return(null); } /* return and signal error */

    if(mode<-1) { /* Decompress is neccessary */
      /* does buffer contain lines ? */
      if(Ix==Ox) { /* no data in line-buffer -- fill it */
        /* decompress until next line(s) is provided */
        if(Expand()) return(null); /* return and signal error */
      } /* line-buffer is filled (?) */
      if(Ix==Ox) return(null); /* still no lines -- EOF */

      is = DtaL[Ox++]; /* get next line from buffer */
      if(Ox==DtaL.length) Ox = 0; /* Wrap-around */
      return(is); /* return new data */
    } /* Decompress is neccessary */

    /* file only need to be read */
    try {is = Rdr.readLine(); /* get next line from file */
    } catch (IOException e) { /* Input-error ? */
      System.err.println(e.toString());
      EMsg = TSLogg.getMsg("TSFleD03",Fle.getAbsolutePath());
      return(null); /* set 'end of data' and mark error */
    } /* Input-error occurred */
    return(is); /* return new data */
  }

 /*=====================================================================
  * Method "getErr"
  * Get active error-message
  ====================================================================*/
  protected String getErr() /* get current error-message */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(EMsg); /* provide error-message or 'null' */
  }

 /*=====================================================================
  * Method "getFle" (Dummy for extended classes)
  ====================================================================*/
  protected int getFle() { return(-1); }

 /*=====================================================================
  * Method "getFloat"
  * Get 4 Byte from file and convert them to Float as STRING 
  ====================================================================*/
  private String getFloat( /* read float-value and convert it */
   int c) /* number of relevant digits in mantissa */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* result-string */
    int    i;   /* temp. value(s) */
    float  f;   /* temp. value(s) */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    i = (int)getInt(4); /* get next Int-value from buffer */
    if(EMsg!=null) return(null); /* error found -- break */
    c = Math.max(c,1); /* min of one decimal place */
    c = Math.min(c,8); /* max of 8 decimal places */
    f = Float.intBitsToFloat(i); /* convert value to float */
    erg = fF[c-1].format(f); /* format float to string */
    i = erg.indexOf('.'); /* get position of decimal point */
    if(i<0) return(erg); /* no decimal point -- OK */
    if(erg.endsWith(".0")) return(erg.substring(0,i)); /* OK */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "getFloat$"
  * Get n x float-value from file using 'getFloat()'
  ====================================================================*/
  private String getFloat$( /* read float-values and convert them */
   int rep, /* number of values */
   int rd)  /* number of relevant digits in mantissa */
   {
   /*-----------------------------------------------------------------*/
    int    i;      /* temp. value(s) */
    String erg,is; /* result */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    erg = ""; /* initialize result */
    for(i=0;i<rep;i+=1) { /* add all requested value(s) */
      is = getFloat(rd); /* extract next value */
      if(EMsg!=null) return(null); /* error found -- break */
      erg += is + " "; /* add value(s) to current line */
    } /* process all values */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "getHex$"
  * Get n x UINT from file and convert to 'hexadecimal'
  ====================================================================*/
  private String getHex$( /* read integer and convert */
   int rep) /* number of values */
   {
   /*-----------------------------------------------------------------*/
    int    i,j;   /* temp. value(s) */
    String erg,s; /* result */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    erg = ""; /* initialize result */
    for(i=0;i<rep;i+=1) {/* add all requested value(s) */
      j = (int)getInt(4); /* extract next value */
      if(EMsg!=null) return(null); /* error found -- break */
      s = "0000000"+Integer.toHexString(j); /* convert to hexa */
      erg += s.substring(s.length()-8) + " "; /* add value to string */
    } /* process all values */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "getInt"
  * Get 'Integer'/'Unsigned Integer' (x Byte) from file as 'long'
  ====================================================================*/
  private long getInt( /* read long-Integer from file */
   int len) /* length of input-field (1..8) */
   {
   /*-----------------------------------------------------------------*/
    long i; /* temp. value(s) */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    try {i = IS.read(ibuf,0,len); /* read n bytes */
    } catch (IOException e) { /* read-error occurred */
      System.err.println(e.toString());
      EMsg = TSLogg.getMsg("TSFleM21",Pos); /* error-message */
      return(0); /* value is NOT available -- return */
    } /* Exception while reading */
    if(i!=len) { /* not enough data available */
      EMsg = TSLogg.getMsg("TSFleM22",null,Pos,(int)i); /* error-message */
      return(0); } /* not enough data available */
    Pos += len; /* increment current position */
    i = 0; /* init result */
    while(--len>=0) i = (i << 8) | (long)(ibuf[len]) & 0x0FF;
      /* set next part of value */
    return(i); /* provide result */
  }

 /*=====================================================================
  * Method "getInt$"
  * Get n x int-value from file using 'getInt()'
  * Convert SIGNED values to String and concatenate them
  ====================================================================*/
  private String getInt$( /* read Integer-values and convert them */
   int rep) /* number of values */
   {
   /*-----------------------------------------------------------------*/
    int    i,j; /* temp. value(s) */
    String erg; /* result */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    erg = ""; /* initialize result */
    for(i=0;i<rep;i+=1) { /* add all requested value(s) */
      j = (int)getInt(4); /* extract next value, j is signed */
      if(EMsg!=null) return(null);/* error found -- break */
      erg += j + " "; /* add value(s) to current line */
    } /* process all values */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "getItem"
  * Extract next item and provide it in String-format
  *   Surrrounding quotes are deleted if present
  * If no item is found, 'null' is returned
  * Method operates on 'DtaL[0]' and uses pointer 'Ix'
  * Testing for 'special values' is possible!
  *  In this case 'null' is returned if match is found
  *   and the found original values otherwise
  * ATTENTION: '(' and')' is a valid seperator 
  ====================================================================*/
  private String getItem( /* get next String-Value */
   String rv) /* requested value or 'null' */
   {
   /*-----------------------------------------------------------------*/
    String e;   /* result */
    int    i,j; /* temp. value(s) */
   /*=================================================================*/
    j = DtaL[0].length(); /* temporary value (used more than once) */
    if(Ix>=j) return(rv); /* error -- string is empty */
    while(DtaL[0].charAt(Ix)==' ') { /* leading blank */
      Ix += 1; /* skip it */
      if(Ix>=j) return(rv); /* error -- string is empty */
    } /* skip all leading blanks */

    if(rv!=null) { /* special value requested */
      i = rv.length(); /* length to compare */
      if(Ix+i>j) return(rv); /* error -- string does not fit */
      e = DtaL[0].substring(Ix,Ix+i); /* extract string to compare */
      if(!e.equalsIgnoreCase(rv)) return(e); /* return string as an error-signal */
      Ix += i; /* skip valid characters */
      return(null); /* return with no error */
    } /* requst for special characters was active */

    /* 'Ix' points to first valid character */
    if(DtaL[0].charAt(Ix)=='"') { /* String contain quotes */
      Ix += 1; /* skip quote-character */
      if((i=DtaL[0].indexOf('"',Ix))<0) return(null); /* search for quote */
        /* second quote is missed -- signal error */
      if(Ix<i) e = DtaL[0].substring(Ix,i); /* extract current string */
      else e = ""; /* result is an empty string */
      Ix = i + 1; /* and set continuation */
      return(e); /* provide result without error */
    } /* String was surrounded by quotes */

    j = DtaL[0].length(); /* set maximum length of string */
    i = DtaL[0].indexOf('(',Ix); /* search for end-char */
    if(i>=0) j = Math.min(i,j); /* minimum distance */
    i = DtaL[0].indexOf(')',Ix); /* search for end-char */
    if(i>=0) j = Math.min(i,j); /* minimum distance */
    i = DtaL[0].indexOf(' ',Ix); /* search for end-char */
    if(i>=0) j = Math.min(i,j); /* minimum distance */
    e = DtaL[0].substring(Ix,j); /* extract string-value */
    Ix = j; /* set position for continuation */
    return(e); /* provide result without error */
  }

 /*=====================================================================
  * Method "getMode"
  * Provides caller with last used I/O-mode
  ====================================================================*/
  protected int getMode() /* provide last used I/O-Mode */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(cMde); /* provide last used I/O-mode */
  }

 /*=====================================================================
  * Method "getString"
  * Read Unicode-String from binery file
  * ATTENTION: No additional Blank is added
  ====================================================================*/
  private String getString( /* get String from file */
   int len) /* length of requested string */
   {
   /*-----------------------------------------------------------------*/
    int               i,bl;  /* temp. value(s) */
    byte              buf[]; /* Input-buffer */
    char              cP[];  /* Convert-buffer */
    InputStreamReader cnv;   /* Reader of Unicode text */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    bl  = len * 2; /* compute Byte-length of string */
    buf = new byte[bl]; /* create buffer of corresponding length */
    try {i = IS.read(buf,0,bl); /* get unicode-string as bytes */
    } catch (IOException e) { /* read-error occurred */
      System.err.println(e.toString());
      EMsg = TSLogg.getMsg("TSFleM31",Pos); /* error-message */
      return(null); /* value is NOT available -- return */
    } /* Exception while reading */
    if(i!=bl) { /* not enough data available */
      EMsg = TSLogg.getMsg("TSFleM32",null,Pos,i); /* error-message */
      return(null); } /* not enough data available */
    Pos += bl; /* increment current position */
    try { cnv = new InputStreamReader( /* convert it */
                new ByteArrayInputStream(buf),"UTF-16LE");
    } catch (UnsupportedEncodingException e) { /* invalid code */ 
      System.err.println(e.toString());
      EMsg = TSLogg.getMsg("TSFleM33",new String(buf)); /* error-message */
      return(null); /* value is NOT available -- return */
    } /* Exception at OPEN */
    cP = new char[len]; /* get buffer for unicode-string */
    try {i = cnv.read(cP,0,len); /* copy bytes to string */
    } catch (IOException e) { /* read-error occurred */
      System.err.println(e.toString());
      EMsg = TSLogg.getKey("TSFleM34"); /* error-message */
      return(null); /* value is NOT available -- return */
    } /* Exception while reading */
    if(i!=len) { /* not enough data available */
      EMsg = TSLogg.getMsg("TSFleM35",i); /* error-message */
      return(null); } /* not enough data available */
    try { cnv.close(); } catch (IOException e) { } /* close again */
    return(new String(cP)); /* provide result */
  }

 /*=====================================================================
  * Method "getString$"
  * Get n x String-definition and concatenate them with ' '
  * If requested or neccessary each string is surrounded with '"'
  ====================================================================*/
  private String getString$( /* read Strings and process themn */
   int     rep, /* number of values */
   boolean add) /* add '"' in every case (if 'true') */
   {
   /*-----------------------------------------------------------------*/
    int    i,j;   /* temp. value(s) */
    String erg,s; /* result */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    erg = ""; /* initialize result */
    for(i=0;i<rep;i+=1) { /* add all requested value(s) */
      j = (int)getInt(2); /* get length of string ('Pos' is inc.) */
      if(EMsg!=null) return(null); /* error found -- break */
      s = getString(j); /* get string itself ('Pos' is incremented) */
      if(s==null) return(null); /* error found -- break */
      s = s.trim(); /* delete leading and trailing blanks */
      if(add || s.indexOf(' ')>=0) s = "\""+s+"\""; /* add " */
      erg += s + " "; /* concatenate string to result */
    } /* process all values */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "getTok" (Dummy for extended classes)
  ====================================================================*/
  protected TSFleTok getTok(int i) { return(null); }

 /*=====================================================================
  * Method "getTok" (Dummy for extended classes)
  ====================================================================*/
  protected TSFleTok getTok(String s) { return(null); }

 /*=====================================================================
  * Method "getUInt$"
  * Get n x unsigned int-value from file using 'getInt()'
  * Convert unsigned values to String and concatenate them
  ====================================================================*/
  private String getUInt$( /* read Integer and convert them */
   int rep) /* number of values */
   {
   /*-----------------------------------------------------------------*/
    int    i;   /* temp. value(s) */
    long   j;   /* temp. value(s) */
    String erg; /* result */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    erg = ""; /* initialize result */
    for(i=0;i<rep;i+=1) { /* add all requested value(s) */
      j = getInt(4); /* extract next value, only unsigned values */
      if(EMsg!=null) return(null);/* error found -- break */
      erg += j + " "; /* add value(s) to current line */
    } /* process all values */
    return(erg); /* provide result */
  }

 /*=====================================================================
  * Method "open"
  * open file  and create Reader or Writer
  * If 'input' is requested: check input-mode of file
  *   (in this case 'iom'-argument is ignored)
  *  Output-Mode: 0 = UT - format
  *               1 = CB - format
  *               2 = CT - format (currently NOT supported) 
  *               3 = UB - format
  * Method returns an error-message or 'null' if no error
  ====================================================================*/
  protected String open( /* open file */
   boolean out, /* 'Output' requested (if 'true') */
   int     iom) /* requested 'Output' - Mode */ 
   {
   /*-----------------------------------------------------------------*/
    FileInputStream   Dbi; /* binary Reader */
    InputStreamReader ISR; /* unicode-reader */
    String            is;  /* temp. value(s) */
    int               i;   /* temp. value(s) */
    char[]            cP;  /* temp. value(s) */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    if(iom<0 || iom>3) return(TSLogg.getMsg("TSFleO01",iom)); /* error */
    if(Fle==null) return(TSLogg.getKey("TSFleO02")); /* no active file */
    if(mode!=0) /* file is ALREADY open -- return with error */
      return(TSLogg.getMsg("TSFleO03",mode)); /* return error-message */

    is = Fle.getAbsolutePath(); /* set complete file-name */
    if(out) { /* Output requested -- open file in output-mode */
      if(Fle.exists()) if(Fle.isDirectory()) /* directory! */
        /* File-definition describes a directory-entry -- return error */
        return(TSLogg.getMsg("TSFleO11",is)); /* return error-message */
      if(iom%2==1) { /* Output' should be reduced */
        /* use 'CB'-format or 'UB'-format for output */
        /* reduce has to be done in memory, because whole length of data
         * is not known */
        try { OS = new FileOutputStream(Fle); /* open file temporary */
        } catch (FileNotFoundException e) { /* parent-folder not found */ 
          System.err.println(e.toString());
          return(TSLogg.getMsg("TSFleO12",is)); /* return error-message */
        } /* File-error found */
        try {OS.write("SIMISA@F".getBytes(),0,8); /* write file-identifier */
        } catch (IOException e) { /* output-error occurred */
          System.err.println(e.toString());
          return(TSLogg.getMsg("TSFleO13",is)); /* return error-message */
        } /* Error encountered */
        mode = iom + 1; /* set mode to 'binary output' ('CB'/'UB'-format) */
        cMde = iom; /* remember last mode */

        cB = new byte[K_bLen]; /* allocate buffer for Output-data */
        Pos = mPos = 0; /* set current position */
        cTok = new TSFleTok(0,TSFile.K_none,0,0,true,"<file>"); /* dummy-token */
        Lvl = 0; /* init counter of Iterations */
        cCmd = -1; /* first command is NOT used */
        nLF1 = nLF2 = false; /* reset newLine-status */
        return(null); /* file is open -- no error */
      } /* Output is 'compressed' */

      /* no compress neccessary - plain write only */
      try { Wtr = new BufferedWriter(new OutputStreamWriter( /* Writer */
                  new FileOutputStream(Fle),"UnicodeLittle"));
      } catch (FileNotFoundException e) { /* parent-folder not found */ 
        System.err.println(e.toString());
        return(TSLogg.getMsg("TSFleO21",is)); /* return error-message */
      } catch (UnsupportedEncodingException e) {
        System.err.println(e.toString());
        return(TSLogg.getMsg("TSFleO22",is)); /* return error-message */
      } /* File-error found */
      mode = iom + 1; /* set mode to 'output character' ('UT'-format) */
        /* only 'UT'-format is really used instead of 'CT'-format */
      cMde = iom; /* remember last mode */

      is = chkFKng(null,false); /* get current file-identifier */
      if(is==null) return(TSLogg.getMsg("TSFleO23",Fle.getAbsolutePath()));
      try {Wtr.write(is,0,is.length()); /* write file-identifier */
      } catch (IOException e) { /* output-error occurred */
        System.err.println(e.toString());
        return(TSLogg.getMsg("TSFleO24",Fle.getAbsolutePath()));
      } /* Error encountered */
      try {Wtr.newLine(); /* write CRLF */
      } catch (IOException e) { /* output-error occurred */
        System.err.println(e.toString());
        return(TSLogg.getMsg("TSFleO25",Fle.getAbsolutePath()));
      } /* Error encountered */
      return(null); /* file is open -- no error */
    } /* Output was requested */

    /* Open file for input and check Input-mode */
    if(!Fle.exists()) return(TSLogg.getMsg("TSFleO31",is)); /* File not found */
    if(Fle.isDirectory()) return(TSLogg.getMsg("TSFleO32",is)); /* File is folder */
 
    try { Dbi = new FileInputStream(Fle); /* read ASCII (binary) */
    } catch (FileNotFoundException e) { /* file not found */
      /* error should NOT occur due to previous testing */
      System.err.println(e.toString());
      return(TSLogg.getMsg("TSFleO33",is)); /* return error-message */
    } /* Exception at OPEN */
    try {i = Dbi.read(ibuf); /* read first part of file-ID */
    } catch (IOException e) { /* handle read-error */
      System.err.println(e.toString());
      try { Dbi.close(); } catch (IOException E) { }
      return(TSLogg.getMsg("TSFleO34",is)); /* read-error found -- return message */
    } /* Exception while reading */
    if(i!=16) { /* not enough data available */
      try { Dbi.close(); } catch (IOException E) { }
      return(TSLogg.getMsg("TSFleO35",is,i,0)); /* read-error found -- return message */
    } /* not enough data available */
    if((new String(ibuf,0,7)).equals("SIMISA@")) { /* type-ID match -> file is binary */
      if(ibuf[7]==64) { /* filetype 'UB' found (ibuf[7]=='@') */
        /* process 'expand' only */
        mPos = (int)(Fle.length() - 32); /* init source-length of file */
        IS = new BufferedInputStream(Dbi); /* use buffered input */
        cMde = 3; /* remember last mode ('UB') */
      } else { /* handle filetype 'CB' */
        /* 'decompress' and 'expand' has to be done */
        mPos = TSUtil.getInt(ibuf,8) - 16; /* init source-length of file */
        IS = new BufferedInputStream(new InflaterInputStream(Dbi));
          /* use buffered input and decompress implicitely */
        cMde = 1; /* remember last mode ('CB') */
      } /* filetype is successfully processed */
      mode = -2; /* set mode to 'binary input' */
      /* common part for input, which must be expanded */
      try {i = IS.read(ibuf,0,16); /* read second part of file-ID */
      } catch (IOException e) { /* handle read-error */
        System.err.println(e.toString());
        close(); /* error -- close file again */
        return(TSLogg.getMsg("TSFleO36",is)); /* read-error found -- return message */
      } /* Exception while reading */
      if(i!=16) { /* not enough data available */
        close(); /* error -- close file again */
        return(TSLogg.getMsg("TSFleO37",is,i,0)); /* read-error found -- return message */
      } /* not enough data available */
      is = new String(ibuf,0,14); /* convert file-ID to string */
      if((is=chkFKng(is,true))!=null) { /* invalid file-ID */
        close(); /* error -- close file again */
        return(TSLogg.getMsg("TSFleO38",is)); /* invalid file-ID found -- return message */
      } /* invalid file-id */

      /* Initialize common values for 'expand' of lines */
      Ix = Ox = 0; /* initialize Bufferpointer */
      DtaL[Ix] = ""; /* init current data-line */
      Pos = 0; /* set start-position */
        /* max. Position (mPos) is alraeady set */
      cTok = new TSFleTok(0,TSFile.K_none,0,0,true,"<file>"); /* dummy-token */
      Lvl = 0; /* init counter of Iterations */
      cCmd = -1; /* Meaning: first token must be loaded */
      nLFT[0] = 1; /* Default: use CRLF */
      nLF1 = nLF2 = false; /* reset newLine-status */
      return(null); /* return without error */
    } /* file is successfully opened for expand */

    try { Dbi.close(); /* close binary file */
    } catch (IOException e) { System.err.println(e.toString()); }

    /* now: file contains an unicode-text in every case */
    try { ISR = new InputStreamReader(new FileInputStream(Fle),"UTF-16");
      /* type of endian is read from file */
    } catch (FileNotFoundException e) { return(TSLogg.getMsg("TSFleO41",is)); }
      catch (UnsupportedEncodingException e) { /* unsupported codeset! */ 
      System.err.println(e.toString());
      return(TSLogg.getMsg("TSFleO42",is)); /* open-file-error found */
    } /* Exception at OPEN */
    mode = -1; /* set mode to 'input character' */
    cP = new char[16]; /* allocate input-buffer for first test */
    try {i = ISR.read(cP,0,16); /* read first part of file-identifier */
    } catch (IOException e) { /* handle read-error */
      System.err.println(e.toString());
      try {ISR.close();} catch (IOException E) { } /* close unicode file */
      return(TSLogg.getMsg("TSFleO43",is)); /* read-error found -- return message */
    } /* Exception while reading */
    if(i!=16) { /* not enough data available */
      try {ISR.close();} catch (IOException e) { } /* close unicode file */
      return(TSLogg.getMsg("TSFleO44",is,i,0)); /* read-error found -- return message */
    } /* not enough data available */
    if((new String(cP,0,8)).equals("SIMISA@F")) { /* type-ID match -> file is compressed */
      try {ISR.close();} catch (IOException e) { } /* close unicode file */
      try {Dbi = new FileInputStream(Fle); /* read binery again */
      } catch (FileNotFoundException e) { }  /* third time -- no error tested */
      try {Dbi.skip(34);} catch (IOException e) { /* skip already read bytes */
        System.err.println(e.toString()); /* number 45 is reserved */}
      try { Rdr = new BufferedReader(new InputStreamReader( /* compressed unicode */
                  new InflaterInputStream(Dbi),"UTF-16LE"));
        /* read compressed unicode, type of endian is fixed */
      } catch (UnsupportedEncodingException e) { /* unsupported codeset! */ 
        System.err.println(e.toString());
        return(TSLogg.getMsg("TSFleO46",is)); /* open-file-error found */
      } /* Exception at OPEN */
//      ibuf = (new String(cP,8,4)).getBytes(); /* get length-field from data */
      is = "SIMISA@@@@@@@@@@"; /* set fixed part of file-ID */
      cMde = 2; /* remember last mode ('CT') */
    } else { /* file is NOT compressed -- use plain read */
      is = new String(cP); /* convert first part of file-ID to string */
      Rdr = new BufferedReader(ISR); /* now reader is buffered */
      cMde = 0; /* remember last mode ('UT') */
    } /* input-mode is set */

    /* file is ready for reading */
    try {is += Rdr.readLine(); /* read rest of file-identifier */
    } catch (IOException e) { /* handle read-error */
      System.err.println(e.toString());
      close(); /* error -- close file again */
      return(TSLogg.getMsg("TSFleO47",Fle.getAbsolutePath())); /* read-error found */
    } /* Exception while reading */

    if((is=chkFKng(is,false))==null) return(null); /* ID OK  -- file open */
    /* file-identifier is not acceptable */
    close(); /* error -- close file again */
    return(TSLogg.getMsg("TSFleO48",is)); /* invalid file-ID found -- return message */
  }

 /*=====================================================================
  * Method "putData"
  * Write line to file
  * Do Compress and/or Reduce if requested
  * Method returns error-message or 'null' if no error
  ====================================================================*/
  protected String putData( /* write line to file */
   String dta) /* line to be written */
   {
   /*-----------------------------------------------------------------*/
    String is; /* temp. value(s) */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    if(dta==null) return(null); /* Writing is NOT neccessary */
    if(Fle==null) return(TSLogg.getKey("TSFleD11")); /* no active file */
    is = Fle.getAbsolutePath(); /* get whole file-name */
    if(mode<=0) /* file is opened for input or close */
      return(TSLogg.getMsg("TSFleD12",is,mode,0)); /* return error-message */
 
    if(mode%2==0) { /* binary output -- Reduce neccessary */
      /* ATTENTION: Data is NOT REALLY written, but buffered in
       * memory only. Writing will occur, before the file is closed.
       */
      if(Reduce(dta)) return(EMsg);
      return(null); /* OK -- Write was successfull */
    } 
    /* file has to be written ONLY, no reduce/compress */
    try {Wtr.write(dta,0,dta.length()); /* write record */
    } catch (IOException e) { /* write-error occurred */
      System.err.println(e.toString()); /* write-error -- return message */
      return(TSLogg.getMsg("TSFleD13",is));
    } /* Exception while writing */
    try {Wtr.newLine(); /* write CRLF */
    } catch (IOException e) { /* write-error occurred */
      System.err.println(e.toString()); /* write-error -- return message */
      return(TSLogg.getMsg("TSFleD14",is));
    } /* Exception while writing */
    return(null); /* OK -- Write was successfull */
  }

 /*=====================================================================
  * Method "putInt"
  * write Integer/unsigned Integer to Byte-Array using MS-Format
  ====================================================================*/
  private boolean putInt( /* write Integer to byte-array */
   long   val, /* value to be written (UINT requests long) */
   byte[] dst, /* target-array */
   int    ofs, /* offset to array */
   int    len) /* Length of target-field (1,2,4) */
   {
   /*-----------------------------------------------------------------*/
    int i; /* temp. value(s) */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    if(dst==null) { /* no array defined */
      EMsg = TSLogg.getKey("TSFleM41"); /* error-message */
      return(true); } /* signal error and return */
    if(len<0 || len>8) { /* invalid value */
      EMsg = TSLogg.getMsg("TSFleM42",len); /* error-message */
      return(true); } /* signal error and return */
    if(ofs<0 || (ofs+len)>dst.length) { /* no space in dest-buffer */
      EMsg = TSLogg.getMsg("TSFleM43",null,ofs+len,dst.length);
      return(true); } /* signal error and return */

    for(i=0;i<len;i+=1) { /* write all bytes */
      dst[ofs++] = (byte)(val & 0x0FF); /* copy byte to array */
      val >>= 8; /* and get next byte into position */
    } /* copy all requested bytes tu buffer */
    Pos += len; /* adjust position-pointer */
    return(false); /* return with NO error */
  }

 /*=====================================================================
  * Method "putString"
  * Convert Unicode-String to Byte-Array
  * If 'putString' is used to write a label-Definition (no length field)
  *  a negative offset is used as marker
  ====================================================================*/
  private boolean putString( /* put String to Byte-Array */
   String s,   /* String to convert */
   byte[] dst, /* target-array */
   int    ofs) /* offset to array */
   {
   /*-----------------------------------------------------------------*/
    byte[]                buf; /* Output-buffer */
    ByteArrayOutputStream bOS; /* Control of Byte-Array */
    OutputStreamWriter    cnv; /* Writer of Unicode text */
    int                   i;   /* temp. value(s) */
   /*=================================================================*/
    EMsg = null; /* clear current error-message */
    if(dst==null) { /* no array defined */
      EMsg = TSLogg.getKey("TSFleM51"); /* error-message */
      return(true); } /* signal error and return */
    bOS = new ByteArrayOutputStream(); /* create Output-Control */
    try { cnv = new OutputStreamWriter( bOS,"UTF-16LE"); /* convert */
    } catch (UnsupportedEncodingException e) { /* invalid code */ 
      System.err.println(e.toString());
      EMsg = TSLogg.getMsg("TSFleM52",s); /* error-message */
      return(true); /* value is NOT available -- return */
    } /* Exception at OPEN */
    i = s.length(); /* Length of String in chars */
    try {cnv.write(s,0,i); /* convert string to bytes */
    } catch (IOException e) { /* write-error occurred */
      System.err.println(e.toString());
      EMsg = TSLogg.getKey("TSFleM53"); /* error-message */
      return(true); /* value is NOT converted -- return */
    } /* Exception while writing */
    try { cnv.close(); } catch (IOException e) { } /* close again */
    buf = bOS.toByteArray(); /* get result */
    if(ofs<0) ofs = -ofs; /* clear marker for label-processing */
    else { /* no label -- write length-field */
      if(putInt(i,dst,ofs,2)) return(true); /* set length, return if error */
      ofs += 2; /* skip length-field */
    } /* length of String is written */
    i = buf.length; /* get Length of new data */
    if(ofs<0 || (ofs+i)>dst.length) { /* no space in dest-buffer */
      EMsg = TSLogg.getMsg("TSFleM54",null,ofs+i,dst.length);
      return(true); } /* signal error and return */
    System.arraycopy(buf,0,dst,ofs,i); /* copy data to destination */
    Pos += i; /* adjust position-pointer */
    return(false); /* and signal 'no error' */
  }

 /*=====================================================================
  * Method "Reduce"
  * Reduce Data-Definition to token-format
  ====================================================================*/
  protected boolean Reduce( /* Reduce lines to token-format */
   String lne) /* current line */
   {
   /*-----------------------------------------------------------------*/
    String is; /* temp. value(s) */
    byte[] tb; /* temp. value(s) */
    int    i;  /* temp. value(s) */
    long   j;  /* temp. value(s) */
    float  f;  /* temp. value(s) */
   /*=================================================================*/
    while(lne.startsWith("\t")) lne = lne.substring(1); /* delete tab */
    lne = lne.trim(); /* delete leading/trailling blank */
    if(lne.length()==0) return(false); /* no valid data (no error) */

    DtaL[0] = lne; /* set current line for reduce */
    Ix      = 0;   /* and start-Position */
    i = cB.length; /* current buffer-length */
    if(i-Pos<K_bInc) { /* available buffer is nearly empty */
      i += K_bLen; /* compute new buffer-length */
      tb = cB; /* save old buffer */
      cB = new byte[i]; /* allocate a greater one */
      System.arraycopy(tb,0,cB,0,Pos); /* copy data to new buffer */
      tb = null; /* return old buffer to garbage collector */
    } /* buffer is OK now */

    while(Ix<DtaL[0].length()) { /* process all Items on current line */
      Ox = Ix; /* save current Item-position for 'pushback' */
      is = getItem(null); /* get next Item from line */
      if(is==null) { /* no item -- set message and return error */
        EMsg = TSLogg.getKey("TSFleR01"); /* error-message */
        return(true); } /* signal error and return */
      if(cTok.isEmbed() || nLF1) { /* 'embeded' token is possible */
        TokT[Lvl] = cTok;  /* save current token-definition */
        cTok = getTok(is); /* get current token-definition */
        if(cTok==null) { /* Token is NOT defined in table */
          if(nLF1) { /* valid token is required but NOT found */
            EMsg = TSLogg.getMsg("TSFleR02",is); /* error-message */
            return(true); } /* signal error and return */
          cTok = TokT[Lvl]; /* restore current token-definition */
        } else { /* valid token found -- embedded token starts here */
          zPos[Lvl] = mPos; /* save buffer-start of current token */
          CmdT[Lvl] = cCmd; /* save current token-command */
          Lvl += 1; /* process new token-definition */
            /* Start of a new token is a new iteration-level */
          if(Lvl>=zPos.length) { /* too less iteration-memory */
            Lvl -= 1; /* set to valid value */
            EMsg = TSLogg.getMsg("TSFleR03",Lvl); /* error-message */
            return(true); /* set 'end of data' and mark error */
          } /* Systemerror found */
          if(putInt(cTok.getKey().longValue(),cB,Pos,2)) return(true);
            /* set tokennumber and return if error */
          if(putInt(getFle(),cB,Pos,2)) return(true);
            /* set file-number and return if error */
          is = getItem(null); /* get optional label from line */
          if(is==null) return(true); /* no token -- error */
          mPos = Pos; /* save Postion of token-start */
          Pos += 4; /* skip field of length-value */
            /* length-value of token will be inserted later */
          if((i=is.length())>255) { /* invalid length-value */
            EMsg = TSLogg.getMsg("TSFleR04",i); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(i,cB,Pos,1)) return(true);
            /* set length-field of label and return if error */
          if(i>0) /* label was found -- process it */
            if(putString(is,cB,-Pos)) return(true); /* convert and set label */
             /* offset is marked -- no length-field is written */
          is = getItem("("); /* test if token starts correctly */
          if(is!=null) { /* no token for start -- return error */
            EMsg = TSLogg.getMsg("TSFleR05",is); /* error-message */
            return(true); } /* signal error and return */
          cCmd = cTok.getTyp() * 10; /* activate current token-command (type) */
          w1 = cTok.getVal1(); /* get first counter (may be changed!) */
          w2 = cTok.getVal2(); /* get second value (may be changed!) */
          nLF1 = false; /* following token is NOT required (default) */
          if(cCmd==230 || cCmd==110) nLF1 = true; /* and following token IS required */

// TSUtil.lg.Msg("Msg010","\t("+Lvl+")-->"+"Pos="+Pos+", zPos="+mPos+
//   ", TTyp="+cTok.getKey().toString()+
//   ", TNme='"+cTok.getName()+"', nLF1="+nLF1+", Act="+cCmd);

          continue; /* -while(Ix<DtaL[0].length())- use next Item to continue 'reduce' */
        } /* new ('embedded') token is available now */
      } /* possible 'embedded' token was found */

      /* is contains 'current item' from input-line */
      i = is.length(); /* set often used value */
      if(i==0) cCmd += 1; /* no remaining value exist -- goto next phase */

// TSUtil.lg.Msg("Msg010","\t("+Lvl+")-->"+"Pos="+Pos+", zPos="+mPos+
//   ", TTyp="+cTok.getKey().toString()+", TNme='"+cTok.getName()+
//   "', nLF1="+nLF1+", w1="+w1+", w2="+w2+", Act="+cCmd+", is='"+is+"'");
// TSUtil.lg.Log("Msg010","("+Lvl+")-->"+"buffer='"+(new String(cB,0,Pos))+"'");

      nLF1 = false; /* a following token is NOT required (default) */
      switch(cCmd) { /* execute next Item or part of it */
//          EMsg = TSLogg.getMsg("TSFleR06",cCmd); /* error-message */
//          return(true); /* signal error and return */
        case(221): /* extract UInt-Value (mixed2) */
          nLF1 = true; /* set required 'embedded' token as NEXT token */
        case( 10): /* extract UInt-Value (UInt) */
        case( 20): /* extract SInt-Value (SInt) */
        case( 51): /* extract UInt-Value (UInt4Float) */
        case( 60): /* extract SInt-Value (SIntFloat) */
        case( 90): /* extract UInt-Value (UIntFloat) */
        case(130): /* extract UInt-Value (UIntFloatHex) */
        case(110): /* extract UInt-Value after token (TokUIntFloat) */
        case(142): /* extract UInt-Value (HexFloatUInt) */
        case(150): /* extract UInt-Value (UIntFloat6) */
        case(161): /* extract UInt-Value (mixed1) */
        case(162): /* extract SInt-Value (mixed1) */
        case(163): /* extract UInt-Value (mixed1) */
        case(170): /* extract UInt-Value (UIntPlus) */
        case(180): /* extract UInt-Value (UIntPlusFloat) */
        case(191): /* extract SInt-Value (mixed3) */
        case(192): /* extract SInt-Value (mixed3) */
        case(201): /* extract SInt-Value (mixed4) */
        case(202): /* extract SInt-Value (mixed4) */
        case(223): /* extract SInt-Value (mixed2) */
          try {j = Long.parseLong(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR11",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set UInt-number and return if error */
          if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case( 30): /* extract Hex-Value (special version) */
          try {j = Long.parseLong(is,16); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR12",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set new number and return if error */
          if(cTok.isEmbed()) /* 'embeded' token is possible -- fixed number */
            if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case( 40): /* extract Float-Value */
        case(222): /* extract Float-Value (mixed2) */
        case(230): /* extract Float-Value (after token-processing) */
          try {f = Float.parseFloat(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR13",is); /* error-message */
            return(true); } /* signal error and return */
          j = Float.floatToRawIntBits(f); /* change it to Int-value */
          if(putInt(j,cB,Pos,4)) return(true);
            /* set Float as UInt-number and return if error */
          if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case( 50): /* init and extract UInt-Value (UInt4Float) */
          try {j = Long.parseLong(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR14",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set UInt-number and return if error */
          cCmd += 1; /* activate next phase or skip one phase */
          w2 = (int)(j * 4); /* compute number of following Float-Values */
          if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case( 52): /* extract Float-Value (UInt4Float) */
        case( 61): /* extract Float-Value (SIntFloat) */
        case( 91): /* extract Float-Value (UIntFloat) */
        case(111): /* extract Float-Value (TokUIntFloat) */
        case(131): /* extract Float-Value (UIntFloatHex) */
        case(141): /* extract Float-Value (HexFloatUInt) */
        case(151): /* extract Float-Value (UIntFloat6) */
          try {f = Float.parseFloat(is); /* convert value from String to Float */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR15",is); /* error-message */
            return(true); } /* signal error and return */
          j = Float.floatToRawIntBits(f); /* change it to Int-value */
          if(putInt(j,cB,Pos,4)) return(true);
            /* set Float as UInt-number and return if error */
          if(--w2<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case( 70): /* extract String-Value */
          if(putString(is,cB,Pos)) return(true); /* set String and return if error */
          cCmd += 1; /* activate next phase */
          break; /* current phase of command is done */
        case(194): /* extract Hex-Value (mixed3) */
        case(204): /* extract Hex-Value (mixed4) */
          nLF1 = true; /* set required 'embedded' token as NEXT token */
        case(100): /* extract Hex-value (HexUInt) */
        case(132): /* extract Hex-Value (UIntFloatHex) */
        case(140): /* extract Hex-Value (HexFloatUInt) */
        case(160): /* extract Hex-Value (mixed1) */
        case(164): /* extract Hex-Value (mixed1) */
        case(190): /* extract Hex-Value (mixed3) */
        case(193): /* extract Hex-Value (mixed3) */
        case(200): /* extract Hex-Value (mixed4) */
        case(203): /* extract Hex-Value (mixed4) */
        case(220): /* extract Hex-Value (mixed2) */
        case(250): /* extract Float-Value (HexFloat) */
          try {j = Long.parseLong(is,16); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR16",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set new number and return if error */
          if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case(101): /* extract UInt-value (HexUInt) */
        case(241): /* extract UInt-Value (StringInt) */
          try {j = Long.parseLong(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR17",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set UInt-number and return if error */
          if(--w2<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case(120): /* special case 'UIntUInt' */
          try {j = Long.parseLong(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR18",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set UInt-number and return if error */
          w1 *= j; /* compute number of following values */
          cCmd += w2 * 2; /* next phase depends on Type */
          break; /* current phase of command is done */
        case(122): /* extract UInt-Value (UIntUInt) */
        case(124): /* extract SInt-Value (UIntUInt) */
        case(165): /* extract SInt-Value (mixed1) */
        case(171): /* extract UInt-Value (UIntPlus) */
        case(195): /* extract UInt-Value (mixed3) */
        case(224): /* extract UInt-Value (mixed2) */
          /* ATTENTION: repeat until END OF DATA reached */
          try {j = Long.parseLong(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR19",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set UInt-number and return if error */
          break; /* current phase of command is done */
        case(126): /* extract HEX-Value (UIntUInt) */
        case(262): /* extract 'Buffer'-Value */
          /* ATTENTION: repeat until END OF DATA reached */
          try {j = Long.parseLong(is,16); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR20",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,4)) return(true); /* set new number and return if error */
          break; /* current phase of command is done */
        case(128): /* extract Float-Value (UIntUInt) */
        case(181): /* extract Float-Value (UIntPlusFloat) */
        case(205): /* extract Float-Value (mixed4) */
        case(251): /* extract Float-Value (HexFloat) */
        case(270): /* extract Float-Value (Floats) */
          /* ATTENTION: repeat until END OF DATA reached */
          try {f = Float.parseFloat(is); /* convert value from String to Float */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR21",is); /* error-message */
            return(true); } /* signal error and return */
          j = Float.floatToRawIntBits(f); /* change it to Int-value */
          if(putInt(j,cB,Pos,4)) return(true);
            /* set Float as UInt-number and return if error */
          break; /* current phase of command is done */
        case(240): /* extract 'StringInt'-Value */
          if(putString(is,cB,Pos)) return(true); /* set String and return if error */
          if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        case(260): /* extract 'Buffer'-Value */
          try {w1 = Integer.parseInt(is); /* convert value from String to Int */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR22",is); /* error-message */
            return(true); } /* signal error and return */
          cCmd += 1; /* activate next phase */
          break; /* current phase of command is done */
        case(261): /* extract 'Buffer'-Value */
          try {j = Long.parseLong(is); /* convert value from String to Int/UInt */
          } catch (NumberFormatException e) { /* error in number-definition */
            EMsg = TSLogg.getMsg("TSFleR23",is); /* error-message */
            return(true); } /* signal error and return */
          if(putInt(j,cB,Pos,1)) return(true); /* set Byte-number and return if error */
          if(--w1<1) cCmd += 1; /* activate next phase if all items processed */
          break; /* current phase of command is done */
        default: /* current command is completely processed */
          if(i!=0) { /* remaining item exists */
            EMsg = TSLogg.getMsg("TSFleR31",is); /* error-message */
            return(true); } /* signal error and return */
          is = getItem(")"); /* test if token terminates correctly */
          if(is!=null) { /* no terminator -- error */
            EMsg = TSLogg.getMsg("TSFleR32",is); /* error-message */
            return(true); } /* signal error and return */
          i = Pos - mPos - 4; /* compute length of current token */
          if(putInt(i,cB,mPos,4)) return(true); /* set Length-Field */
          Pos -= 4; /* do NOT advance pointer when setting length */
          Lvl -= 1; /* deactivate current iteration-level, Parent-token gets active  */
          if(Lvl<0) { /* underflow -- no further action */
            EMsg = TSLogg.getKey("TSFleR07"); /* error-message */
            return(true); } /* signal error and return */
          mPos = zPos[Lvl]; /* new buffer-end-position of current token */
          cTok = TokT[Lvl]; /* activate next/last token-definition */
          cCmd = CmdT[Lvl]; /* activate next/last token-command */
          w1 = cTok.getVal1(); /* restore first counter (may be changed!) */
          w2 = cTok.getVal2(); /* restore second value (may be changed!) */
      } /* execute current command */
    } /* process all Items from current line */

    return(false); /* no error */
  }

 /*=====================================================================
  * Method "toStr"
  * Build an String-representation of all values of this class
  ====================================================================*/
  protected String toStr() /* Create String-Representation of values */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* String for result */
   /*=================================================================*/
    erg = "TSFile{'"+Fle.getAbsolutePath()+"'-"+cMde+"-----}";
    return(erg); /* return result */
  }

 /*===================================================================*/
}

/*======================================================================
 * Class TSFileS -- Compress, decompress, reduce or expand of a S-file
 * ##TSFileS (Eyecatcher)
 =====================================================================*/
class TSFileS extends TSFile { /* (De-)Compress of S-files */
 /*-------------------------------------------------------------------*/
  private static HashMap        cmp;    /* Keymap for Compression */
  private static HashMap        dec;    /* Keymap for Decompression */
  private static final String   FKngU = /* Identifier for S-file */
    /* UNICODE-ID */            "SIMISA@@@@@@@@@@JINX0s1t______";
  private static final String   FKngB = "JINX0s1b______"; /* BIN-ID */
 /*=====================================================================
  * Constructor: (0) Create static definitions
  ====================================================================*/
  static { /* Perfom static initialisations */
    cmp = new HashMap(); /* create table for compress */
    dec = new HashMap(); /* create table for decompress */
    /* insert all definitions */
    tok = new TSFleTok(2,TSFile.K_float,3,6,false,"point");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(3,TSFile.K_float,3,6,false,"vector");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(5,TSFile.K_uint,1,1,true,"normals");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(6,TSFile.K_uintuint,2,K_uint,false,"normal_idxs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(7,TSFile.K_uint,1,1,true,"points");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(8,TSFile.K_float,2,6,false,"uv_point");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(9,TSFile.K_uint,1,1,true,"uv_points");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(10,TSFile.K_float,4,3,false,"colour");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(11,TSFile.K_uint,1,1,true,"colours");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(13,TSFile.K_string,1,0,false,"image");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(14,TSFile.K_uint,1,1,true,"images");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(15,TSFile.K_uintfloathex,2,1,false,"texture");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(16,TSFile.K_uint,1,1,true,"textures");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(17,TSFile.K_hexfloatuint,1,4,false,"light_material");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(18,TSFile.K_uint,1,1,true,"light_materials");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(19,TSFile.K_uintfloat6,1,3,false,"linear_key");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(20,TSFile.K_uintfloat6,1,9,false,"tcb_key");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(21,TSFile.K_uint,1,1,true,"linear_pos");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(22,TSFile.K_uint,1,1,true,"tcb_pos");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(23,TSFile.K_uintfloat6,1,4,false,"slerp_rot");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(24,TSFile.K_uint,1,1,true,"tcb_rot");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(25,TSFile.K_uint,1,1,true,"controllers");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(26,TSFile.K_none,0,0,true,"anim_node");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(27,TSFile.K_uint,1,1,true,"anim_nodes");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(28,TSFile.K_uint,2,1,true,"animation");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(29,TSFile.K_uint,1,1,true,"animations");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(31,TSFile.K_uint,1,1,true,"lod_controls");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(32,TSFile.K_none,0,0,true,"lod_control");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(33,TSFile.K_uintplusfloat,1,6,false,"distance_levels_header");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(34,TSFile.K_none,0,0,true,"distance_level_header");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(35,TSFile.K_float,1,6,false,"dlevel_selection");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(36,TSFile.K_uint,1,1,true,"distance_levels");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(37,TSFile.K_none,0,0,true,"distance_level");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(38,TSFile.K_uint,1,1,true,"sub_objects");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(39,TSFile.K_none,0,0,true,"sub_object");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(40,TSFile.K_mixed3,0,0,true,"sub_object_header");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(41,TSFile.K_uint,10,1,true,"geometry_info");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(42,TSFile.K_uint,1,1,true,"geometry_nodes");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(43,TSFile.K_uint,5,1,true,"geometry_node");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(44,TSFile.K_uintuint,1,K_sint,false,"geometry_node_map");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(45,TSFile.K_uint,3,1,false,"cullable_prims");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(46,TSFile.K_mixed1,0,1,false,"vtx_state");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(47,TSFile.K_uint,1,1,true,"vtx_states");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(48,TSFile.K_mixed4,1,1,true,"vertex");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(49,TSFile.K_uintuint,1,K_uint,false,"vertex_uvs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(50,TSFile.K_uint,1,1,true,"vertices");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(51,TSFile.K_uint,3,1,false,"vertex_set");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(52,TSFile.K_uint,1,1,true,"vertex_sets");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(53,TSFile.K_uint,1,1,true,"primitives");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(54,TSFile.K_mixed2,1,1,true,"prim_state");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(55,TSFile.K_uint,1,1,true,"prim_states");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(56,TSFile.K_uint,1,1,false,"prim_state_idx");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(60,TSFile.K_none,0,0,true,"indexed_trilist");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(61,TSFile.K_uintplus,1,1,false,"tex_idxs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(63,TSFile.K_uintuint,1,K_uint,false,"vertex_idxs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(64,TSFile.K_uintuint,1,K_hex,false,"flags");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(65,TSFile.K_float,12,3,false,"matrix");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(66,TSFile.K_uint,1,1,true,"matrices");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(67,TSFile.K_uintuint,1,K_sint,false,"hierarchy");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(68,TSFile.K_uint,1,1,true,"volumes");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(69,TSFile.K_tokfloat,1,6,true,"vol_sphere");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(70,TSFile.K_hex,2,1,false,"shape_header");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(71,TSFile.K_none,0,0,true,"shape");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(72,TSFile.K_uint,1,1,true,"shader_names");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(74,TSFile.K_uint,1,1,true,"texture_filter_names");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(76,TSFile.K_uint,1,1,true,"sort_vectors");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(79,TSFile.K_uint,1,1,true,"light_model_cfgs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(80,TSFile.K_hex,1,0,true,"light_model_cfg");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(81,TSFile.K_uint,1,1,true,"uv_ops");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(84,TSFile.K_uint,2,1,false,"uv_op_copy");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(104,TSFile.K_uintuint,1,K_uint,false,"subobject_shaders");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(105,TSFile.K_uintuint,1,K_uint,false,"subobject_light_cfgs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(125,TSFile.K_string,1,1,false,"named_filter_mode");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(129,TSFile.K_string,1,0,false,"named_shader");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(208,TSFile.K_uintfloat6,2,1,false,"uv_op_embossbump");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(209,TSFile.K_uintfloat6,2,2,false,"uv_op_nonuniformescale");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(210,TSFile.K_uint,1,1,false,"uv_op_reflectmap");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(211,TSFile.K_uint,1,1,false,"uv_op_reflectmapfull");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(212,TSFile.K_uint,2,1,false,"uv_op_share");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(213,TSFile.K_uint,1,1,false,"uv_op_specularmap");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(214,TSFile.K_uint,1,1,false,"uv_op_spheremap");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(215,TSFile.K_uint,1,1,false,"uv_op_spheremapfull");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(216,TSFile.K_uintfloat6,2,6,false,"uv_op_transform");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(217,TSFile.K_uintfloat6,2,1,false,"uv_op_uniformscale");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(218,TSFile.K_uint,3,1,false,"uv_op_user_nonuniformscale");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(219,TSFile.K_uint,2,1,false,"uv_op_user_transform");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(220,TSFile.K_uint,3,1,false,"uv_op_user_uniformscale");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(221,TSFile.K_uint,1,1,false,"uv_opcopy");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(222,TSFile.K_none,0,0,true,"indexed_line_list");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(223,TSFile.K_uint,2,1,false,"point_list");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
  }
 /*=====================================================================
  * Constructor: (1) Create Default-Entry
  ====================================================================*/
  protected TSFileS() /* create file-control for S-file (Dummy) */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(); /* initialize class TSFile */
  }

 /*=====================================================================
  * Constructor: (2) Create Standard-Entry
  ====================================================================*/
  protected TSFileS( /* create file-control for S-file */
   File dat) /* File-definition */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(dat,16); /* set Values using parent (--- Restriction ---) */
  }

 /*=====================================================================
  * Method "chkFKng"
  * Check File-Identifier for current type of file
  * if argument is 'null' the full key is provided (for write)
  ====================================================================*/
  protected String chkFKng( /* process file-identifier */
   String  K, /* current value or 'null' */
   boolean t) /* Type of header (true-> binary(16bytes)) */
   {
   /*-----------------------------------------------------------------*/
    String cK; /* current Key */
   /*=================================================================*/
    cK = t ? FKngB : FKngU; /* select requested file-IDentifier */
    if(K==null) return(cK); /* provide requested file-ID */
    /* check file-identifier */
    if(K.equals(cK)) return(null); /* identifier is OK */
    if(K.length()<8) return(TSLogg.getMsg("TSFle0S2",K)); /* message */
    return(TSLogg.getMsg("TSFle0S1",K)); /* provide error-message */
  }

 /*=====================================================================
  * Method "getFle"
  * Provide parent-class with File-ID within token
  ====================================================================*/
  protected int getFle() /* provide File-number for token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(0); /* S-Files use no. '0' */
  }

 /*=====================================================================
  * Method "getTok" (version 'expand')
  * Get current definition of token from table (key=number)
  ====================================================================*/
  protected TSFleTok getTok( /* read token-definition from table */
   int nr) /* Number is key of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    nr &= 65535; /* mask number */
    return((TSFleTok)dec.get(new Integer(nr))); /* provide Token */
  }

 /*=====================================================================
  * Method "getTok" (version 'reduce')
  * Get current definition of token from table (key=name)
  ====================================================================*/
  protected TSFleTok getTok( /* read token-definition from table */
   String nme) /* Name is key of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    nme = nme.toLowerCase(); /* lower chars (new String is build) */
    return((TSFleTok)cmp.get(nme)); /* provide Token */
  }

 /*=====================================================================
  * Method "toStr"
  * Build an String-representation of all values of this class
  ====================================================================*/
  protected String toStr() /* Create String-Representation of values */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* String for result */
   /*=================================================================*/
    erg = "TSFileS{"+super.toStr()+",'"+FKngU+"','"+FKngB+"'}";
    return(erg); /* return result */
  }

 /*===================================================================*/
}

/*======================================================================
 * Class TSFileT -- Reduce or expand of a T-file
 * T-Files are not compressed( or decompressed)
 * ##TSFileT (Eyecatcher)
 =====================================================================*/
class TSFileT extends TSFile { /* (De-)Compress of T-files */
 /*-------------------------------------------------------------------*/
  private static HashMap        cmp;    /* Keymap for Compression */
  private static HashMap        dec;    /* Keymap for Decompression */
  private static final String   FKngU = /* Identifier for T-file */
    /* UNICODE-ID */            "SIMISA@@@@@@@@@@JINX0txt______";
  private static final String   FKngB = "JINX0t6b______"; /* BIN-ID */
 /*=====================================================================
  * Constructor: (0) Create static definitions
  ====================================================================*/
  static { /* Perfom static initialisations */
    cmp = new HashMap(); /* create table for compress */
    dec = new HashMap(); /* create table for decompress */
    /* insert all definitions */
    tok = new TSFleTok(136,TSFile.K_none,0,0,true,"terrain");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(137,TSFile.K_float,1,6,false,"terrain_errthreshold_scale");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(138,TSFile.K_uint,1,1,false,"terrain_alwaysselect_maxdist");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(139,TSFile.K_none,0,0,true,"terrain_samples");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(140,TSFile.K_uint,1,1,false,"terrain_nsamples");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(141,TSFile.K_float,1,6,false,"terrain_sample_rotation");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(142,TSFile.K_float,1,3,false,"terrain_sample_floor");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(143,TSFile.K_float,1,8,false,"terrain_sample_scale");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(144,TSFile.K_float,1,4,false,"terrain_sample_size");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(145,TSFile.K_string,1,0,false,"terrain_sample_fbuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(146,TSFile.K_string,1,0,false,"terrain_sample_ybuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(147,TSFile.K_string,1,0,false,"terrain_sample_ebuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(148,TSFile.K_string,1,0,false,"terrain_sample_nbuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(149,TSFile.K_string,1,0,false,"terrain_sample_cbuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(150,TSFile.K_string,1,0,false,"terrain_sample_dbuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(151,TSFile.K_uint,1,1,true,"terrain_shaders");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(152,TSFile.K_string,1,0,true,"terrain_shader");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(153,TSFile.K_uint,1,1,true,"terrain_texslots");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(154,TSFile.K_stringuint,1,2,false,"terrain_texslot");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(155,TSFile.K_uint,1,1,true,"terrain_uvcalcs");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(156,TSFile.K_uintfloat,3,1,false,"terrain_uvcalc");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(157,TSFile.K_none,0,0,true,"terrain_patches");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(158,TSFile.K_uint,1,1,true,"terrain_patchsets");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(159,TSFile.K_none,0,0,true,"terrain_patchset");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(160,TSFile.K_uint,1,1,false,"terrain_patchset_distance");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(161,TSFile.K_uint,1,1,false,"terrain_patchset_npatches");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(163,TSFile.K_none,0,0,true,"terrain_patchset_patches");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(164,TSFile.K_hexfloat,1,6,false,"terrain_patchset_patch");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(251,TSFile.K_floats,4,3,true,"terrain_water_height_offset");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(281,TSFile.K_buffer,0,0,false,"terrain_sample_asbuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(282,TSFile.K_buffer,0,0,false,"terrain_sample_usbuffer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
  }
 /*=====================================================================
  * Constructor: (1) Create Default-Entry
  ====================================================================*/
  protected TSFileT() /* create file-control for T-file (Dummy) */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(); /* initialize class TSFile */
  }

 /*=====================================================================
  * Constructor: (2) Create Standard-Entry
  ====================================================================*/
  protected TSFileT( /* create file-control for T-file */
   File dat) /* File-definition */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(dat,32); /* set Values using parent (--- Restriction ---) */
  }

 /*=====================================================================
  * Method "chkFKng"
  * Check File-Identifier for current type of file
  * if argument is 'null' the full key is provided (for write)
  ====================================================================*/
  protected String chkFKng( /* process file-identifier */
   String  K, /* current value or 'null' */
   boolean t) /* Type of header (true-> binary(16bytes)) */
   {
   /*-----------------------------------------------------------------*/
    String cK; /* current Key */
   /*=================================================================*/
    cK = t ? FKngB : FKngU; /* select requested file-IDentifier */
    if(K==null) return(cK); /* provide requested file-ID */
    /* check file-identifier */
    if(K.equals(cK)) return(null); /* identifier is OK */
    return(TSLogg.getMsg("TSFle0T1",K)); /* provide error-message */
  }

 /*=====================================================================
  * Method "getFle"
  * Provide parent-class with File-ID within token
  ====================================================================*/
  protected int getFle() /* provide File-number for token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(0); /* T-Files use no. '0' */
  }

 /*=====================================================================
  * Method "getTok" (version 'expand')
  * Get current definition of token from table (key=number)
  ====================================================================*/
  protected TSFleTok getTok( /* read token-definition from table */
   int nr) /* Number is key of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    nr &= 65535; /* mask number */
    return((TSFleTok)dec.get(new Integer(nr))); /* provide Token */
  }

 /*=====================================================================
  * Method "getTok" (version 'reduce')
  * Get current definition of token from table (key=name)
  ====================================================================*/
  protected TSFleTok getTok( /* read token-definition from table */
   String nme) /* Name is key of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    nme = nme.toLowerCase(); /* lower chars (new String is build) */
    return((TSFleTok)cmp.get(nme)); /* provide Token */
  }

 /*=====================================================================
  * Method "open"
  * open file and create Reader or Writer
  * If 'input' is requested: check input-mode of file
  *  Output-Mode: 0 = UT - format
  *               1 = CB - format
  *               2 = CT - format (currently NOT supported) 
  *               3 = UB - format
  * Method returns an error-message or 'null' if no error
  * This method overwrites 'open'-function from TSFile-class
  ====================================================================*/
  protected String open( /* open file for T-file */
   boolean out, /* 'Output' requested (if 'true') */
   boolean cmp) /* 'Reduced' requested (if 'true') */ 
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(super.open(out,cmp?3:0)); /* use super-class function */
  }

 /*=====================================================================
  * Method "toStr"
  * Build an String-representation of all values of this class
  ====================================================================*/
  protected String toStr() /* Create String-Representation of values */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* String for result */
   /*=================================================================*/
    erg = "TSFileT{"+super.toStr()+",'"+FKngU+"','"+FKngB+"'}";
    return(erg); /* return result */
  }

 /*===================================================================*/
}

/*======================================================================
 * Class TSFileW -- Compress, decompress, reduce or expand of a W-file
 * ##TSFileW (Eyecatcher)
 =====================================================================*/
class TSFileW extends TSFile { /* (De-)Compress of W-files */
 /*-------------------------------------------------------------------*/
  private static HashMap        cmp;    /* Keymap for Compression */
  private static HashMap        dec;    /* Keymap for Decompression */
  private static final String   FKngU = /* Identifier for W-file */
    /* UNICODE-ID */            "SIMISA@@@@@@@@@@JINX0w0t______";
  private static final String   FKngB = "JINX0w0b______"; /* BIN-ID */
 /*=====================================================================
  * Constructor: (0) Create static definitions
  ====================================================================*/
  static { /* Perfom static initialisations */
    cmp = new HashMap(); /* create table for compress */
    dec = new HashMap(); /* create table for decompress */
    /* insert all definitions */
    tok = new TSFleTok(3,TSFile.K_none,0,0,true,"Static");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(5,TSFile.K_none,0,0,true,"TrackObj");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(6,TSFile.K_none,0,0,true,"Dyntrack");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(8,TSFile.K_none,0,0,true,"Forest");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(9,TSFile.K_none,0,0,true,"Telepole");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(11,TSFile.K_none,0,0,true,"CollideObject");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(17,TSFile.K_none,0,0,true,"Signal");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(56,TSFile.K_none,0,0,true,"Gantry");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(57,TSFile.K_none,0,0,true,"CarSpawner");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(59,TSFile.K_none,0,0,true,"Pickup");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(60,TSFile.K_none,0,0,true,"Platform");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(61,TSFile.K_none,0,0,true,"Siding");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(62,TSFile.K_none,0,0,true,"LevelCr");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(63,TSFile.K_none,0,0,true,"Transfer");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(64,TSFile.K_none,0,0,true,"Speedpost");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(65,TSFile.K_none,0,0,true,"Hazard");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(75,TSFile.K_none,0,0,true,"Tr_Worldfile");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(76,TSFile.K_uint,1,1,false,"Tr_Watermark");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(95,TSFile.K_string,1,0,false,"FileName");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(97,TSFile.K_float,3,4,false,"Position");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(98,TSFile.K_float,3,6,false,"Direction");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(99,TSFile.K_float,1,3,false,"MaxVisDistance");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(101,TSFile.K_sint,1,0,false,"StaticDetailLevel");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(104,TSFile.K_hex,1,0,false,"StaticFlags");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(105,TSFile.K_uint,1,1,false,"CollideFlags");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(106,TSFile.K_uint,1,1,false,"CollideFunction");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(108,TSFile.K_uint,1,1,false,"UiD");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(109,TSFile.K_none,0,0,true,"TrackSections");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(110,TSFile.K_tokuintfloat,1,2,true,"TrackSection");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(119,TSFile.K_uint,1,1,false,"SectionIdx");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(120,TSFile.K_uint,1,2,false,"SectionCurve");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(124,TSFile.K_sintfloat,2,3,false,"JNodePosN");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(158,TSFile.K_hex,1,0,false,"SignalSubObj");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(186,TSFile.K_uint,1,1,true,"SignalUnits");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(187,TSFile.K_uint,1,1,true,"SignalUnit");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(193,TSFile.K_float,1,3,false,"Elevation");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(200,TSFile.K_uint,1,1,false,"Population");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(201,TSFile.K_float,2,3,false,"Area");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(203,TSFile.K_float,2,3,false,"ScaleRange");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(204,TSFile.K_float,3,3,false,"StartPosition");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(205,TSFile.K_float,3,3,false,"EndPosition");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(206,TSFile.K_uint,1,1,false,"StartType");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(207,TSFile.K_uint,1,1,false,"EndType");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(277,TSFile.K_float,1,3,false,"StartDirection");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(278,TSFile.K_float,1,3,false,"EndDirection");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(279,TSFile.K_none,0,0,true,"ViewDbSphere");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(280,TSFile.K_float,1,3,false,"Radius");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(281,TSFile.K_uint,1,3,false,"NoDirLight");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(283,TSFile.K_uint,1,1,false,"VDbId");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(284,TSFile.K_uint,1,1,false,"VDbIdCount");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(298,TSFile.K_float,9,6,false,"Matrix3x3");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(622,TSFile.K_uint,2,1,false,"TrItemId");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(645,TSFile.K_float,4,6,false,"QDirection");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(791,TSFile.K_uint,1,1,false,"Config");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(807,TSFile.K_hex,1,0,false,"PlatformData");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(811,TSFile.K_float,2,3,false,"SpeedRange");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(812,TSFile.K_uint,2,1,false,"PickupType");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(813,TSFile.K_uintfloat,1,1,false,"PickupAnimData");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(814,TSFile.K_float,2,3,false,"PickupCapacity");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(816,TSFile.K_float,1,3,false,"CarFrequency");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(817,TSFile.K_float,1,3,false,"CarAvSpeed");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(820,TSFile.K_hex,1,0,false,"SidingData");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(822,TSFile.K_float,2,3,false,"LevelCrParameters");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(823,TSFile.K_hexuint,1,1,false,"LevelCrData");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(824,TSFile.K_float,3,3,false,"LevelCrTiming");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(831,TSFile.K_uint4float,1,6,false,"Speed_Sign_Shape");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(834,TSFile.K_string,1,0,false,"Speed_Digit_Tex");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(839,TSFile.K_float,3,2,false,"Speed_Text_Size");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(852,TSFile.K_float,1,3,false,"Width");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(853,TSFile.K_float,1,3,false,"Height");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(854,TSFile.K_string,1,0,false,"TreeTexture");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(855,TSFile.K_float,2,3,false,"TreeSize");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
    tok = new TSFleTok(1231,TSFile.K_float,1,3,false,"CrashProbability");
     cmp.put(tok.getName().toLowerCase(),tok); /* insert for compress */
     dec.put(tok.getKey(),tok); /* insert for decompress */
  }
 /*=====================================================================
  * Constructor: (1) Create Default-Entry
  ====================================================================*/
  protected TSFileW() /* create file-control for W-file (Dummy) */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(); /* initialize class TSFile */
  }

 /*=====================================================================
  * Constructor: (2) Create Standard-Entry
  ====================================================================*/
  protected TSFileW( /* create file-control for W-file */
   File dat) /* File-definition */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(dat,16); /* set Values using parent (--- Restriction ---) */
  }

 /*=====================================================================
  * Method "chkFKng"
  * Check File-Identifier for current type of file
  * if argument is 'null' the full key is provided (for write)
  ====================================================================*/
  protected String chkFKng( /* process file-identifier */
   String  K, /* current value or 'null' */
   boolean t) /* Type of header (true-> binary(16bytes)) */
   {
   /*-----------------------------------------------------------------*/
    String cK; /* current Key */
   /*=================================================================*/
    cK = t ? FKngB : FKngU; /* select requested file-IDentifier */
    if(K==null) return(cK); /* provide requested file-ID */
    /* check file-identifier */
    if(K.equals(cK)) return(null); /* identifier is OK */
    return(TSLogg.getMsg("TSFle0W1",K)); /* provide error-message */
  }

 /*=====================================================================
  * Method "getFle"
  * Provide parent-class with File-ID within token
  ====================================================================*/
  protected int getFle() /* provide File-number for token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(4); /* W-Files use no. '4' */
  }

 /*=====================================================================
  * Method "getTok" (version 'expand')
  * Get current definition of token from table (key=number)
  ====================================================================*/
  protected TSFleTok getTok( /* read token-definition from table */
   int nr) /* Number is key of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    nr &= 65535; /* mask number */
    return((TSFleTok)dec.get(new Integer(nr))); /* provide Token */
  }

 /*=====================================================================
  * Method "getTok" (version 'reduce')
  * Get current definition of token from table (key=name)
  ====================================================================*/
  protected TSFleTok getTok( /* read token-definition from table */
   String nme) /* Name is key of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    nme = nme.toLowerCase(); /* lower chars (new String is build) */
    return((TSFleTok)cmp.get(nme)); /* provide Token */
  }

 /*=====================================================================
  * Method "toStr"
  * Build an String-representation of all values of this class
  ====================================================================*/
  protected String toStr() /* Create String-Representation of values */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* String for result */
   /*=================================================================*/
    erg = "TSFileW{"+super.toStr()+",'"+FKngU+"','"+FKngB+"'}";
    return(erg); /* return result */
  }

 /*===================================================================*/
}

/*======================================================================
 * Class TSFileX -- Common access to 'normal' files
 *   This class provides access to 'normal' TS-Files in the same way as
 *     access to S-/T- or W-Files is used.
 *   Input: File-ID is read and checked due to Call-ID, specified.
 *   Output: Correct File-ID is added automaticly. 
 *   File-Sub-Type(input) is preserved -- class may be reused for output
 * ##TSFileX (Eyecatcher)
 =====================================================================*/
class TSFileX extends TSFile { /* Access of 'normal' Files */
 /*-------------------------------------------------------------------*/
  private static final String[][] FKng = { /* Identifier for files */
    /* Route-Def (.trk) [1] */  {"SIMISA@@@@@@@@@@JINX0r0t______",
    /* Route-Def (.trk) [2] */   "SIMISA@@@@@@@@@@JINX0r1t______"},
    /* tsection.dat(global) */  {"SIMISA@@@@@@@@@@JINX0F0t______"},
    /* tsection.dat(local) */   {"SIMISA@@@@@@@@@@JINX0T0t______"},
    /* (lo_)td_idx.dat */       {"SIMISA@@@@@@@@@@JINX0D0t______"},
    /* Terrain-Ctl(.td,.tdl) */ {"SIMISA@@@@@@@@@@JINX0d1b______"},
    /* Sound (.ws) */           {"SIMISA@@@@@@@@@@JINX0W0t______"},
    /* Track-DB (.tdb,.rdb) */  {"SIMISA@@@@@@@@@@JINX0T0t______"},
    /* TrItem-DB (.tit,.rit) */ {"SIMISA@@@@@@@@@@JINX0T0t______"},
    /* Signal (Sigcfg.dat)      {"SIMISA@@@@@@@@@@JINX0G0t______"},
    /* Activity (.act) */       {"SIMISA@@@@@@@@@@JINX0a0t______"},
    /* Path (.pat) */           {"SIMISA@@@@@@@@@@JINX0P0t______"},
    /* Services (.srv) */       {"SIMISA@@@@@@@@@@JINX0v0t______"},
    /* Traffic (.trf) */        {"SIMISA@@@@@@@@@@JINX0f0t______"}};
  private static final String[][] Types = { /* Call-ID for files */
    {"Trk"},{"gts"},{"lts"},{"idx"},{"td","tdl"},{"ws"},
    {"tdb","rdb","xdb"},{"tit","rit","xit"},{"Sig"},{"act"},
    {"pat"},{"srv"},{"trf"}};
 /*-------------------------------------------------------------------*/
  private int                   Typ;    /* current File-Type */
  private int                   ST;     /* current File-Sub-Type */
  private String                cID;    /* current Call-ID */
 /*=====================================================================
  * Constructor: (1) Create Default-Entry
  ====================================================================*/
  protected TSFileX() /* create file-control (Dummy) */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    super(); /* initialize class TSFile */
    cID = null; /* no Call-ID set */
    Typ = -1; /* no file-type set */
    ST  = 0;  /* use dummy-sub-type */
  }

 /*=====================================================================
  * Constructor: (2) Create Standard-Entry
  ====================================================================*/
  protected TSFileX( /* create file-control for file-access */
   String cid, /* Call-ID for File-Type */
   File   dat) /* File-definition */
   {
    super(dat,8); /* set Values using parent (--- Restriction ---) */
   /*-----------------------------------------------------------------*/
    int i,j; /* temp value */
   /*=================================================================*/
    cID = cid; /* store current Call-ID */
    Typ = -1;  /* preset current-File-Type */
    ST  = 0;   /* set standard sub-type */
    for(i=0;i<Types.length;i+=1) /* check all possible Call-ID's */
      for(j=0;j<Types[i].length;j+=1) /* check all possible Call-ID's */
      if(cid.equalsIgnoreCase(Types[i][j])) Typ = i; /* ID found */
  }

 /*=====================================================================
  * Method "chkFKng"
  * Check File-Identifier for current type of file
  * if argument is 'null' the full key is provided (for write)
  ====================================================================*/
  protected String chkFKng( /* process file-identifier */
   String  K, /* current value or 'null' */
   boolean t) /* Type of header (true-> binary(16bytes)) */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(t) return(K); /* answer with error-value */
    if(K==null) return(FKng[Typ][ST]); /* provide requested file-ID */

    /* check file-identifier */
    for(ST=0;ST<FKng[Typ].length;ST+=1) /* all available sub-types */
      if(K.equals(FKng[Typ][ST])) return(null); /* identifier is OK */
    ST = 0; /* reset dummy-sub-type */
    {String[] $={K,cID}; return(TSLogg.getMsg("TSFle0X1",$));}
  }

 /*=====================================================================
  * Method "getST"
  * provides current Filetype-sub-id
  ====================================================================*/
  protected int getST() /* provides current ID */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(ST); /* answer with current filetype-sub-id */
  }

 /*=====================================================================
  * Method "open"
  * open file and create Reader or Writer
  * If 'input' is requested: check input-mode of file
  *  Output-Mode: 0 = UT - format (plain read)
  *               1 = CB - format
  *               2 = CT - format (currently NOT supported) 
  *               3 = UB - format
  * Method returns an error-message or 'null' if no error
  * This method overwrites 'open'-function from TSFile-class
  * ONLY UT-format is used for this filetype!!!
  ====================================================================*/
  protected String open( /* open 'normal' text-file */
   boolean out) /* 'Output' requested (if 'true') */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Typ<0) return(TSLogg.getMsg("TSFle0X2",cID)); /* invalid Type */
    return(super.open(out,0)); /* use super-class function */
  }

 /*=====================================================================
  * Method "open"
  * this method-call blocks super-class function 'open'
  * Method returns an error-message in ANY case
  * This method overwrites 'open'-function from TSFile-class
  ====================================================================*/
  protected String open( /* open 'normal' text-file */
   boolean out, /* 'Output' requested (if 'true') */
   int     iom) /* requested 'Output' - Mode */ 
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(TSLogg.getKey("TSFle0X9")); /* answer with error */
  }

 /*=====================================================================
  * Method "setST"
  * Activate a filtype-Sub-ID for writing
  * Method provides an error-Message or 'null' if no error
  ====================================================================*/
  protected String setST( /* prvides current ID */
   int id) /* new sub-id */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Typ<0) return(TSLogg.getMsg("TSFle0X2",cID)); /* invalid Type */
    if(id<0 || id>=FKng[Typ].length) /* id has invalid range */
      return(TSLogg.getMsg("TSFle0X3",cID,id,FKng[Typ].length-1));
    ST = id; /* activate new ID */
    return(null); /* return: no error */
  }

 /*=====================================================================
  * Method "toStr"
  * Build an String-representation of all values of this class
  ====================================================================*/
  protected String toStr() /* Create String-Representation of values */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* String for result */
    int    i,j; /* temp value */
   /*=================================================================*/
    erg = "TSFileX{" + super.toStr() + "-" + FKng.length + "[";
    for(i=0;i<FKng.length;i+=1) { /* all File-Types */
      if(i>0) erg += ","; /* insert separator */
      erg += FKng[i].length + "["; /* number of subtypes */
      for(j=0;j<FKng[i].length;j+=1) { /* all sub-types */
        if(j>0) erg += ","; /* insert separator */
        erg += "'" + FKng[i][j] + "'"; /* add file-ID */
      } /* all sub-types */
      erg += "]"; /* close list */
    } /* all file-types */
    erg += "]-'" + cID + "'(" + Typ + ")-" + ST + "}";
    return(erg); /* return result */
  }

 /*===================================================================*/
}

/*======================================================================
 * Class TSFleTok -- Token used for reduce/expand of Files
 * ##TSFleTok (Eyecatcher)
 =====================================================================*/
class TSFleTok { /* Token-Definition */
 /*-------------------------------------------------------------------*/
  private int                   Id;     /* ID-number of Token */
  private int                   Typ;    /* Type of Token */
  private int                   W1;     /* first definition-value */
  private int                   W2;     /* second definition-value */
  private boolean               Emb;    /* 'embedded' Token ? */
  private String                Nme;    /* Name of Token */
 /*=====================================================================
  * Constructor: (1) Create Default-Entry
  ====================================================================*/
  protected TSFleTok() /* Create Dummy-Definition of Token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    Id  = 0;     /* set invalid ID */
    Typ = 0;     /* type says 'do nothing' */
    W1  = 0;     /* no definition-value */
    W2  = 0;     /* no definition-value */
    Emb = false; /* Token is NOT 'embedded' */
    Nme = null;  /* no name of token defined */
  }

 /*=====================================================================
  * Constructor: (2) Create Standard-Entry
  ====================================================================*/
  protected TSFleTok( /* create Token-Definition for files */
   int     id,  /* ID-Value of Token */
   int     typ, /* type of Token */
   int     w1,  /* first definition-value of Tokens */
   int     w2,  /* second definition-value of Tokens */
   boolean imb, /* Token embedded ? */
   String  nme) /* Token-name */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    Id  = id;  /* store provided values into token-definition */
    Typ = typ; /* set type */
    W1  = w1;  /* set first definition-value */
    W2  = w2;  /* set second definition-value */
    Emb = imb; /* include 'embedded' - Attribute */
    Nme = nme; /* set name of keyword */
  }

 /*=====================================================================
  * Method "getKey"
  * Provide current key as Integer-Object
  * (is also used for initialization)
  ====================================================================*/
  protected Integer getKey() /* provide current key */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Nme==null) return(null); /* Values are NOT valid -- default */
    return(new Integer(Id)); /* return ID as Integer-object */
  }

 /*=====================================================================
  * Method "getName"
  * Anwer request for current name of token
  * (is also used for initialization)
  ====================================================================*/
  protected String getName() /* request for name of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    return(Nme); /* return name of token or 'null' if error */
  }

 /*=====================================================================
  * Method "getTyp"
  * Answer request for current type of token
  ====================================================================*/
  protected int getTyp() /* request current type of token */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Nme==null) return(0); /* Values are NOT valid -- default */
    return(Typ); /* returns type or 0->'none' */
  }

 /*=====================================================================
  * Method "getVal1"
  * Provide first definition-value of Token
  ====================================================================*/
  protected int getVal1() /* request for first counter */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Nme==null) return(0); /* Values are NOT valid -- default */
    return(W1); /* provide counter or 0->'none' */
  }

 /*=====================================================================
  * Method "getVal2"
  * Provide second definition-value of Token
  ====================================================================*/
  protected int getVal2() /* provide second definition-value */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Nme==null) return(0); /* Values are NOT valid -- default */
    return(W2); /* provide value or 0->'none' */
  }

 /*=====================================================================
  * Method "isEmbed"
  * Returns Info if Token is 'embedded'
  ====================================================================*/
  protected boolean isEmbed() /* Token 'embedded' ? */
   {
   /*-----------------------------------------------------------------*/
                                            /* no local definitions */
   /*=================================================================*/
    if(Nme==null) return(false); /* Values are NOT valid -- default */
    return(Emb); /* return value */
  }

 /*=====================================================================
  * Method "toStr"
  * Build an String-representation of all values of this class
  ====================================================================*/
  protected String toStr() /* Create String-Representation of values */
   {
   /*-----------------------------------------------------------------*/
    String erg; /* String for result */
    int    i,j; /* temp. variable(s) */
   /*=================================================================*/
    erg = "TSFleTok{"+Id+",'"+Nme+"',"+Typ+","+Emb+"}";
    return(erg); /* return result */
  }

 /*===================================================================*/
}
