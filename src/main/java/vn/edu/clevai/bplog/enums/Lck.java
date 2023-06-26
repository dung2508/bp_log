package vn.edu.clevai.bplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Lck {
	ALPK,
	AQR0,
	AQR1,
	AQR2,
	AQRT,
	AQS,
	BCPK,
	BW,
	CO,
	CQR,
	CQS,
	DCT,
	DL,
	DLC,
	DLG,
	DQS,
	DSC,
	GE,
	GES,
	HAV,
	HORG,
	HRG,
	HRV,
	HTRG,
	LCH,
	LI,
	LI0,
	MC,
	MP0,
	MP40,
	MP40L,
	MP71,
	MP80,
	MP80T,
	PC,
	PKOE,
	PKOM,
	PMPK,
	POPK,
	QA,
	QQ,
	QT,
	RC,
	RL,
	RQS,
	TUPK,
	WC,
	WP0,
	WP40,
	WP40L,
	WP71,
	WP80,
	WP80T;

	public static Lck findByName(String name) {
		return valueOf(name);
	}
}
