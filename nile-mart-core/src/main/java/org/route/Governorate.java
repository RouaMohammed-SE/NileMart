package org.route;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Governorate {
    CAIRO("Cairo", Zone.GREATER_CAIRO),
    GIZA("Giza", Zone.GREATER_CAIRO),
    QALYUBIA("Qalyubia", Zone.GREATER_CAIRO),

    ALEXANDRIA("Alexandria", Zone.ALEX_DELTA),
    BEHEIRA("Beheira", Zone.ALEX_DELTA),
    KAFR_EL_SHEIKH("Kafr El Sheikh", Zone.ALEX_DELTA),
    DAKAHLIA("Dakahlia", Zone.ALEX_DELTA),
    DAMIETTA("Damietta", Zone.ALEX_DELTA),
    GHARBIA("Gharbia", Zone.ALEX_DELTA),
    MONUFIA("Monufia", Zone.ALEX_DELTA),
    SHARQIA("Sharqia", Zone.ALEX_DELTA),

    PORT_SAID("Port Said", Zone.CANAL),
    ISMAILIA("Ismailia", Zone.CANAL),
    SUEZ("Suez", Zone.CANAL),

    FAIYUM("Faiyum", Zone.UPPER_EGYPT),
    BENI_SUEF("Beni Suef", Zone.UPPER_EGYPT),
    MINYA("Minya", Zone.UPPER_EGYPT),
    ASYUT("Asyut", Zone.UPPER_EGYPT),
    SOHAG("Sohag", Zone.UPPER_EGYPT),
    QENA("Qena", Zone.UPPER_EGYPT),
    LUXOR("Luxor", Zone.UPPER_EGYPT),
    ASWAN("Aswan", Zone.UPPER_EGYPT),

    MATROUH("Matrouh", Zone.REMOTE),
    NORTH_SINAI("North Sinai", Zone.REMOTE),
    SOUTH_SINAI("South Sinai", Zone.REMOTE),
    RED_SEA("Red Sea", Zone.REMOTE),
    NEW_VALLEY("New Valley", Zone.REMOTE);

    private final String displayName;
    private final Zone zone;

    @Override
    public String toString() {
        return displayName;
    }
}
