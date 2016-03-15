/*
 * Copyright 2015 CIRDLES.
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
package org.cirdles.topsoil.app.util;

import javafx.scene.control.Dialog;
import javax.inject.Inject;

/**
 *
 * @author Emily
 */
public class AboutDialog extends Dialog<Void> implements Runnable {

    @Inject
    public AboutDialog(AboutDialogPane aboutDialogPane) {
        setDialogPane(aboutDialogPane);
    }

    @Override
    public void run() {
        show();
    }

}
