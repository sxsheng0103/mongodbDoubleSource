//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.poi.xssf.usermodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula.Factory;

public final class XSSFCell implements Cell {
    public static final String FALSE_AS_STRING = "0";
    private static final String TRUE_AS_STRING = "1";
    private final CTCell _cell;
    private final XSSFRow _row;
    private int _cellNum;
    private SharedStringsTable _sharedStringSource;
    private StylesTable _stylesSource;

    protected XSSFCell(XSSFRow row, CTCell cell) {
        this._cell = cell;
        this._row = row;
        if (cell.getR() != null) {
            this._cellNum = (new CellReference(cell.getR())).getCol();
        }

        this._sharedStringSource = row.getSheet().getWorkbook().getSharedStringSource();
        this._stylesSource = row.getSheet().getWorkbook().getStylesSource();
    }

    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }

    protected StylesTable getStylesSource() {
        return this._stylesSource;
    }

    public XSSFSheet getSheet() {
        return this.getRow().getSheet();
    }

    public XSSFRow getRow() {
        return this._row;
    }

    public boolean getBooleanCellValue() {
        int cellType = this.getCellType();
        switch(cellType) {
            case 2:
                return this._cell.isSetV() && "1".equals(this._cell.getV());
            case 3:
                return false;
            case 4:
                return this._cell.isSetV() && "1".equals(this._cell.getV());
            default:
                throw typeMismatch(4, cellType, false);
        }
    }

    public void setCellValue(boolean value) {
        this._cell.setT(STCellType.B);
        this._cell.setV(value ? "1" : "0");
    }

    public double getNumericCellValue() {
        int cellType = this.getCellType();
        switch(cellType) {
            case 0:
            case 2:
                if (this._cell.isSetV()) {
                    try {
                        return Double.parseDouble(this._cell.getV());
                    } catch (NumberFormatException var3) {
                        throw typeMismatch(0, 1, false);
                    }
                }

                return 0.0D;
            case 1:
            default:
                throw typeMismatch(0, cellType, false);
            case 3:
                return 0.0D;
        }
    }

    public void setCellValue(double value) {
        if (Double.isInfinite(value)) {
            this._cell.setT(STCellType.E);
            this._cell.setV(FormulaError.DIV0.getString());
        } else if (Double.isNaN(value)) {
            this._cell.setT(STCellType.E);
            this._cell.setV(FormulaError.NUM.getString());
        } else {
            this._cell.setT(STCellType.N);
            this._cell.setV(String.valueOf(value));
        }

    }

    public String getStringCellValue() {
        XSSFRichTextString str = this.getRichStringCellValue();
        return str == null ? null : str.getString();
    }

    public XSSFRichTextString getRichStringCellValue() {
        int cellType = this.getCellType();
        XSSFRichTextString rt;
        switch(cellType) {
            case 1:
                if (this._cell.getT() == STCellType.INLINE_STR) {
                    if (this._cell.isSetIs()) {
                        rt = new XSSFRichTextString(this._cell.getIs());
                    } else if (this._cell.isSetV()) {
                        rt = new XSSFRichTextString(this._cell.getV());
                    } else {
                        rt = new XSSFRichTextString("");
                    }
                } else if (this._cell.getT() == STCellType.STR) {
                    rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
                } else if (this._cell.isSetV()) {
                    int idx = Integer.parseInt(this._cell.getV());
                    rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(idx));
                } else {
                    rt = new XSSFRichTextString("");
                }
                break;
            case 2:
                checkFormulaCachedValueType(1, this.getBaseCellType(false));
                rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
                break;
            case 3:
                rt = new XSSFRichTextString("");
                break;
            default:
                throw typeMismatch(1, cellType, false);
        }

        rt.setStylesTableReference(this._stylesSource);
        return rt;
    }

    private static void checkFormulaCachedValueType(int expectedTypeCode, int cachedValueType) {
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }

    public void setCellValue(String str) {
        this.setCellValue((RichTextString)(str == null ? null : new XSSFRichTextString(str)));
    }

    public void setCellValue(RichTextString str) {
        if (str != null && str.getString() != null) {
            int cellType = this.getCellType();
            switch(cellType) {
                case 2:
                    this._cell.setV(str.getString());
                    this._cell.setT(STCellType.STR);
                    break;
                default:
                    if (this._cell.getT() == STCellType.INLINE_STR) {
                        this._cell.setV(str.getString());
                    } else {
                        this._cell.setT(STCellType.S);
                        XSSFRichTextString rt = (XSSFRichTextString)str;
                        rt.setStylesTableReference(this._stylesSource);
                        int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
                        this._cell.setV(Integer.toString(sRef));
                    }
            }

        } else {
            this.setCellType(3);
        }
    }

    public String getCellFormula() {
        int cellType = this.getCellType();
        if (cellType != 2) {
            throw typeMismatch(2, cellType, false);
        } else {
            CTCellFormula f = this._cell.getF();
            if (this.isPartOfArrayFormulaGroup() && f == null) {
                XSSFCell cell = this.getSheet().getFirstCellInArrayFormula(this);
                return cell.getCellFormula();
            } else {
                return f.getT() == STCellFormulaType.SHARED ? this.convertSharedFormula((int)f.getSi()) : f.getStringValue();
            }
        }
    }

    private String convertSharedFormula(int si) {
        XSSFSheet sheet = this.getSheet();
        CTCellFormula f = sheet.getSharedFormula(si);
        if (f == null) {
            throw new IllegalStateException("Master cell of a shared formula with sid=" + si + " was not found");
        } else {
            String sharedFormula = f.getStringValue();
            String sharedFormulaRange = f.getRef();
            CellRangeAddress ref = CellRangeAddress.valueOf(sharedFormulaRange);
            int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);
            XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(sheet.getWorkbook());
            SharedFormula sf = new SharedFormula(SpreadsheetVersion.EXCEL2007);
            Ptg[] ptgs = FormulaParser.parse(sharedFormula, fpb, 0, sheetIndex);
            Ptg[] fmla = sf.convertSharedFormulas(ptgs, this.getRowIndex() - ref.getFirstRow(), this.getColumnIndex() - ref.getFirstColumn());
            return FormulaRenderer.toFormulaString(fpb, fmla);
        }
    }

    public void setCellFormula(String formula) {
        if (this.isPartOfArrayFormulaGroup()) {
            this.notifyArrayFormulaChanging();
        }

        this.setFormula(formula, 0);
    }

    void setCellArrayFormula(String formula, CellRangeAddress range) {
        this.setFormula(formula, 2);
        CTCellFormula cellFormula = this._cell.getF();
        cellFormula.setT(STCellFormulaType.ARRAY);
        cellFormula.setRef(range.formatAsString());
    }

    private void setFormula(String formula, int formulaType) {
        XSSFWorkbook wb = this._row.getSheet().getWorkbook();
        if (formula == null) {
            wb.onDeleteFormula(this);
            if (this._cell.isSetF()) {
                this._cell.unsetF();
            }

        } else {
            XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
            FormulaParser.parse(formula, fpb, formulaType, wb.getSheetIndex(this.getSheet()));
            CTCellFormula f = Factory.newInstance();
            f.setStringValue(formula);
            this._cell.setF(f);
            if (this._cell.isSetV()) {
                this._cell.unsetV();
            }

        }
    }

    public int getColumnIndex() {
        return this._cellNum;
    }

    public int getRowIndex() {
        return this._row.getRowNum();
    }

    public String getReference() {
        return this._cell.getR();
    }

    public XSSFCellStyle getCellStyle() {
        XSSFCellStyle style = null;
        if (this._stylesSource.getNumCellStyles() > 0) {
            long idx = this._cell.isSetS() ? this._cell.getS() : 0L;
            style = this._stylesSource.getStyleAt((int)idx);
        }

        return style;
    }

    public void setCellStyle(CellStyle style) {
        if (style == null) {
            if (this._cell.isSetS()) {
                this._cell.unsetS();
            }
        } else {
            XSSFCellStyle xStyle = (XSSFCellStyle)style;
            xStyle.verifyBelongsToStylesSource(this._stylesSource);
            long idx = (long)this._stylesSource.putStyle(xStyle);
            this._cell.setS(idx);
        }

    }

    public int getCellType() {
        return this._cell.getF() == null && !this.getSheet().isCellInArrayFormulaContext(this) ? this.getBaseCellType(true) : 2;
    }

    public int getCachedFormulaResultType() {
        if (this._cell.getF() == null) {
            throw new IllegalStateException("Only formula cells have cached results");
        } else {
            return this.getBaseCellType(false);
        }
    }

    private int getBaseCellType(boolean blankCells) {
        switch(this._cell.getT().intValue()) {
            case 1:
                return 4;
            case 2:
                if (!this._cell.isSetV() && blankCells) {
                    return 3;
                }

                return 0;
            case 3:
                return 5;
            case 4:
            case 5:
            case 6:
                return 1;
            default:
                throw new IllegalStateException("Illegal cell type: " + this._cell.getT());
        }
    }

    public Date getDateCellValue() {
        int cellType = this.getCellType();
        if (cellType == 3) {
            return null;
        } else {
            double value = this.getNumericCellValue();
            boolean date1904 = this.getSheet().getWorkbook().isDate1904();
            return DateUtil.getJavaDate(value, date1904);
        }
    }

    public void setCellValue(Date value) {
        boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        this.setCellValue(DateUtil.getExcelDate(value, date1904));
    }

    public void setCellValue(Calendar value) {
        boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        this.setCellValue(DateUtil.getExcelDate(value, date1904));
    }

    public String getErrorCellString() {
        int cellType = this.getBaseCellType(true);
        if (cellType != 5) {
            throw typeMismatch(5, cellType, false);
        } else {
            return this._cell.getV();
        }
    }

    public byte getErrorCellValue() {
        String code = this.getErrorCellString();
        return code == null ? 0 : FormulaError.forString(code).getCode();
    }

    public void setCellErrorValue(byte errorCode) {
        FormulaError error = FormulaError.forInt(errorCode);
        this.setCellErrorValue(error);
    }

    public void setCellErrorValue(FormulaError error) {
        this._cell.setT(STCellType.E);
        this._cell.setV(error.getString());
    }

    public void setAsActiveCell() {
        this.getSheet().setActiveCell(this._cell.getR());
    }

    private void setBlank() {
        CTCell blank = CTCell.Factory.newInstance();
        blank.setR(this._cell.getR());
        if (this._cell.isSetS()) {
            blank.setS(this._cell.getS());
        }

        this._cell.set(blank);
    }

    protected void setCellNum(int num) {
        checkBounds(num);
        this._cellNum = num;
        String ref = (new CellReference(this.getRowIndex(), this.getColumnIndex())).formatAsString();
        this._cell.setR(ref);
    }

    public void setCellType(int cellType) {
        int prevType = this.getCellType();
        if (this.isPartOfArrayFormulaGroup()) {
            this.notifyArrayFormulaChanging();
        }

        if (prevType == 2 && cellType != 2) {
            this.getSheet().getWorkbook().onDeleteFormula(this);
        }

        switch(cellType) {
            case 0:
                this._cell.setT(STCellType.N);
                break;
            case 1:
                if (prevType != 1) {
                    String str = this.convertCellValueToString();
                    XSSFRichTextString rt = new XSSFRichTextString(str);
                    rt.setStylesTableReference(this._stylesSource);
                    int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
                    this._cell.setV(Integer.toString(sRef));
                }

                this._cell.setT(STCellType.S);
                break;
            case 2:
                if (!this._cell.isSetF()) {
                    CTCellFormula f = Factory.newInstance();
                    f.setStringValue("0");
                    this._cell.setF(f);
                    if (this._cell.isSetT()) {
                        this._cell.unsetT();
                    }
                }
                break;
            case 3:
                this.setBlank();
                break;
            case 4:
                String newVal = this.convertCellValueToBoolean() ? "1" : "0";
                this._cell.setT(STCellType.B);
                this._cell.setV(newVal);
                break;
            case 5:
                this._cell.setT(STCellType.E);
                break;
            default:
                throw new IllegalArgumentException("Illegal cell type: " + cellType);
        }

        if (cellType != 2 && this._cell.isSetF()) {
            this._cell.unsetF();
        }

    }

    public String toString() {
        switch(this.getCellType()) {
            case 0:
                if (DateUtil.isCellDateFormatted(this)) {
                    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    return sdf.format(this.getDateCellValue());
                }

                return this.getNumericCellValue() + "";
            case 1:
                return this.getRichStringCellValue().toString();
            case 2:
                return this.getCellFormula();
            case 3:
                return "";
            case 4:
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            case 5:
                return ErrorEval.getText(this.getErrorCellValue());
            default:
                return "Unknown Cell Type: " + this.getCellType();
        }
    }

    public String getRawValue() {
        return this._cell.getV();
    }

    private static String getCellTypeName(int cellTypeCode) {
        switch(cellTypeCode) {
            case 0:
                return "numeric";
            case 1:
                return "text";
            case 2:
                return "formula";
            case 3:
                return "blank";
            case 4:
                return "boolean";
            case 5:
                return "error";
            default:
                return "#unknown cell type (" + cellTypeCode + ")#";
        }
    }

    private static RuntimeException typeMismatch(int expectedTypeCode, int actualTypeCode, boolean isFormulaCell) {
        String msg = "Cannot get a " + getCellTypeName(expectedTypeCode) + " value from a " + getCellTypeName(actualTypeCode) + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }

    private static void checkBounds(int cellIndex) {
        SpreadsheetVersion v = SpreadsheetVersion.EXCEL2007;
        int maxcol = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        if (cellIndex < 0 || cellIndex > maxcol) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + v.name() + " is (0.." + maxcol + ") or ('A'..'" + v.getLastColumnName() + "')");
        }
    }

    public XSSFComment getCellComment() {
        return this.getSheet().getCellComment(this._row.getRowNum(), this.getColumnIndex());
    }

    public void setCellComment(Comment comment) {
        if (comment == null) {
            this.removeCellComment();
        } else {
            comment.setRow(this.getRowIndex());
            comment.setColumn(this.getColumnIndex());
        }
    }

    public void removeCellComment() {
        XSSFComment comment = this.getCellComment();
        if (comment != null) {
            String ref = this._cell.getR();
            XSSFSheet sh = this.getSheet();
            sh.getCommentsTable(false).removeComment(ref);
            sh.getVMLDrawing(false).removeCommentShape(this.getRowIndex(), this.getColumnIndex());
        }

    }

    public XSSFHyperlink getHyperlink() {
        return this.getSheet().getHyperlink(this._row.getRowNum(), this._cellNum);
    }

    public void setHyperlink(Hyperlink hyperlink) {
        XSSFHyperlink link = (XSSFHyperlink)hyperlink;
        link.setCellReference((new CellReference(this._row.getRowNum(), this._cellNum)).formatAsString());
        this.getSheet().addHyperlink(link);
    }

    @Override
    public void removeHyperlink() {

    }

    @Internal
    public CTCell getCTCell() {
        return this._cell;
    }

    private boolean convertCellValueToBoolean() {
        int cellType = this.getCellType();
        if (cellType == 2) {
            cellType = this.getBaseCellType(false);
        }

        switch(cellType) {
            case 0:
                return Double.parseDouble(this._cell.getV()) != 0.0D;
            case 1:
                int sstIndex = Integer.parseInt(this._cell.getV());
                XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                String text = rt.getString();
                return Boolean.parseBoolean(text);
            case 2:
            default:
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            case 3:
            case 5:
                return false;
            case 4:
                return "1".equals(this._cell.getV());
        }
    }

    public String convertCellValueToString() {
        int cellType = this.getCellType();
        switch(cellType) {
            case 0:
            case 5:
                return this._cell.getV();
            case 1:
                int sstIndex = Integer.parseInt(this._cell.getV());
                XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                return rt.getString();
            case 2:
                cellType = this.getBaseCellType(false);
                String textValue = this._cell.getV();
                switch(cellType) {
                    case 0:
                    case 1:
                    case 5:
                        return textValue;
                    case 2:
                    case 3:
                    default:
                        throw new IllegalStateException("Unexpected formula result type (" + cellType + ")");
                    case 4:
                        if ("1".equals(textValue)) {
                            return "TRUE";
                        } else {
                            if ("0".equals(textValue)) {
                                return "FALSE";
                            }

                            throw new IllegalStateException("Unexpected boolean cached formula value '" + textValue + "'.");
                        }
                }
            case 3:
                return "";
            case 4:
                return "1".equals(this._cell.getV()) ? "TRUE" : "FALSE";
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
        }
    }

    public CellRangeAddress getArrayFormulaRange() {
        XSSFCell cell = this.getSheet().getFirstCellInArrayFormula(this);
        if (cell == null) {
            throw new IllegalStateException("Cell " + this._cell.getR() + " is not part of an array formula.");
        } else {
            String formulaRef = cell._cell.getF().getRef();
            return CellRangeAddress.valueOf(formulaRef);
        }
    }

    public boolean isPartOfArrayFormulaGroup() {
        return this.getSheet().isCellInArrayFormulaContext(this);
    }

    void notifyArrayFormulaChanging(String msg) {
        if (this.isPartOfArrayFormulaGroup()) {
            CellRangeAddress cra = this.getArrayFormulaRange();
            if (cra.getNumberOfCells() > 1) {
                throw new IllegalStateException(msg);
            }

            this.getRow().getSheet().removeArrayFormula(this);
        }

    }

    void notifyArrayFormulaChanging() {
        CellReference ref = new CellReference(this);
        String msg = "Cell " + ref.formatAsString() + " is part of a multi-cell array formula. " + "You cannot change part of an array.";
        this.notifyArrayFormulaChanging(msg);
    }
}
