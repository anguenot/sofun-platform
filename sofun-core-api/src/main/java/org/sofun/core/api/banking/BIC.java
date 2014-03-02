/*
 * Copyright (c)  Sofun Gaming SAS.
 * Copyright (c)  Julien Anguenot <julien@anguenot.org>
 * Copyright (c)  Julien De Preaumont <juliendepreaumont@gmail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Julien Anguenot <julien@anguenot.org> - initial API and implementation
*/

package org.sofun.core.api.banking;

/**
 * BIC utils.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class BIC {

    private String invalidCause = null;

    private String bic;

    public BIC(String bic) {
        this.bic = bic;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getInvalidCause() {
        return invalidCause;
    }

    public boolean isValid() {
        if (this.bic == null)
            throw new IllegalStateException("bic is null");
        this.invalidCause = null;
        if (!(this.bic.length() == 8 || this.bic.length() == 11)) {
            this.invalidCause = "BIC must be 8 or 11 chars, got " + this.bic.length();
            return false;
        }
        final String country = this.bic.substring(4, 6);
        if (!ISOCountries.getInstance().isValidCode(country.toUpperCase())) {
            this.invalidCause = "Invalid country code: " + country;
            return false;
        }
        return true;
    }

}
