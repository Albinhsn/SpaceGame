package se.liu.albhe576.project.Parsers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TrueType {
    private TableDirectory tableDirectory;
    private TableRecord[] records;
    private final HashMap<String, Table> tables;
    private static int parseIntFromByteArray(byte [] data, int idx){
        return ByteBuffer.wrap(data, idx, 4).getInt();
    }
    private static short parseShortFromByteArray(byte [] data, int idx){
        return ByteBuffer.wrap(data, idx, 2).getShort();
    }
    private static long parseLongFromByteArray(byte [] data, int idx){
        return ByteBuffer.wrap(data, idx, 8).getLong();
    }
    private static float parseFloatFromByteArray(byte [] data, int idx) {
        return ByteBuffer.wrap(data, idx, 4).getFloat();
    }

    public static TrueType parseTrueTypeFromFile(String fileLocation) throws IOException {
        TrueType trueType = new TrueType();
        byte [] fileData = Files.readAllBytes(Path.of(fileLocation));
        trueType.tableDirectory = new TableDirectory(fileData,  0);
        int idx = TableDirectory.size;

        System.out.println(trueType.tableDirectory);

        trueType.records = new TableRecord[trueType.tableDirectory.numTables];

        TableRecord locaRecord = null;
        TableRecord hmtxRecord = null;
        TableRecord glyfRecord = null;

        for(int i = 0; i < trueType.tableDirectory.numTables; i++, idx += TableRecord.size){
            trueType.records[i] = new TableRecord(fileData, idx);
            System.out.println(trueType.records[i]);

            TableRecord record = trueType.records[i];
            Table table;
            switch(record.tag){
                case "cmap":{
                    table =  new TableCMAP(fileData, record);
                    break;
                }
                case "OS/2":{
                    table =  new TableOSSlash2(fileData, record);
                    break;
                }
                case "hhea":{
                    table =  new TableHHEA(fileData, record);
                    break;
                }
                case "glyf":{
                    glyfRecord = record;
                    continue;
                }
                case "head":{
                    table = new TableHEAD(fileData, record);
                    break;
                }
                case "hmtx":{
                    hmtxRecord = record;
                    continue;
                }
                case "loca":{
                    locaRecord = record;
                    continue;
                }
                case "maxp":{
                    table = new TableMAXP(fileData, record);
                    break;
                }
                case "name":{
                    table = new TableNAME(fileData, record);
                    break;
                }
                case "post":{
                    table = new TablePOST(fileData, record);
                    break;
                }
                default:{
                    System.out.printf("Unknown record '%s'\n", record.tag);
                    continue;
                }
            }
            System.out.println(table);
            trueType.tables.put(record.tag, table);
        }
        assert(locaRecord != null);
        assert(hmtxRecord != null);
        assert(glyfRecord != null);
        trueType.tables.put("glyf", new TableGLYF(fileData, glyfRecord, trueType));
        trueType.tables.put("loca", new TableLOCA(fileData, locaRecord, trueType));
        trueType.tables.put("hmtx", new TableHMTX(fileData, locaRecord, trueType));
        System.out.println(trueType.tables.get("loca"));
        System.out.println(trueType.tables.get("hmtx"));

        return trueType;
    }

    public static void main(String[] args) throws IOException {
        TrueType trueType = TrueType.parseTrueTypeFromFile("./resources/Font/kenvector_future.ttf");
    }
    static class EncodingRecord{
        @Override
        public String toString() {

            return String.format("\t\t\tplatformID:%d", this.platformID) +
                    String.format("\t\t\tencodingID:%d", this.encodingID) +
                    String.format("\t\t\tsubtableOffset:%d", this.subtableOffset);
        }

        short platformID;
        short encodingID;
        int subtableOffset;
        public EncodingRecord(byte [] data, int idx){
            this.platformID     = parseShortFromByteArray(data, idx + 0);
            this.encodingID     = parseShortFromByteArray(data, idx + 2);
            this.subtableOffset = parseIntFromByteArray(data, idx + 4);

        }

    }
    static class TableCMAP extends Table{
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CMAP\n\t");
            stringBuilder.append(String.format("version:'%s'\n\t", VERSION_TABLE.get((int)this.version)));
            stringBuilder.append(String.format("numTables:%d", this.numTables));
            for(int i = 0; i < this.numTables; i++){
                stringBuilder.append("\n\t\t").append(this.encodingRecords[i]);
            }
            return stringBuilder.toString();
        }

        private final Map<Integer, String> VERSION_TABLE = new HashMap<>() {
            {
                put(0, "Unicode");
                put(1, "Macintosh");
                put(2, "ISO");
                put(3, "Window");
                put(4, "Custom");
            }
        };
        short version;
        short numTables;
        EncodingRecord[] encodingRecords;

        public TableCMAP(byte [] data, TableRecord record){
            int offset = record.offset;
            this.version = parseShortFromByteArray(data, offset + 0);
            this.numTables = parseShortFromByteArray(data, offset + 4);

            this.encodingRecords = new EncodingRecord[this.numTables];
            for(int i = 0; i < this.numTables; i++, offset += 8){
                this.encodingRecords[i] = new EncodingRecord(data, offset);
            }
        }
    }

    static class TablePOST extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TablePOST:");
            builder.append(String.format("\n\tversion:0x%x", this.version));
            builder.append(String.format("\n\titalicAngle:%d", this.italicAngle));
            builder.append(String.format("\n\tunderlinePosition:%d", this.underlinePosition));
            builder.append(String.format("\n\tunderlineThickness:%d", this.underlineThickness));
            builder.append(String.format("\n\tisFixedPitch:%d", this.isFixedPitch));
            builder.append(String.format("\n\tminMemType42:%d", this.minMemType42));
            builder.append(String.format("\n\tmaxMemType42:%d", this.maxMemType42));
            builder.append(String.format("\n\tminMemType1:%d", this.minMemType1));
            builder.append(String.format("\n\tmaxMemType1:%d", this.maxMemType1));
            if(this.version == 0x00020000 || this.version == 0x00025000){
                builder.append(String.format("\n\tnumGlyphs:%d", this.numGlyphs));
                builder.append("\n\tglyphNameIndex:");
                for(int i = 0; i < this.numGlyphs;i++){
                    builder.append(String.format("\n\t\t%d", this.glyphNameIndex[i]));
                }

            }

            return builder.toString();
        }

        int version;
        // fixed 16.16
        int italicAngle;
        short underlinePosition;
        short underlineThickness;
        int isFixedPitch;
        int minMemType42;
        int maxMemType42;
        int minMemType1;
        int maxMemType1;

        // Only in 2.0 and 2.5
        short numGlyphs;
        short []glyphNameIndex;
        byte []stringData;


        public TablePOST(byte [] data, TableRecord record){
            int offset = record.offset;
            this.version            = parseIntFromByteArray(data, offset + 0);
            this.italicAngle        = parseIntFromByteArray(data, offset + 4);
            this.underlinePosition  = parseShortFromByteArray(data, offset + 8);
            this.underlineThickness = parseShortFromByteArray(data, offset + 10);
            this.isFixedPitch       = parseIntFromByteArray(data, offset + 12);
            this.minMemType42       = parseIntFromByteArray(data, offset + 16);
            this.maxMemType42       = parseIntFromByteArray(data, offset + 20);
            this.minMemType1        = parseIntFromByteArray(data, offset + 24);
            this.maxMemType1        = parseIntFromByteArray(data, offset + 28);

            if(this.version == 0x00020000 || this.version == 0x00025000){
                this.numGlyphs = parseShortFromByteArray(data, offset + 28);
                this.glyphNameIndex = new short[this.numGlyphs];
                for(int i = 0; i < this.numGlyphs; i++){
                    this.glyphNameIndex[i] = parseShortFromByteArray(data, offset + 30 + i  * 4);
                }
                // Variable?
            }
        }
    }
    static class TableNAME extends Table{

        static class NameRecord{
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("\n\t\tplatformID:%d", this.platformID));
                builder.append(String.format("\n\t\tencodingID:%d", this.encodingID));
                builder.append(String.format("\n\t\tlanguageID:%d, 0x%x", this.languageID, this.languageID));
                builder.append(String.format("\n\t\tnameID:%d", this.nameID));
                builder.append(String.format("\n\t\tlength:%d", this.length));
                builder.append(String.format("\n\t\tstringOffset:%d", this.stringOffset));

                return builder.toString();
            }

            public static final int size = 12;
            short platformID;
            short encodingID;
            short languageID;
            short nameID;
            short length;
            short stringOffset;
            public NameRecord(byte [] data, int offset){
                this.platformID     = parseShortFromByteArray(data, offset);
                this.encodingID     = parseShortFromByteArray(data, offset + 2);
                this.languageID     = parseShortFromByteArray(data, offset + 4);
                this.nameID         = parseShortFromByteArray(data, offset + 6);
                this.length         = parseShortFromByteArray(data, offset + 8);
                this.stringOffset   = parseShortFromByteArray(data, offset + 10);
            }
        }
        static class LangTagRecord{
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("\n\t\tlength:%d", this.length));
                builder.append(String.format("\n\t\tlangTagOffset:%d", this.langTagOffset));

                return builder.toString();
            }

            short length;
            short langTagOffset;
            public LangTagRecord(short length, short langTagOffset){
                this.length = length;
                this.langTagOffset = langTagOffset;
            }
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableNAME:");
            builder.append(String.format("\n\tversion:%d", this.version));
            builder.append(String.format("\n\tcount:%d", this.count));
            builder.append(String.format("\n\tstorageOffset:%d", this.storageOffset));
            builder.append("\n\tnameRecords:");
            for(int i = 0; i < this.count; i++){
                builder.append(String.format("\n\t%d:", i));
                builder.append(this.nameRecords[i]);
            }
            builder.append(String.format("\n\tlangTagCount:%d", this.langTagCount));
            builder.append("\n\tlangTagRecord:");
            for(int i = 0; i < this.langTagCount; i++){
                builder.append(String.format("\n\t%d:", i));
                builder.append(this.langTagRecord[i]);
            }

            return builder.toString();
        }
        short version;
        short count;
        short storageOffset;
        NameRecord[] nameRecords;
        short langTagCount;
        LangTagRecord[] langTagRecord;

        public TableNAME(byte [] data, TableRecord record){
            int offset = record.offset;
            this.version = parseShortFromByteArray(data, offset);
            this.count = parseShortFromByteArray(data, offset + 2);
            this.storageOffset = parseShortFromByteArray(data, offset + 4);
            this.nameRecords = new NameRecord[this.count];

            offset += 6;
            for(int i = 0; i < this.count; i++, offset += NameRecord.size){
                this.nameRecords[i] = new NameRecord(data, offset);
            }

            if(this.version == 1){
                this.langTagCount = parseShortFromByteArray(data, offset + 2);
                this.langTagRecord = new LangTagRecord[this.langTagCount];

                offset += 2;
                for(int i = 0; i < this.langTagCount; i++, offset += 4){
                    short l = parseShortFromByteArray(data, offset);
                    short lto = parseShortFromByteArray(data, offset + 2);
                    this.langTagRecord[i] = new LangTagRecord(l, lto);
                }

            }else{
                assert(this.version == 0);
            }
        }
    }
    static class TableMAXP extends Table{

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableMAXP:");
            builder.append(String.format("\n\tversion:0x%x", this.version));
            builder.append(String.format("\n\tnumGlyphs:%d", this.numGlyphs));
            if(this.version == 0x00010000){
                builder.append(String.format("\n\tmaxPoints:%d", this.maxPoints));
                builder.append(String.format("\n\tmaxContours:%d", this.maxContours));
                builder.append(String.format("\n\tmaxCompositePoints:%d", this.maxCompositePoints));
                builder.append(String.format("\n\tmaxCompositeContours:%d", this.maxCompositeContours));
                builder.append(String.format("\n\tmaxZones:%d", this.maxZones));
                builder.append(String.format("\n\tmaxTwilightPoints:%d", this.maxTwilightPoints));
                builder.append(String.format("\n\tmaxStorage:%d", this.maxStorage));
                builder.append(String.format("\n\tmaxFunctionDefs:%d", this.maxFunctionDefs));
                builder.append(String.format("\n\tmaxInstructionDefs:%d", this.maxInstructionDefs));
                builder.append(String.format("\n\tmaxStackElements:%d", this.maxStackElements));
                builder.append(String.format("\n\tmaxSizeOfInstructions:%d", this.maxSizeOfInstructions));
                builder.append(String.format("\n\tmaxComponentElements:%d", this.maxComponentElements));
                builder.append(String.format("\n\tmaxComponentDepth:%d", this.maxComponentDepth));
            }
            return builder.toString();
        }

        int version;
        short numGlyphs;
        short maxPoints;
        short maxContours;
        short maxCompositePoints;
        short maxCompositeContours;
        short maxZones;
        short maxTwilightPoints;
        short maxStorage;
        short maxFunctionDefs;
        short maxInstructionDefs;
        short maxStackElements;
        short maxSizeOfInstructions;
        short maxComponentElements;
        short maxComponentDepth;
        public TableMAXP(byte [] data, TableRecord record){
            int offset = record.offset;
            this.version = parseIntFromByteArray(data, offset);
            this.numGlyphs = parseShortFromByteArray(data, offset + 4);

            if(this.version == 0x00010000){
                this.maxPoints = parseShortFromByteArray(data, offset + 6);
                this.maxContours = parseShortFromByteArray(data, offset + 8);
                this.maxCompositePoints = parseShortFromByteArray(data, offset + 10);
                this.maxCompositeContours = parseShortFromByteArray(data, offset + 12);
                this.maxZones = parseShortFromByteArray(data, offset + 14);
                this.maxTwilightPoints = parseShortFromByteArray(data, offset + 16);
                this.maxStorage = parseShortFromByteArray(data, offset + 18);
                this.maxFunctionDefs = parseShortFromByteArray(data, offset + 20);
                this.maxInstructionDefs = parseShortFromByteArray(data, offset + 22);
                this.maxStackElements = parseShortFromByteArray(data, offset + 24);
                this.maxSizeOfInstructions = parseShortFromByteArray(data, offset + 26);
                this.maxComponentElements = parseShortFromByteArray(data, offset + 28);
                this.maxComponentDepth = parseShortFromByteArray(data, offset + 30);
            }else{
                assert(this.version == 0x00005000);
            }
        }
    }
    static class TableLOCA extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableLOCA:");
            if(this.shortVersion){
                builder.append(String.format("\n\tOffsets:%d", this.offsetUint16.length));
            }else{
                builder.append(String.format("\n\t\tOffset:%d", this.offsetUint32.length));
            }
            return builder.toString();
        }

        boolean shortVersion;
        short []offsetUint16;
        int []offsetUint32;
        public TableLOCA(byte [] data, TableRecord record, TrueType trueType){
            TableHEAD head = (TableHEAD) trueType.tables.get("head");
            TableMAXP maxp = (TableMAXP) trueType.tables.get("maxp");

            int n = maxp.numGlyphs;
            int offset = record.offset;
            if(head.indexToLocFormat == 0){
                shortVersion = true;
                this.offsetUint16 = new short[n];
                for(int i = 0; i < n; i++){
                    this.offsetUint16[i] = parseShortFromByteArray(data, offset + i * 2);
                }

            }else{
                assert(head.indexToLocFormat == 1);
                this.offsetUint32 = new int[n];
                shortVersion = false;
                for(int i = 0; i < n; i++){
                    this.offsetUint32[i] = parseIntFromByteArray(data, offset + i * 4);
                }
            }
        }
    }
    static class TableHMTX extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableHMTX");
            builder.append(String.format("\n\thMetrics:%d", this.hMetrics.length));
            builder.append(String.format("\n\tleftSideBearings:%d", this.leftSideBearings.length));

            return builder.toString();
        }

        // depends on numberOfHMetrics
        LongHOrMetric[] hMetrics;
        // depends on (numGlyphs - numberOfHMetrics)
        short []leftSideBearings;
        static class LongHOrMetric{
            @Override
            public String toString() {
                return String.format("%d %d", this.advanceWidth, this.lsb);
            }

            short advanceWidth;
            short lsb;
            public LongHOrMetric(short aw, short l){
                this.advanceWidth   = aw;
                this.lsb            = l;
            }

        }
        public TableHMTX(byte [] data, TableRecord record, TrueType trueType){
            TableHHEA hhea = (TableHHEA) trueType.tables.get("hhea");
            TableMAXP maxp = (TableMAXP) trueType.tables.get("maxp");

            int numberOfHMetrics = hhea.numberOfHMetrics;
            int numGlyphs = maxp.numGlyphs;

            int offset = record.offset;

            this.hMetrics = new LongHOrMetric[numberOfHMetrics];
            for(int i = 0; i < numberOfHMetrics; i++){
                short advanceWidth = parseShortFromByteArray(data, offset + i * 4);
                short lsb = parseShortFromByteArray(data, offset + i * 4 + 2);
                this.hMetrics[i] = new LongHOrMetric(advanceWidth, lsb);
            }

            offset += numberOfHMetrics * 4;
            this.leftSideBearings = new short[numGlyphs - numberOfHMetrics];
            for(int i = 0; i < numGlyphs - numberOfHMetrics; i++){
                this.leftSideBearings[i] = parseShortFromByteArray(data, offset + i * 2);
            }
        }
    }
    static class TableHHEA extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableHHEA:");
            builder.append(String.format("\n\tmajorVersion:%d", this.majorVersion));
            builder.append(String.format("\n\tminorVersion:%d", this.minorVersion));
            builder.append(String.format("\n\tascender:%d", this.ascender));
            builder.append(String.format("\n\tdescender:%d", this.descender));
            builder.append(String.format("\n\tlinegap:%d", this.lineGap));
            builder.append(String.format("\n\tadvanceWidthMax:%d", this.advanceWidthMax));
            builder.append(String.format("\n\tminLeftSideBearing:%d", this.minLeftSideBearing));
            builder.append(String.format("\n\tminRightSideBearing:%d", this.minRightSideBearing));
            builder.append(String.format("\n\txMaxExtent:%d", this.xMaxExtent));
            builder.append(String.format("\n\tcaretSlopeRise:%d", this.caretSlopeRise));
            builder.append(String.format("\n\tcaretSlopeRun:%d", this.caretSlopeRun));
            builder.append(String.format("\n\tcaretOffset:%d", this.caretOffset));
            builder.append(String.format("\n\trevserved0:%d", this.revserved0));
            builder.append(String.format("\n\trevserved1:%d", this.revserved1));
            builder.append(String.format("\n\trevserved2:%d", this.revserved2));
            builder.append(String.format("\n\trevserved3:%d", this.revserved3));
            builder.append(String.format("\n\tmetricDataFormat:%d", this.metricDataFormat));
            builder.append(String.format("\n\tnumberOfHMetrics:%d", this.numberOfHMetrics));

            return builder.toString();
        }

        short majorVersion;
        short minorVersion;
        short ascender;
        short descender;
        short lineGap;
        short advanceWidthMax;
        short minLeftSideBearing;
        short minRightSideBearing;
        short xMaxExtent;
        short caretSlopeRise;
        short caretSlopeRun;
        short caretOffset;
        short revserved0;
        short revserved1;
        short revserved2;
        short revserved3;
        short metricDataFormat;
        short numberOfHMetrics;
        public TableHHEA(byte [] data, TableRecord record){
            int offset = record.offset;
            this.majorVersion           = parseShortFromByteArray(data, offset + 0);
            this.minorVersion           = parseShortFromByteArray(data, offset + 2);
            this.ascender               = parseShortFromByteArray(data, offset + 4);
            this.descender              = parseShortFromByteArray(data, offset + 6);
            this.lineGap                = parseShortFromByteArray(data, offset + 8);
            this.advanceWidthMax        = parseShortFromByteArray(data, offset + 10);
            this.minLeftSideBearing     = parseShortFromByteArray(data, offset + 12);
            this.minRightSideBearing    = parseShortFromByteArray(data, offset + 14);
            this.xMaxExtent             = parseShortFromByteArray(data, offset + 16);
            this.caretSlopeRise         = parseShortFromByteArray(data, offset + 18);
            this.caretSlopeRun          = parseShortFromByteArray(data, offset + 20);
            this.caretOffset            = parseShortFromByteArray(data, offset + 22);
            this.revserved0             = parseShortFromByteArray(data, offset + 24);
            this.revserved1             = parseShortFromByteArray(data, offset + 26);
            this.revserved2             = parseShortFromByteArray(data, offset + 28);
            this.revserved3             = parseShortFromByteArray(data, offset + 30);
            this.metricDataFormat       = parseShortFromByteArray(data, offset + 32);
            this.numberOfHMetrics       = parseShortFromByteArray(data, offset + 34);

        }
    }
    static class TableHEAD extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableHEAD:");
            builder.append(String.format("\n\tmajorVersion:%d", this.majorVersion));
            builder.append(String.format("\n\tminorVersion:%d", this.minorVersion));
            builder.append(String.format("\n\tfontRevision:%d", this.fontRevision));
            builder.append(String.format("\n\tchecksumAdjustment:%d", this.checksumAdjustment));
            builder.append(String.format("\n\tmagicnumber:%d", this.magicNumber));
            builder.append(String.format("\n\tflags:%d", this.flags));
            builder.append(String.format("\n\tunitsPerEm:%d", this.unitsPerEm));
            builder.append(String.format("\n\tcreated:%d", this.created));
            builder.append(String.format("\n\tmodified:%d", this.modified));
            builder.append(String.format("\n\txMin:%d", this.xMin));
            builder.append(String.format("\n\tyMin:%d", this.yMin));
            builder.append(String.format("\n\txMax:%d", this.xMax));
            builder.append(String.format("\n\tyMax:%d", this.yMax));
            builder.append(String.format("\n\tmacStyle:%d", this.macStyle));
            builder.append(String.format("\n\tlowestRecPPEM:%d", this.lowestRecPPEM));
            builder.append(String.format("\n\tfontDirectionHint:%d", this.fontDirectionHint));
            builder.append(String.format("\n\tindexToLocFormat:%d", this.indexToLocFormat));
            builder.append(String.format("\n\tglyphDataFormat:%d", this.glyphDataFormat));

            return builder.toString();
        }

        short majorVersion;
        short minorVersion;
        // fixed 16.16
        int fontRevision;
        int checksumAdjustment;
        int magicNumber;
        short flags;
        short unitsPerEm;
        long created;
        long modified;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        short macStyle;
        short lowestRecPPEM;
        short fontDirectionHint;
        short indexToLocFormat;
        short glyphDataFormat;

        public TableHEAD(byte [] data, TableRecord record){
            int offset = record.offset;
            this.majorVersion       = parseShortFromByteArray(data, offset + 0);
            this.minorVersion       = parseShortFromByteArray(data, offset + 2);
            this.fontRevision       = parseIntFromByteArray(data, offset + 4);
            this.checksumAdjustment = parseIntFromByteArray(data, offset + 8);
            this.magicNumber        = parseIntFromByteArray(data, offset + 12);
            this.flags              = parseShortFromByteArray(data, offset + 16);
            this.unitsPerEm         = parseShortFromByteArray(data, offset + 18);
            this.created            = parseLongFromByteArray(data, offset + 20);
            this.modified           = parseLongFromByteArray(data, offset + 28);
            this.xMin               = parseShortFromByteArray(data, offset + 36);
            this.yMin               = parseShortFromByteArray(data, offset + 38);
            this.xMax               = parseShortFromByteArray(data, offset + 40);
            this.yMax               = parseShortFromByteArray(data, offset + 42);
            this.macStyle           = parseShortFromByteArray(data, offset + 44);
            this.lowestRecPPEM      = parseShortFromByteArray(data, offset + 46);
            this.fontDirectionHint  = parseShortFromByteArray(data, offset + 48);
            this.indexToLocFormat   = parseShortFromByteArray(data, offset + 50);
            this.glyphDataFormat    = parseShortFromByteArray(data, offset + 52);

        }
    }
    static class TableGLYF extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableGLYF:");
            builder.append(String.format("\n\tnumberOfContours:%d", this.numberOfContours));
            builder.append(String.format("\n\txMin:%d", this.xMin));
            builder.append(String.format("\n\tyMin:%d", this.yMin));
            builder.append(String.format("\n\txMax:%d", this.xMax));
            builder.append(String.format("\n\tyMax:%d", this.yMax));
            builder.append("\n\tendPtsOfContours:");
            for(int i = 0; i < this.numberOfContours; i++){
                builder.append(String.format("\n\t\t%d:%d", i, this.endPtsOfContours[i]));
            }
            builder.append("\n\tinstructions:");
            for(int i = 0; i < this.instructionLength; i++){
                builder.append(String.format("\n\t\t%d:%d", i, this.instructions[i]));
            }


            return builder.toString();
        }

        short numberOfContours;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        short []endPtsOfContours;
        short instructionLength;
        byte [] instructions;
        byte[] flags;
        byte[] xCoordinates;
        byte[] yCoordinates;

        public TableGLYF(byte [] data, TableRecord record, TrueType trueType){
            int offset = record.offset;
            this.numberOfContours   = parseShortFromByteArray(data, offset + 0);
            this.xMin               = parseShortFromByteArray(data, offset + 2);
            this.yMin               = parseShortFromByteArray(data, offset + 4);
            this.xMax               = parseShortFromByteArray(data, offset + 6);
            this.yMax               = parseShortFromByteArray(data, offset + 8);

            if(this.numberOfContours >= 0){
                // Simple Glyph Table
                this.endPtsOfContours = new short[this.numberOfContours];
                offset += 10;
                for(int i = 0; i < this.numberOfContours; i++, offset += 2){
                    this.endPtsOfContours[i] = parseShortFromByteArray(data, offset);
                }

                this.instructionLength = parseShortFromByteArray(data, offset);
                offset += 2;

                TableHEAD head = (TableHEAD) trueType.tables.get("head");
                int unitsPerEm = head.unitsPerEm;
                System.out.println(unitsPerEm);

                this.instructions = new byte[this.instructionLength];
                for(int i = 0; i < this.instructionLength; i++, offset += 1){
                   this.instructions[i] = data[offset];
                }

                int repeatsFor = this.endPtsOfContours[this.numberOfContours - 1];

                this.flags = new byte[repeatsFor];
                for(int i = 0; i < repeatsFor; i++, offset += 1){
                    this.flags[i] = data[offset];
                }

                this.xCoordinates = new byte[repeatsFor];
                for(int i = 0; i < repeatsFor; i++, offset += 1){
                    this.xCoordinates[i] = data[offset];
                }

                this.yCoordinates = new byte[repeatsFor];
                for(int i = 0; i < repeatsFor; i++, offset += 1){
                    this.yCoordinates[i] = data[offset];
                    System.out.println(this.flags[i] + "->" + this.xCoordinates[i] + "," + this.yCoordinates[i]);
                }
                System.exit(1);
            }
        }
    }
    static class TableOSSlash2 extends Table{
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TableOSSLASH2:\n");
            builder.append(String.format("\tversion:%d\n", this.version));
            builder.append(String.format("\txAvgCharWidth:%d\n", this.xAvgCharWidth));
            builder.append(String.format("\tusWeightClass:%d\n", this.usWeightClass));
            builder.append(String.format("\tusWidthClass:%d\n", this.usWidthClass));
            builder.append(String.format("\tfsType:%d\n", this.fsType));
            builder.append(String.format("\tySubscriptXSize:%d\n", this.ySubscriptXSize));
            builder.append(String.format("\tySubScriptYSize:%d\n", this.ySubscriptYSize));
            builder.append(String.format("\tySubScriptXOffset:%d\n", this.ySubScriptXOffset));
            builder.append(String.format("\tySubScriptYOffset:%d\n", this.ySubScriptYOffset));
            builder.append(String.format("\tySuperscriptXSize:%d\n", this.ySuperscriptXSize));
            builder.append(String.format("\tySuperScriptYSize:%d\n", this.ySuperscriptYSize));
            builder.append(String.format("\tySuperScriptXOffset:%d\n", this.ySuperScriptXOffset));
            builder.append(String.format("\tySuperScriptYOffset:%d\n", this.ySuperScriptYOffset));
            builder.append(String.format("\tyStrikeoutSize:%d\n", this.yStrikeoutSize));
            builder.append(String.format("\tyStrikeoutPosition:%d\n", this.yStrikeoutPosition));
            builder.append(String.format("\tsFamilyClass:%d\n", this.sFamilyClass));
            builder.append("\tpanose: ").append(Arrays.toString(this.panose)).append("\n");
            builder.append(String.format("\tulUnicodeRange1:%d\n", this.ulUnicodeRange1));
            builder.append(String.format("\tulUnicodeRange2:%d\n", this.ulUnicodeRange2));
            builder.append(String.format("\tulUnicodeRange3:%d\n", this.ulUnicodeRange3));
            builder.append(String.format("\tulUnicodeRange4:%d\n", this.ulUnicodeRange4));
            builder.append(String.format("\tachVendID:0x%x\n", this.achVendID));
            builder.append(String.format("\tfsSelection:%d\n", this.fsSelection));
            builder.append(String.format("\tusFirstCharIndex:%d\n", this.usFirstCharIndex));
            builder.append(String.format("\tusLastCharIndex:%d\n", this.usLastCharIndex));
            builder.append(String.format("\tsTypeAscender:%d\n", this.sTypoAscender));
            builder.append(String.format("\tsTypeDescender:%d\n", this.sTypoDescender));
            builder.append(String.format("\tsTypoLineGap:%d\n", this.sTypoLineGap));
            builder.append(String.format("\tusWinAscent:%d\n", this.usWinAscent));
            builder.append(String.format("\tusWinDescent:%d\n", this.usWinDescent));
            builder.append(String.format("\tulCodePageRange1:%d\n", this.ulCodePageRange1));
            builder.append(String.format("\tulCodePageRange2:%d\n", this.ulCodePageRange2));
            builder.append(String.format("\tsxHeight:%d\n", this.sxHeight));
            builder.append(String.format("\tsCapHeight:%d\n", this.sCapHeight));
            builder.append(String.format("\tusDefaultChar:%d\n", this.usDefaultChar));
            builder.append(String.format("\tusBreakChar:%d\n", this.usBreakChar));
            builder.append(String.format("\tusMaxContext:%d\n", this.usMaxContext));

            return builder.toString();
        }

        short version;
        short xAvgCharWidth;
        short usWeightClass;
        short usWidthClass;
        short fsType;
        short ySubscriptXSize;
        short ySubscriptYSize;
        short ySubScriptXOffset;
        short ySubScriptYOffset;

        short ySuperscriptXSize;
        short ySuperscriptYSize;
        short ySuperScriptXOffset;
        short ySuperScriptYOffset;
        short yStrikeoutSize;
        short yStrikeoutPosition;
        short sFamilyClass;
        // Should be size 10 always
        byte []panose;
        int ulUnicodeRange1;
        int ulUnicodeRange2;
        int ulUnicodeRange3;
        int ulUnicodeRange4;
        // byte[4]
        int achVendID;
        short fsSelection;
        short usFirstCharIndex;
        short usLastCharIndex;
        short sTypoAscender;
        short sTypoDescender;
        short sTypoLineGap;
        short usWinAscent;
        short usWinDescent;
        int ulCodePageRange1;
        int ulCodePageRange2;
        short sxHeight;
        short sCapHeight;
        short usDefaultChar;
        short usBreakChar;
        short usMaxContext;

        public void parseVersion4(byte [] data, int offset){
            this.xAvgCharWidth          = parseShortFromByteArray(data, offset + 0);
            this.usWeightClass          = parseShortFromByteArray(data, offset + 2);
            this.usWidthClass           = parseShortFromByteArray(data, offset + 4);
            this.fsType                 = parseShortFromByteArray(data, offset + 6);
            this.ySubscriptXSize        = parseShortFromByteArray(data, offset + 8);
            this.ySubscriptYSize        = parseShortFromByteArray(data, offset + 10);
            this.ySubScriptXOffset      = parseShortFromByteArray(data, offset + 12);
            this.ySubScriptYOffset      = parseShortFromByteArray(data, offset + 14);
            this.ySuperscriptXSize      = parseShortFromByteArray(data, offset + 16);
            this.ySuperscriptYSize      = parseShortFromByteArray(data, offset + 18);
            this.ySuperScriptXOffset    = parseShortFromByteArray(data, offset + 20);
            this.ySuperScriptYOffset    = parseShortFromByteArray(data, offset + 22);
            this.yStrikeoutSize         = parseShortFromByteArray(data, offset + 24);
            this.yStrikeoutPosition     = parseShortFromByteArray(data, offset + 26);
            this.sFamilyClass           = parseShortFromByteArray(data, offset + 28);
            // Should be size 10 always
            this.panose = new byte[10];
            for(int i = 0; i < 10; i++){
                panose[i] = data[offset + 30 + i ];
            }

            this.ulUnicodeRange1        = parseIntFromByteArray(data, offset + 40);
            this.ulUnicodeRange2        = parseIntFromByteArray(data, offset + 44);
            this.ulUnicodeRange3        = parseIntFromByteArray(data, offset + 48);
            this.ulUnicodeRange4        = parseIntFromByteArray(data, offset + 52);
            // byte[4]
            this.achVendID              = parseIntFromByteArray(data, offset + 56);
            this.fsSelection            = parseShortFromByteArray(data, offset + 60);
            this.usFirstCharIndex       = parseShortFromByteArray(data, offset + 62);
            this.usLastCharIndex        = parseShortFromByteArray(data, offset + 64);
            this.sTypoAscender          = parseShortFromByteArray(data, offset + 66);
            this.sTypoDescender         = parseShortFromByteArray(data, offset + 68);
            this.sTypoLineGap           = parseShortFromByteArray(data, offset + 70);
            this.usWinAscent            = parseShortFromByteArray(data, offset + 72);
            this.usWinDescent           = parseShortFromByteArray(data, offset + 74);
            this.ulCodePageRange1       = parseIntFromByteArray(data, offset + 76);
            this.ulCodePageRange2       = parseIntFromByteArray(data, offset + 80);
            this.sxHeight               = parseShortFromByteArray(data, offset + 84);
            this.sCapHeight             = parseShortFromByteArray(data, offset + 86);
            this.usDefaultChar          = parseShortFromByteArray(data, offset + 88);
            this.usBreakChar            = parseShortFromByteArray(data, offset + 90);
            this.usMaxContext           = parseShortFromByteArray(data, offset + 92);

        }
        public TableOSSlash2(byte [] data, TableRecord record){
            int offset = record.offset;
            this.version = parseShortFromByteArray(data, offset + 0);
            switch(this.version){
                case 5:{
                    break;
                }
                case 4:{
                }
                case 3:{

                }
                case 2:{
                    this.parseVersion4(data, offset + 2);
                    break;
                }
                case 1:{

                    break;
                }
                default:{
                    System.out.printf("Idk this shouln't happen? '%d'\n", this.version);
                    System.exit(1);

                }
            }

        }
    }
    static class TableRecord{
        private String getTagString(int tag){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 3; i >= 0; i--){
                stringBuilder.append(String.format("%c", (byte)((tag >> (i * 8)) & 0xFF)));
            }
            return stringBuilder.toString();
        }
        @Override
        public String toString() {
            return String.format(
                    "TableRecord:\n\ttag:%s\n\tchecksum:%d\n\toffset:%d\n\tlength:%d",
                    this.tag,
                    this.checksum,
                    this.offset,
                    this.length
            );
        }
        public static final int size = 16;

        // tag is byte[4]
        String tag;
        int checksum;
        int offset;
        int length;
        public TableRecord(byte [] data, int idx){
            this.tag        = this.getTagString(TrueType.parseIntFromByteArray(data, idx + 0));
            this.checksum   = TrueType.parseIntFromByteArray(data, idx + 4);
            this.offset     = TrueType.parseIntFromByteArray(data, idx + 8);
            this.length     = TrueType.parseIntFromByteArray(data, idx + 12);
        }

    }
    static class TableDirectory{
        @Override
        public String toString() {
            return String.format(
                    "TableDirectory:\n\tsfnt:0x%s\n\tnumTables:0x%s\n\tsearchRanges:0x%s\n\tentrySelector:0x%s\n\trangeShift:0x%s",
                    Integer.toHexString(this.sfntVersion),
                    Integer.toHexString(this.numTables),
                    Integer.toHexString(this.searchRanges),
                    Integer.toHexString(this.entrySelector),
                    Integer.toHexString(this.rangeShift)
            );
        }

        int sfntVersion;
        short numTables;
        short searchRanges;
        short entrySelector;
        short rangeShift;
        public static final int size = 12;
        public TableDirectory(byte [] data, int idx){
            this.sfntVersion    = parseIntFromByteArray(data,idx + 0);
            this.numTables      = parseShortFromByteArray(data, idx + 4);
            this.searchRanges   = parseShortFromByteArray(data, idx + 6);
            this.entrySelector  = parseShortFromByteArray(data, idx + 8);
            this.rangeShift     = parseShortFromByteArray(data, idx + 10);
        }
    }
    static abstract class Table {
    }
    public TrueType(){
        this.tables = new HashMap<>();
    }
}
