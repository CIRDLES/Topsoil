package org.cirdles.topsoil.app.control.data;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.data.FXDataColumn;

import java.util.regex.Pattern;

public class FXDataTreeTableCell<S, T> extends TreeTableCell<S, T> {

    private StringConverter<T> converter;
    private TextField textField;

    FXDataTreeTableCell(FXDataColumn<T> column, StringConverter<T> converter) {
        super();
        this.getStyleClass().add("text-field-tree-table-cell");
        this.converter = converter;
        this.textField = new TextField();
        textField.setOnAction(event -> {
            String text = textField.getText();
            if (column.getType() == Number.class && !isDouble(text)) {
                cancelEdit();
            } else {
                commitEdit(converter.fromString(text));
            }
            event.consume();
        });
    }

    /** {@inheritDoc} */
    @Override public void startEdit() {
        if (! isEditable()
                || ! getTreeTableView().isEditable()
                || ! getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            textField.setText(getItem().toString());
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.requestFocus();
        }
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();
        setText(converter.toString(getItem()));
        setGraphic(null);
    }

    /** {@inheritDoc} */
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (isEmpty()) {
            setText(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(converter.toString(getItem()));
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(converter.toString(getItem()));
                setGraphic(null);
            }
        }
    }

    private boolean isDouble(String string) {
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from section 3.10.2 of
                        // The Java Language Specification.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\." + Digits + "(" + Exp + ")?)|" +

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, string);
    }

}
