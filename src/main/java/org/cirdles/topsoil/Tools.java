/*
 * Copyright 2014 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import static org.cirdles.topsoil.Topsoil.LAST_TABLE_PATH;
import org.cirdles.topsoil.table.Record;
import org.cirdles.topsoil.utils.TSVTableReader;
import org.cirdles.topsoil.utils.TSVTableWriter;
import org.cirdles.topsoil.utils.TableReader;
import org.cirdles.topsoil.utils.TableWriter;
import org.cirdles.utils.function.Translator;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 * Shortcut tools to be used anywhere in the program.
 */
public class Tools {

    public static final StringConverter<Number> DYNAMIC_STRING_CONVERTER = new StringConverter<Number>() {

        @Override
        public String toString(Number object) {
            return String.format("%.10f", object.doubleValue()).replaceFirst("\\.?0+$", "");
        }

        @Override
        public Number fromString(String string) {
            return Double.valueOf(string);
        }
    };

    public static final StringConverter<Number> DYNAMIC_NUMBER_CONVERTER_TO_INTEGER = new StringConverter<Number>() {

        @Override
        public String toString(Number object) {
            return String.valueOf(object.intValue());
        }

        @Override
        public Number fromString(String string) {
            return Integer.valueOf(string);
        }
    };

    public static final StringConverter<String> SUPERSCRIPTPARSER_CONVERTER = new StringConverter<String>() {
        private final String SUPERSCRIPT_SIGN = "^";
        private final String SUBSCRIPT_SIGN = "_";

        /**
         * In a String, search for <code>toCapture</code> between delimiters, converter them, and return the new String
         *
         * @param string
         * @param delimiter
         * @param translator
         * @return
         */
        private String parse(String string, String delimiter, String toCapture, Translator converter) {
            StringBuffer result = new StringBuffer();
            int lenghdelimiter;
            if (delimiter == null) {
                lenghdelimiter = 0;
                delimiter = "";
            } else {
                lenghdelimiter = delimiter.length();
            }

            String regex = "\\Q" + delimiter + "\\E(" + toCapture + "+)\\Q" + delimiter + "\\E";
            
            
            Matcher matcher = Pattern.compile(regex).matcher(string);
            while (matcher.find()) {
                matcher.appendReplacement(result, converter.translate(matcher.group(1)));
            }
            matcher.appendTail(result);

            return result.toString();
        }

        @Override
        public String fromString(String string) {
            String result = parse(string, SUPERSCRIPT_SIGN, "[0-9]", (String origin) -> {
                String retour = new String();
                for (char i : origin.toCharArray()) {
                    if (i == '1') {
                        retour += "\u00B9";
                    } else if (i == '2') {
                        retour += "\u00B2";
                    } else if (i == '3') {
                        retour += "\u00B3";
                    } else {
                        int code = 0x2070 + Integer.valueOf(String.valueOf(i));

                        retour += (char) code;
                    }
                }

                return retour;
            });

            result = parse(result, SUBSCRIPT_SIGN, "[0-9]", (String origin) -> {
                String retour = new String();

                for (char i : origin.toCharArray()) {
                    int code = 0x2080 + Integer.valueOf(String.valueOf(i));

                    retour += (char) code;
                }

                return retour;
            });

            return result;
        }

        class SupersubToNormal implements Translator {

            private String delimiter;

            public SupersubToNormal(String delimiter) {
                this.delimiter = delimiter;
            }

            @Override
            public String translate(String origin) {
                String retour = new String();
                for (char c : origin.toCharArray()) {
                    if (c == '\u00B9') {
                        retour += 1;
                    } else {
                        String code = Integer.toHexString(c);
                        retour += code.charAt(code.length() - 1);
                    }
                }

                return delimiter + retour + delimiter;
            }

        }

        @Override
        public String toString(String string) {
            String result = parse(string,
                                  null,
                                  "[\u2070\u00B9\u00B2\u00B3\u2074\u2075\u2076\u2077\u2078\u2079]",
                                  new SupersubToNormal("^"));

            result = parse(result,
                           null,
                           "[\u2080\u2081\u2082\u2083\u2084\u2085\u2086\u2087\u2088\u2089]",
                           new SupersubToNormal("_"));
            return result;
        }

    };

    /**
     * Prompts the user for a yes or no response with a custom message. If the user selects yes or no, the callback
     * function is called with a boolean indicating the result. Otherwise, the user may choose to cancel the action and
     * the dialog will close without any side effects.
     *
     * @param message the message to display to the user inside the dialog box
     * @param callback the function to be called if the action is not canceled
     */
    public static void yesNoPrompt(String message, Consumer<Boolean> callback) {
        Action response = Dialogs.create()
                .title(Topsoil.APP_NAME)
                .message(message)
                .showConfirm();

        if (response != Dialog.Actions.CANCEL) {
            callback.accept(response == Dialog.Actions.YES);
        }
    }

    public static Label label_minsize(String textlabel) {
        LabelUsePrefSize label = new LabelUsePrefSize(textlabel);
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }

    public static void pasteFromClipboard(TableView<Record> dataTable) {
        Tools.yesNoPrompt("Does the pasted data contain headers?", response -> {
            TableReader tableReader = new TSVTableReader(response);
            tableReader.read(Clipboard.getSystemClipboard().getString(), dataTable);

            saveTable(dataTable);
        });
    }

    public static void saveTable(TableView<Record> dataTable) {
        if (!dataTable.getItems().isEmpty()) {
            TableWriter<Record> tableWriter = new TSVTableWriter(true, ((TSVTable)dataTable).getRequiredColumnCount());
            tableWriter.write(dataTable, LAST_TABLE_PATH);
        } else {
            LAST_TABLE_PATH.toFile().delete();
        }
    }

    public static void clearTable(TableView<Record> dataTable) {
        dataTable.getItems().clear();
        dataTable.getColumns().clear();
    }
}
