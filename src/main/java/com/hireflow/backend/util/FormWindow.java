package com.hireflow.backend.util;

import com.hireflow.backend.entity.Form;

import java.time.LocalDateTime;

/** Akademi formunun açık / kapalı / başvurulabilir olup olmadığını tarih + isActv ile çözer. */
public final class FormWindow {

    public static final Short ACTIVE = 1;
    public static final Short PASSIVE = 0;

    private FormWindow() {
    }

    /** Form kaydı aktif mi (IS_ACTV = 1). */
    public static boolean isActive(Form form) {
        return form != null && ACTIVE.equals(form.getIsActv());
    }

    /** Bitiş tarihi geçmiş mi. */
    public static boolean isExpired(Form form, LocalDateTime now) {
        return form != null && form.getEdate() != null && now.isAfter(form.getEdate());
    }

    /** Başlangıç tarihi geldi mi. */
    public static boolean hasStarted(Form form, LocalDateTime now) {
        return form != null && (form.getSdate() == null || !now.isBefore(form.getSdate()));
    }

    /** Aday listesinde görünsün mü: aktif ve süresi dolmamış. */
    public static boolean isVisibleToCandidates(Form form, LocalDateTime now) {
        return isActive(form) && !isExpired(form, now);
    }

    /** Başvuru kabul edilsin mi: görünür + başlamış. */
    public static boolean canApply(Form form, LocalDateTime now) {
        return isVisibleToCandidates(form, now) && hasStarted(form, now);
    }

    /** İstenen aktif bayrağını uygular; bitiş geçmişse zorla pasif yapar. */
    public static Short resolveActiveFlag(Short requested, LocalDateTime edate, LocalDateTime now) {
        Short flag = requested != null ? requested : ACTIVE;
        if (edate != null && now.isAfter(edate)) {
            return PASSIVE;
        }
        return flag;
    }
}
