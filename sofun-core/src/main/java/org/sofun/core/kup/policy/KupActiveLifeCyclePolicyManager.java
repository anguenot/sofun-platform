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

package org.sofun.core.kup.policy;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.prediction.PredictionService;

/**
 * Kup Policy Timer.
 * 
 * <p/>
 * 
 * triggering Kup life cycle checks on regular basis out of process for active
 * Kups only.
 * 
 * <p/>
 * 
 * This singleton is responsible to perform Kups status transitions. This is not
 * responsible for any kind of computations whatsover.
 * 
 * <p/>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class KupActiveLifeCyclePolicyManager extends
        AbstractKupLifeCycleManager {

    private static final Log log = LogFactory
            .getLog(KupActiveLifeCyclePolicyManager.class);

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    @EJB(beanName = "PredictionServiceImpl", beanInterface = PredictionServiceLocal.class)
    private PredictionService predictions;
    
    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @Override
    public KupService getKups() {
        return kups;
    }

    @Override
    public PredictionService getPredictions() {
        return predictions;
    }

    /**
     * Kup life cycle management.
     * 
     * @throws Exception
     */
    @Schedule(minute = "*/4", hour = "*", persistent = false)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            log.debug("Checking Kups and apply policy...");

            final byte[] activesAndCreated = new byte[3];
            activesAndCreated[0] = KupStatus.CREATED;
            activesAndCreated[1] = KupStatus.OPENED;
            activesAndCreated[2] = KupStatus.ON_GOING;
            final List<Kup> activeKups = kups.getKupsByStatus(
                    activesAndCreated, null);

            final Calendar ref = getReferenceTime();

            ListIterator<Kup> kupsIter = activeKups.listIterator();
            while (kupsIter.hasNext()) {

                Kup kup = kupsIter.next();
                // Ensure no changes have been made by another transaction in
                // the mean time.
                em.refresh(kup);

                if (KupStatus.CREATED == kup.getStatus()) {
                    final Calendar startDate = Calendar.getInstance();
                    startDate.setTime(kup.getStartDate());
                    if (ref.compareTo(startDate) >= 0) {
                        kup.setStatus(KupStatus.OPENED);
                        log.info("Kup with id=" + String.valueOf(kup.getId())
                                + " has now status=" + KupStatus.OPENED);
                    }
                } else if (KupStatus.OPENED == kup.getStatus()) {
                    final Calendar startDate = Calendar.getInstance();
                    startDate.setTime(kup.getEffectiveStartDate());
                    if (ref.compareTo(startDate) >= 0) {
                        boolean cancelled = false;
                        if (kup.getParticipants().size() < 2) {
                            // Cancel gambling Kup if only one (1) participant.
                            if (KupType.GAMBLING_FR.equals(kup.getType())) {
                                kups.cancelKup(kup);
                                log.info("Kup with id="
                                        + String.valueOf(kup.getId())
                                        + " has been cancelled because the "
                                        + "number of participants is < 2");
                                cancelled = true;
                            }
                        }
                        if (!cancelled) {
                            kup.setStatus(KupStatus.ON_GOING);
                            log.info("Kup with id="
                                    + String.valueOf(kup.getId())
                                    + " has now status=" + KupStatus.ON_GOING);
                        }
                    }
                } else if (KupStatus.ON_GOING == kup.getStatus()) {
                    final Calendar endDate = Calendar.getInstance();
                    // Kup is closed if the last event of the Kup is less
                    // than 5 minutes to begin.
                    endDate.setTime(kup.getEffectiveEndDate());
                    if (ref.compareTo(endDate) >= 0) {
                        Map<Integer, Float> repartition = kups
                                .getWinningsRepartitionRulesFor(kup);
                        final int numberOfWinners = repartition.size();
                        boolean cancelled = false;
                        if (kup.getParticipants().size() < numberOfWinners) {
                            // Cancel gambling Kup participants below amount of
                            // announced winners.
                            if (KupType.GAMBLING_FR.equals(kup.getType())) {
                                kups.cancelKup(kup);
                                log.info("Kup with id="
                                        + String.valueOf(kup.getId())
                                        + " has been cancelled because the "
                                        + "number of participants is < 2");
                                cancelled = true;
                            }
                        }
                        if (!cancelled) {
                            kup.setStatus(KupStatus.CLOSED);
                            log.info("Kup with id="
                                    + String.valueOf(kup.getId())
                                    + " has now status=" + KupStatus.CLOSED);
                        }
                    }
                }

            }

        } catch (Throwable t) {
            log.error(t.getMessage());
            t.printStackTrace();
        } finally {
            available = true;
        }

        // Cancel status in exceptional situations will be handled manually per
        // administrator request.
        // @see KupeService.cancelKup()

    }

}
